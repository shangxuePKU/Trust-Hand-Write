package com.aisino.trusthandwrite.view;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.aisino.trusthandwrite.custom.SignaturePad;
import com.aisino.trusthandwrite.data.DataModel;

import com.aisino.trusthandwrite.R;

/**
 * Created by HXQ on 2017/5/23.
 */

public class HandSignActivity extends Activity {

    private RelativeLayout layout;

    private SignaturePad mSignaturePad;
    private ImageButton mClearButton;
    private Button mSaveButton;
    private Button mCancelButton;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hand_sign);

        //点击空白进行取消操作
        layout=(RelativeLayout)findViewById(R.id.sign_action_layout);
        layout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Toast.makeText(getApplicationContext(), "提示：点击窗口外部关闭窗口！",
                        Toast.LENGTH_SHORT).show();
            }
        });

        mSignaturePad = (SignaturePad) findViewById(R.id.signature_pad);
        mSignaturePad.setOnSignedListener(new SignaturePad.OnSignedListener() {
            @Override
            public void onStartSigning() {
//                Toast.makeText(SignActionActivity.this, "手签中", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSigned() {
                mSaveButton.setEnabled(true);
                mClearButton.setEnabled(true);
            }

            @Override
            public void onClear() {
                mSaveButton.setEnabled(false);
                mClearButton.setEnabled(false);

            }
        });

        mClearButton = (ImageButton) findViewById(R.id.btn_sign_clean);
        mSaveButton = (Button) findViewById(R.id.btn_sign_save);
        mCancelButton = (Button) findViewById(R.id.btn_sign_cancel);

        mClearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSignaturePad.clear();
            }
        });

        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSignaturePad.clear();
                //HandSignActivity.this.finish();
                DataModel.back();
            }
        });

        //点击确定
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("mSaveButton","setOnClickListener");
                //存入数组
                Bitmap signatureBitmap = mSignaturePad.getSignatureBitmap();
                if(signatureBitmap != null){
                    DataModel.signatureBitmap = signatureBitmap;
                    mSignaturePad.clear();
                }else {
                    DataModel.signatureBitmap = null;
                }
                HandSignActivity.this.finish();
            }
        });
    }

}
