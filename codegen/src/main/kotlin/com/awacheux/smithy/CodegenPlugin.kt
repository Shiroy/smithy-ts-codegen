package com.awacheux.smithy

import software.amazon.smithy.build.PluginContext
import software.amazon.smithy.build.SmithyBuildPlugin
import software.amazon.smithy.model.shapes.ServiceShape

class CodegenPlugin : SmithyBuildPlugin{
    override fun getName(): String = "smithy-ts-codegen"

    override fun execute(context: PluginContext) {
        val settings = TypescriptSettings.from(context.settings)

        val model = context.model
        val serviceShape = model.expectShape(settings.service, ServiceShape::class.java)

        val codeGenerator = TypescriptGenerator(
            ZodElementBuilder(model, serviceShape),
            context.fileManifest,
            settings,
            model
        )

        codeGenerator.run()
    }
}
