package com.aisino.trusthandwrite.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.aisino.trusthandwrite.data.DataModel;
import com.aisino.trusthandwrite.data.Message;
import com.aisino.trusthandwrite.data.Url;
import com.aisino.trusthandwrite.model.OtherData;
import com.aisino.trusthandwrite.model.Response;
import com.aisino.trusthandwrite.util.DialogUtil;
import com.aisino.trusthandwrite.util.GsonUtil;
import com.aisino.trusthandwrite.util.HttpUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by HXQ on 2017/5/25.
 */

public class AppStartActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("AppStartActivity", "onCreate getTaskId"+this.getTaskId());

        Log.e("AppStartActivity", "onStart code:" + getIntent().getStringExtra("code"));
        ////判断是否第三方跳转
        if (getIntent().getStringExtra("code") != null ) {//第三方跳转过来
            Log.e("AppStartActivity", "onCreate in other if YES");
            DataModel.otherData = new OtherData(AppStartActivity.this);
            String code = getIntent().getStringExtra("code");
            long contractId = getIntent().getLongExtra("contractId", 0);
            String backUrl = getIntent().getStringExtra("backUrl");
            getIntent().removeExtra("code");
            DataModel.otherData.setCode(code);
            DataModel.otherData.setContractId(contractId);
            DataModel.otherData.setBackUrl(backUrl);
            DataModel.initSystemSet(AppStartActivity.this);
            //直接跳转到签署界面
            Intent intent = new Intent(AppStartActivity.this, OtherSignActivity.class);
            startActivity(intent);
            finish();
        }
        else {//不是第三方跳转进来
            normalLine();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.e("AppStartActivity", "onNewIntent");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("AppStartActivity", "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("AppStartActivity", "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("AppStartActivity", "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("AppStartActivity", "onDestroy");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e("AppStartActivity", "onStart");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e("AppStartActivity", "onRestart");
    }

    /**
     * 正常路线
     */
    private void normalLine(){
        Log.e("AppStartActivity", "normalLine in other if NO");
        //判断应用是否被启动
        if (DataModel.isAppStart){//被启动,进行启动路线
            DataModel.isAppStart = false;
            //判断DataModel是否初始化
            if (DataModel.contractSegue == null){//没有
                //初始化
                DataModel.init(AppStartActivity.this);
            }
            Log.e("AppStartActivity", "normalLine end DataModel.init");
            //进行自动登录判断
            appStart();
        }else {//不是启动，是再次打开

        }
    }


    /**
     * 应用入口的相关处理
     */
    private void appStart(){
        //判断token是否已存在
        DataModel.getToken(AppStartActivity.this);
        //判断token是否有值
        if(DataModel.token == null || DataModel.token.equals("")){ //token为空或者“”时进入登陆界面
            DataModel.isVisible = false;
            toLoginActivity();
        } else {//token 有值
            //验证token是否过期
            checkToken(DataModel.token);
            //展示登陆后的主界面
            toEndActivity();
        }
    }

    /**
     * 验证token
     * @param token
     */
    private void checkToken(String token){
        // 使用Map封装请求参数
        Map<String, String> map = new HashMap<>();
        map.put("token", token);
        String json = GsonUtil.GsonString(map);
        // 定义发送请求的URL
        String url = Url.getUrl(Url.Url_Login_CheckToken);
        //发起请求
        HttpUtil.postRequestTest(url, json, false, new HttpUtil.PostResposeListner(){
            @Override
            public void back(boolean status, String result) {
                //判断请求是否成功
                if (!status) {//网络请求失败，无法验证token
                    DataModel.isVisible = false;
                }else {
                    Response response = GsonUtil.GsonToBean(result, Response.class);
                    //对操作返回状态值进行判断
                    if (response.getStatus() != 0) {//操作失败
                        DataModel.isVisible = false;
                    }
                }
            }
        });
    }

    /**
     * 跳转至登陆界面，同时销毁此activity
     */
    private void toLoginActivity (){
        Intent intent = new Intent(AppStartActivity.this, LoginActivity.class);
        startActivity(intent);
        this.finish();
    }

    /**
     * 跳转至主界面，同时销毁此activity
     */
    private void toEndActivity (){
        Intent intent = new Intent(AppStartActivity.this, EndActivity.class);
        startActivity(intent);
        this.finish();
    }
}
