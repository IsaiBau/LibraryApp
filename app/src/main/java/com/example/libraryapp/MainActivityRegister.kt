package com.example.libraryapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class MainActivityRegister : AppCompatActivity() {
    private lateinit var dbHelper: DbHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_register)

        dbHelper = DbHelper(this)

        val usuarioEditText: TextInputEditText = findViewById(R.id.usuario)
        val passwordEditText: TextInputEditText = findViewById(R.id.password)
        val passwordConfirmEditText: TextInputEditText = findViewById(R.id.passwordConfirm)
        val buttonRegister = findViewById<Button>(R.id.buttonRegister)

        buttonRegister.setOnClickListener{
            val username = usuarioEditText.text.toString()
            val password = passwordEditText.text.toString()
            val passwordConfirm = passwordConfirmEditText.text.toString()
            if (username.isEmpty() || password.isEmpty() || passwordConfirm.isEmpty()) {
                Toast.makeText(this, "Por favor ingresa todos los campos", Toast.LENGTH_SHORT).show()
            } else if (password != passwordConfirm) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
            } else {
                userRegister(username, password)
            }
        }

        val buttonLoginRegister = findViewById<Button>(R.id.buttonLoginRegister)
        buttonLoginRegister.setOnClickListener {
            val intent = Intent(this, MainActivityLogin::class.java)
            startActivity(intent)
        }
    }

    private fun userRegister(username: String, password: String){
        val idRegister = dbHelper.insertUser(username, password)
        if (idRegister != 1L){
            Toast.makeText(this, "Registro exitoso!", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MainActivityLogin::class.java)
            startActivity(intent)
            finish()
        }else{
            Toast.makeText(this, "Error al registrar", Toast.LENGTH_SHORT).show()
        }
    }
}