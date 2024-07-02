package com.projetosip.ipbank.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.projetosip.ipbank.databinding.ItemContatosBinding

class ContatosAdapter : Adapter<ContatosAdapter.ContatosViewHolder>(){

    inner class ContatosViewHolder(
        private val binding: ItemContatosBinding
    ) : ViewHolder( binding.root){
        fun  bind(){

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContatosViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = ItemContatosBinding.inflate(
            inflater, parent, false
        )
        return ContatosViewHolder( itemView )
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: ContatosViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

}