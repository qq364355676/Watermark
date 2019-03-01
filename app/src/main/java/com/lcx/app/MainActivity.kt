package com.lcx.app

import android.app.Activity
import android.content.ContentValues
import android.content.Context
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
import android.support.v4.content.ContextCompat
import android.util.Log
import android.widget.Toast
import com.amap.api.services.core.AMapException
import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.geocoder.GeocodeResult
import com.amap.api.services.geocoder.GeocodeSearch
import com.amap.api.services.geocoder.RegeocodeQuery
import com.amap.api.services.geocoder.RegeocodeResult
import com.bumptech.glide.Glide
import com.lcx.skiplibrary.FirstActivity
import com.lcx.watermark.PictureFileUtils
import com.lcx.watermark.Watermark
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
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
//            imgName = format.format(Date(System.currentTimeMillis()))+".jpg"
//            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
//                //小于7.0调用相机方式
//                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(File(tempFile.absolutePath, imgName)))
//            } else {
//                //大于7.0调用相机方式
//                var cv = ContentValues(1)
//                cv.put(MediaStore.Images.Media.DATA,tempFile.absolutePath+File.separator+imgName)
//                val imgUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,cv)
//                intent.putExtra(MediaStore.EXTRA_OUTPUT,imgUri)
//            }
//            startActivityForResult(intent,101)
            val intent = Intent(this@MainActivity,FirstActivity::class.java)
            startActivityForResult(intent,1)
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
            tempFile = File(tempFile.absolutePath+File.separator+"Camera")
            if (!tempFile.exists())tempFile.mkdirs()
            Log.e(TAG, "回调前的文件路径：${tempFile.absolutePath}")
            val displayMetrics = resources.displayMetrics
//            Log.e(TAG,"屏幕宽：${displayMetrics.widthPixels}    高：${displayMetrics.heightPixels}")
//            Log.e(TAG,"xdpi:${displayMetrics.xdpi}  ydpi:${displayMetrics.ydpi}")
//            Log.e(TAG,"density:${displayMetrics.density}    densityDpi:${displayMetrics.densityDpi}")
            dpi = displayMetrics.densityDpi
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 101) {
            val file = File(tempFile.absolutePath, imgName)
            Log.e(TAG, "回调后的文件路径：${file.absolutePath}")
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

//                getRegeocode(this,output[0].toDouble(),output[1].toDouble(),bitmap)
                val content = "坐标信息：${output[0]}， ${output[1]}\n地址：中国河南郑州经开区云保遥感科技有限公司"
                val newBitmap = Watermark.addTextWatermark(this,bitmap,content,16f)
                if (newBitmap != null) {
                    val b = PictureFileUtils.saveFile(file, newBitmap)
                    Glide.with(this)
                        .load(newBitmap)
                        .into(iv_pic)
                }
            }else{
                Log.e(TAG,"没有获取位置信息")
            }

        }else if (requestCode == 1) {
            Log.e(TAG,"返回正确")
            data?.setClass(this,Main2Activity::class.java)
            startActivity(data)
        }

    }


    /**
     * 高德云检索SDK，逆地理编码功能
     * 目前云检索功能不支持debug版，支持release版，debug版报1008错误
     *
     */
    private fun getRegeocode(context: Context, lat: Double, lng: Double,bitmap: Bitmap){
        val geocodeSearch = GeocodeSearch(context)
        val latLonPoint = LatLonPoint(lat, lng)
        val query = RegeocodeQuery(latLonPoint, 1f, GeocodeSearch.GPS)
//        val regeocodeAddress = geocodeSearch.getFromLocation(query)
//        return regeocodeAddress.formatAddress
        geocodeSearch.getFromLocationAsyn(query)
        geocodeSearch.setOnGeocodeSearchListener(object : GeocodeSearch.OnGeocodeSearchListener {
            /**
             * 逆地理编码回调
             */
            override fun onRegeocodeSearched(regeocodeResult: RegeocodeResult?, i: Int) {
                Log.e(TAG,"返回码：$i")
                Toast.makeText(this@MainActivity,"返回码：$i",Toast.LENGTH_SHORT).show()
                if (i == AMapException.CODE_AMAP_SUCCESS) {
                    if (regeocodeResult != null && regeocodeResult.regeocodeAddress != null
                        && regeocodeResult.regeocodeAddress.formatAddress != null
                    ) {
                        val addr = regeocodeResult.regeocodeAddress.formatAddress
                        val content = "坐标信息：$lat, $lng\n地址：$addr"
                        val newBitmap = Watermark.addTextWatermark(this@MainActivity, bitmap,content,16f)
                        if (newBitmap != null) {
                            val file = File(tempFile.absolutePath, imgName)
                            val b = PictureFileUtils.saveFile(file, newBitmap)
                            Glide.with(this@MainActivity)
                                .load(newBitmap)
                                .into(iv_pic)
                        }
                        Log.e(TAG,"11111当前地址为：$addr")
                    } else {

                    }
                } else {

                }
            }

            /**
             * 地理编码回调
             * @param geocodeResult
             * @param i
             */
            override fun onGeocodeSearched(geocodeResult: GeocodeResult, i: Int) {}
        })
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(messageEvent: MessageEvent) {

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
    private fun addTextWatermark(src: Bitmap, content: String, textSize: Float): Bitmap? {
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
        if (!src.isRecycled) {
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

