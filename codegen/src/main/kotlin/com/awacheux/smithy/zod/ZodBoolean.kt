package com.awacheux.smithy.zod

import com.awacheux.smithy.TypescriptSymbolWriter

class ZodBoolean(
    shapeName: String? = null,
): ZodElement(shapeName) {
    override fun renderDefinition(writer: TypescriptSymbolWriter) {
        writer.writeInline("z.boolean()")
    }
}
