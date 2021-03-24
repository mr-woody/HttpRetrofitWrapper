package com.simple.net.config

/**
 * http请求中的模块信息
 */
enum class AppIdEnum(appid: String, desc: String) {

    //s_usercenter
    SPERSONAL("personal", "学生个人中心"),
    SHELP("help", "学生帮助中心模块"),
    SACCOUNT("account", "学生帐号模块"),

    //StudentApp
    SLAUNCHER("launcher", "学生launcher数据模块"),
    SPREVIEW("preview", "学生预习模块"),
    SCOURSE("course", "学生上课模块"),
    SREVIEW("review", "学生复习模块"),

    //common
    SCOMMON("personal", "学生通用模块"),
    PUSHSERVICE("pushservice", "推送");

    var appid: String
        internal set
    var desc: String
        internal set

    init {
        this.appid = appid
        this.desc = desc
    }
}
