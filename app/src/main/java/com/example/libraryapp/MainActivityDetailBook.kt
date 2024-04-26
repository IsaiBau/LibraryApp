package com.example.libraryapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import com.example.libraryapp.R.id.buttonReturn

class MainActivityDetailBook : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_detail_book)
        val usu = intent.getIntExtra("ID_USUARIO", -1)
        val idL = intent.getLongExtra("LIBRO_ID", -1)
        Log.d("MainActivity", "se pudo: $usu, $idL")

        val buttonReturn = findViewById<Button>(R.id.buttonReturn)
        buttonReturn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        val ver = findViewById<ImageButton>(R.id.ver)
        ver.setOnClickListener {
            val intent = Intent(this, MainActivitVisor::class.java).apply {
                putExtra("LIBRO_ID", idL)
                putExtra("ID_USUARIO", usu)
            }
            startActivity(intent)
        }
    }
}