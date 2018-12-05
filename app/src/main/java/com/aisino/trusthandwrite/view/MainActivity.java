package com.aisino.trusthandwrite.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.aisino.trusthandwrite.data.DataModel;
import com.aisino.trusthandwrite.custom.BottomMenuView;

import com.aisino.trusthandwrite.R;

/**
 * Created by HXQ on 2017/5/22.
 */

public class MainActivity extends Activity {

    private boolean mIsExit;

    @Override
    protected void onStart() {
        super.onStart();
        //判断登录是否有效
        if (!DataModel.isVisible) {//无效
            DataModel.out(MainActivity.this);
        }
        //展示主界面
        showMain();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            if (mIsExit) {
                toEndActivity();
            } else {
                Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
                mIsExit = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mIsExit = false;
                    }
                }, 2000);
            }
            return true;
        }else {
            return super.onKeyDown(keyCode, event);
        }
    }

    /**
     * 展现主界面布局和设置菜单项
     */
    private void showMain(){
        //展示登陆后的主界面
        setContentView(R.layout.activity_main);
        //设置底部菜单项
        BottomMenuView bottomMenuView = (BottomMenuView)this.findViewById(R.id.main_bottom_bm);
        bottomMenuView.setMenu(BottomMenuView.MAIN_ACTIVITY);
    }

    /**
     * 清除token,跳转至EndActivity界面以销毁所有activity
     */
    private void toEndActivity(){
        //DataModel.isBackDesktop = true;
        //进入AppStart界面
        Intent intent = new Intent(MainActivity.this, EndActivity.class);
        startActivity(intent);
    }

    /**
     * 点击去设置界面按钮
     * @param view 去设置界面按钮
     */
    public void toSetBtnClick(View view) {
        Intent intent = new Intent (MainActivity.this, SetActivity.class);
        startActivity(intent);
    }

    /**
     * 点击去历史记录界面按钮
     * @param view 去历史记录界面按钮
     */
    public void toRecordBtnClick(View view) {
        Intent intent = new Intent (MainActivity.this,RecordActivity.class);
        startActivity(intent);
    }

    /**
     * 点击去模板列表界面按钮
     * @param view 去模板列表界面按钮
     */
    public void toModelListBtnClick(View view){
        Intent intent = new Intent(MainActivity.this, ChooseTemplateActivity.class);
        startActivity(intent);
    }

    /**
     * 点击去个人信息界面按钮
     * @param view 去个人信息界面按钮
     */
    public void toPersonalBtnClick(View view) {
        Intent intent = new Intent (MainActivity.this,PersonalActivity.class);
        startActivity(intent);
    }

    /**
     * 点击去上传文件界面按钮
     * @param view 去上传文件界面按钮
     */
    public void toAddBtnClick(View view){
        Intent intent = new Intent(MainActivity.this, AddFileActivity.class);
        //Intent intent = new Intent(MainActivity.this, InfoCaptureActivity.class);
        startActivity(intent);
    }
}
