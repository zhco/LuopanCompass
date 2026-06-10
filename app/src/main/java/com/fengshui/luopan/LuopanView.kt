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
 * 包含：天池、八卦、透地六十龙、穿山七十二龙、一百二十分金、地盘24山、人盘24山、天盘24山、周天刻度
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

    // 地盘二十四山 (中圈，正针，与天盘偏移7.5度)
    private val diPan24 = arrayOf(
        "子", "癸", "丑", "艮", "寅", "甲",
        "卯", "乙", "辰", "巽", "巳", "丙",
        "午", "丁", "未", "坤", "申", "庚",
        "酉", "辛", "戌", "乾", "亥", "壬"
    )

    // 人盘二十四山 (内圈，中针，与地盘偏移7.5度，用于消砂纳水)
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
    // 六十甲子配七十二龙，每龙5度（360/72=5）
    // 分布在二十四山之下，每山3龙
    // 其中有12个空亡龙（大空亡）
    private val chuanShan72 = arrayOf(
        // 壬山 (3龙)
        "甲子", "丙子", "戊子",
        // 子山 (3龙)
        "庚子", "壬子", "空亡",
        // 癸山 (3龙)
        "乙丑", "丁丑", "己丑",
        // 丑山 (3龙)
        "辛丑", "癸丑", "空亡",
        // 艮山 (3龙)
        "丙寅", "戊寅", "庚寅",
        // 寅山 (3龙)
        "壬寅", "甲寅", "空亡",
        // 甲山 (3龙)
        "丁卯", "己卯", "辛卯",
        // 卯山 (3龙)
        "癸卯", "乙卯", "空亡",
        // 乙山 (3龙)
        "戊辰", "庚辰", "壬辰",
        // 辰山 (3龙)
        "甲辰", "丙辰", "空亡",
        // 巽山 (3龙)
        "己巳", "辛巳", "癸巳",
        // 巳山 (3龙)
        "乙巳", "丁巳", "空亡",
        // 丙山 (3龙)
        "庚午", "壬午", "甲午",
        // 午山 (3龙)
        "丙午", "戊午", "空亡",
        // 丁山 (3龙)
        "辛未", "癸未", "乙未",
        // 未山 (3龙)
        "丁未", "己未", "空亡",
        // 坤山 (3龙)
        "壬申", "甲申", "丙申",
        // 申山 (3龙)
        "戊申", "庚申", "空亡",
        // 庚山 (3龙)
        "癸酉", "乙酉", "丁酉",
        // 酉山 (3龙)
        "己酉", "辛酉", "空亡",
        // 辛山 (3龙)
        "甲戌", "丙戌", "戊戌",
        // 戌山 (3龙)
        "庚戌", "壬戌", "空亡",
        // 乾山 (3龙)
        "乙亥", "丁亥", "己亥",
        // 亥山 (3龙)
        "辛亥", "癸亥", "空亡"
    )

    // 透地六十龙
    // 六十甲子配六十龙，每龙6度（360/60=6）
    // 分布在二十四山之下，每山2-3龙
    private val touDi60 = arrayOf(
        // 壬山 (2龙)
        "甲子", "丙子",
        // 子山 (3龙)
        "戊子", "庚子", "壬子",
        // 癸山 (2龙)
        "乙丑", "丁丑",
        // 丑山 (3龙)
        "己丑", "辛丑", "癸丑",
        // 艮山 (2龙)
        "丙寅", "戊寅",
        // 寅山 (3龙)
        "庚寅", "壬寅", "甲寅",
        // 甲山 (2龙)
        "丁卯", "己卯",
        // 卯山 (3龙)
        "辛卯", "癸卯", "乙卯",
        // 乙山 (2龙)
        "戊辰", "庚辰",
        // 辰山 (3龙)
        "壬辰", "甲辰", "丙辰",
        // 巽山 (2龙)
        "己巳", "辛巳",
        // 巳山 (3龙)
        "癸巳", "乙巳", "丁巳",
        // 丙山 (2龙)
        "庚午", "壬午",
        // 午山 (3龙)
        "甲午", "丙午", "戊午",
        // 丁山 (2龙)
        "辛未", "癸未",
        // 未山 (3龙)
        "乙未", "丁未", "己未",
        // 坤山 (2龙)
        "壬申", "甲申",
        // 申山 (3龙)
        "丙申", "戊申", "庚申",
        // 庚山 (2龙)
        "癸酉", "乙酉",
        // 酉山 (3龙)
        "丁酉", "己酉", "辛酉",
        // 辛山 (2龙)
        "甲戌", "丙戌",
        // 戌山 (3龙)
        "戊戌", "庚戌", "壬戌",
        // 乾山 (2龙)
        "乙亥", "丁亥",
        // 亥山 (3龙)
        "己亥", "辛亥", "癸亥"
    )

    // 一百二十分金吉凶判断
    private fun isFenJinGood(index: Int): Boolean {
        val posInShan = index % 5
        return posInShan == 0 || posInShan == 2 || posInShan == 4
    }

    // 穿山七十二龙吉凶判断
    // 空亡为凶，其他根据纳音五行判断
    private fun isChuanShanGood(index: Int): Boolean {
        return chuanShan72[index] != "空亡"
    }

    // 透地六十龙吉凶判断
    // 根据纳音五行与坐山五行生克判断
    // 简化：甲子、丙子、戊子、庚子、壬子 等阳干为吉
    private fun isTouDiGood(index: Int): Boolean {
        val name = touDi60[index]
        val gan = name[0]
        // 阳干为吉：甲丙戊庚壬
        return gan in listOf('甲', '丙', '戊', '庚', '壬')
    }

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

        // 绘制天盘 (最外圈24山)
        draw24ShanRing(canvas, tianPan24, radius * 0.95f, radius * 0.82f, 0f, true)

        // 绘制地盘 (中圈24山)
        draw24ShanRing(canvas, diPan24, radius * 0.80f, radius * 0.67f, 7.5f, true)

        // 绘制人盘 (内圈24山，中针)
        draw24ShanRing(canvas, renPan24, radius * 0.65f, radius * 0.52f, 15f, true)

        // 绘制一百二十分金
        drawFenJin120(canvas, radius * 0.50f, radius * 0.40f)

        // 绘制穿山七十二龙
        drawChuanShan72(canvas, radius * 0.38f, radius * 0.28f)

        // 绘制透地六十龙
        drawTouDi60(canvas, radius * 0.26f, radius * 0.18f)

        // 绘制八卦圈
        drawBaGuaRing(canvas, radius * 0.16f, radius * 0.10f)

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
     * 每龙5度，共72龙
     * 空亡龙用红色标注
     */
    private fun drawChuanShan72(canvas: Canvas, outerR: Float, innerR: Float) {
        val step = 360f / 72f  // 每龙5度

        for (i in 0 until 72) {
            val startAngle = i * step - currentDegree - step / 2

            paint.strokeWidth = 0.5f
            paint.color = Color.parseColor("#5D4037")
            val rad = Math.toRadians(startAngle.toDouble())
            canvas.drawLine(
                (innerR * cos(rad)).toFloat(), (innerR * sin(rad)).toFloat(),
                (outerR * cos(rad)).toFloat(), (outerR * sin(rad)).toFloat(), paint
            )

            // 每山分隔（每3龙一山）
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
                textPaint.color = Color.parseColor("#FF0000")  // 空亡 - 红色
                textPaint.typeface = Typeface.DEFAULT_BOLD
            } else if (isChuanShanGood(i)) {
                textPaint.color = Color.parseColor("#90EE90")  // 吉 - 绿色
                textPaint.typeface = Typeface.DEFAULT
            } else {
                textPaint.color = Color.parseColor("#FF6B6B")  // 凶 - 浅红
                textPaint.typeface = Typeface.DEFAULT
            }

            canvas.save()
            canvas.translate(x, y)
            canvas.rotate((startAngle + step / 2 + 90).toFloat())
            // 只显示天干部分，避免拥挤
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
     * 每龙6度，共60龙
     */
    private fun drawTouDi60(canvas: Canvas, outerR: Float, innerR: Float) {
        val step = 360f / 60f  // 每龙6度

        for (i in 0 until 60) {
            val startAngle = i * step - currentDegree - step / 2

            paint.strokeWidth = 0.5f
            paint.color = Color.parseColor("#5D4037")
            val rad = Math.toRadians(startAngle.toDouble())
            canvas.drawLine(
                (innerR * cos(rad)).toFloat(), (innerR * sin(rad)).toFloat(),
                (outerR * cos(rad)).toFloat(), (outerR * sin(rad)).toFloat(), paint
            )

            // 每山分隔
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
            // 只显示天干部分
            val displayText = touDi60[i].substring(0, 1)
            canvas.drawText(displayText, 0f, 0f, textPaint)
            canvas.restore()
        }

        paint.strokeWidth = 2f
        paint.color = Color.parseColor("#FFD700")
        canvas.drawCircle(0f, 0f, outerR, paint)
        canvas.drawCircle(0f, 0f, innerR, paint)
    }

    private fun drawBaGuaRing(canvas: Canvas, outerR: Float, innerR: Float) {
        val step = 360f / 8f

        for (i in 0 until 8) {
            val startAngle = i * step - currentDegree - step / 2
            val midAngle = Math.toRadians((startAngle + step / 2).toDouble())
            val textR = (outerR + innerR) / 2f
            val x = (textR * cos(midAngle)).toFloat()
            val y = (textR * sin(midAngle)).toFloat()

            textPaint.textAlign = Paint.Align.CENTER
            textPaint.textSize = (outerR - innerR) * 0.4f
            textPaint.color = Color.parseColor("#FFD700")
            textPaint.typeface = Typeface.DEFAULT_BOLD

            canvas.save()
            canvas.translate(x, y)
            canvas.rotate((startAngle + step / 2 + 90).toFloat())
            canvas.drawText(baGua[i], 0f, 0f, textPaint)
            canvas.restore()

            paint.strokeWidth = 1f
            paint.color = Color.parseColor("#B8860B")
            val rad = Math.toRadians(startAngle.toDouble())
            canvas.drawLine(
                (innerR * cos(rad)).toFloat(), (innerR * sin(rad)).toFloat(),
                (outerR * cos(rad)).toFloat(), (outerR * sin(rad)).toFloat(), paint
            )
        }

        paint.strokeWidth = 2f
        paint.color = Color.parseColor("#FFD700")
        canvas.drawCircle(0f, 0f, outerR, paint)
        canvas.drawCircle(0f, 0f, innerR, paint)
    }

    private fun drawTianChi(canvas: Canvas) {
        val poolRadius = radius * 0.09f

        val poolPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        poolPaint.style = Paint.Style.FILL
        val gradient = RadialGradient(0f, 0f, poolRadius, Color.parseColor("#0A0500"), Color.parseColor("#1A0A00"), Shader.TileMode.CLAMP)
        poolPaint.shader = gradient
        canvas.drawCircle(0f, 0f, poolRadius, poolPaint)

        paint.strokeWidth = 3f
        paint.color = Color.parseColor("#FFD700")
        canvas.drawCircle(0f, 0f, poolRadius, paint)

        paint.strokeWidth = 1f
        canvas.drawCircle(0f, 0f, poolRadius * 0.85f, paint)

        paint.strokeWidth = 1f
        paint.color = Color.parseColor("#B8860B")
        canvas.drawLine(-poolRadius * 0.8f, 0f, poolRadius * 0.8f, 0f, paint)
        canvas.drawLine(0f, -poolRadius * 0.8f, 0f, poolRadius * 0.8f, paint)

        centerPaint.color = Color.parseColor("#FFD700")
        canvas.drawCircle(0f, 0f, 6f, centerPaint)
        centerPaint.color = Color.parseColor("#CC0000")
        canvas.drawCircle(0f, 0f, 3f, centerPaint)
    }

    private fun drawNeedle(canvas: Canvas) {
        val needleLength = radius * 0.22f

        val northPath = Path()
        northPath.moveTo(0f, -needleLength)
        northPath.lineTo(-8f, 0f)
        northPath.lineTo(0f, -10f)
        northPath.lineTo(8f, 0f)
        northPath.close()

        val northPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        northPaint.style = Paint.Style.FILL
        northPaint.color = Color.parseColor("#FF0000")
        canvas.drawPath(northPath, northPaint)

        val southPath = Path()
        southPath.moveTo(0f, needleLength)
        southPath.lineTo(-8f, 0f)
        southPath.lineTo(0f, 10f)
        southPath.lineTo(8f, 0f)
        southPath.close()

        val southPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        southPaint.style = Paint.Style.FILL
        southPaint.color = Color.parseColor("#FFFFFF")
        canvas.drawPath(southPath, southPaint)

        centerPaint.color = Color.parseColor("#FFD700")
        canvas.drawCircle(0f, 0f, 5f, centerPaint)
    }
}
