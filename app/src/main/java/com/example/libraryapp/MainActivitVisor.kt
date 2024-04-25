package com.example.libraryapp

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.Manifest
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.net.Uri
import android.os.Environment
import androidx.activity.result.contract.ActivityResultContracts
import com.example.libraryapp.databinding.ActivityMainActivitVisorBinding
import com.pdfview.PDFView
import java.io.File

class MainActivitVisor : AppCompatActivity() {
    private lateinit var binding:ActivityMainActivitVisorBinding
    private var selectedUri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_activit_visor)

        binding = ActivityMainActivitVisorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if(checkPermission()){
            Toast.makeText(this, "Permiso aceptado", Toast.LENGTH_LONG).show()
        } else {
            requestPermissions()
        }

        binding.button.setOnClickListener {
            launchPDF.launch("application/pdf")
        }

        //val pdfView = findViewById<PDFView>(R.id.visor)
        //val file = File(filesDir, "content://com.android.providers.media.documents/document/document%3A358550")
        //if (file.exists()) {
        //    // Carga el archivo PDF
        //    pdfView.fromFile(file)
//
        //} else {
        //    // El archivo no existe en la ruta especificada
        //    // Agrega un mensaje de error o realiza alguna acción apropiada
        //}
        //pdfView.isZoomEnabled = true
        //pdfView.show()
    }

    private val launchPDF = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedUri = it // Almacena la URI seleccionada en la variable selectedUri

            binding.visor.fromUri(it)
                .spacing(12)
                .defaultPage(0)
                .enableAnnotationRendering(false)
                .enableDoubletap(true)
                .load()
            binding.visor.fitToWidth()
            binding.visor.useBestQuality(true)
        }
    }

    private fun checkPermission(): Boolean {
        val permission1 = ContextCompat.checkSelfPermission(applicationContext, WRITE_EXTERNAL_STORAGE)
        val permission2 = ContextCompat.checkSelfPermission(applicationContext, READ_EXTERNAL_STORAGE)
        return permission1 == PackageManager.PERMISSION_GRANTED && permission2 == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE),
            200
        )
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 200) {
            if(grantResults.size > 0) {
                val writeStorage = grantResults[0] == PackageManager.PERMISSION_GRANTED
                val readStorage = grantResults[1] == PackageManager.PERMISSION_GRANTED

                if(writeStorage && readStorage) {
                    Toast.makeText(this, "Permisos concedidos", Toast.LENGTH_LONG)
                } else {
                    Toast.makeText(this, "Permisos rechazados", Toast.LENGTH_LONG)
                    finish()
                }
            }
        }
    }
}