package com.fengshui.luopan

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

/**
 * 激活界面
 * 用户输入授权码激活应用
 */
class ActivateActivity : AppCompatActivity() {

    private lateinit var licenseManager: LicenseManager
    private lateinit var deviceIdText: TextView
    private lateinit var licenseInput: EditText
    private lateinit var activateButton: Button
    private lateinit var trialButton: Button
    private lateinit var copyDeviceIdButton: Button
    private lateinit var remainingTrialsText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_activate)

        licenseManager = LicenseManager(this)

        // 如果已激活，直接进入主界面
        if (licenseManager.isActivated()) {
            startMainActivity()
            return
        }

        initViews()
        updateUI()
    }

    private fun initViews() {
        deviceIdText = findViewById(R.id.deviceIdText)
        licenseInput = findViewById(R.id.licenseInput)
        activateButton = findViewById(R.id.activateButton)
        trialButton = findViewById(R.id.trialButton)
        copyDeviceIdButton = findViewById(R.id.copyDeviceIdButton)
        remainingTrialsText = findViewById(R.id.remainingTrialsText)

        // 只显示设备码（Android ID），不显示授权码
        // 用户需要把设备码发给管理员，由管理员生成授权码
        val deviceId = fetchDeviceId()
        deviceIdText.text = "设备码:\n$deviceId"

        // 复制设备码按钮
        copyDeviceIdButton.setOnClickListener {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("设备码", deviceId)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(this, "设备码已复制到剪贴板", Toast.LENGTH_SHORT).show()
        }

        // 激活按钮
        activateButton.setOnClickListener {
            val inputKey = licenseInput.text.toString().trim()
            if (inputKey.isEmpty()) {
                Toast.makeText(this, "请输入授权码", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (licenseManager.activate(inputKey)) {
                Toast.makeText(this, "激活成功！", Toast.LENGTH_LONG).show()
                startMainActivity()
            } else {
                showErrorDialog("授权码无效", "您输入的授权码不正确。请确认授权码与设备匹配，或联系管理员获取正确的授权码。")
            }
        }

        // 试用按钮
        trialButton.setOnClickListener {
            if (licenseManager.useTrial()) {
                val remaining = licenseManager.getRemainingTrials()
                Toast.makeText(this, "试用模式启动，剩余 $remaining 次", Toast.LENGTH_LONG).show()
                startMainActivity()
            } else {
                showErrorDialog("试用次数已用完", "您的免费试用次数已用完，请输入授权码激活应用。")
            }
        }
    }

    private fun updateUI() {
        val remaining = licenseManager.getRemainingTrials()
        val used = licenseManager.getUsedTrials()

        remainingTrialsText.text = "剩余试用次数: $remaining / ${LicenseManager.MAX_TRIAL}"

        if (remaining <= 0) {
            trialButton.isEnabled = false
            trialButton.text = "试用已用完"
            trialButton.alpha = 0.5f
        }

        if (used > 0) {
            remainingTrialsText.text = "已使用 $used 次试用，剩余 $remaining 次"
        }
    }

    private fun showErrorDialog(title: String, message: String) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("确定", null)
            .show()
    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onBackPressed() {
        // 阻止返回，必须激活或试用才能进入
        if (licenseManager.getRemainingTrials() > 0 || licenseManager.isActivated()) {
            super.onBackPressed()
        } else {
            Toast.makeText(this, "请先激活或试用应用", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * 获取设备唯一标识（Android ID）
     */
    private fun fetchDeviceId(): String {
        return Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
            ?: "未知设备"
    }
}
