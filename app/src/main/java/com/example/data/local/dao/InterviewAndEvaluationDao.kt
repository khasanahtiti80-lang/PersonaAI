package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.BrandEvaluationEntity
import com.example.data.local.entity.InterviewSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface InterviewDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: InterviewSessionEntity): Long

    @Update
    suspend fun updateSession(session: InterviewSessionEntity)

    @Delete
    suspend fun deleteSession(session: InterviewSessionEntity)

    @Query("DELETE FROM interview_sessions WHERE id = :id AND userId = :userId")
    suspend fun deleteSessionById(id: Long, userId: Long)

    @Query("SELECT * FROM interview_sessions WHERE userId = :userId ORDER BY createdAt DESC")
    fun observeSessions(userId: Long): Flow<List<InterviewSessionEntity>>

    @Query("SELECT * FROM interview_sessions WHERE userId = :userId ORDER BY createdAt DESC LIMIT 1")
    fun observeLatestSession(userId: Long): Flow<InterviewSessionEntity?>

    @Query("SELECT * FROM interview_sessions WHERE id = :id AND userId = :userId LIMIT 1")
    suspend fun getSessionById(id: Long, userId: Long): InterviewSessionEntity?

    @Query("DELETE FROM interview_sessions WHERE userId = :userId")
    suspend fun deleteAllSessionsForUser(userId: Long)
}

@Dao
interface EvaluationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvaluation(evaluation: BrandEvaluationEntity): Long

    @Query("SELECT * FROM brand_evaluations WHERE userId = :userId ORDER BY createdAt DESC")
    fun observeEvaluations(userId: Long): Flow<List<BrandEvaluationEntity>>

    @Query("SELECT * FROM brand_evaluations WHERE userId = :userId ORDER BY createdAt DESC LIMIT 1")
    fun observeLatestEvaluation(userId: Long): Flow<BrandEvaluationEntity?>

    @Query("SELECT * FROM brand_evaluations WHERE id = :id AND userId = :userId LIMIT 1")
    suspend fun getEvaluationById(id: Long, userId: Long): BrandEvaluationEntity?

    @Query("DELETE FROM brand_evaluations WHERE id = :id AND userId = :userId")
    suspend fun deleteEvaluationById(id: Long, userId: Long)

    @Query("DELETE FROM brand_evaluations WHERE userId = :userId")
    suspend fun deleteAllEvaluationsForUser(userId: Long)
}
