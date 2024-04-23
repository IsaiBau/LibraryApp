package com.example.libraryapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.service.autofill.UserData
import android.util.Log
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
                val idUsuario = dbHelper.obtenerIdUsuario(username, password)
                val preferences = getSharedPreferences("user_data", MODE_PRIVATE)
                val editor = preferences.edit()
                editor.putInt("user_id", idUsuario)
                editor.apply()
                Log.d("MainActivity", "ID de usuario: $idUsuario")
                guardarEstadoSesion(true, idUsuario)

                // Inicio de sesión exitoso, ahora puedes pasar el ID del usuario a la actividad principal
                val intent = Intent(this, MainActivity::class.java).apply {
                    putExtra("ID_USUARIO", idUsuario)
                    Log.d("MainActivity", "ID de usuario: $idUsuario")
                }
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
    private fun sesionIniciada(): Boolean {
        val preferences = getSharedPreferences("sesion", MODE_PRIVATE)
        return preferences.getBoolean("sesionIniciada", false)
    }

    private fun guardarEstadoSesion(esSesionIniciada: Boolean, idUsuario: Int) {
        val preferences = getSharedPreferences("sesion", MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putBoolean("sesionIniciada", esSesionIniciada)
        editor.putInt("idUsuario", idUsuario)
        editor.apply()
    }
}