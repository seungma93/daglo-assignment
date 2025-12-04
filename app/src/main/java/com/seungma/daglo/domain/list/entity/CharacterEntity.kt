package com.seungma.daglo.domain.list.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CharacterEntity (
    val id: Int,
    val image: String,
    val name: String,
    val status: String,
    val gender: String,
    val species: String,
    val orgin: LocationEntity,
    val location: LocationEntity
): Parcelable