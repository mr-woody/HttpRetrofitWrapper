package com.simple.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

/**
 * @author unknown
 */
public class ConnectManager {
    /**
     * Connect type
     */
    private static final String NET_CONNTYPE_2G = "1";
    private static final String NET_CONNTYPE_3G = "2";
    private static final String NET_CONNTYPE_WIFI = "3";

    private static final String MOBILE_UNI_PROXY_IP = "10.0.0.172";
    private static final String TELCOM_PROXY_IP = "10.0.0.200";
    private static final String PROXY_PORT = "80";

    private static final String CHINA_MOBILE_WAP = "CMWAP";
    private static final String CHINA_UNI_WAP = "UNIWAP";
    private static final String CHINA_UNI_3G = "3GWAP";
    private static final String CHINA_TELCOM = "CTWAP";

    private static final String WIFI_NAME = "WIFI";

    private String mApn;
    private static String mProxy;
    private static String mPort;
    private static boolean mUseWap;
    public static final Uri PREFERRED_APN_URI = Uri
            .parse("content://telephony/carriers/preferapn");


    /**
     * Get Current connection type
     *
     * @return ConnectType have three kind of type
     */
    public static void checkConnectType(Context context) {

        final ConnectivityManager conn = (ConnectivityManager) context.getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (conn != null) {

            NetworkInfo info = conn.getActiveNetworkInfo();

            if (info != null) {
                String connStr = info.getTypeName();

                if (WIFI_NAME.equalsIgnoreCase(connStr)) {

                    // set member param
                    mUseWap = false;

                } else if ("MOBILE".equalsIgnoreCase(connStr)) {

                    String apn = info.getExtraInfo();

                    if (null != apn && apn.indexOf("wap") > -1) {

                        if ("cmwap".equals(apn) || "uniwap".equals(apn)
                                || "3gwap".equals(apn)) {

                            mUseWap = true;
                            mProxy = MOBILE_UNI_PROXY_IP;
                            mPort = PROXY_PORT;

                        } else if ("ctwap".equals(apn)) {

                            mUseWap = true;
                            mProxy = TELCOM_PROXY_IP;
                            mPort = PROXY_PORT;

                        } else {
                            // not use wap
                            mUseWap = false;
                        }

                    } else {
                        // not use wap
                        mUseWap = false;
                    }
                }
            }
        }
    }

    /**
     * 网络是否已连接
     *
     * @return
     */
    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean isConnected = false;
        NetworkInfo info = cm.getActiveNetworkInfo();

        if (null != info) {
            isConnected = info.isAvailable();
        }

        return isConnected;
    }

    /**
     * make true current connect service is wifi
     * <p>
     * Application context
     *
     * @return
     */
    public static boolean isWifi(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        }
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetInfo != null
                && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI;
    }

    public static boolean isWapNetwork() {
        return mUseWap;
    }

    public static String getConnectionString(Context context) {
        if (isWifi(context)) {
            return NET_CONNTYPE_WIFI;
        }

        try {
            checkConnectType(context);
        } catch (Exception e) {
            return NET_CONNTYPE_2G;
        }

        if (isWapNetwork()) {
            return NET_CONNTYPE_3G;
        }

        return NET_CONNTYPE_2G;
    }
}