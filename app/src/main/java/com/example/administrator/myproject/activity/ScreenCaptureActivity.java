package com.example.administrator.myproject.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.myproject.R;
import com.example.administrator.myproject.utils.TakePhotoTools;
import com.example.administrator.myproject.view.CustomProgressBar;
import com.example.administrator.myproject.view.HaloToast;
import com.example.administrator.myproject.view.ScaleTransGraffitiView;

import java.io.File;

import static com.example.administrator.myproject.R.id.record;

/**
 * android 5.0及以上
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class ScreenCaptureActivity extends BaseActivity implements View.OnClickListener{

    private static final String TAG = "ScreenCaptureFragment";

    private static final String STATE_RESULT_CODE = "result_code";
    private static final String STATE_RESULT_DATA = "result_data";

    private static final int REQUEST_MEDIA_PROJECTION = 1;

    private int mScreenDensity;

    private int mResultCode;
    private Intent mResultData;

    private DisplayMetrics metrics;
    private MediaProjection mMediaProjection;
    private VirtualDisplay mVirtualDisplay;
    private MediaProjectionManager mMediaProjectionManager;
    private MediaRecorder mediaRecorder;
    private Button mButtonToggle;
    private ScaleTransGraffitiView mGraffitiView;
    private CustomProgressBar mProgressBar;
    private View mRecord;
    private ImageView mRecordingBg,mRecordBtn;
    private CheckBox mPaint;

    /**
     * 倒计时
     */
    private TextView mCountdown;
    private int count = 3;
    /**
     * 是否正在录制
     */
    private boolean isRecording = false;
    /** 录制最长时间 */
    public final static int RECORD_TIME_MAX = 4* 60 * 1000;
    /** 录制最小时间 */
    public final static int RECORD_TIME_MIN = 60 * 1000;
    private String videoPath;

    private ScaleAnimation scaleAnimation;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        if (savedInstanceState != null) {
            mResultCode = savedInstanceState.getInt(STATE_RESULT_CODE);
            mResultData = savedInstanceState.getParcelable(STATE_RESULT_DATA);
        }
        setContentView(R.layout.activity_screen_capture);

        metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        mScreenDensity = metrics.densityDpi;
        mMediaProjectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);

        initViews();
        initAnimation();
        selectFile();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mResultData != null) {
            outState.putInt(STATE_RESULT_CODE, mResultCode);
            outState.putParcelable(STATE_RESULT_DATA, mResultData);
        }
    }

    private void initAnimation() {
        scaleAnimation = new ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(1000);
        scaleAnimation.setFillAfter(false);
        scaleAnimation.setStartOffset(0);
        scaleAnimation.setRepeatCount(2);
        scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mRecordBtn.setVisibility(View.GONE);
                mRecordingBg.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (isFinishing())
                    return;
                mCountdown.setVisibility(View.GONE);
                startScreenCapture();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                mCountdown.setText(--count + "");
            }
        });
    }

    @Override
    public void initViews() {
        mRecord = findViewById(record);
        mPaint = (CheckBox) findViewById(R.id.paint);
        mRecordingBg = (ImageView) findViewById(R.id.recording_bg);
        mRecordBtn = (ImageView) findViewById(R.id.recorder_btn);
        mCountdown = (TextView) findViewById(R.id.count_down);
        mButtonToggle = (Button) findViewById(R.id.close_btn);
        mProgressBar = (CustomProgressBar) findViewById(R.id.custom_progressbar);
        mGraffitiView = (ScaleTransGraffitiView) findViewById(R.id.graffiti);
        mGraffitiView.setBitmap(BitmapFactory.decodeResource(getResources(),R.mipmap.img));
        mGraffitiView.setResizeMode(true);

        mButtonToggle.setOnClickListener(this);
        mRecord.setOnClickListener(this);
        mPaint.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mGraffitiView.setPaintEnable(isChecked);
                mGraffitiView.setResizeMode(!isChecked);
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.close_btn:
                if (isRecording) {
                    stopScreenCapture();

                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("是否取消视频录制？");
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                           finish();
                        }
                    });
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                            VideoPreviewActivity.start(ScreenCaptureActivity.this,videoPath);
                        }
                    });
                    builder.create().show();

                } else {
                    finish();
                }
                break;
            case R.id.record:
                if (!isRecording) {
                    count = 3;
                    mCountdown.setText(count + "");
                    mCountdown.setVisibility(View.VISIBLE);
                    mCountdown.startAnimation(scaleAnimation);
                } else {
                    stopScreenCapture();
                    VideoPreviewActivity.start(this,videoPath);
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == 0x222) {
            String filePath = TakePhotoTools.getRealPath(this, data.getData());
            mGraffitiView.setImage(filePath);
        }
        if (requestCode == REQUEST_MEDIA_PROJECTION) {
            if (resultCode != RESULT_OK) {
                HaloToast.show("User cancelled");
                return;
            }

            //Starting screen capture
            mResultCode = resultCode;
            mResultData = data;

            initRecorder();
            setUpMediaProjection();
            setUpVirtualDisplay();
            mediaRecorder.start();
            //开启计时器
            start_record.start();

            isRecording = true;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        stopScreenCapture();
    }

    //初始化录屏管理器
    private void setUpMediaProjection() {
        mMediaProjection = mMediaProjectionManager.getMediaProjection(mResultCode, mResultData);
    }

    /**
     * 发起录屏
     */
    private void startScreenCapture() {

        Log.i(TAG, "Requesting confirmation");
        // This initiates a prompt dialog for the user to confirm screen projection.发起屏幕捕捉请求
        startActivityForResult(mMediaProjectionManager.createScreenCaptureIntent(), REQUEST_MEDIA_PROJECTION);
    }

    /**
     * 创建虚拟屏幕
     * VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR 参数是指创建屏幕镜像，
     * 所以我们实际录制内容的是屏幕镜像，但内容和实际屏幕是一样的，
     * 并且这里我们把 VirtualDisplay 的渲染目标 Surface 设置为 MediaRecorder 的 getSurface，
     * 后面我就可以通过 MediaRecorder 将屏幕内容录制下来，并且存成 video 文件
     */
    private void setUpVirtualDisplay() {
        Log.i(TAG, "Setting up a VirtualDisplay: " + metrics.widthPixels + "x" + metrics.heightPixels + " (" + mScreenDensity + ")");
        mVirtualDisplay = mMediaProjection.createVirtualDisplay("ScreenCapture",
                metrics.widthPixels, metrics.heightPixels, mScreenDensity,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                mediaRecorder.getSurface(), null, null);
    }

    /**
     * 停止录屏
     */
    private void stopScreenCapture() {

        //停止计时器
        start_record.cancel();

        stopRecord();

        if (mVirtualDisplay != null) {
            mVirtualDisplay.release();
            mVirtualDisplay = null;
        }

        if (mMediaProjection != null) {
            mMediaProjection.stop();
            mMediaProjection = null;
        }
    }

    /**
     *  初始化 MediaRecorder
     */
    private void initRecorder() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        videoPath = getSaveDirectory() + System.currentTimeMillis() + ".mp4";
        mediaRecorder.setOutputFile(videoPath);
        mediaRecorder.setVideoSize(metrics.widthPixels, metrics.heightPixels);
        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mediaRecorder.setVideoEncodingBitRate(5 * 1024 * 1024);
        mediaRecorder.setVideoFrameRate(30);

        try {
            mediaRecorder.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 停止录制
     */
    public void stopRecord() {
        isRecording = false;
        if (mediaRecorder == null){
            return;
        }
        //下面三个参数必须加，不加的话会奔溃，在mediarecorder.stop();
        //报错为：RuntimeException:stop failed
        mediaRecorder.setOnErrorListener(null);
        mediaRecorder.setOnInfoListener(null);
        mediaRecorder.setPreviewDisplay(null);
        mediaRecorder.stop();
        mediaRecorder.reset();
        mediaRecorder =null;
    }

    /**
     * 倒计时计数器
     * 最长录制RECORD_TIME_MAX
     */
    private CountDownTimer start_record = new CountDownTimer(RECORD_TIME_MAX,1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            //更新进度条
            updataProgress(millisUntilFinished);
        }
        @Override
        public void onFinish() {
            //四分钟时间到，自动完成
        }
    };
    /**
     * 更新进度条
     * @param millis
     */
    private void updataProgress(long millis){
        int progress = 100 - (int) (millis*100/RECORD_TIME_MAX);
        mProgressBar.setProgress(progress);
    }
    /**
     * 文件保存路径
     * @return
     */
    public String getSaveDirectory() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String rootDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "ScreenRecord" + "/";

            File file = new File(rootDir);
            if (!file.exists()) {
                if (!file.mkdirs()) {
                    return null;
                }
            }
            HaloToast.show(rootDir);

            return rootDir;
        } else {
            return null;
        }
    }
    protected void selectFile(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");//设置类型，这里是任意类型
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, 0x222);
    }

}
