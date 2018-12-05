package com.aisino.trusthandwrite.view;

import android.app.Activity;
import android.os.Bundle;

import com.aisino.trusthandwrite.data.Url;
import com.aisino.trusthandwrite.custom.BottomMenuView;
import com.aisino.trusthandwrite.custom.PulmListView;
import com.aisino.trusthandwrite.adapter.RecordListViewAdapter;
import com.aisino.trusthandwrite.custom.RefreshableView;
import com.aisino.trusthandwrite.custom.TopNavigationView;
import com.aisino.trusthandwrite.model.Contract;
import com.aisino.trusthandwrite.model.PageInfo;
import com.aisino.trusthandwrite.model.Response;
import com.aisino.trusthandwrite.util.DialogUtil;
import com.aisino.trusthandwrite.util.GsonUtil;
import com.aisino.trusthandwrite.util.HttpUtil;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aisino.trusthandwrite.R;

public class RecordActivity extends Activity {

    //请求数据的2种情况，分别对应刷新数据，加载更多数据
    private final int REFRESH_DATA = 1;
    private final int MORE_DATA = 2;

    private PulmListView listView = null;
    private RecordListViewAdapter recordListViewAdapter = null;
    private RefreshableView refreshableView;
    private List<Contract> recordList = new ArrayList<Contract>();
    private int pageNum = 0;
    private int pageSize = 10;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        //设置默认值
        setDefalut();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //请求list
        getContractRecordList(REFRESH_DATA);
    }

    /**
     * 设置默认值
     */
    private void setDefalut(){
        //bind refreshableview
        refreshableView = (RefreshableView) findViewById(R.id.refreshable_view);
        refreshableView.setOnRefreshListener(new RefreshableView.PullToRefreshListener() {
            @Override
            public void onRefresh() {
                //请求list
                getContractRecordList(REFRESH_DATA);
            }
        }, RefreshableView.RecordActivityPullToRefreshListener);
        //绑定listView
        listView = (PulmListView)findViewById(R.id.recordlist);
        recordListViewAdapter = new RecordListViewAdapter(RecordActivity.this, new ArrayList<Contract>());
        listView.setDivider(null);//去分割线
        listView.setAdapter(recordListViewAdapter);
        listView.setOnPullUpLoadMoreListener(new PulmListView.OnPullUpLoadMoreListener() {
            @Override
            public void onPullUpLoadMore() {
                //实现加载更多
                getContractRecordList(MORE_DATA);
            }
        });
        //设置导航栏
        TopNavigationView topNavigationView = (TopNavigationView)findViewById(R.id.record_navigation);
        topNavigationView.setTitleAndButton(TopNavigationView.RECORD_ACTIVITY);
        //设置底部菜单项
        BottomMenuView bottomMenuView = (BottomMenuView)this.findViewById(R.id.record_bottom_bm);
        bottomMenuView.setMenu(BottomMenuView.RECORD_ACTIVITY);
    }

    /**
     * 封装参数，发起请求合同的历史记录列表
     */
    private void getContractRecordList(final int line){
        if (line == REFRESH_DATA){
            pageNum = 1;
        }else{
            pageNum ++;
        }
        // 使用Map封装请求参数
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("pageNum", pageNum);
        map.put("pageSize",pageSize);
        String json = GsonUtil.GsonString(map);
        // 定义发送请求的URL
        String url = Url.getUrl(Url.Url_Contract_GetHistory);
        //发起请求
        HttpUtil.postRequestTest(url, json, true, new HttpUtil.PostResposeListner() {
            @Override
            public void back(boolean status, String result) {
                //判断请求是否成功
                if (status) {//网络请求成功
                    //解析json获取数据
                    Type type = new TypeToken<Response<Map<String,PageInfo<Contract>>>>(){}.getType();
                    Response response = GsonUtil.GsonToBean(result, type);
                    //对请求合同的历史记录列表操作返回状态值进行判断
                    if (response.getStatus() == 0) {//操作成功
                        PageInfo<Contract> pageInfo = ((Map<String,PageInfo<Contract>>)response.getDatas()).get("pageInfo");
                        List<Contract> list = pageInfo.getList();
                        if (line == REFRESH_DATA){
                            recordList = list;
                            //刷新结束
                            refreshableView.finishRefreshing();
                        }else if (line == MORE_DATA){
                            recordList.addAll(list);
                            //加载更多结束
                            listView.onFinishLoading((pageInfo.getPageNum() == pageInfo.getPages())? true: false);
                        }
                        //更新数据，刷新界面
                        recordListViewAdapter.refresh(recordList);
                        listView.invalidateViews();
                    } else {//操作失败
                        String message = response.getMessage();
                        getDataEnd(line);
                        DialogUtil.showSubmitDialog(RecordActivity.this, message, null);
                    }
                }else {//网络请求失败
                    getDataEnd(line);
                    DialogUtil.showSubmitDialog(RecordActivity.this, result, null);
                }
            }
        });
    }

    /**
     * 结束加载数据
     * @param line 一个标志，区分刷新还是加载更多
     */
    private void getDataEnd(int line){
        if (line == REFRESH_DATA){
            //刷新结束
            refreshableView.finishRefreshing();
        }else if (line == MORE_DATA){
            //加载更多结束
            listView.onFinishLoading(false);
        }
    }
}
