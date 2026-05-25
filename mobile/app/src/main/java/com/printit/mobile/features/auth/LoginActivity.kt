package com.printit.mobile.features.auth

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.util.Patterns
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.printit.mobile.R
import com.printit.mobile.core.network.RetrofitClient
import com.printit.mobile.core.util.Constants
import com.printit.mobile.features.admin.dashboard.AdminDashboardActivity
import com.printit.mobile.features.staff.dashboard.StaffDashboardActivity
import com.printit.mobile.features.student.dashboard.StudentDashboardActivity
import com.printit.mobile.shared.model.UserResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: TextView
    private lateinit var btnGoogleLogin: TextView
    private lateinit var tvGoToRegister: TextView
    private lateinit var tabRegister: TextView
    private lateinit var tvTogglePassword: TextView

    private var passwordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = getSharedPreferences("printit_prefs", MODE_PRIVATE)

        handleOAuthDeepLink(intent)

        setContentView(R.layout.activity_login)

        bindViews()
        setupButtons()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleOAuthDeepLink(intent)
    }

    private fun bindViews() {
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        btnGoogleLogin = findViewById(R.id.btnGoogleLogin)
        tvGoToRegister = findViewById(R.id.tvGoToRegister)
        tabRegister = findViewById(R.id.tabRegister)
        tvTogglePassword = findViewById(R.id.tvTogglePassword)
    }

    private fun setupButtons() {
        btnLogin.setOnClickListener {
            handleLogin()
        }

        btnGoogleLogin.setOnClickListener {
            openGoogleLogin()
        }

        tvGoToRegister.setOnClickListener {
            openRegister()
        }

        tabRegister.setOnClickListener {
            openRegister()
        }

        tvTogglePassword.setOnClickListener {
            togglePasswordVisibility()
        }
    }

    private fun handleLogin() {
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString()

        if (email.isBlank() || password.isBlank()) {
            Toast.makeText(this, "Email and password are required.", Toast.LENGTH_SHORT).show()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email address.", Toast.LENGTH_SHORT).show()
            return
        }

        btnLogin.isEnabled = false
        btnLogin.text = "Signing in..."

        RetrofitClient.instance.loginUser(LoginRequest(email, password))
            .enqueue(object : Callback<UserResponse> {
                override fun onResponse(
                    call: Call<UserResponse>,
                    response: Response<UserResponse>
                ) {
                    btnLogin.isEnabled = true
                    btnLogin.text = "Sign In to PrintIT"

                    if (response.isSuccessful && response.body() != null) {
                        saveUserAndRedirect(response.body()!!)
                    } else {
                        Toast.makeText(
                            this@LoginActivity,
                            "Login failed. Check your credentials.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                    btnLogin.isEnabled = true
                    btnLogin.text = "Sign In to PrintIT"

                    Toast.makeText(
                        this@LoginActivity,
                        "Login failed: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    private fun openGoogleLogin() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(Constants.GOOGLE_OAUTH_URL))
        startActivity(intent)
    }

    private fun handleOAuthDeepLink(intent: Intent?) {
        val data = intent?.data ?: return

        if (data.scheme != "printit") {
            return
        }

        if (data.host == "oauth-error") {
            val error = data.getQueryParameter("error") ?: "Google login failed."
            Toast.makeText(this, error, Toast.LENGTH_LONG).show()
            return
        }

        if (data.host != "oauth-success") {
            return
        }

        val id = data.getQueryParameter("id")?.toLongOrNull()
        val email = data.getQueryParameter("email")
        val fullName = data.getQueryParameter("fullName")
        val role = data.getQueryParameter("role")
        val username = data.getQueryParameter("username")
        val studentId = data.getQueryParameter("studentId")
        val staffId = data.getQueryParameter("staffId")
        val profileImageUrl = data.getQueryParameter("profileImageUrl")
        val approvalStatus = data.getQueryParameter("approvalStatus")

        if (email.isNullOrBlank() || role.isNullOrBlank()) {
            Toast.makeText(this, "Google login failed.", Toast.LENGTH_SHORT).show()
            return
        }

        val user = UserResponse(
            id = id,
            email = email,
            fullName = fullName,
            username = username,
            password = null,
            role = role,
            studentId = studentId,
            staffId = staffId,
            profileImageUrl = profileImageUrl,
            approvalStatus = approvalStatus
        )

        saveUserAndRedirect(user)
    }

    private fun saveUserAndRedirect(user: UserResponse) {
        val email = user.email ?: ""
        val role = user.role ?: ""

        if (email.isBlank() || role.isBlank()) {
            Toast.makeText(this, "Invalid user response.", Toast.LENGTH_SHORT).show()
            return
        }

        sharedPreferences.edit()
            .putLong("id", user.id ?: 0L)
            .putString("email", email)
            .putString("fullName", user.fullName ?: "")
            .putString("username", user.username ?: "")
            .putString("role", role)
            .putString("studentId", user.studentId ?: "")
            .putString("staffId", user.staffId ?: "")
            .putString("profileImageUrl", user.profileImageUrl ?: "")
            .putString("approvalStatus", user.approvalStatus ?: "")
            .apply()

        Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()
        redirectByRole(role)
    }

    private fun redirectByRole(role: String) {
        val nextScreen = when (role.uppercase()) {
            "ADMIN" -> Intent(this, AdminDashboardActivity::class.java)
            "STAFF" -> Intent(this, StaffDashboardActivity::class.java)
            "STUDENT" -> Intent(this, StudentDashboardActivity::class.java)
            else -> Intent(this, StudentDashboardActivity::class.java)
        }

        startActivity(nextScreen)
        finish()
    }

    private fun openRegister() {
        startActivity(Intent(this, RegisterActivity::class.java))
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
}
