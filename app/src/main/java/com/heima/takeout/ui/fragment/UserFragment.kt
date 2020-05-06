package com.heima.takeout.ui.fragment

import android.annotation.SuppressLint
import android.app.Fragment
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.heima.takeout.R
import com.heima.takeout.ui.activity.LoginActivity
import com.heima.takeout.utils.TakeoutApp
import org.jetbrains.anko.find

/**
 * Created by lidongzhi on 2017/8/28.
 */

class UserFragment : Fragment() {
    lateinit var ll_userinfo:LinearLayout
    lateinit var username:TextView
    lateinit var phone:TextView
    lateinit var ivLogin:ImageView
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val userview = View.inflate(activity, R.layout.fragment_user, null)
        ll_userinfo = userview.find<LinearLayout>(R.id.ll_userinfo)
        username = userview.find<TextView>(R.id.username)
        phone = userview.find<TextView>(R.id.phone)
        ivLogin = userview.find<ImageView>(R.id.login)
        ivLogin.setOnClickListener {
            val intent = Intent(activity, LoginActivity::class.java)
            activity.startActivity(intent)
        }
        return userview
    }

    @SuppressLint("SetTextI18n")
    override fun onStart() {
        super.onStart()
        //展示登录成功后的ui效果
        val user = TakeoutApp.sUser
        if (user.id == -1) {
            //未登录
            ll_userinfo.visibility = View.GONE
            ivLogin.visibility = View.VISIBLE
        } else {
            ivLogin.visibility = View.GONE
            ll_userinfo.visibility = View.VISIBLE
            username.text = "欢迎您，${user.name}"
            phone.text = user.phone
        }
    }
}
