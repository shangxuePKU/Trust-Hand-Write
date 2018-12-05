package com.aisino.trusthandwrite.view;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.aisino.trusthandwrite.data.DataModel;
import com.aisino.trusthandwrite.data.Message;
import com.aisino.trusthandwrite.data.Url;
import com.aisino.trusthandwrite.custom.BottomMenuView;
import com.aisino.trusthandwrite.custom.TopNavigationView;
import com.aisino.trusthandwrite.util.DialogUtil;
import com.aisino.trusthandwrite.util.HttpUtil;

import com.aisino.trusthandwrite.R;

public class PersonalActivity extends Activity {

    private boolean mIsExit;
    public static Button logout_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setDefalut();
    }

    /**
     * 设置默认值
     */
    private void setDefalut(){
        setContentView(R.layout.activity_personal);

        //设置导航栏
        TopNavigationView topNavigationView = (TopNavigationView)findViewById(R.id.personal_navigation);
        topNavigationView.setTitleAndButton(TopNavigationView.PERSONAL_ACTIVITY);

        //设置用户信息
        TextView mPlatformName = (TextView)findViewById(R.id.txt_platform2);
        TextView mClientName = (TextView)findViewById(R.id.txt_client2);
        TextView mAuthCode = (TextView)findViewById(R.id.txt_auth2);

        mPlatformName.setText(DataModel.login.getPlatformName());
        mClientName.setText(DataModel.login.getClientName());
        mAuthCode.setText(DataModel.login.getAuthCode());

        logout_btn = (Button) findViewById(R.id.logout_btn);

        //设置底部菜单项
        BottomMenuView bottomMenuView = (BottomMenuView)this.findViewById(R.id.personal_bottom_bm);
        bottomMenuView.setMenu(BottomMenuView.PERSONAL_ACTIVITY);
    }

    /**
     * 点击登出按钮
     * @param v
     */
    public void logoutBtnClick(View v) {
        //退出请求
        logOut();
    }

    /**
     * 发起登出请求，是否请求成功都退出
     */
    private void logOut() {
        // 定义发送请求的URL
        String url = Url.getUrl(Url.Url_Login_Logout);
        Log.v("url",url);
        // 发送请求
        HttpUtil.postRequestTest(url, null, true, new HttpUtil.PostResposeListner() {
            @Override
            public void back(boolean status, String result)  {
                DataModel.isVisible = false;
                toEndActivity();
            }
        });
    }

    /**
     * 清除token,跳转至AppStart界面以销毁所有activity,退出登录
     */
    private void toEndActivity (){
        //清除token
        DataModel.cleanToken(PersonalActivity.this);
        DataModel.isOut = true;
        //进入EndActivity界面
        Intent intent = new Intent(PersonalActivity.this, EndActivity.class);
        startActivity(intent);
    }
}
