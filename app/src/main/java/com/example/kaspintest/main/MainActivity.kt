package com.example.kaspintest.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.kaspintest.R
import com.example.kaspintest.draft.DraftActivity
import com.example.kaspintest.item.ItemActivity
import com.example.kaspintest.transaction.TransactionActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar!!.hide()

        val btItem          : Button = findViewById(R.id.btItem) as Button
        val btTransaction   : Button = findViewById(R.id.btTransaction) as Button
        val btDraft         : Button = findViewById(R.id.btDraft) as Button

        btItem.setOnClickListener {
            val intent = Intent(this@MainActivity, ItemActivity::class.java)
            startActivity(intent)
        }

        btTransaction.setOnClickListener {
            val intent = Intent(this@MainActivity, TransactionActivity::class.java)
            startActivity(intent)
        }

        btDraft.setOnClickListener {
            val intent = Intent(this@MainActivity, DraftActivity::class.java)
            startActivity(intent)
        }
    }
}