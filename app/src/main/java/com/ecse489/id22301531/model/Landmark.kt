package com.ecse489.id22301531.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "landmarks")
data class Landmark(
    @PrimaryKey
    val id: Int,
    val title: String,
    val lat: Double,
    val lon: Double,
    val image: String,
    @SerializedName("visit_count")
    val visitCount: Int,
    @SerializedName("avg_distance")
    val avgDistance: Double,
    val score: Double,
    @SerializedName("is_active")
    val isActive: Int = 1
)