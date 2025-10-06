// path: data/product/src/main/java/com/trendy/data/product/remote/dto/ProductDto.kt
package com.trendy.data.product.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ProductDto(
    @Json(name = "id") val id: Int,
    @Json(name = "title") val title: String,
    @Json(name = "price") val price: Double,
    @Json(name = "description") val description: String,
    @Json(name = "category") val category: String,
    @Json(name = "image") val image: String,
    @Json(name = "rating") val rating: RatingDto?
) {
    @JsonClass(generateAdapter = true)
    data class RatingDto(
        @Json(name = "rate") val rate: Double? = null,
        @Json(name = "count") val count: Int? = null
    )
}
