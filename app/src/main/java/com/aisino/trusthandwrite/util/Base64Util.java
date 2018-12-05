package com.aisino.trusthandwrite.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import Decoder.BASE64Decoder;
import Decoder.BASE64Encoder;

public class Base64Util {
	
	/**
     * TODO:将byte数组以Base64方式编码为字符串
     * @param bytes 待编码的byte数组
     * @return 编码后的字符串
     * */
    public static String encode(byte[] bytes){
        return new BASE64Encoder().encode(bytes);
    }
    
    /**
     * TODO:将以Base64方式编码的字符串解码为byte数组
     * @param encodeStr 待解码的字符串
     * @return 解码后的byte数组
     * @throws IOException
     * */
    public static byte[] decode(String encodeStr) throws IOException {
        byte[] bt = null;  
        BASE64Decoder decoder = new BASE64Decoder();
        bt = decoder.decodeBuffer(encodeStr);
        return bt;
    }
    /**
     * 图片转成string
     *
     * @param bitmap
     * @return
     */
    public static String bitmapTobase64(Bitmap bitmap)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();// outputstream
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] appicon = baos.toByteArray();// 转为byte数组
        Log.v("手签图片长度",""+appicon.length);
        return Base64.encodeToString(appicon, Base64.DEFAULT);

    }

    /**
     * base64转为bitmap
     * @param base64Data
     * @return
     */
    public static Bitmap base64ToBitmap(String base64Data) {
        byte[] bytes = new byte[0];
        try {
            bytes = Base64Util.decode(base64Data);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    public static String FileTobase64(String path) throws Exception {

        File file = new File(path);
        FileInputStream inputFile = new FileInputStream(file);
        byte[] buffer = new byte[(int) file.length()];
        inputFile.read(buffer);
        inputFile.close();
        return Base64.encodeToString(buffer, Base64.DEFAULT);
    }
}
