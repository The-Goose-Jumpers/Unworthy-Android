package io.jumpinggoose.unworthy.utils

import kotlin.random.Random

fun Random.nextFloat(from: Float, until: Float): Float {
    return from + nextFloat() * (until - from)
}
