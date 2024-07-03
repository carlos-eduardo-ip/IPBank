package com.projetosip.ipbank.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.projetosip.ipbank.ui.fragment.ContatosFragment
import com.projetosip.ipbank.ui.fragment.TransferenciaPixFragment

class ViewPagerAdapterTransferir(
    private val abas: List<String>,
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle
) : FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int = abas.size

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            1 -> ContatosFragment()
            else -> TransferenciaPixFragment()
        }
    }
}