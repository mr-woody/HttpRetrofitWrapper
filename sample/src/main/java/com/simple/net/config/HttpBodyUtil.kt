package com.simple.net.config

import android.content.Context
import android.os.Build
import android.util.Log
import com.simple.net.constant.NetConstant
import com.simple.utils.ConnectManager
import com.simple.utils.Device

object HttpBodyUtil {

    fun create(context: Context): HashMap<String, Any?> {
        val bodyMap = HashMap<String,Any?>()

        bodyMap[NetConstant.JsonConstants.JSON_VS] = Device.getVersionName(context)
        bodyMap[NetConstant.JsonConstants.JSON_VC] = Device.getVersionCode(context)
        bodyMap[NetConstant.JsonConstants.JSON_UA] = Build.DISPLAY
        bodyMap[NetConstant.JsonConstants.JSON_OS] = Build.MODEL
        bodyMap[NetConstant.JsonConstants.JSON_SW] = Device.getScreenWidth(context).toString()
        bodyMap[NetConstant.JsonConstants.JSON_SH] = Device.getScreenHeight(context).toString()

        bodyMap[NetConstant.JsonConstants.JSON_ICCID] = Device.getICCID(context)

        val serial = if (Build.SERIAL == null) "" else Build.SERIAL
        bodyMap[NetConstant.JsonConstants.JSON_SERIAL] = serial
        bodyMap[NetConstant.JsonConstants.JSON_CHANNEL] = "pad"
        bodyMap[NetConstant.JsonConstants.JSON_UDID] = Device.getDeviceID(context,"xxxx")
        bodyMap[NetConstant.JsonConstants.JSON_SCREEN_PATTERN] =
            Device.screenPattern

        bodyMap[NetConstant.JsonConstants.JSON_IMEI] = Device.getICCID(context)
        var mac = ""
        try {
            mac = Device.getLocalMacAddress(context)
        }catch (e:Exception){
            e.printStackTrace()
            Log.d("HttpCommonArgsUtil","获取mac地址失败")
        }
        bodyMap[NetConstant.JsonConstants.JSON_MAC] = mac

        bodyMap[NetConstant.JsonConstants.JSON_CONTYPE] = Integer.valueOf(ConnectManager.getConnectionString(context))
        return bodyMap
    }
}
