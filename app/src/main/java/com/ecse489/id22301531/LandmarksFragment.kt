package com.ecse489.id22301531

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.ecse489.id22301531.api.RetrofitClient
import com.ecse489.id22301531.databinding.FragmentLandmarksBinding
import com.ecse489.id22301531.db.AppDatabase
import com.ecse489.id22301531.model.Landmark
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LandmarksFragment : Fragment() {

    private var _binding: FragmentLandmarksBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: LandmarkAdapter
    private var allLandmarks: List<Landmark> = emptyList()
    private val studentKey = "22301531"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLandmarksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = LandmarkAdapter(emptyList())
        binding.rvLandmarks.layoutManager = LinearLayoutManager(requireContext())
        binding.rvLandmarks.adapter = adapter

        fetchLandmarks()

        binding.btnFilter.setOnClickListener {
            val minScore = binding.etMinScore.text.toString().toDoubleOrNull() ?: 0.0
            val filtered = allLandmarks.filter { it.score >= minScore }
            adapter.updateData(filtered)
        }

        binding.btnSort.setOnClickListener {
            val sorted = allLandmarks.sortedByDescending { it.score }
            adapter.updateData(sorted)
        }
    }

    private fun fetchLandmarks() {
        lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.instance.getLandmarks(key = studentKey)
                }
                if (response.isSuccessful && response.body() != null) {
                    allLandmarks = response.body()!!
                    adapter.updateData(allLandmarks)
                    
                    // Cache to local DB
                    withContext(Dispatchers.IO) {
                        val db = AppDatabase.getDatabase(requireContext())
                        db.landmarkDao().deleteAll()
                        db.landmarkDao().insertAll(allLandmarks)
                    }
                } else {
                    loadFromCache()
                }
            } catch (e: Exception) {
                loadFromCache()
                Toast.makeText(requireContext(), "Offline: Showing cached data", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private suspend fun loadFromCache() {
        val cached = withContext(Dispatchers.IO) {
            AppDatabase.getDatabase(requireContext()).landmarkDao().getAllLandmarks()
        }
        allLandmarks = cached
        adapter.updateData(allLandmarks)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}