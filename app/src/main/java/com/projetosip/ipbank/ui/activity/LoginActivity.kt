package com.projetosip.ipbank.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.projetosip.ipbank.R
import com.projetosip.ipbank.databinding.ActivityLoginBinding
import com.projetosip.ipbank.ui.activity.utils.exibirMensagem
import com.projetosip.ipbank.ui.viewmodel.AuthViewModel
import com.projetosip.ipbank.ui.viewmodel.factory.AuthViewModelFactory

class LoginActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityLoginBinding.inflate( layoutInflater )
    }

    private val authViewModel: AuthViewModel by viewModels { AuthViewModelFactory(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        inicializarEventosClieque()
        //firebaseAuth.signOut()
    }

    override fun onStart() {
        super.onStart()
        verificarUsuarioLogado()
    }

    private fun verificarUsuarioLogado() {
        if ( authViewModel.isUserLoggedIn() ){
            startActivity(
                Intent(this, MainActivity::class.java)
            )
        }
    }

    private fun inicializarEventosClieque() {
        binding.textCadastro.setOnClickListener {
            startActivity(
                Intent(this, CadastroActivity::class.java)
            )
        }
        binding.btnLogar.setOnClickListener{
            if ( validadorCampos() ){
                logarUsuario()
            }
        }
    }

    private fun logarUsuario() {
        val email = binding.editLoginEmail.text.toString()
        val senha = binding.editLoginSenha.text.toString()

        authViewModel.login(email, senha) { success, message ->
            if (success) {
                exibirMensagem("Logado com sucesso!")
            } else {
                exibirMensagem(message ?: "Erro desconhecido.")
            }
        }
    }

    private fun validadorCampos(): Boolean {
        val email = binding.editLoginEmail.text.toString()
        val senha = binding.editLoginSenha.text.toString()

        return when {
            email.isEmpty() -> {
                binding.textInputLoginEmail.error = "Preencha o e-mail"
                false
            }
            senha.isEmpty() -> {
                binding.textInputLoginSenha.error = "Preencha a senha"
                false
            }
            else -> {
                binding.textInputLoginEmail.error = null
                binding.textInputLoginSenha.error = null
                true
            }
        }
    }
}