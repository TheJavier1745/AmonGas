package com.amongas.amongas

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.threeten.bp.LocalDate
import java.util.*

class HistoryActivity : BaseActivity() {
    private lateinit var recyclerView: RecyclerView
    private var registros = HistorialManager.registrosEjemplo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        val toolbar = findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar.setNavigationOnClickListener {
            finish()
        }

        val prefs = getSharedPreferences("settings", MODE_PRIVATE)
        val sensorName = prefs.getString("sensor_name", "Historial")
        supportActionBar?.title = sensorName

        recyclerView = findViewById(R.id.rvHistorial)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = RegistroAdapter(registros)

        // Filtro por fecha
        val btnFiltrar = findViewById<Button>(R.id.btnFiltrarFecha)
        btnFiltrar.setOnClickListener {
            val hoy = Calendar.getInstance()
            val datePicker = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    val fechaSeleccionada = LocalDate.of(year, month + 1, dayOfMonth)
                    val filtrados = registros.filter {
                        it.fechaHora.toLocalDate() == fechaSeleccionada
                    }
                    recyclerView.adapter = RegistroAdapter(filtrados)
                },
                hoy.get(Calendar.YEAR),
                hoy.get(Calendar.MONTH),
                hoy.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.show()
        }

        // Quitar filtro
        val btnLimpiar = findViewById<Button>(R.id.btnLimpiarFiltro)
        btnLimpiar.setOnClickListener {
            recyclerView.adapter = RegistroAdapter(registros)
        }

        // Menú inferior
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNav.selectedItemId = R.id.nav_history

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, SensorActivity::class.java))
                    true
                }
                R.id.nav_settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    true
                }
                R.id.nav_history -> true
                else -> false
            }
        }
    }

    override fun onResume() {
        super.onResume()
        findViewById<BottomNavigationView>(R.id.bottomNavigationView).selectedItemId = R.id.nav_history
    }
}
