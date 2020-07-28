package com.simple.net.config

/**
 * http请求中的模块信息
 */
enum class AppIdEnum(appid: String, desc: String) {

    //s_usercenter
    SPERSONAL("spersonal", "学生个人中心"),
    SHELP("shelp", "学生帮助中心模块"),
    SACCOUNT("saccount", "学生帐号模块"),

    //StudentApp
    SLAUNCHER("slauncher", "学生launcher数据模块"),
    SPREVIEW("spreview", "学生预习模块"),
    SCOURSE("scourse", "学生上课模块"),
    SREVIEW("sreview", "学生复习模块"),
    SAUTOLEARNING("sautolearning", "学生自主学习模块"),
    SXIAOYUN("sxiaoyun", "学生OKAY小云模块"),
    SSHOW("sshow", "学生okayshow模块"),
    SOBSERVATORY("sobservatory", "学生观象台模块"),
    SMAGIC("smagic", "学生魔镜模块"),
    OKYUN("okyun", "小云"),

    //common
    SCOMMON("spersonal", "学生通用模块"),
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
