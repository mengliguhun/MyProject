package com.example.administrator.myproject.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.android.tedcoder.wkvideoplayer.util.DensityUtil;
import com.android.tedcoder.wkvideoplayer.view.MediaController;
import com.android.tedcoder.wkvideoplayer.view.SuperVideoPlayer;
import com.example.administrator.myproject.R;
import com.example.administrator.myproject.view.HaloToast;

/**
 * 视频预览页面
 * Created by hubin on 2016/10/10.
 */
public class VideoPreviewActivity extends BaseActivity implements View.OnClickListener{

    private static final String VIDEO_PATH = "video_path";

    private SuperVideoPlayer videoPlayer;

    private ImageView ivPlay;
    private ImageView ivCover;
    private String videoPath;

    public static void start(Context context, String videoPath){
        Intent intent = new Intent(context, VideoPreviewActivity.class);
        intent.putExtra(VIDEO_PATH, videoPath);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_preview);
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            videoPath = bundle.getString(VIDEO_PATH);

        }else{
            HaloToast.show("参数错误");
            finish();
        }

        initDisplay();
    }

    private void initDisplay() {
        videoPlayer = (SuperVideoPlayer) findViewById(R.id.video_player);
        ivPlay = (ImageView) findViewById(R.id.iv_play);
        ivCover= (ImageView) findViewById(R.id.iv_cover);
        ivPlay.setOnClickListener(this);
        videoPlayer.setVideoPlayCallback(mVideoPlayCallback);

    }

    private SuperVideoPlayer.VideoPlayCallbackImpl mVideoPlayCallback = new SuperVideoPlayer.VideoPlayCallbackImpl() {
        @Override
        public void onCloseVideo() {
            videoPlayer.close();
            ivPlay.setVisibility(View.VISIBLE);
            ivCover.setVisibility(View.VISIBLE);
            videoPlayer.setVisibility(View.GONE);
            resetPageToPortrait();
        }

        @Override
        public void onSwitchPageType() {
            if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                videoPlayer.setPageType(MediaController.PageType.SHRINK);
            } else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                videoPlayer.setPageType(MediaController.PageType.EXPAND);
            }
        }

        @Override
        public void onPlayFinish() {

        }
    };

    /***
     * 恢复屏幕至竖屏
     */
    private void resetPageToPortrait() {
        if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            videoPlayer.setPageType(MediaController.PageType.SHRINK);
        }
    }

    /***
     * 旋转屏幕之后回调
     *
     * @param newConfig newConfig
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (null == videoPlayer) return;
        /***
         * 根据屏幕方向重新设置播放器的大小
         */
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().getDecorView().invalidate();
            float height = DensityUtil.getWidthInPx(this);
            float width = DensityUtil.getHeightInPx(this);
            videoPlayer.getLayoutParams().height = (int) width;
            videoPlayer.getLayoutParams().width = (int) height;
        } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            final WindowManager.LayoutParams attrs = getWindow().getAttributes();
            attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().setAttributes(attrs);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            float width = DensityUtil.getWidthInPx(this);
            float height = DensityUtil.dip2px(this, 250.f);
            videoPlayer.getLayoutParams().height = (int) height;
            videoPlayer.getLayoutParams().width = (int) width;
        }
    }

    @Override
    protected void onPause() {
        if(videoPlayer != null){
            videoPlayer.close();
            ivPlay.setVisibility(View.VISIBLE);
            ivCover.setVisibility(View.VISIBLE);
            videoPlayer.setVisibility(View.GONE);
        }
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_play:
                ivPlay.setVisibility(View.GONE);
                videoPlayer.setVisibility(View.GONE);
                ivCover.setVisibility(View.VISIBLE);
                videoPlayer.setAutoHideController(true);

                if(TextUtils.isEmpty(videoPath)){
                    HaloToast.show("无法播放此视频");
                    finish();
                }

                videoPlayer.loadLocalVideo(videoPath);

                break;
        }
    }
}
