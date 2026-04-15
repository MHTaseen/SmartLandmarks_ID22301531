package com.ecse489.id22301531

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.ecse489.id22301531.api.RetrofitClient
import com.ecse489.id22301531.api.VisitRequest
import com.ecse489.id22301531.databinding.FragmentMapBinding
import com.ecse489.id22301531.db.AppDatabase
import com.ecse489.id22301531.model.Landmark
import com.ecse489.id22301531.model.Visit
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MapFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!
    private var googleMap: GoogleMap? = null
    private val studentKey = "22301531"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        
        // Center on Bangladesh
        val bangladesh = LatLng(23.6850, 90.3563)
        googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(bangladesh, 6f))

        loadLandmarks()

        googleMap?.setOnMarkerClickListener { marker ->
            val landmark = marker.tag as? Landmark
            landmark?.let {
                showVisitDialog(it)
            }
            true
        }
    }

    private fun loadLandmarks() {
        lifecycleScope.launch {
            val landmarks = withContext(Dispatchers.IO) {
                AppDatabase.getDatabase(requireContext()).landmarkDao().getAllLandmarks()
            }
            landmarks.forEach { landmark ->
                val markerOptions = MarkerOptions()
                    .position(LatLng(landmark.lat, landmark.lon))
                    .title(landmark.title)
                    .snippet("Score: ${landmark.score}")
                    .icon(BitmapDescriptorFactory.defaultMarker(getMarkerColor(landmark.score)))
                
                val marker = googleMap?.addMarker(markerOptions)
                marker?.tag = landmark
            }
        }
    }

    private fun getMarkerColor(score: Double): Float {
        return when {
            score < 0 -> BitmapDescriptorFactory.HUE_RED
            score < 100 -> BitmapDescriptorFactory.HUE_ORANGE
            score < 500 -> BitmapDescriptorFactory.HUE_YELLOW
            else -> BitmapDescriptorFactory.HUE_GREEN
        }
    }

    private fun showVisitDialog(landmark: Landmark) {
        // In a real app, use a proper Dialog. For now, we'll just try to visit on click.
        Toast.makeText(requireContext(), "Visiting ${landmark.title}...", Toast.LENGTH_SHORT).show()
        visitLandmark(landmark)
    }

    private fun visitLandmark(landmark: Landmark) {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1001)
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                sendVisitRequest(landmark, it.latitude, it.longitude)
            } ?: run {
                Toast.makeText(requireContext(), "Could not get current location", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sendVisitRequest(landmark: Landmark, lat: Double, lon: Double) {
        lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.instance.visitLandmark(
                        key = studentKey,
                        body = VisitRequest(landmark.id, lat, lon)
                    )
                }

                if (response.isSuccessful && response.body() != null) {
                    val visitRes = response.body()!!
                    Toast.makeText(requireContext(), "${visitRes.message}. Distance: ${visitRes.distance}m", Toast.LENGTH_LONG).show()
                    
                    // Save to local history
                    withContext(Dispatchers.IO) {
                        AppDatabase.getDatabase(requireContext()).visitDao().insertVisit(
                            Visit(
                                landmarkId = landmark.id,
                                landmarkName = landmark.title,
                                visitTime = System.currentTimeMillis(),
                                distance = visitRes.distance ?: 0.0,
                                isSynced = true
                            )
                        )
                    }
                } else {
                    queueOfflineVisit(landmark, lat, lon)
                }
            } catch (e: Exception) {
                queueOfflineVisit(landmark, lat, lon)
            }
        }
    }

    private suspend fun queueOfflineVisit(landmark: Landmark, lat: Double, lon: Double) {
        withContext(Dispatchers.IO) {
            AppDatabase.getDatabase(requireContext()).visitDao().insertVisit(
                Visit(
                    landmarkId = landmark.id,
                    landmarkName = landmark.title,
                    visitTime = System.currentTimeMillis(),
                    distance = -1.0, // Unknown yet
                    isSynced = false
                )
            )
        }
        withContext(Dispatchers.Main) {
            Toast.makeText(requireContext(), "Offline: Visit queued", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}