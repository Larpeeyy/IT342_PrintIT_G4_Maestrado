package com.printit.mobile.features.profile

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.printit.mobile.R
import com.printit.mobile.core.network.RetrofitClient
import com.printit.mobile.features.admin.dashboard.AdminDashboardActivity
import com.printit.mobile.features.auth.LoginActivity
import com.printit.mobile.features.staff.dashboard.StaffDashboardActivity
import com.printit.mobile.features.student.dashboard.StudentDashboardActivity
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileSettingsActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var tvProfileBadge: TextView
    private lateinit var tvProfileRole: TextView

    private lateinit var etFullName: EditText
    private lateinit var etUsername: EditText
    private lateinit var etEmail: EditText
    private lateinit var etCurrentPassword: EditText
    private lateinit var etNewPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var tvToggleCurrentPassword: TextView
    private lateinit var tvToggleNewPassword: TextView
    private lateinit var tvToggleConfirmPassword: TextView

    private lateinit var btnSaveProfile: Button
    private lateinit var btnChangePassword: Button
    private lateinit var btnBackDashboard: Button
    private lateinit var btnLogout: Button

    private var profileImageUrl: String? = null
    private var role: String = "STUDENT"
    private var currentPasswordVisible = false
    private var newPasswordVisible = false
    private var confirmPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = getSharedPreferences("printit_prefs", MODE_PRIVATE)

        setContentView(R.layout.activity_profile_settings)

        bindViews()
        setupButtons()
        loadProfile()
    }

    private fun bindViews() {
        tvProfileBadge = findViewById(R.id.tvProfileBadge)
        tvProfileRole = findViewById(R.id.tvProfileRole)

        etFullName = findViewById(R.id.etFullName)
        etUsername = findViewById(R.id.etUsername)
        etEmail = findViewById(R.id.etEmail)
        etCurrentPassword = findViewById(R.id.etCurrentPassword)
        etNewPassword = findViewById(R.id.etNewPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        tvToggleCurrentPassword = findViewById(R.id.tvToggleCurrentPassword)
        tvToggleNewPassword = findViewById(R.id.tvToggleNewPassword)
        tvToggleConfirmPassword = findViewById(R.id.tvToggleConfirmPassword)

        btnSaveProfile = findViewById(R.id.btnSaveProfile)
        btnChangePassword = findViewById(R.id.btnChangePassword)
        btnBackDashboard = findViewById(R.id.btnBackDashboard)
        btnLogout = findViewById(R.id.btnLogout)
    }

    private fun setupButtons() {
        btnSaveProfile.setOnClickListener {
            updateProfile()
        }

        btnChangePassword.setOnClickListener {
            changePassword()
        }

        tvToggleCurrentPassword.setOnClickListener {
            currentPasswordVisible = !currentPasswordVisible
            updatePasswordVisibility(
                etCurrentPassword,
                tvToggleCurrentPassword,
                currentPasswordVisible
            )
        }

        tvToggleNewPassword.setOnClickListener {
            newPasswordVisible = !newPasswordVisible
            updatePasswordVisibility(
                etNewPassword,
                tvToggleNewPassword,
                newPasswordVisible
            )
        }

        tvToggleConfirmPassword.setOnClickListener {
            confirmPasswordVisible = !confirmPasswordVisible
            updatePasswordVisibility(
                etConfirmPassword,
                tvToggleConfirmPassword,
                confirmPasswordVisible
            )
        }

        btnBackDashboard.setOnClickListener {
            goDashboard()
        }

        btnLogout.setOnClickListener {
            sharedPreferences.edit().clear().apply()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun loadProfile() {
        val email = sharedPreferences.getString("email", "") ?: ""

        if (email.isBlank()) {
            Toast.makeText(this, "No logged-in user found.", Toast.LENGTH_SHORT).show()
            return
        }

        RetrofitClient.instance.getProfile(email)
            .enqueue(object : Callback<ProfileResponse> {
                override fun onResponse(
                    call: Call<ProfileResponse>,
                    response: Response<ProfileResponse>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        renderProfile(response.body()!!)
                    } else {
                        Toast.makeText(
                            this@ProfileSettingsActivity,
                            "Failed to load profile.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                    Toast.makeText(
                        this@ProfileSettingsActivity,
                        "Profile error: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    private fun renderProfile(profile: ProfileResponse) {
        val fullName = profile.fullName ?: ""
        val username = profile.username ?: ""
        val email = profile.email ?: ""
        role = profile.role ?: sharedPreferences.getString("role", "STUDENT") ?: "STUDENT"
        profileImageUrl = profile.profileImageUrl

        etFullName.setText(fullName)
        etUsername.setText(username)
        etEmail.setText(email)
        tvProfileRole.text = role
        tvProfileBadge.text = getInitials(fullName)

        sharedPreferences.edit()
            .putString("fullName", fullName)
            .putString("username", username)
            .putString("email", email)
            .putString("role", role)
            .putString("profileImageUrl", profileImageUrl ?: "")
            .apply()
    }

    private fun updateProfile() {
        val email = etEmail.text.toString().trim()
        val fullName = etFullName.text.toString().trim()
        val username = etUsername.text.toString().trim()

        if (fullName.isBlank()) {
            Toast.makeText(this, "Full name is required.", Toast.LENGTH_SHORT).show()
            return
        }

        if (username.isBlank()) {
            Toast.makeText(this, "Username is required.", Toast.LENGTH_SHORT).show()
            return
        }

        val request = UpdateProfileRequest(
            email = email,
            fullName = fullName,
            username = username,
            profileImageUrl = profileImageUrl
        )

        btnSaveProfile.isEnabled = false
        btnSaveProfile.text = "Saving..."

        RetrofitClient.instance.updateProfile(request)
            .enqueue(object : Callback<ProfileResponse> {
                override fun onResponse(
                    call: Call<ProfileResponse>,
                    response: Response<ProfileResponse>
                ) {
                    btnSaveProfile.isEnabled = true
                    btnSaveProfile.text = "Save Changes"

                    if (response.isSuccessful && response.body() != null) {
                        renderProfile(response.body()!!)
                        Toast.makeText(
                            this@ProfileSettingsActivity,
                            "Profile updated successfully.",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            this@ProfileSettingsActivity,
                            "Failed to update profile.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                    btnSaveProfile.isEnabled = true
                    btnSaveProfile.text = "Save Changes"

                    Toast.makeText(
                        this@ProfileSettingsActivity,
                        "Update error: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    private fun changePassword() {
        val email = etEmail.text.toString().trim()
        val currentPassword = etCurrentPassword.text.toString()
        val newPassword = etNewPassword.text.toString()
        val confirmPassword = etConfirmPassword.text.toString()

        if (currentPassword.isBlank()) {
            Toast.makeText(this, "Current password is required.", Toast.LENGTH_SHORT).show()
            return
        }

        if (newPassword.length < 8) {
            Toast.makeText(this, "New password must be at least 8 characters.", Toast.LENGTH_SHORT).show()
            return
        }

        if (newPassword != confirmPassword) {
            Toast.makeText(this, "New passwords do not match.", Toast.LENGTH_SHORT).show()
            return
        }

        val request = ChangePasswordRequest(
            email = email,
            currentPassword = currentPassword,
            newPassword = newPassword
        )

        btnChangePassword.isEnabled = false
        btnChangePassword.text = "Updating..."

        RetrofitClient.instance.changePassword(request)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    btnChangePassword.isEnabled = true
                    btnChangePassword.text = "Update Password"

                    if (response.isSuccessful) {
                        etCurrentPassword.setText("")
                        etNewPassword.setText("")
                        etConfirmPassword.setText("")

                        Toast.makeText(
                            this@ProfileSettingsActivity,
                            "Password updated successfully.",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            this@ProfileSettingsActivity,
                            "Failed to update password.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    btnChangePassword.isEnabled = true
                    btnChangePassword.text = "Update Password"

                    Toast.makeText(
                        this@ProfileSettingsActivity,
                        "Password error: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    private fun goDashboard() {
        val intent = when (role.uppercase()) {
            "ADMIN" -> Intent(this, AdminDashboardActivity::class.java)
            "STAFF" -> Intent(this, StaffDashboardActivity::class.java)
            else -> Intent(this, StudentDashboardActivity::class.java)
        }

        startActivity(intent)
        finish()
    }

    private fun getInitials(name: String): String {
        val parts = name.trim().split(" ").filter { it.isNotBlank() }

        return when {
            parts.size >= 2 -> "${parts[0].first()}${parts[1].first()}".uppercase()
            parts.size == 1 -> parts[0].take(2).uppercase()
            else -> "US"
        }
    }

    private fun updatePasswordVisibility(
        editText: EditText,
        toggleView: TextView,
        visible: Boolean
    ) {
        editText.inputType = if (visible) {
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        } else {
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }

        toggleView.text = if (visible) "🙈" else "👁"
        editText.setSelection(editText.text.length)
    }
}
