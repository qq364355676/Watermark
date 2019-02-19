package com.lcx.watermark

import android.content.Context
import com.amap.api.services.geocoder.GeocodeResult
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import com.amap.api.services.core.AMapException
import com.amap.api.services.geocoder.RegeocodeResult
import com.amap.api.services.geocoder.GeocodeSearch
import com.amap.api.services.geocoder.RegeocodeQuery
import com.amap.api.services.core.LatLonPoint



object AMapGeocode {

    /**
     * 逆地理编码 根据坐标点得到地理位置
     *
     * @param context 当前调用的context
     * @param lat 纬度
     * @param lng 经度
     */

     fun getRegeocode(context: Context, lat: Double, lng: Double) : String{
        var addr = ""
        val geocodeSearch = GeocodeSearch(context)
        val latLonPoint = LatLonPoint(lat, lng)
        val query = RegeocodeQuery(latLonPoint, 10f, GeocodeSearch.GPS)
        geocodeSearch.getFromLocationAsyn(query)
        geocodeSearch.setOnGeocodeSearchListener(object : GeocodeSearch.OnGeocodeSearchListener {
            /**
             * 逆地理编码回调
             */
            override fun onRegeocodeSearched(regeocodeResult: RegeocodeResult?, i: Int) {
                if (i == AMapException.CODE_AMAP_SUCCESS) {
                    if (regeocodeResult != null && regeocodeResult.regeocodeAddress != null
                        && regeocodeResult.regeocodeAddress.formatAddress != null
                    ) {
                        addr = regeocodeResult.regeocodeAddress.formatAddress
                    } else {
                        addr = ""
                    }
                } else {
                    addr = ""
                }
            }

            /**
             * 地理编码回调
             * @param geocodeResult
             * @param i
             */
            override fun onGeocodeSearched(geocodeResult: GeocodeResult, i: Int) {}
        })
        return addr
    }
}