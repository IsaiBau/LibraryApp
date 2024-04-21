package com.example.libraryapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivityCategories : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_categories)

        val buttonView = findViewById<Button>(R.id.buttonView)
        buttonView.setOnClickListener {
            val intent = Intent(this, MainActivityDetailBook::class.java)
            startActivity(intent)
        }
    }
}