package com.heima.takeout.model.dao

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.heima.takeout.model.beans.RecepitAddressBean
import com.heima.takeout.model.beans.User
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper
import com.j256.ormlite.support.ConnectionSource
import com.j256.ormlite.table.TableUtils

/**
 * app版本    数据库版本
 * 1.1版本       1         用户登录
 * 1.3版本       2         地址管理
 */
class TakeoutOpenHelper(val context: Context) : OrmLiteSqliteOpenHelper(context, "takeout_kotlin.db", null, 2) {

    override fun onCreate(db: SQLiteDatabase?, connectionSource: ConnectionSource?) {
        //创建user表
        TableUtils.createTable(connectionSource, User::class.java)
        //创建地址表
        TableUtils.createTable(connectionSource, RecepitAddressBean::class.java)
    }

    override fun onUpgrade(db: SQLiteDatabase?, connectionSource: ConnectionSource?, oldversion: Int, newversion: Int) {
        //升级app的用户会执行此方法
        TableUtils.createTable(connectionSource, RecepitAddressBean::class.java)
    }


}