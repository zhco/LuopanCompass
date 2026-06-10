package com.fengshui.luopan

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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
    private lateinit var ziBaiText: TextView
    private lateinit var ziBaiStatusText: TextView
    private lateinit var gua64Text: TextView
    private lateinit var gua64XiangText: TextView
    private lateinit var shenShaText: TextView
    private lateinit var shenShaStatusText: TextView
    private lateinit var baShaText: TextView
    private lateinit var baShaStatusText: TextView
    private lateinit var siZhuText: TextView
    private lateinit var locationText: TextView
    private lateinit var magDeclText: TextView
    private lateinit var levelText: TextView
    private lateinit var naYinText: TextView
    private lateinit var saveButton: Button
    private lateinit var historyButton: Button

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var magnetometer: Sensor? = null
    private var gyroscope: Sensor? = null

    private val gravityValues = FloatArray(3)
    private val geomagneticValues = FloatArray(3)
    private var hasGravity = false
    private var hasGeomagnetic = false

    private var currentDegree = 0f
    private var smoothDegree = 0f
    private var magDeclination = 0f
    private var isMagDeclEnabled = false

    private lateinit var sharedPrefs: SharedPreferences
    private val PREFS_NAME = "luopan_records"
    private val RECORDS_KEY = "records"

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

    // 紫白飞星
    private val ziBai = arrayOf(
        "一白", "二黑", "三碧", "四绿", "五黄", "六白", "七赤", "八白", "九紫"
    )

    // 紫白飞星五行
    private val ziBaiWuXing = arrayOf(
        "水", "土", "木", "木", "土", "金", "金", "土", "火"
    )

    // 紫白飞星吉凶
    private val ziBaiJiXiong = arrayOf(
        "吉", "凶", "凶", "吉", "大凶", "吉", "凶", "吉", "吉"
    )

    // 六十四卦
    private val gua64 = arrayOf(
        "乾", "坤", "屯", "蒙", "需", "讼", "师", "比",
        "小畜", "履", "泰", "否", "同人", "大有", "谦", "豫",
        "随", "蛊", "临", "观", "噬嗑", "贲", "剥", "复",
        "无妄", "大畜", "颐", "大过", "坎", "离", "咸", "恒",
        "遁", "大壮", "晋", "明夷", "家人", "睽", "蹇", "解",
        "损", "益", "夬", "姤", "萃", "升", "困", "井",
        "革", "鼎", "震", "艮", "渐", "归妹", "丰", "旅",
        "巽", "兑", "涣", "节", "中孚", "小过", "既济", "未济"
    )

    // 六十四卦卦象
    private val gua64Xiang = arrayOf(
        "乾为天", "坤为地", "水雷屯", "山水蒙", "水天需", "天水讼", "地水师", "水地比",
        "风天小畜", "天泽履", "地天泰", "天地否", "天火同人", "火天大有", "地山谦", "雷地豫",
        "泽雷随", "山风蛊", "地泽临", "风地观", "火雷噬嗑", "山火贲", "山地剥", "地雷复",
        "天雷无妄", "山天大畜", "山雷颐", "泽风大过", "坎为水", "离为火", "泽山咸", "雷风恒",
        "天山遁", "雷天大壮", "火地晋", "地火明夷", "风火家人", "火泽睽", "水山蹇", "雷水解",
        "山泽损", "风雷益", "泽天夬", "天风姤", "泽地萃", "地风升", "泽水困", "水风井",
        "泽火革", "火风鼎", "震为雷", "艮为山", "风山渐", "雷泽归妹", "雷火丰", "火山旅",
        "巽为风", "兑为泽", "风水涣", "水泽节", "风泽中孚", "雷山小过", "水火既济", "火水未济"
    )

    // 神煞名称
    private val shenShaNames = arrayOf(
        "太岁", "劫煞", "灾煞", "岁煞", "伏兵", "大祸",
        "天煞", "地煞", "年煞", "月煞", "日煞", "时煞",
        "三煞", "五黄", "二黑", "七赤", "九紫", "一白",
        "四绿", "六白", "八白", "三碧", "太岁", "劫煞"
    )

    // 神煞吉凶
    private val shenShaJiXiong = arrayOf(
        "凶", "凶", "凶", "凶", "凶", "凶",
        "凶", "凶", "凶", "凶", "凶", "凶",
        "大凶", "大凶", "凶", "凶", "吉", "吉",
        "吉", "吉", "吉", "凶", "凶", "凶"
    )

    // 八煞黄泉
    private val baSha = arrayOf(
        "坎龙", "坤兔", "震猴", "巽鸡", "乾马", "兑蛇", "艮虎", "离猪"
    )

    // 八煞吉凶
    private val baShaJiXiong = arrayOf(
        "煞", "煞", "煞", "煞", "煞", "煞", "煞", "煞"
    )

    // 纳音五行（六十甲子纳音）
    private val naYin60 = arrayOf(
        "海中金", "海中金", "海中金", "海中金", "海中金", "海中金",
        "炉中火", "炉中火", "炉中火", "炉中火", "炉中火", "炉中火",
        "大林木", "大林木", "大林木", "大林木", "大林木", "大林木",
        "路旁土", "路旁土", "路旁土", "路旁土", "路旁土", "路旁土",
        "剑锋金", "剑锋金", "剑锋金", "剑锋金", "剑锋金", "剑锋金",
        "山头火", "山头火", "山头火", "山头火", "山头火", "山头火",
        "涧下水", "涧下水", "涧下水", "涧下水", "涧下水", "涧下水",
        "城头土", "城头土", "城头土", "城头土", "城头土", "城头土",
        "白蜡金", "白蜡金", "白蜡金", "白蜡金", "白蜡金", "白蜡金",
        "杨柳木", "杨柳木", "杨柳木", "杨柳木", "杨柳木", "杨柳木"
    )

    // 纳音五行属性
    private val naYinWuXing = arrayOf(
        "金", "金", "金", "金", "金", "金",
        "火", "火", "火", "火", "火", "火",
        "木", "木", "木", "木", "木", "木",
        "土", "土", "土", "土", "土", "土",
        "金", "金", "金", "金", "金", "金",
        "火", "火", "火", "火", "火", "火",
        "水", "水", "水", "水", "水", "水",
        "土", "土", "土", "土", "土", "土",
        "金", "金", "金", "金", "金", "金",
        "木", "木", "木", "木", "木", "木"
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
        ziBaiText = findViewById(R.id.ziBaiText)
        ziBaiStatusText = findViewById(R.id.ziBaiStatusText)
        gua64Text = findViewById(R.id.gua64Text)
        gua64XiangText = findViewById(R.id.gua64XiangText)
        shenShaText = findViewById(R.id.shenShaText)
        shenShaStatusText = findViewById(R.id.shenShaStatusText)
        baShaText = findViewById(R.id.baShaText)
        baShaStatusText = findViewById(R.id.baShaStatusText)
        siZhuText = findViewById(R.id.siZhuText)
        locationText = findViewById(R.id.locationText)
        magDeclText = findViewById(R.id.magDeclText)
        levelText = findViewById(R.id.levelText)
        naYinText = findViewById(R.id.naYinText)
        saveButton = findViewById(R.id.saveButton)
        historyButton = findViewById(R.id.historyButton)

        sharedPrefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

        if (accelerometer == null || magnetometer == null) {
            Toast.makeText(this, R.string.no_sensor, Toast.LENGTH_LONG).show()
        }

        // 磁偏角校正点击
        magDeclText.setOnClickListener {
            showMagDeclDialog()
        }

        // 保存记录按钮
        saveButton.setOnClickListener {
            saveCurrentRecord()
        }

        // 历史记录按钮
        historyButton.setOnClickListener {
            showHistoryDialog()
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
        gyroscope?.let { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI) }
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

                // 水平仪检测
                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]
                val pitch = Math.toDegrees(Math.atan2(x.toDouble(), Math.sqrt((y * y + z * z).toDouble()))).toFloat()
                val roll = Math.toDegrees(Math.atan2(y.toDouble(), Math.sqrt((x * x + z * z).toDouble()))).toFloat()
                val isLevel = Math.abs(pitch) < 2 && Math.abs(roll) < 2
                levelText.text = if (isLevel) "水平: 正常" else "水平: 倾斜 %.1f° %.1f°".format(Math.abs(pitch), Math.abs(roll))
                levelText.setTextColor(android.graphics.Color.parseColor(if (isLevel) "#90EE90" else "#FF6B6B"))
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

                // 应用磁偏角校正
                if (isMagDeclEnabled) {
                    degree += magDeclination
                    if (degree >= 360f) degree -= 360f
                    if (degree < 0) degree += 360f
                }

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

        // 紫白飞星 (每星40度)
        val ziBaiIndex = (degree / 40f).toInt() % 9
        val ziBaiName = ziBai[ziBaiIndex]
        val ziBaiWX = ziBaiWuXing[ziBaiIndex]
        val ziBaiJX = ziBaiJiXiong[ziBaiIndex]
        ziBaiText.text = "紫白: $ziBaiName ($ziBaiWX)"
        ziBaiStatusText.text = ziBaiJX
        when (ziBaiJX) {
            "吉" -> {
                ziBaiText.setTextColor(android.graphics.Color.parseColor("#90EE90"))
                ziBaiStatusText.setTextColor(android.graphics.Color.parseColor("#90EE90"))
            }
            "大凶" -> {
                ziBaiText.setTextColor(android.graphics.Color.parseColor("#FF0000"))
                ziBaiStatusText.setTextColor(android.graphics.Color.parseColor("#FF0000"))
            }
            else -> {
                ziBaiText.setTextColor(android.graphics.Color.parseColor("#DAA520"))
                ziBaiStatusText.setTextColor(android.graphics.Color.parseColor("#DAA520"))
            }
        }

        // 六十四卦 (每卦5.625度)
        val guaIndex = (degree / 5.625f).toInt() % 64
        val guaName = gua64[guaIndex]
        val guaXiangName = gua64Xiang[guaIndex]
        gua64Text.text = "六十四卦: $guaName"
        gua64XiangText.text = guaXiangName
        gua64Text.setTextColor(android.graphics.Color.parseColor("#87CEEB"))
        gua64XiangText.setTextColor(android.graphics.Color.parseColor("#87CEEB"))

        // 神煞 (每山15度)
        val shenShaIndex = ((degree + 7.5f) / 15f).toInt() % 24
        val shenShaName = shenShaNames[shenShaIndex]
        val shenShaJX = shenShaJiXiong[shenShaIndex]
        shenShaText.text = "神煞: $shenShaName"
        shenShaStatusText.text = shenShaJX
        when (shenShaJX) {
            "吉" -> {
                shenShaText.setTextColor(android.graphics.Color.parseColor("#90EE90"))
                shenShaStatusText.setTextColor(android.graphics.Color.parseColor("#90EE90"))
            }
            "大凶" -> {
                shenShaText.setTextColor(android.graphics.Color.parseColor("#FF0000"))
                shenShaStatusText.setTextColor(android.graphics.Color.parseColor("#FF0000"))
            }
            else -> {
                shenShaText.setTextColor(android.graphics.Color.parseColor("#DAA520"))
                shenShaStatusText.setTextColor(android.graphics.Color.parseColor("#DAA520"))
            }
        }

        // 八煞黄泉 (每煞45度)
        val baShaIndex = ((degree + 22.5f) / 45f).toInt() % 8
        val baShaName = baSha[baShaIndex]
        baShaText.text = "八煞: $baShaName"
        baShaStatusText.text = "煞"
        baShaText.setTextColor(android.graphics.Color.parseColor("#FF0000"))
        baShaStatusText.setTextColor(android.graphics.Color.parseColor("#FF0000"))

        // 纳音五行 (每纳音6度)
        val naYinIndex = (degree / 6f).toInt() % 60
        val naYinName = naYin60[naYinIndex]
        val naYinWX = naYinWuXing[naYinIndex]
        naYinText.text = "纳音: $naYinName ($naYinWX)"
        naYinText.setTextColor(android.graphics.Color.parseColor(when (naYinWX) {
            "金" -> "#FFD700"
            "木" -> "#90EE90"
            "水" -> "#87CEEB"
            "火" -> "#FF6B6B"
            "土" -> "#DAA520"
            else -> "#DAA520"
        }))

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

        return yearZhu + "年 " + monthZhu + "月 " + dayZhu + "日 " + shiZhu + "时"
    }

    /**
     * 显示磁偏角设置对话框
     */
    private fun showMagDeclDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("磁偏角校正")

        val input = android.widget.EditText(this)
        input.inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
        input.hint = "输入磁偏角度数（如：-5.2）"
        input.setText(magDeclination.toString())
        builder.setView(input)

        builder.setPositiveButton("确定") { _, _ ->
            val value = input.text.toString().toFloatOrNull() ?: 0f
            magDeclination = value
            isMagDeclEnabled = true
            magDeclText.text = "磁偏角: %.1f°".format(magDeclination)
            magDeclText.setTextColor(android.graphics.Color.parseColor("#90EE90"))
        }
        builder.setNegativeButton("取消", null)
        builder.setNeutralButton("关闭校正") { _, _ ->
            isMagDeclEnabled = false
            magDeclText.text = "磁偏角: 未校正"
            magDeclText.setTextColor(android.graphics.Color.parseColor("#DAA520"))
        }
        builder.show()
    }

    /**
     * 保存当前测量记录
     */
    private fun saveCurrentRecord() {
        val calendar = Calendar.getInstance()
        val timeStr = "%04d-%02d-%02d %02d:%02d:%02d".format(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH) + 1,
            calendar.get(Calendar.DAY_OF_MONTH),
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            calendar.get(Calendar.SECOND)
        )

        val record = "$timeStr | 方位: %.1f° | 山向: ${mountainText.text} | ${renMountainText.text}".format(currentDegree)

        val records = sharedPrefs.getString(RECORDS_KEY, "") ?: ""
        val newRecords = record + "\n" + records

        sharedPrefs.edit().putString(RECORDS_KEY, newRecords).apply()
        Toast.makeText(this, "记录已保存", Toast.LENGTH_SHORT).show()
    }

    /**
     * 显示历史记录对话框
     */
    private fun showHistoryDialog() {
        val records = sharedPrefs.getString(RECORDS_KEY, "")
        val displayText = if (records.isNullOrEmpty()) "暂无记录" else records

        val builder = AlertDialog.Builder(this)
        builder.setTitle("测量记录")

        val scrollView = android.widget.ScrollView(this)
        val textView = TextView(this)
        textView.text = displayText
        textView.setTextColor(android.graphics.Color.parseColor("#DAA520"))
        textView.textSize = 12f
        textView.setPadding(20, 20, 20, 20)
        scrollView.addView(textView)
        builder.setView(scrollView)

        builder.setPositiveButton("关闭", null)
        builder.setNegativeButton("清空") { _, _ ->
            sharedPrefs.edit().remove(RECORDS_KEY).apply()
            Toast.makeText(this, "记录已清空", Toast.LENGTH_SHORT).show()
        }
        builder.show()
    }
}
