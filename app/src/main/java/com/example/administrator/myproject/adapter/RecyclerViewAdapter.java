package com.example.administrator.myproject.adapter;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.administrator.myproject.R;
import com.example.administrator.myproject.bean.FunnyListResult;
import com.example.administrator.myproject.httpapis.HttpApi;
import com.example.administrator.myproject.utils.GraphicUtils;
import com.example.administrator.myproject.utils.ImageLoaderUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yqritc.scalablevideoview.ScalableType;
import com.yqritc.scalablevideoview.ScalableVideoView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/12/24.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<FunnyListResult.ItemsEntity> objects = new ArrayList<>();
    private Context context;
    private LayoutInflater layoutInflater;
    private DisplayMetrics displayMetrics;
    public RecyclerViewAdapter(Context context,List<FunnyListResult.ItemsEntity> objects) {
        this.objects = objects;
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        displayMetrics = context.getResources().getDisplayMetrics();
    }

//    @Override
//    public int getItemViewType(int position) {
//        if (isPositionHeader(position))
//            return RecyclerView.INVALID_TYPE;
//
//        return super.getItemViewType(position);
//    }

//    private boolean isPositionHeader(int position) {
//        return position == 0;
//    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//       if (viewType == RecyclerView.INVALID_TYPE){
//           return new HeaderViewHolder(layoutInflater,parent);
//       }
        return new ItemViewHolder(layoutInflater,parent);
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
//        if (holder instanceof HeaderViewHolder){
//
//        }else {
            initializeViews(objects.get(position),(ItemViewHolder)holder);
//        }

    }

    @Override
    public int getItemCount() {
        return objects.size();
    }
    public class HeaderViewHolder extends RecyclerView.ViewHolder{
        public HeaderViewHolder(LayoutInflater inflater, ViewGroup parent) {
            this(inflater.inflate(R.layout.listview_item, parent, false));
        }
        public HeaderViewHolder(View itemView) {
            super(itemView);
        }
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
        final FunnyListResult.ItemsEntity entity = (FunnyListResult.ItemsEntity) object;
        holder.text.setText(entity.getContent());

        if (!TextUtils.isEmpty(entity.getImage())){
            holder.image.setVisibility(View.VISIBLE);
            holder.textureView.setVisibility(View.GONE);

            int width = displayMetrics.widthPixels-GraphicUtils.dip2px(context,20);
            int height = width *entity.getImage_size().getM().get(1)/entity.getImage_size().getM().get(0);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width,height);
            holder.image.setLayoutParams(params);
//            Picasso.with(context).load(HttpApi.getInstance().getImageUrl(entity.getImage(), entity.getId() + "")).into(holder.image);
            ImageLoader.getInstance()
                    .displayImage(HttpApi.getInstance().getImageUrl(entity.getImage(), entity.getId() + ""), holder.image, ImageLoaderUtil.getDisplayImageOptions());
        }
        else if (!TextUtils.isEmpty(entity.getPic_url())){
            holder.image.setVisibility(View.VISIBLE);
            holder.textureView.setVisibility(View.VISIBLE);
            holder.progressBar.setVisibility(View.GONE);
            holder.play.setVisibility(View.VISIBLE);

            final int width = displayMetrics.widthPixels-GraphicUtils.dip2px(context,20);
            final int height = width *entity.getPic_size().get(1)/entity.getPic_size().get(0);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width,height);
            holder.image.setLayoutParams(params);
            ImageLoader.getInstance()
                    .displayImage(entity.getPic_url(), holder.image, ImageLoaderUtil.getDisplayImageOptions());
//            Picasso.with(context).load(entity.getPic_url()).into(holder.image);
            holder.textureView.setLayoutParams(params);

            try {
                holder.textureView.setDataSource(context, Uri.parse(entity.getHigh_url()));
                holder.textureView.setScalableType(ScalableType.FIT_XY);
                if (!entity.isVisible()) {
                    if (holder.textureView.isPlaying()) {
                        holder.textureView.stop();
                    }
                    holder.progressBar.setVisibility(View.GONE);
                    holder.play.setVisibility(View.VISIBLE);
                }
                else {

                    if (!holder.textureView.isPlaying()) {
                        holder.progressBar.setVisibility(View.GONE);
                        holder.play.setVisibility(View.VISIBLE);
                    }
                    else {
                        holder.progressBar.setVisibility(View.GONE);
                        holder.play.setVisibility(View.GONE);
                    }

                }

                holder.textureView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                            if (entity.isVisible()){
                                if (holder.textureView.isPlaying()){
                                    holder.textureView.stop();
                                    holder.play.setVisibility(View.VISIBLE);
                                }
                            }
                    }
                });
                holder.textureView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        holder.textureView.reset();
                        holder.play.setVisibility(View.VISIBLE);
                    }
                });
                holder.play.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder.play.setVisibility(View.GONE);
                        try {
                            holder.textureView.setDataSource(context, Uri.parse(entity.getHigh_url()));
                            holder.textureView.onSurfaceTextureAvailable(holder.textureView.getSurfaceTexture(),width,height);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (!holder.textureView.isPlaying()) {
                            holder.progressBar.setVisibility(View.VISIBLE);
                            holder.textureView.prepareAsync(new MediaPlayer.OnPreparedListener() {
                                @Override
                                public void onPrepared(MediaPlayer mp) {
                                    if (entity.isVisible()){
                                        holder.progressBar.setVisibility(View.GONE);
                                        holder.textureView.start();
                                    }

                                }
                            });
                        }



                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        else {
            holder.image.setVisibility(View.GONE);
            holder.textureView.setVisibility(View.GONE);
            holder.progressBar.setVisibility(View.GONE);
        }
    }
}
