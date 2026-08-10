package com.example.codebox.presentation.auth

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import dagger.hilt.android.lifecycle.HiltViewModel

import androidx.compose.runtime.State
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor() : ViewModel(){
    private val auth = Firebase.auth

    private val _email = mutableStateOf("")
    val email: State<String> = _email

    private val _password = mutableStateOf("")
    val password: State<String> = _password

    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> = _error

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    fun onEmailChange(newEmail: String){
        _email.value = newEmail
        _error.value = null
    }

    fun onPasswordChange(newPassword: String){
        _password.value = newPassword
        _error.value = null
    }

    fun signIn(onSuccess: () -> Unit){
        if (_email.value.isBlank() || _password.value.isBlank()){
            _error.value = "Заполните все поля"
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            try {
                auth.signInWithEmailAndPassword(_email.value, _password.value).await()
                onSuccess()
            } catch (e: Exception){
                _error.value = e.message ?: "Ошибка входа"
            }
            _isLoading.value = false
        }
    }

    fun signUp(onSuccess: () -> Unit){
        if (_email.value.isBlank() || _password.value.isBlank()){
            _error.value = "Заполните все поля"
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            try {
                auth.createUserWithEmailAndPassword(_email.value, _password.value).await()
                onSuccess()
            } catch (e: Exception){
                _error.value = e.message ?: "Ошибка регистрации"
            }
            _isLoading.value = false
        }
    }

    fun signOut(){
        auth.signOut()
    }

}