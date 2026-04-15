package com.ecse489.id22301531

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.ecse489.id22301531.api.RetrofitClient
import com.ecse489.id22301531.databinding.FragmentAddBinding
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream

class AddFragment : Fragment() {

    private var _binding: FragmentAddBinding? = null
    private val binding get() = _binding!!
    private var selectedImageUri: Uri? = null
    private val studentKey = "22301531"

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            selectedImageUri = result.data?.data
            binding.ivPreview.setImageURI(selectedImageUri)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnFetchLocation.setOnClickListener {
            fetchCurrentLocation()
        }

        binding.btnSelectImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickImageLauncher.launch(intent)
        }

        binding.btnSubmit.setOnClickListener {
            submitLandmark()
        }
    }

    private fun fetchCurrentLocation() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1001)
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
                binding.etLat.setText(it.latitude.toString())
                binding.etLon.setText(it.longitude.toString())
            } ?: run {
                Toast.makeText(requireContext(), "Could not get location", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun submitLandmark() {
        val title = binding.etTitle.text.toString()
        val lat = binding.etLat.text.toString()
        val lon = binding.etLon.text.toString()

        if (title.isEmpty() || lat.isEmpty() || lon.isEmpty() || selectedImageUri == null) {
            Toast.makeText(requireContext(), "Please fill all fields and select an image", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                val titleBody = title.toRequestBody("text/plain".toMediaTypeOrNull())
                val latBody = lat.toRequestBody("text/plain".toMediaTypeOrNull())
                val lonBody = lon.toRequestBody("text/plain".toMediaTypeOrNull())

                val file = uriToFile(selectedImageUri!!)
                val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                val imagePart = MultipartBody.Part.createFormData("image", file.name, requestFile)

                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.instance.createLandmark(
                        key = studentKey,
                        title = titleBody,
                        lat = latBody,
                        lon = lonBody,
                        image = imagePart
                    )
                }

                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Landmark created successfully!", Toast.LENGTH_SHORT).show()
                    clearFields()
                } else {
                    Toast.makeText(requireContext(), "Failed: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun clearFields() {
        binding.etTitle.text?.clear()
        binding.etLat.text?.clear()
        binding.etLon.text?.clear()
        binding.ivPreview.setImageResource(android.R.drawable.ic_menu_gallery)
        selectedImageUri = null
    }

    private fun uriToFile(uri: Uri): File {
        val inputStream = requireContext().contentResolver.openInputStream(uri)
        val file = File(requireContext().cacheDir, "upload_image.jpg")
        val outputStream = FileOutputStream(file)
        inputStream?.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }
        return file
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}