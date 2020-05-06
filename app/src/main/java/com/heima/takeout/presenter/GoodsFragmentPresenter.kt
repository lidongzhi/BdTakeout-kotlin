package com.heima.takeout.presenter

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.heima.takeout.model.beans.GoodsInfo
import com.heima.takeout.model.beans.GoodsTypeInfo
import com.heima.takeout.ui.activity.BusinessActivity
import com.heima.takeout.ui.fragment.GoodsFragment
import com.heima.takeout.utils.TakeoutApp
import org.json.JSONObject

class GoodsFragmentPresenter(val goodsFragment: GoodsFragment) : NetPresenter() {
    val allTypeGoodsList : ArrayList<GoodsInfo> = arrayListOf()
    var goodstypeList: List<GoodsTypeInfo> = arrayListOf()
    //连接服务器拿到此商家所有商品
    fun getBusinessInfo(sellerId: String) {
        val businessCall = takeoutService.getBusinessInfo(sellerId)
        businessCall.enqueue(callback)
    }

    override fun parserJson(json: String) {
        val gson = Gson()
        val jsoObj = JSONObject(json)
        val allStr = jsoObj.getString("list")
        val hasSelectInfo = (goodsFragment.activity as BusinessActivity).hasSelectInfo //是否有点餐记录
        //List<GoodsTypeInfo>
        goodstypeList = gson.fromJson(allStr, object : TypeToken<List<GoodsTypeInfo>>() {
        }.type)
        Log.e("business", "该商家一共有" + goodstypeList.size + "个类别商品")
        for( i in 0 until  goodstypeList.size){
            val goodsTypeInfo = goodstypeList.get(i)
            var aTypeCount = 0
            if(hasSelectInfo){
                aTypeCount = TakeoutApp.sInstance.queryCacheSelectedInfoByTypeId(goodsTypeInfo.id)
                goodsTypeInfo.redDotCount = aTypeCount  //一个类别的记录
            }
            val aTypeList:List<GoodsInfo> = goodsTypeInfo.list
            for(j in 0 until  aTypeList.size){
                val goodsInfo = aTypeList.get(j)
                if(aTypeCount > 0){
                    val count = TakeoutApp.sInstance.queryCacheSelectedInfoByGoodsId(goodsInfo.id)
                    goodsInfo.count = count  //具体商品的记录个数
                }
                //建立双向绑定关系
                goodsInfo.typeName = goodsTypeInfo.name
                goodsInfo.typeId = goodsTypeInfo.id
            }
            allTypeGoodsList.addAll(aTypeList)
        }
        //更新购物车ui
        (goodsFragment.activity as BusinessActivity).updateCartUi()
        goodsFragment.onLoadBusinessSuccess(goodstypeList, allTypeGoodsList)
    }

    //根据商品类别id找到此类别第一个商品的位置
    fun getGoodsPositionByTypeId(typeId: Int): Int {
        var position = -1 //-1表示未找到
        for(j in 0 until  allTypeGoodsList.size){
            val goodsInfo = allTypeGoodsList.get(j)
            if(goodsInfo.typeId == typeId){
                position = j
                break;
            }
        }
        return position
    }

    //根据类别id找到其在左侧列表中的position
    fun getTypePositionByTypeId(newTypeId: Int):Int {
        var position = -1 //-1表示未找到
        for(i in 0 until  goodstypeList.size){
            val goodsTypeInfo = goodstypeList.get(i)
            if(goodsTypeInfo.id == newTypeId){
                position = i
                break;
            }
        }
        return position
    }

    fun getCartList() : ArrayList<GoodsInfo> {
        val cartList = arrayListOf<GoodsInfo>()
        //count >0的为购物车商品
        for(j in 0 until  allTypeGoodsList.size){
            val goodsInfo = allTypeGoodsList.get(j)
            if(goodsInfo.count>0){
                cartList.add(goodsInfo)
            }
        }
        return cartList
    }

    fun clearCart() {
        val cartList = getCartList()
        for(j in 0 until  cartList.size) {
            val goodsInfo = cartList.get(j)
            goodsInfo.count = 0
        }
    }

}