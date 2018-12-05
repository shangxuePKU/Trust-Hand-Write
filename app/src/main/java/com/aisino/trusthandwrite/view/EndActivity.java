package com.aisino.trusthandwrite.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.aisino.trusthandwrite.data.DataModel;

/**
 * Created by HXQ on 2017/6/2.
 */

public class EndActivity extends Activity {

    /**
     * 此Activity的launchMode设置为singleTask模式
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toMainActivity();
    }

    /**
     * 此Activity的launchMode设置为singleTask模式，只有不是第一次进入此activity，都会先进入此方法
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.e("EndActivity", "onNewIntent");
        Log.e("EndActivity", "onNewIntent code:"+getIntent().getStringExtra("code"));
        //判断是否退出
        if (DataModel.isOut){//退出情况下会进入这里
            //跳转到登录界面
            toLoginActivity();
            DataModel.isOut = false;
        }else {//返回桌面
            DataModel.isAppStart = true;
            this.finish();
        }
    }

    /**
     * 跳转至登陆界面，同时销毁此activity
     */
    private void toLoginActivity (){
        Intent intent = new Intent(EndActivity.this, LoginActivity.class);
        startActivity(intent);
        this.finish();
    }

    /**
     * 跳转至主界面
     */
    private void toMainActivity (){
        Intent intent = new Intent(EndActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
