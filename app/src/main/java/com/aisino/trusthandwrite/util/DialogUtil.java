package com.aisino.trusthandwrite.util;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;


/**
 * Created by shangxue on 16/9/19.
 */

public class DialogUtil {

    //回调接口
    public static interface ButtonClickListner{
        public void onClick() ;
    }

    //等待提示框对象
    private static ProgressDialog waitingDialog;

    /**
     * 显示带有确定按钮的提示框
     * @param context 初始化提示框的值
     * @param message 提示内容
     * @param clickListner 确定按钮点击事件的回调函数
     */
    public static void showSubmitDialog(Context context, String message, final ButtonClickListner clickListner){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(message);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (clickListner != null){
                    clickListner.onClick();
                }
            }
        });
        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * 显示等待提示框
     * @param context 初始化提示框的值
     * @param message 提示框挑剔
     */
    public static void showWaitingDialog(Context context, String message) {
        waitingDialog = new ProgressDialog(context);
        //waitingDialog.setTitle(message);
        waitingDialog.setMessage(message);
        waitingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        waitingDialog.setIndeterminate(false);
        waitingDialog.setCancelable(false);
        waitingDialog.show();
    }

    /**
     * 隐藏等待提示框
     */
    public static void hiddenWaitingDialog(){
        if (waitingDialog != null){
            waitingDialog.hide();
        }
    }

    /**
     * 关闭等待提示框
     */
    public static void dismissWaitingDialog(){
        if (waitingDialog != null){
            waitingDialog.dismiss();
        }
    }
}