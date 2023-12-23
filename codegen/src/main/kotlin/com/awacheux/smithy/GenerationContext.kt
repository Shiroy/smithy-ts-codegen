package com.awacheux.smithy

import software.amazon.smithy.build.FileManifest
import software.amazon.smithy.codegen.core.CodegenContext
import software.amazon.smithy.codegen.core.SymbolProvider
import software.amazon.smithy.codegen.core.WriterDelegator
import software.amazon.smithy.model.Model

data class GenerationContext(
    val settings: TypescriptSettings,
    //val symbolWriter: TypescriptSymbolWriter,
    //val integration: TypescriptIntegration,
    val model: Model,
    // val service: ShapeId,
    val symbolProvider: TypescriptSymbolBuilder,
    val fileManifest: FileManifest
) : CodegenContext<TypescriptSettings, TypescriptSymbolWriter, TypescriptIntegration> {
    private val writerDelegator = WriterDelegator(
        fileManifest,
        symbolProvider,
        TypescriptSymbolWriter.Factory
    )

    /**
     * @return Gets the model being code generated.
     */
    override fun model(): Model = model

    /**
     * @return Gets code generation settings.
     */
    override fun settings(): TypescriptSettings = settings

    /**
     * @return Gets the SymbolProvider used for code generation.
     */
    override fun symbolProvider(): SymbolProvider = symbolProvider

    /**
     * @return Gets the FileManifest being written to for code generation.
     */
    override fun fileManifest(): FileManifest = fileManifest

    /**
     * Get the WriterDelegator used for generating code.
     *
     *
     * Generates might need other delegators for specific purposes, and it's fine to
     * add more methods for those specific purposes. If an implementation uses a specific
     * subclass of a WriterDelegator, implementations can override this method to return
     * a more specific WriterDelegator type.
     *
     * @return Returns the writer delegator used by the generator.
     */
    override fun writerDelegator(): WriterDelegator<TypescriptSymbolWriter> = writerDelegator

    /**
     * @return Gets the SmithyIntegrations used for code generation.
     */
    override fun integrations(): MutableList<TypescriptIntegration> = mutableListOf()
}
