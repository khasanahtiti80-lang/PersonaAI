package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "interview_sessions")
data class InterviewSessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val interviewType: String, // Internship, First Job, Scholarship, Organizational, General
    val targetRole: String,
    val difficulty: String, // Entry, Intermediate, Advanced
    val focus: String, // Communication, Competencies, Experience, Behavioral
    val language: String = "English",
    val questionsAndAnswersJson: String, // Stored as JSON list of Q&A + feedback
    val overallScore: Int = 0,
    val overallFeedback: String = "",
    val strengths: String = "",
    val improvementPlan: String = "",
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
