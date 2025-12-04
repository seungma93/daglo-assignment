package com.seungma.daglo.domain.list.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CharactersSearchEntity (
    val characters: List<CharacterEntity>,
    val isLast: Boolean
): Parcelable