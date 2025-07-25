package com.amongas.amongas

import android.Manifest
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.os.*
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var btnConnect: Button
    private val REQUEST_BLUETOOTH_PERMISSIONS = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnConnect = findViewById(R.id.btnConnect)

        // ✅ Verificar si ya hay WiFi configurada
        val prefs = getSharedPreferences("settings", MODE_PRIVATE)
        val wifiConfigured = prefs.getBoolean("wifi_configured", false)

        if (wifiConfigured) {
            // Ir directo a la pantalla del sensor
            startActivity(Intent(this, SensorActivity::class.java))
            finish()
            return
        }

        solicitarPermisosBluetooth()

        btnConnect.visibility = Button.VISIBLE
        btnConnect.setOnClickListener {
            mostrarDialogoBluetooth()
        }
    }

    private fun mostrarDialogoBluetooth() {
        val inflater = LayoutInflater.from(this)
        val dialogView = inflater.inflate(R.layout.dialog_wifi_input, null)

        val ssidInput = dialogView.findViewById<EditText>(R.id.inputSsid)
        val passInput = dialogView.findViewById<EditText>(R.id.inputPass)

        AlertDialog.Builder(this)
            .setTitle("Configurar Wi-Fi")
            .setView(dialogView)
            .setPositiveButton("Enviar") { _, _ ->
                val ssid = ssidInput.text.toString()
                val pass = passInput.text.toString()
                enviarCredencialesBT(ssid, pass)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun enviarCredencialesBT(ssid: String, pass: String) {
        val adapter = BluetoothAdapter.getDefaultAdapter()
        if (adapter == null || !adapter.isEnabled) {
            Toast.makeText(this, "Bluetooth no disponible", Toast.LENGTH_SHORT).show()
            return
        }

        val device = adapter.bondedDevices.find { it.name == "ConfiguraGas" }
        if (device == null) {
            Toast.makeText(this, "Dispositivo ConfiguraGas no emparejado", Toast.LENGTH_LONG).show()
            return
        }

        val uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb")

        try {
            val socket = device.createRfcommSocketToServiceRecord(uuid)
            socket.connect()
            val outputStream = socket.outputStream
            val mensaje = "SSID:$ssid;PASS:$pass;\n"
            outputStream.write(mensaje.toByteArray())
            outputStream.flush()
            socket.close()

            // ✅ Guardar estado de configuración exitosa
            getSharedPreferences("settings", MODE_PRIVATE).edit()
                .putBoolean("wifi_configured", true)
                .apply()

            Toast.makeText(this, "✅ Wi-Fi configurada", Toast.LENGTH_LONG).show()
            startActivity(Intent(this, SensorActivity::class.java))
            finish()

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "❌ Error al enviar por Bluetooth", Toast.LENGTH_LONG).show()
        }
    }

    private fun solicitarPermisosBluetooth() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val permisos = mutableListOf<String>()
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT)
                != PackageManager.PERMISSION_GRANTED
            ) {
                permisos.add(Manifest.permission.BLUETOOTH_CONNECT)
            }
            if (permisos.isNotEmpty()) {
                ActivityCompat.requestPermissions(this, permisos.toTypedArray(), REQUEST_BLUETOOTH_PERMISSIONS)
            }
        }
    }
}
