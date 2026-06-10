package com.fengshui.luopan

import android.content.Context
import android.content.SharedPreferences
import android.provider.Settings
import java.security.MessageDigest
import java.util.*

/**
 * 授权管理器
 * 支持设备绑定授权码激活
 */
class LicenseManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val deviceId: String = getDeviceId(context)

    companion object {
        private const val PREFS_NAME = "luopan_license"
        private const val KEY_LICENSE = "license_key"
        private const val KEY_ACTIVATED = "is_activated"
        private const val KEY_ACTIVATE_TIME = "activate_time"
        private const val KEY_TRIAL_COUNT = "trial_count"
        private const val MAX_TRIAL = 3 // 试用次数

        // 授权码格式: XXXX-XXXX-XXXX-XXXX (基于设备ID生成)
        fun generateLicense(deviceId: String): String {
            val hash = md5(deviceId + "luopan_fengshui_secret_salt_2026")
            val parts = hash.chunked(4).take(4)
            return parts.joinToString("-") { it.uppercase() }
        }

        private fun md5(input: String): String {
            val md = MessageDigest.getInstance("MD5")
            val digest = md.digest(input.toByteArray())
            return digest.joinToString("") { "%02x".format(it) }
        }
    }

    /**
     * 获取设备唯一标识
     */
    private fun getDeviceId(context: Context): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
            ?: UUID.randomUUID().toString()
    }

    /**
     * 获取当前设备的正确授权码
     */
    fun getCorrectLicense(): String {
        return generateLicense(deviceId)
    }

    /**
     * 验证授权码
     */
    fun validateLicense(licenseKey: String): Boolean {
        val normalized = licenseKey.replace("-", "").trim().uppercase()
        val correct = generateLicense(deviceId).replace("-", "")
        return normalized == correct
    }

    /**
     * 激活应用
     */
    fun activate(licenseKey: String): Boolean {
        if (validateLicense(licenseKey)) {
            prefs.edit().apply {
                putString(KEY_LICENSE, licenseKey)
                putBoolean(KEY_ACTIVATED, true)
                putLong(KEY_ACTIVATE_TIME, System.currentTimeMillis())
                apply()
            }
            return true
        }
        return false
    }

    /**
     * 是否已激活
     */
    fun isActivated(): Boolean {
        return prefs.getBoolean(KEY_ACTIVATED, false)
    }

    /**
     * 获取剩余试用次数
     */
    fun getRemainingTrials(): Int {
        val used = prefs.getInt(KEY_TRIAL_COUNT, 0)
        return (MAX_TRIAL - used).coerceAtLeast(0)
    }

    /**
     * 使用一次试用
     */
    fun useTrial(): Boolean {
        val used = prefs.getInt(KEY_TRIAL_COUNT, 0)
        if (used < MAX_TRIAL) {
            prefs.edit().putInt(KEY_TRIAL_COUNT, used + 1).apply()
            return true
        }
        return false
    }

    /**
     * 获取已使用的试用次数
     */
    fun getUsedTrials(): Int {
        return prefs.getInt(KEY_TRIAL_COUNT, 0)
    }

    /**
     * 获取激活时间
     */
    fun getActivateTime(): Long {
        return prefs.getLong(KEY_ACTIVATE_TIME, 0)
    }

    /**
     * 清除授权（调试用）
     */
    fun clearLicense() {
        prefs.edit().clear().apply()
    }
}
