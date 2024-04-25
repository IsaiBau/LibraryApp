package com.example.libraryapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import com.example.libraryapp.R.id.addButton

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val usu = intent.getIntExtra("ID_USUARIO", -1)
        Log.d("MainActivity", "se pudo: $usu")
        val buttonAdd = findViewById<ImageButton>(R.id.addButton)
        buttonAdd.setOnClickListener {
            val intent = Intent(this, MainActivityAddBook::class.java).apply {
                putExtra("ID_USUARIO", intent.getIntExtra("ID_USUARIO", -1))
                putExtra("usu", usu)
            }
            startActivity(intent)
        }

        val buttonView = findViewById<Button>(R.id.buttonView)
        buttonView.setOnClickListener {
            val intent = Intent(this, MainActivityDetailBook::class.java)
            startActivity(intent)
        }

        val buttonCat = findViewById<Button>(R.id.buttonCat)
        buttonCat.setOnClickListener {
            val intent = Intent(this, MainActivityCategories::class.java)
            startActivity(intent)
        }
    }
}