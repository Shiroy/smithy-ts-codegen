package com.awacheux.smithy

import software.amazon.smithy.codegen.core.SymbolProvider
import software.amazon.smithy.codegen.core.directed.*

class TypescriptDirectedCodegen: DirectedCodegen<GenerationContext, TypescriptSettings, TypescriptIntegration> {
    /**
     * Create the [SymbolProvider] used to map shapes to code symbols.
     *
     * @param directive Directive context data.
     * @return Returns the created SymbolProvider.
     */
    override fun createSymbolProvider(directive: CreateSymbolProviderDirective<TypescriptSettings>): SymbolProvider = TypescriptSymbolBuilder(directive.model(), directive.service())

    /**
     * Creates the codegen context object.
     *
     * @param directive Directive context data.
     * @return Returns the created context object used by the rest of the directed generation.
     */
    override fun createContext(directive: CreateContextDirective<TypescriptSettings, TypescriptIntegration>): GenerationContext = GenerationContext(
        settings = directive.settings(),
        model = directive.model(),
        symbolProvider = TypescriptSymbolBuilder(directive.model(), directive.service()),
        fileManifest = directive.fileManifest()
    )

    /**
     * Generates the code needed for a service shape.
     *
     * @param directive Directive to perform.
     */
    override fun generateService(directive: GenerateServiceDirective<GenerationContext, TypescriptSettings>) {
        println("Generating service : ${directive.shape()}")
    }

    /**
     * Generates the code needed for a structure shape.
     *
     *
     * This method should not be invoked for structures marked with the
     * `error` trait.
     *
     * @param directive Directive to perform.
     */
    override fun generateStructure(directive: GenerateStructureDirective<GenerationContext, TypescriptSettings>) {
        StructureGeneration(directive).render()
    }

    /**
     * Generates the code needed for an error structure.
     *
     * @param directive Directive to perform.
     */
    override fun generateError(directive: GenerateErrorDirective<GenerationContext, TypescriptSettings>) {
        println("Generating error : ${directive.shape()}")
    }

    /**
     * Generates the code needed for a union shape.
     *
     * @param directive Directive to perform.
     */
    override fun generateUnion(directive: GenerateUnionDirective<GenerationContext, TypescriptSettings>) {
        println("Generating union : ${directive.shape()}")
    }

    /**
     * Generates the code needed for an enum shape, whether it's a string shape
     * marked with the enum trait, or a proper enum shape introduced in Smithy
     * IDL 2.0.
     *
     * @param directive Directive to perform.
     */
    override fun generateEnumShape(directive: GenerateEnumDirective<GenerationContext, TypescriptSettings>) {
        println("Generating enum : ${directive.shape()}")
    }

    /**
     * Generates the code needed for an intEnum shape.
     *
     * @param directive Directive to perform.
     */
    override fun generateIntEnumShape(directive: GenerateIntEnumDirective<GenerationContext, TypescriptSettings>) {
        println("Generating int-enum  : ${directive.shape()}")
    }

}