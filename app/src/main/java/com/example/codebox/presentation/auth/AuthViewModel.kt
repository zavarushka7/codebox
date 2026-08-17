package com.example.codebox.presentation.auth

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.codebox.data.repository.SettingsRepository
import com.example.codebox.domain.TextCaseStyle
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    settingsRepository: SettingsRepository
) : ViewModel() {

    val textCaseStyle: StateFlow<TextCaseStyle> = settingsRepository.textCaseStyle
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TextCaseStyle.NORMAL)

    // ─── состояния формы ───
    private val _email = mutableStateOf("")
    val email: State<String> = _email

    private val _password = mutableStateOf("")
    val password: State<String> = _password

    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> = _error

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    fun onEmailChange(new: String) {
        _email.value = new
    }

    fun onPasswordChange(new: String) {
        _password.value = new
    }

    fun signIn(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                Firebase.auth
                    .signInWithEmailAndPassword(_email.value, _password.value)
                    .await()
                onSuccess()
            } catch (e: Exception) {
                _error.value = e.localizedMessage ?: "auth error"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun signUp(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val result = Firebase.auth
                    .createUserWithEmailAndPassword(_email.value, _password.value)
                    .await()

                val uid = result.user?.uid ?: throw Exception("uid is null")
                val userEmail = result.user?.email ?: _email.value

                // ← создаём профиль в Firestore
                Firebase.firestore.collection("users").document(uid)
                    .set(
                        hashMapOf(
                            "email" to userEmail,
                            "nickname" to uid.take(6),
                            "description" to "",
                            "avatarBase64" to "",
                            "avatarUrl" to ""
                        )
                    ).await()

                onSuccess()
            } catch (e: Exception) {
                _error.value = e.localizedMessage ?: "registration error"
            } finally {
                _isLoading.value = false
            }
        }
    }
}