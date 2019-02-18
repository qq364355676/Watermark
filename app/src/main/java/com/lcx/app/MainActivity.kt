package com.lcx.app

import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.widget.Toast
import com.bumptech.glide.Glide
import com.lcx.watermark.Watermark
import kotlinx.android.synthetic.main.activity_main.*
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private val TAG = MainActivity::class.java.simpleName
    private val permissions = arrayOf(
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
    )
    private lateinit var filesDirs: Array<File>
    private val format = SimpleDateFormat("yyyy-MM-dd_HH:mm:ss")
    private lateinit var imgName : String
    private lateinit var tempFile : File
    private var dpi = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val isCoarseLocation = ContextCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED
        val isFineLocation = ContextCompat.checkSelfPermission(this, permissions[1]) == PackageManager.PERMISSION_GRANTED
        val isReadRxternalStorage = ContextCompat.checkSelfPermission(this, permissions[2]) == PackageManager.PERMISSION_GRANTED
        if (isCoarseLocation && isFineLocation && isReadRxternalStorage) {
//            Toast.makeText(this, "权限已授予", Toast.LENGTH_SHORT).show()
            getExternalFile()
        } else {
            ActivityCompat.requestPermissions(this, permissions, 100)
        }

        btn_camera.setOnClickListener {
            imgName = format.format(Date(System.currentTimeMillis()))+".jpg"
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                //小于7.0调用相机方式
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(File(tempFile.absolutePath, imgName)))
            } else {
                //大于7.0调用相机方式
                tempFile = File(tempFile.absolutePath+File.separator+"Camera")
                var cv = ContentValues(1)
                cv.put(MediaStore.Images.Media.DATA,tempFile.absolutePath+File.separator+imgName)
                val imgUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,cv)
                intent.putExtra(MediaStore.EXTRA_OUTPUT,imgUri)
            }
            startActivityForResult(intent,101)
        }

    }

    private fun getExternalFile() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            filesDirs = getExternalFilesDirs("")
//            tempFile = if (filesDirs.size == 1) {
//                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
//            } else {
//                filesDirs[1]
//            }
//            Log.e(TAG, "文件名:${tempFile.path}")
            if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
                Log.e(TAG, "已挂载")
                tempFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
            } else {
                Log.e(TAG, "未挂载")
            }
            val displayMetrics = resources.displayMetrics
            Log.e(TAG,"屏幕宽：${displayMetrics.widthPixels}    高：${displayMetrics.heightPixels}")
            Log.e(TAG,"xdpi:${displayMetrics.xdpi}  ydpi:${displayMetrics.ydpi}")
            Log.e(TAG,"density:${displayMetrics.density}    densityDpi:${displayMetrics.densityDpi}")
            dpi = displayMetrics.densityDpi
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val file = File(tempFile.absolutePath, imgName)
        var bitmap = BitmapFactory.decodeFile(file.absolutePath)
        val output  = FloatArray(2)
        val exif = ExifInterface(file.absolutePath)
        //获取图片的方向
        val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,ExifInterface.ORIENTATION_NORMAL)
        //获取图片的旋转角度
        val degrees = when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90f
            ExifInterface.ORIENTATION_ROTATE_180 -> 180f
            ExifInterface.ORIENTATION_ROTATE_270 -> 270f
            else -> 0f
        }
        // 根据图片的旋转角度旋转图片
        if (degrees != 0f) {
            val matrix = Matrix()
            matrix.postRotate(degrees)
            bitmap = Bitmap.createBitmap(bitmap,0,0,bitmap.width,bitmap.height,matrix,true)
        }
        val b = exif.getLatLong(output)
        if (b) {
            Log.e(TAG,"纬度：${output[0]}  经度${output[1]}")
            val content = "坐标信息：${output[0]}\t${output[1]}"
            var textSize = when (dpi) {
                in 0..160 -> 60f
                in  161..320 -> 120f
                in 321..480 -> 180f
                else -> {
                    240f
                }
            }
            val newBitmap = Watermark.addTextWatermark(bitmap,content,textSize)
            if (newBitmap != null) {
                Glide.with(this)
                    .load(newBitmap)
                    .into(iv_pic)
            }
        }else{
            Log.e(TAG,"没有获取位置信息")
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults != null && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            Toast.makeText(this, "权限已授予", Toast.LENGTH_SHORT).show()
            getExternalFile()
        } else {
            Toast.makeText(this, "权限被拒绝", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * 往图片中添加文本水印
     * @param src 原图片Bitmap
     * @param content 文字
     * @param textSize 字体大小
     * @param isRecycle 是否回收
     * @return 带水印的bitmap
     */
    private fun addTextWatermark(src: Bitmap, content: String, textSize: Float, isRecycle: Boolean): Bitmap? {
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

    /**
     * 将bitmap保存到文件
     * @param src 图片bitmap
     * @param format 图片格式
     * @param path 文件路径
     *
     * @return true 成功  false 失败
     */
    private fun saveFile(src: Bitmap, format: Bitmap.CompressFormat, path: File): Boolean {
        var isSuccess = false
        if (src == null) {
            return false
        }
        try {
            var stream = BufferedOutputStream(FileOutputStream(path))
            isSuccess = src.compress(format, 100, stream)
            if (!src.isRecycled)src.recycle()
            stream.flush()
            stream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return isSuccess
    }

}
