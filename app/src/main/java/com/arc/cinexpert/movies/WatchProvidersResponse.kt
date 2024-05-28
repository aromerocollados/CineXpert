package com.arc.cinexpert.movies

data class WatchProvidersResponse(
    val id: Int,
    val results: Map<String, WatchProviderDetails>
)

data class WatchProviderDetails(
    val link: String?,
    val flatrate: List<Provider>?,
    val rent: List<Provider>?,
    val buy: List<Provider>?
)

data class Provider(
    val display_priority: Int,
    val logo_path: String,
    val provider_id: Int,
    val provider_name: String
)
