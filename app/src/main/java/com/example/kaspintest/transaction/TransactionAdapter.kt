package com.example.kaspintest.transaction

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

class TransactionAdapter(private val context : Context, val handler : Handler, private val itemData : ArrayList<ItemData>) : RecyclerView.Adapter<TransactionAdapter.ListViewHolder>() {
    var transactionActivity                : TransactionActivity = TransactionActivity()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view : View = LayoutInflater.from(parent.context).inflate(R.layout.rv_item_transaction, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val (name, id, code, stock) = itemData[position]
        holder.tvTransactionName.text  = name
        holder.tvTransactionCode.text  = code

        holder.imgAdd.setOnClickListener {
            val state = holder.imgAdd.isSaveEnabled
            if(state){
                holder.imgAdd.isSaveEnabled = false
                holder.imgAdd.setImageResource(R.drawable.ic_close)
                val message = handler.obtainMessage(transactionActivity.MESSAGE_ADD)
                val bundle = Bundle()
                bundle.putString(transactionActivity.msgName, name)
                message.data = bundle
                handler.sendMessage(message)
            }
            else{
                holder.imgAdd.isSaveEnabled = true
                holder.imgAdd.setImageResource(R.drawable.ic_chart)
                val message = handler.obtainMessage(transactionActivity.MESSAGE_DEL)
                val bundle = Bundle()
                bundle.putString(transactionActivity.msgName, name)
                message.data = bundle
                handler.sendMessage(message)
            }
        }
    }

    override fun getItemCount(): Int = itemData.size

    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvTransactionName : TextView = itemView.findViewById(R.id.tvTransactionName)
        var tvTransactionCode : TextView = itemView.findViewById(R.id.tvTransactionCode)
        var imgAdd     : ImageView = itemView.findViewById(R.id.imgAdd)
    }
}