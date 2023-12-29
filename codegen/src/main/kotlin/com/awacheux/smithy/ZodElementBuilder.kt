package com.awacheux.smithy

import com.awacheux.smithy.zod.ZodArray
import com.awacheux.smithy.zod.ZodBoolean
import com.awacheux.smithy.zod.ZodElement
import com.awacheux.smithy.zod.ZodNumber
import com.awacheux.smithy.zod.ZodObject
import com.awacheux.smithy.zod.ZodString
import software.amazon.smithy.codegen.core.Symbol
import software.amazon.smithy.codegen.core.SymbolProvider
import software.amazon.smithy.model.Model
import software.amazon.smithy.model.shapes.BigDecimalShape
import software.amazon.smithy.model.shapes.BigIntegerShape
import software.amazon.smithy.model.shapes.BlobShape
import software.amazon.smithy.model.shapes.BooleanShape
import software.amazon.smithy.model.shapes.ByteShape
import software.amazon.smithy.model.shapes.DocumentShape
import software.amazon.smithy.model.shapes.DoubleShape
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

class ZodElementBuilder(
    private val model: Model,
    private val service: ServiceShape
) : ShapeVisitor<ZodElement>, SymbolProvider {

    override fun toSymbol(shape: Shape): Symbol = shape.accept(this).getTypeSymbol()
    override fun blobShape(shape: BlobShape): ZodElement {
        TODO("Not yet implemented")
    }

    override fun booleanShape(shape: BooleanShape): ZodElement = ZodBoolean(extractShapeName(shape))

    override fun listShape(shape: ListShape): ZodElement {
        val childZodElement = shape.member.accept(this)
        return ZodArray(extractShapeName(shape), childZodElement)
    }

    override fun mapShape(shape: MapShape): ZodElement {
        TODO("Not yet implemented")
    }

    override fun byteShape(shape: ByteShape): ZodElement = ZodNumber(
        extractShapeName(shape),
        integer = true,
        min = -128,
        max = 127
    )

    override fun shortShape(shape: ShortShape): ZodElement = ZodNumber(
        extractShapeName(shape),
        integer = true,
        min = -32768,
        max = 32767
    )

    override fun integerShape(shape: IntegerShape): ZodElement = ZodNumber(
        extractShapeName(shape),
        integer = true,
        min = -2147483648,
        max = 2147483647
    )

    override fun longShape(shape: LongShape): ZodElement = ZodNumber(
        extractShapeName(shape),
        integer = true
    )

    override fun floatShape(shape: FloatShape): ZodElement = ZodNumber(extractShapeName(shape))

    override fun documentShape(shape: DocumentShape): ZodElement {
        TODO("Not yet implemented")
    }

    override fun doubleShape(shape: DoubleShape): ZodElement = ZodNumber(extractShapeName(shape))


    override fun bigIntegerShape(shape: BigIntegerShape): ZodElement = ZodNumber(
        extractShapeName(shape),
        integer = true
    )

    override fun bigDecimalShape(shape: BigDecimalShape): ZodElement = ZodNumber(extractShapeName(shape))

    override fun operationShape(shape: OperationShape): ZodElement {
        TODO("Not yet implemented")
    }

    override fun resourceShape(shape: ResourceShape): ZodElement {
        TODO("Not yet implemented")
    }

    override fun serviceShape(shape: ServiceShape): ZodElement {
        TODO("Not yet implemented")
    }

    override fun stringShape(shape: StringShape): ZodElement = ZodString(extractShapeName(shape))

    override fun structureShape(shape: StructureShape): ZodElement {
        val properties = shape.members().map { member ->
            val memberShape = model.expectShape(member.target)
            val memberZodElement = memberShape.accept(this)
            ZodObject.Property(
                member.memberName,
                memberZodElement
            )
        }

        val shapeName = extractShapeName(shape)
        requireNotNull(shapeName) { "A structure must have a name" }

        return ZodObject(shapeName, properties)
    }

    override fun unionShape(shape: UnionShape): ZodElement {
        TODO("Not yet implemented")
    }

    override fun memberShape(shape: MemberShape): ZodElement {
        val targetShape = model.expectShape(shape.target)
        return targetShape.accept(this)
    }

    override fun timestampShape(shape: TimestampShape): ZodElement = ZodString(extractShapeName(shape))

    private fun extractShapeName(shape: Shape): String? = if (shape.id.namespace == "smithy.api") {
        null
    } else {
        shape.id.getName(service)
    }
}
