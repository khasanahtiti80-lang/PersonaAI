package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "brand_profiles")
data class BrandProfileEntity(
    @PrimaryKey
    val userId: Long,
    val fullName: String = "",
    val education: String = "",
    val currentRole: String = "",
    val skills: String = "",
    val interests: String = "",
    val experienceAndAchievements: String = "",
    val personalValues: String = "",
    val careerGoals: String = "",
    val targetAudience: String = "",
    val communicationStyle: String = "Confident & Approachable",
    val preferredPlatform: String = "LinkedIn",
    val completionPercentage: Int = 0,

    // Generated Brand Identity insights
    val coreStrengths: String = "",
    val professionalIdentity: String = "",
    val uniqueValueProposition: String = "",
    val brandingStatement: String = "",
    val keyDifferentiators: String = "",
    val targetAudienceDesc: String = "",
    val developmentRecommendations: String = "",
    val updatedAt: Long = System.currentTimeMillis()
)
