package com.printit.mobile.features.student.neworder

import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.Gravity
import android.view.Window
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.printit.mobile.R
import com.printit.mobile.core.network.RetrofitClient
import com.printit.mobile.features.student.orders.StudentOrderResponse
import com.printit.mobile.features.student.orders.StudentOrdersActivity
import com.printit.mobile.shared.components.MobileTopbarHelper
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.math.BigDecimal

class NewOrderActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var tvProfileBadge: TextView
    private lateinit var tvNotificationBell: TextView
    private lateinit var tvSelectedFile: TextView
    private lateinit var btnChooseFile: TextView
    private lateinit var spPaperSize: Spinner
    private lateinit var btnBlackWhite: TextView
    private lateinit var btnColor: TextView
    private lateinit var etCopies: EditText
    private lateinit var btnSubmitOrder: TextView
    private lateinit var btnViewOrders: TextView

    private var selectedFileUri: Uri? = null
    private var selectedFileName: String = ""
    private var selectedPaperSize: String = "A4"
    private var selectedColorMode: String = "Black & White"

    companion object {
        private const val TAG = "NewOrderActivity"
        private const val MAX_FILE_SIZE_BYTES = 50L * 1024L * 1024L
    }

    private val filePickerLauncher =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            if (uri != null) {
                try {
                    contentResolver.takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                } catch (error: Exception) {
                    Log.w(TAG, "Persist permission not granted: ${error.message}")
                }

                selectedFileUri = uri
                selectedFileName = getFileName(uri)

                if (selectedFileName.isBlank()) {
                    selectedFileName = "Selected file"
                }

                tvSelectedFile.text = selectedFileName
                tvSelectedFile.setTextColor(Color.parseColor("#111827"))
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = getSharedPreferences("printit_prefs", MODE_PRIVATE)

        setContentView(R.layout.activity_new_order)

        bindViews()
        setupTopbar()
        setupPaperSizeDropdown()
        setupColorOptions()
        setupButtons()
    }

    override fun onResume() {
        super.onResume()
        setupTopbar()
    }

    private fun bindViews() {
        tvProfileBadge = findViewById(R.id.tvProfileBadge)
        tvNotificationBell = findViewById(R.id.tvNotificationBell)
        tvSelectedFile = findViewById(R.id.tvSelectedFile)
        btnChooseFile = findViewById(R.id.btnChooseFile)
        spPaperSize = findViewById(R.id.spPaperSize)
        btnBlackWhite = findViewById(R.id.btnBlackWhite)
        btnColor = findViewById(R.id.btnColor)
        etCopies = findViewById(R.id.etCopies)
        btnSubmitOrder = findViewById(R.id.btnSubmitOrder)
        btnViewOrders = findViewById(R.id.btnViewOrders)
    }

    private fun setupTopbar() {
        MobileTopbarHelper.setup(this, tvProfileBadge, tvNotificationBell)
    }

    private fun setupPaperSizeDropdown() {
        val paperSizes = listOf("A4", "Long", "Legal")

        val adapter = ArrayAdapter(
            this,
            R.layout.spinner_selected_item,
            paperSizes
        )

        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)

        spPaperSize.adapter = adapter
        spPaperSize.setSelection(0)
        selectedPaperSize = "A4"

        spPaperSize.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: android.view.View?,
                position: Int,
                id: Long
            ) {
                selectedPaperSize = paperSizes[position]

                if (view is TextView) {
                    view.text = selectedPaperSize
                    view.setTextColor(Color.parseColor("#111827"))
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedPaperSize = "A4"
            }
        }
    }

    private fun setupColorOptions() {
        updateColorSelection("Black & White")

        btnBlackWhite.setOnClickListener {
            updateColorSelection("Black & White")
        }

        btnColor.setOnClickListener {
            updateColorSelection("Color")
        }
    }

    private fun updateColorSelection(colorMode: String) {
        selectedColorMode = colorMode

        if (colorMode == "Black & White") {
            btnBlackWhite.setBackgroundResource(R.drawable.bg_button_maroon)
            btnBlackWhite.setTextColor(Color.WHITE)

            btnColor.setBackgroundResource(R.drawable.bg_button_soft)
            btnColor.setTextColor(Color.parseColor("#111827"))
        } else {
            btnBlackWhite.setBackgroundResource(R.drawable.bg_button_soft)
            btnBlackWhite.setTextColor(Color.parseColor("#111827"))

            btnColor.setBackgroundResource(R.drawable.bg_button_maroon)
            btnColor.setTextColor(Color.WHITE)
        }
    }

    private fun setupButtons() {
        btnChooseFile.setOnClickListener {
            filePickerLauncher.launch(
                arrayOf(
                    "application/pdf",
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
                )
            )
        }

        btnSubmitOrder.setOnClickListener {
            validateThenShowReview()
        }

        btnViewOrders.setOnClickListener {
            startActivity(Intent(this, StudentOrdersActivity::class.java))
        }
    }

    private fun validateThenShowReview() {
        if (selectedFileUri == null || selectedFileName.isBlank()) {
            Toast.makeText(this, "Please choose a file first.", Toast.LENGTH_SHORT).show()
            return
        }

        val lowerFileName = selectedFileName.lowercase()

        if (!lowerFileName.endsWith(".pdf") && !lowerFileName.endsWith(".docx")) {
            Toast.makeText(this, "Only PDF and DOCX files are allowed.", Toast.LENGTH_SHORT).show()
            return
        }

        val paperSize = selectedPaperSize.ifBlank {
            spPaperSize.selectedItem?.toString() ?: "A4"
        }

        val copiesText = etCopies.text.toString().trim()

        if (copiesText.isBlank()) {
            Toast.makeText(this, "Please enter number of copies.", Toast.LENGTH_SHORT).show()
            return
        }

        val copies = copiesText.toIntOrNull()

        if (copies == null || copies <= 0) {
            Toast.makeText(this, "Copies must be greater than 0.", Toast.LENGTH_SHORT).show()
            return
        }

        showReviewDialog(
            fileName = selectedFileName,
            paperSize = paperSize,
            colorMode = selectedColorMode,
            copies = copies
        )
    }

    private fun showReviewDialog(
        fileName: String,
        paperSize: String,
        colorMode: String,
        copies: Int
    ) {
        val pricePerCopy = getPricePerCopy(colorMode)
        val total = pricePerCopy.multiply(BigDecimal.valueOf(copies.toLong()))

        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        val outerRoot = LinearLayout(this)
        outerRoot.orientation = LinearLayout.VERTICAL
        outerRoot.setPadding(dp(18), dp(18), dp(18), dp(18))
        outerRoot.background = createRoundedDrawable("#FFFFFF", 22f)

        val scrollView = ScrollView(this)
        scrollView.isFillViewport = false

        val contentRoot = LinearLayout(this)
        contentRoot.orientation = LinearLayout.VERTICAL

        scrollView.addView(
            contentRoot,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        val title = TextView(this)
        title.text = "Review Your Order"
        title.setTextColor(Color.parseColor("#111827"))
        title.textSize = 22f
        title.setTypeface(null, Typeface.BOLD)
        contentRoot.addView(title)

        val subtitle = TextView(this)
        subtitle.text = "Please confirm the details before submitting."
        subtitle.setTextColor(Color.parseColor("#667085"))
        subtitle.textSize = 14f
        subtitle.setPadding(0, dp(4), 0, dp(18))
        contentRoot.addView(subtitle)

        contentRoot.addView(createSectionTitle("Files (1)"))
        contentRoot.addView(createFileCard(fileName))

        contentRoot.addView(createSpacer(18))

        contentRoot.addView(createSectionTitle("Print Settings"))

        val settingsCard = LinearLayout(this)
        settingsCard.orientation = LinearLayout.VERTICAL
        settingsCard.setPadding(dp(16), dp(14), dp(16), dp(14))
        settingsCard.background = createRoundedDrawable("#F9FAFB", 16f)

        settingsCard.addView(createLabelValueRow("Paper Size", paperSize))
        settingsCard.addView(createDivider())
        settingsCard.addView(createLabelValueRow("Color Mode", colorMode))
        settingsCard.addView(createDivider())
        settingsCard.addView(createLabelValueRow("Copies", copies.toString()))

        contentRoot.addView(settingsCard)

        contentRoot.addView(createSpacer(18))

        contentRoot.addView(createSectionTitle("Price Summary"))

        val priceCard = LinearLayout(this)
        priceCard.orientation = LinearLayout.VERTICAL
        priceCard.setPadding(dp(16), dp(14), dp(16), dp(14))
        priceCard.background = createRoundedDrawable("#F9FAFB", 16f)

        priceCard.addView(
            createPriceRow(
                "P ${formatMoney(pricePerCopy)}/copy × $copies copy(s)",
                "P ${formatMoney(total)}"
            )
        )

        priceCard.addView(createDivider())

        priceCard.addView(
            createPriceRow(
                "Total",
                "P ${formatMoney(total)}",
                true
            )
        )

        contentRoot.addView(priceCard)

        outerRoot.addView(
            scrollView,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        )

        val buttonRow = LinearLayout(this)
        buttonRow.orientation = LinearLayout.HORIZONTAL
        buttonRow.gravity = Gravity.CENTER
        buttonRow.setPadding(0, dp(18), 0, 0)

        val btnPrevious = TextView(this)
        btnPrevious.text = "Previous"
        btnPrevious.gravity = Gravity.CENTER
        btnPrevious.setTextColor(Color.parseColor("#667085"))
        btnPrevious.textSize = 14f
        btnPrevious.setTypeface(null, Typeface.BOLD)
        btnPrevious.background = createStrokeRoundedDrawable("#FFFFFF", "#D0D5DD", 14f)
        btnPrevious.setOnClickListener {
            dialog.dismiss()
        }

        val btnConfirmSubmit = TextView(this)
        btnConfirmSubmit.text = "Submit Order"
        btnConfirmSubmit.gravity = Gravity.CENTER
        btnConfirmSubmit.setTextColor(Color.WHITE)
        btnConfirmSubmit.textSize = 14f
        btnConfirmSubmit.setTypeface(null, Typeface.BOLD)
        btnConfirmSubmit.background = createRoundedDrawable("#9B2C3A", 14f)
        btnConfirmSubmit.setOnClickListener {
            dialog.dismiss()

            submitOrder(
                fileName = fileName,
                paperSize = paperSize,
                colorMode = colorMode,
                copies = copies
            )
        }

        buttonRow.addView(
            btnPrevious,
            LinearLayout.LayoutParams(
                0,
                dp(50),
                1f
            )
        )

        val submitParams = LinearLayout.LayoutParams(
            0,
            dp(50),
            1f
        )
        submitParams.setMargins(dp(10), 0, 0, 0)

        buttonRow.addView(btnConfirmSubmit, submitParams)

        outerRoot.addView(
            buttonRow,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        dialog.setContentView(outerRoot)

        dialog.setOnShowListener {
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.window?.setDimAmount(0.55f)
            dialog.window?.setLayout(
                (resources.displayMetrics.widthPixels * 0.92).toInt(),
                (resources.displayMetrics.heightPixels * 0.78).toInt()
            )
            dialog.window?.setGravity(Gravity.CENTER)
            dialog.window?.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        }

        dialog.show()
    }

    private fun submitOrder(
        fileName: String,
        paperSize: String,
        colorMode: String,
        copies: Int
    ) {
        val email = sharedPreferences.getString("email", "") ?: ""

        if (email.isBlank()) {
            Toast.makeText(this, "No logged-in user found.", Toast.LENGTH_SHORT).show()
            return
        }

        val fileUri = selectedFileUri

        if (fileUri == null) {
            Toast.makeText(this, "Please choose a file first.", Toast.LENGTH_SHORT).show()
            return
        }

        btnSubmitOrder.isEnabled = false
        btnSubmitOrder.text = "Uploading..."

        val inputStream = try {
            contentResolver.openInputStream(fileUri)
        } catch (error: Exception) {
            null
        }

        if (inputStream == null) {
            resetSubmitButton()
            Toast.makeText(this, "Unable to read selected file.", Toast.LENGTH_SHORT).show()
            return
        }

        val fileBytes = try {
            inputStream.use { it.readBytes() }
        } catch (error: Exception) {
            resetSubmitButton()
            Toast.makeText(this, "Unable to prepare selected file.", Toast.LENGTH_SHORT).show()
            return
        }

        if (fileBytes.size > MAX_FILE_SIZE_BYTES) {
            resetSubmitButton()
            Toast.makeText(this, "File must not exceed 50 MB.", Toast.LENGTH_LONG).show()
            return
        }

        val mediaType = when {
            fileName.lowercase().endsWith(".pdf") -> "application/pdf"
            fileName.lowercase().endsWith(".docx") ->
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
            else -> "application/octet-stream"
        }

        val requestBody = fileBytes.toRequestBody(mediaType.toMediaTypeOrNull())

        val filePart = MultipartBody.Part.createFormData(
            "file",
            fileName,
            requestBody
        )

        Log.d(TAG, "Uploading file: $fileName")

        RetrofitClient.instance.uploadStudentOrderFile(filePart)
            .enqueue(object : Callback<OrderFileUploadResponse> {
                override fun onResponse(
                    call: Call<OrderFileUploadResponse>,
                    response: Response<OrderFileUploadResponse>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        val uploadResult = response.body()!!

                        val uploadedFileName = uploadResult.fileName?.takeIf { it.isNotBlank() }
                            ?: fileName

                        val uploadedFileUrl = uploadResult.fileUrl

                        Log.d(TAG, "Upload success. fileName=$uploadedFileName")
                        Log.d(TAG, "Upload success. fileUrl=$uploadedFileUrl")

                        if (uploadedFileUrl.isNullOrBlank()) {
                            resetSubmitButton()

                            Toast.makeText(
                                this@NewOrderActivity,
                                "File uploaded but no download URL was returned.",
                                Toast.LENGTH_LONG
                            ).show()
                            return
                        }

                        createOrderAfterUpload(
                            email = email,
                            fileName = uploadedFileName,
                            fileUrl = uploadedFileUrl,
                            paperSize = paperSize,
                            colorMode = colorMode,
                            copies = copies
                        )
                    } else {
                        val errorBody = response.errorBody()?.string()

                        Log.e(TAG, "Upload failed. Code=${response.code()}")
                        Log.e(TAG, "Upload failed. Error=$errorBody")

                        resetSubmitButton()

                        Toast.makeText(
                            this@NewOrderActivity,
                            if (response.code() == 413) {
                                "File must not exceed 50 MB."
                            } else {
                                "Upload failed: ${response.code()}"
                            },
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<OrderFileUploadResponse>, t: Throwable) {
                    Log.e(TAG, "Upload error", t)

                    resetSubmitButton()

                    Toast.makeText(
                        this@NewOrderActivity,
                        "Upload error: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    private fun createOrderAfterUpload(
        email: String,
        fileName: String,
        fileUrl: String,
        paperSize: String,
        colorMode: String,
        copies: Int
    ) {
        btnSubmitOrder.text = "Submitting..."

        val request = CreateStudentOrderRequest(
            email = email,
            fileName = fileName,
            fileUrl = fileUrl,
            paperSize = paperSize,
            colorMode = colorMode,
            copies = copies
        )

        Log.d(TAG, "Creating order with fileUrl=$fileUrl")

        RetrofitClient.instance.createStudentOrder(request)
            .enqueue(object : Callback<StudentOrderResponse> {
                override fun onResponse(
                    call: Call<StudentOrderResponse>,
                    response: Response<StudentOrderResponse>
                ) {
                    resetSubmitButton()

                    if (response.isSuccessful && response.body() != null) {
                        Log.d(TAG, "Order created successfully.")

                        Toast.makeText(
                            this@NewOrderActivity,
                            "Order submitted successfully.",
                            Toast.LENGTH_SHORT
                        ).show()

                        startActivity(
                            Intent(
                                this@NewOrderActivity,
                                StudentOrdersActivity::class.java
                            )
                        )
                        finish()
                    } else {
                        val errorBody = response.errorBody()?.string()

                        Log.e(TAG, "Create order failed. Code=${response.code()}")
                        Log.e(TAG, "Create order failed. Error=$errorBody")
                        Log.e(TAG, "Create order failed. Sent fileUrl=$fileUrl")

                        Toast.makeText(
                            this@NewOrderActivity,
                            "Failed to submit order: ${response.code()}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<StudentOrderResponse>, t: Throwable) {
                    Log.e(TAG, "Create order error", t)

                    resetSubmitButton()

                    Toast.makeText(
                        this@NewOrderActivity,
                        "Order error: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    private fun resetSubmitButton() {
        btnSubmitOrder.isEnabled = true
        btnSubmitOrder.text = "Review Order"
    }

    private fun getPricePerCopy(colorMode: String): BigDecimal {
        return if (colorMode.equals("Color", ignoreCase = true)) {
            BigDecimal("8.00")
        } else {
            BigDecimal("2.00")
        }
    }

    private fun createSectionTitle(text: String): TextView {
        val view = TextView(this)
        view.text = text
        view.setTextColor(Color.parseColor("#111827"))
        view.textSize = 16f
        view.setTypeface(null, Typeface.BOLD)
        view.setPadding(0, 0, 0, dp(8))
        return view
    }

    private fun createFileCard(text: String): TextView {
        val view = TextView(this)
        view.text = text
        view.setTextColor(Color.parseColor("#111827"))
        view.textSize = 14f
        view.setTypeface(null, Typeface.BOLD)
        view.gravity = Gravity.CENTER_VERTICAL
        view.setPadding(dp(14), 0, dp(14), 0)
        view.background = createRoundedDrawable("#F9FAFB", 14f)
        view.maxLines = 2

        view.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            dp(58)
        )

        return view
    }

    private fun createLabelValueRow(label: String, value: String): LinearLayout {
        val row = LinearLayout(this)
        row.orientation = LinearLayout.HORIZONTAL
        row.gravity = Gravity.CENTER_VERTICAL
        row.setPadding(0, dp(8), 0, dp(8))

        val labelView = TextView(this)
        labelView.text = label
        labelView.setTextColor(Color.parseColor("#667085"))
        labelView.textSize = 13f

        val valueView = TextView(this)
        valueView.text = value
        valueView.setTextColor(Color.parseColor("#111827"))
        valueView.textSize = 14f
        valueView.setTypeface(null, Typeface.BOLD)
        valueView.gravity = Gravity.END

        row.addView(
            labelView,
            LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
        )

        row.addView(
            valueView,
            LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
        )

        return row
    }

    private fun createPriceRow(
        label: String,
        value: String,
        isTotal: Boolean = false
    ): LinearLayout {
        val row = LinearLayout(this)
        row.orientation = LinearLayout.HORIZONTAL
        row.gravity = Gravity.CENTER_VERTICAL
        row.setPadding(0, dp(8), 0, dp(8))

        val labelView = TextView(this)
        labelView.text = label
        labelView.setTextColor(Color.parseColor("#111827"))
        labelView.textSize = if (isTotal) 15f else 14f

        if (isTotal) {
            labelView.setTypeface(null, Typeface.BOLD)
        }

        val valueView = TextView(this)
        valueView.text = value
        valueView.setTextColor(Color.parseColor("#111827"))
        valueView.textSize = if (isTotal) 15f else 14f
        valueView.gravity = Gravity.END
        valueView.setTypeface(null, Typeface.BOLD)

        row.addView(
            labelView,
            LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
        )

        row.addView(
            valueView,
            LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
        )

        return row
    }

    private fun createDivider(): TextView {
        val divider = TextView(this)
        divider.setBackgroundColor(Color.parseColor("#E5E7EB"))

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            dp(1)
        )

        params.setMargins(0, dp(4), 0, dp(4))
        divider.layoutParams = params

        return divider
    }

    private fun createSpacer(height: Int): TextView {
        val spacer = TextView(this)
        spacer.height = dp(height)
        return spacer
    }

    private fun createRoundedDrawable(color: String, radius: Float): GradientDrawable {
        val drawable = GradientDrawable()
        drawable.setColor(Color.parseColor(color))
        drawable.cornerRadius = dp(radius.toInt()).toFloat()
        return drawable
    }

    private fun createStrokeRoundedDrawable(
        color: String,
        strokeColor: String,
        radius: Float
    ): GradientDrawable {
        val drawable = GradientDrawable()
        drawable.setColor(Color.parseColor(color))
        drawable.setStroke(dp(1), Color.parseColor(strokeColor))
        drawable.cornerRadius = dp(radius.toInt()).toFloat()
        return drawable
    }

    private fun formatMoney(value: BigDecimal): String {
        return String.format("%.2f", value)
    }

    private fun getFileName(uri: Uri): String {
        var result = ""

        if (uri.scheme == "content") {
            val cursor = contentResolver.query(uri, null, null, null, null)

            cursor?.use {
                val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)

                if (nameIndex >= 0 && it.moveToFirst()) {
                    result = it.getString(nameIndex) ?: ""
                }
            }
        }

        if (result.isBlank()) {
            result = uri.path?.substringAfterLast("/") ?: ""
        }

        return result
    }

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }
}
