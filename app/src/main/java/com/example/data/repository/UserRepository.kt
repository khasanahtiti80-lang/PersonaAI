package com.example.data.repository

import com.example.data.local.PersonaDatabase
import com.example.data.local.SessionManager
import com.example.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

class UserRepository(
    private val database: PersonaDatabase,
    private val sessionManager: SessionManager
) {
    private val userDao = database.userDao()

    val currentUserId: Flow<Long?> = sessionManager.currentUserId

    suspend fun signUp(email: String, password: String, fullName: String): Result<UserEntity> {
        val trimmedEmail = email.trim().lowercase()
        val trimmedName = fullName.trim()

        if (trimmedEmail.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()) {
            return Result.failure(IllegalArgumentException("Please enter a valid email address."))
        }
        if (password.length < 6) {
            return Result.failure(IllegalArgumentException("Password must be at least 6 characters long."))
        }
        if (trimmedName.isBlank()) {
            return Result.failure(IllegalArgumentException("Please enter your name."))
        }

        val existing = userDao.getUserByEmail(trimmedEmail)
        if (existing != null) {
            return Result.failure(IllegalArgumentException("An account with this email already exists."))
        }

        val hash = SessionManager.hashPassword(password)
        val user = UserEntity(
            email = trimmedEmail,
            passwordHash = hash,
            fullName = trimmedName
        )
        val id = userDao.insertUser(user)
        val created = user.copy(id = id)
        sessionManager.loginUser(id)
        return Result.success(created)
    }

    suspend fun login(email: String, password: String): Result<UserEntity> {
        val trimmedEmail = email.trim().lowercase()
        if (trimmedEmail.isBlank()) {
            return Result.failure(IllegalArgumentException("Email is required."))
        }
        if (password.isBlank()) {
            return Result.failure(IllegalArgumentException("Password is required."))
        }

        val user = userDao.getUserByEmail(trimmedEmail)
            ?: return Result.failure(IllegalArgumentException("No account found with this email."))

        val hash = SessionManager.hashPassword(password)
        if (user.passwordHash != hash) {
            return Result.failure(IllegalArgumentException("Incorrect password. Please try again."))
        }

        sessionManager.loginUser(user.id)
        return Result.success(user)
    }

    suspend fun getCurrentUser(): UserEntity? {
        val id = sessionManager.currentUserId.value ?: return null
        return userDao.getUserById(id)
    }

    fun observeCurrentUser(): Flow<UserEntity?> {
        val id = sessionManager.currentUserId.value ?: -1L
        return userDao.observeUser(id)
    }

    fun logout() {
        sessionManager.logout()
    }

    suspend fun deleteAccount(): Result<Boolean> {
        val id = sessionManager.currentUserId.value
            ?: return Result.failure(IllegalStateException("No user logged in"))

        database.userDao().deleteUser(id)
        database.brandProfileDao().deleteProfileByUserId(id)
        database.contentDao().deleteAllBiosForUser(id)
        database.contentDao().deleteAllSocialPostsForUser(id)
        database.interviewDao().deleteAllSessionsForUser(id)
        database.evaluationDao().deleteAllEvaluationsForUser(id)
        sessionManager.logout()
        return Result.success(true)
    }
}
