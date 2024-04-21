package com.example.libraryapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.example.libraryapp.R.id.buttonLoginRegister
import com.example.libraryapp.R.id.buttonRegister
import com.google.android.material.textfield.TextInputEditText

class MainActivityLogin : AppCompatActivity() {
    private lateinit var dbHelper: DbHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_login)

        dbHelper = DbHelper(this)

        val buttonLogin = findViewById<Button>(R.id.buttonLogin)
        buttonLogin.setOnClickListener {
            val usuarioEditText: TextInputEditText = findViewById(R.id.usuario)
            val passwordEditText: TextInputEditText = findViewById(R.id.password)

            val username = usuarioEditText.text.toString()
            val password = passwordEditText.text.toString()

            val userId = dbHelper.leerusuario(username, password)
            if (userId != -1L) {
                // Inicio de sesión exitoso, ahora puedes pasar el ID del usuario a la actividad principal
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("USER_ID", userId)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
            }
        }

        val buttonLoginRegister = findViewById<Button>(R.id.buttonLoginRegister)
        buttonLoginRegister.setOnClickListener {
            val intent = Intent(this, MainActivityRegister::class.java)
            startActivity(intent)
        }
    }
}