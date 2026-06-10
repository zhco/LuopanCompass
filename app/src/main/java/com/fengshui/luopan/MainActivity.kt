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
import java.util.Calendar

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
    private lateinit var chuanShanText: TextView
    private lateinit var chuanShanStatusText: TextView
    private lateinit var touDiText: TextView
    private lateinit var touDiStatusText: TextView
    private lateinit var changShengText: TextView
    private lateinit var changShengStatusText: TextView
    private lateinit var xiu28Text: TextView
    private lateinit var xiu28XiangText: TextView
    private lateinit var jiuXingText: TextView
    private lateinit var jiuXingWuXingText: TextView
    private lateinit var siZhuText: TextView
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

    // 穿山七十二龙
    private val chuanShan72 = arrayOf(
        "甲子", "丙子", "戊子",
        "庚子", "壬子", "空亡",
        "乙丑", "丁丑", "己丑",
        "辛丑", "癸丑", "空亡",
        "丙寅", "戊寅", "庚寅",
        "壬寅", "甲寅", "空亡",
        "丁卯", "己卯", "辛卯",
        "癸卯", "乙卯", "空亡",
        "戊辰", "庚辰", "壬辰",
        "甲辰", "丙辰", "空亡",
        "己巳", "辛巳", "癸巳",
        "乙巳", "丁巳", "空亡",
        "庚午", "壬午", "甲午",
        "丙午", "戊午", "空亡",
        "辛未", "癸未", "乙未",
        "丁未", "己未", "空亡",
        "壬申", "甲申", "丙申",
        "戊申", "庚申", "空亡",
        "癸酉", "乙酉", "丁酉",
        "己酉", "辛酉", "空亡",
        "甲戌", "丙戌", "戊戌",
        "庚戌", "壬戌", "空亡",
        "乙亥", "丁亥", "己亥",
        "辛亥", "癸亥", "空亡"
    )

    // 透地六十龙
    private val touDi60 = arrayOf(
        "甲子", "丙子",
        "戊子", "庚子", "壬子",
        "乙丑", "丁丑",
        "己丑", "辛丑", "癸丑",
        "丙寅", "戊寅",
        "庚寅", "壬寅", "甲寅",
        "丁卯", "己卯",
        "辛卯", "癸卯", "乙卯",
        "戊辰", "庚辰",
        "壬辰", "甲辰", "丙辰",
        "己巳", "辛巳",
        "癸巳", "乙巳", "丁巳",
        "庚午", "壬午",
        "甲午", "丙午", "戊午",
        "辛未", "癸未",
        "乙未", "丁未", "己未",
        "壬申", "甲申",
        "丙申", "戊申", "庚申",
        "癸酉", "乙酉",
        "丁酉", "己酉", "辛酉",
        "甲戌", "丙戌",
        "戊戌", "庚戌", "壬戌",
        "乙亥", "丁亥",
        "己亥", "辛亥", "癸亥"
    )

    // 十二长生宫
    private val changSheng12 = arrayOf(
        "长生", "沐浴", "冠带", "临官", "帝旺", "衰",
        "病", "死", "墓", "绝", "胎", "养"
    )

    // 十二长生宫对应的地支（以火局为例，从寅开始长生）
    private val changShengZhi = arrayOf("寅", "卯", "辰", "巳", "午", "未", "申", "酉", "戌", "亥", "子", "丑")

    // 二十八宿
    private val xiu28 = arrayOf(
        "角", "亢", "氐", "房", "心", "尾", "箕",
        "斗", "牛", "女", "虚", "危", "室", "壁",
        "奎", "娄", "胃", "昴", "毕", "觜", "参",
        "井", "鬼", "柳", "星", "张", "翼", "轸"
    )

    // 二十八宿度数分配（每宿度数不同，总计360度）
    private val xiu28Degrees = floatArrayOf(
        12.5f, 10.0f, 15.0f, 5.0f, 5.0f, 18.0f, 11.0f,
        26.0f, 8.0f, 12.0f, 10.0f, 17.0f, 16.0f, 9.0f,
        16.0f, 12.0f, 14.0f, 11.0f, 16.0f, 2.0f, 9.0f,
        33.0f, 3.0f, 15.0f, 7.0f, 18.0f, 18.0f, 17.0f
    )

    // 二十八宿四象
    private val xiu28Xiang = arrayOf(
        "东方青龙", "东方青龙", "东方青龙", "东方青龙", "东方青龙", "东方青龙", "东方青龙",
        "北方玄武", "北方玄武", "北方玄武", "北方玄武", "北方玄武", "北方玄武", "北方玄武",
        "西方白虎", "西方白虎", "西方白虎", "西方白虎", "西方白虎", "西方白虎", "西方白虎",
        "南方朱雀", "南方朱雀", "南方朱雀", "南方朱雀", "南方朱雀", "南方朱雀", "南方朱雀"
    )

    // 九星
    private val jiuXing = arrayOf(
        "贪狼", "巨门", "禄存", "文曲", "廉贞", "武曲", "破军", "左辅", "右弼"
    )

    // 九星五行属性
    private val jiuXingWuXing = arrayOf(
        "木", "土", "土", "水", "火", "金", "金", "土", "金"
    )

    // 九星吉凶
    private val jiuXingJiXiong = arrayOf(
        "吉", "凶", "凶", "凶", "凶", "吉", "凶", "吉", "吉"
    )

    // 天干
    private val tianGan = arrayOf("甲", "乙", "丙", "丁", "戊", "己", "庚", "辛", "壬", "癸")

    // 地支
    private val diZhi = arrayOf("子", "丑", "寅", "卯", "辰", "巳", "午", "未", "申", "酉", "戌", "亥")

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
        chuanShanText = findViewById(R.id.chuanShanText)
        chuanShanStatusText = findViewById(R.id.chuanShanStatusText)
        touDiText = findViewById(R.id.touDiText)
        touDiStatusText = findViewById(R.id.touDiStatusText)
        changShengText = findViewById(R.id.changShengText)
        changShengStatusText = findViewById(R.id.changShengStatusText)
        xiu28Text = findViewById(R.id.xiu28Text)
        xiu28XiangText = findViewById(R.id.xiu28XiangText)
        jiuXingText = findViewById(R.id.jiuXingText)
        jiuXingWuXingText = findViewById(R.id.jiuXingWuXingText)
        siZhuText = findViewById(R.id.siZhuText)
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
        val posInShan = fenJinIndex % 5
        val fenJinGood = posInShan == 0 || posInShan == 2 || posInShan == 4
        if (fenJinGood) {
            fenJinStatusText.text = "吉"
            fenJinStatusText.setTextColor(android.graphics.Color.parseColor("#90EE90"))
            fenJinText.setTextColor(android.graphics.Color.parseColor("#90EE90"))
        } else {
            fenJinStatusText.text = "凶"
            fenJinStatusText.setTextColor(android.graphics.Color.parseColor("#FF6B6B"))
            fenJinText.setTextColor(android.graphics.Color.parseColor("#FF6B6B"))
        }

        // 穿山七十二龙 (每龙5度，共72龙)
        val chuanShanIndex = (degree / 5f).toInt() % 72
        val chuanShanName = chuanShan72[chuanShanIndex]
        chuanShanText.text = "穿山: $chuanShanName"
        if (chuanShanName == "空亡") {
            chuanShanStatusText.text = "空亡"
            chuanShanStatusText.setTextColor(android.graphics.Color.parseColor("#FF0000"))
            chuanShanText.setTextColor(android.graphics.Color.parseColor("#FF0000"))
        } else {
            chuanShanStatusText.text = "可用"
            chuanShanStatusText.setTextColor(android.graphics.Color.parseColor("#87CEEB"))
            chuanShanText.setTextColor(android.graphics.Color.parseColor("#87CEEB"))
        }

        // 透地六十龙 (每龙6度，共60龙)
        val touDiIndex = (degree / 6f).toInt() % 60
        val touDiName = touDi60[touDiIndex]
        touDiText.text = "透地: $touDiName"
        val gan = touDiName[0]
        val touDiGood = gan in listOf('甲', '丙', '戊', '庚', '壬')
        if (touDiGood) {
            touDiStatusText.text = "吉"
            touDiStatusText.setTextColor(android.graphics.Color.parseColor("#DDA0DD"))
            touDiText.setTextColor(android.graphics.Color.parseColor("#DDA0DD"))
        } else {
            touDiStatusText.text = "凶"
            touDiStatusText.setTextColor(android.graphics.Color.parseColor("#FF6B6B"))
            touDiText.setTextColor(android.graphics.Color.parseColor("#FF6B6B"))
        }

        // 十二长生宫 (每宫30度，共12宫)
        val changShengIndex = (degree / 30f).toInt() % 12
        val changShengName = changSheng12[changShengIndex]
        val changShengZhiName = changShengZhi[changShengIndex]
        changShengText.text = "$changShengName: $changShengZhiName"
        val changShengGood = changShengIndex == 0 || changShengIndex == 2 || changShengIndex == 3 || changShengIndex == 4
        if (changShengGood) {
            changShengStatusText.text = "吉"
            changShengStatusText.setTextColor(android.graphics.Color.parseColor("#FFA500"))
            changShengText.setTextColor(android.graphics.Color.parseColor("#FFA500"))
        } else {
            changShengStatusText.text = "平"
            changShengStatusText.setTextColor(android.graphics.Color.parseColor("#DAA520"))
            changShengText.setTextColor(android.graphics.Color.parseColor("#DAA520"))
        }

        // 二十八宿 (每宿度数不同)
        var xiuIndex = 0
        var accumulatedDegrees = 0f
        for (i in xiu28Degrees.indices) {
            accumulatedDegrees += xiu28Degrees[i]
            if (degree < accumulatedDegrees) {
                xiuIndex = i
                break
            }
        }
        xiu28Text.text = "二十八宿: ${xiu28[xiuIndex]}"
        xiu28XiangText.text = xiu28Xiang[xiuIndex]
        xiu28Text.setTextColor(android.graphics.Color.parseColor("#98FB98"))
        xiu28XiangText.setTextColor(android.graphics.Color.parseColor("#98FB98"))

        // 九星 (每星40度)
        val jiuXingIndex = (degree / 40f).toInt() % 9
        val jiuXingName = jiuXing[jiuXingIndex]
        val jiuXingWX = jiuXingWuXing[jiuXingIndex]
        val jiuXingJX = jiuXingJiXiong[jiuXingIndex]
        jiuXingText.text = "九星: $jiuXingName ($jiuXingWX)"
        jiuXingWuXingText.text = jiuXingJX
        if (jiuXingJX == "吉") {
            jiuXingText.setTextColor(android.graphics.Color.parseColor("#FFD700"))
            jiuXingWuXingText.setTextColor(android.graphics.Color.parseColor("#FFD700"))
        } else {
            jiuXingText.setTextColor(android.graphics.Color.parseColor("#8B4513"))
            jiuXingWuXingText.setTextColor(android.graphics.Color.parseColor("#8B4513"))
        }

        // 四柱干支
        val siZhu = calculateSiZhu()
        siZhuText.text = "四柱: $siZhu"
        siZhuText.setTextColor(android.graphics.Color.parseColor("#DDA0DD"))
    }

    /**
     * 计算当前四柱干支（简化算法）
     * 使用近似的干支计算
     */
    private fun calculateSiZhu(): String {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val hour = calendar.get(Calendar.HOUR_OF_DAY)

        // 年柱（以1984年为甲子年）
        val yearGanIndex = (year - 1984) % 10
        val yearZhiIndex = (year - 1984) % 12
        val yearZhu = tianGan[if (yearGanIndex >= 0) yearGanIndex else yearGanIndex + 10] +
                diZhi[if (yearZhiIndex >= 0) yearZhiIndex else yearZhiIndex + 12]

        // 月柱（简化：正月为寅）
        val monthGanIndex = ((year - 1984) % 10 * 2 + month + 1) % 10
        val monthZhu = tianGan[monthGanIndex] + diZhi[(month + 1) % 12]

        // 日柱（简化公式）
        val baseDate = java.util.GregorianCalendar(1900, 0, 31).timeInMillis
        val currentDate = calendar.timeInMillis
        val diffDays = ((currentDate - baseDate) / (1000 * 60 * 60 * 24)).toInt()
        val dayGanIndex = diffDays % 10
        val dayZhiIndex = diffDays % 12
        val dayZhu = tianGan[dayGanIndex] + diZhi[dayZhiIndex]

        // 时柱
        val shiZhiIndex = ((hour + 1) / 2) % 12
        val shiGanIndex = (dayGanIndex * 2 + shiZhiIndex) % 10
        val shiZhu = tianGan[shiGanIndex] + diZhi[shiZhiIndex]

        return "$yearZhu年 $monthZhu月 $dayZhu日 $shiZhu时"
    }
}
