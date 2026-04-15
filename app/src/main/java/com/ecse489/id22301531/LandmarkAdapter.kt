package com.ecse489.id22301531

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ecse489.id22301531.model.Landmark

// The Adapter takes a list of Landmarks as a parameter
class LandmarkAdapter(private var landmarks: List<Landmark>) :
    RecyclerView.Adapter<LandmarkAdapter.ViewHolder>() {

    // 1. ViewHolder holds the references to the UI views in item_landmark.xml
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.tvTitle)
        val score: TextView = view.findViewById(R.id.tvScore)
        val image: ImageView = view.findViewById(R.id.ivLandmark)
    }

    // 2. This creates the "Row" layout in the memory
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_landmark, parent, false)
        return ViewHolder(view)
    }

    // 3. This "Pumps" the actual data into the specific Row
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val landmark = landmarks[position]

        holder.title.text = landmark.title
        holder.score.text = "Score: ${landmark.score}"

        // Image URL: Base URL + path from API
        val imageUrl = "https://labs.anontech.info/cse489/exm3/${landmark.image}"

        // Glide is a library that handles downloading and showing the image
        Glide.with(holder.itemView.context)
            .load(imageUrl)
            .placeholder(android.R.drawable.ic_menu_gallery) // shows while loading
            .into(holder.image)
    }

    override fun getItemCount() = landmarks.size

    // 4. Helper function to refresh the list when data arrives from the API
    fun updateData(newList: List<Landmark>) {
        landmarks = newList
        notifyDataSetChanged()
    }
}