package com.heima.takeout.ui.fragment

import android.app.Fragment
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

/**
 * Created by lidongzhi on 2017/9/25.
 */
class CommentsFragment: Fragment() {
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val commentsView = TextView(activity)
        commentsView.text = "评论"
        commentsView.gravity = Gravity.CENTER
        commentsView.setTextColor(Color.BLACK)
        return commentsView
    }
}