package com.aisino.trusthandwrite.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.aisino.trusthandwrite.data.DataModel;
import com.aisino.trusthandwrite.data.Message;
import com.aisino.trusthandwrite.data.Url;
import com.aisino.trusthandwrite.custom.TopNavigationView;
import com.aisino.trusthandwrite.model.Login;
import com.aisino.trusthandwrite.model.Response;
import com.aisino.trusthandwrite.model.SystemSet;
import com.aisino.trusthandwrite.util.DialogUtil;
import com.aisino.trusthandwrite.util.GsonUtil;
import com.aisino.trusthandwrite.util.HttpUtil;

import java.util.HashMap;
import java.util.Map;

import com.aisino.trusthandwrite.R;

/**
 * Created by HXQ on 2017/5/22.
 */

public class LoginActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("LoginActivity", "onCreate");
        setContentView(R.layout.activity_login);
        //设置导航栏
        TopNavigationView topNavigationView = (TopNavigationView)findViewById(R.id.login_navigation);
        topNavigationView.setTitleAndButton(TopNavigationView.LOGIN_ACTIVITY);
    }
    @Override
    protected void onResume() {
        super.onResume();
        Log.e("LoginActivity", "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("LoginActivity", "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("AppStartActivity", "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("LoginActivity", "onDestroy");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e("LoginActivity", "onStart");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //点击后退按钮回到系统界面
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
        DataModel.isAppStart = true;
        LoginActivity.this.onDestroy();
    }

    /**
     *  封装参数，发起登陆请求
     * @param platformName 登陆参数
     * @param clientName 登陆参数
     * @param authCode 登陆参数
     * @throws Exception 异常
     */
    private void login(final String platformName, final String clientName, final String authCode) {
        // 使用Map封装请求参数
        Map<String, String> map = new HashMap<>();
        map.put("platformName", platformName);
        map.put("clientName", clientName);
        map.put("authCode", authCode);
        String json = GsonUtil.GsonString(map);
        // 定义发送请求的URL
        String url = Url.getUrl(Url.Url_Login_LoginByCode);
        // 发送请求
        HttpUtil.postRequestTest(url, json, false, new HttpUtil.PostResposeListner() {
            @Override
            public void back(boolean status, String result)  {
                DialogUtil.hiddenWaitingDialog();
                if (status){//网络请求成功
                    Response response = GsonUtil.GsonToBean(result, Response.class);
                    //对操作返回状态值进行判断
                    if (response.getStatus() == 0){//操作成功
                        String token = ((Map<String, String>) response.getDatas()).get("token");
                        //判断token值是否为空
                        if (token != null){//不为空
                            DataModel.token = token;
                            DataModel.login = new Login(platformName, clientName, authCode);
                            DataModel.saveToken(LoginActivity.this);
                            DataModel.isVisible = true;
                            //进入主菜单
                            toMainActivity();
                        }else {//为空
                            DialogUtil.showSubmitDialog(LoginActivity.this, Message.getMessage(Message.Message_Error_Unkown), null);
                        }
                    }else {//操作失败
                        String message = response.getMessage();
                        DialogUtil.showSubmitDialog(LoginActivity.this, message, null);
                    }
                }else {//网络请求失败
                    DialogUtil.showSubmitDialog(LoginActivity.this, result, null);
                }
            }
        });

    }

    /**
     * 进入主菜单
     */
    private void toMainActivity() {
        Intent intent = new Intent();
        intent.setClass(LoginActivity.this, EndActivity.class);
        startActivity(intent);
        this.finish();
    }

    /**
     * 点击登陆按钮
     * @param v 登陆按钮
     */
    public void loginBtnClick(View v) {
        //显示等待动画
        DialogUtil.showWaitingDialog(LoginActivity.this,
                Message.getMessage(Message.Message_Login_Loading));
        //获取用户输入的登录名
        final String authCode = ((EditText) this.findViewById(R.id.login_authCode_edit)).getText().toString();
        final String platformName = ((EditText) this.findViewById(R.id.login_platformName_edit)).getText().toString();
        final String clientName = ((EditText) this.findViewById(R.id.login_clientName_edit)).getText().toString();
        //登陆
        login(platformName, clientName, authCode);
    }
}
