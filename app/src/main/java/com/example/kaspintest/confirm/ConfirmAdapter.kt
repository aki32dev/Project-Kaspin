package com.example.kaspintest.confirm

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.kaspintest.R
import com.example.kaspintest.dataparcel.ItemData
import java.lang.Exception

class ConfirmAdapter(private val context : Context, val handler : Handler, private val itemData : ArrayList<ItemData>, private val num : ArrayList<Int>) : RecyclerView.Adapter<ConfirmAdapter.ListViewHolder>() {
    val confirmActivity                 : ConfirmActivity = ConfirmActivity()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view : View = LayoutInflater.from(parent.context).inflate(R.layout.rv_item_confirm, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val (name, id, code, stock) = itemData[position]
        holder.tvConfirmName.text  = name
        holder.tvConfirmCode.text  = code
        holder.tvConfirm.text = num.get(position).toString()

        var orderStock : Int = 0

        holder.imgConfirmDelete.setOnClickListener {
            if(stock != null){
                try{
                    orderStock = Integer.parseInt(stock)
                }catch (e : Exception){
                    Log.e("strtoint", stock)
                }
            }

            val message = handler.obtainMessage(confirmActivity.MESSAGE_DEL)
            val bundle = Bundle()
            bundle.putString(confirmActivity.msgName, name)
            bundle.putInt(confirmActivity.msgNow, num.get(position))
            message.data = bundle
            handler.sendMessage(message)
        }

        holder.imgPlus.setOnClickListener {
            try{
                orderStock = Integer.parseInt(stock)
            }catch (e : Exception){
                if (stock != null) {
                    Log.e("strtoint", stock)
                }
            }

            if(num.get(position) == orderStock){
                Toast.makeText(context, "Stok tersedia hanya " + stock, Toast.LENGTH_SHORT).show()
            }
            else{
                num.set(position, num.get(position) + 1)
                holder.tvConfirm.setText(num.get(position).toString())
                val message = handler.obtainMessage(confirmActivity.MESSAGE_PLUSMIN)
                val bundle = Bundle()
                bundle.putString(confirmActivity.msgName, name)
                bundle.putInt(confirmActivity.msgNow, num.get(position))
                message.data = bundle
                handler.sendMessage(message)
            }
        }

        holder.imgMin.setOnClickListener {
            if(num.get(position) > 1){
                num.set(position, num.get(position) - 1)
                holder.tvConfirm.setText(num.get(position).toString())
                val message = handler.obtainMessage(confirmActivity.MESSAGE_PLUSMIN)
                val bundle = Bundle()
                bundle.putString(confirmActivity.msgName, name)
                bundle.putInt(confirmActivity.msgNow, num.get(position))
                message.data = bundle
                handler.sendMessage(message)
            }
        }
    }

    override fun getItemCount(): Int = itemData.size

    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvConfirmName : TextView = itemView.findViewById(R.id.tvConfirmName)
        var tvConfirmCode : TextView = itemView.findViewById(R.id.tvConfirmCode)
        var tvConfirm : TextView = itemView.findViewById(R.id.tvConfirm)
        var imgPlus     : ImageView = itemView.findViewById(R.id.imgPlus)
        var imgMin     : ImageView = itemView.findViewById(R.id.imgMin)
        var imgConfirmDelete     : ImageView = itemView.findViewById(R.id.imgConfirmDelete)
    }
}