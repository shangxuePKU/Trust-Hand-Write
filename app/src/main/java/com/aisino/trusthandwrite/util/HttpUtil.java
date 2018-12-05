package com.aisino.trusthandwrite.util;

/**
 * Created by shangxue on 16/8/26.
 */

import android.os.Handler;
import android.util.Log;

import com.aisino.trusthandwrite.data.DataModel;
import com.aisino.trusthandwrite.data.Message;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpUtil {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    //postRequestTest 中handle的message的 what的可能值，表示请求为为失败的情况
    private static final int FAILURE = 0;
    //postRequestTest 中handle的message的 what的可能值，表示请求为成功的情况
    private static final int SUCCESS = 1;

    //读取超时时间
    private static final int READ_TIME_OUT = 30;
    //链接超时时间
    private static final int CONNECT_TIME_OUT = 60;
    //写入超时时间
    private static final int WRITE_TIME_OUT = 60;

    /**
     * 不带token头的网络请求
     * @param url 网络请求的地址
     * @param json 网络请求的参数，json字符串格式
     * @param listner 回调接口
     * @throws Exception
     */
    public static void postRequestTest(String url, String json, Boolean haveTokenHeard, final PostResposeListner listner){
        Log.i("url:",url);
        Log.i("json:",(json = (json == null? "": json)));
        //处理请求返回数据
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(android.os.Message message) {
                super.handleMessage(message);
                switch (message.what) {
                    case SUCCESS:
                        listner.back(true, (String) message.obj);
                        break;
                    case FAILURE:
                        listner.back(false,(String) message.obj);
                        break;
                }
            }
        };

        //构造请求
        //初始化OkHttpClient对象并设置超时时间
        OkHttpClient client = new OkHttpClient().newBuilder().
                readTimeout(READ_TIME_OUT, TimeUnit.SECONDS).
                connectTimeout(CONNECT_TIME_OUT, TimeUnit.SECONDS).
                writeTimeout(WRITE_TIME_OUT, TimeUnit.SECONDS)
                .build();

        //根据url初始化请求Builder
        Request.Builder builder = new Request.Builder().url(url);

        //判断是否需要添加token头
        if (haveTokenHeard){//需要添加token头
            builder.addHeader("token", DataModel.token);
        }

        //根据参数初始化请求体
        RequestBody body = RequestBody.create(JSON, json);

        //初始化请求对象
        Request request = builder
                .post(body)
                .build();

        //发起请求
        client.newCall(request).enqueue(new Callback(){
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("post onFailure",e.toString());
                //该方法在子线程中，需要将数据通过Handler传递到主线程中
                android.os.Message message = new android.os.Message();
                message.obj = Message.getMessage(Message.Message_Connect_Unkown);
                message.what = FAILURE;

                handler.sendMessage(message);
            }

            @Override
            public void onResponse(Call call, Response response)  {

                String result = null;
                //该方法在子线程中，需要将数据通过Handler传递到主线程中
                android.os.Message message = new android.os.Message();
                try {
                    result = response.body().string();
                    Log.i("post onResponse",result);
                    message.obj = result;
                    message.what = SUCCESS;
                } catch (IOException e) {
                    e.printStackTrace();
                    message.obj = Message.getMessage(Message.Message_Connect_Unkown);
                    message.what = FAILURE;
                }
                handler.sendMessage(message);
            }
        });

    }

    //回调
    public interface PostResposeListner{
        void back(boolean status, String result);
    }
}
