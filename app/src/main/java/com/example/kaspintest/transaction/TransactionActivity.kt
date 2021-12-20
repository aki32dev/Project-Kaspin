package com.example.kaspintest.transaction

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kaspintest.R
import com.example.kaspintest.confirm.ConfirmActivity
import com.example.kaspintest.database.LocalDB
import com.example.kaspintest.dataparcel.ItemData
import com.example.kaspintest.item.ItemAdapter

class TransactionActivity : AppCompatActivity() {
    var localDB  : LocalDB? = null
    private lateinit var rvTransaction : RecyclerView
    private lateinit var btCheckout : Button
    private val listTrans = ArrayList<String>()
    private val list = ArrayList<ItemData>()

    val MESSAGE_ADD : Int = 0
    val MESSAGE_DEL : Int = 1

    val msgName : String = "keyName"
    val keyItem : String = "keyItem"
    val keyTotal : String = "keyTotal"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction)
        localDB = LocalDB(this@TransactionActivity)
        rvTransaction = findViewById(R.id.rvTransaction)
        btCheckout = findViewById(R.id.btCheckout)

        list.addAll(readDB())
        showRecyclerList()

        btCheckout.setOnClickListener {
            val total = listTrans.size
            val intent = Intent(this@TransactionActivity, ConfirmActivity::class.java)
            if(total > 0){
                intent.putExtra(keyTotal, total)

                for (i in 0..total - 1){
                    val key = "keyItem" + i.toString()
                    intent.putExtra(key, listTrans.get(i))
                }
                startActivity(intent)
                finish()
            }
            else{
                Toast.makeText(this@TransactionActivity, "Silakan pilih barang", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showRecyclerList(){
        rvTransaction.setHasFixedSize(true)
        rvTransaction.layoutManager = LinearLayoutManager(this)
        val transactionAdapter = TransactionAdapter(this, handler, list)
        rvTransaction.adapter = transactionAdapter
    }

    private fun readDB() : ArrayList<ItemData>{
        val res = localDB!!.getItem()
        val dataList = ArrayList<ItemData>()
        if(res.count > 0){
            while (res.moveToNext()) {
                val dbName = res.getString(0)
                val dbId = res.getString(1)
                val dbCode = res.getString(2)
                val dbStock = res.getString(3)

                val itemData = ItemData(dbName, dbId, dbCode, dbStock)
                dataList.add(itemData)
            }
        }
        else{
            Toast.makeText(this@TransactionActivity, "Tidak ada data barang", Toast.LENGTH_SHORT).show()
        }
        return dataList
    }

    private val handler = Handler { message ->
        when(message.what){
            MESSAGE_ADD     -> {
                val msg1 = message.data.getString(msgName).toString()
                listTrans.add(msg1)

                val total = listTrans.count()
                if(total > 0){
                    val sTotal = "Checkout(" + total.toString() + ")"
                    btCheckout.setText(sTotal)
                }
                else{
                    btCheckout.setText(R.string.checkout)
                }
            }
            MESSAGE_DEL    -> {
                val msg1 = message.data.getString(msgName).toString()
                listTrans.remove(msg1)

                val total = listTrans.size
                if(total > 0){
                    val sTotal = "Checkout(" +total.toString() + ")"
                    btCheckout.setText(sTotal)
                }
                else{
                    btCheckout.setText(R.string.checkout)
                }
            }
        }
        false
    }
}