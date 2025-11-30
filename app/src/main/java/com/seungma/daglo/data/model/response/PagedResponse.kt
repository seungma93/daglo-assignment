package com.seungma.daglo.data.model.response

import com.google.gson.annotations.SerializedName

data class PagedResponse(
    @SerializedName("info") val info: InfoResponse?,
    @SerializedName("results") val results: List<CharacterResponse>?
)