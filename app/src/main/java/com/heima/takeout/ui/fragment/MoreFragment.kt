package com.heima.takeout.ui.fragment

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.heima.takeout.R

/**
 * Created by lidongzhi on 2017/8/28.
 */

class MoreFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = View.inflate(activity, R.layout.fragment_, null)
        (view as TextView).setText("更多")
        return view
    }
}
