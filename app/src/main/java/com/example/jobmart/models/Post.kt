package com.example.jobmart.models

import com.google.firebase.firestore.PropertyName

data class Post(
    var description: String = "",
    @get:PropertyName("image_url")   @set:PropertyName("image_url") var imageUrl: String = "",
    @get:PropertyName("creation_time_ms")   @set:PropertyName("creation_time_ms") var creationTimeMs: Long = 0,
    var address: String = "",
    var pay: String = "",
    var user: User? = null
)