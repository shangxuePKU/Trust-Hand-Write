package com.aisino.trusthandwrite.data;

/**
 * Created by HXQ on 2017/4/24.
 */

public class Url {

    public static final String BASE_URL = "http://192.168.3.117:9000/e-contract/mobileapi/";
    public static final String WEB_URL = "http://192.168.3.117:9000/e-contract/webapi/";

    //登录
    public final static int Url_Login_LoginByCode = 0;
    public final static int Url_Login_Logout = 1;
    public final static int Url_Login_CheckToken = 2;
    //合同
    public final static int Url_Contract_GetContract = 3;
    public final static int Url_Contract_GetContractByCode = 4;
    public final static int Url_Contract_GetImage = 5;
    public final static int Url_Contract_GetImageByCode = 6;
    public final static int Url_Contract_SignEventContract = 7;
    public final static int Url_Contract_GetHistory = 8;
    public final static int Url_Contract_SendContract = 9;
    public final static int Url_Contract_SendEventContract= 10;
    //模板
    public final static int Url_Template_List = 11;
    public final static int Url_Template_Content = 12;

    private static String[] urls;

    static {
        urls = new String[256];
        urls[Url_Login_LoginByCode] = "login/loginByCode";
        urls[Url_Login_Logout] = "login/logout";
        urls[Url_Login_CheckToken] = "login/checkToken";
        urls[Url_Contract_GetContract] = "contract/getContract";
        urls[Url_Contract_GetImage] = "contract/getImage";
        urls[Url_Contract_SignEventContract] = "contract/signEventContract";
        urls[Url_Template_List] = "template/list";
        urls[Url_Template_Content] = "template/content";
        urls[Url_Contract_GetHistory] = "contract/getHistory";
        urls[Url_Contract_SendContract] = "contract/sendContract";
        urls[Url_Contract_GetContractByCode] = "contract/getContractByCode";
        urls[Url_Contract_GetImageByCode] = "contract/getImageByCode";
        urls[Url_Contract_SendEventContract] = "sendEventContract";
    }

    public static String getUrl(int index){
        if(urls[index] == null)
            return BASE_URL;
        String url;
        if (index == Url_Contract_SendEventContract){
            url = WEB_URL + urls[index];
        } else {
            url = BASE_URL + urls[index];
        }
        return url;
    }
}
