package com.ecse489.id22301531

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.ecse489.id22301531.databinding.FragmentActivityBinding
import com.ecse489.id22301531.db.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ActivityFragment : Fragment() {

    private var _binding: FragmentActivityBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: ActivityAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentActivityBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ActivityAdapter(emptyList())
        binding.rvActivity.layoutManager = LinearLayoutManager(requireContext())
        binding.rvActivity.adapter = adapter

        loadVisitHistory()
    }

    private fun loadVisitHistory() {
        lifecycleScope.launch {
            val visits = withContext(Dispatchers.IO) {
                AppDatabase.getDatabase(requireContext()).visitDao().getAllVisits()
            }
            adapter.updateData(visits)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}