package com.example.gptchat

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.gptchat.databinding.ItemChatBinding

class ChatAdapter(private val data: MutableList<ChatData> = mutableListOf()) :
    RecyclerView.Adapter<ChatAdapter.VH>() {


    class VH(val binding: ItemChatBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(ItemChatBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val chatData = data[position]
        Log.d("TAG", "onBindViewHolder: ")
        holder.binding.apply {
            if (chatData.type == 0) {
                left.visibility = View.VISIBLE
                right.visibility = View.GONE
                tvMessage0.text = chatData.text
            } else {
                left.visibility = View.GONE
                right.visibility = View.VISIBLE
                tvMessage1.text = chatData.text
            }
        }
    }

    fun addData(newData: ChatData) {
        data.add(newData)
        notifyDataSetChanged()
    }

    override fun getItemCount() = data.size
}