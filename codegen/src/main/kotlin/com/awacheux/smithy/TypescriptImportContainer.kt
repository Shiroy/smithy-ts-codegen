package com.awacheux.smithy

import software.amazon.smithy.codegen.core.ImportContainer
import software.amazon.smithy.codegen.core.Symbol
import kotlin.io.path.Path

class TypescriptImportContainer(currentFilePath: String): ImportContainer {

    // This maps stores the import statements that will be generated.
    // The key is the filename that will be imported, and the value is a pair of the symbol name and the alias.
    // For example, if we need to import the symbol "CreateLedgerRequest" from the file "CreateLedgerRequest.ts" without an alias, we would add the following entry:
    // "CreateLedgerRequest.ts" -> Pair("CreateLedgerRequest", null)
    // If we need to import the symbol "CreateLedgerRequest" from the file "CreateLedgerRequest.ts" with an alias of "CreateLedgerRequestAlias", we would add the following entry:
    // "CreateLedgerRequest.ts" -> Pair("CreateLedgerRequest", "CreateLedgerRequestAlias")
    private val imports = mutableMapOf<String, MutableSet<Pair<String, String?>>>()
    private val currentPath = Path(currentFilePath)

    /**
     * Imports a symbol from a file. If an alias is provided, the symbol will be imported with an alias.
     * If the symbol is already imported, this method will do nothing.
     *
     * If the symbol does not define a file, this method will do nothing.
     */
    override fun importSymbol(symbol: Symbol, alias: String?) {
        val module = if (symbol.definitionFile.isNotEmpty()) {
            val pathToImport = Path(symbol.definitionFile)
            val relativePath = currentPath.relativize(pathToImport)

            relativePath.toString()
        } else if(symbol.properties.containsKey("from")) {
            symbol.properties["from"].toString()
        } else {
            ""
        }

        if(module.isEmpty()) {
            return
        }

        val importForFile = imports.computeIfAbsent(module) { mutableSetOf() }
        importForFile.add(Pair(symbol.name, alias))
    }

    override fun toString(): String {
        val result = StringBuilder()

        imports.forEach { (module, importList) ->

            val importContent = importList.joinToString(", ") { (type, alias) ->
                if (alias != null && alias != type) {
                    "$type as $alias"
                } else {
                    type
                }
            }

            result.appendLine("import { $importContent } from \"$module\"")
        }

        return result.toString()
    }
}