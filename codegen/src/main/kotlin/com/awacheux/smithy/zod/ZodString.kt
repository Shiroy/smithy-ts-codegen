package com.awacheux.smithy.zod

import com.awacheux.smithy.TypescriptSymbolWriter

class ZodString(
    shapeName: String? = null,
    var min: Int? = null,
    var max: Int? = null,
    var pattern: String? = null,
): ZodElement(shapeName) {
    override fun renderDefinition(writer: TypescriptSymbolWriter) {
        writer.writeInline("z.string()")
        min?.let { writer.writeInline(".min($it)") }
        max?.let { writer.writeInline(".max($it)") }
        pattern?.let { writer.writeInline(".regex($it)") }
    }

}
