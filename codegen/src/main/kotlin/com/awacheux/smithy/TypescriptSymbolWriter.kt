package com.awacheux.smithy

import software.amazon.smithy.codegen.core.SymbolWriter

class TypescriptSymbolWriter(filename: String) : SymbolWriter<TypescriptSymbolWriter, TypescriptImportContainer>(
    TypescriptImportContainer(filename)
) {
    init {
        expressionStart = '#'
    }
    companion object Factory : SymbolWriter.Factory<TypescriptSymbolWriter> {
        /**
         * Creates a `SymbolWriter` of type `W` for the given
         * filename and namespace.
         *
         * @param filename  Non-null filename of the writer being created.
         * @param namespace Non-null namespace associated with the file (possibly empty string).
         * @return Returns the created writer of type `W`.
         */
        override fun apply(filename: String, namespace: String?): TypescriptSymbolWriter {
            println("New writer for file : $filename")

            return TypescriptSymbolWriter(filename)
        }

    }

    override fun toString(): String {
        val content = super.toString()
        val importSection = importContainer.toString()

        val fileContent = StringBuilder()

        fileContent.appendLine("// DO NOT EDIT. This file has been generated automatically.")
        fileContent.appendLine("// All change will br overwritten at the next generation.")
        fileContent.appendLine(importSection)
        fileContent.appendLine(content)

        return fileContent.toString()
    }
}
