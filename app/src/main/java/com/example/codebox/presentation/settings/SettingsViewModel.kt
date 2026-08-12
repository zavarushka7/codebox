package com.example.codebox.presentation.settings

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.util.Base64
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val user = Firebase.auth.currentUser
    private val uid = user?.uid ?: ""

    private val _nickname = mutableStateOf("")
    val nickname: State<String> = _nickname

    private val _avatarUrl = mutableStateOf("") // здесь либо URL, либо Base64
    val avatarUrl: State<String> = _avatarUrl

    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> = _error

    private val _success = mutableStateOf<String?>(null)
    val success: State<String?> = _success

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    init {
        if (uid.isNotBlank()) {
            viewModelScope.launch {
                try {
                    val doc = firestore.collection("users").document(uid).get().await()
                    _nickname.value = doc.getString("nickname") ?: user?.displayName ?: ""
                    // сначала ищем avatarBase64, если нет — старый avatarUrl
                    _avatarUrl.value = doc.getString("avatarBase64") ?: doc.getString("avatarUrl") ?: ""
                } catch (_: Exception) {
                    _nickname.value = user?.displayName ?: ""
                }
            }
        }
    }

    fun onNicknameChange(value: String) {
        _nickname.value = value
        _error.value = null
        _success.value = null
    }

    fun onAvatarSelected(uri: Uri, context: Context) {
        if (uid.isBlank()) {
            _error.value = "пользователь не авторизован"
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                val scaled = Bitmap.createScaledBitmap(bitmap, 200, 200, true)
                val stream = ByteArrayOutputStream()
                scaled.compress(Bitmap.CompressFormat.JPEG, 70, stream)
                val bytes = stream.toByteArray()
                val base64 = Base64.encodeToString(bytes, Base64.DEFAULT)

                // лимит Firestore — 1 МБ на документ
                if (base64.length > 900_000) {
                    _error.value = "изображение слишком большое"
                    _isLoading.value = false
                    return@launch
                }

                firestore.collection("users").document(uid)
                    .update("avatarBase64", base64)
                    .await()

                _avatarUrl.value = base64
                _success.value = "аватар обновлён"
            } catch (e: Exception) {
                _error.value = e.message ?: "ошибка загрузки аватара"
                e.printStackTrace()
            }
            _isLoading.value = false
        }
    }

    fun saveProfile() {
        if (_nickname.value.isBlank()) {
            _error.value = "никнейм не может быть пустым"
            return
        }
        if (uid.isBlank()) {
            _error.value = "пользователь не авторизован"
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            try {
                firestore.collection("users").document(uid)
                    .set(
                        hashMapOf("nickname" to _nickname.value),
                        com.google.firebase.firestore.SetOptions.merge()
                    )
                    .await()
                _success.value = "профиль обновлён"
            } catch (e: Exception) {
                _error.value = e.message ?: "ошибка сохранения"
            }
            _isLoading.value = false
        }
    }

    fun signOut() {
        Firebase.auth.signOut()
    }
}