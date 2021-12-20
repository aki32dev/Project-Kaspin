package com.example.kaspintest.success

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.kaspintest.R
import com.example.kaspintest.main.MainActivity
import com.example.kaspintest.transaction.TransactionActivity

class SuccessActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_success)

        val btNew : Button = findViewById(R.id.btNew)
        val btHome : Button = findViewById(R.id.btHome)

        btHome.setOnClickListener {
            finish()
        }

        btNew.setOnClickListener {
            val intent = Intent(this@SuccessActivity, TransactionActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}