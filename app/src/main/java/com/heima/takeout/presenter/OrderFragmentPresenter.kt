package com.heima.takeout.presenter

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.heima.takeout.model.beans.Order
import com.heima.takeout.model.net.ResponseInfo
import com.heima.takeout.ui.fragment.OrderFragment
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * Created by lidongzhi on 2017/9/4.
 */
class OrderFragmentPresenter(val orderFragment: OrderFragment) : NetPresenter() {

    fun getOrderList(userId: String) {
        val observable: Observable<ResponseInfo> = takeoutService.getOrderListByRxjava(userId)
//        orderCall.enqueue(callback)
//        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
//                .subscribe(object : Observer<ResponseInfo> {
//                    override fun onCompleted() {
//                    }
//
//                    override fun onError(e: Throwable?) {
//                        if (e != null) {
//                            Log.e("rxjava", e.localizedMessage);
//                        }
//                    }
//
//                    override fun onNext(responseInfo: ResponseInfo?) {
//                        if (responseInfo != null) {
//                            parserJson(responseInfo.data)
//                        }
//                    }
//                })

        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    parserJson(it.data)
                }, {
                    Log.e("rxjava", it.localizedMessage);

                }, {
                    Log.e("rxjava", "onComplete!");
                })

    }

    override fun parserJson(json: String) {
        //此处解析，List<Order>
        val orderList: List<Order> = Gson().fromJson(json, object : TypeToken<List<Order>>() {}.type)
        if (orderList.isNotEmpty()) {
            orderFragment.onOrderSuccess(orderList)
        } else {
            orderFragment.onOrderFailed()
        }
    }
}