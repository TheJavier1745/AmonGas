package com.amongas.amongas

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.*
import org.threeten.bp.*
import org.threeten.bp.format.DateTimeFormatter
import java.util.*

class HistoryActivity : BaseActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var dbRef: DatabaseReference
    private val registros = mutableListOf<RegistroGas>()
    private var registrosOriginales = listOf<RegistroGas>()
    private val formatoFechaHora = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")


    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("Historial", "🟢 Entrando a HistoryActivity")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        val toolbar = findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        recyclerView = findViewById(R.id.rvHistorial)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = RegistroAdapter(emptyList())

        // 📡 Conexión a Firebase
        dbRef = FirebaseDatabase.getInstance().getReference("lecturas")

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("Historial", "🟢 Datos recibidos de Firebase: ${snapshot.childrenCount}")
                registros.clear()

                for (dataSnap in snapshot.children) {
                    val valor = dataSnap.child("valor").getValue(Float::class.java) ?: 0f
                    val rawTimestamp = dataSnap.child("timestamp").value

                    val fechaHora: LocalDateTime? = when (rawTimestamp) {
                        is Long -> {
                            try {
                                // 🔁 Convertir de UTC a hora chilena (UTC-3)
                                val instant = org.threeten.bp.Instant.ofEpochMilli(rawTimestamp)
                                val zonaChile = org.threeten.bp.ZoneId.of("America/Santiago")
                                org.threeten.bp.LocalDateTime.ofInstant(instant, zonaChile)
                            } catch (e: Exception) {
                                Log.e("Historial", "❌ Error convirtiendo timestamp Unix: $rawTimestamp", e)
                                null
                            }
                        }
                        is String -> {
                            try {
                                // Si el timestamp viene como texto tipo "dd/MM/yyyy HH:mm:ss"
                                LocalDateTime.parse(rawTimestamp, formatoFechaHora)
                            } catch (e: Exception) {
                                Log.e("Historial", "❌ Error al parsear String timestamp: $rawTimestamp", e)
                                null
                            }
                        }
                        else -> null
                    }

                    if (fechaHora != null) {
                        registros.add(RegistroGas(fechaHora, valor.toInt()))
                    } else {
                        Log.w("Historial", "⚠️ Registro sin timestamp válido: $rawTimestamp")
                    }
                }

                Log.d("Historial", "📋 Registros válidos agregados: ${registros.size}")
                registros.sortByDescending { it.fechaHora }
                registrosOriginales = registros.toList()
                recyclerView.adapter = RegistroAdapter(registros)
            }


            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "❌ Error al leer datos", error.toException())
            }
        })

        // 📅 Filtro por fecha
        val btnFiltrar = findViewById<Button>(R.id.btnFiltrarFecha)
        btnFiltrar.setOnClickListener {
            val hoy = Calendar.getInstance()
            val datePicker = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    val fechaSeleccionada = LocalDate.of(year, month + 1, dayOfMonth)
                    val filtrados = registrosOriginales.filter {
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

        // 🔄 Quitar filtro
        findViewById<Button>(R.id.btnLimpiarFiltro).setOnClickListener {
            recyclerView.adapter = RegistroAdapter(registrosOriginales)
        }

        // ⚙️ Menú inferior
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
