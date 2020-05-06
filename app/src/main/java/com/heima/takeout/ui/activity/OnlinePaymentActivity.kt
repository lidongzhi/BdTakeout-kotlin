package com.heima.takeout.ui.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.TypedValue
import android.widget.Toast
import com.alipay.sdk.app.PayTask
import com.heima.takeout.R
import com.heima.takeout.pay.PayResult
import com.heima.takeout.pay.SignUtils
import com.heima.takeout.utils.CommonUtil
import kotlinx.android.synthetic.main.activity_online_payment.*
import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.*

class OnlinePaymentActivity : AppCompatActivity() {
    companion object {
        val SDK_PAY_FLAG: Int = 1000
    }

    private val mHandler = object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                SDK_PAY_FLAG -> {
                    val payResult = PayResult(msg.obj as String)

                    // 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
                    val resultInfo = payResult.getResult()

                    val resultStatus = payResult.getResultStatus()

                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {
                        Toast.makeText(this@OnlinePaymentActivity, "支付成功",
                                Toast.LENGTH_SHORT).show()
                        finish()
                        //跳转到订单页面
//                        val intent : Intent = Intent(this@OnlinePaymentActivity,OrderDetailActivity::class.java)
                    } else {
                        // 判断resultStatus 为非“9000”则代表可能支付失败
                        // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            Toast.makeText(this@OnlinePaymentActivity, "支付结果确认中",
                                    Toast.LENGTH_SHORT).show()

                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            Toast.makeText(this@OnlinePaymentActivity, "支付失败",
                                    Toast.LENGTH_SHORT).show()

                        }
                    }
                }
                else -> {
                }
            }
        }
    }

    //商户PID
    val PARTNER = "2088011085074233"
    //商户收款账号
    val SELLER = "917356107@qq.com"
    //商户私钥，pkcs8格式
    val RSA_PRIVATE = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAL51jaxQhxW9PnWpW+nz6yJ76tp9eGFXmfGnuxMK+Pmx/qavdsewXOLBfI2OSCR39TzxwMYvCmUrnrt0fVSa7mblbNos2FnMM9ijnx8bsAAhm+i7BKhuaHMunJKH69L+D753zH3P1YIh0ly5DnAr3WPqHydp384qBvb8NS9Tay0HAgMBAAECgYB82PIVknP6fCMFXg8yPQJViIVa1ASlSpdPIXQv93FdvKABA+QI4kMBIXRUFoCT506KtK55OzzFNOLIXoQJgcXj69z0l6pmjJJgXMaBW/9rOzelot13CiGatrIrGngEZO+bCBTud/jQA598zjZ1g182tT+FLDL7GIftW2hC8GqtAQJBAN+XrYsyfL+uSmLdAVEz1vzziU1naGr10Msm1jMnnO/JYdB+84j7FSHxsQ4YOgsmeN5YVsJcVfc/CReOxknns38CQQDaEHnVPDt+Z7sqT7bN0UKh0/CrqkDTiIjhz1lJyIIoqVRoeJjJn1wlEKBV5R9gkTJutQTVU19XFtblMEnOy6p5AkEAw170rEmMSa0QoHw+d2bVtydR1QnDapqqO6kOx5oYfkm4J4eWYx4J5CQdMpSmuzF9scL85E3sa+NvnV8LEm7cHwJALtXzFPWG4bNt47yTSslzQka/Hl/G5Kginj1mtA44xnr4AihEyKlNpThY95nqj1cgOd7vVtI9W/sv1LH2aFAeIQJBAIqXbMc6xGVfuiFAJKtg+AFNMBP0UOEgMEoKo4RPFp21nBhFgL9/WYM4ZjyHUdr45rCySAqQovw4DCHLfQZC23I="
    //支付宝公钥
    val RSA_PUBLIC = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC+dY2sUIcVvT51qVvp8+sie+rafXhhV5nxp7sTCvj5sf6mr3bHsFziwXyNjkgkd/U88cDGLwplK567dH1Umu5m5WzaLNhZzDPYo58fG7AAIZvouwSobmhzLpySh+vS/g++d8x9z9WCIdJcuQ5wK91j6h8nad/OKgb2/DUvU2stBwIDAQAB"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_online_payment)
        if (CommonUtil.checkDeviceHasNavigationBar(this)) {
            activity_online_pay.setPadding(0, 0, 0, 48.dp2px())
        }
        bt_confirm_pay.setOnClickListener {
            //开始支付
            pay()
        }
    }

    fun getOutTradeNo(): String {
        val format = SimpleDateFormat("MMddHHmmss",
                Locale.getDefault())
        val date = Date()
        var key = format.format(date)

        val r = Random()
        key = key + r.nextInt()
        key = key.substring(0, 15)
        return key
    }

    fun getOrderInfo(subject: String, body: String, price: String): String {
        // 签约合作者身份ID
        var orderInfo = "partner=" + "\"" + PARTNER + "\""

        // 签约卖家支付宝账号
        orderInfo += "&seller_id=" + "\"" + SELLER + "\""

        // 商户网站唯一订单号
        orderInfo += "&out_trade_no=" + "\"" + getOutTradeNo() + "\""

        // 商品名称
        orderInfo += "&subject=" + "\"" + subject + "\""

        // 商品详情
        orderInfo += "&body=" + "\"" + body + "\""

        // 商品金额
        orderInfo += "&total_fee=" + "\"" + price + "\""

        // 服务器异步通知页面路径
        orderInfo += "&notify_url=" + "\"" + "http://notify.msp.hk/notify.htm" + "\""

        // 服务接口名称， 固定值
        orderInfo += "&service=\"mobile.securitypay.pay\""

        // 支付类型， 固定值
        orderInfo += "&payment_type=\"1\""

        // 参数编码， 固定值
        orderInfo += "&_input_charset=\"utf-8\""

        // 设置未付款交易的超时时间
        // 默认30分钟，一旦超时，该笔交易就会自动被关闭。
        // 取值范围：1m～15d。
        // m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
        // 该参数数值不接受小数点，如1.5h，可转换为90m。
        orderInfo += "&it_b_pay=\"30m\""

        // extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
        // orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

        // 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
        orderInfo += "&return_url=\"m.alipay.com\""

        // 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
        // orderInfo += "&paymethod=\"expressGateway\"";

        return orderInfo
    }

    fun pay() {
        // 订单
        val orderInfo = getOrderInfo("附近第0家分店", "一共点了2个快餐", "0.01")

        // 对订单做RSA 签名
        var sign = sign(orderInfo)
        try {
            // 仅需对sign 做URL编码
            sign = URLEncoder.encode(sign, "UTF-8")
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }

        // 完整的符合支付宝参数规范的订单信息
        val payInfo = orderInfo + "&sign=\"" + sign + "\"&" + getSignType()

        val payRunnable = Runnable {
            // 构造PayTask 对象
            val alipay = PayTask(this@OnlinePaymentActivity)
            // 调用支付接口，获取支付结果
            val result = alipay.pay(payInfo)

            val msg = Message()
            msg.what = SDK_PAY_FLAG
            msg.obj = result
            mHandler.sendMessage(msg)
        }

        // 必须异步调用
        val payThread = Thread(payRunnable)
        payThread.start()
    }

    fun Int.dp2px(): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                toFloat(), resources.displayMetrics).toInt()

    }

    fun sign(content: String): String {
        return SignUtils.sign(content, RSA_PRIVATE)
    }

    /**
     * get the sign type we use. 获取签名方式

     */
    fun getSignType(): String {
        return "sign_type=\"RSA\""
    }
}