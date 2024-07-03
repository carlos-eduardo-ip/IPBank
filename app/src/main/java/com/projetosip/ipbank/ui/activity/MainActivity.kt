package com.projetosip.ipbank.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.projetosip.ipbank.R
import com.projetosip.ipbank.data.model.Usuario
import com.projetosip.ipbank.databinding.ActivityMainBinding
import com.squareup.picasso.Picasso
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    private val firestore by lazy {
        FirebaseFirestore.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        recuperarDadosUsuario()
        inicializarEventosClieque()
    }

    private fun recuperarDadosUsuario() {
        val idUsuario = firebaseAuth.currentUser?.uid
        if (idUsuario != null) {
            firestore.collection("usuarios")
                .document(idUsuario)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    val usuario = documentSnapshot.toObject(Usuario::class.java)
                    if (usuario != null) {
                        binding.textNomeUsuario.text = usuario.nome
                        binding.textTotalSaldo.text = String.format("R$ %.2f", usuario.saldo)
                        if (usuario.foto.isNotEmpty()) {
                            Picasso.get().load(usuario.foto).into(binding.imgUsuario)
                        }

                        val saudacao = when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
                            in 0..11 -> "Bom dia!"
                            in 12..17 -> "Boa tarde!"
                            else -> "Boa noite!"
                        }
                        binding.textSaudacao.text = saudacao
                    }
                }
                .addOnFailureListener{erro ->
                    erro.printStackTrace()
                }
        }
    }

    private fun inicializarEventosClieque() {
        binding.imgUsuario.setOnClickListener {
            startActivity(
                Intent(this, PerfilUsuarioActivity::class.java)
            )
        }
        binding.textVerCartoes.setOnClickListener {
            startActivity(
                Intent(this, GerenciarCartaoActivity::class.java)
            )
        }
        binding.imgTransferir.setOnClickListener {
            startActivity(
                Intent(this, TransferirActivity::class.java)
            )
        }
    }

}