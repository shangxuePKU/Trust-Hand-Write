package com.aisino.trusthandwrite.data;

import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.view.KeyEvent;

import com.aisino.trusthandwrite.model.Contract;
import com.aisino.trusthandwrite.model.Login;
import com.aisino.trusthandwrite.model.OtherData;
import com.aisino.trusthandwrite.model.SystemSet;
import com.aisino.trusthandwrite.model.Template;
import com.aisino.trusthandwrite.util.DialogUtil;
import com.aisino.trusthandwrite.view.AppStartActivity;

/**
 * 用于操作和处理一些公用数据
 * Created by HXQ on 2017/4/24.
 */

public class DataModel {

    //用于判断是否 应用刚启动进入MainActivity
    public static boolean isAppStart = true;
    //用于判断是否处于退出状态下
    public static boolean isOut = false;
    //登录是否有效
    public static boolean isVisible = true;

    public static boolean isFromRecoed = true;

    //保存token,登陆参数,系统设置参数的文件的文件名
    private static final String KEY_TOKEN = "token";
    private static final String KEY_LOGIN_1 = "login1";
    private static final String KEY_LOGIN_2 = "login2";
    private static final String KEY_LOGIN_3 = "login3";
    private static final String KEY_SYSTEMSET_1= "systemSet1";
    private static final String KEY_SYSTEMSET_2= "systemSet2";
    private static final String KEY_SYSTEMSET_3= "systemSet3";
    private static final String KEY_SYSTEMSET_4= "systemSet4";

    //登陆后服务端返回的值，用于接口的身份验证
    public static String token;
    //登陆相关参数
    public static Login login;
    //登陆后的相关设置
    public static SystemSet systemSet;
    //选择的模板
    public static Template selectedTemplate;

    //存储用于跳转传递的合同数据
    public static Contract contractSegue;
    //存储上一次请求的合同的信息
    public static Contract contractCache;

    //存储手写的签名
    public static Bitmap signatureBitmap;

    public static OtherData otherData;

    public static void init(Context context){
        contractSegue = new Contract(context);
        contractCache = new Contract(context);
    }

    /**
     * token,login,systemSet值保存到SharedPreferences中
     * @param context  调用SharedPreferences需要的值
     */
    public static void saveToken(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor=preferences.edit();
        //压入值
        editor.putString(KEY_TOKEN, token);
        editor.putString(KEY_LOGIN_1, login.getPlatformName());
        editor.putString(KEY_LOGIN_2, login.getClientName());
        editor.putString(KEY_LOGIN_3, login.getAuthCode());
        editor.putBoolean(KEY_SYSTEMSET_1, systemSet.getSwitch1());
        editor.putBoolean(KEY_SYSTEMSET_2, systemSet.getSwitch2());
        editor.putBoolean(KEY_SYSTEMSET_3, systemSet.getSwitch3());
        editor.putBoolean(KEY_SYSTEMSET_4, systemSet.getSwitch4());
        //保存
        editor.commit();
    }

    /**
     * 从ISharedPreferences获取token,login,systemSet值（第一次获取,token值为null）
     * @param context  调用SharedPreferences需要的值
     */
    public static void  getToken(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        //获取token值，如果没有这个key，则返回null
        token = preferences.getString(KEY_TOKEN, null);
        String login1 = preferences.getString(KEY_LOGIN_1, null);
        String login2 = preferences.getString(KEY_LOGIN_2, null);
        String login3 = preferences.getString(KEY_LOGIN_3, null);
        login = new Login(login1, login2, login3);
        boolean systemSet1 = preferences.getBoolean(KEY_SYSTEMSET_1, true);
        boolean systemSet2 = preferences.getBoolean(KEY_SYSTEMSET_2, false);
        boolean systemSet3 = preferences.getBoolean(KEY_SYSTEMSET_3, false);
        boolean systemSet4 = preferences.getBoolean(KEY_SYSTEMSET_4, false);
        systemSet = new SystemSet(systemSet1, systemSet2, systemSet3, systemSet4);
    }

    /**
     * 从ISharedPreferences获取systemSet值
     * @param context  调用SharedPreferences需要的值
     */
    public static void  initSystemSet(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean systemSet1 = preferences.getBoolean(KEY_SYSTEMSET_1, true);
        boolean systemSet2 = preferences.getBoolean(KEY_SYSTEMSET_2, false);
        boolean systemSet3 = preferences.getBoolean(KEY_SYSTEMSET_3, false);
        boolean systemSet4 = preferences.getBoolean(KEY_SYSTEMSET_4, false);
        systemSet = new SystemSet(systemSet1, systemSet2, systemSet3, systemSet4);
    }

    /**
     * 清除token的值
     * @param context 调用SharedPreferences需要的值
     */
    public static void cleanToken(Context context) {
        token = "";
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor=preferences.edit();
        //压入值
        editor.putString(KEY_TOKEN, token);
        editor.putString(KEY_LOGIN_1, token);
        editor.putString(KEY_LOGIN_2, token);
        editor.putString(KEY_LOGIN_3, token);
        editor.putBoolean(KEY_SYSTEMSET_1, systemSet.getSwitch1());
        editor.putBoolean(KEY_SYSTEMSET_2, systemSet.getSwitch2());
        editor.putBoolean(KEY_SYSTEMSET_3, systemSet.getSwitch3());
        editor.putBoolean(KEY_SYSTEMSET_4, systemSet.getSwitch4());
        //保存
        editor.commit();
    }

    /**
     * 实现类似系统back键返回效果
     */
    public static void back(){
        new Thread(){
            public void run() {
                try{
                    Instrumentation inst = new Instrumentation();
                    inst.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
                } catch (Exception e) {

                }
            }
        }.start();
    }

    /**
     * 判断登录是否有效
     */
    public static void out(final Context context){
        //清除token，进入登陆界面
        DataModel.cleanToken(context);
        DataModel.isOut = true;
        DialogUtil.showSubmitDialog(context,
                Message.getMessage(Message.Message_Login_Expire),
                new DialogUtil.ButtonClickListner() {
                    @Override
                    public void onClick() {
                        Intent intent = new Intent(context, AppStartActivity.class);
                        context.startActivity(intent);
                    }
                });
    }
}
