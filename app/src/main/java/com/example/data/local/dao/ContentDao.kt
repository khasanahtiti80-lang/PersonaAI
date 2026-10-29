package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.GeneratedBioEntity
import com.example.data.local.entity.SocialMediaPostEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ContentDao {
    // Bio Operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBio(bio: GeneratedBioEntity): Long

    @Update
    suspend fun updateBio(bio: GeneratedBioEntity)

    @Delete
    suspend fun deleteBio(bio: GeneratedBioEntity)

    @Query("DELETE FROM bios WHERE id = :id AND userId = :userId")
    suspend fun deleteBioById(id: Long, userId: Long)

    @Query("SELECT * FROM bios WHERE userId = :userId ORDER BY createdAt DESC")
    fun observeBios(userId: Long): Flow<List<GeneratedBioEntity>>

    @Query("SELECT * FROM bios WHERE userId = :userId ORDER BY createdAt DESC LIMIT :limit")
    fun observeRecentBios(userId: Long, limit: Int = 5): Flow<List<GeneratedBioEntity>>

    @Query("SELECT * FROM bios WHERE id = :id AND userId = :userId LIMIT 1")
    suspend fun getBioById(id: Long, userId: Long): GeneratedBioEntity?

    // Social Media Operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSocialPost(post: SocialMediaPostEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSocialPosts(posts: List<SocialMediaPostEntity>)

    @Update
    suspend fun updateSocialPost(post: SocialMediaPostEntity)

    @Delete
    suspend fun deleteSocialPost(post: SocialMediaPostEntity)

    @Query("DELETE FROM social_posts WHERE id = :id AND userId = :userId")
    suspend fun deleteSocialPostById(id: Long, userId: Long)

    @Query("SELECT * FROM social_posts WHERE userId = :userId ORDER BY createdAt DESC")
    fun observeSocialPosts(userId: Long): Flow<List<SocialMediaPostEntity>>

    @Query("SELECT * FROM social_posts WHERE userId = :userId ORDER BY createdAt DESC LIMIT :limit")
    fun observeRecentSocialPosts(userId: Long, limit: Int = 5): Flow<List<SocialMediaPostEntity>>

    @Query("SELECT * FROM social_posts WHERE id = :id AND userId = :userId LIMIT 1")
    suspend fun getSocialPostById(id: Long, userId: Long): SocialMediaPostEntity?

    @Query("DELETE FROM bios WHERE userId = :userId")
    suspend fun deleteAllBiosForUser(userId: Long)

    @Query("DELETE FROM social_posts WHERE userId = :userId")
    suspend fun deleteAllSocialPostsForUser(userId: Long)
}
