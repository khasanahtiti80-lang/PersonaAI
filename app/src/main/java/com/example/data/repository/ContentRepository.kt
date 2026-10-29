package com.example.data.repository

import com.example.data.ai.GeminiAiService
import com.example.data.local.PersonaDatabase
import com.example.data.local.entity.BrandProfileEntity
import com.example.data.local.entity.GeneratedBioEntity
import com.example.data.local.entity.SocialMediaPostEntity
import kotlinx.coroutines.flow.Flow
import org.json.JSONArray
import org.json.JSONObject

class ContentRepository(
    private val database: PersonaDatabase,
    private val aiService: GeminiAiService
) {
    private val contentDao = database.contentDao()

    // Bios & Profiles
    fun observeBios(userId: Long): Flow<List<GeneratedBioEntity>> = contentDao.observeBios(userId)
    fun observeRecentBios(userId: Long): Flow<List<GeneratedBioEntity>> = contentDao.observeRecentBios(userId, 5)

    suspend fun saveBio(bio: GeneratedBioEntity): Long = contentDao.insertBio(bio)
    suspend fun updateBio(bio: GeneratedBioEntity) = contentDao.updateBio(bio)
    suspend fun deleteBio(id: Long, userId: Long) = contentDao.deleteBioById(id, userId)

    suspend fun generateBio(
        userId: Long,
        profile: BrandProfileEntity,
        bioType: String,
        platform: String,
        tone: String,
        length: String,
        customInstruction: String
    ): Result<String> {
        val prompt = """
            You are PersonaAI's professional biographical copywriter.
            Write a high-converting, professional $bioType for the user.
            
            SPECIFICATIONS:
            - Content Type: $bioType
            - Intended Platform: $platform
            - Desired Tone: $tone
            - Length: $length
            - Custom Goal / Directives: ${if (customInstruction.isBlank()) "Standard best practice" else customInstruction}
            
            USER PROFILE FACTS (Strict Grounding - do not fabricate external credentials):
            - Full Name: ${profile.fullName.ifBlank { "The Professional" }}
            - Current Role: ${profile.currentRole}
            - Field / Education: ${profile.education}
            - Core Skills: ${profile.skills}
            - Passion & Interests: ${profile.interests}
            - Experience & Milestones: ${profile.experienceAndAchievements}
            - Personal Values: ${profile.personalValues}
            - Career Goal: ${profile.careerGoals}
            - Communication Style: ${profile.communicationStyle}
            - Existing Branding Statement: ${profile.brandingStatement}
            - Key UVP: ${profile.uniqueValueProposition}
            
            Write the complete, polished, ready-to-use $bioType. Do not include meta-commentary, introductory notes, or placeholders.
        """.trimIndent()

        return aiService.generateText(
            prompt = prompt,
            systemInstruction = "You are a master career copywriter and personal branding ghostwriter for executives and emerging leaders. Output only the finished, polished copy."
        )
    }

    // Social Media Studio
    fun observeSocialPosts(userId: Long): Flow<List<SocialMediaPostEntity>> = contentDao.observeSocialPosts(userId)
    fun observeRecentSocialPosts(userId: Long): Flow<List<SocialMediaPostEntity>> = contentDao.observeRecentSocialPosts(userId, 5)

    suspend fun saveSocialPost(post: SocialMediaPostEntity): Long = contentDao.insertSocialPost(post)
    suspend fun updateSocialPost(post: SocialMediaPostEntity) = contentDao.updateSocialPost(post)
    suspend fun deleteSocialPost(id: Long, userId: Long) = contentDao.deleteSocialPostById(id, userId)

    suspend fun generateSocialContent(
        userId: Long,
        profile: BrandProfileEntity,
        platform: String,
        contentType: String,
        topic: String,
        objective: String,
        tone: String,
        scheduledDay: String
    ): Result<SocialMediaPostEntity> {
        val prompt = """
            You are an elite social media content creator and personal branding strategist.
            Create an authentic, high-engagement personal branding post for $platform.
            
            PARAMETERS:
            - Platform: $platform
            - Content Category: $contentType
            - Topic / Subject: $topic
            - Objective: $objective
            - Tone: $tone
            - Scheduled For: $scheduledDay
            
            USER PROFILE CONTEXT:
            - Name: ${profile.fullName}
            - Role: ${profile.currentRole}
            - Skills: ${profile.skills}
            - Achievements/Experiences: ${profile.experienceAndAchievements}
            - Core UVP: ${profile.uniqueValueProposition}
            
            Return ONLY a valid JSON object matching this schema:
            {
              "title": "A short descriptive title for the post",
              "hook": "An attention-grabbing first line or hook (no clickbait slop, authentic and compelling)",
              "caption": "The main body of the post formatted with clean paragraphs and line breaks",
              "callToAction": "A natural, engaging closing call-to-action question or invitation",
              "hashtags": "3-5 relevant, focused hashtags (e.g. #PersonalBranding #TechCareer)",
              "carouselOrScript": "If carousel or video script is appropriate, provide slide-by-slide outline or script with visual notes, otherwise empty string"
            }
        """.trimIndent()

        val aiResult = aiService.generateText(
            prompt = prompt,
            systemInstruction = "You are a viral yet thoughtful social media ghostwriter for professionals. Output only valid JSON."
        )

        return aiResult.mapCatching { jsonText ->
            val cleanJson = jsonText.trim()
                .removePrefix("```json")
                .removePrefix("```")
                .removeSuffix("```")
                .trim()

            val json = JSONObject(cleanJson)
            SocialMediaPostEntity(
                userId = userId,
                title = json.optString("title", "$contentType on $topic"),
                contentType = contentType,
                platform = platform,
                topic = topic,
                hook = json.optString("hook", ""),
                caption = json.optString("caption", ""),
                callToAction = json.optString("callToAction", ""),
                hashtags = json.optString("hashtags", ""),
                carouselOrScript = json.optString("carouselOrScript", ""),
                scheduledDay = scheduledDay,
                status = "Ready"
            )
        }
    }

    suspend fun generateWeeklyCalendar(
        userId: Long,
        profile: BrandProfileEntity,
        platform: String,
        theme: String
    ): Result<List<SocialMediaPostEntity>> {
        val prompt = """
            Create a complete 5-day personal branding content calendar (Monday through Friday) for $platform around the weekly theme: '$theme'.
            
            User context: ${profile.fullName}, ${profile.currentRole}, Skills: ${profile.skills}.
            
            Output a JSON array of 5 post objects:
            [
              {
                "scheduledDay": "Monday",
                "title": "Title",
                "contentType": "Educational",
                "hook": "Hook line",
                "caption": "Full post text",
                "callToAction": "CTA",
                "hashtags": "#Tag1 #Tag2"
              },
              ...
            ]
            Output ONLY valid JSON array.
        """.trimIndent()

        val aiResult = aiService.generateText(prompt)
        return aiResult.mapCatching { text ->
            val cleanJson = text.trim()
                .removePrefix("```json")
                .removePrefix("```")
                .removeSuffix("```")
                .trim()
            val array = JSONArray(cleanJson)
            val posts = mutableListOf<SocialMediaPostEntity>()
            for (i in 0 until array.length()) {
                val item = array.getJSONObject(i)
                posts.add(
                    SocialMediaPostEntity(
                        userId = userId,
                        title = item.optString("title", "Post ${i + 1}"),
                        contentType = item.optString("contentType", "Industry Insights"),
                        platform = platform,
                        topic = theme,
                        hook = item.optString("hook", ""),
                        caption = item.optString("caption", ""),
                        callToAction = item.optString("callToAction", ""),
                        hashtags = item.optString("hashtags", ""),
                        scheduledDay = item.optString("scheduledDay", "Day ${i + 1}"),
                        status = "Scheduled"
                    )
                )
            }
            contentDao.insertSocialPosts(posts)
            posts
        }
    }

    suspend fun clearAllContent(userId: Long) {
        contentDao.deleteAllBiosForUser(userId)
        contentDao.deleteAllSocialPostsForUser(userId)
    }
}
