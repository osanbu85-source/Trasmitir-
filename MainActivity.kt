package com.example.screenmirror

import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var projectionManager: MediaProjectionManager
    private val scanner = DeviceScanner()
    private var server: ScreenStreamServer? = null
    private val REQUEST_CODE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Creación rápida de la interfaz
        val layout = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(50, 50, 50, 50)
        }

        val statusText = TextView(this).apply { text = "Estado: Esperando..." }
        val btnScan = Button(this).apply { text = "1. Buscar Televisores" }
        val btnStart = Button(this).apply { text = "2. Transmitir Pantalla" }

        layout.addView(statusText)
        layout.addView(btnScan)
        layout.addView(btnStart)
        setContentView(layout)

        projectionManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager

        // Iniciar el servidor local en el puerto 8080
        try {
            server = ScreenStreamServer(8080)
            server?.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Evento para rastrear televisores
        btnScan.setOnClickListener {
            statusText.text = "Estado: Escaneando la red..."
            scanner.scanNetwork(object : DeviceScanner.OnDeviceFoundListener {
                override fun onDeviceFound(name: String, ipAddress: String) {
                    runOnUiThread {
                        statusText.text = "TV Detectado: $name\nDirección IP: $ipAddress"
                        Toast.makeText(this@MainActivity, "Encontrado: $name", Toast.LENGTH_SHORT).show()
                    }
                }
            })
        }

        // Evento para solicitar permiso de captura de pantalla
        btnStart.setOnClickListener {
            val intent = projectionManager.createScreenCaptureIntent()
            startActivityForResult(intent, REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Toast.makeText(this, "Captura iniciada. Abre la IP en el navegador de tu TV", Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        server?.stop()
    }
}
