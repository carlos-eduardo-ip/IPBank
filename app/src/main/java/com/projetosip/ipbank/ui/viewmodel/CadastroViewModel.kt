package com.projetosip.ipbank.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.projetosip.ipbank.data.repository.AuthRepository

class CadastroViewModel(private val authRepository: AuthRepository) : ViewModel() {

    fun registerUser(nome: String, email: String, senha: String, onComplete: (Boolean, String?) -> Unit) {
        authRepository.registerUser(nome, email, senha, onComplete)
    }
}