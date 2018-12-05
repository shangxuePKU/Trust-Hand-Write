package com.aisino.trusthandwrite.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.aisino.trusthandwrite.data.DataModel;
import com.aisino.trusthandwrite.data.Message;
import com.aisino.trusthandwrite.data.Url;
import com.aisino.trusthandwrite.custom.TopNavigationView;
import com.aisino.trusthandwrite.model.Response;
import com.aisino.trusthandwrite.util.DialogUtil;
import com.aisino.trusthandwrite.util.GsonUtil;
import com.aisino.trusthandwrite.util.HttpUtil;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import com.aisino.trusthandwrite.R;

/**
 * Created by HXQ on 2017/5/23.
 */

public class BuildContractActivity extends Activity {

    private TextView tvEndTime;
    private EditText etContractName;

    final Integer sendType = 2;
    final Integer eventSignCount = 1;
    final Integer isAutoSign = 0;
    final Integer[] receiver = new Integer[]{-1};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_build_contract);

        //设置默认值
        setDefalut();
    }

    /**
     * 设置默认值
     */
    public void setDefalut(){
        //设置导航栏
        TopNavigationView topNavigationView = (TopNavigationView)findViewById(R.id.bc_navigation);
        topNavigationView.setTitleAndButton(TopNavigationView.BUILDCONTRACT_ACTIVITY);

        etContractName = (EditText)this.findViewById(R.id.bc_name_et);
        tvEndTime = (TextView)findViewById(R.id.bc_end_time_tv);
        //设置当前日期为默认过期日期
        Calendar calendar = getNowDateCalendar();
        StringBuffer sb = new StringBuffer();
        sb.append(String.format("%d-%02d-%02d",
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH)));
        String defalutDateTime = sb + " 23:59:59";
        tvEndTime.setText(defalutDateTime);
        //设置过期时间显示控件的点击事件
        tvEndTime.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    AlertDialog.Builder builder = new AlertDialog.Builder(BuildContractActivity.this);
                    View view1 = View.inflate(BuildContractActivity.this, R.layout.date_time_dialog, null);
                    //显示与选择日期与时间的控件
                    final DatePicker datePicker = (DatePicker) view1.findViewById(R.id.date_picker);

                    builder.setView(view1);

                    Calendar calendar = getNowDateCalendar();

                    //将当前日期与时间显示添加到控件显示
                    datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH)
                            , calendar.get(Calendar.DAY_OF_MONTH), null);

                    //日期时间选择视图添加标题与确定按钮
                    builder.setTitle("选取有效日期");
                    builder.setPositiveButton("确  定", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //初始化
                            StringBuffer sb = new StringBuffer();
                            sb.append(String.format("%d-%02d-%02d",
                                    datePicker.getYear(),
                                    datePicker.getMonth() + 1,
                                    datePicker.getDayOfMonth()));
                            String defalutDateTime = sb + " 23:59:59";
                            tvEndTime.setText(defalutDateTime);

                            dialog.cancel();
                        }
                    });

                    Dialog dialog = builder.create();
                    dialog.show();
                }
                return false;
            }
        });
    }

    /**
     * 得到当前时间的Calendar对象
     * @return Calendar对象
     */
    private Calendar getNowDateCalendar(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        return calendar;
    }

    /**
     * 封装参数，发起生成合同请求
     */
    private void uploadContratct(){
        // 使用Map封装请求参数
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("templateId", String.valueOf(DataModel.selectedTemplate.getCustomerTemplateId()));
        map.put("content", DataModel.selectedTemplate.getDescr());
        map.put("name", etContractName.getText().toString());
        map.put("expireTime", tvEndTime.getText().toString());
        map.put("sendType", Integer.toString(sendType));
        map.put("descr", "");
        map.put("eventSignCount", Integer.toString(eventSignCount));
        map.put("isAutoSign", Integer.toString(isAutoSign));
        map.put("receiver",receiver);
        String json = GsonUtil.GsonString(map);
        // 定义发送请求的URL
        String url = Url.getUrl(Url.Url_Contract_SendContract);
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
                        DialogUtil.showSubmitDialog(BuildContractActivity.this, Message.getMessage(Message.Message_Contract_Send_Success), new DialogUtil.ButtonClickListner() {
                            @Override
                            public void onClick() {
                                toSignActivity();
                            }
                        });
                    } else {//操作失败
                        String message = response.getMessage();
                        DialogUtil.showSubmitDialog(BuildContractActivity.this, message, null);
                    }
                }else {//网络请求失败
                    DialogUtil.showSubmitDialog(BuildContractActivity.this, result, null);
                }
            }
        });
    }

    /**
     * 进入签署合同界面
     */
    private void toSignActivity(){
        Intent intent = new Intent();
        intent.setClass(BuildContractActivity.this, SignContractActivity.class);
        startActivity(intent);
    }

    /**
     * 生成合同点击按钮
     * @param view 生成合同按钮
     */
    public void createContractBtnClick(View view){
        //显示等待动画
        DialogUtil.showWaitingDialog(BuildContractActivity.this,
                Message.getMessage(Message.Message_Contract_Send_Loading));
        //生成合同
        uploadContratct();
    }
}
