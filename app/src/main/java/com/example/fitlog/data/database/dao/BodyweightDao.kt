package com.example.fitlog.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.fitlog.data.database.entity.BodyweightEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BodyweightDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(bodyweight: BodyweightEntity): Long

    @Update
    suspend fun update(bodyweight: BodyweightEntity)

    @Delete
    suspend fun delete(bodyweight: BodyweightEntity)

    @Query("SELECT * FROM bodyweight WHERE id = :id")
    suspend fun getById(id: Int): BodyweightEntity?

    @Query("SELECT * FROM bodyweight ORDER BY date DESC")
    fun getAllEntries(): Flow<List<BodyweightEntity>>

    @Query("SELECT * FROM bodyweight ORDER BY date DESC LIMIT 1")
    suspend fun getLatestEntry(): BodyweightEntity?

    @Query("SELECT * FROM bodyweight ORDER BY date DESC LIMIT 1")
    fun getLatestEntryFlow(): Flow<BodyweightEntity?>

    @Query("SELECT * FROM bodyweight WHERE date >= :startDate AND date <= :endDate ORDER BY date ASC")
    fun getEntriesInDateRange(startDate: Long, endDate: Long): Flow<List<BodyweightEntity>>

    @Query("SELECT * FROM bodyweight WHERE date = :date LIMIT 1")
    suspend fun getEntryForDate(date: Long): BodyweightEntity?

    @Query("SELECT AVG(weight) FROM bodyweight WHERE date >= :startDate AND date <= :endDate")
    suspend fun getAverageWeightInRange(startDate: Long, endDate: Long): Float?

    @Query("SELECT MIN(weight) FROM bodyweight")
    suspend fun getMinWeight(): Float?

    @Query("SELECT MAX(weight) FROM bodyweight")
    suspend fun getMaxWeight(): Float?

    @Query("SELECT COUNT(*) FROM bodyweight")
    suspend fun getEntryCount(): Int
}

