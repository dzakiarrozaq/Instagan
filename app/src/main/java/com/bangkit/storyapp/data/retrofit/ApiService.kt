package com.bangkit.storyapp.data.retrofit

import com.bangkit.storyapp.data.response.AddResponse
import com.bangkit.storyapp.data.response.SignInResponse
import com.bangkit.storyapp.data.response.SignUpResponse
import com.bangkit.storyapp.data.response.StoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part


interface ApiService {

    @FormUrlEncoded
    @POST("/v1/register")
    fun doSignup(
        @Field("name") name: String?,
        @Field("email") email: String?,
        @Field("password") password: String?
    ): Call<SignUpResponse>

    @FormUrlEncoded
    @POST("/v1/login")
    fun doSignin(
        @Field("email") email: String?,
        @Field("password") password: String?
    ): Call<SignInResponse>

    @GET("v1/stories")
    fun getListStory(
        @Header("Authorization") bearer: String?
    ): Call<StoryResponse>

    @Multipart
    @POST("/v1/stories")
    fun postNewStory(
        @Header("Authorization") bearer: String?,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody?,
    ): Call<AddResponse>
}