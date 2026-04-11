package com.printit.mobile.ui

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.printit.mobile.R
import com.printit.mobile.api.RetrofitClient
import com.printit.mobile.model.RegisterRequest
import com.printit.mobile.model.UserResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    private lateinit var rbStudent: RadioButton
    private lateinit var rbStaff: RadioButton
    private lateinit var etFullName: EditText
    private lateinit var etEmail: EditText
    private lateinit var tvIdLabel: TextView
    private lateinit var etIdNumber: EditText
    private lateinit var tvHelper: TextView
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var btnRegister: Button
    private lateinit var tvGoToLogin: TextView

    private var selectedRole = "STUDENT"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        rbStudent = findViewById(R.id.rbStudent)
        rbStaff = findViewById(R.id.rbStaff)
        etFullName = findViewById(R.id.etFullName)
        etEmail = findViewById(R.id.etEmail)
        tvIdLabel = findViewById(R.id.tvIdLabel)
        etIdNumber = findViewById(R.id.etIdNumber)
        tvHelper = findViewById(R.id.tvHelper)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        btnRegister = findViewById(R.id.btnRegister)
        tvGoToLogin = findViewById(R.id.tvGoToLogin)

        updateRoleUI()

        rbStudent.setOnClickListener {
            selectedRole = "STUDENT"
            updateRoleUI()
        }

        rbStaff.setOnClickListener {
            selectedRole = "STAFF"
            updateRoleUI()
        }

        btnRegister.setOnClickListener {
            handleRegister()
        }

        tvGoToLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun updateRoleUI() {
        if (selectedRole == "STUDENT") {
            tvIdLabel.text = "Student ID"
            etIdNumber.hint = "Enter your student ID"
            tvHelper.visibility = View.GONE
        } else {
            tvIdLabel.text = "Staff ID"
            etIdNumber.hint = "Enter your staff ID"
            tvHelper.visibility = View.VISIBLE
        }
    }

    private fun handleRegister() {
        val fullName = etFullName.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val idNumber = etIdNumber.text.toString().trim()
        val password = etPassword.text.toString()
        val confirmPassword = etConfirmPassword.text.toString()

        when {
            fullName.isEmpty() -> {
                Toast.makeText(this, "Full Name is required.", Toast.LENGTH_SHORT).show()
                return
            }
            email.isEmpty() -> {
                Toast.makeText(this, "Email Address is required.", Toast.LENGTH_SHORT).show()
                return
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                Toast.makeText(this, "Please enter a valid email address.", Toast.LENGTH_SHORT).show()
                return
            }
            idNumber.isEmpty() -> {
                Toast.makeText(
                    this,
                    if (selectedRole == "STUDENT") "Student ID is required." else "Staff ID is required.",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }
            password.isEmpty() -> {
                Toast.makeText(this, "Password is required.", Toast.LENGTH_SHORT).show()
                return
            }
            confirmPassword.isEmpty() -> {
                Toast.makeText(this, "Confirm Password is required.", Toast.LENGTH_SHORT).show()
                return
            }
            password != confirmPassword -> {
                Toast.makeText(this, "Passwords do not match.", Toast.LENGTH_SHORT).show()
                return
            }
        }

        btnRegister.isEnabled = false
        btnRegister.text = "Creating..."

        val request = RegisterRequest(
            fullName = fullName,
            email = email,
            password = password,
            role = selectedRole,
            studentId = if (selectedRole == "STUDENT") idNumber else null,
            staffId = if (selectedRole == "STAFF") idNumber else null
        )

        RetrofitClient.instance.registerUser(request).enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                btnRegister.isEnabled = true
                btnRegister.text = "Register"

                if (response.isSuccessful && response.body() != null) {
                    val user = response.body()!!

                    if (user.id != null) {
                        Toast.makeText(
                            this@RegisterActivity,
                            "Registration successful. You can now login.",
                            Toast.LENGTH_LONG
                        ).show()

                        startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(
                            this@RegisterActivity,
                            "Registration done, but no user returned.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this@RegisterActivity,
                        "Registration failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                btnRegister.isEnabled = true
                btnRegister.text = "Register"
                Toast.makeText(
                    this@RegisterActivity,
                    "Registration failed: ${t.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }
}