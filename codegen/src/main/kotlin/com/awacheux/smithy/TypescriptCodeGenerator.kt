package com.awacheux.smithy

import software.amazon.smithy.codegen.core.SymbolProvider
import software.amazon.smithy.codegen.core.WriterDelegator
import software.amazon.smithy.model.shapes.*
import software.amazon.smithy.model.traits.LengthTrait
import software.amazon.smithy.model.traits.PatternTrait

class TypescriptCodeGenerator(
    private val symbolProvider: SymbolProvider,
    private val writerDelegator: WriterDelegator<TypescriptSymbolWriter>
) : ShapeVisitor.Default<Unit>() {
    override fun getDefault(shape: Shape?) {

    }

    override fun stringShape(shape: StringShape) {
        val symbol = symbolProvider.toSymbol(shape)

        if(symbol.definitionFile.isNotEmpty()) {
            writerDelegator.useFileWriter(symbol.definitionFile) { writer ->
                writer.addDependency(TypescriptDependencies.ZOD)
                val validatorName = "${symbol.name}Validator"
                writer.writeInline("export const \$L = z.string()", validatorName)
                handleLengthTrait(shape, writer)
                handlePatternTrait(shape, writer)
                writer.writeInline(";")
                writer.ensureNewline()
                writer.write("export type \$L = z.inferType<typeof \$L>;", symbol.name, validatorName)
            }
        }
    }

    private fun handleLengthTrait(shape: Shape, writer: TypescriptSymbolWriter) {
        val lengthTrait = shape.getTrait(LengthTrait::class.java)
        if(lengthTrait.isPresent) {
            if(lengthTrait.get().min.isPresent) {
                writer.writeInline(".min(\$L)", lengthTrait.get().min.get())
            }

            if(lengthTrait.get().max.isPresent) {
                writer.writeInline(".max(\$L)", lengthTrait.get().max.get())
            }
        }
    }

    private fun handlePatternTrait(
        shape: Shape,
        writer: TypescriptSymbolWriter
    ) {
        val patternTrait = shape.getTrait(PatternTrait::class.java)
        if (patternTrait.isPresent) {
            writer.writeInline(".regex(/\$L/)", patternTrait.get().value)
        }
    }
}