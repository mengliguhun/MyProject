package com.example.administrator.myproject.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.administrator.myproject.R;
import com.example.administrator.myproject.bean.FunnyListResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/12/24.
 */

public class ListViewAdapter extends BaseAdapter {

    private List<FunnyListResult.ItemsEntity> objects = new ArrayList<>();

    private Context context;
    private LayoutInflater layoutInflater;

    public ListViewAdapter(Context context,List<FunnyListResult.ItemsEntity> objects) {
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.objects = objects;
    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public Object getItem(int position) {
        return objects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.listview_item, null);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.text = (TextView) convertView.findViewById(R.id.text);

            convertView.setTag(viewHolder);
        }
        initializeViews(getItem(position), (ViewHolder) convertView.getTag());
        return convertView;
    }

    private void initializeViews(Object object, ViewHolder holder) {
        //TODO implement
//        Contributor contributor = (Contributor) object;
//        holder.text.setText(contributor.toString());
    }

    protected class ViewHolder {
        private TextView text;
    }
}
