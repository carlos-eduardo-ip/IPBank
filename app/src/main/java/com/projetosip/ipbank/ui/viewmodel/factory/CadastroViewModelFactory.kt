package com.projetosip.ipbank.ui.viewmodel.factory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.projetosip.ipbank.data.repository.AuthRepository
import com.projetosip.ipbank.ui.viewmodel.CadastroViewModel

class CadastroViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CadastroViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CadastroViewModel(AuthRepository(context)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}