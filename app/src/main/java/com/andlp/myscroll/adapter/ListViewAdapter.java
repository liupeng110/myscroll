package com.andlp.myscroll.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.andlp.myscroll.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 717219917@qq.com      2018/1/11  14:11
 */

public class ListViewAdapter extends BaseAdapter{
    Context mContext;
    List<String>mList = new ArrayList<>();
    LayoutInflater inflater;
    public ListViewAdapter(Context context, List<String>list){
        mContext=context;
        mList=list;
        inflater=LayoutInflater.from(context);
    }

    @Override public int getCount() {  return mList.size();  }
    @Override public Object getItem(int position) {  return mList.get(position);  }
    @Override public long getItemId(int position) {  return position;  }
    @Override public View getView(int position, View convertView, ViewGroup parent) {  //加载布局为一个视图
        ViewHolder holder = null;
        if(convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.listview_item, null);
            holder = new ViewHolder();
            holder.tv = convertView.findViewById(R.id.tv);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
       final String str = mList.get(position);
        holder.tv.setText(str);
        holder.tv.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                Toast.makeText(mContext,str,Toast.LENGTH_SHORT).show();
            }
        });

        return convertView;
    }

    class ViewHolder {  TextView tv;  }

}
