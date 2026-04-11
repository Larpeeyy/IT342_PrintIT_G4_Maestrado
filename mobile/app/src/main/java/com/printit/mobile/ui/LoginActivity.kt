package com.printit.mobile.ui

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.printit.mobile.R
import com.printit.mobile.api.RetrofitClient
import com.printit.mobile.model.LoginRequest
import com.printit.mobile.model.UserResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var tvGoToRegister: TextView
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = getSharedPreferences("printit_prefs", MODE_PRIVATE)

        val savedEmail = sharedPreferences.getString("email", null)
        val savedRole = sharedPreferences.getString("role", null)

        if (!savedEmail.isNullOrEmpty() && !savedRole.isNullOrEmpty()) {
            redirectByRole(savedRole)
            return
        }

        setContentView(R.layout.activity_login)

        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        tvGoToRegister = findViewById(R.id.tvGoToRegister)

        btnLogin.setOnClickListener {
            handleLogin()
        }

        tvGoToRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun handleLogin() {
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Email and Password are required.", Toast.LENGTH_SHORT).show()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email address.", Toast.LENGTH_SHORT).show()
            return
        }

        btnLogin.isEnabled = false
        btnLogin.text = "Logging in..."

        val request = LoginRequest(email, password)

        RetrofitClient.instance.loginUser(request).enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                btnLogin.isEnabled = true
                btnLogin.text = "Login"

                if (response.isSuccessful && response.body() != null) {
                    val user = response.body()!!

                    if (user.email.isNullOrEmpty() || user.role.isNullOrEmpty()) {
                        Toast.makeText(
                            this@LoginActivity,
                            "Invalid credentials.",
                            Toast.LENGTH_SHORT
                        ).show()
                        return
                    }

                    sharedPreferences.edit()
                        .putString("email", user.email)
                        .putString("fullName", user.fullName ?: "")
                        .putString("role", user.role)
                        .apply()

                    Toast.makeText(this@LoginActivity, "Login successful!", Toast.LENGTH_SHORT).show()
                    redirectByRole(user.role!!)
                } else {
                    Toast.makeText(
                        this@LoginActivity,
                        "Login failed. Check credentials.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                btnLogin.isEnabled = true
                btnLogin.text = "Login"
                Toast.makeText(
                    this@LoginActivity,
                    "Login failed: ${t.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun redirectByRole(role: String) {
        val intent = when (role.uppercase()) {
            "STAFF" -> Intent(this, StaffDashboardActivity::class.java)
            "STUDENT" -> Intent(this, StudentDashboardActivity::class.java)
            else -> Intent(this, StudentDashboardActivity::class.java)
        }

        startActivity(intent)
        finish()
    }
}