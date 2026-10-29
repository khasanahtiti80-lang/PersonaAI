package com.example.data.repository

import com.example.data.ai.GeminiAiService
import com.example.data.local.PersonaDatabase
import com.example.data.local.entity.BrandProfileEntity
import com.example.data.local.entity.InterviewSessionEntity
import kotlinx.coroutines.flow.Flow
import org.json.JSONArray
import org.json.JSONObject

data class InterviewTurn(
    val questionNumber: Int,
    val questionText: String,
    val userAnswer: String = "",
    val feedback: String = "",
    val score: Int = 0, // 0-100
    val suggestedImprovement: String = ""
)

class InterviewRepository(
    private val database: PersonaDatabase,
    private val aiService: GeminiAiService
) {
    private val interviewDao = database.interviewDao()

    fun observeSessions(userId: Long): Flow<List<InterviewSessionEntity>> =
        interviewDao.observeSessions(userId)

    fun observeLatestSession(userId: Long): Flow<InterviewSessionEntity?> =
        interviewDao.observeLatestSession(userId)

    suspend fun getSession(id: Long, userId: Long): InterviewSessionEntity? =
        interviewDao.getSessionById(id, userId)

    suspend fun saveSession(session: InterviewSessionEntity): Long =
        interviewDao.insertSession(session)

    suspend fun updateSession(session: InterviewSessionEntity) =
        interviewDao.updateSession(session)

    suspend fun deleteSession(id: Long, userId: Long) =
        interviewDao.deleteSessionById(id, userId)

    suspend fun generateFirstQuestion(
        profile: BrandProfileEntity,
        interviewType: String,
        targetRole: String,
        difficulty: String,
        focus: String,
        language: String
    ): Result<String> {
        val prompt = """
            You are an expert interviewer conducting an authentic $difficulty-level $interviewType interview in $language for the role of '$targetRole'.
            The focus is: $focus questions.
            
            Candidate background:
            - Education: ${profile.education}
            - Background: ${profile.currentRole}
            - Skills: ${profile.skills}
            - Experience: ${profile.experienceAndAchievements}
            
            Generate the FIRST interview question. It should be realistic, direct, and tailored to the target role.
            Output ONLY the question itself.
        """.trimIndent()

        return aiService.generateText(prompt)
    }

    suspend fun evaluateAnswerAndGetNextQuestion(
        profile: BrandProfileEntity,
        interviewType: String,
        targetRole: String,
        focus: String,
        questionIndex: Int, // 1-based current question
        totalQuestions: Int,
        currentQuestion: String,
        userAnswer: String
    ): Result<Pair<InterviewTurn, String?>> {
        val isLast = questionIndex >= totalQuestions
        val prompt = """
            You are an expert interviewer evaluating a candidate's answer and deciding the next step.
            Candidate target position: '$targetRole' ($interviewType, $focus focus).
            
            INTERVIEW QUESTION:
            "$currentQuestion"
            
            CANDIDATE'S ANSWER:
            "$userAnswer"
            
            EVALUATION CRITERIA:
            1. Relevance (Did they answer the prompt directly?)
            2. Clarity & Conciseness
            3. Structure (STAR method: Situation, Task, Action, Result)
            4. Confidence & Professionalism
            
            Generate:
            - A realistic score from 0 to 100 based strictly on what they wrote.
            - Constructive feedback highlighting specific strengths and what was missing.
            - A refined/suggested version of their answer that retains their actual facts without fabricating experiences.
            ${if (!isLast) "- The NEXT relevant follow-up or next interview question (${questionIndex + 1} of $totalQuestions)." else "- (This was the final question, no next question)."}
            
            Return ONLY a valid JSON object:
            {
              "score": 85,
              "feedback": "Specific feedback text...",
              "suggestedImprovement": "How the candidate could frame this answer better...",
              "nextQuestion": ${if (!isLast) "\"Next question text...\"" else "null"}
            }
        """.trimIndent()

        val aiResult = aiService.generateText(prompt)
        return aiResult.mapCatching { jsonText ->
            val cleanJson = jsonText.trim()
                .removePrefix("```json")
                .removePrefix("```")
                .removeSuffix("```")
                .trim()

            val json = JSONObject(cleanJson)
            val score = json.optInt("score", 75)
            val feedback = json.optString("feedback", "Good effort.")
            val suggested = json.optString("suggestedImprovement", "")
            val nextQ = if (!isLast && json.has("nextQuestion") && !json.isNull("nextQuestion")) {
                json.optString("nextQuestion").takeIf { it.isNotBlank() }
            } else null

            val turn = InterviewTurn(
                questionNumber = questionIndex,
                questionText = currentQuestion,
                userAnswer = userAnswer,
                feedback = feedback,
                score = score,
                suggestedImprovement = suggested
            )
            Pair(turn, nextQ)
        }
    }

    suspend fun generateFinalSummary(
        targetRole: String,
        interviewType: String,
        turns: List<InterviewTurn>
    ): Result<Triple<Int, String, String>> {
        val turnsText = turns.mapIndexed { idx, t ->
            "Q${idx + 1}: ${t.questionText}\nA: ${t.userAnswer}\nScore: ${t.score}/100"
        }.joinToString("\n\n")

        val prompt = """
            The interview session for '$targetRole' ($interviewType) is finished.
            Here are the Q&As and intermediate scores:
            $turnsText
            
            Provide:
            1. An overall score (0 to 100).
            2. An overall performance feedback summary.
            3. A prioritized improvement plan with specific actionable steps before the real interview.
            
            Return ONLY valid JSON:
            {
              "overallScore": 82,
              "overallFeedback": "Summary of candidate performance...",
              "improvementPlan": "Actionable 3-point practice plan..."
            }
        """.trimIndent()

        val aiResult = aiService.generateText(prompt)
        return aiResult.mapCatching { jsonText ->
            val cleanJson = jsonText.trim()
                .removePrefix("```json")
                .removePrefix("```")
                .removeSuffix("```")
                .trim()
            val json = JSONObject(cleanJson)
            Triple(
                json.optInt("overallScore", turns.map { it.score }.average().toInt()),
                json.optString("overallFeedback", "Overall solid demonstration."),
                json.optString("improvementPlan", "Continue practicing structured STAR examples.")
            )
        }
    }

    suspend fun clearAll(userId: Long) {
        interviewDao.deleteAllSessionsForUser(userId)
    }
}
