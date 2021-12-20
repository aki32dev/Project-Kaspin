package com.example.kaspintest.order

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kaspintest.R
import com.example.kaspintest.confirm.ConfirmActivity
import com.example.kaspintest.dataparcel.OrderData
import com.google.firebase.database.*
import com.google.firebase.database.DataSnapshot
import java.net.InetAddress
import java.net.UnknownHostException
import java.util.concurrent.*


class OrderActivity : AppCompatActivity() {
    private lateinit var rvOrder        : RecyclerView
    private val list                    = ArrayList<OrderData>()

    private lateinit var database       : FirebaseDatabase
    private lateinit var ref            : DatabaseReference

    val MESSAGE_DEL                     : Int = 0
    val MESSAGE_LOAD                    : Int = 1

    private val parentReference         = "kaspin"
    private val childList               = "orderlist"

    private val childOrderCode          = "ordercode"

    val msgName : String                = "keyName"
    val keyName : String                = "orderName"

    val keyDB : String                  = "keyDatabase"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)
        supportActionBar!!.title = "Order"
        database = FirebaseDatabase.getInstance()
        rvOrder = findViewById(R.id.rvOrder)

        if(isOnline()){
            initDB()
        }
        else{
            Toast.makeText(this@OrderActivity, "Tidak ada koneksi internet", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showRecyclerList(){
        rvOrder.setHasFixedSize(true)
        rvOrder.layoutManager = LinearLayoutManager(this)
        val orderAdapter = OrderAdapter(this, handler, list)
        rvOrder.adapter = orderAdapter
    }

    private fun initDB(){
        ref = database.getReference(parentReference).child(childList)
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                list.clear()
                for (ds in dataSnapshot.children) {
                    val orderName = ds.key
                    val orderCode = ds.child(childOrderCode).getValue().toString()
                    val orderData = OrderData(orderName, orderCode)
                    list.add(orderData)
                }
                showRecyclerList()
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        }
        ref.addValueEventListener(postListener)
    }

    private val handler = Handler { message ->
        when(message.what){
            MESSAGE_DEL    -> {
                if(isOnline()){
                    val msg1 = message.data.getString(msgName).toString()
                    ref = database.getReference(parentReference).child(childList)
                    ref.child(msg1).removeValue()
                }
                else{
                    Toast.makeText(this@OrderActivity, "Tidak ada koneksi internet", Toast.LENGTH_SHORT).show()
                }
            }
            MESSAGE_LOAD    -> {
                val msg1 = message.data.getString(msgName).toString()
                val intent = Intent(this@OrderActivity, ConfirmActivity::class.java)
                intent.putExtra(keyName, msg1)
                intent.putExtra(keyDB, "FIREBASE")
                startActivity(intent)
                finish()
            }
        }
        false
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
}