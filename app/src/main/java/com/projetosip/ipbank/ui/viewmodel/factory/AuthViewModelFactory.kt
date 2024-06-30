package com.projetosip.ipbank.ui.viewmodel.factory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.projetosip.ipbank.data.repository.AuthRepository
import com.projetosip.ipbank.ui.viewmodel.AuthViewModel

class AuthViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(AuthRepository(context)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}