package com.simple.net.constant

/**
 * 包含中间层下发的错误码，参考http://wiki.okjiaoyu.cn/pages/viewpage.action?pageId=24648402
 * PAD_ERROR_GENER(1001, "服务器开小差了，请你稍后再试！")
 * PAD_ERROR_PARAM(1005, "参数错误")
 *
 * 疑问：以下是业务异常还是代码异常
 * PAD_ERROR_QUESTION_CORRECTING_FAIL(1008, "试题修改失败")
 * PAD_ERROR_UPLOAD_AVATAR(1013, "上传头像失败，请重新上传")
 * PAD_ERROR_ANSWER_SUBMIT(1014, "试题答案数据错误")
 * PAD_ERROR_USER_UPDATE_AVATAR(1017, "更新头像失败")
 * PAD_ERROR_FILE_UPLOAD(1022, "文件上传失败")
 * PAD_INFO_STUDENT_NO_TEACHER(1029, "学生没有老师")
 * PAD_ERROR_PARAM_NO_REQUEST_ID(1038, "请传入参数requestId")
 * PAD_ERROR_OTHER(1042,"服务器内部出错")
 * PAD_INFO_ERROR_APPUPDATE(1050, "APP升级错误")
 * PAD_BAD_ADDFROMDB(1055,"失败")
 * PAD_INFO_BADUPDATEAPPNAME(1066,"升级包名错误")
 * PAD_ERROR_CLASS_APPLY(1070,"申请加入班级失败")
 * PAD_INFO_GET_USER_FAIL(1095,"获取用户信息失败")
 * PAD_CHANGE_PWD_FALSE(1100,"变更密码失败")
 *
 * 1180 - 1255   和  1900 - 1904 是服务异常
 */
object OkHttpCode {
    const val CODE_DEFAULT = -1
    const val CODE_SUCCESS = 0
    const val CODE_200 = 200

    const val CODE_OK_TOKEN_INVALID = 1004
    const val CODE_SERVER_ERROR = 1001
    const val CODE_NO_MORE_DATA = 1015
    const val CODE_SERVER_ERROR_MORE_FIRST = 1180

    const val CODE_TIME_INVALID = 1181

    /**
     * 时间错误导致https证书校验失败
     */
    val CODE_SSL_TIME_ERROR_CODE = 1030
    /**
     * 需要token的接口传递了null或者""的token会报此错误码异常
     */
    const val CODE_OK_TOKEN_INVALID_NULL = 1900
    /**
     * 需要token的接口传递了token但是token错误
     */
    const val CODE_OK_TOKEN_INVALID_ERROR = 1903
    /**
     * 非当前学段的数据 todo 这块需要梳理，需要区分服务异常和业务异常
     */
    const val CODE_OK_SCHOOL_ERROR = 1121
    /**
     * 加入的班级不存在
     */
    const val CODE_OK_CLASS_NULL = 1084
}