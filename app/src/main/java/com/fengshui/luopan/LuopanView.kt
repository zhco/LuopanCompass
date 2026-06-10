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
 * 包含多层环形：天池(中心)、内盘(天盘24山)、中盘(地盘24山)、外盘(人盘24山)
 */
class LuopanView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // 二十四山名称 - 天盘(内圈)
    private val tianPan24 = arrayOf(
        "壬", "子", "癸",    // 北
        "丑", "艮", "寅",    // 东北
        "甲", "卯", "乙",    // 东
        "辰", "巽", "巳",    // 东南
        "丙", "午", "丁",    // 南
        "未", "坤", "申",    // 西南
        "庚", "酉", "辛",    // 西
        "戌", "乾", "亥"     // 西北
    )

    // 地盘24山 (与天盘偏移7.5度)
    private val diPan24 = arrayOf(
        "子", "癸", "丑",
        "艮", "寅", "甲",
        "卯", "乙", "辰",
        "巽", "巳", "丙",
        "午", "丁", "未",
        "坤", "申", "庚",
        "酉", "辛", "戌",
        "乾", "亥", "壬"
    )

    // 八卦名称
    private val baGua = arrayOf("坎", "艮", "震", "巽", "离", "坤", "兑", "乾")

    // 二十四山对应颜色 (四正为红，四隅为特殊色，其余为金)
    private val siZheng = setOf(0, 8, 12, 16) // 子卯午酉 的索引

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

        // 绘制外圈装饰
        drawOuterDecoration(canvas)

        // 绘制人盘 (外圈24山)
        draw24ShanRing(canvas, tianPan24, radius * 0.95f, radius * 0.72f, 0f)

        // 绘制地盘 (中圈24山)
        draw24ShanRing(canvas, diPan24, radius * 0.70f, radius * 0.50f, 7.5f)

        // 绘制八卦圈
        drawBaGuaRing(canvas, radius * 0.48f, radius * 0.36f)

        // 绘制天池 (中心)
        drawTianChi(canvas)

        // 绘制指针
        drawNeedle(canvas)

        canvas.restore()
    }

    private fun drawBackground(canvas: Canvas) {
        // 外圈木色背景
        val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        bgPaint.style = Paint.Style.FILL

        // 渐变背景
        val gradient = RadialGradient(0f, 0f, radius, Color.parseColor("#2A1500"), Color.parseColor("#1A0A00"), Shader.TileMode.CLAMP)
        bgPaint.shader = gradient
        canvas.drawCircle(0f, 0f, radius, bgPaint)

        // 木纹效果 - 同心圆
        val woodPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        woodPaint.style = Paint.Style.STROKE
        woodPaint.strokeWidth = 0.5f
        for (i in 0..20) {
            val r = radius * i / 20f
            woodPaint.color = Color.argb(30, 139, 90, 43)
            canvas.drawCircle(0f, 0f, r, woodPaint)
        }
    }

    private fun drawOuterDecoration(canvas: Canvas) {
        // 最外圈金色边框
        paint.strokeWidth = 3f
        paint.color = Color.parseColor("#FFD700")
        canvas.drawCircle(0f, 0f, radius, paint)
        canvas.drawCircle(0f, 0f, radius * 0.97f, paint)

        // 外圈刻度线
        paint.strokeWidth = 1f
        paint.color = Color.parseColor("#B8860B")
        for (i in 0 until 360) {
            val angleRad = Math.toRadians(i.toDouble() - currentDegree.toDouble())
            val innerR = radius * 0.97f
            val outerR = if (i % 15 == 0) radius * 0.95f else radius * 0.96f
            val x1 = (innerR * cos(angleRad)).toFloat()
            val y1 = (innerR * sin(angleRad)).toFloat()
            val x2 = (outerR * cos(angleRad)).toFloat()
            val y2 = (outerR * sin(angleRad)).toFloat()
            canvas.drawLine(x1, y1, x2, y2, paint)
        }
    }

    private fun draw24ShanRing(canvas: Canvas, shanNames: Array<String>, outerR: Float, innerR: Float, offsetDegree: Float) {
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
                // 四正: 子午卯酉 - 红色
                textPaint.color = Color.parseColor("#FF3333")
                textPaint.typeface = Typeface.DEFAULT_BOLD
            } else if (i % 3 == 0) {
                // 四隅: 乾坤艮巽 - 金色加粗
                textPaint.color = Color.parseColor("#FFD700")
                textPaint.typeface = Typeface.DEFAULT_BOLD
            } else {
                // 其余 - 浅金色
                textPaint.color = Color.parseColor("#DAA520")
                textPaint.typeface = Typeface.DEFAULT
            }

            // 旋转文字使其沿径向
            canvas.save()
            canvas.translate(x, y)
            canvas.rotate((startAngle + step / 2 + 90).toFloat())
            canvas.drawText(shanNames[i], 0f, 0f, textPaint)
            canvas.restore()
        }

        // 绘制内外圈边线
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

            // 分隔线
            paint.strokeWidth = 1f
            paint.color = Color.parseColor("#B8860B")
            val rad = Math.toRadians(startAngle.toDouble())
            canvas.drawLine(
                (innerR * cos(rad)).toFloat(), (innerR * sin(rad)).toFloat(),
                (outerR * cos(rad)).toFloat(), (outerR * sin(rad)).toFloat(), paint
            )
        }

        // 边线
        paint.strokeWidth = 2f
        paint.color = Color.parseColor("#FFD700")
        canvas.drawCircle(0f, 0f, outerR, paint)
        canvas.drawCircle(0f, 0f, innerR, paint)
    }

    private fun drawTianChi(canvas: Canvas) {
        val poolRadius = radius * 0.34f

        // 天池背景 - 深色渐变
        val poolPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        poolPaint.style = Paint.Style.FILL
        val gradient = RadialGradient(0f, 0f, poolRadius, Color.parseColor("#0A0500"), Color.parseColor("#1A0A00"), Shader.TileMode.CLAMP)
        poolPaint.shader = gradient
        canvas.drawCircle(0f, 0f, poolRadius, poolPaint)

        // 天池边框
        paint.strokeWidth = 3f
        paint.color = Color.parseColor("#FFD700")
        canvas.drawCircle(0f, 0f, poolRadius, paint)

        // 内圈装饰
        paint.strokeWidth = 1f
        canvas.drawCircle(0f, 0f, poolRadius * 0.85f, paint)

        // 十字线
        paint.strokeWidth = 1f
        paint.color = Color.parseColor("#B8860B")
        canvas.drawLine(-poolRadius * 0.8f, 0f, poolRadius * 0.8f, 0f, paint)
        canvas.drawLine(0f, -poolRadius * 0.8f, 0f, poolRadius * 0.8f, paint)

        // 中心点
        centerPaint.color = Color.parseColor("#FFD700")
        canvas.drawCircle(0f, 0f, 6f, centerPaint)
        centerPaint.color = Color.parseColor("#CC0000")
        canvas.drawCircle(0f, 0f, 3f, centerPaint)
    }

    private fun drawNeedle(canvas) {
        val needleLength = radius * 0.30f

        // 北指针 (红色)
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

        // 南指针 (白色)
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

        // 中心轴
        centerPaint.color = Color.parseColor("#FFD700")
        canvas.drawCircle(0f, 0f, 5f, centerPaint)
    }
}
