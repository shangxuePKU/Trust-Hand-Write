package com.aisino.trusthandwrite.data;

/**
 * Created by shangxue on 16/8/26.
 */

public class Message {

    public final static int Message_Connect_Unkown = 0;
    public final static int Message_Login_Loading = 1;
    public final static int Message_Login_Expire = 2;
    public final static int Message_Login_Exception = 3;
    public final static int Message_Logout_Loading = 4;
    public final static int Message_Error_Unkown = 5;
    public final static int Message_Contract_Send_Loading = 6;
    public final static int Message_Contract_Send_Success = 7;
    public final static int Message_Contract_Get_Loading = 8;
    public final static int Message_Contract_Sign_Loading = 9;
    public final static int Message_Contract_Sign_Success = 10;
    public final static int Message_File_Send_Loading = 11;
    public final static int Message_File_Change_Error = 12;
    public final static int Message_Contract_Sign_Capture = 13;
    public final static int Message_Contract_Other_Sign_Success = 14;
    public final static int Message_Other_App_Not_Found = 15;

    private static String[] messages;

    static {
        messages = new String[256];
        messages[Message_Connect_Unkown] = "服务器无响应";
        messages[Message_Login_Loading] = "正在登录...";
        messages[Message_Login_Expire] = "登陆已过期，跳转至登陆界面重新登陆";
        messages[Message_Login_Exception] = "登陆异常，请重新登陆";
        messages[Message_Logout_Loading] = "正在退出...";
        messages[Message_Error_Unkown] = "程序异常";
        messages[Message_Contract_Send_Loading] = "生成合同中...";
        messages[Message_Contract_Send_Success] = "生成合同成功，点击\"确定\"后跳转至合同签署";
        messages[Message_Contract_Get_Loading] = "合同加载中...";
        messages[Message_Contract_Sign_Loading] = "合同签署中...";
        messages[Message_Contract_Sign_Success] = "合同签署成功，点击\"确定\"后跳转至历史记录";
        messages[Message_File_Send_Loading] = "文件上传中...";
        messages[Message_File_Change_Error] = "获取文件数据失败！";
        messages[Message_Contract_Sign_Capture] = "信息采集功能已开启，点击\"确定\"后跳转至信息采集";
        messages[Message_Contract_Other_Sign_Success] = "合同签署成功，点击\"确定\"后跳回至来源APP";
        messages[Message_Other_App_Not_Found] = "来源APP传递返回路径错误，请联系来源APP";
    }

    public static String getMessage(int index){
        if(messages[index] == null)
            return messages[Message_Connect_Unkown];

        return messages[index];
    }
}

