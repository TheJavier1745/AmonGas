package com.amongas.amongas

import android.os.Bundle
import android.widget.SeekBar
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val switchAlerts = findViewById<Switch>(R.id.switchAlerts)
        val seekBar = findViewById<SeekBar>(R.id.seekBarThreshold)
        val tvThreshold = findViewById<TextView>(R.id.tvThresholdValue)

        val prefs = getSharedPreferences("settings", MODE_PRIVATE)

        // Cargar estado guardado
        switchAlerts.isChecked = prefs.getBoolean("alerts_enabled", true)
        val savedThreshold = prefs.getInt("gas_threshold", 500)
        seekBar.progress = savedThreshold
        tvThreshold.text = "Umbral: $savedThreshold"

        // Guardar cambio del Switch
        switchAlerts.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("alerts_enabled", isChecked).apply()
        }

        // Guardar cambio del SeekBar
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tvThreshold.text = "Umbral: $progress"
                prefs.edit().putInt("gas_threshold", progress).apply()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }
}
