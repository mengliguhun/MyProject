/*
 * Copyright 2013 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.administrator.myproject.recordscreen;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Choreographer;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.administrator.myproject.R;
import com.example.administrator.myproject.recordscreen.gles.MiscUtils;
import com.example.administrator.myproject.view.BasicGraffitiView;

import java.lang.ref.WeakReference;

/**
 * Demonstrates efficient display + recording of OpenGL rendering using an FBO.  This
 * records only the GL surface (i.e. not the app UI, nav bar, status bar, or alert dialog).
 * <p>
 * This uses a plain SurfaceView, rather than GLSurfaceView, so we have full control
 * over the EGL config and rendering.  When available, we use GLES 3, which allows us
 * to do recording with one extra copy instead of two.
 * <p>
 * We use Choreographer so our animation matches vsync, and a separate rendering
 * thread to keep the heavy lifting off of the UI thread.  Ideally we'd let the render
 * thread receive the Choreographer events directly, but that appears to be creating
 * a permanent JNI global reference to the render thread object, preventing it from
 * being garbage collected (which, in turn, causes the Activity to be retained).  So
 * instead we receive the vsync on the UI thread and forward it.
 * <p>
 * If the rendering is fairly simple, it may be more efficient to just render the scene
 * twice (i.e. configure for display, call draw(), configure for video, call draw()).  If
 * the video being created is at a lower resolution than the display, rendering at the lower
 * resolution may produce better-looking results than a downscaling blit.
 * <p>
 * To reduce the impact of recording on rendering (which is probably a fancy-looking game),
 * we want to perform the recording tasks on a separate thread.  The actual video encoding
 * is performed in a separate process by the hardware H.264 encoder, so feeding input into
 * the encoder requires little effort.  The MediaMuxer step runs on the CPU and performs
 * disk I/O, so we really want to drain the encoder on a separate thread.
 * <p>
 * Some other examples use a pair of EGL contexts, configured to share state.  We don't want
 * to do that here, because GLES3 allows us to improve performance by using glBlitFramebuffer(),
 * and framebuffer objects aren't shared.  So we use a single EGL context for rendering to
 * both the display and the video encoder.
 * <p>
 * It might appear that shifting the rendering for the encoder input to a different thread
 * would be advantageous, but in practice all of the work is done by the GPU, and submitting
 * the requests from different CPU cores isn't going to matter.
 * <p>
 * As always, we have to be careful about sharing state across threads.  By fully configuring
 * the encoder before starting the encoder thread, we ensure that the new thread sees a
 * fully-constructed object.  The encoder object then "lives" in the encoder thread.  The main
 * thread doesn't need to talk to it directly, because all of the input goes through Surface.
 * <p>
 * TODO: add another bouncing rect that uses decoded video as a texture.  Useful for
 * evaluating simultaneous video playback and recording.
 * <p>
 * TODO: show the MP4 file name somewhere in the UI so people can find it in the player
 */
public class RecordScreenActivity extends Activity implements SurfaceHolder.Callback,
        Choreographer.FrameCallback {
    private static final String TAG = RecordScreenActivity.class.getSimpleName();

    // See the (lengthy) notes at the top of HardwareScalerActivity for thoughts about
    // Activity / Surface lifecycle management.

    private boolean mRecordingEnabled = false;          // controls button state
    private boolean mBlitFramebufferAllowed = false;    // requires GLES3
    private int mSelectedRecordMethod;                  // current radio button

    private RenderThread mRenderThread;

    private BasicGraffitiView mGraffitiView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_fbo);

        mSelectedRecordMethod = mRenderThread.RECMETHOD_DRAW_TWICE;
        updateControls();

        mGraffitiView = (BasicGraffitiView) findViewById(R.id.graffiti);
        mGraffitiView.setBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.share_via_barcode));
        mGraffitiView.setPaintEnable(true);

        SurfaceView sv = (SurfaceView) findViewById(R.id.fboActivity_surfaceView);
        sv.getHolder().addCallback(this);

        Log.d(TAG, "RecordFBOActivity: onCreate done");
    }

    @Override
    protected void onPause() {
        super.onPause();
        // TODO: we might want to stop recording here.  As it is, we continue "recording",
        //       which is pretty boring since we're not outputting any frames (test this
        //       by blanking the screen with the power button).

        // If the callback was posted, remove it.  This stops the notifications.  Ideally we
        // would send a message to the thread letting it know, so when it wakes up it can
        // reset its notion of when the previous Choreographer event arrived.
        Log.d(TAG, "onPause unhooking choreographer");
        Choreographer.getInstance().removeFrameCallback(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // If we already have a Surface, we just need to resume the frame notifications.
        if (mRenderThread != null) {
            Log.d(TAG, "onResume re-hooking choreographer");
            Choreographer.getInstance().postFrameCallback(this);
        }

        updateControls();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG, "surfaceCreated holder=" + holder);

        SurfaceView sv = (SurfaceView) findViewById(R.id.fboActivity_surfaceView);
        mRenderThread = new RenderThread(this,mGraffitiView,sv.getHolder(), new ActivityHandler(this),
                MiscUtils.getDisplayRefreshNsec(this));
        mRenderThread.setName("RecordFBO GL render");
        mRenderThread.start();
        mRenderThread.waitUntilReady();
        mRenderThread.setRecordMethod(mSelectedRecordMethod);

        RenderThread.RenderHandler rh = mRenderThread.getHandler();
        if (rh != null) {
            rh.sendSurfaceCreated();
        }

        // start the draw events
        Choreographer.getInstance().postFrameCallback(this);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d(TAG, "surfaceChanged fmt=" + format + " size=" + width + "x" + height +
                " holder=" + holder);
        RenderThread.RenderHandler rh = mRenderThread.getHandler();
        if (rh != null) {
            rh.sendSurfaceChanged(format, width, height);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG, "surfaceDestroyed holder=" + holder);

        // We need to wait for the render thread to shut down before continuing because we
        // don't want the Surface to disappear out from under it mid-render.  The frame
        // notifications will have been stopped back in onPause(), but there might have
        // been one in progress.
        //
        // TODO: the RenderThread doesn't currently wait for the encoder / muxer to stop,
        //       so we can't use this as an indication that the .mp4 file is complete.

        RenderThread.RenderHandler rh = mRenderThread.getHandler();
        if (rh != null) {
            rh.sendShutdown();
            try {
                mRenderThread.join();
            } catch (InterruptedException ie) {
                // not expected
                throw new RuntimeException("join was interrupted", ie);
            }
        }
        mRenderThread = null;
        mRecordingEnabled = false;

        // If the callback was posted, remove it.  Without this, we could get one more
        // call on doFrame().
        Choreographer.getInstance().removeFrameCallback(this);
        Log.d(TAG, "surfaceDestroyed complete");
    }

    /*
     * Choreographer callback, called near vsync.
     *
     * @see android.view.Choreographer.FrameCallback#doFrame(long)
     */
    @Override
    public void doFrame(long frameTimeNanos) {
        RenderThread.RenderHandler rh = mRenderThread.getHandler();
        if (rh != null) {
            Choreographer.getInstance().postFrameCallback(this);
            rh.sendDoFrame(frameTimeNanos);
        }
    }

    /**
     * Updates the GLES version string.
     * <p>
     * Called from the render thread (via ActivityHandler) after the EGL context is created.
     */
    void handleShowGlesVersion(int version) {
        TextView tv = (TextView) findViewById(R.id.glesVersionValue_text);
        tv.setText("" + version);
        if (version >= 3) {
            mBlitFramebufferAllowed = true;
            updateControls();
        }
    }

    /**
     * Updates the FPS counter.
     * <p>
     * Called periodically from the render thread (via ActivityHandler).
     */
    void handleUpdateFps(int tfps, int dropped) {
        String str = getString(R.string.frameRateFormat, tfps / 1000.0f, dropped);
        TextView tv = (TextView) findViewById(R.id.frameRateValue_text);
        tv.setText(str);
    }

    /**
     * onClick handler for "record" button.
     * <p>
     * Ideally we'd grey out the button while in a state of transition, e.g. while the
     * MediaMuxer finishes creating the file, and in the (very brief) period before the
     * SurfaceView's surface is created.
     */
    public void clickToggleRecording(@SuppressWarnings("unused") View unused) {
        Log.d(TAG, "clickToggleRecording");
        RenderThread.RenderHandler rh = mRenderThread.getHandler();
        if (rh != null) {
            mRecordingEnabled = !mRecordingEnabled;
            updateControls();
            rh.setRecordingEnabled(mRecordingEnabled);
        }
    }

    /**
     * onClick handler for radio buttons.
     */
    public void onRadioButtonClicked(View view) {
        RadioButton rb = (RadioButton) view;
        if (!rb.isChecked()) {
            Log.d(TAG, "Got click on non-checked radio button");
            return;
        }

        switch (rb.getId()) {
            case R.id.recDrawTwice_radio:
                mSelectedRecordMethod = mRenderThread.RECMETHOD_DRAW_TWICE;
                break;
            case R.id.recFramebuffer_radio:
                mSelectedRecordMethod = mRenderThread.RECMETHOD_BLIT_FRAMEBUFFER;
                break;
            default:
                throw new RuntimeException("Click from unknown id " + rb.getId());
        }

        Log.d(TAG, "Selected rec mode " + mSelectedRecordMethod);
        RenderThread.RenderHandler rh = mRenderThread.getHandler();
        if (rh != null) {
            rh.setRecordMethod(mSelectedRecordMethod);
        }
    }

    /**
     * Updates the on-screen controls to reflect the current state of the app.
     */
    private void updateControls() {
        Button toggleRelease = (Button) findViewById(R.id.fboRecord_button);
        int id = mRecordingEnabled ?
                R.string.toggleRecordingOff : R.string.toggleRecordingOn;
        toggleRelease.setText(id);

        RadioButton rb;
        rb = (RadioButton) findViewById(R.id.recDrawTwice_radio);
        rb.setChecked(mSelectedRecordMethod == mRenderThread.RECMETHOD_DRAW_TWICE);
        rb = (RadioButton) findViewById(R.id.recFramebuffer_radio);
        rb.setChecked(mSelectedRecordMethod == mRenderThread.RECMETHOD_BLIT_FRAMEBUFFER);
        rb.setEnabled(mBlitFramebufferAllowed);

        TextView tv = (TextView) findViewById(R.id.nowRecording_text);
        if (mRecordingEnabled) {
            tv.setText(getString(R.string.nowRecording));
        } else {
            tv.setText("");
        }
    }


    /**
     * Handles messages sent from the render thread to the UI thread.
     * <p>
     * The object is created on the UI thread, and all handlers run there.
     */
    static class ActivityHandler extends Handler {
        private static final int MSG_GLES_VERSION = 0;
        private static final int MSG_UPDATE_FPS = 1;

        // Weak reference to the Activity; only access this from the UI thread.
        private WeakReference<RecordScreenActivity> mWeakActivity;

        public ActivityHandler(RecordScreenActivity activity) {
            mWeakActivity = new WeakReference<RecordScreenActivity>(activity);
        }

        /**
         * Send the GLES version.
         * <p>
         * Call from non-UI thread.
         */
        public void sendGlesVersion(int version) {
            sendMessage(obtainMessage(MSG_GLES_VERSION, version, 0));
        }

        /**
         * Send an FPS update.  "fps" should be in thousands of frames per second
         * (i.e. fps * 1000), so we can get fractional fps even though the Handler only
         * supports passing integers.
         * <p>
         * Call from non-UI thread.
         */
        public void sendFpsUpdate(int tfps, int dropped) {
            sendMessage(obtainMessage(MSG_UPDATE_FPS, tfps, dropped));
        }

        @Override  // runs on UI thread
        public void handleMessage(Message msg) {
            int what = msg.what;
            //Log.d(TAG, "ActivityHandler [" + this + "]: what=" + what);

            RecordScreenActivity activity = mWeakActivity.get();
            if (activity == null) {
                Log.w(TAG, "ActivityHandler.handleMessage: activity is null");
                return;
            }

            switch (what) {
                case MSG_GLES_VERSION:
                    activity.handleShowGlesVersion(msg.arg1);
                    break;
                case MSG_UPDATE_FPS:
                    activity.handleUpdateFps(msg.arg1, msg.arg2);
                    break;
                default:
                    throw new RuntimeException("unknown msg " + what);
            }
        }
    }
}
