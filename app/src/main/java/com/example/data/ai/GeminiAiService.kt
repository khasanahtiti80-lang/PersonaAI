package com.example.data.ai

import android.util.Log
import com.example.data.local.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GeminiAiService(private val sessionManager: SessionManager) {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    suspend fun generateText(prompt: String, systemInstruction: String? = null): Result<String> =
        withContext(Dispatchers.IO) {
            val apiKey = sessionManager.getEffectiveApiKey()
            if (apiKey.isBlank()) {
                return@withContext Result.failure(
                    IllegalStateException("Gemini API key is missing. Please configure your key in Settings or the AI Studio Secrets panel.")
                )
            }

            try {
                // Models: gemini-2.5-flash or gemini-1.5-flash
                val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$apiKey"

                val rootJson = JSONObject()
                val contentsArray = JSONArray()
                val contentObj = JSONObject()
                val partsArray = JSONArray()

                val promptPart = JSONObject().put("text", prompt)
                partsArray.put(promptPart)
                contentObj.put("parts", partsArray)
                contentsArray.put(contentObj)
                rootJson.put("contents", contentsArray)

                if (!systemInstruction.isNullOrBlank()) {
                    val sysInstructionObj = JSONObject()
                    val sysParts = JSONArray().put(JSONObject().put("text", systemInstruction))
                    sysInstructionObj.put("parts", sysParts)
                    rootJson.put("systemInstruction", sysInstructionObj)
                }

                val genConfig = JSONObject()
                    .put("temperature", 0.7)
                    .put("maxOutputTokens", 2500)
                rootJson.put("generationConfig", genConfig)

                val body = rootJson.toString().toRequestBody(jsonMediaType)
                val request = Request.Builder()
                    .url(url)
                    .post(body)
                    .build()

                val response = client.newCall(request).execute()
                val responseBody = response.body?.string().orEmpty()

                if (!response.isSuccessful) {
                    Log.e("GeminiAiService", "API error: HTTP ${response.code} - $responseBody")
                    val errorMsg = try {
                        val errObj = JSONObject(responseBody).getJSONObject("error")
                        errObj.optString("message", "HTTP ${response.code}: Request failed")
                    } catch (_: Exception) {
                        "API request failed with HTTP ${response.code}"
                    }
                    return@withContext Result.failure(Exception(errorMsg))
                }

                val jsonResponse = JSONObject(responseBody)
                val candidates = jsonResponse.optJSONArray("candidates")
                if (candidates == null || candidates.length() == 0) {
                    return@withContext Result.failure(Exception("AI model returned an empty response."))
                }

                val candidate = candidates.getJSONObject(0)
                val parts = candidate.getJSONObject("content").getJSONArray("parts")
                val text = parts.getJSONObject(0).getString("text")

                Result.success(text.trim())
            } catch (e: Exception) {
                Log.e("GeminiAiService", "Generation failed", e)
                Result.failure(e)
            }
        }

    suspend fun testConnection(apiKey: String): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val testKey = apiKey.trim()
            if (testKey.isBlank()) return@withContext Result.failure(Exception("API key cannot be empty"))

            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$testKey"
            val rootJson = JSONObject().apply {
                put("contents", JSONArray().put(
                    JSONObject().put("parts", JSONArray().put(JSONObject().put("text", "Respond with the word 'OK'.")))
                ))
            }
            val body = rootJson.toString().toRequestBody(jsonMediaType)
            val request = Request.Builder().url(url).post(body).build()

            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                Result.success(true)
            } else {
                val errorBody = response.body?.string().orEmpty()
                Result.failure(Exception("Invalid API key or network error (HTTP ${response.code})"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
