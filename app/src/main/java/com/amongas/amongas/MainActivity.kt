package com.amongas.amongas

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val imgLogo = findViewById<ImageView>(R.id.imgLogo)
        val appTitle = findViewById<TextView>(R.id.appTitle)
        val btnConnect = findViewById<Button>(R.id.btnConnect)

        val fadeInFast = AnimationUtils.loadAnimation(this, R.anim.fade_in_fast)
        val fadeInDelayed = AnimationUtils.loadAnimation(this, R.anim.fade_in_delayed)

        // Animar logo primero
        imgLogo.startAnimation(fadeInFast)

        // Texto aparece justo después del logo
        appTitle.startAnimation(fadeInDelayed)

        // Botón aparece con el mismo delay (o más)
        btnConnect.startAnimation(fadeInDelayed)

        btnConnect.setOnClickListener {
            val intent = Intent(this, SensorActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }
    }
}
