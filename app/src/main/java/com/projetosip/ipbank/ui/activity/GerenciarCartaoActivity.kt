package com.projetosip.ipbank.ui.activity

import android.app.AlertDialog
import android.content.Intent
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
import com.google.firebase.auth.FirebaseAuth
import com.projetosip.ipbank.R
import com.projetosip.ipbank.databinding.ActivityGerenciarCartaoBinding
import com.projetosip.ipbank.ui.adapter.ViewPagerAdapter
import com.projetosip.ipbank.ui.viewmodel.AuthViewModel
import com.projetosip.ipbank.ui.viewmodel.factory.AuthViewModelFactory

class GerenciarCartaoActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityGerenciarCartaoBinding.inflate( layoutInflater )
    }

    private val authViewModel: AuthViewModel by viewModels { AuthViewModelFactory(this) }

    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
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
        inicializarNevevacaoAbas()
    }

    private fun inicializarNevevacaoAbas() {

        val tabLayout = binding.tabLayout
        val viewPage = binding.viewPagerPrincipal
        //Adapter
        val abas = listOf("TRANSAÇÕES", "CONFIGURAÇÕES")
        viewPage.adapter = ViewPagerAdapter(abas, supportFragmentManager, lifecycle)

        tabLayout.isTabIndicatorFullWidth = true
        TabLayoutMediator(tabLayout, viewPage){ aba, posicao ->
            aba.text = abas[posicao]
        }.attach()
    }

    private fun inicializarToolbar() {
        val toolbar = binding.includeGerenciarToolbar.tbPrincipal
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = "Gerenciamento de Cartão"
            setDisplayHomeAsUpEnabled(true)
        }

        addMenuProvider(
            object : MenuProvider{
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.menu_principal, menu)
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                    return when (menuItem.itemId) {
                        R.id.item_sobre -> {
                            startActivity(Intent(this@GerenciarCartaoActivity, SobreActivity::class.java))
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