package io.jumpinggoose.unworthy.objects

import io.jumpinggoose.unworthy.Constants
import io.jumpinggoose.unworthy.core.GameObjectList

class Terrain(id: String) : GameObjectList(id) {
    init {
        layer = Constants.LAYER_GROUND
    }
}
