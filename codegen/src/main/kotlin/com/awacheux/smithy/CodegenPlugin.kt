package com.awacheux.smithy

import software.amazon.smithy.build.PluginContext
import software.amazon.smithy.build.SmithyBuildPlugin
import software.amazon.smithy.codegen.core.directed.CodegenDirector

class CodegenPlugin : SmithyBuildPlugin{
    override fun getName(): String = "smithy-ts-codegen"

    override fun execute(context: PluginContext) {
        val runner = CodegenDirector<TypescriptSymbolWriter, TypescriptIntegration, GenerationContext, TypescriptSettings>()

        runner.directedCodegen(TypescriptDirectedCodegen())

        runner.integrationClass(TypescriptIntegration::class.java)
        runner.fileManifest(context.fileManifest)
        runner.model(context.model)

        val settings = TypescriptSettings.from(context.settings)

        runner.settings(settings)

        runner.service(settings.service)

        runner.performDefaultCodegenTransforms()

        runner.createDedicatedInputsAndOutputs()

        runner.run()
    }
}
