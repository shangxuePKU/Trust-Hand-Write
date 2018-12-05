package com.aisino.trusthandwrite.adapter;


import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aisino.trusthandwrite.model.Template;
import com.aisino.trusthandwrite.model.TemplateContent;
import com.aisino.trusthandwrite.R;

import java.util.List;
import java.util.Map;

/**
 * Created by HXQ on 2017/5/22.
 */

public class WriteTemplateAdapter extends BaseAdapter{

    private Context context;
    private List<TemplateContent> list;
    //map存放模板内容，用于存储数据，便于转换为json
    Map<String, String> contentMap;
    private Integer selectItem = -1;

    public WriteTemplateAdapter(Context context, List<TemplateContent> list, Map<String, String> contentMap){
        this.context = context;
        this.list = list;
        this.contentMap = contentMap;
    }
    public void refresh(List<TemplateContent> list){
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount(){
        return  list.size();
    }

    @Override
    public Object getItem(int position){
        return list.get(position);
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    public Map<String, String> getContentMap() {
        return contentMap;
    }

    @SuppressLint("ResourceAsDrawable")
    @Override
    public View getView (final int position, View convertView, ViewGroup parent){
        ViewHolder viewHolder = null;
        if(convertView == null && list.size() != 0){
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView =inflater.inflate(R.layout.item_write_template,null);
            viewHolder.itemTextView = (TextView) convertView.findViewById(R.id.item_wt_tv);
            viewHolder.itemEditText = (EditText) convertView.findViewById(R.id.item_wt_et);
            convertView.setTag(viewHolder);
        } else {
            //复用listview给的view
            viewHolder = (ViewHolder)convertView.getTag();
        }
        //获取对应的数据
        TemplateContent templateContent = list.get(position);
        //把bean与输入框绑定
        viewHolder.itemEditText.setTag(templateContent);
        //设置TextView的文本
        viewHolder.itemTextView.setText(templateContent.getKey() + ":");
        Log.i("key", templateContent.getKey());
        Log.i("value", contentMap.get(templateContent.getKey()) + "!");

        //判断itemEditText用户是否输入信息
        if(contentMap.get(templateContent.getKey()).equals("")){//没有输入信息
            viewHolder.itemEditText.setText("");
            Log.i("没有输入信息", "显示Hint");
            //设置Hint
            if(templateContent.getValue() != null){
                viewHolder.itemEditText.setHint(templateContent.getValue());
            }else {
                viewHolder.itemEditText.setHint("");
            }
        }else {//输入信息
            Log.i("有输入信息", "显示"+contentMap.get(templateContent.getKey()) + "!");
            viewHolder.itemEditText.setText(contentMap.get(templateContent.getKey()));
        }


        // 根据手指触碰的位置，获取当前EditText的位置；
        viewHolder.itemEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // 1.解决第4个问题(不可滑动的问题)
//                    v.getParent().requestDisallowInterceptTouchEvent(true);
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    selectItem = position;
                }
                return false;
            }
        });

        final ViewHolder finalViewHolder = viewHolder;
        viewHolder.itemEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //获取editext所在position里面的map,并设置数据
                TemplateContent templateContent = (TemplateContent) finalViewHolder.itemEditText.getTag();
                //templateContent.setValue(charSequence+"");
                contentMap.put(templateContent.getKey(), charSequence+"");
                Log.i("in onTextChanged CharSequence", charSequence +"");
                Log.i("in onTextChanged key", templateContent.getKey());
                Log.i("in onTextChanged value", contentMap.get(templateContent.getKey()));
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });


        return convertView;
    }

    private class ViewHolder{
        private TextView itemTextView;
        private EditText itemEditText;
    }
}
