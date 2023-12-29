package com.awacheux.smithy

import software.amazon.smithy.build.FileManifest
import software.amazon.smithy.codegen.core.WriterDelegator
import software.amazon.smithy.model.Model
import software.amazon.smithy.model.neighbor.Walker
import software.amazon.smithy.model.shapes.ServiceShape
import software.amazon.smithy.model.transform.ModelTransformer

private typealias Transform = (model: Model, transform: ModelTransformer) -> Model

/**
 * This class encapsulates the logic to generate Typescript code from a Smithy model.
 *
 * It manages resources such the symbol provider, writer, the file delegator
 *
 * It also applies the different transformation steps such as:
 * - flattening the model by removing the mixins
 * - generating dedicated structures for operations' inputs and outputs
 */
class TypescriptGenerator(
    zodElementBuilder: ZodElementBuilder,
    fileManifest: FileManifest,
    private val settings: TypescriptSettings,
    private var model: Model
) {
    private val transforms = mutableListOf<Transform>()
    private val writerDelegator = WriterDelegator(
        fileManifest,
        zodElementBuilder,
        TypescriptSymbolWriter
    )
    private val codeGenerator = TypescriptCodeGenerator(zodElementBuilder, writerDelegator)
    fun run() {
        simplifyModelBeforeCodegen()
        createDedicatedInputsAndOutputs()

        applyTransforms()

        generateCode()

        writerDelegator.flushWriters()
    }

    private fun simplifyModelBeforeCodegen() {
        transforms.add { model, transformer ->
            val serviceShape = model.expectShape(
                settings.service,
                ServiceShape::class.java
            )
            var newModel = transformer.copyServiceErrorsToOperations(model, serviceShape)
            newModel = transformer.flattenAndRemoveMixins(newModel)
            return@add newModel
        }
    }

    private fun createDedicatedInputsAndOutputs() {
        transforms.add { model, transformer ->
            return@add transformer.createDedicatedInputAndOutput(model, "Input", "Output")
        }
    }

    private fun applyTransforms() {
        val transformer = ModelTransformer.create()
        transforms.forEach { transform ->
            model = transform(model, transformer)
        }
    }

    private fun generateCode() {
        val walker = Walker(model)
        val serviceShape = model.expectShape(
            settings.service,
            ServiceShape::class.java
        )

        val shapes = HashSet(walker.walkShapes(serviceShape))

        shapes.forEach { shape ->
            shape.accept(codeGenerator)
        }
    }
}
