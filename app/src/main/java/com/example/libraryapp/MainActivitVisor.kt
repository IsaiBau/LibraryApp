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
import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.os.Environment
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import com.example.libraryapp.databinding.ActivityMainActivitVisorBinding
import com.github.barteksc.pdfviewer.PDFView
import java.io.BufferedInputStream

import java.io.File
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class MainActivitVisor : AppCompatActivity() {
    private lateinit var binding:ActivityMainActivitVisorBinding
    private var selectedUri: Uri? = null
    private lateinit var pdfView: PDFView
    private lateinit var dbHelper: DbHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_activit_visor)

        binding = ActivityMainActivitVisorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        dbHelper = DbHelper(this)
        val usu = intent.getIntExtra("ID_USUARIO", -1)
        val usu2 =usu.toLong()
        val idL = intent.getLongExtra("LIBRO_ID", -1)
        Log.d("VISOR", "se pudo: $usu, $idL")
        if(checkPermission()){
            Toast.makeText(this, "Permiso aceptado", Toast.LENGTH_LONG).show()
        } else {
            requestPermissions()
        }
        pdfView = binding.visor


        val cursor = dbHelper.getLibroPorId(usu2, idL)
        if (cursor != null && cursor.moveToFirst()) {
            val filePathIndex = cursor.getColumnIndex("file_path")
            val filePath = cursor.getString(filePathIndex)
            Log.d("VISOR", "filepath: $filePath")
            val uri = Uri.parse(filePath)
            Log.d("VISOR", "uriparse: $uri")

            RetrievePDFFromURL(pdfView).execute(filePath)

        } else {
            // Manejar el caso en el que no se encuentre el libro
        }
        cursor?.close()

        //binding.button.setOnClickListener {
            //launchPDF.launch("application/pdf")
        //}

    }
    class RetrievePDFFromURL(pdfView: PDFView) :
        AsyncTask<String, Void, InputStream>() {

        // on below line we are creating a variable for our pdf view.
        val mypdfView: PDFView = pdfView

        // on below line we are calling our do in background method.
        override fun doInBackground(vararg params: String?): InputStream? {
            // on below line we are creating a variable for our input stream.
            var inputStream: InputStream? = null
            try {
                // on below line we are creating an url
                // for our url which we are passing as a string.
                val url = URL(params.get(0))

                // on below line we are creating our http url connection.
                val urlConnection: HttpURLConnection = url.openConnection() as HttpsURLConnection

                // on below line we are checking if the response
                // is successful with the help of response code
                // 200 response code means response is successful
                if (urlConnection.responseCode == 200) {
                    // on below line we are initializing our input stream
                    // if the response is successful.
                    inputStream = BufferedInputStream(urlConnection.inputStream)
                }
            }
            // on below line we are adding catch block to handle exception
            catch (e: Exception) {
                // on below line we are simply printing
                // our exception and returning null
                e.printStackTrace()
                return null;
            }
            // on below line we are returning input stream.
            return inputStream;
        }

        // on below line we are calling on post execute
        // method to load the url in our pdf view.
        override fun onPostExecute(result: InputStream?) {
            // on below line we are loading url within our
            // pdf view on below line using input stream.
            mypdfView.fromStream(result).load()

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
    //private val launchPDF = registerForActivityResult(
    //    ActivityResultContracts.GetContent()
    //) { uri: Uri? ->
    //    uri?.let {
    //        selectedUri = it // Almacena la URI seleccionada en la variable selectedUri
//
    //        binding.visor.fromUri(it)
    //            .spacing(12)
    //            .defaultPage(0)
    //            .enableAnnotationRendering(false)
    //            .enableDoubletap(true)
    //            .load()
    //        binding.visor.fitToWidth()
    //        binding.visor.useBestQuality(true)
    //    }
    //}
}