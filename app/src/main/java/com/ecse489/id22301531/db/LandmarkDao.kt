package com.ecse489.id22301531.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ecse489.id22301531.model.Landmark

@Dao
interface LandmarkDao {
    @Query("SELECT * FROM landmarks WHERE isActive = 1")
    suspend fun getAllLandmarks(): List<Landmark>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(landmarks: List<Landmark>)

    @Query("DELETE FROM landmarks")
    suspend fun deleteAll()
}