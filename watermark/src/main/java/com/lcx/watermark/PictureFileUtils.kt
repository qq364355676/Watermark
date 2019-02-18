package com.lcx.watermark

import android.graphics.Bitmap
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception

object PictureFileUtils {

    fun saveFile(file: File, bm: Bitmap): Boolean {
        if (file.exists()) {
            file.delete()
        }
        lateinit var stream : FileOutputStream
        var b : Boolean = false
        try {
            stream = FileOutputStream(file)
            b = bm.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            if (!bm.isRecycled) bm.recycle()
        } catch (e: Exception) {
            e.printStackTrace()
        }finally {
            stream.flush()
            stream.close()
        }
        return b
    }
    }
    fun saveFile(file: File, bm: Bitmap, format: Bitmap.CompressFormat, quality:Int = 100): Boolean {
        if (file.exists()) {
            file.delete()
        }
        lateinit var stream : FileOutputStream
        var b : Boolean = false
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

    fun saveFile(path : String,name:String,bm : Bitmap) : Boolean{
        val file = File(path,name)
        if (file.exists())file.delete()
        lateinit var stream : FileOutputStream
        var b : Boolean = false
        try {
            stream = FileOutputStream(file)
            b = bm.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            if (!bm.isRecycled)bm.recycle()
        }catch (e : Exception){
            e.printStackTrace()
        }finally {
            stream.flush()
            stream.close()
        }
        return b
    }
    fun saveFile(path : String,name:String,bm : Bitmap,format:Bitmap.CompressFormat, quality: Int = 100) : Boolean{
        val file = File(path,name)
        if (file.exists())file.delete()
        lateinit var stream : FileOutputStream
        var b : Boolean = false
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