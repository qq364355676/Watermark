package com.lcx.watermark

import android.graphics.Bitmap
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception

object PictureFileUtils {

    fun saveFile(path : String,name:String,bm : Bitmap) : Boolean{
        val file = File(path,name)
        if (file.exists())file.delete()
        lateinit var out : FileOutputStream
        var b : Boolean = false
        try {
            out = FileOutputStream(file)
            b = bm.compress(Bitmap.CompressFormat.JPEG, 100, out)
            if (!bm.isRecycled)bm.recycle()
        }catch (e : Exception){
            e.printStackTrace()
        }finally {
            out.flush()
            out.close()
        }
        return b
    }
}