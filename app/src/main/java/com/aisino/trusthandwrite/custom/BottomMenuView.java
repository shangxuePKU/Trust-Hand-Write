package com.aisino.trusthandwrite.custom;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.aisino.trusthandwrite.view.ChooseTemplateActivity;
import com.aisino.trusthandwrite.view.MainActivity;
import com.aisino.trusthandwrite.view.PersonalActivity;
import com.aisino.trusthandwrite.R;
import com.aisino.trusthandwrite.view.RecordActivity;
import com.aisino.trusthandwrite.view.SetActivity;


/**
 * Created by HXQ on 2017/5/17.
 */

public class BottomMenuView extends LinearLayout {

    //当前菜单展示的activity
    public static final int MAIN_ACTIVITY = 1;
    public static final int SET_ACTIVITY = 2;
    public static final int RECORD_ACTIVITY = 4;
    public static final int PERSONAL_ACTIVITY = 5;

    private LinearLayout mainLl;
    private ImageView mainIv;
    private TextView mainTv;
    private LinearLayout setLl;
    private ImageView setIv;
    private TextView setTv;
    private LinearLayout signLl;
    private ImageView signIv;
    private TextView signTv;
    private LinearLayout recordLl;
    private ImageView recordIv;
    private TextView recordTv;
    private LinearLayout personalLl;
    private ImageView personalIv;
    private TextView personalTv;

    public BottomMenuView(Context context) {
        super(context);
        // 加载布局
        init(context);
    }

    public BottomMenuView(Context context, AttributeSet attrs) {
        super(context, attrs);// 加载布局
        init(context);
    }

    public BottomMenuView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);// 加载布局
        init(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public BottomMenuView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    /**
     * 初始化布局
     * @param context
     */
    private void init(Context context){
        LayoutInflater.from(context).inflate(R.layout.bootom_menu, this);
        //获取控件
        mainLl = (LinearLayout)findViewById(R.id.bm_main_ll);
        mainIv = (ImageView)findViewById(R.id.bm_main_iv);
        mainTv = (TextView)findViewById(R.id.bm_main_tv);
        setLl = (LinearLayout)findViewById(R.id.bm_set_ll);
        setIv = (ImageView)findViewById(R.id.bm_set_iv);
        setTv = (TextView)findViewById(R.id.bm_set_tv);
        signLl = (LinearLayout)findViewById(R.id.bm_sign_ll);
        signIv = (ImageView)findViewById(R.id.bm_sign_iv);
        signTv = (TextView)findViewById(R.id.bm_sign_tv);
        recordLl = (LinearLayout)findViewById(R.id.bm_record_ll);
        recordIv = (ImageView)findViewById(R.id.bm_record_iv);
        recordTv = (TextView)findViewById(R.id.bm_record_tv);
        personalLl = (LinearLayout)findViewById(R.id.bm_personal_ll);
        personalIv = (ImageView)findViewById(R.id.bm_personal_iv);
        personalTv = (TextView)findViewById(R.id.bm_personal_tv);

        final Context newContext = context;
        //监听菜单项主界面点击事件
        mainLl.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //跳转至主界面
                Intent intent = new Intent(newContext, MainActivity.class);
                newContext.startActivity(intent);
            }
        });
        //监听菜单项设置点击事件
        setLl.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //跳转至设置界面
                Intent intent = new Intent(newContext, SetActivity.class);
                newContext.startActivity(intent);
            }
        });
        //监听菜单项模板列表点击事件
        signLl.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //跳转至模板列表界面
                Intent intent = new Intent(newContext, ChooseTemplateActivity.class);
                newContext.startActivity(intent);
            }
        });
        //监听菜单项历史记录点击事件
        recordLl.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //跳转至历史记录
                Intent intent = new Intent(newContext, RecordActivity.class);
                newContext.startActivity(intent);
            }
        });
        //监听菜单项个人信息点击事件
        personalLl.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //跳转至个人信息界面
                Intent intent = new Intent(newContext, PersonalActivity.class);
                newContext.startActivity(intent);
            }
        });
    }

    /**
     * 根据传入的activity，设置相应的菜单项变色
     * @param activity
     */
    public void setMenu(int activity){
        switch (activity){
            case MAIN_ACTIVITY:
                //不可点击
                mainLl.setClickable(false);
                //改变图片和文本颜色
                mainIv.setImageResource(R.drawable.tab_home_pressed);
                mainTv.setTextColor(android.graphics.Color.parseColor("#1E90FF"));
                break;
            case SET_ACTIVITY:
                //不可点击
                setLl.setClickable(false);
                //改变图片和文本颜色
                setIv.setImageResource(R.drawable.tab_set_pressed);
                setTv.setTextColor(android.graphics.Color.parseColor("#1E90FF"));
                break;
            case RECORD_ACTIVITY:
                //不可点击
                recordLl.setClickable(false);
                //改变图片和文本颜色
                recordIv.setImageResource(R.drawable.tab_record_pressed);
                recordTv.setTextColor(android.graphics.Color.parseColor("#1E90FF"));
                break;
            case PERSONAL_ACTIVITY:
                //不可点击
                personalLl.setClickable(false);
                //改变图片和文本颜色
                personalIv.setImageResource(R.drawable.tab_personal_pressed);
                personalTv.setTextColor(android.graphics.Color.parseColor("#1E90FF"));
                break;
        }
    }
}
