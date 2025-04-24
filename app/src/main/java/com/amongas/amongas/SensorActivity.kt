package com.amongas.amongas

import android.content.Intent
import android.media.MediaPlayer
import android.os.*
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.google.firebase.database.*
import com.jakewharton.threetenabp.AndroidThreeTen
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter

class SensorActivity : BaseActivity() {
    private lateinit var tvGasLevel: TextView
    private lateinit var tvStatus: TextView
    private lateinit var imageStatus: ImageView
    private lateinit var chartGasLevels: LineChart
    private lateinit var dbRef: DatabaseReference

    private val gasValues = mutableListOf<Float>()
    private var timeStep = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidThreeTen.init(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sensor)

        val toolbar = findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        tvGasLevel = findViewById(R.id.tvGasLevel)
        tvStatus = findViewById(R.id.tvStatus)
        imageStatus = findViewById(R.id.imageStatus)
        chartGasLevels = findViewById(R.id.chartGasLevels)

        val prefs = getSharedPreferences("settings", MODE_PRIVATE)
        val sensorName = prefs.getString("sensor_name", "Sensor")
        supportActionBar?.title = sensorName
        findViewById<TextView>(R.id.tvSensorName).text = sensorName

        dbRef = FirebaseDatabase.getInstance().getReference("lecturas")

        chartGasLevels.description.isEnabled = false
        chartGasLevels.setTouchEnabled(false)
        chartGasLevels.setPinchZoom(false)
        chartGasLevels.setDrawGridBackground(false)
        chartGasLevels.axisRight.isEnabled = false

        val xAxis = chartGasLevels.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)

        val yAxis = chartGasLevels.axisLeft
        yAxis.axisMinimum = 0f
        yAxis.axisMaximum = 1000f

        chartGasLevels.data = LineData()

        escucharDatosFirebase()

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNav.selectedItemId = R.id.nav_home

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    true
                }
                R.id.nav_history -> {
                    startActivity(Intent(this, HistoryActivity::class.java))
                    true
                }
                R.id.nav_home -> true
                else -> false
            }
        }
    }

    private fun escucharDatosFirebase() {
        dbRef.limitToLast(1).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.firstOrNull()?.let { ultimo ->
                    val valor = ultimo.child("valor").getValue(Double::class.java)?.toFloat() ?: 0f

                    // Generar timestamp local automáticamente (zona horaria Chile)
                    val fechaChile = LocalDateTime.now(ZoneId.of("America/Santiago"))
                    val timestamp = fechaChile.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))

                    updateUI(valor.toInt(), timestamp)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                tvGasLevel.text = "Error al leer datos"
            }
        })
    }

    private fun updateUI(gasLevel: Int, timestamp: String) {
        val prefs = getSharedPreferences("settings", MODE_PRIVATE)
        val showPercentage = prefs.getBoolean("show_percentage", false)

        val gasText = if (showPercentage) {
            val porcentaje = gasLevel / 10.0f
            "Nivel de gas: %.1f%%".format(porcentaje)
        } else {
            "Nivel de gas: $gasLevel ppm\n$timestamp"
        }
        tvGasLevel.text = gasText

        timeStep += 1f
        gasValues.add(gasLevel.toFloat())
        if (gasValues.size > 10) gasValues.removeAt(0)

        val dataSets = mutableListOf<ILineDataSet>()
        for (i in 0 until gasValues.size - 1) {
            val entryStart = Entry(i.toFloat(), gasValues[i])
            val entryEnd = Entry((i + 1).toFloat(), gasValues[i + 1])
            val color = when {
                gasValues[i + 1] <= 249f -> getColor(R.color.green)
                gasValues[i + 1] in 250f..499f -> getColor(R.color.yellow)
                else -> getColor(R.color.red)
            }

            val segment = LineDataSet(listOf(entryStart, entryEnd), "").apply {
                setDrawValues(true)
                setDrawCircles(true)
                valueTextSize = 10f
                circleRadius = 4f
                this.color = color
                setCircleColor(color)
                lineWidth = 3f
            }

            dataSets.add(segment)
        }

        chartGasLevels.data = LineData(dataSets)
        chartGasLevels.invalidate()

        when {
            gasLevel <= 249 -> {
                tvStatus.text = "Estado: Normal"
                tvStatus.setTextColor(getColor(R.color.green))
                imageStatus.setImageResource(R.drawable.normal_alto)
            }
            gasLevel in 250..499 -> {
                tvStatus.text = "Estado: Medio"
                tvStatus.setTextColor(getColor(R.color.yellow))
                imageStatus.setImageResource(R.drawable.normal_alto)
            }
            else -> {
                tvStatus.text = "Estado: Emergencia"
                tvStatus.setTextColor(getColor(R.color.red))
                imageStatus.setImageResource(R.drawable.emergencia)

                if (prefs.getBoolean("alerts_enabled", true)) {
                    val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vibrator.vibrate(
                            VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE)
                        )
                    } else {
                        @Suppress("DEPRECATION")
                        vibrator.vibrate(500)
                    }

                    val mediaPlayer = MediaPlayer.create(this@SensorActivity, R.raw.alert_sound)
                    mediaPlayer.start()
                }
            }
        }
    }
}
