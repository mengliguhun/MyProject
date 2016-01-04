package com.example.administrator.myproject.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.administrator.myproject.R;
import com.example.administrator.myproject.httputils.HttpUtils.Contributor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/12/24.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private List<Contributor> objects = new ArrayList<>();
    private Context context;
    private LayoutInflater layoutInflater;

    public RecyclerViewAdapter(Context context,List<Contributor> objects) {
        this.objects = objects;
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);;
    }
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new ViewHolder(layoutInflater,parent);
    }

    @Override
    public void onBindViewHolder(RecyclerViewAdapter.ViewHolder holder, int position) {

        initializeViews(objects.get(position),holder);
    }

    @Override
    public int getItemCount() {
        return objects.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView text;

        public ViewHolder(LayoutInflater inflater, ViewGroup parent) {
            this(inflater.inflate(R.layout.listview_item, parent, false));
        }

        public ViewHolder(View view) {
            super(view);
            text = (TextView) view.findViewById(R.id.text);
        }

        public TextView getText() {
            return text;
        }
    }
    private void initializeViews(Object object, ViewHolder holder) {
        //TODO implement
        Contributor contributor = (Contributor) object;
        holder.text.setText(contributor.toString());
    }
}
