package com.bangkit.storyapp.ui.signin

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.bangkit.storyapp.ui.main.MainActivity
import com.bangkit.storyapp.R
import com.bangkit.storyapp.databinding.ActivitySignInBinding
import com.bangkit.storyapp.data.response.UserPreferenceDatastore
import com.bangkit.storyapp.ui.ViewModelFactory
import com.bangkit.storyapp.ui.signup.SignUpActivity

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "User")

class SignInActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignInBinding
    private lateinit var signinViewModel: SignInModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupViewModel()
        setupAction()
        playAnimation()

        binding.haveAccountTextView.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }


    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupViewModel() {
        signinViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreferenceDatastore.getInstance(dataStore))
        )[SignInModel::class.java]

        signinViewModel.let { vmsignin ->
            vmsignin.signinResult.observe(this) { signin ->
                // success signin process triggered -> save preferences
                vmsignin.saveUser(
                    signin.loginResult.name,
                    signin.loginResult.userId,
                    signin.loginResult.token
                )

            }
            vmsignin.message.observe(this) { message ->
                if (message == "200") {
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle(R.string.info)
                    builder.setMessage(R.string.validate_login_success)
                    builder.setIcon(R.drawable.centang_hijau)
                    val alertDialog: AlertDialog = builder.create()
                    alertDialog.setCancelable(false)
                    alertDialog.show()
                    Handler(Looper.getMainLooper()).postDelayed({
                        alertDialog.dismiss()
                        val intent = Intent(this, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
                    }, 2000)
                }
            }
            vmsignin.error.observe(this) { error ->
                if (error == "400") {
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle(R.string.info)
                    builder.setMessage(R.string.label_invalid_email)
                    builder.setIcon(R.drawable.silang_merah)
                    val alertDialog: AlertDialog = builder.create()
                    alertDialog.setCancelable(false)
                    alertDialog.show()
                    Handler(Looper.getMainLooper()).postDelayed({
                        alertDialog.dismiss()
                    }, 2000)
                }
                if (error == "401") {
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle(R.string.info)
                    builder.setMessage(R.string.login_user_not_found)
                    builder.setIcon(R.drawable.silang_merah)
                    val alertDialog: AlertDialog = builder.create()
                    alertDialog.setCancelable(false)
                    alertDialog.show()
                    Handler(Looper.getMainLooper()).postDelayed({
                        alertDialog.dismiss()
                    }, 2000)
                }
            }
            vmsignin.isLoading.observe(this) {
                showLoading(it)
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBarLogin.visibility = View.VISIBLE
        } else {
            binding.progressBarLogin.visibility = View.GONE
        }
    }

    private fun playAnimation() {
        val haveAccountTextView = ObjectAnimator.ofFloat(binding.haveAccountTextView, View.ALPHA, 1f).setDuration(500)
        val tvSignin = ObjectAnimator.ofFloat(binding.tvSignin, View.ALPHA, 1f).setDuration(500)
        val ivSignin = ObjectAnimator.ofFloat(binding.ivSignin, View.ALPHA, 1f).setDuration(500)
        val tvLoginEmail = ObjectAnimator.ofFloat(binding.tvLoginEmail, View.ALPHA, 1f).setDuration(500)
        val edLoginEmail = ObjectAnimator.ofFloat(binding.edLoginEmail, View.ALPHA, 1f).setDuration(500)
        val tvEdLoginPassword = ObjectAnimator.ofFloat(binding.tvEdLoginPassword, View.ALPHA, 1f).setDuration(500)
        val edLoginPassword = ObjectAnimator.ofFloat(binding.edLoginPassword, View.ALPHA, 1f).setDuration(500)
        val signin = ObjectAnimator.ofFloat(binding.signinButton, View.ALPHA, 1f).setDuration(500)
        val copyrightTextView = ObjectAnimator.ofFloat(binding.copyrightTextView, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(tvSignin, ivSignin, tvLoginEmail, edLoginEmail, tvEdLoginPassword, edLoginPassword, haveAccountTextView, signin, copyrightTextView)
            startDelay = 500
        }.start()
    }

    private fun setupAction() {
        binding.signinButton.setOnClickListener {
            val email = binding.edLoginEmail.text.toString()
            val password = binding.edLoginPassword.text.toString()
            when {
                email.isEmpty() -> {
                    binding.edLoginEmail.error = getString(R.string.input_name)
                }
                password.isEmpty() -> {
                    binding.edLoginPassword.error = getString(R.string.input_password)
                }
                password.length < 6 -> {
                    binding.edLoginPassword.error = getString(R.string.label_validation_password)
                }

                else -> {
                    signinViewModel.signin(email, password)
                }
            }
        }
    }

}