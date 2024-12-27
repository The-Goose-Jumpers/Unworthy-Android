package io.jumpinggoose.unworthy.utils

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.Texture.TextureFilter.Linear
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Matrix3
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import ktx.assets.toInternalFile

fun Sprite(path: String) : Sprite {
    return Sprite(Texture(path.toInternalFile(), true).apply { setFilter(Linear, Linear) })
}

fun Sprite.setPosition(position: Vector2) {
    setPosition(position.x, position.y)
}

fun Sprite.setOriginBasedPosition(position: Vector2) {
    setOriginBasedPosition(position.x, position.y)
}

fun Sprite.getCorners(position: Vector2, scale: Vector2 = Vector2(1f, 1f), rotation: Float = 0f): Array<Vector2> {
    var vector1 = Vector2(-this.originX, -this.originY)
    var vector2 = Vector2(vector1.x + this.width, vector1.y + this.height)
    var vector3 = position.cpy()

    if (scale != Vector2(1f, 1f)) {
        vector1.scl(scale)
        vector2.scl(scale)
    }

    val corners = arrayOf(
        vector1,
        Vector2(vector2.x, vector1.y),
        vector2,
        Vector2(vector1.x, vector2.y)
    )

    if (rotation != 0f) {
        val rotationMatrix = Matrix3().setToRotation(rotation)
        for (i in corners.indices) {
            corners[i].mul(rotationMatrix)
        }
    }

    corners.forEach { it.add(vector3) }
    return corners
}

fun Sprite.getBoundingRectangle(position: Vector2, scale: Vector2 = Vector2(1f, 1f), rotation: Float = 0f): Rectangle {
    val corners = getCorners(position, scale, rotation)
    val minX = corners.minOf { it.x }
    val minY = corners.minOf { it.y }
    val maxX = corners.maxOf { it.x }
    val maxY = corners.maxOf { it.y }
    return Rectangle(minX, minY, maxX - minX, maxY - minY)
}
