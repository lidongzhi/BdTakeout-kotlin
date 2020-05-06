package com.heima.takeout.model.dao

import android.content.Context
import android.util.Log
import com.heima.takeout.model.beans.RecepitAddressBean
import com.j256.ormlite.dao.Dao

class AddressDao(val context: Context) {
    lateinit var addressDao: Dao<RecepitAddressBean, Int>

    init {
        val openHelper = TakeoutOpenHelper(context)
        addressDao = openHelper.getDao(RecepitAddressBean::class.java)
    }

    fun addRecepitAddressBean(bean: RecepitAddressBean){
        try{
            addressDao.create(bean)
        }catch (e : Exception){
            Log.e("addressBean", e.localizedMessage)

        }
    }

    fun deleteRecepitAddressBean(bean: RecepitAddressBean){
        try{
            addressDao.delete(bean)
        }catch (e : Exception){
            Log.e("addressBean", e.localizedMessage)

        }
    }

    fun updateRecepitAddressBean(bean: RecepitAddressBean){
        try{
            addressDao.update(bean)
        }catch (e : Exception){
            Log.e("addressBean", e.localizedMessage)

        }
    }

    fun queryAllAddress():List<RecepitAddressBean>{
        try{
           return addressDao.queryForAll()
        }catch (e : Exception){
            Log.e("addressBean", e.localizedMessage)
            return ArrayList<RecepitAddressBean>()
        }
    }
}