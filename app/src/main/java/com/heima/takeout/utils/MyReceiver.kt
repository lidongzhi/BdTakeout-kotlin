package com.heima.takeout.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import cn.jpush.android.api.JPushInterface



class MyReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val bundle:Bundle? = intent?.extras
        if(bundle!=null){
            val message:String? = bundle.getString(JPushInterface.EXTRA_MESSAGE)
//            Log.e("order", message)  //消息内容
            val extras:String? = bundle.getString(JPushInterface.EXTRA_EXTRA)
            if(!TextUtils.isEmpty(extras)){
                Log.e("order", extras)  //附加字段
                //方法一：通过mainactivity ---> orderFragment ---> OrderRvAdapter ---> 第二个条目的tvOrderType ---> 改变值40
                //方法二：使用观察者模式(OrderRvAdapter是观察者，MyReceiver是被观察者）
                OrderObservable.instance.newMsgComing(extras!!)

            }
        }
    }
}