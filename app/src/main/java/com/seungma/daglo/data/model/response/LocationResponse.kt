package com.seungma.daglo.data.model.response

import com.google.gson.annotations.SerializedName

data class LocationResponse(
    @SerializedName("name") val name: String?,
    @SerializedName("url") val url: String?
)