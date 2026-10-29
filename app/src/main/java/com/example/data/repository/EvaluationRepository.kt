package com.example.data.repository

import com.example.data.ai.GeminiAiService
import com.example.data.local.PersonaDatabase
import com.example.data.local.entity.BrandEvaluationEntity
import com.example.data.local.entity.BrandProfileEntity
import kotlinx.coroutines.flow.Flow
import org.json.JSONObject

data class SelfAssessmentAnswers(
    val onlinePresenceScore: Int = 3, // 1 to 5
    val messageConsistencyScore: Int = 3,
    val networkEngagementScore: Int = 3,
    val portfolioClarityScore: Int = 3
)

class EvaluationRepository(
    private val database: PersonaDatabase,
    private val aiService: GeminiAiService
) {
    private val evaluationDao = database.evaluationDao()

    fun observeEvaluations(userId: Long): Flow<List<BrandEvaluationEntity>> =
        evaluationDao.observeEvaluations(userId)

    fun observeLatestEvaluation(userId: Long): Flow<BrandEvaluationEntity?> =
        evaluationDao.observeLatestEvaluation(userId)

    suspend fun getEvaluation(id: Long, userId: Long): BrandEvaluationEntity? =
        evaluationDao.getEvaluationById(id, userId)

    suspend fun evaluateBrand(
        userId: Long,
        profile: BrandProfileEntity,
        selfAssessment: SelfAssessmentAnswers,
        optionalText: String
    ): Result<BrandEvaluationEntity> {
        val prompt = """
            You are an objective, rigorous Personal Branding Auditor and Career Strategist.
            Evaluate the user's personal brand across the 7 core dimensions.
            
            USER PROFILE DATA:
            - Name: ${profile.fullName}
            - Current Role: ${profile.currentRole}
            - Education: ${profile.education}
            - Skills: ${profile.skills}
            - Interests: ${profile.interests}
            - Experience/Achievements: ${profile.experienceAndAchievements}
            - Career Goals: ${profile.careerGoals}
            - Target Audience: ${profile.targetAudience}
            - UVP: ${profile.uniqueValueProposition}
            - Personal Branding Statement: ${profile.brandingStatement}
            
            SELF-ASSESSMENT RATINGS (1-5 scale):
            - Perceived Online Presence: ${selfAssessment.onlinePresenceScore}/5
            - Message Consistency: ${selfAssessment.messageConsistencyScore}/5
            - Network Engagement: ${selfAssessment.networkEngagementScore}/5
            - Portfolio/Proof Clarity: ${selfAssessment.portfolioClarityScore}/5
            
            SUPPLEMENTAL SUBMITTED TEXT (Bio, Portfolio snippet, or Profile About):
            "${if (optionalText.isBlank()) "None submitted" else optionalText}"
            
            EVALUATION INSTRUCTIONS:
            Score each of the 7 dimensions strictly from 0 to 100 based on clarity and substance:
            1. Clarity of professional identity (clarityScore)
            2. Consistency of personal messaging (consistencyScore)
            3. Strength of unique value proposition (uvpScore)
            4. Relevance to target audience (relevanceScore)
            5. Quality of professional communication (communicationScore)
            6. Evidence of skills and achievements (evidenceScore)
            7. Alignment with career goals (alignmentScore)
            
            Calculate the overallScore (weighted average 0-100). Do NOT hand out 95+ automatically unless backed by strong evidence.
            
            Provide:
            - summaryFeedback: 2-3 sentence high-level executive summary
            - strengths: 3-4 bullet points of distinct advantages identified
            - areasForImprovement: 3-4 specific gaps or vulnerabilities
            - prioritizedRecommendations: Top 3 immediate actions
            - sevenDayPlan: 7 concrete daily tasks (Day 1 through Day 7) to elevate their brand
            
            Return ONLY a valid JSON object matching:
            {
              "overallScore": 78,
              "clarityScore": 80,
              "consistencyScore": 75,
              "uvpScore": 70,
              "relevanceScore": 82,
              "communicationScore": 85,
              "evidenceScore": 72,
              "alignmentScore": 84,
              "summaryFeedback": "...",
              "strengths": "...",
              "areasForImprovement": "...",
              "prioritizedRecommendations": "...",
              "sevenDayPlanJson": "[\"Day 1: Audit LinkedIn profile headline and banner...\", \"Day 2: ...\", \"Day 3: ...\", \"Day 4: ...\", \"Day 5: ...\", \"Day 6: ...\", \"Day 7: ...\"]"
            }
        """.trimIndent()

        val aiResult = aiService.generateText(
            prompt = prompt,
            systemInstruction = "You are a senior brand consultant for professionals. You provide thorough, objective, non-generic evaluations in valid JSON format."
        )

        return aiResult.mapCatching { jsonText ->
            val cleanJson = jsonText.trim()
                .removePrefix("```json")
                .removePrefix("```")
                .removeSuffix("```")
                .trim()

            val json = JSONObject(cleanJson)
            val entity = BrandEvaluationEntity(
                userId = userId,
                overallScore = json.optInt("overallScore", 75),
                clarityScore = json.optInt("clarityScore", 75),
                consistencyScore = json.optInt("consistencyScore", 75),
                uvpScore = json.optInt("uvpScore", 75),
                relevanceScore = json.optInt("relevanceScore", 75),
                communicationScore = json.optInt("communicationScore", 75),
                evidenceScore = json.optInt("evidenceScore", 75),
                alignmentScore = json.optInt("alignmentScore", 75),
                summaryFeedback = json.optString("summaryFeedback", "Comprehensive self-evaluation completed."),
                strengths = json.optString("strengths", "Strong foundation of skills and values."),
                areasForImprovement = json.optString("areasForImprovement", "Provide more quantifiable project outcomes."),
                prioritizedRecommendations = json.optString("prioritizedRecommendations", "Clarify your unique value proposition for your niche."),
                sevenDayPlanJson = json.optString("sevenDayPlanJson", "[]"),
                submittedPortfolioOrBio = optionalText
            )

            val id = evaluationDao.insertEvaluation(entity)
            entity.copy(id = id)
        }
    }

    suspend fun clearAll(userId: Long) {
        evaluationDao.deleteAllEvaluationsForUser(userId)
    }
}
