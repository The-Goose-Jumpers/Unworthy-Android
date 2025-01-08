package io.jumpinggoose.unworthy.utils

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Circle
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2

fun SpriteBatch.fillRectangle(x: Float, y: Float, width: Float, height: Float, color: Color = Color.WHITE) {
    DrawingHelper.fillRectangle(this, x, y, width, height, color)
}

fun SpriteBatch.fillRectangle(position: Vector2, width: Float, height: Float, color: Color = Color.WHITE) {
    DrawingHelper.fillRectangle(this, position.x, position.y, width, height, color)
}

fun SpriteBatch.fillRectangle(rect: Rectangle, color: Color = Color.WHITE) {
    DrawingHelper.fillRectangle(this, rect.x, rect.y, rect.width, rect.height, color)
}

fun SpriteBatch.drawRectangle(rect: Rectangle, color: Color = Color.WHITE, thickness: Float = 1f) {
    DrawingHelper.drawRectangle(this, rect.x, rect.y, rect.width, rect.height, color, thickness)
}

fun SpriteBatch.fillCircle(x: Float, y: Float, radius: Float, color: Color = Color.WHITE) {
    DrawingHelper.fillCircle(this, x, y, radius, color)
}

fun SpriteBatch.fillCircle(position: Vector2, radius: Float, color: Color = Color.WHITE) {
    DrawingHelper.fillCircle(this, position.x, position.y, radius, color)
}

fun SpriteBatch.fillCircle(circle: Circle, color: Color = Color.WHITE) {
    DrawingHelper.fillCircle(this, circle.x, circle.y, circle.radius, color)
}

fun SpriteBatch.drawCircle(x: Float, y: Float, radius: Float, color: Color = Color.WHITE, thickness: Float = 1f) {
    DrawingHelper.drawCircle(this, x, y, radius, color, thickness)
}

fun SpriteBatch.drawCircle(position: Vector2, radius: Float, color: Color = Color.WHITE, thickness: Float = 1f) {
    DrawingHelper.drawCircle(this, position.x, position.y, radius, color, thickness)
}

fun SpriteBatch.drawCircle(circle: Circle, color: Color = Color.WHITE, thickness: Float = 1f) {
    DrawingHelper.drawCircle(this, circle.x, circle.y, circle.radius, color, thickness)
}
