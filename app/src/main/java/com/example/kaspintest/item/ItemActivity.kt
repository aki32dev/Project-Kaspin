package com.example.kaspintest.item

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kaspintest.R
import com.example.kaspintest.database.LocalDB
import com.example.kaspintest.dataparcel.ItemData
import java.util.*
import kotlin.collections.ArrayList

class ItemActivity : AppCompatActivity() {
    var localDB                     : LocalDB? = null
    private lateinit var rvItem     : RecyclerView
    private val list                = ArrayList<ItemData>()

    val MESSAGE_EDIT                : Int = 0
    val MESSAGE_DEL                 : Int = 1

    val msgName                     : String = "keyName"
    val msgStock                    : String = "keyStock"

    private lateinit var dialog     : Dialog
    var itemId                      : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item)
        supportActionBar!!.title = "Barang"
        dialog = Dialog(this@ItemActivity)
        localDB = LocalDB(this@ItemActivity)
        rvItem = findViewById(R.id.rvItem)

        list.addAll(readDB())
        showRecyclerList()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        getMenuInflater().inflate(R.menu.menu_item, menu);
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.btAdd -> addUpdate("", "")
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showRecyclerList(){
        rvItem.setHasFixedSize(true)
        rvItem.layoutManager = LinearLayoutManager(this)
        val itemAdapter = ItemAdapter(this, handler, list)
        rvItem.adapter = itemAdapter
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

                try{
                    itemId = Integer.parseInt(dbId)
                }catch (e : Exception){
                    Log.e("strtoint", itemId.toString())
                }

                val itemData = ItemData(dbName, dbId, dbCode, dbStock)
                dataList.add(itemData)
            }
        }
        else{
            Toast.makeText(this@ItemActivity, "Tidak ada data barang", Toast.LENGTH_SHORT).show()
        }
        return dataList
    }

    private fun addUpdate(itemName : String, itemStock : String){
        dialog.setContentView(R.layout.dialog_item)
        dialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
        val edName          : EditText  = dialog.findViewById(R.id.edName) as EditText
        val edStock         : EditText  = dialog.findViewById(R.id.edStock) as EditText
        val btAddUpdate     : Button    = dialog.findViewById(R.id.btAddUpdate) as Button
        val btDeleteItem    : Button    = dialog.findViewById(R.id.btDeleteItem) as Button
        val imgCloseDialog  : ImageView = dialog.findViewById(R.id.imgCloseDialog) as ImageView

        if(itemName != ""){
            edName.setText(itemName)
        }

        if(itemStock != ""){
            edStock.setText(itemStock)
        }

        imgCloseDialog.setOnClickListener {
            dialog.dismiss()
        }

        btAddUpdate.setOnClickListener {
            val name    = edName.text.toString()
            val stock   = edStock.text.toString()
            if((name != "") && (stock != "")){
                val dbId = (itemId + 1).toString()
                val dbCode = UUID.randomUUID().toString().take(8).uppercase()
                val state = localDB!!.inputItem(name, dbId, dbCode, stock)
                if(state){
                    Toast.makeText(this@ItemActivity, "Berhasil menambahkan barang", Toast.LENGTH_SHORT).show()
                    list.clear()
                    list.addAll(readDB())
                    showRecyclerList()
                    dialog.dismiss()
                }
                else{
                    val stateUpdate = localDB!!.updateItem(name, stock)
                    if(stateUpdate){
                        Toast.makeText(this@ItemActivity, "Berhasil memperbarui barang", Toast.LENGTH_SHORT).show()
                        list.clear()
                        list.addAll(readDB())
                        showRecyclerList()
                        dialog.dismiss()
                    }
                    else{
                        Toast.makeText(this@ItemActivity, "Gagal menambahkan/memperbarui barang", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            else{
                Toast.makeText(this@ItemActivity, "Isi data dengan benar", Toast.LENGTH_SHORT).show()
            }
        }

        btDeleteItem.setOnClickListener {
            val name    = edName.text.toString()
            if (name != ""){
                val state = localDB!!.deleteItem(name)
                if(state){
                    Toast.makeText(this@ItemActivity, "Berhasil menghapus barang", Toast.LENGTH_SHORT).show()
                    list.clear()
                    list.addAll(readDB())
                    showRecyclerList()
                    dialog.dismiss()
                }
                else{
                    Toast.makeText(this@ItemActivity, "Barang tidak terdaftar", Toast.LENGTH_SHORT).show()
                }
            }
            else{
                Toast.makeText(this@ItemActivity, "Isi data dengan benar", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val handler = Handler { message ->
        when(message.what){
            MESSAGE_EDIT     -> {
                val msg1 = message.data.getString(msgName).toString()
                val msg2 = message.data.getString(msgStock).toString()
                addUpdate(msg1, msg2)
            }
            MESSAGE_DEL     -> {
                val msg1 = message.data.getString(msgName).toString()
                val state = localDB!!.deleteItem(msg1)
                if(state){
                    Toast.makeText(this@ItemActivity, "Berhasil menghapus barang", Toast.LENGTH_SHORT).show()
                    list.clear()
                    list.addAll(readDB())
                    showRecyclerList()
                    dialog.dismiss()
                }
            }
        }
        false
    }
}