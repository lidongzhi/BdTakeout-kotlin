package com.heima.takeout.dagger2.component

import com.heima.takeout.dagger2.module.HomeFragmentModule
import com.heima.takeout.ui.fragment.HomeFragment
import dagger.Component

/**
 * Created by lidongzhi on 2017/8/30.
 */
@Component(modules = arrayOf(HomeFragmentModule::class)) interface HomeFragmentComponent {

    fun inject(homeFragment: HomeFragment)
}