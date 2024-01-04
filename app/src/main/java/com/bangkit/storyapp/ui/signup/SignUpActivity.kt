package com.bangkit.storyapp.ui.signup

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
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
import androidx.lifecycle.ViewModelProvider
import com.bangkit.storyapp.R
import com.bangkit.storyapp.databinding.ActivitySignUpBinding
import com.bangkit.storyapp.data.response.UserPreferenceDatastore
import com.bangkit.storyapp.data.response.dataStore
import com.bangkit.storyapp.ui.ViewModelFactory
import com.bangkit.storyapp.ui.signin.SignInActivity

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var signupViewModel: SignUpModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupViewModel()
        setupAction()
        playAnimation()
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
        signupViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreferenceDatastore.getInstance(dataStore))
        )[SignUpModel::class.java]

        signupViewModel?.let { signupvm ->
            signupvm.message.observe(this) { message ->
                if (message == "201") {
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle(R.string.info)
                    builder.setMessage(R.string.validate_register_success)
                    builder.setIcon(R.drawable.centang_hijau)
                    val alertDialog: AlertDialog = builder.create()
                    alertDialog.setCancelable(false)
                    alertDialog.show()
                    Handler(Looper.getMainLooper()).postDelayed({
                        alertDialog.dismiss()
                        val intent = Intent(this, SignInActivity::class.java)
                        startActivity(intent)
                        finish()
                    }, 2000)
                }
            }
            signupvm.error.observe(this) { error ->
                if (error == "400") {
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle(R.string.info)
                    builder.setMessage(R.string.validate_register_failed)
                    builder.setIcon(R.drawable.silang_merah)
                    val alertDialog: AlertDialog = builder.create()
                    alertDialog.setCancelable(false)
                    alertDialog.show()
                    Handler(Looper.getMainLooper()).postDelayed({
                        alertDialog.dismiss()
                    }, 2000)
                }
            }
            signupvm.isLoading.observe(this) {
                showLoading(it)
            }

        }
    }
    private fun showLoading(isLoading: Boolean) {

        if (isLoading) {
            binding.progressBarRegister.visibility = View.VISIBLE
        } else {
            binding.progressBarRegister.visibility = View.GONE
        }
    }


    private fun setupAction() {
        binding.signupButton.setOnClickListener {
            val name = binding.edRegisterName.text.toString()
            val email = binding.edRegisterEmail.text.toString()
            val password = binding.edRegisterPassword.text.toString()
            when {
                name.isEmpty() -> {
                    binding.edRegisterName.error = getString(R.string.input_name)
                }
                email.isEmpty() -> {
                    binding.edRegisterEmail.error = getString(R.string.input_email)
                }
                password.isEmpty() -> {
                    binding.edRegisterPassword.error = getString(R.string.input_password)
                }
                password.length < 6 -> {
                    binding.edRegisterPassword.error = getString(R.string.label_validation_password)
                }
                else -> {
                    signupViewModel.signup(name, email, password)
                }
            }
        }
    }

    private fun playAnimation() {
        val tvSignup = ObjectAnimator.ofFloat(binding.tvSignup, View.ALPHA, 1f).setDuration(500)
        val ivSignup = ObjectAnimator.ofFloat(binding.ivSignup, View.ALPHA, 1f).setDuration(500)
        val tvRegisterName = ObjectAnimator.ofFloat(binding.tvRegisterName, View.ALPHA, 1f).setDuration(500)
        val edRegisterName = ObjectAnimator.ofFloat(binding.edRegisterName, View.ALPHA, 1f).setDuration(500)
        val tvRegisterEmail = ObjectAnimator.ofFloat(binding.tvRegisterEmail, View.ALPHA, 1f).setDuration(500)
        val edRegisterEmail = ObjectAnimator.ofFloat(binding.edRegisterEmail, View.ALPHA, 1f).setDuration(500)
        val tvRegisterPassword = ObjectAnimator.ofFloat(binding.tvRegisterPassword, View.ALPHA, 1f).setDuration(500)
        val edRegisterPassword = ObjectAnimator.ofFloat(binding.edRegisterPassword, View.ALPHA, 1f).setDuration(500)
        val signup = ObjectAnimator.ofFloat(binding.signupButton, View.ALPHA, 1f).setDuration(500)
        val copyrightTextView = ObjectAnimator.ofFloat(binding.copyrightTextView, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(tvSignup, ivSignup, tvRegisterName, edRegisterName, tvRegisterEmail, edRegisterEmail, tvRegisterPassword, edRegisterPassword, signup, copyrightTextView)
            startDelay = 500
        }.start()
    }
}