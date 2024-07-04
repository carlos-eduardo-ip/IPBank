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
import com.projetosip.ipbank.R
import com.projetosip.ipbank.databinding.ActivitySobreBinding
import com.projetosip.ipbank.ui.viewmodel.AuthViewModel
import com.projetosip.ipbank.ui.viewmodel.factory.AuthViewModelFactory

class SobreActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivitySobreBinding.inflate( layoutInflater )
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
        inicializarToolbar()
    }

    private fun inicializarToolbar() {
        val toolbar = binding.includeToolbarSobre.tbPrincipal
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = "Sobre"
            setDisplayHomeAsUpEnabled(true)
        }

        addMenuProvider(
            object : MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.menu_principal, menu)
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                    when( menuItem.itemId ){
                        R.id.item_sobre ->{
                            Intent(this@SobreActivity, SobreActivity::class.java)
                        }
                        R.id.item_sair -> {
                            deslogarUsuario()
                        }
                    }
                    return true
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