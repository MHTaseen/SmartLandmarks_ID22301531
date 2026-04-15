package com.ecse489.id22301531

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ecse489.id22301531.model.Visit
import java.text.SimpleDateFormat
import java.util.*

class ActivityAdapter(private var visits: List<Visit>) :
    RecyclerView.Adapter<ActivityAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tvLandmarkName)
        val time: TextView = view.findViewById(R.id.tvVisitTime)
        val distance: TextView = view.findViewById(R.id.tvDistance)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_activity, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val visit = visits[position]
        holder.name.text = visit.landmarkName
        
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        holder.time.text = sdf.format(Date(visit.visitTime))
        
        holder.distance.text = if (visit.distance >= 0) {
            "Distance: ${String.format("%.2f", visit.distance)}m"
        } else {
            "Distance: Pending (Offline)"
        }
    }

    override fun getItemCount() = visits.size

    fun updateData(newList: List<Visit>) {
        visits = newList
        notifyDataSetChanged()
    }
}