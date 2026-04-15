package com.ecse489.id22301531.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.ecse489.id22301531.model.Visit

@Dao
interface VisitDao {
    @Query("SELECT * FROM visits ORDER BY visitTime DESC")
    suspend fun getAllVisits(): List<Visit>

    @Insert
    suspend fun insertVisit(visit: Visit)

    @Query("SELECT * FROM visits WHERE isSynced = 0")
    suspend fun getUnsyncedVisits(): List<Visit>

    @Query("UPDATE visits SET isSynced = 1 WHERE id = :id")
    suspend fun markAsSynced(id: Int)
}