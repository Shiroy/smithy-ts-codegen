package com.awacheux.smithy.zod

import com.awacheux.smithy.TypescriptSymbolWriter

class ZodNumber(
    shapeName: String? = null,
    var min: Int? = null,
    var max: Int? = null,
    var integer : Boolean = false,
) : ZodElement(shapeName) {
    override fun renderDefinition(writer: TypescriptSymbolWriter) {
        writer.writeInline("z.number()")
        if (integer) {
            writer.writeInline(".int()")
        }
        min?.let { writer.writeInline(".min($it)") }
        max?.let { writer.writeInline(".max($it)") }
    }
}
