package com.heima.takeout.ui.adapter

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.heima.takeout.R
import com.heima.takeout.model.beans.Order
import com.heima.takeout.ui.activity.OrderDetailActivity
import com.heima.takeout.utils.OrderObservable
import org.jetbrains.anko.find
import org.json.JSONObject
import java.util.*

/**
 * 订单观察者
 */
class OrderRvAdapter(val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>(),Observer {
    init {
        OrderObservable.instance.addObserver(this)  //让观察者和被观察者建立绑定关系
    }

    override fun update(observer: Observable?, data: Any?) {
        //观察者的响应
        Log.e("order","观察者收到了消息，消息内容：" + data)
        //更新UI
        val jsonObj : JSONObject = JSONObject(data as String)
        val pushOrderId = jsonObj.getString("orderId")
        val pushType = jsonObj.getString("type")
        var index = -1
        for(i in 0 until orderList.size){
            val order = orderList.get(i)
            if(order.id.equals(pushOrderId)){
                order.type = pushType
                index = i
            }
        }
        if(index !=-1) {
            notifyItemChanged(index) //刷新单个条目，减少UI开销
        }
//        notifyDataSetChanged()
    }

    private var orderList: List<Order> = ArrayList<Order>()

    fun setOrderData(orders: List<Order>) {
        this.orderList = orders
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
//        val itemView = View.inflate(context, R.layout.item_order_item, null)
        //TODO:没有填充满,原因是recycleview的孩子，测量模式是UNSPECIFY
        //通过返回值已经addview,如果attachToRoot使用true会再一次addView()，就会报错
        val itemView = LayoutInflater.from(context).inflate(R.layout.item_order_item,parent, false)
        return OrderItemHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        (holder as OrderItemHolder).bindData(orderList.get(position))
    }

    override fun getItemCount(): Int {
        return orderList.size
    }

    inner class OrderItemHolder(item: View) : RecyclerView.ViewHolder(item) {
        val tvSellerName: TextView
        val tvOrderType: TextView
        lateinit var order:Order
        init {
            //findviewbyId tv_order_item_seller_name tv_order_item_type
            tvSellerName = item.find(R.id.tv_order_item_seller_name)
            tvOrderType = item.find(R.id.tv_order_item_type) //订单状态
            item.setOnClickListener {
                val intent: Intent =  Intent(context, OrderDetailActivity::class.java)
                intent.putExtra("orderId", order.id)
                intent.putExtra("type", order.type)
                context.startActivity(intent)
            }
        }

        fun bindData(order: Order) {
            this.order = order
            tvSellerName.text = order.seller.name
            tvOrderType.text = getOrderTypeInfo(order.type)
        }
    }

    private fun getOrderTypeInfo(type: String): String {
        /**
         * 订单状态
         * 1 未支付 2 已提交订单 3 商家接单  4 配送中,等待送达 5已送达 6 取消的订单
         */
        //            public static final String ORDERTYPE_UNPAYMENT = "10";
        //            public static final String ORDERTYPE_SUBMIT = "20";
        //            public static final String ORDERTYPE_RECEIVEORDER = "30";
        //            public static final String ORDERTYPE_DISTRIBUTION = "40";
        //            public static final String ORDERTYPE_SERVED = "50";
        //            public static final String ORDERTYPE_CANCELLEDORDER = "60";

        var typeInfo = ""
        when (type) {
            OrderObservable.ORDERTYPE_UNPAYMENT -> typeInfo = "未支付"
            OrderObservable.ORDERTYPE_SUBMIT -> typeInfo = "已提交订单"
            OrderObservable.ORDERTYPE_RECEIVEORDER -> typeInfo = "商家接单"
            OrderObservable.ORDERTYPE_DISTRIBUTION -> typeInfo = "配送中"
            OrderObservable.ORDERTYPE_SERVED -> typeInfo = "已送达"
            OrderObservable.ORDERTYPE_CANCELLEDORDER -> typeInfo = "取消的订单"
        }
        return typeInfo
    }
}