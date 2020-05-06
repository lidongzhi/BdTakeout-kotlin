package com.heima.takeout.ui.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.*
import android.widget.BaseAdapter
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.heima.takeout.R
import com.heima.takeout.model.beans.CacheSelectedInfo
import com.heima.takeout.model.beans.GoodsInfo
import com.heima.takeout.ui.activity.BusinessActivity
import com.heima.takeout.ui.fragment.GoodsFragment
import com.heima.takeout.utils.Constants
import com.heima.takeout.utils.PriceFormater
import com.heima.takeout.utils.TakeoutApp
import com.squareup.picasso.Picasso
import org.jetbrains.anko.find
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter

class GoodsAdapter(val context: Context, val goodsFragment: GoodsFragment) : BaseAdapter(), StickyListHeadersAdapter {
    companion object

    val DURATION: Long = 1000

    inner class GoodsItemHolder(itemView: View) : View.OnClickListener {
        lateinit var goodsInfo: GoodsInfo

        override fun onClick(v: View?) {
            var isAdd: Boolean = false
            when (v?.id) {
                R.id.ib_add -> {
                    isAdd = true
                    doAddOperation()
                }
                R.id.ib_minus -> doMinusOperation()
            }
            processRedDotCount(isAdd)
            (goodsFragment.activity as BusinessActivity).updateCartUi();
        }

        private fun processRedDotCount(isAdd: Boolean) {
            //找到此商品属于的类别
            val typeId = goodsInfo.typeId
            //找到此类别在左侧列表中的位置（遍历）
            val typePosition = goodsFragment.goodsFragmentPresenter.getTypePositionByTypeId(typeId)
            //最后找出tvRedDotCount
            val goodsTypeInfo = goodsFragment.goodsFragmentPresenter.goodstypeList.get(typePosition)
            var redDotCount = goodsTypeInfo.redDotCount
            if (isAdd) {
                redDotCount++
            } else {
                redDotCount--
            }
            goodsTypeInfo.redDotCount = redDotCount
            //刷新左侧列表
            goodsFragment.goodsTypeAdapter.notifyDataSetChanged()
        }

        private fun doMinusOperation() {
            //改变count值
            var count = goodsInfo.count
            if (count == 1) {
                //最后一次点击减号执行动画集
                val hideAnimationSet: AnimationSet = getHideAnimation()
                tvCount.startAnimation(hideAnimationSet)
                btnMinus.startAnimation(hideAnimationSet)
                //删除缓存
                TakeoutApp.sInstance.deleteCacheSelectedInfo(goodsInfo.id)
            }else{
                //更新缓存
                TakeoutApp.sInstance.updateCacheSelectedInfo(goodsInfo.id, Constants.MINUS)
            }
            count--
            //改变数据层
            goodsInfo.count = count
            notifyDataSetChanged()
        }

        private fun doAddOperation() {
            //改变count值
            var count = goodsInfo.count
            if (count == 0) {
                //第一次点击加号执行动画集
                val showAnimationSet: AnimationSet = getShowAnimation()
                tvCount.startAnimation(showAnimationSet)
                btnMinus.startAnimation(showAnimationSet)
                //添加缓存
                TakeoutApp.sInstance.addCacheSelectedInfo(CacheSelectedInfo(goodsInfo.sellerId,goodsInfo.typeId,goodsInfo.id,1))
            }else{
                //更新缓存
                TakeoutApp.sInstance.updateCacheSelectedInfo(goodsInfo.id, Constants.ADD)
            }
            count++
            //改变数据层
            goodsInfo.count = count
            notifyDataSetChanged()

            //抛物线

            //1.克隆+号，并且添加到acitivty上
            var ib = ImageButton(context)
            //大小，位置、背景全部相同
            ib.setBackgroundResource(R.drawable.button_add)
//            btnAdd.width
            val srcLocation = IntArray(2)
            btnAdd.getLocationInWindow(srcLocation)
            Log.e("location", srcLocation[0].toString() + ":" + srcLocation[1])
            ib.x = srcLocation[0].toFloat()
            ib.y = srcLocation[1].toFloat()
            (goodsFragment.activity as BusinessActivity).addImageButton(ib, btnAdd.width, btnAdd.height)
            //2.执行抛物线动画（水平位移，垂直加速位移）
            val destLocation = (goodsFragment.activity as BusinessActivity).getCartLocation()
            val parabolaAnim: AnimationSet = getParabolaAnimation(ib, srcLocation, destLocation)
            ib.startAnimation(parabolaAnim)
            //3.动画完成后回收克隆的+号
        }

        private fun getParabolaAnimation(ib: ImageButton, srcLocation: IntArray, destLocation: IntArray): AnimationSet {
            val parabolaAnim: AnimationSet = AnimationSet(false)
            parabolaAnim.duration = DURATION
            val translateX = TranslateAnimation(
                    Animation.ABSOLUTE, 0f,
                    Animation.ABSOLUTE, destLocation[0].toFloat() - srcLocation[0].toFloat(),
                    Animation.ABSOLUTE, 0.0f,
                    Animation.ABSOLUTE, 0.0f)
            translateX.duration = DURATION
            parabolaAnim.addAnimation(translateX)
            val translateY = TranslateAnimation(
                    Animation.ABSOLUTE, 0F,
                    Animation.ABSOLUTE, 0F,
                    Animation.ABSOLUTE, 0f,
                    Animation.ABSOLUTE, destLocation[1].toFloat() - srcLocation[1].toFloat())
            translateY.setInterpolator(AccelerateInterpolator())
            translateY.duration = DURATION
            parabolaAnim.addAnimation(translateY)
            parabolaAnim.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationEnd(animation: Animation?) {
                    val viewParent = ib.parent
                    if (viewParent != null) {
                        (viewParent as ViewGroup).removeView(ib)
                    }
                }

                override fun onAnimationRepeat(animation: Animation?) {
                }

                override fun onAnimationStart(animation: Animation?) {

                }
            })
            return parabolaAnim
        }

        private fun getHideAnimation(): AnimationSet {
            var animationSet: AnimationSet = AnimationSet(false)
            animationSet.duration = DURATION
            val alphaAnim: Animation = AlphaAnimation(1f, 0.0f)
            alphaAnim.duration = DURATION
            animationSet.addAnimation(alphaAnim)
            val rotateAnim: Animation = RotateAnimation(720.0f, 0.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
            rotateAnim.duration = DURATION
            animationSet.addAnimation(rotateAnim)
            val translateAnim: Animation = TranslateAnimation(
                    Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, 2.0f,
                    Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, 0.0f)
            translateAnim.duration = DURATION
            animationSet.addAnimation(translateAnim)
            return animationSet
        }

        private fun getShowAnimation(): AnimationSet {
            var animationSet: AnimationSet = AnimationSet(false)
            animationSet.duration = DURATION
            val alphaAnim: Animation = AlphaAnimation(0.0f, 1.0f)
            alphaAnim.duration = DURATION
            animationSet.addAnimation(alphaAnim)
            val rotateAnim: Animation = RotateAnimation(0.0f, 720.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
            rotateAnim.duration = DURATION
            animationSet.addAnimation(rotateAnim)
            val translateAnim: Animation = TranslateAnimation(
                    Animation.RELATIVE_TO_SELF, 2.0f,
                    Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, 0.0f)
            translateAnim.duration = DURATION
            animationSet.addAnimation(translateAnim)
            return animationSet
        }

        val ivIcon: ImageView
        val tvName: TextView
        val tvForm: TextView
        val tvMonthSale: TextView
        val tvNewPrice: TextView
        val tvOldPrice: TextView
        val btnAdd: ImageButton
        val btnMinus: ImageButton
        val tvCount: TextView

        init {
            ivIcon = itemView.find(R.id.iv_icon)
            tvName = itemView.find(R.id.tv_name)
            tvForm = itemView.find(R.id.tv_form)
            tvMonthSale = itemView.find(R.id.tv_month_sale)
            tvNewPrice = itemView.find(R.id.tv_newprice)
            tvOldPrice = itemView.find(R.id.tv_oldprice)
            tvCount = itemView.find(R.id.tv_count)
            btnAdd = itemView.find(R.id.ib_add)
            btnMinus = itemView.find(R.id.ib_minus)
            btnAdd.setOnClickListener(this)
            btnMinus.setOnClickListener(this)
        }

        fun bindData(goodsInfo: GoodsInfo) {
            this.goodsInfo = goodsInfo
            Picasso.with(context).load(goodsInfo.icon).into(ivIcon)
            tvName.text = goodsInfo.name
            tvForm.text = goodsInfo.form
            tvMonthSale.text = "月售${goodsInfo.monthSaleNum}份"
            tvNewPrice.text = PriceFormater.format(goodsInfo.newPrice.toFloat())
//            tvNewPrice.text = "$${goodsInfo.newPrice}"
            tvOldPrice.text = "￥${goodsInfo.oldPrice}"
            tvOldPrice.paint.flags = Paint.STRIKE_THRU_TEXT_FLAG
            if (goodsInfo.oldPrice > 0) {
                tvOldPrice.visibility = View.VISIBLE
            } else {
                tvOldPrice.visibility = View.GONE
            }
            tvCount.text = goodsInfo.count.toString()
            if (goodsInfo.count > 0) {
                tvCount.visibility = View.VISIBLE
                btnMinus.visibility = View.VISIBLE
            } else {
                tvCount.visibility = View.INVISIBLE
                btnMinus.visibility = View.INVISIBLE
            }
        }
    }


    var goodsList: List<GoodsInfo> = ArrayList()

    fun setDatas(goodsInfoList: List<GoodsInfo>) {
        this.goodsList = goodsInfoList
        notifyDataSetChanged()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var itemView: View
        val goodsItemHolder: GoodsItemHolder
        if (convertView == null) {
            itemView = LayoutInflater.from(context).inflate(R.layout.item_goods, parent, false)
            goodsItemHolder = GoodsItemHolder(itemView)
            itemView.tag = goodsItemHolder
        } else {
            itemView = convertView
            goodsItemHolder = convertView.tag as GoodsItemHolder
        }
        goodsItemHolder.bindData(goodsList.get(position))
        return itemView
    }

    override fun getItem(position: Int): Any {
        return goodsList.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return goodsList.size
    }


    override fun getHeaderId(position: Int): Long {
        val goodsInfo: GoodsInfo = goodsList.get(position)
        return goodsInfo.typeId.toLong()
    }

    override fun getHeaderView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val goodsInfo: GoodsInfo = goodsList.get(position)
        val typeName = goodsInfo.typeName
        val textView: TextView = LayoutInflater.from(context).inflate(R.layout.item_type_header, parent, false) as TextView
        textView.text = typeName
        textView.setTextColor(Color.BLACK)
        return textView
    }
}