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
import kotlin.math.atan2
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var luopanView: LuopanView
    private lateinit var degreeText: TextView
    private lateinit var directionText: TextView
    private lateinit var mountainText: TextView
    private lateinit var waterText: TextView
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

    // 二十四山
    private val shan24 = arrayOf(
        "壬", "子", "癸", "丑", "艮", "寅",
        "甲", "卯", "乙", "辰", "巽", "巳",
        "丙", "午", "丁", "未", "坤", "申",
        "庚", "酉", "辛", "戌", "乾", "亥"
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
        locationText = findViewById(R.id.locationText)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

        if (accelerometer == null || magnetometer == null) {
            Toast.makeText(this, R.string.no_sensor, Toast.LENGTH_LONG).show()
        }

        // 请求位置权限
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

                // 平滑处理
                smoothDegree = smoothDegree * 0.85f + degree * 0.15f
                currentDegree = smoothDegree

                // 更新UI
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
        // 更新罗盘视图
        luopanView.currentDegree = degree

        // 更新角度显示
        degreeText.text = getString(R.string.degree_format, degree)

        // 更新方位
        val directionIndex = ((degree + 11.25f) / 22.5f).toInt() % 16
        directionText.text = directions[directionIndex]

        // 更新山/水 (坐山朝向)
        val shanIndex = ((degree + 7.5f) / 15f).toInt() % 24
        val shuiIndex = (shanIndex + 12) % 24
        mountainText.text = "山: ${shan24[shanIndex]}"
        waterText.text = "水: ${shan24[shuiIndex]}"
    }
}
