package com.projetosip.ipbank.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.projetosip.ipbank.data.model.Usuario
import com.projetosip.ipbank.databinding.FragmentTransferenciaPixBinding
import com.projetosip.ipbank.ui.activity.utils.obterDataAtual

@Suppress("DEPRECATION")
class TransferenciaPixFragment : Fragment() {

    private lateinit var binding: FragmentTransferenciaPixBinding
    private val firestore by lazy {
        FirebaseFirestore.getInstance()
    }
    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentTransferenciaPixBinding.inflate(
            inflater, container, false
        )
        inicializarEventosClique()
        configurarRecebimentoDados()
        return binding.root
    }

    private fun inicializarEventosClique() {
        binding.ActionButtonPix.setOnClickListener {
            realizarTransferencia()
        }
    }

    private fun configurarRecebimentoDados() {
        parentFragmentManager.setFragmentResultListener("dadosDestinatario", this) { _, bundle ->
            val usuario = bundle.getParcelable<Usuario>("usuario")
            if (usuario != null) {
                binding.textInputChavePix.setText(usuario.email)
                binding.textInputNome.setText(usuario.nome)
            }
        }
    }

    fun receberDadosDestinatario(usuario: Usuario) {
        binding.textInputChavePix.setText(usuario.email)
        binding.textInputNome.setText(usuario.nome)
    }

    private fun realizarTransferencia() {
        val email = binding.textInputChavePix.text.toString()
        val nome = binding.textInputNome.text.toString()
        val valor = binding.textInputValorPix.text.toString().toDoubleOrNull()

        if (email.isNotEmpty() && valor != null) {
            firestore.collection("usuarios")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (querySnapshot.isEmpty) {
                        exibirMensagem("E-mail não encontrado no banco de dados.")
                    } else {
                        val usuarioDestino = querySnapshot.documents[0].toObject(Usuario::class.java)
                        verificarSaldoETransferir(usuarioDestino, valor)
                    }
                }
                .addOnFailureListener {
                    exibirMensagem("Erro ao buscar e-mail no banco de dados.")
                }
        } else {
            exibirMensagem("Preencha todos os campos corretamente.")
        }
    }

    private fun verificarSaldoETransferir(usuarioDestino: Usuario?, valor: Double) {
        val usuarioAtual = firebaseAuth.currentUser?.uid
        if (usuarioAtual != null && usuarioDestino != null) {
            firestore.collection("usuarios").document(usuarioAtual).get()
                .addOnSuccessListener { documentSnapshot ->
                    val usuarioOrigem = documentSnapshot.toObject(Usuario::class.java)
                    if (usuarioOrigem != null && usuarioOrigem.saldo >= valor) {
                        val novoSaldoOrigem = usuarioOrigem.saldo - valor
                        val novoSaldoDestino = usuarioDestino.saldo + valor

                        firestore.runBatch { batch ->
                            batch.update(firestore.collection("usuarios")
                                .document(usuarioAtual), "saldo", novoSaldoOrigem, "transacao", valor, "dataTransacao", obterDataAtual())
                            batch.update(firestore.collection("usuarios")
                                .document(usuarioDestino.id), "saldo", novoSaldoDestino)
                        }.addOnSuccessListener {
                            exibirMensagem("Transferência realizada com sucesso.")
                        }.addOnFailureListener {
                            exibirMensagem("Erro ao realizar a transferência.")
                        }
                    } else {
                        exibirMensagem("Saldo insuficiente para a transferência.")
                    }
                }
        }
    }

    private fun exibirMensagem(mensagem: String) {
        Toast.makeText(requireContext(), mensagem, Toast.LENGTH_SHORT).show()
    }
}