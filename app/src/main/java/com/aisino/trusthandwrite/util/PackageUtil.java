package com.aisino.trusthandwrite.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * Created by HXQ on 2017/6/1.
 */

public class PackageUtil {

    /**
     * 判断安装的应用之中是否安装了指定包名的应用
     * @param pkgName
     * @return
     */
    public static boolean isPkgInstalled(Context context, String pkgName) {
        PackageInfo packageInfo = null;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(pkgName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            packageInfo = null;
            e.printStackTrace();
        }
        if (packageInfo == null) {
            return false;
        } else {
            return true;
        }
    }


}
