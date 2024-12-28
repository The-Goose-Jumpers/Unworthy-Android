package io.jumpinggoose.unworthy.android.repository.models

import io.jumpinggoose.unworthy.models.PlayerData

data class AndroidPlayerData(
    val owner: String = "",
    val data: PlayerData = PlayerData()
)
