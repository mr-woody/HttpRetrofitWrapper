package com.simple.net.config


import android.content.Context
import android.os.Build
import com.simple.utils.Device
import com.simple.utils.PackageUtils
import java.util.*

/**
 * 公共请求头实例
 */
object HttpHeadUtil {

    const val HEAD_TYPE = "type"
    const val HEAD_TYPE_FILE = "file"

    fun fileHeadMap(): HashMap<String, String?> {
        val imgHeadMap = HashMap<String, String?>()
        imgHeadMap[HEAD_TYPE] = HEAD_TYPE_FILE
        return imgHeadMap
    }

    fun fileHeadMap(context: Context): MutableMap<String, String?>
    {
        val imgHeadMap = mutableMapOf<String, String?>()
        imgHeadMap[HEAD_TYPE] = HEAD_TYPE_FILE
        imgHeadMap["Accept"] = "application/json"
        imgHeadMap["appversion"] = Device.getVersionName(context)
        imgHeadMap["requestid"] = GlobalParams.genRequestId()
        imgHeadMap["m"] = "s"
        return imgHeadMap
    }

    fun baseHeadMap(context: Context): HashMap<String, String?> {
        val headMap = HashMap<String, String?>()
        headMap["Api-Gzip"] = "1"// Api-Gzip 关联needGzip(拦截器处理) 查看是否还还关联Content-Encoding
        headMap["needGzip"] = "true"
        headMap["Connection"] = "close"
        headMap["Content-Encoding"] = "gzip"
        headMap["appversion"] = Device.getVersionName(context)
        headMap["requestid"] = GlobalParams.genRequestId()
        headMap["m"] = "s"
        headMap["rom"] = Build.DISPLAY
        val appName = PackageUtils.getPackageName(context)
        if (!appName.isNullOrEmpty()) {
            headMap["appName"] = appName
        }
        val appId = GlobalParams.getAppDefaultId()
        if (appId != null) {
            headMap["Request-From"] = appId
        }
        return headMap
    }

}
