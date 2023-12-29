package com.awacheux.smithy.zod

import com.awacheux.smithy.TypescriptSymbolWriter
import software.amazon.smithy.codegen.core.Symbol

class ZodArray(
    shapeName: String? = null,
    val element: ZodElement,
    var min: Int? = null,
    var max: Int? = null) : ZodElement(shapeName) {
    override fun renderDefinition(writer: TypescriptSymbolWriter) {
        writer.writeInline("z.array(${element.renderUse(writer)})")
        min?.let { writer.writeInline(".min($it)") }
        max?.let { writer.writeInline(".max($it)") }
    }

    override fun getValidatorSymbol(): Symbol {
        val symbol = super.getValidatorSymbol()
        val builder = symbol.toBuilder()

        builder.addReference(element.getValidatorSymbol())

        return builder.build()
    }
}
