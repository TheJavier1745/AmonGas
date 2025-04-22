package com.amongas.amongas

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlin.random.Random

class SensorActivity : BaseActivity() {
    private lateinit var tvGasLevel: TextView
    private lateinit var tvStatus: TextView
    private lateinit var imageStatus: ImageView
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sensor)

        tvGasLevel = findViewById(R.id.tvGasLevel)
        tvStatus = findViewById(R.id.tvStatus)
        imageStatus = findViewById(R.id.imageStatus)

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
                R.id.nav_home -> {
                    // Ya estás en home
                    true
                }
                else -> false
            }
        }


    }

    private fun updateGasLevel() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                val gasLevel = Random.nextInt(0, 1001)
                tvGasLevel.text = "Nivel de gas: $gasLevel"
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
        }, 2000)
    }
}
