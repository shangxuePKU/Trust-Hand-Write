package com.aisino.trusthandwrite.view;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.aisino.trusthandwrite.data.DataModel;
import com.aisino.trusthandwrite.custom.BottomMenuView;
import com.aisino.trusthandwrite.custom.TopNavigationView;

import com.aisino.trusthandwrite.R;

/**
 * Created by HXQ on 2017/5/22.
 */

public class SetActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set);
        //判断登录是否有效
        if (!DataModel.isVisible) {//无效
            DataModel.out(SetActivity.this);
        }

        ImageView signImage = (ImageView) findViewById(R.id.sign_seton);
        signImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "默认必须采集签名信息！", Toast.LENGTH_SHORT).show();
            }
        });

        final ImageView photoImage = (ImageView) findViewById(R.id.photo_setoff);
        if(DataModel.systemSet.getSwitch2()){
            photoImage.setImageResource(R.drawable.set_on);
        }
        photoImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(DataModel.systemSet.getSwitch2()){
                    photoImage.setImageResource(R.drawable.set_off);
                    DataModel.systemSet.setSwitch2(false);
                }else {
                    photoImage.setImageResource(R.drawable.set_on);
                    DataModel.systemSet.setSwitch2(true);
                }
            }
        });

        final ImageView img_voice_seton = (ImageView) findViewById(R.id.voice_setoff);
        if(DataModel.systemSet.getSwitch3()){
            img_voice_seton.setImageResource(R.drawable.set_on);
        }
        img_voice_seton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(DataModel.systemSet.getSwitch3()){
                    img_voice_seton.setImageResource(R.drawable.set_off);
                    DataModel.systemSet.setSwitch3(false);
                }else {
                    img_voice_seton.setImageResource(R.drawable.set_on);
                    DataModel.systemSet.setSwitch3(true);
                }
            }
        });

        final ImageView img_video_seton = (ImageView) findViewById(R.id.video_setoff);
        if(DataModel.systemSet.getSwitch4()){
            img_video_seton.setImageResource(R.drawable.set_on);
        }
        img_video_seton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(DataModel.systemSet.getSwitch4()){
                    img_video_seton.setImageResource(R.drawable.set_off);
                    DataModel.systemSet.setSwitch4(false);
                }else {
                    img_video_seton.setImageResource(R.drawable.set_on);
                    DataModel.systemSet.setSwitch4(true);
                }
            }
        });
        //设置导航栏
        TopNavigationView topNavigationView = (TopNavigationView)this.findViewById(R.id.set_navigation);
        topNavigationView.setTitleAndButton(TopNavigationView.SET_ACTIVITY);
        //设置底部菜单项
        BottomMenuView bottomMenuView = (BottomMenuView)this.findViewById(R.id.set_bottom_bm);
        bottomMenuView.setMenu(BottomMenuView.SET_ACTIVITY);
    }
}
