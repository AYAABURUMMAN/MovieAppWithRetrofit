package com.movie.data.models


import com.google.gson.annotations.SerializedName
import com.movie.data.models.Movies


data class  ApiListResponse (

  @SerializedName("page"          ) var page         : Int?               = null,
  @SerializedName("results"       ) var results      : List<Movies> = listOf(),
  @SerializedName("total_pages"   ) var totalPages   : Int?               = null,
  @SerializedName("total_results" ) var totalResults : Int?               = null

)