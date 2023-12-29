package com.awacheux.smithy

import software.amazon.smithy.codegen.core.WriterDelegator
import software.amazon.smithy.model.shapes.Shape
import software.amazon.smithy.model.shapes.ShapeVisitor
import software.amazon.smithy.model.shapes.StringShape
import software.amazon.smithy.model.shapes.StructureShape

class TypescriptCodeGenerator(
    private val zodElementBuilder: ZodElementBuilder,
    private val writerDelegator: WriterDelegator<TypescriptSymbolWriter>
) : ShapeVisitor.Default<Unit>() {
    override fun getDefault(shape: Shape?) = Unit

    override fun stringShape(shape: StringShape) {
        val zodElement = shape.accept(zodElementBuilder)
        val symbol = zodElement.getValidatorSymbol()
        if(symbol.definitionFile.isNotEmpty()) {
            writerDelegator.useFileWriter(symbol.definitionFile) { writer ->
                writer.ensureNewline()
                writer.writeInline("export const #L = ", zodElement.validatorName!!)
                zodElement.renderDefinition(writer)
                writer.ensureNewline()
                writer.write("export type #L = #T.infer<typeof #L>;",
                    zodElement.shapeName,
                    TypescriptDependencies.getZodZSymbol(),
                    zodElement.validatorName
                )
            }
        }
    }

    override fun structureShape(shape: StructureShape) {
        StructureGeneration(shape, zodElementBuilder, writerDelegator).render()
    }
}
