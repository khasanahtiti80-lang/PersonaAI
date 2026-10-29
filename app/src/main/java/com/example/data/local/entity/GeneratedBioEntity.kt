package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bios")
data class GeneratedBioEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val title: String,
    val bioType: String, // Professional Bio, Short Bio, Long Intro, LinkedIn About, Instagram Bio, Portfolio Intro, CV Summary, Tagline, Elevator Pitch
    val platform: String,
    val tone: String,
    val length: String,
    val content: String,
    val createdAt: Long = System.currentTimeMillis()
)
