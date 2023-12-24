package com.awacheux.smithy

import software.amazon.smithy.codegen.core.Symbol
import software.amazon.smithy.codegen.core.Symbol.Builder
import software.amazon.smithy.codegen.core.SymbolProvider
import software.amazon.smithy.model.Model
import software.amazon.smithy.model.shapes.*

class TypescriptSymbolBuilder(private val model: Model, private val service: ServiceShape) : SymbolProvider, ShapeVisitor<Symbol> {
    override fun toSymbol(shape: Shape): Symbol {
        val symbol = shape.accept(this)
        return symbol
    }

    override fun structureShape(shape: StructureShape): Symbol {
        val name = shape.id.getName(service)
        return Symbol.builder().name(name).definitionFile("$name.ts").build()
    }

    override fun stringShape(shape: StringShape): Symbol = createSymbolBuilder(shape, "string").build()

    override fun bigDecimalShape(shape: BigDecimalShape): Symbol = createSymbolBuilder(shape, "number").build()

    override fun byteShape(shape: ByteShape): Symbol = createSymbolBuilder(shape, "number").build()

    override fun shortShape(shape: ShortShape): Symbol = createSymbolBuilder(shape, "number").build()

    override fun integerShape(shape: IntegerShape): Symbol = createSymbolBuilder(shape, "number").build()

    override fun longShape(shape: LongShape): Symbol = createSymbolBuilder(shape, "number").build()

    override fun floatShape(shape: FloatShape): Symbol = createSymbolBuilder(shape, "number").build()

    override fun doubleShape(shape: DoubleShape): Symbol = createSymbolBuilder(shape, "number").build()

    override fun bigIntegerShape(shape: BigIntegerShape): Symbol = createSymbolBuilder(shape, "number").build()

    override fun booleanShape(shape: BooleanShape): Symbol =createSymbolBuilder(shape, "boolean").build()

    override fun enumShape(shape: EnumShape): Symbol = Symbol.builder().name(shape.id.getName(service)).build()

    override fun documentShape(shape: DocumentShape): Symbol =
        Symbol.builder().name("any").build()

    override fun blobShape(shape: BlobShape): Symbol = createSymbolBuilder(shape, "UInt8Array").build()

    override fun listShape(shape: ListShape): Symbol {
        val elementReference = toSymbol(shape.member)
        return Symbol.builder().name("(${elementReference.name})[]").addReference(elementReference).build()
    }

    override fun mapShape(shape: MapShape): Symbol {
        val keyReference = toSymbol(shape.key)
        val valueReference = toSymbol(shape.value)

        return Symbol.builder().name("Record<${keyReference.name}, ${valueReference.name}>").addReference(keyReference)
            .addReference(valueReference).build()
    }

    override fun memberShape(shape: MemberShape): Symbol {
        val targetShape = model.expectShape(shape.target)
        val targetSymbol = toSymbol(targetShape)

        return targetSymbol
    }

    override fun unionShape(shape: UnionShape): Symbol {
        val membersReference = shape.members().associate { it.memberName to toSymbol(it) }
        val builder = Symbol.builder().name(shape.id.getName(service))
        membersReference.values.forEach {
            builder.addReference(it)
        }

        return builder.build()
    }

    override fun timestampShape(shape: TimestampShape): Symbol = createSymbolBuilder(shape, "Date").build()

    override fun resourceShape(shape: ResourceShape): Symbol {
        val name = shape.id.getName(service)
        val builder = Symbol.builder().name(name).definitionFile("$name.ts")
        return builder.build()
    }

    override fun operationShape(shape: OperationShape): Symbol = Symbol.builder().name(shape.id.getName(service)).build()

    override fun serviceShape(shape: ServiceShape): Symbol = Symbol.builder().name(shape.id.getName(service)).build()

    private fun createSymbolBuilder(shape: Shape, tsTypeName: String): Builder {
        val symbolBuilder = Symbol.builder()
        if (shape.id.namespace == "smithy.api") {
            symbolBuilder.name(tsTypeName)
        } else {
            val name = shape.id.getName(service)
            val definitionFile = "models/$name.ts"
            symbolBuilder.name(name).definitionFile(definitionFile)
        }
        return symbolBuilder
    }
}
