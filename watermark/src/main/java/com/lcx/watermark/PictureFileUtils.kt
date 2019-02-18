package com.lcx.watermark

import android.graphics.Bitmap
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception

/**
 * @author lcx
 * @date 2019/02/18
 */
object PictureFileUtils {

    /**
     * 保存图片到文件
     * @param file 图片路径文件
     * @param bm 图片bitmap对象
     * @param format 图片保存的格式
     * @param quality 压缩比例
     * @return true 成功 false 失败
     */
    fun saveFile(file: File, bm: Bitmap, format: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG, quality:Int = 100): Boolean {
        if (file.exists()) {
            file.delete()
        }
        lateinit var stream : FileOutputStream
        var b = false
        try {
            stream = FileOutputStream(file)
            b = bm.compress(format, quality, stream)
            if (!bm.isRecycled) bm.recycle()
        } catch (e: Exception) {
            e.printStackTrace()
        }finally {
            stream.flush()
            stream.close()
        }
        return b
    }

    /**
     * 保存图片到文件
     * @param path 图片路径
     * @param name 图片名字
     * @param bm 图片bitmap对象
     * @param format 图片格式
     * @param quality 压缩比例
     * @return true 成功  false 失败
     */
    fun saveFile(path : String, name:String, bm : Bitmap, format:Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG, quality: Int = 100) : Boolean{
        val file = File(path,name)
        if (file.exists())file.delete()
        lateinit var stream : FileOutputStream
        var b = false
        try {
            stream = FileOutputStream(file)
            b = bm.compress(format, quality, stream)
            if (!bm.isRecycled)bm.recycle()
        }catch (e : Exception){
            e.printStackTrace()
        }finally {
            stream.flush()
            stream.close()
        }
        return b
    }
}