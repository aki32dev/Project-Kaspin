package com.example.kaspintest.order

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
import com.example.kaspintest.dataparcel.OrderData

class OrderAdapter(private val context : Context, val handler : Handler, private val orderData : ArrayList<OrderData>) : RecyclerView.Adapter<OrderAdapter.ListViewHolder>() {
    var orderActivity                   : OrderActivity = OrderActivity()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view : View = LayoutInflater.from(parent.context).inflate(R.layout.rv_item_order, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val (name, code) = orderData[position]
        holder.tvOrderName.text  = name
        holder.tvOrderCode.text  = code

        holder.imgOrderDelete.setOnClickListener {
            val message = handler.obtainMessage(orderActivity.MESSAGE_DEL)
            val bundle = Bundle()
            bundle.putString(orderActivity.msgName, name)
            message.data = bundle
            handler.sendMessage(message)
        }

        holder.imgLoad.setOnClickListener {
            val message = handler.obtainMessage(orderActivity.MESSAGE_LOAD)
            val bundle = Bundle()
            bundle.putString(orderActivity.msgName, name)
            message.data = bundle
            handler.sendMessage(message)
        }
    }

    override fun getItemCount(): Int = orderData.size

    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvOrderName : TextView = itemView.findViewById(R.id.tvOrderName)
        var tvOrderCode : TextView = itemView.findViewById(R.id.tvOrderCode)
        var imgOrderDelete     : ImageView = itemView.findViewById(R.id.imgOrderDelete)
        var imgLoad     : ImageView = itemView.findViewById(R.id.imgLoad)
    }
}