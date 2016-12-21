package com.example.administrator.myproject.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.administrator.myproject.R;
import com.example.administrator.myproject.realm.model.Dog;

import io.realm.OrderedRealmCollection;

/**
 * Created by Administrator on 2015/12/24.
 */

public class RealmRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private OrderedRealmCollection<Dog> objects;
    private Context context;
    private LayoutInflater layoutInflater;
    public RealmRecyclerViewAdapter(Context context, OrderedRealmCollection<Dog> objects) {
        this.objects = objects;
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemViewHolder(layoutInflater,parent);
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        initializeViews(objects.get(position),(ItemViewHolder)holder);

    }

    @Override
    public int getItemCount() {
        return objects.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView text;
        private ImageView image;

        public ItemViewHolder(LayoutInflater inflater, ViewGroup parent) {
            this(inflater.inflate(R.layout.realm_listview_item, parent, false));
        }

        public ItemViewHolder(View view) {
            super(view);
            text = (TextView) view.findViewById(R.id.text);
            image = (ImageView) view.findViewById(R.id.image);

            view.setOnClickListener(this);

        }

        public TextView getText() {
            return text;
        }

        @Override
        public void onClick(View v) {

        }
    }
    private void initializeViews(Object object, final ItemViewHolder holder) {
        //TODO implement
        final Dog dog = (Dog) object;
        holder.text.setText("Name:"+dog.getName()+" Age:"+dog.getAge());

        if (!TextUtils.isEmpty(dog.getPic())){

            holder.image.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(dog.getPic())
                    .placeholder(R.mipmap.ic_insert_photo_black)
                    .error(R.mipmap.ic_insert_photo_black)
                    .into(holder.image);
        }
        else {
            holder.image.setVisibility(View.GONE);
        }
    }
}
