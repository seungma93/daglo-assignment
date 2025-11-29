package com.seungma.daglo.network.retrofit.dto

import com.google.gson.annotations.SerializedName

data class InfoDto(
    @SerializedName("count") val count: Int,
    @SerializedName("pages") val pages: Int,
    @SerializedName("next") val next: String?,
    @SerializedName("prev") val prev: String?
)

data class PagedResponseDto<T>(
    @SerializedName("info") val info: InfoDto,
    @SerializedName("results") val results: List<T>
)

data class SimpleLocationRef(
    @SerializedName("name") val name: String,
    @SerializedName("url") val url: String
)

data class CharacterDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("status") val status: String,
    @SerializedName("species") val species: String,
    @SerializedName("type") val type: String,
    @SerializedName("gender") val gender: String,
    @SerializedName("origin") val origin: SimpleLocationRef,
    @SerializedName("location") val location: SimpleLocationRef,
    @SerializedName("image") val image: String,
    @SerializedName("episode") val episode: List<String>,
    @SerializedName("url") val url: String,
    @SerializedName("created") val created: String
)