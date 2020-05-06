package com.heima.takeout.ui.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.heima.takeout.R
import com.heima.takeout.model.beans.GoodsTypeInfo
import com.heima.takeout.ui.fragment.GoodsFragment
import org.jetbrains.anko.find

class GoodsTypeRvAdapter(val context: Context, val goodsFragment: GoodsFragment) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var goodsTypeList: List<GoodsTypeInfo> = listOf()

    fun setDatas(list: List<GoodsTypeInfo>) {
        this.goodsTypeList = list
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        val goodsTypeItemHolder = holder as GoodsTypeItemHolder
        goodsTypeItemHolder.bindData(goodsTypeList.get(position), position)
    }

    override fun getItemCount(): Int {
        return goodsTypeList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.item_type, parent, false)
        return GoodsTypeItemHolder(itemView)
    }

    var selectPosition = 0 //选中的位置

    inner class GoodsTypeItemHolder(val item: View) : RecyclerView.ViewHolder(item) {
        val tvType: TextView
        val tvRedDotCount: TextView
        var mPosition: Int = 0
        lateinit var goodsTypeInfo: GoodsTypeInfo

        init {
            tvType = item.find<TextView>(R.id.type)
            tvRedDotCount = item.find<TextView>(R.id.tvRedDotCount)
            item.setOnClickListener {
                selectPosition = mPosition
                notifyDataSetChanged()
                //step2:右侧列表跳转到该类型中第一个商品
                val typeId = goodsTypeInfo.id
                //遍历所有商品，找到此position
                val position = goodsFragment.goodsFragmentPresenter.getGoodsPositionByTypeId(typeId)
                goodsFragment.slhlv.setSelection(position)
            }
        }

        fun bindData(goodsTypeInfo: GoodsTypeInfo, position: Int) {
            mPosition = position
            this.goodsTypeInfo = goodsTypeInfo
            if (position == selectPosition) {
                //选中的为白底加粗黑字，
                item.setBackgroundColor(Color.WHITE)
                tvType.setTextColor(Color.BLACK)
                tvType.setTypeface(Typeface.DEFAULT_BOLD)
            } else {
                //未选中是灰色背景 普通字体
                item.setBackgroundColor(Color.parseColor("#b9dedcdc"))
                tvType.setTextColor(Color.GRAY)
                tvType.setTypeface(Typeface.DEFAULT)
            }
            tvType.text = goodsTypeInfo.name
            tvRedDotCount.text = goodsTypeInfo.redDotCount.toString()
            if (goodsTypeInfo.redDotCount > 0) {
                tvRedDotCount.visibility = View.VISIBLE
            } else {
                tvRedDotCount.visibility = View.GONE
            }
        }
    }
}