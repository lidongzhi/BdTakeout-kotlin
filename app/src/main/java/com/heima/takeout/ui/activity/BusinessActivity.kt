package com.heima.takeout.ui.activity

import android.app.AlertDialog
import android.app.Fragment
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v13.app.FragmentPagerAdapter
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import com.heima.takeout.R
import com.heima.takeout.model.beans.Seller
import com.heima.takeout.ui.adapter.CartRvAdapter
import com.heima.takeout.ui.fragment.CommentsFragment
import com.heima.takeout.ui.fragment.GoodsFragment
import com.heima.takeout.ui.fragment.SellerFragment
import com.heima.takeout.utils.PriceFormater
import com.heima.takeout.utils.TakeoutApp
import kotlinx.android.synthetic.main.activity_business.*


class BusinessActivity : AppCompatActivity(), View.OnClickListener {
    var bottomSheetView: View? = null
    lateinit var rvCart: RecyclerView
    lateinit var cartAdapter: CartRvAdapter
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.bottom -> showOrHideCart()
            R.id.tvSubmit -> {
                val intent : Intent = Intent(this, ConfirmOrderActivity::class.java)
                startActivity(intent)
            }
        }
    }

    fun showOrHideCart() {
        if (bottomSheetView == null) {
            //加载要显示的布局
            bottomSheetView = LayoutInflater.from(this).inflate(R.layout.cart_list, window.decorView as ViewGroup, false)
            rvCart = bottomSheetView!!.findViewById(R.id.rvCart) as RecyclerView
            rvCart.layoutManager = LinearLayoutManager(this)
            cartAdapter = CartRvAdapter(this)
            rvCart.adapter = cartAdapter
            val tvClear: TextView = bottomSheetView!!.findViewById(R.id.tvClear) as TextView
            tvClear.setOnClickListener {
                var builder = AlertDialog.Builder(this)
                builder.setTitle("确认都不吃了么？")
                builder.setPositiveButton("是，我要减肥", object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        //开始清空购物车,把购物车中商品的数量重置为0
                        val goodsFragment: GoodsFragment = fragments.get(0) as GoodsFragment
                        goodsFragment.goodsFragmentPresenter.clearCart()
                        cartAdapter.notifyDataSetChanged()
                        //关闭购物车
                        showOrHideCart()
                        //刷新右侧
                        goodsFragment.goodsAdapter.notifyDataSetChanged()
                        //清空所有红点
                        clearRedDot()
                        goodsFragment.goodsTypeAdapter.notifyDataSetChanged()
                        //更新下方购物篮
                        updateCartUi()
                        //清空缓存
                        TakeoutApp.sInstance.clearCacheSelectedInfo(seller.id.toInt())
                    }


                })
                builder.setNegativeButton("不，我还要吃", object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {

                    }
                })
                builder.show()
            }
        }
        //判断BottomSheetLayout内容是否显示
        if (bottomSheetLayout.isSheetShowing) {
            //关闭内容显示
            bottomSheetLayout.dismissSheet()
        } else {
            //显示BottomSheetLayout里面的内容
            val goodsFragment: GoodsFragment = fragments.get(0) as GoodsFragment
            val cartList = goodsFragment.goodsFragmentPresenter.getCartList()
            cartAdapter.setCart(cartList)
            if (cartList.size > 0) {
                bottomSheetLayout.showWithSheetView(bottomSheetView)
            }
        }
    }

    private fun clearRedDot() {
        val goodsFragment: GoodsFragment = fragments.get(0) as GoodsFragment
        val goodstypeList = goodsFragment.goodsFragmentPresenter.goodstypeList
        for (i in 0 until goodstypeList.size) {
            val goodsTypeInfo = goodstypeList.get(i)
            goodsTypeInfo.redDotCount = 0
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_business)
        processIntent()
        if (checkDeviceHasNavigationBar(this)) {
            fl_Container.setPadding(0, 0, 0, 48.dp2px())
        }

        vp.adapter = BusinessFragmentPagerAdapter()
        tabs.setupWithViewPager(vp)
        bottom.setOnClickListener(this)
    }

    var hasSelectInfo = false
    lateinit var seller: Seller
    private fun processIntent() {
        if (intent.hasExtra("hasSelectInfo")) {
            hasSelectInfo = intent.getBooleanExtra("hasSelectInfo", false)
            seller = intent.getSerializableExtra("seller") as Seller
            tvDeliveryFee.text = "另需配送费" + PriceFormater.format(seller.deliveryFee.toFloat())
            tvSendPrice.text = PriceFormater.format(seller.sendPrice.toFloat()) + "起送"
        }
    }

    val fragments = listOf<Fragment>(GoodsFragment(), SellerFragment(), CommentsFragment())

    /**
     * 把转化功能添加到Int类中作为扩展函数
     */
    fun Int.dp2px(): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                toFloat(), resources.displayMetrics).toInt()

    }


    //获取是否存在NavigationBar
    fun checkDeviceHasNavigationBar(context: Context): Boolean {
        var hasNavigationBar = false
        val rs = context.getResources()
        val id = rs.getIdentifier("config_showNavigationBar", "bool", "android")
        if (id > 0) {
            hasNavigationBar = rs.getBoolean(id)
        }
        try {
            val systemPropertiesClass = Class.forName("android.os.SystemProperties")
            val m = systemPropertiesClass.getMethod("get", String::class.java)
            val navBarOverride = m.invoke(systemPropertiesClass, "qemu.hw.mainkeys") as String
            if ("1" == navBarOverride) {
                hasNavigationBar = false
            } else if ("0" == navBarOverride) {
                hasNavigationBar = true
            }
        } catch (e: Exception) {

        }

        return hasNavigationBar

    }

    val titles = listOf<String>("商品", "商家", "评论")

    inner class BusinessFragmentPagerAdapter : FragmentPagerAdapter(fragmentManager) {

        override fun getPageTitle(position: Int): CharSequence {
            return titles.get(position)
        }

        override fun getItem(position: Int): Fragment {
            return fragments.get(position)
        }

        override fun getCount(): Int {
            return titles.size
        }

    }

    fun addImageButton(ib: ImageButton, width: Int, height: Int) {
        fl_Container.addView(ib, width, height)
    }

    fun getCartLocation(): IntArray {
        val destLocation = IntArray(2)
        imgCart.getLocationInWindow(destLocation)
        return destLocation
    }

    fun updateCartUi() {
        //更新数量，更新总价
        var count = 0
        var countPrice = 0.0f
        //哪些商品属于购物车？
        val goodsFragment: GoodsFragment = fragments.get(0) as GoodsFragment
        val cartList = goodsFragment.goodsFragmentPresenter.getCartList()
        for (i in 0 until cartList.size) {
            val goodsInfo = cartList.get(i)
            count += goodsInfo.count
            countPrice += goodsInfo.count * goodsInfo.newPrice.toFloat()
        }
        tvSelectNum.text = count.toString()
        if (count > 0) {
            tvSelectNum.visibility = View.VISIBLE
        } else {
            tvSelectNum.visibility = View.GONE
        }
        tvCountPrice.text = PriceFormater.format(countPrice)
        if (countPrice >= seller.sendPrice.toFloat()) {
            tvSubmit.visibility = View.VISIBLE
            tvSendPrice.visibility = View.GONE
        }else{
            tvSubmit.visibility = View.GONE
            tvSendPrice.visibility = View.VISIBLE
        }
    }
}


