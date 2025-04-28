package com.healthapp.emotional.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.healthapp.emotional.data.dao.UserProfileDao
import com.healthapp.emotional.data.models.UserProfile
import com.healthapp.emotional.data.models.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileState(
    val userProfile: UserProfile? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val userProfileDao: UserProfileDao
) : ViewModel() {
    
    private val _profileState = MutableStateFlow(ProfileState())
    val profileState: StateFlow<ProfileState> = _profileState.asStateFlow()
    
    fun loadUserProfile(userId: String) {
        viewModelScope.launch {
            _profileState.value = _profileState.value.copy(
                isLoading = true,
                errorMessage = null
            )
            
            try {
                userProfileDao.getUserProfile().collectLatest { profile ->
                    if (profile != null && profile.id == userId) {
                        _profileState.value = _profileState.value.copy(
                            userProfile = profile,
                            isLoading = false
                        )
                    } else {
                        _profileState.value = _profileState.value.copy(
                            isLoading = false,
                            errorMessage = "Perfil não encontrado"
                        )
                    }
                }
            } catch (e: Exception) {
                _profileState.value = _profileState.value.copy(
                    isLoading = false,
                    errorMessage = "Erro ao carregar perfil: ${e.message}"
                )
            }
        }
    }
    
    fun updateUserProfile(userId: String, name: String) {
        viewModelScope.launch {
            _profileState.value = _profileState.value.copy(
                isLoading = true,
                errorMessage = null
            )
            
            try {
                val currentProfile = _profileState.value.userProfile
                
                if (currentProfile != null) {
                    val updatedProfile = currentProfile.copy(
                        name = name
                    )
                    
                    userProfileDao.updateUserProfile(updatedProfile)
                    
                    _profileState.value = _profileState.value.copy(
                        userProfile = updatedProfile,
                        isLoading = false
                    )
                } else {
                    // Create new profile if it doesn't exist
                    val newProfile = UserProfile(
                        id = userId,
                        name = name,
                        email = "",
                        preferences = UserPreferences()
                    )
                    
                    userProfileDao.insertUserProfile(newProfile)
                    
                    _profileState.value = _profileState.value.copy(
                        userProfile = newProfile,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _profileState.value = _profileState.value.copy(
                    isLoading = false,
                    errorMessage = "Erro ao atualizar perfil: ${e.message}"
                )
            }
        }
    }
    
    fun updateThemePreference(darkMode: Boolean) {
        viewModelScope.launch {
            val currentProfile = _profileState.value.userProfile ?: return@launch
            
            try {
                val updatedPreferences = currentProfile.preferences.copy(
                    darkMode = darkMode
                )
                
                val updatedProfile = currentProfile.copy(
                    preferences = updatedPreferences
                )
                
                userProfileDao.updateUserProfile(updatedProfile)
                
                _profileState.value = _profileState.value.copy(
                    userProfile = updatedProfile
                )
            } catch (e: Exception) {
                _profileState.value = _profileState.value.copy(
                    errorMessage = "Erro ao atualizar preferências: ${e.message}"
                )
            }
        }
    }
    
    fun updateNotificationPreference(enabled: Boolean) {
        viewModelScope.launch {
            val currentProfile = _profileState.value.userProfile ?: return@launch
            
            try {
                val updatedPreferences = currentProfile.preferences.copy(
                    notificationsEnabled = enabled
                )
                
                val updatedProfile = currentProfile.copy(
                    preferences = updatedPreferences
                )
                
                userProfileDao.updateUserProfile(updatedProfile)
                
                _profileState.value = _profileState.value.copy(
                    userProfile = updatedProfile
                )
            } catch (e: Exception) {
                _profileState.value = _profileState.value.copy(
                    errorMessage = "Erro ao atualizar preferências: ${e.message}"
                )
            }
        }
    }
    
    fun clearErrorMessage() {
        _profileState.value = _profileState.value.copy(
            errorMessage = null
        )
    }
} 