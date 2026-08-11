package com.example.codebox.presentation.settings

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val user = Firebase.auth.currentUser

    private val _nickname = mutableStateOf("")
    val nickname: State<String> = _nickname

    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> = _error

    private val _success = mutableStateOf<String?>(null)
    val success: State<String?> = _success

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    init {
        // 1. Мгновенный fallback: displayName или часть email до @
        _nickname.value = user?.displayName ?: user?.email?.substringBefore("@") ?: ""

        // 2. Асинхронно подтягиваем из Firestore (приоритетнее)
        user?.uid?.let { uid ->
            viewModelScope.launch {
                try {
                    val doc = firestore.collection("users").document(uid).get().await()
                    val firestoreNick = doc.getString("nickname")
                    if (!firestoreNick.isNullOrBlank()) {
                        _nickname.value = firestoreNick
                    }
                } catch (_: Exception) {
                    // оставляем fallback из Auth
                }
            }
        }
    }

    fun onNicknameChange(value: String) {
        _nickname.value = value
        _error.value = null
        _success.value = null
    }

    fun saveProfile() {
        if (_nickname.value.isBlank()) {
            _error.value = "никнейм не может быть пустым"
            return
        }
        val currentUser = user ?: run {
            _error.value = "пользователь не авторизован"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _success.value = null
            try {
                // Обновляем Firebase Auth
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(_nickname.value)
                    .build()
                currentUser.updateProfile(profileUpdates).await()

                // Обновляем/создаём документ в Firestore
                currentUser.uid?.let { uid ->
                    firestore.collection("users").document(uid)
                        .set(
                            mapOf("nickname" to _nickname.value),
                            SetOptions.merge()
                        )
                        .await()
                }

                _success.value = "профиль обновлён"
            } catch (e: Exception) {
                _error.value = e.message ?: "ошибка обновления профиля"
            } finally {
                _isLoading.value = false
            }
        }
    }
}