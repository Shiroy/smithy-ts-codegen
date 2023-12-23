package com.awacheux.smithy

import software.amazon.smithy.codegen.core.directed.GenerateStructureDirective
import software.amazon.smithy.model.traits.RequiredTrait

class StructureGeneration(private val directive: GenerateStructureDirective<GenerationContext, TypescriptSettings>) {

    private val shape = directive.shape()
    private val symbolProvider = directive.symbolProvider()

    fun render() {
        val structureSymbol = directive.symbol()

        directive.context().writerDelegator().useFileWriter(structureSymbol.definitionFile) { writer ->
            writer.openBlock("export interface \$L {", structureSymbol)
            writer.call {
                generateMembers(writer)
            }
            writer.closeBlock("}")
        }
    }

    private fun generateMembers(writer: TypescriptSymbolWriter) {
        shape.members().forEach { member ->
            val symbol = symbolProvider.toSymbol(member)

            val required = if (member.hasTrait(RequiredTrait::class.java)) {
                ""
            } else {
                "?"
            }

            writer.write("\$L: \$T\$L", member.memberName, symbol, required)

        }
    }
}