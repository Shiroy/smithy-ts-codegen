package com.awacheux.smithy

import software.amazon.smithy.codegen.core.SmithyIntegration

interface TypescriptIntegration: SmithyIntegration<TypescriptSettings, TypescriptSymbolWriter, GenerationContext> {
}