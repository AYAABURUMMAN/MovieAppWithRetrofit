package com.movie.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.movie.data.local.MovieDatabase
import com.movie.data.local.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val userDao = MovieDatabase.getInstance(application).userDao()
    private val prefs = application.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    val isLoggedIn: Boolean
        get() = prefs.getBoolean("is_logged_in", false)

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error("Please fill in all fields")
            return
        }
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val user = userDao.login(email.trim(), password)
            if (user != null) {
                prefs.edit().putBoolean("is_logged_in", true).apply()
                _authState.value = AuthState.Success
            } else {
                _authState.value = AuthState.Error("Invalid email or password")
            }
        }
    }

    fun register(email: String, password: String, confirmPassword: String) {
        if (email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
            _authState.value = AuthState.Error("Please fill in all fields")
            return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _authState.value = AuthState.Error("Please enter a valid email")
            return
        }
        if (password.length < 6) {
            _authState.value = AuthState.Error("Password must be at least 6 characters")
            return
        }
        if (password != confirmPassword) {
            _authState.value = AuthState.Error("Passwords do not match")
            return
        }
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val taken = userDao.isEmailTaken(email.trim())
            if (taken > 0) {
                _authState.value = AuthState.Error("Email already registered")
                return@launch
            }
            userDao.registerUser(User(email = email.trim(), password = password))
            prefs.edit().putBoolean("is_logged_in", true).apply()
            _authState.value = AuthState.Success
        }
    }

    fun logout() {
        prefs.edit().putBoolean("is_logged_in", false).apply()
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }

    companion object {
        fun factory(application: Application): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    AuthViewModel(application) as T
            }
    }
}