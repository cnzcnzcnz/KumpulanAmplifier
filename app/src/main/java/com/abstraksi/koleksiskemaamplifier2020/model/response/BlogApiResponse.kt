package com.abstraksi.koleksiskemaamplifier2020.model.response

import com.abstraksi.koleksiskemaamplifier2020.model.Blog
import com.google.gson.annotations.SerializedName


data class BlogApiResponse (
    @SerializedName("kind")
    var kind: String ? = null,
    @SerializedName("nextPageToken")
    var nextPageToken: String ? = null,
    @SerializedName(value ="items", alternate = ["Items"] )
    var items: ArrayList<Blog> ? = null
)