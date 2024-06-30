package com.projetosip.ipbank.data.repository

import android.content.Context
import android.content.Intent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore
import com.projetosip.ipbank.data.model.Usuario
import com.projetosip.ipbank.ui.activity.LoginActivity
import com.projetosip.ipbank.ui.activity.MainActivity

class AuthRepository(private val context: Context) {

    private val firebaseAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private val firestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    fun login(email: String, senha: String, onComplete: (Boolean, String?) -> Unit) {
        firebaseAuth.signInWithEmailAndPassword(email, senha)
            .addOnSuccessListener {
                onComplete(true, null)
                context.startActivity(Intent(context, MainActivity::class.java))
            }
            .addOnFailureListener { erro ->
                val errorMessage = when (erro) {
                    is FirebaseAuthInvalidUserException -> "E-mail não cadastrado."
                    is FirebaseAuthInvalidCredentialsException -> "E-mail ou senha estão incorretos!"
                    else -> "Falha no login: ${erro.message}"
                }
                onComplete(false, errorMessage)
            }
    }

    fun registerUser(nome: String, email: String, senha: String, onComplete: (Boolean, String?) -> Unit) {
        firebaseAuth.createUserWithEmailAndPassword(email, senha)
            .addOnCompleteListener { resultado ->
                if (resultado.isSuccessful) {
                    val idUsuario = resultado.result.user?.uid
                    if (idUsuario != null) {
                        val usuario = Usuario(idUsuario, nome, email)
                        saveUserToFirestore(usuario, onComplete)
                    } else {
                        onComplete(false, "Erro ao obter ID do usuário.")
                    }
                }
            }
            .addOnFailureListener { erro ->
                val errorMessage = when (erro) {
                    is FirebaseAuthUserCollisionException -> "E-mail já pertence a outro usuário."
                    is FirebaseAuthWeakPasswordException -> "Senha fraca! Use letras, números e caracteres especiais."
                    is FirebaseAuthInvalidCredentialsException -> "E-mail inválido, tente outro e-mail."
                    else -> "Erro ao cadastrar: ${erro.message}"
                }
                onComplete(false, errorMessage)
            }
    }

    private fun saveUserToFirestore(usuario: Usuario, onComplete: (Boolean, String?) -> Unit) {
        firestore.collection("usuarios")
            .document(usuario.id)
            .set(usuario)
            .addOnSuccessListener {
                onComplete(true, null)
                context.startActivity(Intent(context, MainActivity::class.java))
            }
            .addOnFailureListener { erro ->
                onComplete(false, "Erro ao salvar usuário no Firestore: ${erro.message}")
            }
    }

    fun checkUserLoggedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }

    fun logout() {
        firebaseAuth.signOut()
        val intent = Intent(context, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        context.startActivity(intent)
    }
}