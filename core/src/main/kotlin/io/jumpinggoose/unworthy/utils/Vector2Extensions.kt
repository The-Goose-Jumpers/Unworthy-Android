package io.jumpinggoose.unworthy.utils

import com.badlogic.gdx.math.Vector2
import ktx.math.plus
import kotlin.math.sqrt

fun Vector2.moveTowards(target: Vector2, maxDistanceDelta: Float): Vector2 {
    val toVectorX = target.x - this.x
    val toVectorY = target.y - this.y

    val sqDist = toVectorX * toVectorX + toVectorY * toVectorY

    if (sqDist == 0f || (maxDistanceDelta >= 0 && sqDist <= maxDistanceDelta * maxDistanceDelta)) {
        return target
    }

    val dist = sqrt(sqDist)

    return this + Vector2(toVectorX / dist * maxDistanceDelta, toVectorY / dist * maxDistanceDelta)
}
