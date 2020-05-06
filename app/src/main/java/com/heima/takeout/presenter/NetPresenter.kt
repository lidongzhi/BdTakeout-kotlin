package com.heima.takeout.presenter

import android.util.Log
import com.heima.takeout.model.net.ResponseInfo
import com.heima.takeout.model.net.TakeoutService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by lidongzhi on 2017/9/1.
 */
open abstract class NetPresenter {
    val takeoutService: TakeoutService

    init {
        val retrofit = Retrofit.Builder()
                .baseUrl("http://192.168.0.104:8080/TakeoutService/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build()

        takeoutService = retrofit.create<TakeoutService>(TakeoutService::class.java!!)
    }

    abstract fun parserJson(json: String)

    val callback = object : Callback<ResponseInfo> {
        override fun onResponse(call: Call<ResponseInfo>?, response: Response<ResponseInfo>?) {
            if (response == null) {
                Log.e("home", "服务器没有成功返回")
            } else {
                if (response.isSuccessful()) {
                    val responseInfo = response.body()
                    if (responseInfo.code.equals("0")) {
                        val json = responseInfo.data
                        parserJson(json)
                    } else if (responseInfo.code.equals("-1")) {
                        //根据具体接口文档表示含义，比如定义-1为空数据
                        Log.e("home", "定义-1为空数据")
                    }
                } else {
                    Log.e("home", "服务器代码错误")
                }
            }
        }

        override fun onFailure(call: Call<ResponseInfo>?, t: Throwable?) {
            //没有连上服务器
            Log.e("home", "没有连上服务器")
        }
    }
}