package com.abstraksi.koleksiskemaamplifier2020.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Blog: Serializable {
    var kind: String? = null
    var id: String? = null
    var blog: Id? = null
    var published: String? = null
    var updated: String? = null
    var url: String? = null
    var selflink: String? = null
    var title: String? = null
    var content: String? = null
    var author: Author? = null
//    val replies: RepliesPost ? = null
}