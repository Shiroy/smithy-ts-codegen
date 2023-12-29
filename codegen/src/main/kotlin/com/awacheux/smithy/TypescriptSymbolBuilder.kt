package com.awacheux.smithy

import software.amazon.smithy.codegen.core.Symbol
import software.amazon.smithy.codegen.core.Symbol.Builder
import software.amazon.smithy.codegen.core.SymbolProvider
import software.amazon.smithy.model.Model
import software.amazon.smithy.model.shapes.BigDecimalShape
import software.amazon.smithy.model.shapes.BigIntegerShape
import software.amazon.smithy.model.shapes.BlobShape
import software.amazon.smithy.model.shapes.BooleanShape
import software.amazon.smithy.model.shapes.ByteShape
import software.amazon.smithy.model.shapes.DocumentShape
import software.amazon.smithy.model.shapes.DoubleShape
import software.amazon.smithy.model.shapes.EnumShape
import software.amazon.smithy.model.shapes.FloatShape
import software.amazon.smithy.model.shapes.IntegerShape
import software.amazon.smithy.model.shapes.ListShape
import software.amazon.smithy.model.shapes.LongShape
import software.amazon.smithy.model.shapes.MapShape
import software.amazon.smithy.model.shapes.MemberShape
import software.amazon.smithy.model.shapes.OperationShape
import software.amazon.smithy.model.shapes.ResourceShape
import software.amazon.smithy.model.shapes.ServiceShape
import software.amazon.smithy.model.shapes.Shape
import software.amazon.smithy.model.shapes.ShapeVisitor
import software.amazon.smithy.model.shapes.ShortShape
import software.amazon.smithy.model.shapes.StringShape
import software.amazon.smithy.model.shapes.StructureShape
import software.amazon.smithy.model.shapes.TimestampShape
import software.amazon.smithy.model.shapes.UnionShape
import software.amazon.smithy.model.traits.LengthTrait
import software.amazon.smithy.model.traits.PatternTrait
import software.amazon.smithy.model.traits.RequiredTrait

class TypescriptSymbolBuilder(
    private val model: Model,
    private val service: ServiceShape
) : SymbolProvider, ShapeVisitor<Symbol> {
    override fun toSymbol(shape: Shape): Symbol {
        val symbol = shape.accept(this)
        return symbol
    }

    override fun structureShape(shape: StructureShape): Symbol {
        val name = shape.id.getName(service)
        val builder = Symbol.builder().name(name).definitionFile("models/$name.ts")
        builder.putProperty(SymbolProperties.ZOD_VALIDATOR_NAME, "${name}Validator")

        return builder.build()
    }

    override fun stringShape(shape: StringShape): Symbol {
        val builder = createSymbolBuilder(shape, "string")
        val zodBuilder = listOf(zodString(), applyPatternTrait(shape), applyLengthTrait(shape)).flatten()
        builder.putProperty(SymbolProperties.ZOD_BUILDER, zodBuilder)

        return builder.build()
    }

    override fun bigDecimalShape(shape: BigDecimalShape): Symbol = buildNumberSymbol(shape)

    override fun byteShape(shape: ByteShape): Symbol = buildNumberSymbol(shape)

    override fun shortShape(shape: ShortShape): Symbol = buildNumberSymbol(shape)

    override fun integerShape(shape: IntegerShape): Symbol = buildNumberSymbol(shape)

    override fun longShape(shape: LongShape): Symbol = buildNumberSymbol(shape)

    override fun floatShape(shape: FloatShape): Symbol = buildNumberSymbol(shape)

    override fun doubleShape(shape: DoubleShape): Symbol = buildNumberSymbol(shape)

    override fun bigIntegerShape(shape: BigIntegerShape): Symbol = buildNumberSymbol(shape)

    override fun booleanShape(shape: BooleanShape): Symbol = createSymbolBuilder(shape, "boolean").build()

    override fun enumShape(shape: EnumShape): Symbol = Symbol.builder().name(shape.id.getName(service)).build()

    override fun documentShape(shape: DocumentShape): Symbol =
        Symbol.builder().name("any").build()

    override fun blobShape(shape: BlobShape): Symbol = createSymbolBuilder(shape, "UInt8Array").build()

    override fun listShape(shape: ListShape): Symbol {
        val elementReference = toSymbol(shape.member)
        val builder = Symbol.builder().name("(${elementReference.name})[]").addReference(elementReference)

        val zodValidatorOptional = elementReference.getProperty(SymbolProperties.ZOD_VALIDATOR_NAME)

        val elementReferenceType = if(zodValidatorOptional.isPresent) {
            zodValidatorOptional.get() as String
        } else {
            val elementZodBuilderProperty = elementReference.getProperty(SymbolProperties.ZOD_BUILDER).get()
            val elementZodBuilder = (elementZodBuilderProperty as? List<*>)?.asListOfType<String>()
            requireNotNull(elementZodBuilder)

            builder.addReference(TypescriptDependencies.getZodZSymbol())
            "z.${elementZodBuilder.joinToString(".")}"
        }

        val arrayType = listOf("array($elementReferenceType)")
        val zodBuilder = listOf(arrayType, applyLengthTrait(shape)).flatten()
        builder.putProperty(SymbolProperties.ZOD_BUILDER, zodBuilder)
        return builder.build()
    }

    override fun mapShape(shape: MapShape): Symbol {
        val keyReference = toSymbol(shape.key)
        val valueReference = toSymbol(shape.value)

        return Symbol.builder().name("Record<${keyReference.name}, ${valueReference.name}>").addReference(keyReference)
            .addReference(valueReference).build()
    }

    override fun memberShape(shape: MemberShape): Symbol {
        val targetShape = model.expectShape(shape.target)
        val targetSymbol = toSymbol(targetShape).toBuilder()
        targetSymbol.putProperty(SymbolProperties.MEMBER_REQUIRED, shape.hasTrait(RequiredTrait::class.java))

        return targetSymbol.build()
    }

    override fun unionShape(shape: UnionShape): Symbol {
        val membersReference = shape.members().associate { it.memberName to toSymbol(it) }
        val builder = Symbol.builder().name(shape.id.getName(service))
        membersReference.values.forEach {
            builder.addReference(it)
        }

        return builder.build()
    }

    override fun timestampShape(shape: TimestampShape): Symbol {
        val builder = createSymbolBuilder(shape, "string")
        builder.putProperty(SymbolProperties.ZOD_BUILDER, listOf("string()"))
        return builder.build()
    }

    override fun resourceShape(shape: ResourceShape): Symbol {
        val name = shape.id.getName(service)
        val builder = Symbol.builder().name(name).definitionFile("$name.ts")
        return builder.build()
    }

    override fun operationShape(shape: OperationShape): Symbol =
        Symbol.builder().name(shape.id.getName(service)).build()

    override fun serviceShape(shape: ServiceShape): Symbol = Symbol.builder().name(shape.id.getName(service)).build()

    private fun createSymbolBuilder(shape: Shape, tsTypeName: String): Builder {
        val symbolBuilder = Symbol.builder()
        if (shape.id.namespace == "smithy.api") {
            symbolBuilder.name(tsTypeName)
        } else {
            val name = shape.id.getName(service)
            val definitionFile = "models/$name.ts"
            symbolBuilder.name(name).definitionFile(definitionFile)
            symbolBuilder.putProperty(SymbolProperties.ZOD_VALIDATOR_NAME, "${name}Validator")
        }
        return symbolBuilder
    }

    private fun zodNumber() = listOf("number()")
    private fun zodString() = listOf("string()")

    private fun applyLengthTrait(shape: Shape): List<String> {
        val results = mutableListOf<String>()

        val lengthTraitOpt = shape.getTrait(LengthTrait::class.java)
        lengthTraitOpt.ifPresent { lengthTrait ->
            lengthTrait.min.ifPresent {
                results.add("min($it)")
            }

            lengthTrait.max.ifPresent {
                results.add("max($it)")
            }
        }
        return results
    }

    private fun applyPatternTrait(shape: Shape): List<String> {
        val results = mutableListOf<String>()

        val patternTraitOpt = shape.getTrait(PatternTrait::class.java)
        patternTraitOpt.ifPresent { patternTrait ->
            results.add("pattern(/${patternTrait.pattern}/)")
        }

        return results
    }

    private fun buildNumberSymbol(shape: Shape): Symbol {
        val builder = createSymbolBuilder(shape, "number")
        val zodBuilder = listOf(zodNumber(), applyLengthTrait(shape)).flatten()
        builder.putProperty(SymbolProperties.ZOD_BUILDER, zodBuilder)

        return builder.build()
    }
}
