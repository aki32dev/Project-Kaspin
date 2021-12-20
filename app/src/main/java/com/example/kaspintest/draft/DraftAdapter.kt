package com.example.kaspintest.draft

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

class DraftAdapter(private val context : Context, val handler : Handler, private val orderData : ArrayList<OrderData>) : RecyclerView.Adapter<DraftAdapter.ListViewHolder>() {
    var draftActivity                   : DraftActivity = DraftActivity()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view : View = LayoutInflater.from(parent.context).inflate(R.layout.rv_item_draft, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val (name, code) = orderData[position]
        holder.tvDraftName.text  = name
        holder.tvDraftCode.text  = code

        holder.imgDraftDelete.setOnClickListener {
            val message = handler.obtainMessage(draftActivity.MESSAGE_DEL)
            val bundle = Bundle()
            bundle.putString(draftActivity.msgName, name)
            message.data = bundle
            handler.sendMessage(message)
        }

        holder.imgDraftLoad.setOnClickListener {
            val message = handler.obtainMessage(draftActivity.MESSAGE_LOAD)
            val bundle = Bundle()
            bundle.putString(draftActivity.msgName, name)
            message.data = bundle
            handler.sendMessage(message)
        }
    }

    override fun getItemCount(): Int = orderData.size

    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvDraftName : TextView = itemView.findViewById(R.id.tvOrderNameDraft)
        var tvDraftCode : TextView = itemView.findViewById(R.id.tvOrderCodeDraft)
        var imgDraftDelete     : ImageView = itemView.findViewById(R.id.imgOrderDeleteDraft)
        var imgDraftLoad     : ImageView = itemView.findViewById(R.id.imgLoadDraft)
    }
}