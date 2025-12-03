package com.seungma.daglo.domain.list.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CharactersLoadEntity (
    val characters: List<CharacterEntity>,
    val nextPage: String?
): Parcelable