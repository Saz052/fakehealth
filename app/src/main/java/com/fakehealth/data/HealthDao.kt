package com.fakehealth.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface HealthDao {

    @Query("SELECT * FROM health_records WHERE date = :date")
    suspend fun getRecordForDate(date: Long): HealthRecord?

    @Query("SELECT * FROM health_records WHERE date = :date")
    fun observeRecordForDate(date: Long): Flow<HealthRecord?>

    @Query("SELECT * FROM health_records ORDER BY date DESC LIMIT 7")
    suspend fun getLast7Days(): List<HealthRecord>

    @Query("SELECT * FROM health_records ORDER BY date DESC LIMIT 30")
    suspend fun getLast30Days(): List<HealthRecord>

    @Query("SELECT * FROM health_records WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    suspend fun getRecordsInRange(startDate: Long, endDate: Long): List<HealthRecord>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: HealthRecord): Long

    @Update
    suspend fun updateRecord(record: HealthRecord): Int

    @Query("DELETE FROM health_records")
    suspend fun clearAll(): Int
}