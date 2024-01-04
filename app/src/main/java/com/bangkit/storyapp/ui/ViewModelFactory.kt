package com.bangkit.storyapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bangkit.storyapp.data.response.UserPreferenceDatastore
import com.bangkit.storyapp.ui.main.MainModel
import com.bangkit.storyapp.ui.signin.SignInModel
import com.bangkit.storyapp.ui.signup.SignUpModel

class ViewModelFactory(private val pref: UserPreferenceDatastore) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(SignInModel::class.java) -> {
                SignInModel(pref) as T
            }
            modelClass.isAssignableFrom(SignUpModel::class.java) -> {
                SignUpModel(pref) as T
            }
            modelClass.isAssignableFrom(MainModel::class.java) -> {
                MainModel(pref) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }
}