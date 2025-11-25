package com.sample.storyapp2.ui.addstory

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.sample.storyapp2.R
import com.sample.storyapp2.databinding.ActivityAddStoryBinding
import com.sample.storyapp2.utils.ImageUtils
import com.sample.storyapp2.utils.Result
import com.sample.storyapp2.viewmodel.AddStoryViewModel
import com.sample.storyapp2.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class AddStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddStoryBinding
    private var currentImageUri: Uri? = null
    private var currentPhotoFile: File? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentLocation: Location? = null
    private val viewModel: AddStoryViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions[Manifest.permission.CAMERA] == true -> {
                Toast.makeText(this, "Camera permission granted", Toast.LENGTH_SHORT).show()
            }
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                    permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true -> {
                getMyLastLocation()
            }
        }
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        }
    }

    private val launcherCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            currentImageUri = Uri.fromFile(currentPhotoFile)
            showImage()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }

        setupAction()
        setupBackPressHandler()
    }

    private fun setupBackPressHandler() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })
    }

    private fun allPermissionsGranted() = ContextCompat.checkSelfPermission(
        this, Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED

    private fun checkLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getMyLastLocation() {
        if (checkLocationPermission()) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    currentLocation = location
                    Toast.makeText(
                        this,
                        "Location acquired: ${location.latitude}, ${location.longitude}",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        this,
                        "Location not found. Make sure location is enabled",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun setupAction() {
        with(binding) {
            btnGallery.setOnClickListener {
                startGallery()
            }

            btnCamera.setOnClickListener {
                startCamera()
            }

            cbAddLocation.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    getMyLastLocation()
                } else {
                    currentLocation = null
                }
            }

            buttonAdd.setOnClickListener {
                uploadStory()
            }
        }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun startCamera() {
        currentPhotoFile = ImageUtils.createTempFile(this)
        val photoURI: Uri = FileProvider.getUriForFile(
            this,
            "${applicationContext.packageName}.fileprovider",
            currentPhotoFile!!
        )
        launcherCamera.launch(photoURI)
    }

    private fun showImage() {
        currentImageUri?.let {
            binding.ivPreview.setImageURI(it)
        }
    }

    private fun uploadStory() {
        val description = binding.edAddDescription.text.toString()

        when {
            currentImageUri == null -> {
                Toast.makeText(this, getString(R.string.choose_photo), Toast.LENGTH_SHORT).show()
            }
            description.isEmpty() -> {
                binding.edAddDescription.error = getString(R.string.error_empty_description)
            }
            else -> {
                val imageFile = ImageUtils.uriToFile(currentImageUri!!, this)
                val compressedFile = ImageUtils.reduceFileImage(imageFile)

                if (compressedFile.length() > 1024 * 1024) {
                    Toast.makeText(this, getString(R.string.image_too_large), Toast.LENGTH_SHORT).show()
                    return
                }

                val requestImageFile = compressedFile.asRequestBody("image/jpeg".toMediaType())
                val imageMultipart = MultipartBody.Part.createFormData(
                    "photo",
                    compressedFile.name,
                    requestImageFile
                )
                val requestDescription = description.toRequestBody("text/plain".toMediaType())

                val isLocationEnabled = binding.cbAddLocation.isChecked && currentLocation != null
                val lat = if (isLocationEnabled) {
                    currentLocation!!.latitude.toString().toRequestBody("text/plain".toMediaType())
                } else null

                val lon = if (isLocationEnabled) {
                    currentLocation!!.longitude.toString().toRequestBody("text/plain".toMediaType())
                } else null

                lifecycleScope.launch {
                    viewModel.uploadStory(imageMultipart, requestDescription, lat, lon)
                        .observe(this@AddStoryActivity) { result ->
                            when (result) {
                                is Result.Loading -> {
                                    showLoading(true)
                                }
                                is Result.Success -> {
                                    showLoading(false)
                                    Toast.makeText(
                                        this@AddStoryActivity,
                                        getString(R.string.upload_success),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    val intent = Intent()
                                    setResult(RESULT_OK, intent)
                                    finish()
                                }
                                is Result.Error -> {
                                    showLoading(false)
                                    Toast.makeText(
                                        this@AddStoryActivity,
                                        result.error,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.apply {
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            buttonAdd.isEnabled = !isLoading
            btnGallery.isEnabled = !isLoading
            btnCamera.isEnabled = !isLoading
        }
    }
}