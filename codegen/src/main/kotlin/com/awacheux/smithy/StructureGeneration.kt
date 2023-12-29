package com.awacheux.smithy

import software.amazon.smithy.codegen.core.WriterDelegator
import software.amazon.smithy.model.shapes.StructureShape

class StructureGeneration(
    shape: StructureShape,
    zodElementBuilder: ZodElementBuilder,
    private val writerDelegator: WriterDelegator<TypescriptSymbolWriter>
) {
    private val zodElement = shape.accept(zodElementBuilder)

    fun render() {
        val structureSymbol = zodElement.getValidatorSymbol()
        require(structureSymbol.definitionFile.isNotEmpty()) { "Structure symbols must have a definition file" }

        writerDelegator.useFileWriter(structureSymbol.definitionFile) { writer ->
            val validatorName = zodElement.validatorName
            requireNotNull(validatorName) { "Structure symbols must have a validator name" }

            writer.writeInline(
                "export const #L = ",
                validatorName
            )

            zodElement.renderDefinition(writer)

            writer.write(
                "export type #L = #T.infer<typeof #L>",
                zodElement.shapeName!!,
                TypescriptDependencies.getZodZSymbol(),
                validatorName
            )
        }
    }
}
