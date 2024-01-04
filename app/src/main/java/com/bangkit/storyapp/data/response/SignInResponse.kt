package com.bangkit.storyapp.data.response

import com.google.gson.annotations.SerializedName

data class SignInResponse(

    @field:SerializedName("loginResult")
    val loginResult: SignInResult,

    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String
)

data class SignInResult(

    @field:SerializedName("name")
    val name: String,

    @field:SerializedName("userId")
    val userId: String,

    @field:SerializedName("token")
    val token: String
)