package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "brand_evaluations")
data class BrandEvaluationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val overallScore: Int,
    val clarityScore: Int,
    val consistencyScore: Int,
    val uvpScore: Int,
    val relevanceScore: Int,
    val communicationScore: Int,
    val evidenceScore: Int,
    val alignmentScore: Int,
    val summaryFeedback: String,
    val strengths: String,
    val areasForImprovement: String,
    val prioritizedRecommendations: String,
    val sevenDayPlanJson: String, // JSON list of Day 1 to Day 7 steps
    val submittedPortfolioOrBio: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
