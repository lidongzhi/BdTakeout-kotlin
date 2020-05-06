package com.heima.takeout.ui.activity

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationListener
import com.amap.api.maps2d.AMap
import com.amap.api.maps2d.CameraUpdateFactory
import com.amap.api.maps2d.MapView
import com.amap.api.maps2d.model.LatLng
import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.core.PoiItem
import com.amap.api.services.poisearch.PoiResult
import com.amap.api.services.poisearch.PoiSearch
import com.heima.takeout.R
import com.heima.takeout.ui.adapter.AroundRvAdapter
import kotlinx.android.synthetic.main.activity_map_location.*
import org.jetbrains.anko.toast
import java.util.*


class MapLocationActivity : AppCompatActivity(), AMapLocationListener, PoiSearch.OnPoiSearchListener {
    override fun onPoiItemSearched(poiItem: PoiItem?, rcode: Int) {

    }

    override fun onPoiSearched(poiResult: PoiResult?, rcode: Int) {
        if(rcode == 1000){
            if(poiResult!=null) {
                val poiItems: ArrayList<PoiItem> = poiResult.pois!!
                adapter.setPoiItemList(poiItems)
            }

        }
    }

    override fun onLocationChanged(aMapLocation: AMapLocation?) {
        if (aMapLocation != null) {
            toast(aMapLocation.address)
            //移动地图到当前位置
            aMap.moveCamera(CameraUpdateFactory.changeLatLng(LatLng(aMapLocation.latitude, aMapLocation.longitude)))
            aMap.moveCamera(CameraUpdateFactory.zoomTo(16f))
            doSearchQuery(aMapLocation)
            mLocationClient.stopLocation()
        }
    }

    private fun doSearchQuery(aMapLocation: AMapLocation) {
        val query = PoiSearch.Query("", "", aMapLocation.city)
//keyWord表示搜索字符串，
//第二个参数表示POI搜索类型，二者选填其一，选用POI搜索类型时建议填写类型代码，码表可以参考下方（而非文字）
//cityCode表示POI搜索区域，可以是城市编码也可以是城市名称，也可以传空字符串，空字符串代表全国在全国范围内进行搜索
        query.pageSize = 30// 设置每页最多返回多少条poiitem
        query.pageNum = 1//设置查询页码
        val poiSearch = PoiSearch(this, query)
        //搜索范围
        poiSearch.bound = PoiSearch.SearchBound(LatLonPoint(aMapLocation.latitude,aMapLocation.longitude), 350)
        poiSearch.setOnPoiSearchListener(this)
        poiSearch.searchPOIAsyn();
    }

    //声明AMapLocationClient类对象
    lateinit var mLocationClient: AMapLocationClient
    //地图控制器
    lateinit var aMap: AMap
    lateinit var adapter:AroundRvAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_location)

        val mapView = findViewById(R.id.map) as MapView
        rv_around.layoutManager = LinearLayoutManager(this)
        adapter = AroundRvAdapter(this)
        rv_around.adapter = adapter
        mapView.onCreate(savedInstanceState)// 此方法必须重写
        aMap = mapView.map

        checkPermision()

    }

    private val WRITE_COARSE_LOCATION_REQUEST_CODE: Int = 10

    private fun checkPermision() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            //申请WRITE_EXTERNAL_STORAGE权限
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                    WRITE_COARSE_LOCATION_REQUEST_CODE);//自定义的code
        } else {
            initLocation()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //用户在对话框中点击允许
            initLocation()
        } else {
            finish()
            toast("需要有定位权限才能成功定位")
        }
    }

    private fun initLocation() {
        //初始化定位
        mLocationClient = AMapLocationClient(getApplicationContext());
//设置定位回调监听
        mLocationClient.setLocationListener(this);
//启动定位
        mLocationClient.startLocation();
    }
}