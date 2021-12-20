package com.example.kaspintest.confirm

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kaspintest.R
import com.example.kaspintest.database.FirebaseDB
import com.example.kaspintest.database.LocalDB
import com.example.kaspintest.dataparcel.ItemData
import com.example.kaspintest.success.SuccessActivity
import com.example.kaspintest.transaction.TransactionActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.lang.Exception

class ConfirmActivity : AppCompatActivity() {
    val transactionActivity                : TransactionActivity = TransactionActivity()

    private val firebaseDB = FirebaseDB()

    var localDB  : LocalDB? = null
    private lateinit var rvConfirm : RecyclerView
    private lateinit var btSubmit : Button
    private lateinit var btSaveOrder : Button
    private lateinit var imgCancel : ImageView

    private val listItemConfirm = ArrayList<String>()
    private val listIdConfirm = ArrayList<String>()
    private val listCodeConfirm = ArrayList<String>()
    private val listStockConfirm = ArrayList<String>()
    private val listNum = ArrayList<Int>()

    private val list = ArrayList<ItemData>()

    val MESSAGE_PLUSMIN : Int = 0
    val MESSAGE_DEL : Int = 1

    val msgName : String = "keyName"
    val msgNow : String = "keyNow"

    private var totalItem : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm)
        localDB = LocalDB(this@ConfirmActivity)
        rvConfirm = findViewById(R.id.rvConfirm)
        btSubmit = findViewById(R.id.btSubmit)
        btSaveOrder = findViewById(R.id.btSaveOrder)
        imgCancel = findViewById(R.id.imgCancel)

        totalItem = intent.getIntExtra(transactionActivity.keyTotal, 0)
        if(totalItem > 0){
            for(i in 0..totalItem - 1){
                listNum.add(1)
            }
            for (i in 0..totalItem - 1){
                val keyItem = transactionActivity.keyItem + i.toString()
                val nameItem = intent.getStringExtra(keyItem).toString()
                listItemConfirm.add(nameItem)
            }
        }

        list.addAll(readDB())
        showRecyclerList()

        imgCancel.setOnClickListener {
            val intent = Intent(this@ConfirmActivity, TransactionActivity::class.java)
            startActivity(intent)
            finish()
        }

        btSaveOrder.setOnClickListener {
            val orderNum = firebaseDB.readOrder()

            Toast.makeText(this@ConfirmActivity, orderNum, Toast.LENGTH_SHORT).show()
//            val setOrder = "Order " + orderNum.toString()
//            if(firebaseDB.writeOrder("setOrder", "Rujak", "1", "A1", "10")){
//                Toast.makeText(this@ConfirmActivity, "Sukses", Toast.LENGTH_SHORT).show()
//            }
//            else{
//                Toast.makeText(this@ConfirmActivity, "Gagal", Toast.LENGTH_SHORT).show()
//            }
        }

        btSubmit.setOnClickListener {
            submit()
        }
    }

    private fun showRecyclerList(){
        rvConfirm.setHasFixedSize(true)
        rvConfirm.layoutManager = LinearLayoutManager(this)
        val confirmAdapter = ConfirmAdapter(this, handler, list)
        rvConfirm.adapter = confirmAdapter
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

                val totalItem = listItemConfirm.size
                for (i in 0..totalItem - 1){
                    if(dbName.equals(listItemConfirm.get(i))){
                        val itemData = ItemData(dbName, dbId, dbCode, dbStock)
                        dataList.add(itemData)

                        listIdConfirm.add(dbId)
                        listCodeConfirm.add(dbCode)
                        listStockConfirm.add(dbStock)
                    }
                }
            }
        }
        else{
            Toast.makeText(this@ConfirmActivity, "Tidak ada data barang", Toast.LENGTH_SHORT).show()
        }
        return dataList
    }

    private val handler = Handler { message ->
        when(message.what){
            MESSAGE_PLUSMIN     -> {
                val msg1 = message.data.getString(msgName).toString()
                val msg2 = message.data.getInt(msgNow)
                val indexAdd = listItemConfirm.indexOf(msg1)
                listNum.set(indexAdd, msg2)
            }
            MESSAGE_DEL    -> {
                val msg1 = message.data.getString(msgName).toString()
                val indexDelete = listItemConfirm.indexOf(msg1)
                listItemConfirm.removeAt(indexDelete)
                listIdConfirm.removeAt(indexDelete)
                listCodeConfirm.removeAt(indexDelete)
                listStockConfirm.removeAt(indexDelete)
                listNum.removeAt(indexDelete)
                list.clear()
                list.addAll(readDB())
                showRecyclerList()
                Toast.makeText(this@ConfirmActivity, "Berhasil menghapus barang", Toast.LENGTH_SHORT).show()
            }
        }
        false
    }

    private fun submit(){
        val total = listItemConfirm.size
        var stateSuccess = false
        for(i in 0..total - 1){
            try {
                try {
                    val nameItem = listItemConfirm.get(i)
                    val stockItem = Integer.parseInt(listStockConfirm.get(i))
                    val orderItem = listNum.get(i)
                    localDB!!.updateItem(nameItem, (stockItem - orderItem).toString())
                    stateSuccess = true
                }catch (e : Exception){
                    Log.e("process", listItemConfirm.get(i) + " " + listStockConfirm.get(i) + " " + listNum.get(i).toString())
                    Toast.makeText(this@ConfirmActivity, "Transaksi gagal", Toast.LENGTH_SHORT).show()
                    stateSuccess = false;
                }
            }catch (e : Exception){
                Log.e("process", listItemConfirm.get(i) + " " + listStockConfirm.get(i) + " " + listNum.get(i).toString())
                Toast.makeText(this@ConfirmActivity, "Transaksi gagal", Toast.LENGTH_SHORT).show()
                stateSuccess = false;
            }
        }
        if(stateSuccess){
            val intent = Intent(this@ConfirmActivity, SuccessActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}