package com.seungma.daglo.domain.list.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CharacterItemKeyEntity(
    val characterId: Int
) : Parcelable
