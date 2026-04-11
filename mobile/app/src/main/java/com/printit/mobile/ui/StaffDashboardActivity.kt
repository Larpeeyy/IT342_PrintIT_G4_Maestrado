package com.printit.mobile.ui

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.printit.mobile.R

class StaffDashboardActivity : AppCompatActivity() {

    private lateinit var tvProfileBadge: TextView
    private lateinit var tvWelcomeTitle: TextView
    private lateinit var tvWelcomeMessage: TextView
    private lateinit var btnLogout: Button
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_staff_dashboard)

        sharedPreferences = getSharedPreferences("printit_prefs", MODE_PRIVATE)

        tvProfileBadge = findViewById(R.id.tvProfileBadge)
        tvWelcomeTitle = findViewById(R.id.tvWelcomeTitle)
        tvWelcomeMessage = findViewById(R.id.tvWelcomeMessage)
        btnLogout = findViewById(R.id.btnLogout)

        val fullName = sharedPreferences.getString("fullName", "Staff") ?: "Staff"
        val initials = getInitials(fullName)

        tvProfileBadge.text = initials
        tvWelcomeTitle.text = "Staff Dashboard"
        tvWelcomeMessage.text =
            "Welcome to PrintIT, $fullName. This page is currently for testing role-based login and navigation."

        btnLogout.setOnClickListener {
            sharedPreferences.edit().clear().apply()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun getInitials(name: String): String {
        val parts = name.trim().split(" ").filter { it.isNotBlank() }
        return when {
            parts.size >= 2 -> "${parts[0].first()}${parts[1].first()}".uppercase()
            parts.size == 1 -> parts[0].take(2).uppercase()
            else -> "SF"
        }
    }
}