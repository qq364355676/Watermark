package com.lcx.watermark

import android.content.Context
import android.graphics.*
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.DisplayMetrics



/**
 * @author lcx
 * @date 2019/02/18
 *
 */
object Watermark {
    /**
     * 水印颜色
     */
    private var color : Int = 0
    /**
     * 水印的位置
     */
    private var x : Float = 0.0f
    private var y : Float = 0.0f
    /**
     *  设置水印的x位置
     */
    fun setX(x: Float): Watermark {
        this.x = x
        return this
    }

    /**
     * 设置水印的y位置
     */
    fun setY(y: Float): Watermark {
        this.y = y
        return this
    }

    /**
     * 设置水印的颜色
     */
    fun setColor(color: Int): Watermark {
        this.color = color
        return this
    }

    /**
     * 添加水印
     */
    fun addTextWatermark(context: Context, src: Bitmap, content: String, textSize: Float): Bitmap? {
        if (src == null || content.isEmpty()) {
            return null
        }
        val copyBitmap = src.copy(src.config, true)
        val paint = TextPaint(Paint.ANTI_ALIAS_FLAG)
        paint.textSize = dip2px(context,textSize)
        paint.color = if (color == 0) Color.RED else color
        val canvas = Canvas(copyBitmap)
        val rect = Rect()
        paint.getTextBounds(content,0,content.length,rect)
//        val layout = StaticLayout.Builder
//            .obtain(content,0,content.length,paint,src.width)
//            .build()
        val layout = StaticLayout(content,0,content.length,paint,src.width, Layout.Alignment.ALIGN_NORMAL,1.0f,0.5f,true)
        x = if (x == 0f) src.width * 0.02f else x
        y = if (y == 0f) src.height * 0.7f else y
        canvas.translate(x,y)
        layout.draw(canvas)
//        canvas.drawText(content,x,y,paint)
        if (!src.isRecycled) {
            src.recycle()
        }
        return copyBitmap
    }

    fun dip2px(context: Context, dipVlue: Float): Float {
        val metrics = context.getResources().getDisplayMetrics()
        val sDensity = metrics.density
        return (dipVlue * sDensity + 0.5f)
    }
}