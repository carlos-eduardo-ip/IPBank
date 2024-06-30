package com.projetosip.ipbank.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.projetosip.ipbank.ui.fragment.ConfiguracoesFragment
import com.projetosip.ipbank.ui.fragment.TransacoesFragment

class ViewPagerAdapter(
    private val abas: List<String>,
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle
) : FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int = abas.size

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            1 -> ConfiguracoesFragment()
            else -> TransacoesFragment()
        }
    }
}