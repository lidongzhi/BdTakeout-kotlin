package com.heima.takeout.presenter

import android.util.Log
import com.google.gson.Gson
import com.heima.takeout.model.beans.User
import com.heima.takeout.model.dao.TakeoutOpenHelper
import com.heima.takeout.ui.activity.LoginActivity
import com.heima.takeout.utils.TakeoutApp
import com.j256.ormlite.android.AndroidDatabaseConnection
import com.j256.ormlite.dao.Dao
import java.sql.Savepoint

/**
 * Created by lidongzhi on 2017/9/1.
 */
class LoginActivityPresenter(val loginActivity: LoginActivity) : NetPresenter() {

    override fun parserJson(json: String) {
        val user = Gson().fromJson(json, User::class.java)

        if (user != null) {
            //缓存到内存中
            TakeoutApp.sUser = user
            var connection: AndroidDatabaseConnection? = null
            var startPoint: Savepoint? = null
            try {
                //缓存到本地数据库中，SqLiteOpenhelper,要使用SQL语句
                //第三方ORM框架(ormlite,greendao)，直接操纵javabean,
                val takeoutOpenHelper = TakeoutOpenHelper(loginActivity)
                val userDao: Dao<User, Int> = takeoutOpenHelper.getDao(User::class.java)
                //保证如果已有此用户，就不需再创建了
//            userDao.create(user)
//            userDao.createOrUpdate(user)
//            Log.e("login", "创建新用户或者更新老用户信息")
                connection = AndroidDatabaseConnection(takeoutOpenHelper.writableDatabase, true)
                startPoint = connection.setSavePoint("start")
                connection.isAutoCommit = false //取消自动提交
                //TODO:区分新用户和老用户,并且统计比率
                //step1:查找以前是否有这个用户记录
                val userList: List<User> = userDao.queryForAll()
                var isOldUser: Boolean = false
                for (i in 0 until userList.size) {
                    val u = userList.get(i)
                    if (u.id == user.id) {
                        isOldUser = true
                    }
                }
                //step2：更新或者创建
                if (isOldUser) {
                    //TecentStasticSdk.userAction(-1)
                    userDao.update(user)
                    Log.e("login", "老用户更新信息")
                } else {
                    //TecentStasticSdk.userAction(0)
                    userDao.create(user)
                    Log.e("login", "新用户登录")
                }
                connection.commit(startPoint)
                Log.e("login", "事务正常")
                loginActivity.onLoginSuccess()
            } catch (e: Exception) {
                Log.e("login", "出现ormlite事务异常," + e.localizedMessage)
                loginActivity.onLoginFailed()
                if (connection != null) {
                    connection.rollback(startPoint)
                }
            }
        } else {
            loginActivity.onLoginFailed()
        }
    }

    //使用手机号码登录的业务
    fun loginByPhone(phone: String) {
        val loginCall = takeoutService.loginByPhone(phone)
        loginCall.enqueue(callback)
    }
}