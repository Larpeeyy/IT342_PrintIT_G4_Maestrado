package com.printit.mobile.features.auth

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.util.Patterns
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.printit.mobile.R
import com.printit.mobile.core.network.RetrofitClient
import com.printit.mobile.core.util.Constants
import com.printit.mobile.shared.model.UserResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    private lateinit var rbStudent: RadioButton
    private lateinit var rbStaff: RadioButton
    private lateinit var etFullName: EditText
    private lateinit var etEmail: EditText
    private lateinit var tvEmailHelper: TextView
    private lateinit var tvIdLabel: TextView
    private lateinit var etIdNumber: EditText
    private lateinit var tvDepartmentLabel: TextView
    private lateinit var spinnerDepartment: Spinner
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var tvTogglePassword: TextView
    private lateinit var tvToggleConfirmPassword: TextView
    private lateinit var btnRegister: TextView
    private lateinit var btnGoogleRegister: TextView
    private lateinit var tabSignIn: TextView

    private var selectedRole = "STUDENT"
    private var passwordVisible = false
    private var confirmPasswordVisible = false

    private val departments = listOf(
        "Select department...",
        "CCS",
        "CEA",
        "CBA",
        "CAS",
        "COE",
        "SHS",
        "Other"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_register)

        bindViews()
        setupDepartmentSpinner()
        setupButtons()
        updateRoleUI()
    }

    private fun bindViews() {
        rbStudent = findViewById(R.id.rbStudent)
        rbStaff = findViewById(R.id.rbStaff)
        etFullName = findViewById(R.id.etFullName)
        etEmail = findViewById(R.id.etEmail)
        tvEmailHelper = findViewById(R.id.tvEmailHelper)
        tvIdLabel = findViewById(R.id.tvIdLabel)
        etIdNumber = findViewById(R.id.etIdNumber)
        tvDepartmentLabel = findViewById(R.id.tvDepartmentLabel)
        spinnerDepartment = findViewById(R.id.spinnerDepartment)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        tvTogglePassword = findViewById(R.id.tvTogglePassword)
        tvToggleConfirmPassword = findViewById(R.id.tvToggleConfirmPassword)
        btnRegister = findViewById(R.id.btnRegister)
        btnGoogleRegister = findViewById(R.id.btnGoogleRegister)
        tabSignIn = findViewById(R.id.tabSignIn)
    }

    private fun setupDepartmentSpinner() {
        val adapter = ArrayAdapter(
            this,
            R.layout.spinner_selected_item,
            departments
        )

        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)

        spinnerDepartment.adapter = adapter
    }

    private fun setupButtons() {
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

        btnGoogleRegister.setOnClickListener {
            openGoogleLogin()
        }

        tabSignIn.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        tvTogglePassword.setOnClickListener {
            togglePasswordVisibility()
        }

        tvToggleConfirmPassword.setOnClickListener {
            toggleConfirmPasswordVisibility()
        }
    }

    private fun updateRoleUI() {
        if (selectedRole == "STUDENT") {
            rbStudent.setTextColor(android.graphics.Color.parseColor("#9B2C3A"))
            rbStaff.setTextColor(android.graphics.Color.parseColor("#667085"))

            tvEmailHelper.text = "Use your official student university email address."
            tvIdLabel.text = "Student ID"
            etIdNumber.hint = "00-0000-000"
            tvDepartmentLabel.visibility = View.VISIBLE
            spinnerDepartment.visibility = View.VISIBLE
            btnRegister.text = "Create Student Account"
        } else {
            rbStudent.setTextColor(android.graphics.Color.parseColor("#667085"))
            rbStaff.setTextColor(android.graphics.Color.parseColor("#9B2C3A"))

            tvEmailHelper.text = "Use your official staff university email address."
            tvIdLabel.text = "Staff ID"
            etIdNumber.hint = "Enter your staff ID"
            tvDepartmentLabel.visibility = View.VISIBLE
            spinnerDepartment.visibility = View.VISIBLE
            btnRegister.text = "Create Staff Account"
        }
    }

    private fun handleRegister() {
        val fullName = etFullName.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val idNumber = etIdNumber.text.toString().trim()
        val password = etPassword.text.toString()
        val confirmPassword = etConfirmPassword.text.toString()

        if (fullName.isBlank()) {
            Toast.makeText(this, "Full name is required.", Toast.LENGTH_SHORT).show()
            return
        }

        if (email.isBlank()) {
            Toast.makeText(this, "Email is required.", Toast.LENGTH_SHORT).show()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email address.", Toast.LENGTH_SHORT).show()
            return
        }

        if (idNumber.isBlank()) {
            Toast.makeText(
                this,
                if (selectedRole == "STUDENT") "Student ID is required." else "Staff ID is required.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (password.length < 8) {
            Toast.makeText(this, "Password must be at least 8 characters.", Toast.LENGTH_SHORT).show()
            return
        }

        if (password != confirmPassword) {
            Toast.makeText(this, "Passwords do not match.", Toast.LENGTH_SHORT).show()
            return
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

        RetrofitClient.instance.registerUser(request)
            .enqueue(object : Callback<UserResponse> {
                override fun onResponse(
                    call: Call<UserResponse>,
                    response: Response<UserResponse>
                ) {
                    btnRegister.isEnabled = true
                    updateRoleUI()

                    if (response.isSuccessful && response.body() != null) {
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
                            "Registration failed.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                    btnRegister.isEnabled = true
                    updateRoleUI()

                    Toast.makeText(
                        this@RegisterActivity,
                        "Registration failed: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    private fun openGoogleLogin() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(Constants.GOOGLE_OAUTH_URL))
        startActivity(intent)
    }

    private fun togglePasswordVisibility() {
        passwordVisible = !passwordVisible

        if (passwordVisible) {
            etPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            tvTogglePassword.text = "🙈"
        } else {
            etPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            tvTogglePassword.text = "👁"
        }

        etPassword.setSelection(etPassword.text.length)
    }

    private fun toggleConfirmPasswordVisibility() {
        confirmPasswordVisible = !confirmPasswordVisible

        if (confirmPasswordVisible) {
            etConfirmPassword.inputType =
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            tvToggleConfirmPassword.text = "🙈"
        } else {
            etConfirmPassword.inputType =
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            tvToggleConfirmPassword.text = "👁"
        }

        etConfirmPassword.setSelection(etConfirmPassword.text.length)
    }
}
