package com.amongas.amongas

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.OutputStream
import java.util.*

class BluetoothConfigActivity : AppCompatActivity() {

    private val deviceName = "ConfiguraGas"
    private val uuid: UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bluetooth_config)

        val ssidInput = findViewById<EditText>(R.id.inputSsid)
        val passInput = findViewById<EditText>(R.id.inputPass)
        val btnSend = findViewById<Button>(R.id.btnSend)

        btnSend.setOnClickListener {
            val ssid = ssidInput.text.toString()
            val pass = passInput.text.toString()
            sendCredentialsOverBluetooth(ssid, pass)
        }
    }

    private fun sendCredentialsOverBluetooth(ssid: String, pass: String) {
        val adapter = BluetoothAdapter.getDefaultAdapter()
        if (adapter == null || !adapter.isEnabled) {
            Toast.makeText(this, "Bluetooth no disponible", Toast.LENGTH_SHORT).show()
            return
        }

        val device: BluetoothDevice? = adapter.bondedDevices.find { it.name == deviceName }
        if (device == null) {
            Toast.makeText(this, "Dispositivo no encontrado", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val socket: BluetoothSocket = device.createRfcommSocketToServiceRecord(uuid)
            socket.connect()
            val outputStream: OutputStream = socket.outputStream
            val data = "SSID:$ssid;PASS:$pass;\n"
            outputStream.write(data.toByteArray())
            outputStream.flush()
            socket.close()

            Toast.makeText(this, "✅ Enviado correctamente", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "❌ Error al enviar", Toast.LENGTH_LONG).show()
        }
    }
}
