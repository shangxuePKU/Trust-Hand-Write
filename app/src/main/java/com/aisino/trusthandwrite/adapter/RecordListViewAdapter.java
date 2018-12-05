package com.aisino.trusthandwrite.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aisino.trusthandwrite.data.DataModel;
import com.aisino.trusthandwrite.model.Contract;
import com.aisino.trusthandwrite.R;
import com.aisino.trusthandwrite.view.SignContractActivity;

import java.util.List;

public class RecordListViewAdapter extends BaseAdapter {
    private Context context;
    private List<Contract> list;
    private int selectItem = -1;

    public RecordListViewAdapter(Context context, List<Contract> maps) {
        this.context = context;
        this.list = maps;
    }

    public void refresh(List<Contract> maps) {
        this.list = maps;
        notifyDataSetChanged();
    }

    public void addMoreItems(List<Contract> newItems, boolean isFirstLoad) {
        if (isFirstLoad) {
            this.list.clear();
        }
        this.list.addAll(newItems);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setSelectItem(int selectItem) {
        this.selectItem = selectItem;
    }

    @SuppressLint("ResourceAsDrawable")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null && list.size() != 0) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.item_record_list, parent, false);
            viewHolder.itemNameTextView = (TextView) convertView.findViewById(R.id.record_name);
            viewHolder.itemTimeTextView = (TextView) convertView.findViewById(R.id.record_time);
            viewHolder.itemSignExistImageView = (ImageView) convertView.findViewById(R.id.sign_exist);
            viewHolder.itemRocordImgBtn = (ImageButton) convertView.findViewById(R.id.record_look_btn);
            viewHolder.itemRelativeLayout = (RelativeLayout) convertView.findViewById(R.id.RelativeLayout_record);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        //填充数据
        final Contract contract = list.get(position);
        viewHolder.itemNameTextView.setText(contract.getName());
        viewHolder.itemTimeTextView.setText(contract.getLastOpTime());
        //判断是否已签署，status为6表示已签
        if (contract.getStatus() == 6) {//已签
            viewHolder.itemSignExistImageView.setImageResource(R.drawable.history_001);
        } else {//未签
            viewHolder.itemSignExistImageView.setImageResource(R.drawable.history_002);
        }
        //监听点击事件
        viewHolder.itemRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //记录点击的合同id,签署状态,标记从历史记录界面进入合同展示界面
                DataModel.contractSegue.setContractId(contract.getContractId());
                DataModel.contractSegue.setSign((contract.getStatus() == 6));
                DataModel.isFromRecoed = true;
                //跳转到合同展示界面
                Intent intent = new Intent(context, SignContractActivity.class);
                context.startActivity(intent);
            }
        });
        return convertView;
    }

    private class ViewHolder {
        private TextView itemNameTextView;
        private TextView itemTimeTextView;
        private ImageView itemSignExistImageView;
        private ImageButton itemRocordImgBtn;
        private RelativeLayout itemRelativeLayout;
    }
}



