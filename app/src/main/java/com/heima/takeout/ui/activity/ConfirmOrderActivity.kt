package com.heima.takeout.ui.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.TypedValue
import com.heima.takeout.R
import com.heima.takeout.model.beans.RecepitAddressBean
import com.heima.takeout.utils.CommonUtil
import kotlinx.android.synthetic.main.activity_confirm_order.*

class ConfirmOrderActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm_order)
        if (CommonUtil.checkDeviceHasNavigationBar(this)) {
            activity_confirm_order.setPadding(0, 0, 0, 48.dp2px())
        }
        rl_location.setOnClickListener {
            val intent = Intent(this, RecepitAddressActivity::class.java)
            startActivityForResult(intent, 1002)
        }
        tvSubmit.setOnClickListener {
            val intent = Intent(this, OnlinePaymentActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == 200){
            if(data!=null) {
                val address :RecepitAddressBean = data.getSerializableExtra("address") as RecepitAddressBean
                tv_name.text = address.username
                //TODO:其他字段类似赋值
            }
        }
    }

    fun Int.dp2px(): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                toFloat(), resources.displayMetrics).toInt()

    }

}