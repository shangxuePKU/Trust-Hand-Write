package com.aisino.trusthandwrite.custom;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aisino.trusthandwrite.data.DataModel;
import com.aisino.trusthandwrite.R;

/**
 * Created by HXQ on 2017/5/22.
 */

public class TopNavigationView extends RelativeLayout {

    //当前导航所属的activity
    public static final int LOGIN_ACTIVITY = 1;
    public static final int SET_ACTIVITY = 2;
    public static final int CHOOSE_TEMPLATE_ACTIVITY = 3;
    public static final int RECORD_ACTIVITY = 4;
    public static final int PERSONAL_ACTIVITY = 5;
    public static final int WRITETEMPLATE_ACTIVITY = 6;
    public static final int BUILDCONTRACT_ACTIVITY = 7;
    public static final int SIGNCONTRACT_ACTIVITY = 8;
    public static final int ADDFILE_ACTIVITY = 9;
    public static final int INFOCAPTURE_ACTIVITY = 10;
    public static final int OTHERSIGN_ACTIVITY = 11;

    private Button backBtn;
    private TextView titleTv;
    private Button nextBtn;

    public TopNavigationView(Context context) {
        super(context);
        init(context);
    }

    public TopNavigationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TopNavigationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public TopNavigationView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    /**
     * 初始化布局
     * @param context
     */
    private void init(Context context){
        LayoutInflater.from(context).inflate(R.layout.top_navigation, this);
        backBtn = (Button)findViewById(R.id.tn_back);
        titleTv = (TextView)findViewById(R.id.tn_title);
        nextBtn = (Button)findViewById(R.id.tn_next);

        final Context newContext = context;
        //监听返回按钮点击事件
        backBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (navigationOtherBackClickListner != null){
                    navigationOtherBackClickListner.back();
                }else {
                    DataModel.back();
                }
            }
        });
        //监听下一步按钮点击事件
        nextBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (navigationClickListner != null){
                    navigationClickListner.next();
                }
            }
        });
    }

    /**
     * 根据传入的activity，设置导航项
     * @param activity
     */
    public void setTitleAndButton(int activity){
        switch (activity){
            case LOGIN_ACTIVITY:
                titleTv.setText("登 录");
                break;
            case SET_ACTIVITY:
                titleTv.setText("系统设置");
                break;
            case CHOOSE_TEMPLATE_ACTIVITY:
                backBtn.setVisibility(View.VISIBLE);
                titleTv.setText("模板签章");
                break;
            case RECORD_ACTIVITY:
                titleTv.setText("历史合同");
                break;
            case PERSONAL_ACTIVITY:
                titleTv.setText("用户认证");
                break;
            case WRITETEMPLATE_ACTIVITY:
                backBtn.setVisibility(View.VISIBLE);
                titleTv.setText("模板签章");
                break;
            case BUILDCONTRACT_ACTIVITY:
                backBtn.setVisibility(View.VISIBLE);
                titleTv.setText("模板签章");
                break;
            case SIGNCONTRACT_ACTIVITY:
                backBtn.setVisibility(View.VISIBLE);
                titleTv.setText("合同查看");
                break;
            case ADDFILE_ACTIVITY:
                backBtn.setVisibility(View.VISIBLE);
                titleTv.setText("上传文件");
                break;
            case INFOCAPTURE_ACTIVITY:
                backBtn.setVisibility(View.VISIBLE);
                titleTv.setText("信息采集");
                break;
            case OTHERSIGN_ACTIVITY:
                backBtn.setVisibility(View.VISIBLE);
                titleTv.setText("合同查看");
                break;
        }
    }

    /**
     * 显示下一步按钮
     */
    public void showNextBtn(){
        nextBtn.setVisibility(View.VISIBLE);
    }

    /**
     * 隐藏下一步按钮
     */
    public void hiddenNextBtn(){
        nextBtn.setVisibility(View.GONE);
    }
    //回调
    public interface NavigationClickListner{
        void next();
    }

    NavigationClickListner navigationClickListner;

    public  void setNavigationClickListner(NavigationClickListner navigationClickListner) {
        this.navigationClickListner = navigationClickListner;
    }

    public interface NavigationOtherBackClickListner{
        void back();
    }

    NavigationOtherBackClickListner navigationOtherBackClickListner;

    public void setNavigationOtherBackClickListner(NavigationOtherBackClickListner navigationOtherBackClickListner) {
        this.navigationOtherBackClickListner = navigationOtherBackClickListner;
    }
}
