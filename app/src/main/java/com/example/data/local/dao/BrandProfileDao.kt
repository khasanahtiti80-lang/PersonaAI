package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.BrandProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BrandProfileDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: BrandProfileEntity)

    @Update
    suspend fun updateProfile(profile: BrandProfileEntity)

    @Query("SELECT * FROM brand_profiles WHERE userId = :userId LIMIT 1")
    suspend fun getProfileByUserId(userId: Long): BrandProfileEntity?

    @Query("SELECT * FROM brand_profiles WHERE userId = :userId LIMIT 1")
    fun observeProfileByUserId(userId: Long): Flow<BrandProfileEntity?>

    @Query("DELETE FROM brand_profiles WHERE userId = :userId")
    suspend fun deleteProfileByUserId(userId: Long)
}
