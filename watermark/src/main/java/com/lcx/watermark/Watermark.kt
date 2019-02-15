package com.lcx.watermark

import android.graphics.*

object Watermark {
    fun addTextWatermark(src: Bitmap, content: String, textSize: Float, isRecycle: Boolean): Bitmap? {
        if (src == null || content.isEmpty()) {
            return null
        }
        val copyBitmap = src.copy(src.config, true)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.textSize = textSize
        paint.color = Color.RED
        val canvas = Canvas(copyBitmap)
        val rect = Rect()
        paint.getTextBounds(content,0,content.length,rect)
        canvas.drawText(content,src.width * 0.05f,src.height * 0.7f,paint)
        if (isRecycle && !src.isRecycled) {
            src.recycle()
        }
        return copyBitmap
    }
}