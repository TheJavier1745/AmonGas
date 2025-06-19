package com.amongas.amongas

import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.SeekBar
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.UUID
import androidx.appcompat.app.AlertDialog

class SettingsActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val switchAlerts = findViewById<Switch>(R.id.switchAlerts)
        val switchPorcentaje = findViewById<Switch>(R.id.switchPorcentaje)
        val seekBar = findViewById<SeekBar>(R.id.seekBarThreshold)
        val tvThreshold = findViewById<TextView>(R.id.tvThresholdValue)

        val prefs = getSharedPreferences("settings", MODE_PRIVATE)
        val btnForgetWifi = findViewById<Button>(R.id.btnForgetWifi)

        btnForgetWifi.setOnClickListener {
            val adapter = BluetoothAdapter.getDefaultAdapter()
            val device = adapter?.bondedDevices?.find { it.name == "ConfiguraGas" }
            val uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb")

            if (device != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    if (checkSelfPermission(android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Permiso de Bluetooth requerido", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                }

                // Paso 1: Mostrar diálogo
                val view = layoutInflater.inflate(R.layout.dialog_wifi_input, null)
                val ssidInput = view.findViewById<EditText>(R.id.inputSsid)
                val passInput = view.findViewById<EditText>(R.id.inputPass)

                val dialog = AlertDialog.Builder(this)
                    .setTitle("Nueva conexión WiFi")
                    .setView(view)
                    .setPositiveButton("Enviar") { _, _ ->
                        val newSsid = ssidInput.text.toString()
                        val newPass = passInput.text.toString()

                        Thread {
                            try {
                                val socket = device.createRfcommSocketToServiceRecord(uuid)
                                socket.connect()

                                val outputStream = socket.outputStream
                                val command = "BORRAR;SSID:$newSsid;PASS:$newPass;\n"
                                outputStream.write(command.toByteArray())
                                outputStream.flush()
                                socket.close()

                                runOnUiThread {
                                    Toast.makeText(this, "✅ Nueva WiFi enviada", Toast.LENGTH_SHORT).show()
                                }
                            } catch (e: Exception) {
                                runOnUiThread {
                                    Toast.makeText(this, "❌ Error al enviar nueva WiFi", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }.start()
                    }
                    .setNegativeButton("Cancelar", null)
                    .create()

                dialog.show()
            } else {
                Toast.makeText(this, "Dispositivo no emparejado", Toast.LENGTH_SHORT).show()
            }
        }




        // ✅ Cargar valores guardados
        switchAlerts.isChecked = prefs.getBoolean("alerts_enabled", true)
        switchPorcentaje.isChecked = prefs.getBoolean("show_percentage", false)

        val savedThreshold = prefs.getInt("gas_threshold", 500)
        seekBar.progress = savedThreshold
        tvThreshold.text = "Umbral: $savedThreshold"

        // ✅ Guardar cambios de cada switch
        switchAlerts.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("alerts_enabled", isChecked).apply()
        }

        switchPorcentaje.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("show_percentage", isChecked).apply()
        }
        val toolbar = findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val prefss = getSharedPreferences("settings", MODE_PRIVATE)
        val sensorName = prefss.getString("sensor_name", "Configuración")
        supportActionBar?.title = sensorName
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val editSensorName = findViewById<EditText>(R.id.editSensorName)
        val btnGuardarNombre = findViewById<Button>(R.id.btnGuardarNombre)

        // Cargar nombre actual (si existe)
        val currentName = prefs.getString("sensor_name", "")
        editSensorName.setText(currentName)

        // Guardar nuevo nombre
        btnGuardarNombre.setOnClickListener {
            val newName = editSensorName.text.toString().trim()
            if (newName.isNotEmpty()) {
                prefs.edit().putString("sensor_name", newName).apply()
                // Actualizar título del Toolbar también
                supportActionBar?.title = newName
            }
        }


        // Al hacer clic en el botón ←
        toolbar.setNavigationOnClickListener {
            finish()
        }


        // ✅ Guardar el umbral con el SeekBar
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tvThreshold.text = "Umbral: $progress"
                prefs.edit().putInt("gas_threshold", progress).apply()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // ✅ Menú inferior
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNav.selectedItemId = R.id.nav_settings

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, SensorActivity::class.java))
                    true
                }
                R.id.nav_history -> {
                    startActivity(Intent(this, HistoryActivity::class.java))
                    true
                }
                R.id.nav_settings -> true // Ya estás en esta pantalla
                else -> false
            }
        }
    }
}
