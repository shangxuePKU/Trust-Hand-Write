package com.aisino.trusthandwrite.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;


import com.aisino.trusthandwrite.data.DataModel;
import com.aisino.trusthandwrite.data.Url;
import com.aisino.trusthandwrite.adapter.ChooseTemplateAdapter;
import com.aisino.trusthandwrite.custom.TopNavigationView;
import com.aisino.trusthandwrite.model.PageInfo;
import com.aisino.trusthandwrite.model.Response;
import com.aisino.trusthandwrite.model.Template;
import com.aisino.trusthandwrite.util.DialogUtil;
import com.aisino.trusthandwrite.util.GsonUtil;
import com.aisino.trusthandwrite.util.HttpUtil;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.aisino.trusthandwrite.R;

/**
 * Created by HXQ on 2017/5/22.
 */

public class ChooseTemplateActivity extends Activity{

    private ListView listView = null;
    private ChooseTemplateAdapter chooseTemplateadapter = null;
    private ProgressBar progressBar;
    private Button nextBtn;

    //已查询的模板列表
    private List<Template> templateList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_template);
        //设置默认值
        setDefalut();
        //获取模板数据
        getTemplateList();
        //刷新数据
//        customListViewAdapter.refresh(templateList);
//        listView.invalidateViews();
    }

    /**
     * 设置默认值
     */
    private void setDefalut() {
        //设置导航栏
        TopNavigationView topNavigationView = (TopNavigationView)findViewById(R.id.ct_navigation);
        topNavigationView.setTitleAndButton(TopNavigationView.CHOOSE_TEMPLATE_ACTIVITY);
        //bind view
        listView = (ListView)findViewById(R.id.ct_lv);
        //bind progressBar
        progressBar = (ProgressBar) findViewById(R.id.ct_pro) ;
        progressBar.setVisibility(View.VISIBLE);
        //填充adapter
        chooseTemplateadapter = new ChooseTemplateAdapter(this, templateList);
        listView.setDivider(null);//去分割线
        listView.setAdapter(chooseTemplateadapter);
        //下一步按钮默认为不可点击状态
        nextBtn = (Button) findViewById(R.id.ct_nextBtn);
        nextBtn.setEnabled(false);
        //为listview的列表项单击事件绑定事件监听器
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            //第position项被单击时激发该方法
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view,
                                    int position, long id) {
                //将当前选择的模板记录
                DataModel.selectedTemplate = templateList.get(position);
                //设置当前选择模板的位置
                chooseTemplateadapter.setSelectItem(position);
                chooseTemplateadapter.notifyDataSetInvalidated();
                //判断按钮是否可点击
                if (!nextBtn.isEnabled()){//不可点击状态
                    //设置为可以点击
                    nextBtn.setEnabled(true);
                    nextBtn.setBackgroundResource(R.drawable.login1_001);
                }
            }
        });
    }

    /**
     * 获取模板列表
     */
    private void getTemplateList(){
        // 定义发送请求的URL
        String url = Url.getUrl(Url.Url_Template_List);
        //发起请求
        HttpUtil.postRequestTest(url, null, true, new HttpUtil.PostResposeListner() {
            @Override
            public void back(boolean status, String result)  {
                //隐藏等待控件
                progressBar.setVisibility(View.GONE);
                //判断请求是否成功
                if (status) {//网络请求成功
                    //解析json获取数据
                    Type type = new TypeToken<Response<Map<String,PageInfo<Template>>>>(){}.getType();
                    Response response = GsonUtil.GsonToBean(result, type);
                    //对获取模板列表操作返回状态值进行判断
                    if (response.getStatus() == 0) {//操作成功
                        templateList = ((Map<String,PageInfo<Template>>)response.getDatas()).get("pageInfo").getList();
                        //刷新界面
                        chooseTemplateadapter.refresh(templateList);
                        listView.invalidateViews();
                    } else {//操作失败
                        String message = response.getMessage();
                        DialogUtil.showSubmitDialog(ChooseTemplateActivity.this, message, null);
                    }
                }else{//网络请求失败
                    DialogUtil.showSubmitDialog(ChooseTemplateActivity.this, result, null);
                }
            }
        });
    }

    /**
     * 下一步按钮的点击响应
     * @param view 下一步按钮
     */
    public void nextBtnClick (View view){
        Intent intent = new Intent (ChooseTemplateActivity.this, WriteTemplateActivity.class);
        startActivity(intent);
    }
}
