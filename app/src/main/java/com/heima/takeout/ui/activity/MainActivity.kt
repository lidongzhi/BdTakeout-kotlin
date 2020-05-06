package com.heima.takeout.ui.activity

import android.app.Fragment
import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import com.heima.takeout.R
import com.heima.takeout.ui.fragment.HomeFragment
import com.heima.takeout.ui.fragment.MoreFragment
import com.heima.takeout.ui.fragment.OrderFragment
import com.heima.takeout.ui.fragment.UserFragment
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //判断设备是否有虚拟按键，如果有增加paddingBottom=50dp
        if (checkDeviceHasNavigationBar(this)) {
            ll_main_activity.setPadding(0, 0, 0, 50.dp2px())
        }
        initBottomBar()
        changeIndex(0)
    }


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

    val fragments: List<Fragment> = listOf<Fragment>(HomeFragment(), OrderFragment(), UserFragment(), MoreFragment())

    private fun initBottomBar() {
        for (i in 0 until main_bottom_bar.childCount) {
            main_bottom_bar.getChildAt(i).setOnClickListener {
                view ->
                changeIndex(i)
            }
        }
    }

    private fun changeIndex(index: Int) {
        for (i in 0 until main_bottom_bar.childCount) {
            val child = main_bottom_bar.getChildAt(i)
            if (i == index) {
                //选中，禁用效果
                setEnable(child, false)
            } else {
                //没选中的，enable=true
                setEnable(child, true)
            }
        }
        fragmentManager.beginTransaction().replace(R.id.main_content, fragments[index]).commit()
    }

    private fun setEnable(child: View, isEnable: Boolean) {
        child.isEnabled = isEnable
        if (child is ViewGroup) {
            for (i in 0 until child.childCount) {
                child.getChildAt(i).isEnabled = isEnable
            }
        }
    }
}
