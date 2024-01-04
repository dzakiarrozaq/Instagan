package com.bangkit.storyapp.data.response

import com.google.gson.annotations.SerializedName

data class AddResponse(

    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String
)