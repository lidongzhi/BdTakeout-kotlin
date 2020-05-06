package com.heima.takeout.ui.fragment

import android.app.Fragment
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import com.heima.takeout.R
import com.heima.takeout.model.beans.GoodsInfo
import com.heima.takeout.model.beans.GoodsTypeInfo
import com.heima.takeout.presenter.GoodsFragmentPresenter
import com.heima.takeout.ui.activity.BusinessActivity
import com.heima.takeout.ui.adapter.GoodsAdapter
import com.heima.takeout.ui.adapter.GoodsTypeRvAdapter
import org.jetbrains.anko.find
import se.emilsjolander.stickylistheaders.StickyListHeadersListView

class GoodsFragment : Fragment() {
    lateinit var rvGoodsType: RecyclerView
    lateinit var slhlv: StickyListHeadersListView
    lateinit var goodsFragmentPresenter: GoodsFragmentPresenter
    lateinit var goodsAdapter: GoodsAdapter
    lateinit var goodsTypeAdapter :GoodsTypeRvAdapter
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val goodsView = LayoutInflater.from(activity).inflate(R.layout.fragment_goods, container, false)
        rvGoodsType = goodsView.find(R.id.rv_goods_type)
        slhlv = goodsView.find<StickyListHeadersListView>(R.id.slhlv)
        goodsAdapter = GoodsAdapter(activity,this)
        slhlv.adapter = goodsAdapter
        rvGoodsType.layoutManager = LinearLayoutManager(activity)
        goodsTypeAdapter = GoodsTypeRvAdapter(activity, this)
        rvGoodsType.adapter = goodsTypeAdapter
        goodsFragmentPresenter = GoodsFragmentPresenter(this)
        return goodsView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        goodsFragmentPresenter.getBusinessInfo((activity as BusinessActivity).seller.id.toString())
    }

    fun onLoadBusinessSuccess(goodstypeList: List<GoodsTypeInfo>, allTypeGoodsList: ArrayList<GoodsInfo>) {
        goodsTypeAdapter.setDatas(goodstypeList)  //左侧列表
        //右侧列表
        goodsAdapter.setDatas(allTypeGoodsList)

        slhlv.setOnScrollListener(object  : AbsListView.OnScrollListener{

            override fun onScroll(view: AbsListView?, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
                //先找出旧的类别
                val oldPosition = goodsTypeAdapter.selectPosition

                val newTypeId = goodsFragmentPresenter.allTypeGoodsList.get(firstVisibleItem).typeId
                //把新的id找到它对应的position
                val newPositon = goodsFragmentPresenter.getTypePositionByTypeId(newTypeId)
                //当newPositon与旧的不同时，证明需要切换类别了
                if(newPositon!=oldPosition){
                    goodsTypeAdapter.selectPosition = newPositon
                    goodsTypeAdapter.notifyDataSetChanged()
                }
            }

            override fun onScrollStateChanged(view: AbsListView?, scrollState: Int) {

            }
        })

    }
}