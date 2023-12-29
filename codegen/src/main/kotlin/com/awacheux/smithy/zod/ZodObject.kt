package com.awacheux.smithy.zod

import com.awacheux.smithy.TypescriptSymbolWriter

class ZodObject(
    shapeName: String,
    val properties: List<Property>,
): ZodElement(shapeName) {

    data class Property(
        val name: String,
        val element: ZodElement,
        val isOptional: Boolean = false,
    )

    override fun renderDefinition(writer: TypescriptSymbolWriter) {
        writer.openBlock("z.object({")
        properties.forEach { property ->
            writer.writeInline("${property.name}: ")
            property.element.renderUse(writer)
            writer.writeInline(if (property.isOptional) ".optional()" else "")
            writer.writeInline(",")
            writer.ensureNewline()
        }
        writer.closeBlock("})")
    }
}
