package com.projetosip.ipbank.ui.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.MenuProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.projetosip.ipbank.R
import com.projetosip.ipbank.databinding.ActivityPerfilUsuarioBinding
import com.projetosip.ipbank.ui.viewmodel.AuthViewModel
import com.projetosip.ipbank.ui.viewmodel.factory.AuthViewModelFactory
import com.squareup.picasso.Picasso

class PerfilUsuarioActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityPerfilUsuarioBinding.inflate( layoutInflater )
    }

    private val firebaseAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    private val storage by lazy {
        FirebaseStorage.getInstance()
    }
    private val firestore by lazy {
        FirebaseFirestore.getInstance()
    }

    private val authViewModel: AuthViewModel by viewModels { AuthViewModelFactory(this) }

    private var temPermissaoCamera = false
    private var temPermissaoGallery = false

    private val gerenciadorGaleria = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ){uri ->
        if ( uri != null){
            binding.imagePerfil.setImageURI( uri )
            uploadImagemStorage(uri)
        }else{
            exibirMensagem("Nenhuma imagem selecionada!")
        }
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
        inicializarToolbar()
        solicitarPermissoes()
        inicializarEventosClique()
    }

    override fun onStart() {
        super.onStart()
        recuperarDadosIniciaisUsuarios()
    }

    private fun exibirMensagem(mensagem: String) {
        Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show()
    }

    private fun recuperarDadosIniciaisUsuarios() {
        val idUsuario = firebaseAuth.currentUser?.uid
        if (idUsuario != null) {
            firestore
                .collection("usuarios")
                .document(idUsuario)
                .get()
                .addOnSuccessListener {documentSnapshot ->
                    val dadosUsuarios = documentSnapshot.data
                    if (dadosUsuarios != null){
                        val nome = dadosUsuarios["nome"] as String
                        val foto = dadosUsuarios["foto"] as String

                        binding.editNomePerfil.setText(nome)
                        if (foto.isNotEmpty()){
                            Picasso.get()
                                .load(foto)
                                .into(binding.imagePerfil)
                        }
                    }
                }
        }
    }

    private fun uploadImagemStorage(uri: Uri) {
        // fotos -> usuarios -> idUsuario -> perfil.jpg
        val idUsuario = firebaseAuth.currentUser?.uid
        if (idUsuario!= null){
            storage
                .getReference("fotos")
                .child("usuarios")
                .child(idUsuario)
                .child("perfil.jpg")
                .putFile(uri)
                .addOnSuccessListener {task ->
                    exibirMensagem("Sucesso ao fazer upload da imagem.")
                    task.metadata
                        ?.reference
                        ?.downloadUrl
                        ?.addOnSuccessListener {url ->
                            val dados = mapOf(
                                "foto" to url.toString()
                            )
                            atualizarDadosPerfil(idUsuario, dados)
                        }
                }.addOnFailureListener{
                    exibirMensagem("Erro ao fazer upload da imagem.")
                }
        }
    }

    private fun atualizarDadosPerfil(idUsuario: String, dados: Map<String, String>) {
        firestore
            .collection("usuarios")
            .document(idUsuario)
            .update(dados)
            .addOnSuccessListener {
                exibirMensagem("Sucesso ao atualizar perfil")
            }
            .addOnFailureListener{
                exibirMensagem("Erro ao atualizar perfil do usuário")
            }
    }

    private fun inicializarEventosClique() {
        binding.fabSelecionar.setOnClickListener{
            if ( temPermissaoGallery ) {
                gerenciadorGaleria.launch("image/*")
            }else{
                exibirMensagem("Não tem permissão para acessar galeria")
                solicitarPermissoes()
            }
        }
        binding.btnAtualizarPerfil.setOnClickListener{

            val  nomeUsuario = binding.editNomePerfil.text.toString()
            if ( nomeUsuario.isNotEmpty()){
                val idUsuario = firebaseAuth.currentUser?.uid
                if (idUsuario != null) {
                    val dados = mapOf(
                        "nome" to nomeUsuario
                    )
                    atualizarDadosPerfil(idUsuario, dados)
                }
            }else{
                exibirMensagem("Preencha o nome para atualizar")
            }
        }
    }

    private fun solicitarPermissoes() {
        //Verificar se o usuário já tem permissão
        temPermissaoCamera = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        temPermissaoGallery = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_MEDIA_IMAGES
        ) == PackageManager.PERMISSION_GRANTED

        //Lista de permissões negadas
        val listaPermissoesNegadas = mutableListOf<String>()
        if ( !temPermissaoCamera ){
            listaPermissoesNegadas.add( Manifest.permission.CAMERA)
        }
        if ( !temPermissaoGallery ) {
            listaPermissoesNegadas.add(Manifest.permission.READ_MEDIA_IMAGES)
        }

        if ( listaPermissoesNegadas.isNotEmpty() ) {
            //Solicitar multiplas permissões
            val gerenciadorPermissoes = registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ) { permissoes ->
                temPermissaoCamera = permissoes[Manifest.permission.CAMERA] ?: temPermissaoCamera
                temPermissaoGallery =
                    permissoes[Manifest.permission.READ_MEDIA_IMAGES] ?: temPermissaoGallery
            }
            gerenciadorPermissoes.launch(listaPermissoesNegadas.toTypedArray())
        }
    }

    private fun inicializarToolbar() {
        val toolbar = binding.includeMainToolbar.tbPrincipal
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = "Editar Perfil"
            setDisplayHomeAsUpEnabled(true)
        }

        addMenuProvider(
            object : MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.menu_principal, menu)
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                    return when (menuItem.itemId) {
                        R.id.item_sobre -> {
                            startActivity(Intent(this@PerfilUsuarioActivity, SobreActivity::class.java))
                            true
                        }
                        R.id.item_sair -> {
                            deslogarUsuario()
                            true
                        }
                        else -> false
                    }
                }
            }
        )
    }

    private fun deslogarUsuario() {
        android.app.AlertDialog.Builder(this)
            .setTitle("Deslogar")
            .setMessage("Deseja realmente sair?")
            .setNegativeButton("Não") { dialog, _ -> dialog.dismiss() }
            .setPositiveButton("Sim") { _, _ ->
                authViewModel.logout()
            }
            .create()
            .show()
    }
}
