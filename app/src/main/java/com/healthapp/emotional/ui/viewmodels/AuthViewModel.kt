package com.healthapp.emotional.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.healthapp.emotional.data.User
import com.healthapp.emotional.data.UserDao
import com.healthapp.emotional.data.dao.UserProfileDao
import com.healthapp.emotional.data.models.UserProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import java.util.UUID

data class AuthState(
    val isLoggedIn: Boolean = false,
    val currentUser: User? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val userDao: UserDao,
    private val userProfileDao: UserProfileDao,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {
    
    private val _authState = MutableStateFlow(AuthState())
    val authState: StateFlow<AuthState> = _authState.asStateFlow()
    
    val isLoggedIn: StateFlow<Boolean> get() = MutableStateFlow(_authState.value.isLoggedIn)
    
    init {
        // Verificar se há um usuário já autenticado no Firebase
        firebaseAuth.currentUser?.let { firebaseUser ->
            viewModelScope.launch {
                try {
                    val user = User(
                        id = firebaseUser.uid,
                        email = firebaseUser.email ?: "",
                        name = firebaseUser.displayName ?: "",
                        password = "" // Não armazenamos a senha localmente
                    )
                    
                    // Verificar se o usuário existe no banco local
                    val existingUser = userDao.getUserById(user.id)
                    if (existingUser == null) {
                        // Se não existir, criar no banco local
                        userDao.insertUser(user)
                        
                        // Criar perfil do usuário
                        val userProfile = UserProfile(
                            id = user.id,
                            name = user.name,
                            email = user.email
                        )
                        userProfileDao.insertUserProfile(userProfile)
                    }
                    
                    _authState.value = _authState.value.copy(
                        isLoggedIn = true,
                        currentUser = user,
                        errorMessage = null
                    )
                } catch (e: Exception) {
                    _authState.value = _authState.value.copy(
                        errorMessage = "Erro ao carregar usuário: ${e.message}"
                    )
                }
            }
        }
    }
    
    fun register(email: String, password: String, name: String) {
        if (email.isBlank() || password.isBlank()) {
            _authState.value = _authState.value.copy(
                errorMessage = "Email e senha não podem estar vazios"
            )
            return
        }
        
        viewModelScope.launch {
            _authState.value = _authState.value.copy(isLoading = true, errorMessage = null)
            
            try {
                // Criar usuário no Firebase
                val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
                val firebaseUser = result.user
                
                if (firebaseUser != null) {
                    // Atualizar nome do usuário no Firebase
                    val profileUpdates = com.google.firebase.auth.UserProfileChangeRequest.Builder()
                        .setDisplayName(name)
                        .build()
                    firebaseUser.updateProfile(profileUpdates).await()
                    
                    // Criar usuário no banco local
                    val user = User(
                        id = firebaseUser.uid,
                        email = email,
                        name = name,
                        password = "" // Não armazenamos a senha localmente
                    )
                    userDao.insertUser(user)
                    
                    // Criar perfil do usuário
                    val userProfile = UserProfile(
                        id = user.id,
                        name = name,
                        email = email
                    )
                    userProfileDao.insertUserProfile(userProfile)
                    
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        isLoggedIn = true,
                        currentUser = user,
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                _authState.value = _authState.value.copy(
                    isLoading = false,
                    errorMessage = "Erro ao registrar: ${e.message}"
                )
            }
        }
    }
    
    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _authState.value = _authState.value.copy(
                errorMessage = "Email e senha não podem estar vazios"
            )
            return
        }
        
        viewModelScope.launch {
            _authState.value = _authState.value.copy(isLoading = true, errorMessage = null)
            
            try {
                // Autenticar no Firebase
                val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
                val firebaseUser = result.user
                
                if (firebaseUser != null) {
                    // Verificar se o usuário existe no banco local
                    var user = userDao.getUserById(firebaseUser.uid)
                    
                    if (user == null) {
                        // Se não existir, criar no banco local
                        user = User(
                            id = firebaseUser.uid,
                            email = firebaseUser.email ?: "",
                            name = firebaseUser.displayName ?: "",
                            password = "" // Não armazenamos a senha localmente
                        )
                        userDao.insertUser(user)
                        
                        // Criar perfil do usuário
                        val userProfile = UserProfile(
                            id = user.id,
                            name = user.name,
                            email = user.email
                        )
                        userProfileDao.insertUserProfile(userProfile)
                    }
                    
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        isLoggedIn = true,
                        currentUser = user,
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                _authState.value = _authState.value.copy(
                    isLoading = false,
                    errorMessage = "Erro ao fazer login: ${e.message}"
                )
            }
        }
    }
    
    fun processLogout() {
        viewModelScope.launch {
            try {
                firebaseAuth.signOut()
                _authState.value = _authState.value.copy(
                    isLoggedIn = false,
                    currentUser = null
                )
            } catch (e: Exception) {
                _authState.value = _authState.value.copy(
                    errorMessage = "Erro ao fazer logout: ${e.message}"
                )
            }
        }
    }

    // Carregar usuário atual
    fun loadCurrentUser() {
        viewModelScope.launch {
            try {
                _authState.value = _authState.value.copy(isLoading = true, errorMessage = null)
                
                val firebaseUser = firebaseAuth.currentUser
                if (firebaseUser != null) {
                    // Verificar se o usuário existe no banco local
                    var user = userDao.getUserById(firebaseUser.uid)
                    
                    if (user == null) {
                        // Se não existir, criar no banco local
                        user = User(
                            id = firebaseUser.uid,
                            email = firebaseUser.email ?: "",
                            name = firebaseUser.displayName ?: "",
                            password = "" // Não armazenamos a senha localmente
                        )
                        userDao.insertUser(user)
                        
                        // Criar perfil do usuário
                        val userProfile = UserProfile(
                            id = user.id,
                            name = user.name,
                            email = user.email
                        )
                        userProfileDao.insertUserProfile(userProfile)
                    }
                    
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        isLoggedIn = true,
                        currentUser = user,
                        errorMessage = null
                    )
                } else {
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        isLoggedIn = false,
                        currentUser = null,
                        errorMessage = "Usuário não autenticado. Por favor, faça login."
                    )
                }
            } catch (e: Exception) {
                _authState.value = _authState.value.copy(
                    isLoading = false,
                    errorMessage = "Erro ao carregar usuário: ${e.message}"
                )
            }
        }
    }
} 