package com.awacheux.smithy

import software.amazon.smithy.model.node.ObjectNode
import software.amazon.smithy.model.shapes.ShapeId

/**
 * Settings for the Typescript code generator.
 *
 * These settings are loaded from the smithy-build.json file.
 */
data class TypescriptSettings(
    val service: ShapeId
) {
    companion object {
        fun from(config: ObjectNode): TypescriptSettings {
            val service = config.expectStringMember("service").expectShapeId()
            return TypescriptSettings(service)
        }
    }
}
