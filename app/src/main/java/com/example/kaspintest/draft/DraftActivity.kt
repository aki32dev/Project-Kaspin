package com.example.kaspintest.draft

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kaspintest.R
import com.example.kaspintest.dataparcel.OrderData
import com.example.kaspintest.listdraft.ListActivity
import com.google.firebase.database.*
import java.net.InetAddress
import java.net.UnknownHostException
import java.util.concurrent.*

class DraftActivity : AppCompatActivity() {
    private lateinit var rvDraft    : RecyclerView
    private val list                = ArrayList<OrderData>()

    //FIREBASE
    private lateinit var database   : FirebaseDatabase
    private lateinit var ref        : DatabaseReference

    val MESSAGE_DEL                 : Int = 0
    val MESSAGE_LOAD                : Int = 1

    private val parentReference     = "kaspin"
    private val childDraft          = "draftlist"

    private val childDraftCode      = "ordercode"

    val msgName : String            = "keyName"
    val keyName : String            = "draftName"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_draft)
        database = FirebaseDatabase.getInstance()
        rvDraft = findViewById(R.id.rvDraft)

        if(isOnline()){
            initDB()
        }
        else{
            Toast.makeText(this@DraftActivity, "Tidak ada koneksi internet", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showRecyclerList(){
        rvDraft.setHasFixedSize(true)
        rvDraft.layoutManager = LinearLayoutManager(this)
        val draftAdapter = DraftAdapter(this, handler, list)
        rvDraft.adapter = draftAdapter
    }

    private fun initDB(){
        ref = database.getReference(parentReference).child(childDraft)
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                list.clear()
                for (ds in dataSnapshot.children) {
                    val draftName = ds.key
                    val draftCode = ds.child(childDraftCode).getValue().toString()
                    val draftData = OrderData(draftName, draftCode)
                    list.add(draftData)
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
                    ref = database.getReference(parentReference).child(childDraft)
                    ref.child(msg1).removeValue()
                }
                else{
                    Toast.makeText(this@DraftActivity, "Tidak ada koneksi internet", Toast.LENGTH_SHORT).show()
                }
            }
            MESSAGE_LOAD    -> {
                if(isOnline()){
                    val msg1 = message.data.getString(msgName).toString()
                    val intent = Intent(this@DraftActivity, ListActivity::class.java)
                    intent.putExtra(keyName, msg1)
                    startActivity(intent)
                    finish()
                }
                else{
                    Toast.makeText(this@DraftActivity, "Tidak ada koneksi internet", Toast.LENGTH_SHORT).show()
                }
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