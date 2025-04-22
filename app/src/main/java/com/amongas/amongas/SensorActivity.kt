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
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import kotlin.random.Random

class SensorActivity : BaseActivity() {
    private lateinit var tvGasLevel: TextView
    private lateinit var tvStatus: TextView
    private lateinit var imageStatus: ImageView
    private lateinit var chartGasLevels: LineChart

    private val handler = Handler(Looper.getMainLooper())
    private val gasValues = mutableListOf<Float>()
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

                // Guardar el nuevo valor, mantener solo los últimos 10
                gasValues.add(gasLevel.toFloat())
                if (gasValues.size > 10) gasValues.removeAt(0)

                // Crear un dataset por segmento (con valores visibles al final)
                val dataSets = mutableListOf<ILineDataSet>()
                for (i in 0 until gasValues.size - 1) {
                    val entryStart = Entry(i.toFloat(), gasValues[i])
                    val entryEnd = Entry((i + 1).toFloat(), gasValues[i + 1])

                    val segmentColor = when {
                        gasValues[i + 1] <= 249 -> getColor(R.color.green)
                        gasValues[i + 1] in 250f..499f -> getColor(R.color.yellow)
                        else -> getColor(R.color.red)
                    }

                    val segmentDataSet = LineDataSet(listOf(entryStart, entryEnd), "").apply {
                        setDrawValues(true)              // ✅ Mostrar valor
                        setDrawCircles(true)             // ✅ Círculo en punto
                        valueTextSize = 10f              // ✅ Tamaño del valor
                        circleRadius = 4f
                        color = segmentColor
                        setCircleColor(segmentColor)
                        lineWidth = 3f
                    }

                    // Solo mostrar valor del último punto del segmento
                    segmentDataSet.setDrawValues(true)

                    dataSets.add(segmentDataSet)
                }

                chartGasLevels.data = LineData(dataSets)
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

                handler.postDelayed(this, 2000)
            }
        }, 0)
    }
}
