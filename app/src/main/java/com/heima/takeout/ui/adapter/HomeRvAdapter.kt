package com.heima.takeout.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import com.daimajia.slider.library.SliderLayout
import com.daimajia.slider.library.SliderTypes.TextSliderView
import com.heima.takeout.R
import com.heima.takeout.model.beans.Seller
import com.heima.takeout.ui.activity.BusinessActivity
import com.heima.takeout.utils.TakeoutApp
import com.squareup.picasso.Picasso
import org.jetbrains.anko.find

/**
 * Created by lidongzhi on 2017/8/28.
 */

class HomeRvAdapter(val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        val TYPE_TITLE = 0
        val TYPE_SELLER = 1
    }

    /**
     * 不同position对应不同类型
     */
    override fun getItemViewType(position: Int): Int {
        if (position == 0) {
            return TYPE_TITLE
        } else {
            return TYPE_SELLER
        }
    }

    var mDatas: ArrayList<Seller> = ArrayList()

    fun setData(data: ArrayList<Seller>) {
        this.mDatas = data
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        val viewType = getItemViewType(position)
        when (viewType) {
            TYPE_TITLE -> (holder as TitleHolder).bindData("我是大哥----------------------------------------")
            TYPE_SELLER -> (holder as SellerHolder).bindData(mDatas[position - 1])
        }

    }

    override fun getItemCount(): Int {
        if (mDatas.size > 0) {
            return mDatas.size + 1
        } else {
            return 0
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            TYPE_TITLE -> return TitleHolder(View.inflate(context, R.layout.item_title, null))
            TYPE_SELLER -> return SellerHolder(View.inflate(context, R.layout.item_seller, null))
            else -> return TitleHolder(View.inflate(context, R.layout.item_home_common, null))
        }
    }

    var url_maps: HashMap<String, String> = HashMap()

    inner class TitleHolder(item: View) : RecyclerView.ViewHolder(item) {
        val slideLayout: SliderLayout

        init {
            slideLayout = item.findViewById(R.id.slider) as SliderLayout
        }

        fun bindData(data: String) {
            if (url_maps.size == 0) {
                url_maps.put("Hannibal", "http://static2.hypable.com/wp-content/uploads/2013/12/hannibal-season-2-release-date.jpg");
                url_maps.put("Big Bang Theory", "http://tvfiles.alphacoders.com/100/hdclearart-10.png");
                url_maps.put("House of Cards", "http://cdn3.nflximg.net/images/3093/2043093.jpg");
                url_maps.put("Game of Thrones", "http://images.boomsbeat.com/data/images/full/19640/game-of-thrones-season-4-jpg.jpg");

                for ((key, value) in url_maps) {
                    var textSlideView: TextSliderView = TextSliderView(context)
                    textSlideView.description(key).image(value)
                    slideLayout.addSlider(textSlideView)
                }
            }
        }
    }

    inner class SellerHolder(item: View) : RecyclerView.ViewHolder(item) {
        val tvTitle: TextView
        val ivLogo: ImageView
        val rbScore: RatingBar
        val tvSale: TextView
        val tvSendPrice: TextView
        val tvDistance: TextView
        lateinit var mSeller: Seller

        init {
            tvTitle = item.find(R.id.tv_title)
            ivLogo = item.find(R.id.seller_logo)
            rbScore = item.find(R.id.ratingBar)

            tvSale = item.find(R.id.tv_home_sale)
            tvSendPrice = item.find(R.id.tv_home_send_price)
            tvDistance = item.find(R.id.tv_home_distance)
            item.setOnClickListener {
                val intent: Intent = Intent(context, BusinessActivity::class.java)
                //去取小明在田老师这家店是否有缓存信息
                //逐层读取，判断整个这家店是否有缓存
                var hasSelectInfo = false
                val count = TakeoutApp.sInstance.queryCacheSelectedInfoBySellerId(mSeller.id.toInt())
                if (count > 0) {
                    hasSelectInfo = true
                }
                intent.putExtra("seller", mSeller)
                intent.putExtra("hasSelectInfo", hasSelectInfo)
                context.startActivity(intent)
            }
        }

        @SuppressLint("SetTextI18n")
        fun bindData(seller: Seller) {
            this.mSeller = seller
            tvTitle.text = seller.name
            //TODO:赋值其他字段
            Picasso.with(context).load(seller.icon).into(ivLogo)
            rbScore.rating = seller.score.toFloat()
            tvSale.text = "月售${seller.sale}单"
            tvSendPrice.text = "￥${seller.sendPrice}起送/配送费￥${seller.deliveryFee}"
            tvDistance.text = seller.distance
        }
    }
}
