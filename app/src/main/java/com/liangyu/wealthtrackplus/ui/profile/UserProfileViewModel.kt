package com.liangyu.wealthtrackplus.ui.profile

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class UserProfileViewModel : ViewModel() {
    private val avatarPalette = listOf(
        Color(0xFFC7F2D7),
        Color(0xFFD2E8FF),
        Color(0xFFFFF2CC)
    )
    private var currentAvatarIndex = 0

    private val _uiState = MutableStateFlow(
        UserProfileUiState(
            displayName = "Liangyu Chen",
            avatarBackground = avatarPalette.first(),
            fields = listOf(
                ProfileField(
                    title = "目標資產",
                    value = "1,000",
                    supportingText = "新台幣"
                ),
                ProfileField(
                    title = "主要幣別",
                    value = "新台幣"
                ),
                ProfileField(
                    title = "投資風格屬性",
                    value = "穩健型"
                ),
                ProfileField(
                    title = "首買資產檢視方式",
                    value = "依資產類別"
                ),
                ProfileField(
                    title = "偏好語言",
                    value = "繁體中文"
                )
            )
        )
    )
    val uiState: StateFlow<UserProfileUiState> = _uiState.asStateFlow()

    fun updateDisplayName(newName: String) {
        if (newName.isBlank()) return
        _uiState.update { it.copy(displayName = newName.trim()) }
    }

    fun updateDisplayNameFromNav(argumentName: String) {
        if (argumentName.isBlank()) return
        _uiState.update { current ->
            if (current.displayName == argumentName) {
                current
            } else {
                current.copy(displayName = argumentName)
            }
        }
    }

    fun cycleAvatarBackground() {
        currentAvatarIndex = (currentAvatarIndex + 1) % avatarPalette.size
        val nextColor = avatarPalette[currentAvatarIndex]
        _uiState.update { it.copy(avatarBackground = nextColor) }
    }
}


data class UserProfileUiState(
    val displayName: String = "",
    val avatarBackground: Color = Color(0xFFC7F2D7),
    val fields: List<ProfileField> = emptyList()
)

data class ProfileField(
    val title: String,
    val value: String,
    val supportingText: String? = null
)
