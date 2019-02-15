package com.lcx.watermark

import android.graphics.*

object Watermark {
    /**
     * 水印颜色
     */
    var color : Int = 0
    /**
     * 水印的位置
     */
    var x : Float = 0.0f
    var y : Float = 0.0f
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
    fun addTextWatermark(src: Bitmap, content: String, textSize: Float): Bitmap? {
        if (src == null || content.isEmpty()) {
            return null
        }
        val copyBitmap = src.copy(src.config, true)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.textSize = textSize
        paint.color = if (color == 0) Color.RED else color
        val canvas = Canvas(copyBitmap)
        val rect = Rect()
        paint.getTextBounds(content,0,content.length,rect)
        x = if (x == 0f) src.width * 0.05f else x
        y = if (y == 0f) src.height * 0.7f else y
        canvas.drawText(content,x,y,paint)
        if (!src.isRecycled) {
            src.recycle()
        }
        return copyBitmap
    }
}