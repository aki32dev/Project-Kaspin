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
import com.example.kaspintest.database.LocalDB
import com.example.kaspintest.dataparcel.ItemData
import com.example.kaspintest.order.OrderActivity
import com.example.kaspintest.success.SuccessActivity
import com.example.kaspintest.transaction.TransactionActivity
import com.google.firebase.database.*
import java.lang.Exception
import java.net.InetAddress
import java.net.UnknownHostException
import java.util.*
import java.util.concurrent.*
import kotlin.collections.ArrayList

class ConfirmActivity : AppCompatActivity() {
    val transactionActivity         : TransactionActivity = TransactionActivity()
    val orderActivity               : OrderActivity = OrderActivity()

    private lateinit var database   : FirebaseDatabase
    private lateinit var ref        : DatabaseReference

    private val parentReference     = "kaspin"
    private val childList           = "orderlist"
    private val childDraft          = "draftlist"

    private val childId             = "id"
    private val childCode           = "code"
    private val childStock          = "stock"
    private val childOrderCode      = "ordercode"
    private val childNum            = "num"

    var localDB                     : LocalDB? = null
    private lateinit var rvConfirm  : RecyclerView
    private lateinit var btSubmit   : Button
    private lateinit var btSaveOrder: Button
    private lateinit var imgCancel  : ImageView

    private val listItemConfirm     = ArrayList<String>()
    private val listIdConfirm       = ArrayList<String>()
    private val listCodeConfirm     = ArrayList<String>()
    private val listStockConfirm    = ArrayList<String>()
    private val listNum             = ArrayList<Int>()

    private val list                = ArrayList<ItemData>()

    val MESSAGE_PLUSMIN             : Int = 0
    val MESSAGE_DEL                 : Int = 1

    val msgName                     : String = "keyName"
    val msgNow                      : String = "keyNow"

    private var totalItem           : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm)
        localDB = LocalDB(this@ConfirmActivity)
        rvConfirm = findViewById(R.id.rvConfirm)
        btSubmit = findViewById(R.id.btSubmit)
        btSaveOrder = findViewById(R.id.btSaveOrder)
        imgCancel = findViewById(R.id.imgCancel)
        database = FirebaseDatabase.getInstance()

        val load = intent.getStringExtra(transactionActivity.keyDB)
        if(load.equals("FIREBASE")){
            val orderName = intent.getStringExtra(orderActivity.keyName).toString()
            val dataList = ArrayList<ItemData>()
            if(isOnline()){
                ref = database.getReference(parentReference).child(childList).child(orderName)
                ref.get().addOnSuccessListener {
                    for (ds in it.children) {
                        if(ds.hasChildren()){
                            val itemName = ds.key.toString()
                            val itemId = ds.child(childId).getValue().toString()
                            val itemCode = ds.child(childCode).getValue().toString()
                            val itemStock = ds.child(childStock).getValue().toString()
                            val itemNum = Integer.parseInt(ds.child(childNum).getValue().toString())

                            listItemConfirm.add(itemName)
                            listIdConfirm.add(itemId)
                            listCodeConfirm.add(itemCode)
                            listStockConfirm.add(itemStock)
                            listNum.add(itemNum)

                            val itemData = ItemData(itemName, itemId, itemCode, itemStock)
                            dataList.add(itemData)
                        }
                    }
                    list.addAll(dataList)
                    showRecyclerList()
                    Toast.makeText(this@ConfirmActivity, "Sukses memuat order", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener{
                    Toast.makeText(this@ConfirmActivity, "Gagal memuat order", Toast.LENGTH_SHORT).show()
                }
            }
            else{
                Toast.makeText(this@ConfirmActivity, "Tidak ada koneksi internet", Toast.LENGTH_SHORT).show()
            }
        }
        else{
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
                list.addAll(readDB())
                showRecyclerList()
            }
        }

        imgCancel.setOnClickListener {
            val intent = Intent(this@ConfirmActivity, TransactionActivity::class.java)
            startActivity(intent)
            finish()
        }

        btSaveOrder.setOnClickListener {
            if(isOnline()){
                val loadS = intent.getStringExtra(transactionActivity.keyDB)

                if(loadS.equals("FIREBASE")){
                    ref = database.getReference(parentReference).child(childList)
                    ref.get().addOnSuccessListener {
                        val orderNum = intent.getStringExtra(orderActivity.keyName).toString()
                        val orderCode = UUID.randomUUID().toString().take(8).uppercase()
                        val total = listItemConfirm.size
                        for(i in 0..total - 1){
                            writeOrder(orderNum, orderCode, listItemConfirm.get(i), listIdConfirm.get(i), listCodeConfirm.get(i), listStockConfirm.get(i), listNum.get(i).toString())
                        }
                        Toast.makeText(this@ConfirmActivity, "Sukses memperbarui order", Toast.LENGTH_SHORT).show()
                    }.addOnFailureListener{
                        Toast.makeText(this@ConfirmActivity, "Gagal memperbarui order", Toast.LENGTH_SHORT).show()
                    }
                }
                else{
                    ref = database.getReference(parentReference).child(childList)
                    ref.get().addOnSuccessListener {
                        val orderNum = "Order " + (it.childrenCount + 1).toString()
                        val orderCode = UUID.randomUUID().toString().take(8).uppercase()
                        val total = listItemConfirm.size
                        for(i in 0..total - 1){
                            writeOrder(orderNum, orderCode, listItemConfirm.get(i), listIdConfirm.get(i), listCodeConfirm.get(i), listStockConfirm.get(i), listNum.get(i).toString())
                        }
                        Toast.makeText(this@ConfirmActivity, "Sukses menyimpan order", Toast.LENGTH_SHORT).show()
                    }.addOnFailureListener{
                        Toast.makeText(this@ConfirmActivity, "Gagal menyimpan order", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            else{
                Toast.makeText(this@ConfirmActivity, "Tidak ada koneksi internet", Toast.LENGTH_SHORT).show()
            }
        }

        btSubmit.setOnClickListener {
            submit()
        }
    }

    private fun showRecyclerList(){
        rvConfirm.setHasFixedSize(true)
        rvConfirm.layoutManager = LinearLayoutManager(this)
        val confirmAdapter = ConfirmAdapter(this, handler, list, listNum)
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
            ref = database.getReference(parentReference).child(childDraft)
            ref.get().addOnSuccessListener {
                val draftNum = "Draft " + (it.childrenCount + 1).toString()
                val draftCode = UUID.randomUUID().toString().take(8).uppercase()
                val totalDraft = listItemConfirm.size
                for(i in 0..totalDraft - 1){
                    writeDraft(draftNum, draftCode, listItemConfirm.get(i), listIdConfirm.get(i), listCodeConfirm.get(i), listStockConfirm.get(i), listNum.get(i).toString())
                }
            }.addOnFailureListener{}

            val loadD = intent.getStringExtra(transactionActivity.keyDB)
            if(loadD.equals("FIREBASE")){
                val orderDelete = intent.getStringExtra(orderActivity.keyName).toString()
                ref = database.getReference(parentReference).child(childList)
                ref.child(orderDelete).removeValue()
            }

            val intent = Intent(this@ConfirmActivity, SuccessActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    //ONLINE
    private fun isOnline() : Boolean {
        var inetAddress: InetAddress? = null
        try {
            val future: Future<InetAddress?>? =
                Executors.newSingleThreadExecutor().submit(object : Callable<InetAddress?> {
                    override fun call(): InetAddress? {
                        return try {
                            InetAddress.getByName("google.com")
                        } catch (e: UnknownHostException) {
                            null
                        }
                    }
                })
            inetAddress = future!!.get(1000, TimeUnit.MILLISECONDS)
            future!!.cancel(true)
        } catch (e: InterruptedException) {
        } catch (e: ExecutionException) {
        } catch (e: TimeoutException) {
        }
        return inetAddress != null && !inetAddress.equals("")
    }

    fun writeOrder(order : String, orderCode : String, name : String, id : String, code : String, stock : String, num : String){
        val itemData = ItemData(name, id, code, stock)
        ref = database.getReference(parentReference).child(childList)
        ref.child(order).child(childOrderCode).setValue(orderCode)
        ref.child(order).child(name).setValue(itemData)
        ref.child(order).child(name).child(childNum).setValue(num)
    }

    fun writeDraft(draft : String, draftCode : String, name : String, id : String, code : String, stock : String, num : String){
        val itemData = ItemData(name, id, code, stock)
        ref = database.getReference(parentReference).child(childDraft)
        ref.child(draft).child(childOrderCode).setValue(draftCode)
        ref.child(draft).child(name).setValue(itemData)
        ref.child(draft).child(name).child(childNum).setValue(num)
    }
}