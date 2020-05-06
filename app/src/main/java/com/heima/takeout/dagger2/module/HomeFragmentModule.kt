package com.heima.takeout.dagger2.module

import com.heima.takeout.presenter.HomeFragmentPresenter
import com.heima.takeout.ui.fragment.HomeFragment
import dagger.Module
import dagger.Provides

/**
 * Created by lidongzhi on 2017/8/30.
 */
@Module class HomeFragmentModule(val homeFragment: HomeFragment){

    @Provides fun provideHomeFragmentPresenter(): HomeFragmentPresenter{
        return HomeFragmentPresenter(homeFragment)
    }
}