package com.heima.takeout.ui.activity

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.TypedValue
import android.view.View
import android.widget.Toast
import com.heima.takeout.R
import com.heima.takeout.model.beans.RecepitAddressBean
import com.heima.takeout.model.dao.AddressDao
import com.heima.takeout.utils.CommonUtil
import kotlinx.android.synthetic.main.activity_add_edit_receipt_address.*
import org.jetbrains.anko.toast


class AddOrEditAddressActivity : AppCompatActivity(), View.OnClickListener {
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ib_back -> finish()
            R.id.ib_add_phone_other -> rl_phone_other.visibility = View.VISIBLE
            R.id.ib_delete_phone -> et_phone.setText("")
            R.id.ib_delete_phone_other -> et_phone_other.setText("")
            R.id.ib_select_label -> selectLabel()
            R.id.btn_ok -> {
                val isOk = checkReceiptAddressInfo()
                if (isOk) {
                    if (intent.hasExtra("addressBean")) {
                        updateAddress()
                    }else {
                        //新增地址
                        insertAddress()
                    }
                }
            }
            R.id.btn_location_address -> {
                val intent = Intent(this, MapLocationActivity::class.java)
                        startActivityForResult(intent, 1001)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == 200){
            if(data!=null) {
                val title = data.getStringExtra("title")
                val address = data.getStringExtra("address")
                et_receipt_address.setText(title)
                et_detail_address.setText(address)
            }
        }
    }

    private fun updateAddress() {
        var username = et_name.text.toString().trim()
        var sex = "女士"
        if (rb_man.isChecked) {
            sex = "先生"
        }
        var phone = et_phone.text.toString().trim()
        var phoneOther = et_phone_other.text.toString().trim()
        var address = et_receipt_address.text.toString().trim()
        var detailAddress = et_detail_address.text.toString().trim()
        var label = tv_label.text.toString()
        addressBean.username = username
        addressBean.sex = sex
        addressBean.phone = phone
        addressBean.phoneOther = phoneOther
        addressBean.address = address
        addressBean.detailAddress = detailAddress
        addressBean.label = label
        addressDao.updateRecepitAddressBean(addressBean)
        toast("更新地址成功")
        finish()
    }

    private fun insertAddress() {
        var username = et_name.text.toString().trim()
        var sex = "女士"
        if (rb_man.isChecked) {
            sex = "先生"
        }
        var phone = et_phone.text.toString().trim()
        var phoneOther = et_phone_other.text.toString().trim()
        var address = et_receipt_address.text.toString().trim()
        var detailAddress = et_detail_address.text.toString().trim()
        var label = tv_label.text.toString()
        addressDao.addRecepitAddressBean(RecepitAddressBean(999, username, sex, phone, phoneOther, address, detailAddress, label, "38"))
        toast("新增地址成功")
        finish()
    }

    val titles = arrayOf("无", "家", "学校", "公司")
    val colors = arrayOf("#778899", "#ff3399", "#ff9933", "#33ff99")
    lateinit var addressDao: AddressDao
    private fun selectLabel() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("请选择地址标签")
        builder.setItems(titles, object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface?, which: Int) {
                tv_label.text = titles[which].toString()
                tv_label.setBackgroundColor(Color.parseColor(colors[which]))
                tv_label.setTextColor(Color.BLACK)
            }
        })
        builder.show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_receipt_address)
        processIntent()
        addressDao = AddressDao(this)
        if (CommonUtil.checkDeviceHasNavigationBar(this)) {
            activity_add_address.setPadding(0, 0, 0, 48.dp2px())
        }
        btn_location_address.setOnClickListener(this)
        ib_back.setOnClickListener(this)
        ib_add_phone_other.setOnClickListener(this)
        ib_delete_phone.setOnClickListener(this)
        ib_delete_phone_other.setOnClickListener(this)
        ib_select_label.setOnClickListener(this)
        btn_ok.setOnClickListener(this)
        et_phone.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (!TextUtils.isEmpty(s)) {
                    ib_delete_phone.visibility = View.VISIBLE
                } else {
                    ib_delete_phone.visibility = View.INVISIBLE
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })
        et_phone_other.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (!TextUtils.isEmpty(s)) {
                    ib_delete_phone_other.visibility = View.VISIBLE
                } else {
                    ib_delete_phone_other.visibility = View.INVISIBLE
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })
    }

    lateinit var addressBean: RecepitAddressBean
    private fun processIntent() {
        if (intent.hasExtra("addressBean")) {
            addressBean = intent.getSerializableExtra("addressBean") as RecepitAddressBean
            tv_title.text = "修改地址"
            ib_delete.visibility = View.VISIBLE
            ib_delete.setOnClickListener {
                addressDao.deleteRecepitAddressBean(addressBean)
                toast("删除此地址成功")
                finish()
            }
            et_name.setText(addressBean.username)
            val sex = addressBean.sex
            if ("先生".equals(sex)) {
                rb_man.isChecked = true
            } else {
                rb_women.isChecked = true
            }
            et_phone.setText(addressBean.phone)
            et_phone_other.setText(addressBean.phoneOther)
            et_receipt_address.setText(addressBean.address)
            et_detail_address.setText(addressBean.detailAddress)
            tv_label.text = addressBean.label
        }
    }

    fun Int.dp2px(): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                toFloat(), resources.displayMetrics).toInt()

    }

    fun checkReceiptAddressInfo(): Boolean {
        val name = et_name.getText().toString().trim()
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "请填写联系人", Toast.LENGTH_SHORT).show()
            return false
        }
        val phone = et_phone.getText().toString().trim()
        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "请填写手机号码", Toast.LENGTH_SHORT).show()
            return false
        }
        if (!isMobileNO(phone)) {
            Toast.makeText(this, "请填写合法的手机号", Toast.LENGTH_SHORT).show()
            return false
        }
        val receiptAddress = et_receipt_address.getText().toString().trim()
        if (TextUtils.isEmpty(receiptAddress)) {
            Toast.makeText(this, "请填写收获地址", Toast.LENGTH_SHORT).show()
            return false
        }
        val address = et_detail_address.getText().toString().trim()
        if (TextUtils.isEmpty(address)) {
            Toast.makeText(this, "请填写详细地址", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    fun isMobileNO(phone: String): Boolean {
        val telRegex = "[1][358]\\d{9}"//"[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        return phone.matches(telRegex.toRegex())
    }
}