package com.example.kaspintest.item

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kaspintest.R
import com.example.kaspintest.dataparcel.ItemData

class ItemAdapter(private val context : Context, val handler : Handler, private val itemData : ArrayList<ItemData>) : RecyclerView.Adapter<ItemAdapter.ListViewHolder>() {
    val itemActivity                : ItemActivity      = ItemActivity()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view : View = LayoutInflater.from(parent.context).inflate(R.layout.rv_item_stock, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val (name, id, code, stock) = itemData[position]
        holder.tvStockName.text  = name
        holder.tvStockCode.text  = code
        holder.tvStock.text      = stock

        holder.imgEdit.setOnClickListener {
            val message = handler.obtainMessage(itemActivity.MESSAGE_EDIT)
            val bundle = Bundle()
            bundle.putString(itemActivity.msgName, name)
            bundle.putString(itemActivity.msgStock, stock)
            message.data = bundle
            handler.sendMessage(message)
        }

        holder.imgDelete.setOnClickListener {
            val message = handler.obtainMessage(itemActivity.MESSAGE_DEL)
            val bundle = Bundle()
            bundle.putString(itemActivity.msgName, name)
            message.data = bundle
            handler.sendMessage(message)
        }
    }

    override fun getItemCount(): Int = itemData.size

    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvStockName : TextView  = itemView.findViewById(R.id.tvStockName)
        var tvStockCode : TextView  = itemView.findViewById(R.id.tvStockCode)
        var tvStock     : TextView  = itemView.findViewById(R.id.tvStock)
        var imgEdit     : ImageView = itemView.findViewById(R.id.imgEdit)
        var imgDelete   : ImageView = itemView.findViewById(R.id.imgDelete)
    }
}