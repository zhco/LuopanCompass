package com.fengshui.luopan

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.min

/**
 * 风水罗盘自定义绘制视图
 * 包含：天池、八卦、十二长生、二十八宿、透地六十龙、穿山七十二龙、一百二十分金、地盘24山、人盘24山、天盘24山、周天刻度
 */
class LuopanView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // 天盘二十四山 (最外圈，正针)
    private val tianPan24 = arrayOf(
        "壬", "子", "癸", "丑", "艮", "寅",
        "甲", "卯", "乙", "辰", "巽", "巳",
        "丙", "午", "丁", "未", "坤", "申",
        "庚", "酉", "辛", "戌", "乾", "亥"
    )

    // 地盘二十四山 (中圈，正针)
    private val diPan24 = arrayOf(
        "子", "癸", "丑", "艮", "寅", "甲",
        "卯", "乙", "辰", "巽", "巳", "丙",
        "午", "丁", "未", "坤", "申", "庚",
        "酉", "辛", "戌", "乾", "亥", "壬"
    )

    // 人盘二十四山 (内圈，中针)
    private val renPan24 = arrayOf(
        "子", "癸", "丑", "艮", "寅", "甲",
        "卯", "乙", "辰", "巽", "巳", "丙",
        "午", "丁", "未", "坤", "申", "庚",
        "酉", "辛", "戌", "乾", "亥", "壬"
    )

    // 八卦名称
    private val baGua = arrayOf("坎", "艮", "震", "巽", "离", "坤", "兑", "乾")

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
    // 以十二地支为基础，每宫30度
    // 顺序：长生、沐浴、冠带、临官、帝旺、衰、病、死、墓、绝、胎、养
    private val changSheng12 = arrayOf(
        "长生", "沐浴", "冠带", "临官", "帝旺", "衰",
        "病", "死", "墓", "绝", "胎", "养"
    )

    // 十二长生宫对应的地支（以火局丙丁为例，从寅开始长生）
    // 实际应根据坐山五行局来定，这里做简化展示
    // 金局长生从巳起，木局长生从亥起，水局长生从申起，火局长生从寅起
    private val changShengZhi = arrayOf("寅", "卯", "辰", "巳", "午", "未", "申", "酉", "戌", "亥", "子", "丑")

    // 二十八宿
    // 东方青龙七宿：角亢氐房心尾箕
    // 北方玄武七宿：斗牛女虚危室壁
    // 西方白虎七宿：奎娄胃昴毕觜参
    // 南方朱雀七宿：井鬼柳星张翼轸
    // 每宿约12.857度（360/28）
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

    // 九星（贪狼、巨门、禄存、文曲、廉贞、武曲、破军、左辅、右弼）
    // 按洛书九宫顺序排列，每星40度（360/9）
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

    // 紫白飞星（一白到九紫，按洛书九宫排列）
    // 每星40度（360/9）
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

    // 六十四卦（按先天八卦顺序，每卦5.625度）
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

    // 六十四卦卦象（上卦+下卦）
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

    // 一百二十分金吉凶判断
    private fun isFenJinGood(index: Int): Boolean {
        val posInShan = index % 5
        return posInShan == 0 || posInShan == 2 || posInShan == 4
    }

    // 穿山七十二龙吉凶判断
    private fun isChuanShanGood(index: Int): Boolean {
        return chuanShan72[index] != "空亡"
    }

    // 透地六十龙吉凶判断
    private fun isTouDiGood(index: Int): Boolean {
        val name = touDi60[index]
        val gan = name[0]
        return gan in listOf('甲', '丙', '戊', '庚', '壬')
    }

    // 十二长生宫吉凶判断
    // 长生、冠带、临官、帝旺为吉
    // 沐浴、衰、病、死、墓、绝、胎、养为凶或平
    private fun isChangShengGood(index: Int): Boolean {
        return index == 0 || index == 2 || index == 3 || index == 4
    }

    // 神煞名称（每山对应的神煞）
    // 24山 x 主要神煞
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

    // 八煞黄泉（坐山克向水）
    // 八煞：坎龙、坤兔、震山猴、巽鸡、乾马、兑蛇头、艮虎、离猪为煞曜
    private val baSha = arrayOf(
        "坎龙", "坤兔", "震猴", "巽鸡", "乾马", "兑蛇", "艮虎", "离猪"
    )

    // 八煞对应方位（度数）
    private val baShaDegrees = floatArrayOf(
        0f, 45f, 90f, 135f, 180f, 225f, 270f, 315f
    )

    // 八煞吉凶（均为凶）
    private val baShaJiXiong = arrayOf(
        "煞", "煞", "煞", "煞", "煞", "煞", "煞", "煞"
    )

    // 当前角度
    var currentDegree: Float = 0f
        set(value) {
            field = value
            invalidate()
        }

    // 画笔
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val goldPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val redPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val darkPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val centerPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val goodPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val badPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var centerX = 0f
    private var centerY = 0f
    private var radius = 0f

    init {
        paint.style = Paint.Style.STROKE
        paint.color = Color.parseColor("#B8860B")
        paint.strokeWidth = 1.5f

        goldPaint.color = Color.parseColor("#FFD700")
        goldPaint.textAlign = Paint.Align.CENTER

        redPaint.color = Color.parseColor("#CC0000")
        redPaint.textAlign = Paint.Align.CENTER

        darkPaint.color = Color.parseColor("#3E2723")
        darkPaint.style = Paint.Style.FILL

        centerPaint.color = Color.parseColor("#FFD700")
        centerPaint.style = Paint.Style.FILL

        goodPaint.color = Color.parseColor("#00FF00")
        goodPaint.textAlign = Paint.Align.CENTER

        badPaint.color = Color.parseColor("#FF0000")
        badPaint.textAlign = Paint.Align.CENTER
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        centerX = w / 2f
        centerY = h / 2f
        radius = min(w, h) / 2f * 0.92f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.save()
        canvas.translate(centerX, centerY)

        // 绘制背景圆盘
        drawBackground(canvas)

        // 绘制最外圈周天360度刻度
        drawZhouTianScale(canvas)

        // 绘制神煞（最外圈）
        drawShenSha(canvas, radius * 0.98f, radius * 0.95f)

        // 绘制八煞黄泉
        drawBaSha(canvas, radius * 0.94f, radius * 0.90f)

        // 绘制天盘 (最外圈24山)
        draw24ShanRing(canvas, tianPan24, radius * 0.89f, radius * 0.78f, 0f, true)

        // 绘制地盘 (中圈24山)
        draw24ShanRing(canvas, diPan24, radius * 0.76f, radius * 0.65f, 7.5f, true)

        // 绘制人盘 (内圈24山，中针)
        draw24ShanRing(canvas, renPan24, radius * 0.63f, radius * 0.52f, 15f, true)

        // 绘制一百二十分金
        drawFenJin120(canvas, radius * 0.50f, radius * 0.40f)

        // 绘制穿山七十二龙
        drawChuanShan72(canvas, radius * 0.38f, radius * 0.28f)

        // 绘制透地六十龙
        drawTouDi60(canvas, radius * 0.26f, radius * 0.18f)

        // 绘制十二长生宫
        drawChangSheng12(canvas, radius * 0.16f, radius * 0.10f)

        // 绘制二十八宿
        draw28Xiu(canvas, radius * 0.08f, radius * 0.04f)

        // 绘制紫白飞星
        drawZiBai(canvas, radius * 0.035f, radius * 0.01f)

        // 绘制六十四卦
        draw64Gua(canvas, radius * 0.008f, radius * 0.001f)

        // 绘制天池 (中心)
        drawTianChi(canvas)

        // 绘制指针
        drawNeedle(canvas)

        canvas.restore()
    }

    private fun drawBackground(canvas: Canvas) {
        val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        bgPaint.style = Paint.Style.FILL

        val gradient = RadialGradient(0f, 0f, radius, Color.parseColor("#2A1500"), Color.parseColor("#1A0A00"), Shader.TileMode.CLAMP)
        bgPaint.shader = gradient
        canvas.drawCircle(0f, 0f, radius, bgPaint)

        // 木纹效果
        val woodPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        woodPaint.style = Paint.Style.STROKE
        woodPaint.strokeWidth = 0.5f
        for (i in 0..20) {
            val r = radius * i / 20f
            woodPaint.color = Color.argb(30, 139, 90, 43)
            canvas.drawCircle(0f, 0f, r, woodPaint)
        }
    }

    private fun drawZhouTianScale(canvas: Canvas) {
        // 最外圈金色边框
        paint.strokeWidth = 3f
        paint.color = Color.parseColor("#FFD700")
        canvas.drawCircle(0f, 0f, radius, paint)
        canvas.drawCircle(0f, 0f, radius * 0.97f, paint)

        // 周天360度刻度线
        paint.strokeWidth = 1f
        paint.color = Color.parseColor("#B8860B")
        for (i in 0 until 360) {
            val angleRad = Math.toRadians(i.toDouble() - currentDegree.toDouble())
            val innerR = radius * 0.97f
            val outerR = when {
                i % 90 == 0 -> radius * 0.94f
                i % 45 == 0 -> radius * 0.95f
                i % 15 == 0 -> radius * 0.96f
                i % 5 == 0 -> radius * 0.965f
                else -> radius * 0.97f
            }
            val x1 = (innerR * cos(angleRad)).toFloat()
            val y1 = (innerR * sin(angleRad)).toFloat()
            val x2 = (outerR * cos(angleRad)).toFloat()
            val y2 = (outerR * sin(angleRad)).toFloat()
            canvas.drawLine(x1, y1, x2, y2, paint)
        }

        // 周天度数标注 (每30度)
        textPaint.textAlign = Paint.Align.CENTER
        textPaint.textSize = radius * 0.03f
        textPaint.color = Color.parseColor("#B8860B")
        textPaint.typeface = Typeface.DEFAULT
        for (i in 0 until 360 step 30) {
            val angleRad = Math.toRadians(i.toDouble() - currentDegree.toDouble())
            val textR = radius * 0.92f
            val x = (textR * cos(angleRad)).toFloat()
            val y = (textR * sin(angleRad)).toFloat()
            canvas.save()
            canvas.translate(x, y)
            canvas.rotate((i - currentDegree + 90).toFloat())
            canvas.drawText("${i}°", 0f, 0f, textPaint)
            canvas.restore()
        }
    }

    private fun draw24ShanRing(canvas: Canvas, shanNames: Array<String>, outerR: Float, innerR: Float, offsetDegree: Float, showBorder: Boolean) {
        val step = 360f / 24f

        for (i in 0 until 24) {
            val startAngle = i * step + offsetDegree - currentDegree - step / 2
            val endAngle = startAngle + step

            // 绘制扇区分隔线
            paint.strokeWidth = 1f
            paint.color = Color.parseColor("#B8860B")
            val rad1 = Math.toRadians(startAngle.toDouble())
            val rad2 = Math.toRadians(endAngle.toDouble())
            canvas.drawLine(
                (innerR * cos(rad1)).toFloat(), (innerR * sin(rad1)).toFloat(),
                (outerR * cos(rad1)).toFloat(), (outerR * sin(rad1)).toFloat(), paint
            )

            // 扇区背景 - 四正方位用红色
            if (i % 6 == 0) {
                val sectorPaint = Paint(Paint.ANTI_ALIAS_FLAG)
                sectorPaint.style = Paint.Style.FILL
                sectorPaint.color = Color.argb(40, 204, 0, 0)
                val path = Path()
                path.arcTo(RectF(-outerR, -outerR, outerR, outerR), startAngle, step, true)
                path.arcTo(RectF(-innerR, -innerR, innerR, innerR), endAngle, -step, false)
                path.close()
                canvas.drawPath(path, sectorPaint)
            }

            // 绘制文字
            val midAngle = Math.toRadians((startAngle + step / 2).toDouble())
            val textR = (outerR + innerR) / 2f
            val x = (textR * cos(midAngle)).toFloat()
            val y = (textR * sin(midAngle)).toFloat()

            textPaint.textAlign = Paint.Align.CENTER
            textPaint.textSize = (outerR - innerR) * 0.35f

            if (i % 6 == 0) {
                textPaint.color = Color.parseColor("#FF3333")
                textPaint.typeface = Typeface.DEFAULT_BOLD
            } else if (i % 3 == 0) {
                textPaint.color = Color.parseColor("#FFD700")
                textPaint.typeface = Typeface.DEFAULT_BOLD
            } else {
                textPaint.color = Color.parseColor("#DAA520")
                textPaint.typeface = Typeface.DEFAULT
            }

            canvas.save()
            canvas.translate(x, y)
            canvas.rotate((startAngle + step / 2 + 90).toFloat())
            canvas.drawText(shanNames[i], 0f, 0f, textPaint)
            canvas.restore()
        }

        if (showBorder) {
            paint.strokeWidth = 2f
            paint.color = Color.parseColor("#FFD700")
            canvas.drawCircle(0f, 0f, outerR, paint)
            canvas.drawCircle(0f, 0f, innerR, paint)
        }
    }

    /**
     * 绘制一百二十分金
     */
    private fun drawFenJin120(canvas: Canvas, outerR: Float, innerR: Float) {
        val step = 360f / 120f

        for (i in 0 until 120) {
            val startAngle = i * step - currentDegree - step / 2

            paint.strokeWidth = 0.5f
            paint.color = Color.parseColor("#5D4037")
            val rad = Math.toRadians(startAngle.toDouble())
            canvas.drawLine(
                (innerR * cos(rad)).toFloat(), (innerR * sin(rad)).toFloat(),
                (outerR * cos(rad)).toFloat(), (outerR * sin(rad)).toFloat(), paint
            )

            if (i % 5 == 0) {
                paint.strokeWidth = 1f
                paint.color = Color.parseColor("#B8860B")
                canvas.drawLine(
                    (innerR * cos(rad)).toFloat(), (innerR * sin(rad)).toFloat(),
                    (outerR * cos(rad)).toFloat(), (outerR * sin(rad)).toFloat(), paint
                )
            }

            val midAngle = Math.toRadians((startAngle + step / 2).toDouble())
            val textR = (outerR + innerR) / 2f
            val x = (textR * cos(midAngle)).toFloat()
            val y = (textR * sin(midAngle)).toFloat()

            textPaint.textAlign = Paint.Align.CENTER
            textPaint.textSize = (outerR - innerR) * 0.25f

            if (isFenJinGood(i)) {
                textPaint.color = Color.parseColor("#90EE90")
            } else {
                textPaint.color = Color.parseColor("#FF6B6B")
            }
            textPaint.typeface = Typeface.DEFAULT

            val displayText = fenJin120[i].substring(0, 1)

            canvas.save()
            canvas.translate(x, y)
            canvas.rotate((startAngle + step / 2 + 90).toFloat())
            canvas.drawText(displayText, 0f, 0f, textPaint)
            canvas.restore()
        }

        paint.strokeWidth = 2f
        paint.color = Color.parseColor("#FFD700")
        canvas.drawCircle(0f, 0f, outerR, paint)
        canvas.drawCircle(0f, 0f, innerR, paint)
    }

    /**
     * 绘制穿山七十二龙
     */
    private fun drawChuanShan72(canvas: Canvas, outerR: Float, innerR: Float) {
        val step = 360f / 72f

        for (i in 0 until 72) {
            val startAngle = i * step - currentDegree - step / 2

            paint.strokeWidth = 0.5f
            paint.color = Color.parseColor("#5D4037")
            val rad = Math.toRadians(startAngle.toDouble())
            canvas.drawLine(
                (innerR * cos(rad)).toFloat(), (innerR * sin(rad)).toFloat(),
                (outerR * cos(rad)).toFloat(), (outerR * sin(rad)).toFloat(), paint
            )

            if (i % 3 == 0) {
                paint.strokeWidth = 1f
                paint.color = Color.parseColor("#B8860B")
                canvas.drawLine(
                    (innerR * cos(rad)).toFloat(), (innerR * sin(rad)).toFloat(),
                    (outerR * cos(rad)).toFloat(), (outerR * sin(rad)).toFloat(), paint
                )
            }

            val midAngle = Math.toRadians((startAngle + step / 2).toDouble())
            val textR = (outerR + innerR) / 2f
            val x = (textR * cos(midAngle)).toFloat()
            val y = (textR * sin(midAngle)).toFloat()

            textPaint.textAlign = Paint.Align.CENTER
            textPaint.textSize = (outerR - innerR) * 0.3f

            val name = chuanShan72[i]
            if (name == "空亡") {
                textPaint.color = Color.parseColor("#FF0000")
                textPaint.typeface = Typeface.DEFAULT_BOLD
            } else if (isChuanShanGood(i)) {
                textPaint.color = Color.parseColor("#90EE90")
                textPaint.typeface = Typeface.DEFAULT
            } else {
                textPaint.color = Color.parseColor("#FF6B6B")
                textPaint.typeface = Typeface.DEFAULT
            }

            canvas.save()
            canvas.translate(x, y)
            canvas.rotate((startAngle + step / 2 + 90).toFloat())
            val displayText = if (name == "空亡") "空" else name.substring(0, 1)
            canvas.drawText(displayText, 0f, 0f, textPaint)
            canvas.restore()
        }

        paint.strokeWidth = 2f
        paint.color = Color.parseColor("#FFD700")
        canvas.drawCircle(0f, 0f, outerR, paint)
        canvas.drawCircle(0f, 0f, innerR, paint)
    }

    /**
     * 绘制透地六十龙
     */
    private fun drawTouDi60(canvas: Canvas, outerR: Float, innerR: Float) {
        val step = 360f / 60f

        for (i in 0 until 60) {
            val startAngle = i * step - currentDegree - step / 2

            paint.strokeWidth = 0.5f
            paint.color = Color.parseColor("#5D4037")
            val rad = Math.toRadians(startAngle.toDouble())
            canvas.drawLine(
                (innerR * cos(rad)).toFloat(), (innerR * sin(rad)).toFloat(),
                (outerR * cos(rad)).toFloat(), (outerR * sin(rad)).toFloat(), paint
            )

            if (i % 3 == 0 || i % 2 == 0) {
                paint.strokeWidth = 1f
                paint.color = Color.parseColor("#B8860B")
                canvas.drawLine(
                    (innerR * cos(rad)).toFloat(), (innerR * sin(rad)).toFloat(),
                    (outerR * cos(rad)).toFloat(), (outerR * sin(rad)).toFloat(), paint
                )
            }

            val midAngle = Math.toRadians((startAngle + step / 2).toDouble())
            val textR = (outerR + innerR) / 2f
            val x = (textR * cos(midAngle)).toFloat()
            val y = (textR * sin(midAngle)).toFloat()

            textPaint.textAlign = Paint.Align.CENTER
            textPaint.textSize = (outerR - innerR) * 0.3f

            if (isTouDiGood(i)) {
                textPaint.color = Color.parseColor("#90EE90")
                textPaint.typeface = Typeface.DEFAULT
            } else {
                textPaint.color = Color.parseColor("#FF6B6B")
                textPaint.typeface = Typeface.DEFAULT
            }

            canvas.save()
            canvas.translate(x, y)
            canvas.rotate((startAngle + step / 2 + 90).toFloat())
            val displayText = touDi60[i].substring(0, 1)
            canvas.drawText(displayText, 0f, 0f, textPaint)
            canvas.restore()
        }

        paint.strokeWidth = 2f
        paint.color = Color.parseColor("#FFD700")
        canvas.drawCircle(0f, 0f, outerR, paint)
        canvas.drawCircle(0f, 0f, innerR, paint)
    }

    /**
     * 绘制十二长生宫
     * 每宫30度，共12宫
     */
    private fun drawChangSheng12(canvas: Canvas, outerR: Float, innerR: Float) {
        val step = 360f / 12f  // 每宫30度

        for (i in 0 until 12) {
            val startAngle = i * step - currentDegree - step / 2

            paint.strokeWidth = 1f
            paint.color = Color.parseColor("#B8860B")
            val rad = Math.toRadians(startAngle.toDouble())
            canvas.drawLine(
                (innerR * cos(rad)).toFloat(), (innerR * sin(rad)).toFloat(),
                (outerR * cos(rad)).toFloat(), (outerR * sin(rad)).toFloat(), paint
            )

            val midAngle = Math.toRadians((startAngle + step / 2).toDouble())
            val textR = (outerR + innerR) / 2f
            val x = (textR * cos(midAngle)).toFloat()
            val y = (textR * sin(midAngle)).toFloat()

            textPaint.textAlign = Paint.Align.CENTER
            textPaint.textSize = (outerR - innerR) * 0.25f

            // 根据吉凶显示颜色
            if (isChangShengGood(i)) {
                textPaint.color = Color.parseColor("#90EE90")  // 吉 - 浅绿
                textPaint.typeface = Typeface.DEFAULT_BOLD
            } else {
                textPaint.color = Color.parseColor("#DAA520")  // 平/凶 - 金色
                textPaint.typeface = Typeface.DEFAULT
            }

            canvas.save()
            canvas.translate(x, y)
            canvas.rotate((startAngle + step / 2 + 90).toFloat())
            // 显示长生宫名称（简化显示，只显示前两个字）
            val displayText = changSheng12[i].substring(0, minOf(2, changSheng12[i].length))
            canvas.drawText(displayText, 0f, 0f, textPaint)
            canvas.restore()
        }

        paint.strokeWidth = 2f
        paint.color = Color.parseColor("#FFD700")
        canvas.drawCircle(0f, 0f, outerR, paint)
        canvas.drawCircle(0f, 0f, innerR, paint)
    }

    /**
     * 绘制二十八宿
     * 每宿度数不同，总计360度
     */
    private fun draw28Xiu(canvas: Canvas, outerR: Float, innerR: Float) {
        var currentAngle = 0f

        for (i in 0 until 28) {
            val step = xiu28Degrees[i]
            val startAngle = currentAngle - currentDegree - step / 2

            paint.strokeWidth = 0.5f
            paint.color = Color.parseColor("#5D4037")
            val rad = Math.toRadians(startAngle.toDouble())
            canvas.drawLine(
                (innerR * cos(rad)).toFloat(), (innerR * sin(rad)).toFloat(),
                (outerR * cos(rad)).toFloat(), (outerR * sin(rad)).toFloat(), paint
            )

            val midAngle = Math.toRadians((startAngle + step / 2).toDouble())
            val textR = (outerR + innerR) / 2f
            val x = (textR * cos(midAngle)).toFloat()
            val y = (textR * sin(midAngle)).toFloat()

            textPaint.textAlign = Paint.Align.CENTER
            textPaint.textSize = (outerR - innerR) * 0.35f

            // 四象颜色区分
            textPaint.color = when {
                i < 7 -> Color.parseColor("#90EE90")    // 东方青龙 - 绿
                i < 14 -> Color.parseColor("#87CEEB")   // 北方玄武 - 蓝
                i < 21 -> Color.parseColor("#FFD700")   // 西方白虎 - 金
                else -> Color.parseColor("#FF6B6B")     // 南方朱雀 - 红
            }
            textPaint.typeface = Typeface.DEFAULT_BOLD

            canvas.save()
            canvas.translate(x, y)
            canvas.rotate((startAngle + step / 2 + 90).toFloat())
            canvas.drawText(xiu28[i], 0f, 0f, textPaint)
            canvas.restore()

            currentAngle += step
        }

        paint.strokeWidth = 2f
        paint.color = Color.parseColor("#FFD700")
        canvas.drawCircle(0f, 0f, outerR, paint)
        canvas.drawCircle(0f, 0f, innerR, paint)
    }

    /**
     * 绘制九星
     * 每星40度，共9星
     */
    private fun drawJiuXing(canvas: Canvas, outerR: Float, innerR: Float) {
        val step = 360f / 9f  // 每星40度

        for (i in 0 until 9) {
            val startAngle = i * step - currentDegree - step / 2

            paint.strokeWidth = 1f
            paint.color = Color.parseColor("#B8860B")
            val rad = Math.toRadians(startAngle.toDouble())
            canvas.drawLine(
                (innerR * cos(rad)).toFloat(), (innerR * sin(rad)).toFloat(),
                (outerR * cos(rad)).toFloat(), (outerR * sin(rad)).toFloat(), paint
            )

            val midAngle = Math.toRadians((startAngle + step / 2).toDouble())
            val textR = (outerR + innerR) / 2f
            val x = (textR * cos(midAngle)).toFloat()
            val y = (textR * sin(midAngle)).toFloat()

            textPaint.textAlign = Paint.Align.CENTER
            textPaint.textSize = (outerR - innerR) * 0.4f

            // 根据吉凶显示颜色
            if (jiuXingJiXiong[i] == "吉") {
                textPaint.color = Color.parseColor("#FFD700")  // 吉 - 金色
                textPaint.typeface = Typeface.DEFAULT_BOLD
            } else {
                textPaint.color = Color.parseColor("#8B4513")  // 凶 - 深棕
                textPaint.typeface = Typeface.DEFAULT
            }

            canvas.save()
            canvas.translate(x, y)
            canvas.rotate((startAngle + step / 2 + 90).toFloat())
            // 显示九星名称（简化显示）
            val displayText = jiuXing[i].substring(0, minOf(2, jiuXing[i].length))
            canvas.drawText(displayText, 0f, 0f, textPaint)
            canvas.restore()
        }

        paint.strokeWidth = 2f
        paint.color = Color.parseColor("#FFD700")
        canvas.drawCircle(0f, 0f, outerR, paint)
        canvas.drawCircle(0f, 0f, innerR, paint)
    }

    /**
     * 绘制紫白飞星
     * 每星40度，共9星
     */
    private fun drawZiBai(canvas: Canvas, outerR: Float, innerR: Float) {
        val step = 360f / 9f  // 每星40度

        for (i in 0 until 9) {
            val startAngle = i * step - currentDegree - step / 2

            paint.strokeWidth = 1f
            paint.color = Color.parseColor("#B8860B")
            val rad = Math.toRadians(startAngle.toDouble())
            canvas.drawLine(
                (innerR * cos(rad)).toFloat(), (innerR * sin(rad)).toFloat(),
                (outerR * cos(rad)).toFloat(), (outerR * sin(rad)).toFloat(), paint
            )

            val midAngle = Math.toRadians((startAngle + step / 2).toDouble())
            val textR = (outerR + innerR) / 2f
            val x = (textR * cos(midAngle)).toFloat()
            val y = (textR * sin(midAngle)).toFloat()

            textPaint.textAlign = Paint.Align.CENTER
            textPaint.textSize = (outerR - innerR) * 0.4f

            // 根据吉凶显示颜色
            textPaint.color = when (ziBaiJiXiong[i]) {
                "吉" -> Color.parseColor("#90EE90")    // 吉 - 浅绿
                "大凶" -> Color.parseColor("#FF0000")  // 大凶 - 红
                else -> Color.parseColor("#DAA520")    // 凶 - 金色
            }
            textPaint.typeface = Typeface.DEFAULT_BOLD

            canvas.save()
            canvas.translate(x, y)
            canvas.rotate((startAngle + step / 2 + 90).toFloat())
            canvas.drawText(ziBai[i], 0f, 0f, textPaint)
            canvas.restore()
        }

        paint.strokeWidth = 2f
        paint.color = Color.parseColor("#FFD700")
        canvas.drawCircle(0f, 0f, outerR, paint)
        canvas.drawCircle(0f, 0f, innerR, paint)
    }

    /**
     * 绘制六十四卦
     * 每卦5.625度，共64卦
     */
    private fun draw64Gua(canvas: Canvas, outerR: Float, innerR: Float) {
        val step = 360f / 64f  // 每卦5.625度

        for (i in 0 until 64) {
            val startAngle = i * step - currentDegree - step / 2

            paint.strokeWidth = 0.5f
            paint.color = Color.parseColor("#5D4037")
            val rad = Math.toRadians(startAngle.toDouble())
            canvas.drawLine(
                (innerR * cos(rad)).toFloat(), (innerR * sin(rad)).toFloat(),
                (outerR * cos(rad)).toFloat(), (outerR * sin(rad)).toFloat(), paint
            )

            // 每8卦画一条粗线
            if (i % 8 == 0) {
                paint.strokeWidth = 1f
                paint.color = Color.parseColor("#B8860B")
                canvas.drawLine(
                    (innerR * cos(rad)).toFloat(), (innerR * sin(rad)).toFloat(),
                    (outerR * cos(rad)).toFloat(), (outerR * sin(rad)).toFloat(), paint
                )
            }

            val midAngle = Math.toRadians((startAngle + step / 2).toDouble())
            val textR = (outerR + innerR) / 2f
            val x = (textR * cos(midAngle)).toFloat()
            val y = (textR * sin(midAngle)).toFloat()

            textPaint.textAlign = Paint.Align.CENTER
            textPaint.textSize = (outerR - innerR) * 0.35f

            // 八卦颜色区分
            textPaint.color = when {
                i < 8 -> Color.parseColor("#90EE90")    // 乾宫 - 绿
                i < 16 -> Color.parseColor("#87CEEB")   // 兑宫 - 蓝
                i < 24 -> Color.parseColor("#FFD700")   // 离宫 - 金
                i < 32 -> Color.parseColor("#FF6B6B")   // 震宫 - 红
                i < 40 -> Color.parseColor("#DDA0DD")   // 巽宫 - 紫
                i < 48 -> Color.parseColor("#F0E68C")   // 坎宫 - 黄
                i < 56 -> Color.parseColor("#FFA500")   // 艮宫 - 橙
                else -> Color.parseColor("#98FB98")     // 坤宫 - 浅绿
            }
            textPaint.typeface = Typeface.DEFAULT_BOLD

            canvas.save()
            canvas.translate(x, y)
            canvas.rotate((startAngle + step / 2 + 90).toFloat())
            canvas.drawText(gua64[i], 0f, 0f, textPaint)
            canvas.restore()
        }

        paint.strokeWidth = 2f
        paint.color = Color.parseColor("#FFD700")
        canvas.drawCircle(0f, 0f, outerR, paint)
        canvas.drawCircle(0f, 0f, innerR, paint)
    }

    /**
     * 绘制神煞
     * 每山15度，共24山
     */
    private fun drawShenSha(canvas: Canvas, outerR: Float, innerR: Float) {
        val step = 360f / 24f

        for (i in 0 until 24) {
            val startAngle = i * step - currentDegree - step / 2

            paint.strokeWidth = 0.5f
            paint.color = Color.parseColor("#5D4037")
            val rad = Math.toRadians(startAngle.toDouble())
            canvas.drawLine(
                (innerR * cos(rad)).toFloat(), (innerR * sin(rad)).toFloat(),
                (outerR * cos(rad)).toFloat(), (outerR * sin(rad)).toFloat(), paint
            )

            val midAngle = Math.toRadians((startAngle + step / 2).toDouble())
            val textR = (outerR + innerR) / 2f
            val x = (textR * cos(midAngle)).toFloat()
            val y = (textR * sin(midAngle)).toFloat()

            textPaint.textAlign = Paint.Align.CENTER
            textPaint.textSize = (outerR - innerR) * 0.25f

            // 根据吉凶显示颜色
            textPaint.color = when (shenShaJiXiong[i]) {
                "吉" -> Color.parseColor("#90EE90")
                "大凶" -> Color.parseColor("#FF0000")
                else -> Color.parseColor("#DAA520")
            }
            textPaint.typeface = Typeface.DEFAULT_BOLD

            canvas.save()
            canvas.translate(x, y)
            canvas.rotate((startAngle + step / 2 + 90).toFloat())
            canvas.drawText(shenShaNames[i], 0f, 0f, textPaint)
            canvas.restore()
        }

        paint.strokeWidth = 2f
        paint.color = Color.parseColor("#FFD700")
        canvas.drawCircle(0f, 0f, outerR, paint)
        canvas.drawCircle(0f, 0f, innerR, paint)
    }

    /**
     * 绘制八煞黄泉
     * 八煞方位，每煞45度
     */
    private fun drawBaSha(canvas: Canvas, outerR: Float, innerR: Float) {
        val step = 360f / 8f  // 每煞45度

        for (i in 0 until 8) {
            val startAngle = i * step - currentDegree - step / 2

            paint.strokeWidth = 1f
            paint.color = Color.parseColor("#CC0000")
            val rad = Math.toRadians(startAngle.toDouble())
            canvas.drawLine(
                (innerR * cos(rad)).toFloat(), (innerR * sin(rad)).toFloat(),
                (outerR * cos(rad)).toFloat(), (outerR * sin(rad)).toFloat(), paint
            )

            val midAngle = Math.toRadians((startAngle + step / 2).toDouble())
            val textR = (outerR + innerR) / 2f
            val x = (textR * cos(midAngle)).toFloat()
            val y = (textR * sin(midAngle)).toFloat()

            textPaint.textAlign = Paint.Align.CENTER
            textPaint.textSize = (outerR - innerR) * 0.3f
            textPaint.color = Color.parseColor("#FF0000")
            textPaint.typeface = Typeface.DEFAULT_BOLD

            canvas.save()
            canvas.translate(x, y)
            canvas.rotate((startAngle + step / 2 + 90).toFloat())
            canvas.drawText(baSha[i], 0f, 0f, textPaint)
            canvas.restore()
        }

        paint.strokeWidth = 2f
        paint.color = Color.parseColor("#FF0000")
        canvas.drawCircle(0f, 0f, outerR, paint)
        canvas.drawCircle(0f, 0f, innerR, paint)
    }

    private fun drawTianChi(canvas: Canvas) {
        val poolRadius = radius * 0.035f

        val poolPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        poolPaint.style = Paint.Style.FILL
        val gradient = RadialGradient(0f, 0f, poolRadius, Color.parseColor("#0A0500"), Color.parseColor("#1A0A00"), Shader.TileMode.CLAMP)
        poolPaint.shader = gradient
        canvas.drawCircle(0f, 0f, poolRadius, poolPaint)

        paint.strokeWidth = 2f
        paint.color = Color.parseColor("#FFD700")
        canvas.drawCircle(0f, 0f, poolRadius, paint)

        paint.strokeWidth = 1f
        canvas.drawCircle(0f, 0f, poolRadius * 0.85f, paint)

        paint.strokeWidth = 1f
        paint.color = Color.parseColor("#B8860B")
        canvas.drawLine(-poolRadius * 0.8f, 0f, poolRadius * 0.8f, 0f, paint)
        canvas.drawLine(0f, -poolRadius * 0.8f, 0f, poolRadius * 0.8f, paint)

        centerPaint.color = Color.parseColor("#FFD700")
        canvas.drawCircle(0f, 0f, 4f, centerPaint)
        centerPaint.color = Color.parseColor("#CC0000")
        canvas.drawCircle(0f, 0f, 2f, centerPaint)
    }

    private fun drawNeedle(canvas: Canvas) {
        val needleLength = radius * 0.22f

        val northPath = Path()
        northPath.moveTo(0f, -needleLength)
        northPath.lineTo(-6f, 0f)
        northPath.lineTo(0f, -8f)
        northPath.lineTo(6f, 0f)
        northPath.close()

        val northPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        northPaint.style = Paint.Style.FILL
        northPaint.color = Color.parseColor("#FF0000")
        canvas.drawPath(northPath, northPaint)

        val southPath = Path()
        southPath.moveTo(0f, needleLength)
        southPath.lineTo(-6f, 0f)
        southPath.lineTo(0f, 8f)
        southPath.lineTo(6f, 0f)
        southPath.close()

        val southPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        southPaint.style = Paint.Style.FILL
        southPaint.color = Color.parseColor("#FFFFFF")
        canvas.drawPath(southPath, southPaint)

        centerPaint.color = Color.parseColor("#FFD700")
        canvas.drawCircle(0f, 0f, 4f, centerPaint)
    }
}
