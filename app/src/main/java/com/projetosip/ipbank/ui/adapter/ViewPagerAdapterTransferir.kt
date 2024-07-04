package com.projetosip.ipbank.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.projetosip.ipbank.ui.fragment.ContatosFragment
import com.projetosip.ipbank.ui.fragment.TransferenciaPixFragment

class ViewPagerAdapterTransferir(
    fragmentActivity: FragmentActivity
) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int {
        return 2 // Número de tabs/fragments
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> TransferenciaPixFragment()
            1 -> ContatosFragment()
            else -> throw IllegalArgumentException("Posição inválida")
        }
    }
}