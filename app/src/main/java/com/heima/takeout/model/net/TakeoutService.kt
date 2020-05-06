package com.heima.takeout.model.net

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import rx.Observable


/**
 * Created by lidongzhi on 2017/8/30.
 */
interface TakeoutService {

//    @GET("users/{user}/repos")
//    fun listRepos(@Path("user") user: String): Call<List<Repo>>

    @GET("home")
    fun getHomeInfo(): Call<ResponseInfo>

    @GET("login")
    fun loginByPhone(@Query("phone") phone: String): Call<ResponseInfo>

    @GET("order")
    fun getOrderList(@Query("id") userId: String): Call<ResponseInfo>

    //使用Rxjava组合的接口
    @GET("order")
    fun getOrderListByRxjava(@Query("id") userId: String): Observable<ResponseInfo>

    @GET("business")
    fun getBusinessInfo(@Query("sellerId") sellerId: String): Call<ResponseInfo>
}