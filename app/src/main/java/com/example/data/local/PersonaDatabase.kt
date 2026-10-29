package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.BrandProfileDao
import com.example.data.local.dao.ContentDao
import com.example.data.local.dao.EvaluationDao
import com.example.data.local.dao.InterviewDao
import com.example.data.local.dao.UserDao
import com.example.data.local.entity.BrandEvaluationEntity
import com.example.data.local.entity.BrandProfileEntity
import com.example.data.local.entity.GeneratedBioEntity
import com.example.data.local.entity.InterviewSessionEntity
import com.example.data.local.entity.SocialMediaPostEntity
import com.example.data.local.entity.UserEntity

@Database(
    entities = [
        UserEntity::class,
        BrandProfileEntity::class,
        GeneratedBioEntity::class,
        SocialMediaPostEntity::class,
        InterviewSessionEntity::class,
        BrandEvaluationEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class PersonaDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun brandProfileDao(): BrandProfileDao
    abstract fun contentDao(): ContentDao
    abstract fun interviewDao(): InterviewDao
    abstract fun evaluationDao(): EvaluationDao

    companion object {
        @Volatile
        private var INSTANCE: PersonaDatabase? = null

        fun getInstance(context: Context): PersonaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PersonaDatabase::class.java,
                    "persona_ai_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
