package com.heima.takeout.ui.fragment

import android.app.Fragment
import android.graphics.Color
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.heima.takeout.R
import com.heima.takeout.dagger2.component.DaggerHomeFragmentComponent
import com.heima.takeout.dagger2.module.HomeFragmentModule
import com.heima.takeout.model.beans.Seller
import com.heima.takeout.presenter.HomeFragmentPresenter
import com.heima.takeout.ui.adapter.HomeRvAdapter
import dagger.internal.DaggerCollections
import kotlinx.android.synthetic.main.fragment_home.*
import org.jetbrains.anko.find
import org.jetbrains.anko.toast
import javax.inject.Inject

/**
 * Created by lidongzhi on 2017/8/28.
 */

class HomeFragment : Fragment() {
    lateinit var homeRvAdapter: HomeRvAdapter
    lateinit var rvHome: RecyclerView


    @Inject
    lateinit var homeFragmentPresenter:HomeFragmentPresenter
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = View.inflate(activity, R.layout.fragment_home, null)
        rvHome = view.find<RecyclerView>(R.id.rv_home)
        rvHome.layoutManager = LinearLayoutManager(activity)  //从上到下的列表视图
        homeRvAdapter = HomeRvAdapter(activity)
        rvHome.adapter = homeRvAdapter
//        homeFragmentPresenter = HomeFragmentPresenter(this)
        //TODO:解耦View层和P层，通过dagger2（基于注解的依赖注入）生成HomeFragmentPresenter
        DaggerHomeFragmentComponent.builder().homeFragmentModule(HomeFragmentModule(this)).build().inject(this)
        distance = 120.dp2px()
        return view
    }

    fun Int.dp2px(): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                toFloat(), resources.displayMetrics).toInt()

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initData()
    }


    val datas: ArrayList<String> = ArrayList<String>()
    var sum: Int = 0
    var distance: Int = 0
    var alpha = 55
    private fun initData() {
//        for (i in 0 until 100) {
//            datas.add("我是商家：" + i)
//        }
        homeFragmentPresenter.getHomeInfo()
//        homeRvAdapter.setData(datas)
    }

    val allList:ArrayList<Seller> = ArrayList()
    fun onHomeSuccess(nearbySellers: List<Seller>, otherSellers: List<Seller>) {
        allList.clear()
        allList.addAll(nearbySellers)
        allList.addAll(otherSellers)
        homeRvAdapter.setData(allList)

        //有数据可以滚动才可以监听滚动事件
        rvHome.setOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
            }

            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                sum += dy
                if (sum > distance) {
                    alpha = 255
                } else {
                    alpha = sum * 200 / distance
                    alpha += 55
                }
                Log.e("home", "alpha:$alpha")
                ll_title_container.setBackgroundColor(Color.argb(alpha,0x31,0x90,0xe8))
            }
        })
    }

    fun onHomeFailed() {
        toast("获取首页数据失败")
    }
}
