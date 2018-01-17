package com.example.administrator.myproject.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.administrator.myproject.R;
import com.yqritc.scalablevideoview.ScalableVideoView;

import java.util.List;

/**
 * @author jack
 * @date 2018/1/17.
 */

public class AutoPollAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final LayoutInflater layoutInflater;
    private Context mContext;
    private List<String> mData;

    public AutoPollAdapter(Context mContext, List<String> mData) {
        this.mContext = mContext;
        this.mData = mData;
        this.layoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemViewHolder(layoutInflater,parent);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((ItemViewHolder)holder).text.setText(mData.get(position%mData.size()));
    }

    @Override
    public int getItemCount() {
        return mData.size()* 3000;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView text;
        private ImageView image;
        private ScalableVideoView textureView;
        private ProgressBar progressBar;
        private ImageView play;
        public ItemViewHolder(LayoutInflater inflater, ViewGroup parent) {
            this(inflater.inflate(R.layout.listview_item, parent, false));
        }

        public ItemViewHolder(View view) {
            super(view);
            text = (TextView) view.findViewById(R.id.text);
            image = (ImageView) view.findViewById(R.id.image);
            textureView = (ScalableVideoView) view.findViewById(R.id.video);
            progressBar = (ProgressBar) view.findViewById(R.id.progressbar);
            play = (ImageView) view.findViewById(R.id.play);
//            view.setOnClickListener(this);


        }

        public TextView getText() {
            return text;
        }

        @Override
        public void onClick(View v) {

        }
    }
}
