package com.example.kaspintest.database

import com.example.kaspintest.dataparcel.ItemData
import com.google.firebase.database.*

class FirebaseDB {
    private var database : FirebaseDatabase
    private lateinit var ref : DatabaseReference

    private val parentReference = "kaspin"
    private val childList = "orderlist"

    private val childName = "name"
    private val childId = "id"
    private val childCode = "code"
    private val childStock = "stock"

    init {
        database = FirebaseDatabase.getInstance()
    }

    fun writeOrder(order : String, name : String, id : String, code : String, stock : String) : Boolean {
        val itemData = ItemData(name, id, code, stock)
        ref = database.getReference(parentReference).child(childList)
        return if(ref.child(order).child(name).setValue(itemData).isSuccessful){true}else{false}
    }

    fun readOrder() : String{
        var result = ""
        ref = database.getReference(parentReference).child("Id")
        ref.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                result = snapshot.getValue().toString()
            }
            override fun onCancelled(error: DatabaseError) {
                result = ""
            }
        })
        return result
    }
}