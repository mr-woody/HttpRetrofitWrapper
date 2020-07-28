package com.simple.net.config


object GlobalParams {

    fun genRequestId(): String = "02xxxxx"

    /**
     * 获取Url默认配置的模块属性,正常应该在发起request的head中配置
     * 在拦截器中
     */
    fun getAppDefaultId(): String = AppIdEnum.SLAUNCHER.appid

}
