package com.heima.takeout.ui.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.TypedValue
import com.heima.takeout.R
import com.heima.takeout.model.beans.RecepitAddressBean
import com.heima.takeout.model.dao.AddressDao
import com.heima.takeout.ui.adapter.AddressRvAdapter
import com.heima.takeout.ui.views.RecycleViewDivider
import com.heima.takeout.utils.CommonUtil
import kotlinx.android.synthetic.main.activity_address_list.*

class RecepitAddressActivity : AppCompatActivity() {
    lateinit var addressDao:AddressDao
    lateinit var adapter:AddressRvAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address_list)
        addressDao = AddressDao(this)
        rv_receipt_address.layoutManager = LinearLayoutManager(this)
        rv_receipt_address.addItemDecoration(RecycleViewDivider(this,LinearLayoutManager.HORIZONTAL))
        adapter =  AddressRvAdapter(this)
        rv_receipt_address.adapter =adapter
        if (CommonUtil.checkDeviceHasNavigationBar(this)) {
            activity_address_list.setPadding(0, 0, 0, 48.dp2px())
        }
        tv_add_address.setOnClickListener {
            val intent : Intent = Intent(this, AddOrEditAddressActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        val addressList = addressDao.queryAllAddress()
        if(addressList.isNotEmpty()){
//            toast("一共有" + addressList.size + "个地址")
            adapter.setAddList(addressList as ArrayList<RecepitAddressBean>)
        }
    }

    fun Int.dp2px(): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                toFloat(), resources.displayMetrics).toInt()

    }
}