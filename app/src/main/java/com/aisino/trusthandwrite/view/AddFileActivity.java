package com.aisino.trusthandwrite.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aisino.trusthandwrite.custom.OpenFileDialog;
import com.aisino.trusthandwrite.custom.TopNavigationView;
import com.aisino.trusthandwrite.data.DataModel;
import com.aisino.trusthandwrite.data.Message;
import com.aisino.trusthandwrite.data.Url;
import com.aisino.trusthandwrite.model.Response;
import com.aisino.trusthandwrite.util.Base64Util;
import com.aisino.trusthandwrite.util.DialogUtil;
import com.aisino.trusthandwrite.util.GsonUtil;
import com.aisino.trusthandwrite.util.HttpUtil;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import com.aisino.trusthandwrite.R;

public class AddFileActivity extends Activity {

    private boolean isSelected = false;
    private String filePath;
    private String fileName;

    private ImageView titleIv;
    private LinearLayout fileLl;
    private TextView fileNmaeTV;
    private Button findBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_file);
        //设置导航栏
        TopNavigationView topNavigationView = (TopNavigationView)findViewById(R.id.af_navigation);
        topNavigationView.setTitleAndButton(TopNavigationView.ADDFILE_ACTIVITY);
        titleIv = (ImageView)findViewById(R.id.af_title);
        fileLl = (LinearLayout)findViewById(R.id.af_file_ll);
        fileNmaeTV = (TextView)findViewById(R.id.af_file_name);
        findBtn = (Button)findViewById(R.id.af_findBtn);
    }

    @Override
    protected Dialog onCreateDialog(final int id) {
        Map<String, Integer> images = new HashMap<String, Integer>();
        // 下面几句设置各文件类型的图标， 需要你先把图标添加到资源文件夹
        images.put(OpenFileDialog.sRoot, R.drawable.filedialog_root);   // 根目录图标
        images.put(OpenFileDialog.sParent, R.drawable.filedialog_folder_up);    //返回上一层的图标
        images.put(OpenFileDialog.sFolder, R.drawable.filedialog_folder);   //文件夹图标
        images.put("doc", R.drawable.filedoc);   //文件图标
        images.put(OpenFileDialog.sEmpty, R.drawable.filedialog_root);
        Dialog dialog = OpenFileDialog.createDialog(id, this, "选择文件", new OpenFileDialog.CallbackBundle() {
                    @Override
                    public void callback(Bundle bundle) {
                        filePath = bundle.getString("path");
                        fileName = bundle.getString("name");
                        setTitle(filePath); // 把文件路径显示在标题上
                        isSelected = true;
                        titleIv.setImageResource(R.drawable.main4_001);
                        fileLl.setVisibility(View.VISIBLE);
                        fileNmaeTV.setText(fileName);
                        findBtn.setText("上传文件");
                    }
                },
                ".doc;",
                images);
        return dialog;
    }

    private void uploadFile(String file){
        if (fileName.indexOf(".") > -1){
            String [] Strings = fileName.split("\\.");
            fileName = Strings[0];
        }
        Map<String, Object> datas = new HashMap<String, Object>();
        datas.put("platformName", DataModel.login.getPlatformName());
        datas.put("clientName", DataModel.login.getClientName());
        datas.put("clientCode", DataModel.login.getAuthCode());
        datas.put("doc", file);
        datas.put("docType", "doc");
        datas.put("customerIds", new Integer[]{-1});
        datas.put("contractName", fileName);
        datas.put("sendType", 2);
        datas.put("type", "doc");
        datas.put("signKeyType", 1);

        Map<String,Object> map = new HashMap<String, Object>();
        map.put("datas", datas);

        String json = GsonUtil.GsonString(map);
        // 定义发送请求的URL
        String url = Url.getUrl(Url.Url_Contract_SendEventContract);
        // 发送请求
        HttpUtil.postRequestTest(url, json, true, new HttpUtil.PostResposeListner() {
            @Override
            public void back(boolean status, String result)  {
                DialogUtil.hiddenWaitingDialog();
                //判断请求是否成功
                if (status){//网络请求成功
                    //解析json获取数据
                    Type type = new TypeToken<Response<Map<String, Object>>>(){}.getType();
                    Response response = GsonUtil.GsonToBean(result, type);
                    //对生成合同操作返回状态值进行判断
                    if (response.getStatus() == 0) {//操作成功
                        double contractId = (double)((Map<String, Object>)response.getDatas()).get("contractId");
                        //记录合同id,签署状态,标记从生成合同界面进入合同展示界面
                        DataModel.contractSegue.setContractId((long)contractId);
                        DataModel.contractSegue.setSign(false);
                        DataModel.isFromRecoed = false;
                        DialogUtil.showSubmitDialog(AddFileActivity.this, Message.getMessage(Message.Message_Contract_Send_Success), new DialogUtil.ButtonClickListner() {
                            @Override
                            public void onClick() {
                                toSignActivity();
                            }
                        });
                    } else {//操作失败
                        String message = response.getMessage();
                        DialogUtil.showSubmitDialog(AddFileActivity.this, message, null);
                    }
                }else {//网络请求失败
                    DialogUtil.showSubmitDialog(AddFileActivity.this, result, null);
                }
            }
        });
    }

    /**
     * 进入签署合同界面
     */
    private void toSignActivity(){
        Intent intent = new Intent();
        intent.setClass(AddFileActivity.this, SignContractActivity.class);
        startActivity(intent);
    }

    /**
     * 点击选择文件按钮
     * @param view 选择文件按钮
     */
    public void findFileBtnClick(View view){
        if (isSelected){
            //显示等待动画
            DialogUtil.showWaitingDialog(AddFileActivity.this,
                    Message.getMessage(Message.Message_File_Send_Loading));
            try {
                String fileData = Base64Util.FileTobase64(filePath);
                fileData = fileData.replace( "+","%2B");
                uploadFile(fileData);
            } catch (Exception e) {
                e.printStackTrace();
                DialogUtil.hiddenWaitingDialog();
                DialogUtil.showSubmitDialog(AddFileActivity.this, Message.getMessage(Message.Message_File_Change_Error), null);
            }

        }else {
            showDialog(0);
        }
    }
}
