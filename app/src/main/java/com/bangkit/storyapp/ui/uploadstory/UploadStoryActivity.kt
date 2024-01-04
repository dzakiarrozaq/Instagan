package com.bangkit.storyapp.ui.uploadstory

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.bangkit.storyapp.R
import com.bangkit.storyapp.databinding.ActivityUploadStoryBinding
import com.bangkit.storyapp.data.response.UserPreferenceDatastore
import com.bangkit.storyapp.data.response.dataStore
import com.bangkit.storyapp.data.response.reduceFileImage
import com.bangkit.storyapp.data.response.rotateBitmap
import com.bangkit.storyapp.data.response.uriToFile
import com.bangkit.storyapp.ui.ViewModelFactory
import com.bangkit.storyapp.ui.main.MainActivity
import com.bangkit.storyapp.ui.main.MainModel
import com.bangkit.storyapp.ui.signin.SignInModel
import java.io.File

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "User")
class UploadStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUploadStoryBinding
    private lateinit var mainViewModel: MainModel
    private lateinit var signViewModel: SignInModel

    companion object {
        const val CAMERA_X_RESULT = 200

        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    getString(R.string.permission),
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = getString(R.string.add_story)


        mainViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreferenceDatastore.getInstance(dataStore))
        )[MainModel::class.java]

        signViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreferenceDatastore.getInstance(dataStore))
        )[SignInModel::class.java]


        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        binding.btnAddCamera.setOnClickListener { startCameraX() }
        binding.btnAddGalery.setOnClickListener { startGallery() }
        binding.buttonAdd.setOnClickListener {
            if (getFile != null) {
                if(binding.edAddDescription.text.toString().isNotEmpty()) {
                    val file = reduceFileImage(getFile as File)
                    signViewModel.getUser().observe(this){user->
                        mainViewModel.postNewStory(user.token, file, binding.edAddDescription.text.toString())
                        mainViewModel.isLoading.observe(this) {
                            showLoading(it)
                        }
                    }
                } else {
                    Toast.makeText(this@UploadStoryActivity, getString(R.string.description_mandatory), Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this@UploadStoryActivity, getString(R.string.image_mandatory), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, getString(R.string.choose_picture))
        launcherIntentGallery.launch(chooser)
    }

    private fun startCameraX() {
        val intent = Intent(this, CameraStoryActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }




    private var getFile: File? = null
    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERA_X_RESULT) {
            val myFile = it.data?.getSerializableExtra("picture") as File
            val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean

            getFile = myFile
            val result = rotateBitmap(
                BitmapFactory.decodeFile(getFile?.path),
                isBackCamera
            )

            binding.tvAddImg.setImageBitmap(result)
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, this@UploadStoryActivity)
            getFile = myFile

            binding.tvAddImg.setImageURI(selectedImg)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBarAdd.visibility = View.VISIBLE
        } else {
            binding.progressBarAdd.visibility = View.GONE

            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }
}