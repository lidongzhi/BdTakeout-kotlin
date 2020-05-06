package com.heima.takeout.ui.fragment

import android.app.Fragment
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.heima.takeout.R
import com.heima.takeout.model.beans.Order
import com.heima.takeout.presenter.OrderFragmentPresenter
import com.heima.takeout.ui.adapter.OrderRvAdapter
import com.heima.takeout.utils.TakeoutApp
import org.jetbrains.anko.find
import org.jetbrains.anko.toast

/**
 * Created by lidongzhi on 2017/8/28.
 */

class OrderFragment : Fragment() {
    lateinit var rvOrder: RecyclerView
    lateinit var swipeLayout:SwipeRefreshLayout
    lateinit var orderPresenter: OrderFragmentPresenter
    lateinit var adapter: OrderRvAdapter
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val orderView = View.inflate(activity, R.layout.fragment_order, null)
        orderPresenter = OrderFragmentPresenter(this)
        rvOrder = orderView.find<RecyclerView>(R.id.rv_order_list)
        rvOrder.layoutManager = LinearLayoutManager(activity)
        adapter = OrderRvAdapter(activity)
        rvOrder.adapter = adapter
        swipeLayout = orderView.find<SwipeRefreshLayout>(R.id.srl_order)
        swipeLayout.setOnRefreshListener(object : SwipeRefreshLayout.OnRefreshListener{
            override fun onRefresh() {
                //下拉后重新请求
                val userId = TakeoutApp.sUser.id
                if (-1 == userId) {
                    toast("请先登录，再查看订单")
                } else {
                    orderPresenter.getOrderList(userId.toString())
                }
            }
        })

        return orderView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //访问服务器，获取所有订单数据
        val userId = TakeoutApp.sUser.id
        if (-1 == userId) {
            toast("请先登录，再查看订单")
        } else {
            orderPresenter.getOrderList(userId.toString())
        }
    }

    fun onOrderSuccess(orderList: List<Order>) {
        //TODO:给adapter设置数据
        adapter.setOrderData(orderList)
        swipeLayout.isRefreshing = false
    }

    fun onOrderFailed() {
        toast("服务器繁忙")
        swipeLayout.isRefreshing = false
    }
}
