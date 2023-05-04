package com.example.test.api

import com.google.gson.annotations.SerializedName

data class ImageSearchResponse(
    @SerializedName("items") val items: List<ImageResult>?
)

data class ImageResult(
    @SerializedName("link") val imageUrl: String,
    @SerializedName("image") val image: ImageContext // Add this line
)

data class ImageContext( // Add this class
    @SerializedName("contextLink") val source: String
)