package com.awacheux.smithy.zod

import com.awacheux.smithy.TypescriptDependencies
import com.awacheux.smithy.TypescriptSymbolWriter
import software.amazon.smithy.codegen.core.Symbol

/**
 * Base class for modeling Zod validators.
 */
abstract class ZodElement(val shapeName: String? = null) {

    val validatorName: String? = shapeName?.let { "${it}Validator" }
    abstract fun renderDefinition(writer: TypescriptSymbolWriter)

    fun renderUse(writer: TypescriptSymbolWriter) = shapeName?.let {
        writer.writeInline("${it}Validator")
    } ?: renderDefinition(writer)

    /**
     * Returns the symbol for the Zod validator if it exists. Otherwise, return a dummy empty symbol.
     */
    open fun getValidatorSymbol(): Symbol {
        val builder = Symbol.builder()

        if (shapeName != null) {
            builder.name(validatorName!!)
            builder.definitionFile("models/${shapeName}.ts")
        }

        builder.addDependency(TypescriptDependencies.getZodZSymbol())

        return builder.build()
    }

    /**
     * Returns the symbol for the type if it exists. Otherwise, return a dummy empty symbol.
     */
    fun getTypeSymbol(): Symbol {
        val builder = Symbol.builder()

        if (shapeName != null) {
            builder.name(shapeName)
            builder.definitionFile("models/${shapeName}.ts")
        }

        return builder.build()
    }
}
