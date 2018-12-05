package com.aisino.trusthandwrite.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aisino.trusthandwrite.model.Template;
import com.aisino.trusthandwrite.R;

import java.util.List;

/**
 * Created by HXQ on 2017/5/22.
 */

public class ChooseTemplateAdapter extends BaseAdapter {

    private Context context;
    private List<Template> list;
    private int selectItem = -1;

    public ChooseTemplateAdapter(Context context, List<Template> maps){
        this.context = context;
        this.list = maps;
    }

    public void refresh(List<Template> maps){
        this.list = maps;
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

    public void setSelectItem(int selectItem){
        this.selectItem = selectItem;
    }

    @SuppressLint("ResourceAsDrawable")
    @Override
    public View getView (int position, View convertView, ViewGroup parent){

        ViewHolder viewHolder = null;

        if(convertView == null && list.size() != 0){
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView =inflater.inflate(R.layout.item_chosse_template,null);
            viewHolder.itemTextView = (TextView) convertView.findViewById(R.id.item_ct_tv);
            viewHolder.relativeLayout = (RelativeLayout) convertView.findViewById(R.id.item_ct_rl);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }

        Template template = list.get(position);
        viewHolder.itemTextView.setText(template.getName());

        //select many times
        if (selectItem == position){
            viewHolder.itemTextView.setSelected(true);
            viewHolder.itemTextView.setPressed(true);
            viewHolder.relativeLayout.setBackgroundResource(R.drawable.main5_002);
        } else {
            viewHolder.itemTextView.setSelected(false);
            viewHolder.itemTextView.setPressed(false);
            viewHolder.relativeLayout.setBackgroundResource(R.drawable.main5_003);
        }

        return convertView;
    }

    private class ViewHolder{
        private TextView itemTextView;
        private RelativeLayout relativeLayout;
    }
}
