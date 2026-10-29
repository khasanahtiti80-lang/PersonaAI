package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "social_posts")
data class SocialMediaPostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val title: String,
    val contentType: String, // Educational, Tips, Story, Showcase, Achievement, Insight, Video Script
    val platform: String,
    val topic: String,
    val hook: String,
    val caption: String,
    val callToAction: String,
    val hashtags: String,
    val carouselOrScript: String = "",
    val scheduledDay: String = "Monday", // For calendar view
    val status: String = "Draft", // Draft, Ready, Scheduled
    val createdAt: Long = System.currentTimeMillis()
)
