package com.amongas.amongas

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlin.random.Random

class SensorActivity : BaseActivity() {
    private lateinit var tvGasLevel: TextView
    private lateinit var tvStatus: TextView
    private lateinit var imageStatus: ImageView
    private lateinit var chartGasLevels: LineChart

    private val handler = Handler(Looper.getMainLooper())
    private val gasEntries = ArrayList<Entry>()
    private var timeStep = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sensor)

        tvGasLevel = findViewById(R.id.tvGasLevel)
        tvStatus = findViewById(R.id.tvStatus)
        imageStatus = findViewById(R.id.imageStatus)
        chartGasLevels = findViewById(R.id.chartGasLevels)

        // Configurar gráfico
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

        updateGasLevel()

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
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

    private fun updateGasLevel() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                val gasLevel = Random.nextInt(0, 1001)
                tvGasLevel.text = "Nivel de gas: $gasLevel"
                timeStep += 1f
                gasEntries.add(Entry(timeStep, gasLevel.toFloat()))

                if (gasEntries.size > 20) gasEntries.removeAt(0)

                // Determinar color según el nivel
                val lineColor = when {
                    gasLevel <= 249 -> getColor(R.color.green)
                    gasLevel in 250..499 -> getColor(R.color.yellow)
                    else -> getColor(R.color.red)
                }

                // Crear dataset
                val dataSet = LineDataSet(gasEntries, "Nivel de Gas").apply {
                    setDrawValues(true) // Mostrar valor numérico
                    setDrawCircles(true) // Mostrar puntos
                    circleRadius = 4f
                    lineWidth = 2f
                    valueTextSize = 10f
                    color = lineColor
                    setCircleColor(lineColor)
                    mode = LineDataSet.Mode.LINEAR
                }

                chartGasLevels.data = LineData(dataSet)
                chartGasLevels.invalidate()

                // Actualizar estado visual
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
                    }
                }

                handler.postDelayed(this, 5000)
            }
        }, 0)
    }
}
