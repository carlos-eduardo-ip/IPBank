package com.projetosip.ipbank.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.projetosip.ipbank.R
import com.projetosip.ipbank.data.model.Usuario
import com.projetosip.ipbank.databinding.FragmentContatosBinding
import com.projetosip.ipbank.ui.activity.TransferirActivity
import com.projetosip.ipbank.ui.activity.utils.Constantes
import com.projetosip.ipbank.ui.adapter.ContatosAdapter

class ContatosFragment : Fragment() {

    private lateinit var binding: FragmentContatosBinding
    private lateinit var eventoSnapshot: ListenerRegistration
    private lateinit var contatosAdapter: ContatosAdapter

    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    private val firestore by lazy {
        FirebaseFirestore.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentContatosBinding.inflate(
            inflater, container, false
        )

        contatosAdapter = ContatosAdapter { usuario ->
            // Envia os dados do contato para o TransferenciaPixFragment
            parentFragmentManager.setFragmentResult("dadosDestinatario", Bundle().apply {
                putParcelable("usuario", usuario)
            })

            // Muda para a aba de transferência PIX
            (activity as TransferirActivity).binding.viewPagerTransferir.currentItem = 0
        }

        binding.rvContatos.adapter = contatosAdapter
        binding.rvContatos.layoutManager = LinearLayoutManager(context)
        binding.rvContatos.addItemDecoration(
            DividerItemDecoration(
                context, LinearLayoutManager.VERTICAL
            )
        )

        return binding.root
        //return inflater.inflate(R.layout.fragment_contatos, container, false)
    }

    override fun onStart() {
        super.onStart()
        adicionarListenerContatos()
    }

    private fun adicionarListenerContatos() {
        eventoSnapshot = firestore.collection("usuarios").addSnapshotListener { querySnapshot, _ ->
            val listaContatos = mutableListOf<Usuario>()
            val documentos = querySnapshot?.documents
            documentos?.forEach { documentSnapshot ->
                val idUsuarioLogado = firebaseAuth.currentUser?.uid
                val usuario = documentSnapshot.toObject(Usuario::class.java)
                if (usuario != null && idUsuarioLogado != null) {
                    if (idUsuarioLogado != usuario.id) {
                        listaContatos.add(usuario)
                    }
                }
            }
            contatosAdapter.adicionarLista(listaContatos)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        eventoSnapshot.remove()
    }

}