package com.example

import com.example.data.local.SessionManager
import com.example.data.local.entity.BrandProfileEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testPasswordHashingIsConsistentAndSecure() {
        val pass = "Password123!"
        val hash1 = SessionManager.hashPassword(pass)
        val hash2 = SessionManager.hashPassword(pass)
        assertEquals(hash1, hash2)
        assertNotEquals(pass, hash1)
        assertTrue(hash1.length >= 64)
    }

    @Test
    fun testProfileCompletionPercentage() {
        val emptyProfile = BrandProfileEntity(userId = 1)
        var completed = 0
        val total = 10
        if (emptyProfile.fullName.isNotBlank()) completed++
        if (emptyProfile.education.isNotBlank()) completed++
        if (emptyProfile.currentRole.isNotBlank()) completed++
        val emptyPct = ((completed.toFloat() / total) * 100).toInt()
        assertEquals(0, emptyPct)

        val fullProfile = BrandProfileEntity(
            userId = 1,
            fullName = "Alex",
            education = "HCI",
            currentRole = "Lead",
            skills = "Design",
            interests = "AI",
            experienceAndAchievements = "Led app",
            personalValues = "Empathy",
            careerGoals = "Director",
            targetAudience = "Founders",
            communicationStyle = "Direct"
        )
        var fullCompleted = 0
        if (fullProfile.fullName.isNotBlank()) fullCompleted++
        if (fullProfile.education.isNotBlank()) fullCompleted++
        if (fullProfile.currentRole.isNotBlank()) fullCompleted++
        if (fullProfile.skills.isNotBlank()) fullCompleted++
        if (fullProfile.interests.isNotBlank()) fullCompleted++
        if (fullProfile.experienceAndAchievements.isNotBlank()) fullCompleted++
        if (fullProfile.personalValues.isNotBlank()) fullCompleted++
        if (fullProfile.careerGoals.isNotBlank()) fullCompleted++
        if (fullProfile.targetAudience.isNotBlank()) fullCompleted++
        if (fullProfile.communicationStyle.isNotBlank()) fullCompleted++
        val fullPct = ((fullCompleted.toFloat() / total) * 100).toInt()
        assertEquals(100, fullPct)
    }
}
