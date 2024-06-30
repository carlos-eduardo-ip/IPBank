package com.projetosip.ipbank.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.projetosip.ipbank.data.repository.AuthRepository

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {

    fun login(email: String, senha: String, onComplete: (Boolean, String?) -> Unit) {
        authRepository.login(email, senha, onComplete)
    }

    fun isUserLoggedIn(): Boolean {
        return authRepository.checkUserLoggedIn()
    }

    fun logout() {
        authRepository.logout()
    }
}