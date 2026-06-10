package com.fengshui.luopan

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var luopanView: LuopanView
    private lateinit var degreeText: TextView
    private lateinit var directionText: TextView
    private lateinit var mountainText: TextView
    private lateinit var waterText: TextView
    private lateinit var renMountainText: TextView
    private lateinit var renWaterText: TextView
    private lateinit var fenJinText: TextView
    private lateinit var fenJinStatusText: TextView
    private lateinit var locationText: TextView

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var magnetometer: Sensor? = null

    private val gravityValues = FloatArray(3)
    private val geomagneticValues = FloatArray(3)
    private var hasGravity = false
    private var hasGeomagnetic = false

    private var currentDegree = 0f
    private var smoothDegree = 0f

    // 二十四山 - 地盘/天盘
    private val shan24 = arrayOf(
        "壬", "子", "癸", "丑", "艮", "寅",
        "甲", "卯", "乙", "辰", "巽", "巳",
        "丙", "午", "丁", "未", "坤", "申",
        "庚", "酉", "辛", "戌", "乾", "亥"
    )

    // 人盘二十四山 (中针，与地盘偏移7.5度)
    private val renPan24 = arrayOf(
        "子", "癸", "丑", "艮", "寅", "甲",
        "卯", "乙", "辰", "巽", "巳", "丙",
        "午", "丁", "未", "坤", "申", "庚",
        "酉", "辛", "戌", "乾", "亥", "壬"
    )

    // 一百二十分金
    private val fenJin120 = arrayOf(
        "甲子", "丙子", "戊子", "庚子", "壬子",
        "甲子", "丙子", "戊子", "庚子", "壬子",
        "乙丑", "丁丑", "己丑", "辛丑", "癸丑",
        "丙寅", "戊寅", "庚寅", "壬寅", "甲寅",
        "丙寅", "戊寅", "庚寅", "壬寅", "甲寅",
        "丙寅", "戊寅", "庚寅", "壬寅", "甲寅",
        "丁卯", "己卯", "辛卯", "癸卯", "乙卯",
        "丁卯", "己卯", "辛卯", "癸卯", "乙卯",
        "戊辰", "庚辰", "壬辰", "甲辰", "丙辰",
        "戊辰", "庚辰", "壬辰", "甲辰", "丙辰",
        "己巳", "辛巳", "癸巳", "乙巳", "丁巳",
        "己巳", "辛巳", "癸巳", "乙巳", "丁巳",
        "庚午", "壬午", "甲午", "丙午", "戊午",
        "庚午", "壬午", "甲午", "丙午", "戊午",
        "辛未", "癸未", "乙未", "丁未", "己未",
        "辛未", "癸未", "乙未", "丁未", "己未",
        "壬申", "甲申", "丙申", "戊申", "庚申",
        "壬申", "甲申", "丙申", "戊申", "庚申",
        "癸酉", "乙酉", "丁酉", "己酉", "辛酉",
        "癸酉", "乙酉", "丁酉", "己酉", "辛酉",
        "甲戌", "丙戌", "戊戌", "庚戌", "壬戌",
        "甲戌", "丙戌", "戊戌", "庚戌", "壬戌",
        "乙亥", "丁亥", "己亥", "辛亥", "癸亥",
        "乙亥", "丁亥", "己亥", "辛亥", "癸亥"
    )

    // 方位名称
    private val directions = arrayOf("北", "北偏东", "东北", "东偏北", "东", "东偏南", "东南", "南偏东",
        "南", "南偏西", "西南", "西偏南", "西", "西偏北", "西北", "北偏西")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        luopanView = findViewById(R.id.luopanView)
        degreeText = findViewById(R.id.degreeText)
        directionText = findViewById(R.id.directionText)
        mountainText = findViewById(R.id.mountainText)
        waterText = findViewById(R.id.waterText)
        renMountainText = findViewById(R.id.renMountainText)
        renWaterText = findViewById(R.id.renWaterText)
        fenJinText = findViewById(R.id.fenJinText)
        fenJinStatusText = findViewById(R.id.fenJinStatusText)
        locationText = findViewById(R.id.locationText)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

        if (accelerometer == null || magnetometer == null) {
            Toast.makeText(this, R.string.no_sensor, Toast.LENGTH_LONG).show()
        }

        requestLocationPermission()
    }

    private fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                1001)
        } else {
            startLocationUpdates()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1001 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates()
        }
    }

    private fun startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            try {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000L, 10f, object : LocationListener {
                    override fun onLocationChanged(location: Location) {
                        locationText.text = getString(R.string.location_format, location.longitude, location.latitude)
                    }
                    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
                    override fun onProviderEnabled(provider: String) {}
                    override fun onProviderDisabled(provider: String) {}
                })
            } catch (e: Exception) {
                locationText.text = "无法获取位置"
            }
        }
    }

    override fun onResume() {
        super.onResume()
        accelerometer?.let { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI) }
        magnetometer?.let { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI) }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                System.arraycopy(event.values, 0, gravityValues, 0, 3)
                hasGravity = true
            }
            Sensor.TYPE_MAGNETIC_FIELD -> {
                System.arraycopy(event.values, 0, geomagneticValues, 0, 3)
                hasGeomagnetic = true
            }
        }

        if (hasGravity && hasGeomagnetic) {
            val rotationMatrix = FloatArray(9)
            val orientationValues = FloatArray(3)

            val success = SensorManager.getRotationMatrix(rotationMatrix, null, gravityValues, geomagneticValues)
            if (success) {
                SensorManager.getOrientation(rotationMatrix, orientationValues)
                var degree = Math.toDegrees(orientationValues[0].toDouble()).toFloat()
                if (degree < 0) degree += 360f

                smoothDegree = smoothDegree * 0.85f + degree * 0.15f
                currentDegree = smoothDegree

                updateUI(currentDegree)
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        if (accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE) {
            Toast.makeText(this, R.string.calibrate_hint, Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUI(degree: Float) {
        luopanView.currentDegree = degree

        // 角度和方位
        degreeText.text = getString(R.string.degree_format, degree)
        val directionIndex = ((degree + 11.25f) / 22.5f).toInt() % 16
        directionText.text = directions[directionIndex]

        // 地盘坐山朝向 (偏移7.5度)
        val diShanIndex = ((degree + 7.5f) / 15f).toInt() % 24
        val diShuiIndex = (diShanIndex + 12) % 24
        mountainText.text = "地盘山: ${shan24[diShanIndex]}"
        waterText.text = "地盘水: ${shan24[diShuiIndex]}"

        // 人盘坐山朝向 (中针，偏移15度)
        val renShanIndex = ((degree + 15f) / 15f).toInt() % 24
        val renShuiIndex = (renShanIndex + 12) % 24
        renMountainText.text = "人盘山: ${renPan24[renShanIndex]}"
        renWaterText.text = "人盘水: ${renPan24[renShuiIndex]}"

        // 一百二十分金 (每格3度，共120格)
        val fenJinIndex = (degree / 3f).toInt() % 120
        fenJinText.text = "分金: ${fenJin120[fenJinIndex]}"

        // 分金吉凶判断
        val posInShan = fenJinIndex % 5
        val isGood = posInShan == 0 || posInShan == 2 || posInShan == 4
        if (isGood) {
            fenJinStatusText.text = "吉"
            fenJinStatusText.setTextColor(android.graphics.Color.parseColor("#90EE90"))
            fenJinText.setTextColor(android.graphics.Color.parseColor("#90EE90"))
        } else {
            fenJinStatusText.text = "凶"
            fenJinStatusText.setTextColor(android.graphics.Color.parseColor("#FF6B6B"))
            fenJinText.setTextColor(android.graphics.Color.parseColor("#FF6B6B"))
        }
    }
}
