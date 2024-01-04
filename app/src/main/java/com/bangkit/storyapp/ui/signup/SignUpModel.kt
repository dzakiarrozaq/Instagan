package com.bangkit.storyapp.ui.signup

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bangkit.storyapp.data.retrofit.ApiConfig
import com.bangkit.storyapp.data.response.SignUpResponse
import com.bangkit.storyapp.data.response.UserPreferenceDatastore
import com.bangkit.storyapp.ui.signin.SignInModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpModel(private val pref: UserPreferenceDatastore) : ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    val error = MutableLiveData("")
    val message = MutableLiveData("")
    private val TAG = SignInModel::class.simpleName

    fun signup(name: String, email: String, password: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().doSignup(name, email, password)
        client.enqueue(object : Callback<SignUpResponse> {
            override fun onResponse(call: Call<SignUpResponse>, response: Response<SignUpResponse>) {
                when (response.code()) {
                    400 -> error.postValue("400")
                    201 -> message.postValue("201")
                    else -> error.postValue("ERROR ${response.code()} : ${response.errorBody()}")
                }
                _isLoading.value = false
            }
            override fun onFailure(call: Call<SignUpResponse>, t: Throwable) {
                _isLoading.value = true
                Log.e(TAG, "onFailure Call: ${t.message}")
                error.postValue(t.message)
            }
        })
    }
}