package com.example.test.api

import com.google.gson.annotations.SerializedName

data class AsinSearchResponse(
    @SerializedName("search_results")
    val searchResults: List<SearchResult>
)

data class SearchResult(
    val asin: String
)