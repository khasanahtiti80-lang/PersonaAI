package com.example.data.repository

import com.example.data.ai.GeminiAiService
import com.example.data.local.PersonaDatabase
import com.example.data.local.entity.BrandProfileEntity
import kotlinx.coroutines.flow.Flow
import org.json.JSONObject

class BrandProfileRepository(
    private val database: PersonaDatabase,
    private val aiService: GeminiAiService
) {
    private val profileDao = database.brandProfileDao()

    fun observeProfile(userId: Long): Flow<BrandProfileEntity?> {
        return profileDao.observeProfileByUserId(userId)
    }

    suspend fun getProfile(userId: Long): BrandProfileEntity {
        return profileDao.getProfileByUserId(userId) ?: BrandProfileEntity(userId = userId)
    }

    suspend fun saveProfile(profile: BrandProfileEntity) {
        val percentage = calculateCompletionPercentage(profile)
        val updated = profile.copy(
            completionPercentage = percentage,
            updatedAt = System.currentTimeMillis()
        )
        profileDao.insertOrUpdateProfile(updated)
    }

    fun calculateCompletionPercentage(profile: BrandProfileEntity): Int {
        var completed = 0
        val total = 10
        if (profile.fullName.isNotBlank()) completed++
        if (profile.education.isNotBlank()) completed++
        if (profile.currentRole.isNotBlank()) completed++
        if (profile.skills.isNotBlank()) completed++
        if (profile.interests.isNotBlank()) completed++
        if (profile.experienceAndAchievements.isNotBlank()) completed++
        if (profile.personalValues.isNotBlank()) completed++
        if (profile.careerGoals.isNotBlank()) completed++
        if (profile.targetAudience.isNotBlank()) completed++
        if (profile.communicationStyle.isNotBlank()) completed++
        return ((completed.toFloat() / total) * 100).toInt().coerceIn(0, 100)
    }

    suspend fun generateBrandIdentity(profile: BrandProfileEntity): Result<BrandProfileEntity> {
        val prompt = """
            You are a master personal branding strategist for PersonaAI.
            Analyze the following user profile and generate their distinctive personal brand architecture.
            CRITICAL RULE: Rely ONLY on the facts provided below. Do NOT fabricate awards, companies, degrees, or years of experience.
            
            USER PROFILE:
            - Full Name: ${profile.fullName}
            - Education/Field: ${profile.education}
            - Current Role/Status: ${profile.currentRole}
            - Skills & Competencies: ${profile.skills}
            - Interests & Passions: ${profile.interests}
            - Experience & Key Achievements: ${profile.experienceAndAchievements}
            - Personal Core Values: ${profile.personalValues}
            - Career Goals: ${profile.careerGoals}
            - Target Audience: ${profile.targetAudience}
            - Preferred Communication Style: ${profile.communicationStyle}
            - Preferred Platform: ${profile.preferredPlatform}
            
            Respond in valid JSON format with exactly these keys:
            {
              "coreStrengths": "Summary of 3-4 validated core strengths",
              "professionalIdentity": "A compelling 1-sentence professional identity title (e.g., 'Data-Driven Product Designer for EdTech Solutions')",
              "uniqueValueProposition": "A high-impact 2-3 sentence UVP describing how the user solves problems and drives value for their audience",
              "brandingStatement": "A memorable personal branding statement suitable for a LinkedIn headline or header banner",
              "keyDifferentiators": "Bullet points (newline-separated) of 3-5 distinct differentiators that set the user apart",
              "targetAudienceDesc": "Analysis of who their audience is and what they care about most",
              "developmentRecommendations": "3 actionable recommendations for further elevating their personal brand"
            }
            Output ONLY valid JSON.
        """.trimIndent()

        val aiResult = aiService.generateText(
            prompt = prompt,
            systemInstruction = "You are an expert executive personal branding coach. You generate concise, empowering, professional branding statements based strictly on user-provided details in valid JSON format."
        )

        return aiResult.mapCatching { jsonText ->
            val cleanJson = jsonText.trim()
                .removePrefix("```json")
                .removePrefix("```")
                .removeSuffix("```")
                .trim()

            val json = JSONObject(cleanJson)
            val updatedProfile = profile.copy(
                coreStrengths = json.optString("coreStrengths", ""),
                professionalIdentity = json.optString("professionalIdentity", ""),
                uniqueValueProposition = json.optString("uniqueValueProposition", ""),
                brandingStatement = json.optString("brandingStatement", ""),
                keyDifferentiators = json.optString("keyDifferentiators", ""),
                targetAudienceDesc = json.optString("targetAudienceDesc", ""),
                developmentRecommendations = json.optString("developmentRecommendations", ""),
                updatedAt = System.currentTimeMillis()
            )
            saveProfile(updatedProfile)
            updatedProfile
        }
    }
}
