package com.projetosip.ipbank.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore
import com.projetosip.ipbank.R
import com.projetosip.ipbank.databinding.ActivityCadastroBinding
import com.projetosip.ipbank.data.model.Usuario
import com.projetosip.ipbank.ui.viewmodel.CadastroViewModel
import com.projetosip.ipbank.ui.viewmodel.factory.CadastroViewModelFactory
import com.projetosip.ipbank.ui.activity.utils.exibirMensagem

class CadastroActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityCadastroBinding.inflate( layoutInflater )
    }

    private val cadastroViewModel: CadastroViewModel by viewModels { CadastroViewModelFactory(this)}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        inicializarToolbar()
        inicializarEventosClique()
    }

    private fun validarCampos(): Boolean {
        val nome = binding.editNome.text.toString()
        val email = binding.editEmail.text.toString()
        val senha = binding.editSenha.text.toString()

        return when {
            nome.isEmpty() -> {
                binding.textInputLayoutNome.error = "Preencha o seu nome!"
                false
            }
            email.isEmpty() -> {
                binding.textInputLayoutEmail.error = "Preencha o seu e-mail!"
                false
            }
            senha.isEmpty() -> {
                binding.textInputLayoutSenha.error = "Preencha a senha!"
                false
            }
            else -> {
                binding.textInputLayoutNome.error = null
                binding.textInputLayoutEmail.error = null
                binding.textInputLayoutSenha.error = null
                true
            }
        }
    }

    private fun inicializarEventosClique() {
        binding.btnCadastrar.setOnClickListener {
            if (validarCampos()) {
                val nome = binding.editNome.text.toString()
                val email = binding.editEmail.text.toString()
                val senha = binding.editSenha.text.toString()
                cadastrarUsuario(nome, email, senha)
            }
        }
    }

    private fun cadastrarUsuario(nome: String, email: String, senha: String) {
        cadastroViewModel.registerUser(nome, email, senha) { success, message ->
            if (success) {
                exibirMensagem("Sucesso ao fazer seu cadastro.")
                startActivity(Intent(applicationContext, MainActivity::class.java))
            } else {
                exibirMensagem(message ?: "Erro desconhecido.")
            }
        }
    }

    private fun inicializarToolbar() {
        val toolbar = binding.includeToolbar.tbPrincipal
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = "Faça o seu cadastro"
            setDisplayHomeAsUpEnabled(true)
        }
    }
}