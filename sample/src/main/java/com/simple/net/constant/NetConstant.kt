package com.simple.net.constant

object NetConstant {

    /**
     * 定义Json上下行常量
     */
    object JsonConstants {
        /**
         * 公共上行
         */
        const val JSON_VS = "vs"
        const val JSON_VC = "vc"
        const val JSON_UA = "ua"
        const val JSON_OS = "os"
        const val JSON_SW = "sw"
        const val JSON_SH = "sh"
        const val JSON_CONTYPE = "contype"
        const val JSON_IMEI = "imei"
        const val JSON_ICCID = "iccid"
        const val JSON_MAC = "mac"
        const val JSON_CHANNEL = "channel"
        const val JSON_UDID = "udid"
        const val JSON_SERIAL = "serial"
        const val JSON_SCREEN_PATTERN = "screen_pattern"

        /**
         *
         */
        const val JSON_UID = "uid"
        const val JSON_TOKEN = "token"
        /**
         * 反馈内容
         */
        const val JSON_CONTENT = "content"
        /**
         * 反馈联系方式
         */
        const val JSON_CONTACT = "contact"
        /**
         * 反馈类型
         */
        const val JSON_FEEDBACK_TYPE = "type"
        /**
         * 反馈是否需要回复
         */
        const val JSON_NEEDREPLY = "needreply"
        /**
         * 反馈问题id
         */
        const val JSON_PERSONAL_QUESTION_ID = "questionId"
    }

    const val NO_DATA_CODE = 1015
    const val NO_EXAM_CODE = 1016
    const val SSL_TIME_ERROR_CODE = 1030
}
