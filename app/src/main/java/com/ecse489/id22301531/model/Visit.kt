package com.ecse489.id22301531.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "visits")
data class Visit(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val landmarkId: Int,
    val landmarkName: String,
    val visitTime: Long,
    val distance: Double,
    val isSynced: Boolean = true
)