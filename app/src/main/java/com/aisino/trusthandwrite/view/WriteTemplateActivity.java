package com.aisino.trusthandwrite.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.aisino.trusthandwrite.data.DataModel;
import com.aisino.trusthandwrite.custom.TopNavigationView;
import com.aisino.trusthandwrite.adapter.WriteTemplateAdapter;
import com.aisino.trusthandwrite.model.TemplateContent;
import com.aisino.trusthandwrite.util.GsonUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.aisino.trusthandwrite.R;

/**
 * Created by HXQ on 2017/5/22.
 */

public class WriteTemplateActivity extends Activity {

    private ListView listView = null;
    private WriteTemplateAdapter writeTemplateAdapter = null;
    private ProgressBar progressBar;

    //map存放模板内容，用于存储数据，便于转换为json
    Map<String, String> contentMap;
    //list存放模板内容，用于显示到界面
    private List<TemplateContent> contentList = new ArrayList<TemplateContent>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_template);

        //获取模板内容
        getContent();
        //设置默认值
        setDefalut();
//        //刷新数据
//        writeTemplateAdapter.refresh(contentList);
//        listView.invalidateViews();
    }

    /**
     * 设置默认值
     */
    private void setDefalut() {
        //设置导航栏
        TopNavigationView topNavigationView = (TopNavigationView)findViewById(R.id.wt_navigation);
        topNavigationView.setTitleAndButton(TopNavigationView.WRITETEMPLATE_ACTIVITY);
        //bind view
        listView = (ListView)findViewById(R.id.wt_lv);

        //填充adapter
        writeTemplateAdapter = new WriteTemplateAdapter(this, contentList, contentMap);
        listView.setDivider(null);//去分割线
        listView.setAdapter(writeTemplateAdapter);

        //设置滑动处理
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
                switch (i){
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE://当停止滚动时
                        break;

                    case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL://滚动时
                        HideKeyBoard();
                        break;

                    case AbsListView.OnScrollListener.SCROLL_STATE_FLING://手指抬起，但是屏幕还在滚动状态
                        break;

                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        });
    }

    /**
     * 获取内容列表
     */
    private void getContent(){
        //json转为map
        contentMap = GsonUtil.GsonToMaps(DataModel.selectedTemplate.getDescr());
        //判断map是否为空
        if (contentMap != null){//不为空
            for (Map.Entry<String, String> entry : contentMap.entrySet()){
                TemplateContent templateContent = new TemplateContent();
                templateContent.setKey(entry.getKey());
                templateContent.setValue(entry.getValue());
                contentMap.put(entry.getKey(),"");
                contentList.add(templateContent);
            }
        }
    }

    /**
     * 隐藏软键盘
     */
    private void HideKeyBoard(){
        ((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(WriteTemplateActivity.this.getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 下一步按钮的点击响应
     * @param view 下一步按钮
     */
    public void nextBtnClick(View view){
        //得到用户填写以后的模板内容的json格式
        contentMap = writeTemplateAdapter.getContentMap();
        DataModel.selectedTemplate.setDescr(GsonUtil.GsonString(contentMap));
        Log.d("content:",  DataModel.selectedTemplate.getDescr() );

        Intent intent = new Intent(WriteTemplateActivity.this, BuildContractActivity.class);
        startActivity(intent);
    }
}
