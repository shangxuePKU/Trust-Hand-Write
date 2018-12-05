package com.aisino.trusthandwrite.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aisino.trusthandwrite.custom.TopNavigationView;
import com.aisino.trusthandwrite.data.DataModel;
import com.aisino.trusthandwrite.util.Base64Util;
import com.aisino.trusthandwrite.util.GsonUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.aisino.trusthandwrite.R;

/**
 * Created by HXQ on 2017/5/26.
 */

public class InfoCaptureActivity extends Activity{

    private final int SYSTEM_CAMERA_REQUESTCODE_PHOTO = 0;
    private final int SYSTEM_CAMERA_REQUESTCODE_VIDEO = 1;

    //录音计时
    private int recorderTime = 0;
    //记录是否正在录音，默认不是正在录音
    private boolean isRecorder = false;
    //记录是否正在播放录音，默认不是正在播放
    private boolean isRecorderPlay = false;

    private Bitmap photoBitmap = null;
    private String videoFilePath = null;
    private String recorderFilePath = null;

    private Bitmap largeBitmap;
    private MediaPlayer mPlayer = null;
    private MediaRecorder mRecorder = null;

    private Button photoToBtn;
    private ImageView photoImage;
    private Button photoRestartBtn;
    private Button videoToBtn;
    private ImageView videoImage;
    private RelativeLayout videoBackground;
    private Button videoRestartBtn;
    private TextView recorderTimeTv;
    private Button recorderToBtn;
    private ImageView recorderPlayImage;
    private Button recorderRestartBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_capture);
        //设置导航栏
        TopNavigationView topNavigationView = (TopNavigationView)findViewById(R.id.ic_navigation);
        topNavigationView.setTitleAndButton(TopNavigationView.INFOCAPTURE_ACTIVITY);

        RelativeLayout photoRl = (RelativeLayout)findViewById(R.id.ic_photo_rl);
        RelativeLayout videoRl = (RelativeLayout)findViewById(R.id.ic_video_rl);
        RelativeLayout recorderRl = (RelativeLayout)findViewById(R.id.ic_recorder_rl);
        //判断图片采集是否开启
        if (DataModel.systemSet.getSwitch2()){//开启
            photoToBtn = (Button)findViewById(R.id.ic_photo_toBtn);
            photoImage = (ImageView)findViewById(R.id.ic_photo_image);
            photoRestartBtn = (Button)findViewById(R.id.ic_photo_restaartBtn);
        }else {//没有开启
            photoRl.setVisibility(View.GONE);
        }
        //判断声音采集是否开启
        if (DataModel.systemSet.getSwitch3()){//开启
            recorderTimeTv = (TextView)findViewById(R.id.ic_recorder_time);
            recorderToBtn = (Button)findViewById(R.id.ic_recorder_toBtn);
            recorderPlayImage = (ImageView)findViewById(R.id.ic_recorder_image);
            recorderPlayImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setRecorderPlay();
                }
            });
            recorderRestartBtn = (Button)findViewById(R.id.ic_recorder_restaartBtn);
        }else {//没有开启
            recorderRl.setVisibility(View.GONE);
        }
        //判断视频采集是否开启
        if (DataModel.systemSet.getSwitch4()){//开启
            videoToBtn = (Button)findViewById(R.id.ic_video_toBtn);
            videoImage = (ImageView)findViewById(R.id.ic_video_image);
            videoBackground = (RelativeLayout)findViewById(R.id.ic_video_background);
            videoRestartBtn = (Button)findViewById(R.id.ic_video_restaartBtn);
        }else {//没有开启
            videoRl.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case SYSTEM_CAMERA_REQUESTCODE_PHOTO:
                Bundle bundle = data.getExtras();
                // 获取相机返回的数据，并转换为Bitmap图片格式
                if (bundle != null){
                    Bitmap bitmap = (Bitmap) bundle.get("data");
                    photoBitmap = bitmap;
                    setPhoto(bitmap);
                }
                break;
            case SYSTEM_CAMERA_REQUESTCODE_VIDEO:
                if (data != null){
                    Uri videoUri = data.getData();
                    Cursor cursor = managedQuery(videoUri, null, null, null, null);
                    cursor.moveToFirst();//这个必须加，否则下面读取会报错
                    String videoType = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.MIME_TYPE));
                    String videoFilePath = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
                    int recordedVideoFileSize = cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media.SIZE));
                    Log.i("videoType", videoType);
                    Log.i("videoFilePath", videoFilePath);
                    Log.i("videoSize", ""+recordedVideoFileSize);
                    this.videoFilePath = videoFilePath;
                    setVideo(videoType);
                }
                break;
        }
    }

    /**
     * 设置照片和相关按钮
     * @param bitmap
     */
    private void setPhoto(Bitmap bitmap){
        photoToBtn.setVisibility(View.GONE);
        //展示照片
        photoImage.setImageBitmap(bitmap);
        photoImage.setVisibility(View.VISIBLE);
        largeBitmap = bitmap;
        //点击查看大图
        photoImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = LayoutInflater.from(InfoCaptureActivity.this);
                View imgEntryView = inflater.inflate(R.layout.dialog_photo_entry, null); // 加载自定义的布局文件
                final AlertDialog dialog = new AlertDialog.Builder(InfoCaptureActivity.this).create();
                ImageView img1 = (ImageView)imgEntryView.findViewById(R.id.large_image);
                img1.setImageBitmap(largeBitmap);
                dialog.setView(imgEntryView); // 自定义dialog
                dialog.show();
                // 点击布局文件（也可以理解为点击大图）后关闭dialog，这里的dialog不需要按钮
                imgEntryView.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View paramView) {
                        dialog.cancel();
                    }
                });
            }
        });
        photoRestartBtn.setVisibility(View.VISIBLE);
    }

    /**
     * 设置视频和相关按钮
     */
    private void setVideo(final String videoType){
        videoToBtn.setVisibility(View.GONE);
        Bitmap bitmap = getVideoThumbnail(videoFilePath);
        //展示视频缩略图
        videoImage.setImageBitmap(bitmap);
        videoImage.setVisibility(View.VISIBLE);
        videoBackground.setVisibility(View.VISIBLE);
        //设置videoBackground点击事件响应,点击播放视频
        videoBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("file://" + videoFilePath);
                //调用系统自带的播放器
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(uri, videoType);
                try{
                    InfoCaptureActivity.this.startActivity(intent);
                }catch (Exception e){
                    Toast.makeText(InfoCaptureActivity.this, "没有默认播放器", Toast.LENGTH_LONG).show();
                }
            }
        });
        videoRestartBtn.setVisibility(View.VISIBLE);
    }

    /**
     * 获取视频缩略图
     * @param filePath
     * @return
     */
    public Bitmap getVideoThumbnail(String filePath) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(filePath);
            bitmap = retriever.getFrameAtTime(0);
        }
        catch(IllegalArgumentException e) {
            e.printStackTrace();
        }
        catch (RuntimeException e) {
            e.printStackTrace();
        }
        finally {
            try {
                retriever.release();
            }
            catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    /**
     * 调用系统的相机拍照
     */
    private void toSystemCreamPhoto(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, SYSTEM_CAMERA_REQUESTCODE_PHOTO);
    }

    /**
     * 调用系统的相机录像
     */
    private void toSystemCreamVideo(){
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        startActivityForResult(intent, SYSTEM_CAMERA_REQUESTCODE_VIDEO);
    }

    /**
     * 调用系统的录音机录音
     */
    private void toSystemCreamRecorder(){
        String fileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        recorderFilePath = fileName + "/audiorecordtest.3gp";

        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(recorderFilePath);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        try {
            mRecorder.prepare();
            mRecorder.start();
        } catch (IOException e) {
            Toast.makeText(InfoCaptureActivity.this, "无法录音！", Toast.LENGTH_LONG).show();
            recorderStop();
        }
    }

    /**
     * 停止录音
     */
    private void recorderStop(){
        //停止录音
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
        recorderToBtn.setVisibility(View.GONE);
        recorderPlayImage.setVisibility(View.VISIBLE);
        recorderRestartBtn.setVisibility(View.VISIBLE);
        isRecorder = false;
        recorderTime = 0;
    }

    /**
     * 设置录音播放
     */
    private void setRecorderPlay(){
        //判断是否正在播放录音
        if (isRecorderPlay){//正在播放
            //停止播放
            recorderPalyStop();
        }else {//没有播放
            //开始播放
            recorderPaly();
        }
        isRecorderPlay = !isRecorderPlay;
    }

    /**
     * 播放录音
     */
    private void recorderPaly(){
        recorderRestartBtn.setVisibility(View.GONE);
        recorderPlayImage.setImageResource(R.drawable.capture_005);
        //判断是开始播放还是继续播放
        if (mPlayer == null){//开始播放
            mPlayer = new MediaPlayer();
            //监听播放进度
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    //播放结束
                    recorderPalyEnd();
                }
            });
            try{
                mPlayer.setDataSource(recorderFilePath);
                mPlayer.prepare();
                mPlayer.start();
            }catch(IOException e){
                Toast.makeText(InfoCaptureActivity.this, "播放失败！", Toast.LENGTH_LONG).show();
                recorderPalyStop();
            }
        }else {//继续播放
            //继续之前的进度播放
            mPlayer.seekTo(mPlayer.getCurrentPosition());
            mPlayer.start();
        }

    }

    /**
     * 停止播放录音
     */
    private void recorderPalyStop(){
        recorderRestartBtn.setVisibility(View.VISIBLE);
        recorderPlayImage.setImageResource(R.drawable.capture_004);
        mPlayer.pause();
    }

    /**
     * 播放结束
     */
    private void recorderPalyEnd(){
        recorderRestartBtn.setVisibility(View.VISIBLE);
        recorderPlayImage.setImageResource(R.drawable.capture_004);
        mPlayer.release();
        mPlayer = null;
        isRecorderPlay = false;
    }

    /**
     * 录音计时
     */
    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Log.i("isRecorderPlay value", recorderTime + "");
            //判断是否处于录音中
            if (isRecorder){//录音中
                //录音计时
                recorderTime++;
                if (recorderTime < 60){
                    recorderTimeTv.setText(recorderTime + "s");
                    handler.postDelayed(this, 1000);
                }else if(recorderTime > 60 && recorderTime <= 300){
                    int m = recorderTime / 60;
                    int s = recorderTime % 60;
                    recorderTimeTv.setText(m + "'" + s + "s");
                    handler.postDelayed(this, 1000);
                }else{
                    recorderStop();
                }
            }
        }
    };

    private void getOherData(){
        String photoJson = null;
        String videoJson = null;
        String recorderJson = null;
        try {
            if (photoBitmap != null) {
                photoJson = Base64Util.bitmapTobase64(photoBitmap).replace("+", "%2B");
            }else {
                photoJson = "";
            }
            if (videoFilePath != null) {
                videoJson = Base64Util.FileTobase64(videoFilePath).replace("+", "%2B");
            }else {
                videoJson = "";
            }
            if (recorderFilePath != null) {
                recorderJson = Base64Util.FileTobase64(recorderFilePath).replace("+", "%2B");
            }else {
                recorderJson = "";
            }
        } catch (Exception e) {
            photoJson = "";
            videoJson = "";
            recorderJson = "";
            e.printStackTrace();
        }
        //组装otherdata
        Map<String,String> map = new HashMap<String,String>() ;
        map.put("picture",photoJson) ;  // 增加内容
        map.put("audio",recorderJson) ;  // 增加内容
        map.put("video",videoJson) ;  // 增加内容
        String otherData = GsonUtil.GsonString(map);
        DataModel.contractSegue.setOtherData(otherData);
    }

    /**
     * 点击拍照按钮
     * @param v 拍照按钮
     */
    public void photoBtnClick(View v) {
        toSystemCreamPhoto();
    }

    /**
     * 点击录像按钮
     * @param v 录像按钮
     */
    public void videoBtnClick(View v) {
        toSystemCreamVideo();
    }

    /**
     * 点击录音按钮
     * @param v 录音按钮
     */
    public void recorderBtnClick(View v) {
        //判断当前状态是否在录音中
        if (isRecorder){//在录音中
            isRecorder = false;
            //停止录音
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
            recorderToBtn.setVisibility(View.GONE);
            recorderPlayImage.setVisibility(View.VISIBLE);
            recorderRestartBtn.setVisibility(View.VISIBLE);
        }else {//不在录音中
            isRecorder = true;
            //判断现在是否在播放录音
            if (mPlayer != null){//在播放录音
                //结束播放
                recorderPalyEnd();
            }
            //开始录音
            recorderToBtn.setText("停止录音");
            recorderToBtn.setVisibility(View.VISIBLE);
            recorderPlayImage.setVisibility(View.GONE);
            recorderRestartBtn.setVisibility(View.GONE);
            toSystemCreamRecorder();
            recorderTime = 0;
            recorderTimeTv.setText(recorderTime + "s");
            handler.postDelayed(runnable, 1000);
        }
    }

    /**
     * 点击下一步按钮
     * @param v 下一步按钮
     */
    public void nextBtnClick(View v) {
        getOherData();
        this.finish();;
    }
}
