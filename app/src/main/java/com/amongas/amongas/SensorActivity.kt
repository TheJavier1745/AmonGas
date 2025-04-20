package com.amongas.amongas

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import com.google.android.material.navigation.NavigationView
import android.widget.ImageView
import android.widget.TextView
import kotlin.random.Random

class SensorActivity : BaseActivity() {
    private lateinit var tvGasLevel: TextView
    private lateinit var tvStatus: TextView
    private lateinit var imageStatus: ImageView
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var drawerToggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sensor)

        tvGasLevel = findViewById(R.id.tvGasLevel)
        tvStatus = findViewById(R.id.tvStatus)
        imageStatus = findViewById(R.id.imageStatus)

        val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)
        val navView = findViewById<NavigationView>(R.id.navView)

        drawerToggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_settings -> startActivity(Intent(this, SettingsActivity::class.java))
                R.id.nav_history -> startActivity(Intent(this, HistoryActivity::class.java))
            }
            drawerLayout.closeDrawers()
            true
        }

        updateGasLevel()
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