package com.printit.mobile

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.printit.mobile.features.admin.dashboard.AdminDashboardActivity
import com.printit.mobile.features.auth.LoginActivity
import com.printit.mobile.features.staff.dashboard.StaffDashboardActivity
import com.printit.mobile.features.student.dashboard.StudentDashboardActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPreferences = getSharedPreferences("printit_prefs", MODE_PRIVATE)

        val email = sharedPreferences.getString("email", "") ?: ""
        val role = sharedPreferences.getString("role", "") ?: ""

        if (email.isBlank() || role.isBlank()) {
            goToLogin()
            return
        }

        redirectByRole(role)
    }

    private fun redirectByRole(role: String) {
        val intent = when (role.uppercase()) {
            "ADMIN" -> Intent(this, AdminDashboardActivity::class.java)
            "STAFF" -> Intent(this, StaffDashboardActivity::class.java)
            "STUDENT" -> Intent(this, StudentDashboardActivity::class.java)
            else -> Intent(this, LoginActivity::class.java)
        }

        startActivity(intent)
        finish()
    }

    private fun goToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}