package io.jumpinggoose.unworthy

import io.jumpinggoose.unworthy.models.PlayerData

interface PlayerDataRepository {
    fun getData(onSuccess: (PlayerData) -> Unit, onFailure: (Exception) -> Unit)
    fun setData(data: PlayerData, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)
}
