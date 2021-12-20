package com.example.kaspintest.listdraft

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import com.example.kaspintest.R
import com.example.kaspintest.draft.DraftActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ListActivity : AppCompatActivity(){
    val draftActivity               : DraftActivity = DraftActivity()

    //FIREBASE
    private lateinit var database   : FirebaseDatabase
    private lateinit var ref        : DatabaseReference

    private val parentReference     = "kaspin"
    private val childDraft          = "draftlist"

    private val childNum            = "num"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)
        val tvList : TextView = findViewById(R.id.tvListCart)
        var stringText = ""

        database = FirebaseDatabase.getInstance()

        val orderName = intent.getStringExtra(draftActivity.keyName).toString()
        supportActionBar!!.title = orderName
        ref = database.getReference(parentReference).child(childDraft).child(orderName)
        ref.get().addOnSuccessListener {
            for (ds in it.children) {
                if(ds.hasChildren()){
                    val itemName = ds.key.toString()
                    val itemNum = Integer.parseInt(ds.child(childNum).getValue().toString())

                    stringText = stringText + itemName + "( Jumlah = " + itemNum + ")" + "\n"
                }
            }
            tvList.setText(stringText)
            Toast.makeText(this@ListActivity, "Sukses memuat draft", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener{
            Toast.makeText(this@ListActivity, "Gagal memuat draft", Toast.LENGTH_SHORT).show()
        }
    }
}