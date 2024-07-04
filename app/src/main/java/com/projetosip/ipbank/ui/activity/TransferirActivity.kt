package com.projetosip.ipbank.ui.activity

import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.tabs.TabLayoutMediator
import com.projetosip.ipbank.R
import com.projetosip.ipbank.data.model.Usuario
import com.projetosip.ipbank.databinding.ActivityTransferirBinding
import com.projetosip.ipbank.ui.activity.utils.Constantes
import com.projetosip.ipbank.ui.adapter.ViewPagerAdapterTransferir
import com.projetosip.ipbank.ui.fragment.TransferenciaPixFragment
import com.projetosip.ipbank.ui.viewmodel.AuthViewModel
import com.projetosip.ipbank.ui.viewmodel.factory.AuthViewModelFactory

class TransferirActivity : AppCompatActivity() {

    val binding by lazy {
        ActivityTransferirBinding.inflate( layoutInflater )
    }
    private var dadosDestinatario: Usuario? = null

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

        inicializarToolbar()
        inicializarNevevacaoAbas()
        recuperarDadosUsuarioDestinatario()
    }

    private fun recuperarDadosUsuarioDestinatario() {
        val extras = intent.extras
        if (extras != null) {
            val origem = extras.getString("origem")
            if (origem == Constantes.ORIGEM_CONTATO) {
                dadosDestinatario = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    extras.getParcelable("dadosDestinatario", Usuario::class.java)
                } else {
                    extras.getParcelable("dadosDestinatario")
                }

                // Envia os dados do destinatário para o TransferenciaPixFragment
                dadosDestinatario?.let { usuario ->
                    val fragment = supportFragmentManager.findFragmentByTag("f0") as? TransferenciaPixFragment
                    fragment?.receberDadosDestinatario(usuario)
                }
            }
        }
    }

    private fun inicializarNevevacaoAbas() {
        val tabLayout = binding.tabLayoutTransferir
        val viewPager = binding.viewPagerTransferir

        viewPager.adapter = ViewPagerAdapterTransferir(this)

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "PIX"
                1 -> "CONTATOS"
                else -> null
            }
        }.attach()
    }

    private fun inicializarToolbar() {
        val toolbar = binding.includeTransferirToolbar.tbPrincipal
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = "Transfêrencia Pix"
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
                            startActivity(Intent(this@TransferirActivity, SobreActivity::class.java))
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun deslogarUsuario() {
        AlertDialog.Builder(this)
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