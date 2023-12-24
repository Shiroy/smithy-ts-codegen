package com.awacheux.smithy

import software.amazon.smithy.codegen.core.Symbol
import software.amazon.smithy.codegen.core.SymbolDependency
import software.amazon.smithy.codegen.core.SymbolDependencyContainer

enum class TypescriptDependencies(dependencyType: String, packageName: String, version: String): SymbolDependencyContainer {

    ZOD("dependencies", "zod", "^3.22.4");

    private val dependency: SymbolDependency =
        SymbolDependency.builder().dependencyType(dependencyType).packageName(packageName).version(version).build()

    /**
     * Gets the list of dependencies that this object introduces.
     *
     *
     * A dependency is a dependency on another package that a Symbol
     * or type requires. It is quite different from a reference since a
     * reference only refers to a symbol; a reference provides no context
     * as to whether or not a dependency is required or the dependency's
     * coordinates.
     *
     * @return Returns the dependencies.
     */
    override fun getDependencies(): MutableList<SymbolDependency> = mutableListOf(dependency)

    companion object {
        fun getZodZSymbol(): Symbol = Symbol.builder().name("z").addDependency(ZOD).putProperty("from", "zod").build()
    }
}