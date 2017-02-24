package com.example.administrator.myproject.recordscreen;

/**
 * Created by Administrator on 2017/2/22.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.Matrix;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.View;

import com.example.administrator.myproject.recordscreen.encoder.MediaAudioEncoder;
import com.example.administrator.myproject.recordscreen.encoder.MediaEncoder;
import com.example.administrator.myproject.recordscreen.encoder.MediaMuxerWrapper;
import com.example.administrator.myproject.recordscreen.encoder.MediaVideoEncoder;
import com.example.administrator.myproject.recordscreen.gles.EglCore;
import com.example.administrator.myproject.recordscreen.gles.FlatShadedProgram;
import com.example.administrator.myproject.recordscreen.gles.FullFrameRect;
import com.example.administrator.myproject.recordscreen.gles.GlUtil;
import com.example.administrator.myproject.recordscreen.gles.Texture2dProgram;
import com.example.administrator.myproject.recordscreen.gles.WindowSurface;
import com.example.administrator.myproject.utils.SystemInfoUtil;

import java.io.IOException;
import java.lang.ref.WeakReference;

/**
 * This class handles all OpenGL rendering.
 * <p>
 * We use Choreographer to coordinate with the device vsync.  We deliver one frame
 * per vsync.  We can't actually know when the frame we render will be drawn, but at
 * least we get a consistent frame interval.
 * <p>
 * Start the render thread after the Surface has been created.
 */
public class RenderThread extends Thread {
    private static final String TAG = RenderThread.class.getSimpleName();
    public static final int RECMETHOD_DRAW_TWICE = 0;
    public static final int RECMETHOD_BLIT_FRAMEBUFFER = 2;

    // Object must be created on render thread to get correct Looper, but is used from
    // UI thread, so we need to declare it volatile to ensure the UI thread sees a fully
    // constructed object.
    private volatile RenderHandler mHandler;

    // Handler we can send messages to if we want to update the app UI.
    private RecordScreenActivity.ActivityHandler mActivityHandler;

    // Used to wait for the thread to start.
    private Object mStartLock = new Object();
    private boolean mReady = false;

    private volatile SurfaceHolder mSurfaceHolder;  // may be updated by UI thread
    private EglCore mEglCore;
    private WindowSurface mWindowSurface;
    private FlatShadedProgram mProgram;

    // Orthographic projection matrix.
    private float[] mDisplayProjectionMatrix = new float[16];

    private final float[] mIdentityMatrix;

    // FPS / drop counter.
    private long mRefreshPeriodNanos;
    private long mFpsCountStartNanos;
    private int mFpsCountFrame;
    private int mDroppedFrames;

    // Used for off-screen rendering.
    private int mOffscreenTexture;
    private int mFramebuffer;
    private int mDepthBuffer;
    private FullFrameRect mFullScreen;

    // Used for recording.
    private boolean mRecordingEnabled;
    private WindowSurface mInputWindowSurface;

    private MediaMuxerWrapper mMuxer;
    private MediaVideoEncoder encoderCore;
    private MediaAudioEncoder audioEncoder;

    private int mRecordMethod;
    private boolean mRecordedPrevious;
    private Rect mVideoRect;
    private int[] mTexNames = new int[1];
    private GLBitmap glBitmap;
    private View viewTarget;
    private Context mContext;
    /**
     * Pass in the SurfaceView's SurfaceHolder.  Note the Surface may not yet exist.
     */
    public RenderThread(Context context, View viewTarget,SurfaceHolder holder, RecordScreenActivity.ActivityHandler ahandler, long refreshPeriodNs) {

        this.mContext = context;

        this.viewTarget = viewTarget;

        mSurfaceHolder = holder;
        mActivityHandler = ahandler;
        mRefreshPeriodNanos = refreshPeriodNs;

        mVideoRect = new Rect();

        mIdentityMatrix = new float[16];
        Matrix.setIdentityM(mIdentityMatrix, 0);

        glBitmap = new GLBitmap(context);
    }

    /**
     * Thread entry point.
     * <p>
     * The thread should not be started until the Surface associated with the SurfaceHolder
     * has been created.  That way we don't have to wait for a separate "surface created"
     * message to arrive.
     */
    @Override
    public void run() {
        Looper.prepare();
        mHandler = new RenderHandler(this);
        mEglCore = new EglCore(null, EglCore.FLAG_RECORDABLE | EglCore.FLAG_TRY_GLES3);
        synchronized (mStartLock) {
            mReady = true;
            mStartLock.notify();    // signal waitUntilReady()
        }

        Looper.loop();

        Log.d(TAG, "looper quit");
        releaseGl();
        mEglCore.release();

        synchronized (mStartLock) {
            mReady = false;
        }
    }

    /**
     * Waits until the render thread is ready to receive messages.
     * <p>
     * Call from the UI thread.
     */
    public void waitUntilReady() {
        synchronized (mStartLock) {
            while (!mReady) {
                try {
                    mStartLock.wait();
                } catch (InterruptedException ie) { /* not expected */ }
            }
        }
    }

    /**
     * Shuts everything down.
     */
    private void shutdown() {
        Log.d(TAG, "shutdown");
        stopEncoder();
        Looper.myLooper().quit();
    }

    /**
     * Returns the render thread's Handler.  This may be called from any thread.
     */
    public RenderHandler getHandler() {
        return mHandler;
    }

    /**
     * Prepares the surface.
     */
    private void surfaceCreated() {
        Surface surface = mSurfaceHolder.getSurface();
        prepareGl(surface);
    }

    /**
     * Prepares window surface and GL state.
     */
    private void prepareGl(Surface surface) {
        Log.d(TAG, "prepareGl");

        mWindowSurface = new WindowSurface(mEglCore, surface, false);
        mWindowSurface.makeCurrent();

        // Used for blitting texture to FBO.
        mFullScreen = new FullFrameRect(
                new Texture2dProgram(Texture2dProgram.ProgramType.TEXTURE_2D));

        // Program used for drawing onto the screen.
        mProgram = new FlatShadedProgram();

        // Set the background color.
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        // Disable depth testing -- we're 2D only.
        GLES20.glDisable(GLES20.GL_DEPTH_TEST);

        // Don't need backface culling.  (If you're feeling pedantic, you can turn it on to
        // make sure we're defining our shapes correctly.)
        GLES20.glDisable(GLES20.GL_CULL_FACE);

        mActivityHandler.sendGlesVersion(mEglCore.getGlVersion());
    }

    /**
     * Handles changes to the size of the underlying surface.  Adjusts viewport as needed.
     * Must be called before we start drawing.
     * (Called from RenderHandler.)
     */
    private void surfaceChanged(int width, int height) {
        Log.d(TAG, "surfaceChanged " + width + "x" + height);

        prepareFramebuffer(width, height);

        // Use full window.
        GLES20.glViewport(0, 0, width, height);

        // Simple orthographic projection, with (0,0) in lower-left corner.
        Matrix.orthoM(mDisplayProjectionMatrix, 0, 0, width, 0, height, -1, 1);

    }

    /**
     * Prepares the off-screen framebuffer.
     */
    private void prepareFramebuffer(int width, int height) {

        GlUtil.checkGlError("prepareFramebuffer start");

        // Create a texture object and bind it.  This will be the color buffer.
        GLES20.glGenTextures(1, mTexNames, 0);
        GlUtil.checkGlError("glGenTextures");
//            glBitmap.createTexture(values,width,height);
        mOffscreenTexture = mTexNames[0];   // expected > 0
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mOffscreenTexture);
        GlUtil.checkGlError("glBindTexture " + mOffscreenTexture);

        // Create texture storage.
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width, height, 0,
                GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);

        // Set parameters.  We're probably using non-power-of-two dimensions, so
        // some values may not be available for use.
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,
                GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER,
                GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,
                GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,
                GLES20.GL_CLAMP_TO_EDGE);
        GlUtil.checkGlError("glTexParameter");

        // Create framebuffer object and bind it.
        GLES20.glGenFramebuffers(1, mTexNames, 0);
        GlUtil.checkGlError("glGenFramebuffers");
        mFramebuffer = mTexNames[0];    // expected > 0
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFramebuffer);
        GlUtil.checkGlError("glBindFramebuffer " + mFramebuffer);

        // Create a depth buffer and bind it.
        GLES20.glGenRenderbuffers(1, mTexNames, 0);
        GlUtil.checkGlError("glGenRenderbuffers");
        mDepthBuffer = mTexNames[0];    // expected > 0
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, mDepthBuffer);
        GlUtil.checkGlError("glBindRenderbuffer " + mDepthBuffer);

        // Allocate storage for the depth buffer.
        GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, GLES20.GL_DEPTH_COMPONENT16,
                width, height);
        GlUtil.checkGlError("glRenderbufferStorage");

        // Attach the depth buffer and the texture (color buffer) to the framebuffer object.
        GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT,
                GLES20.GL_RENDERBUFFER, mDepthBuffer);
        GlUtil.checkGlError("glFramebufferRenderbuffer");
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                GLES20.GL_TEXTURE_2D, mOffscreenTexture, 0);
        GlUtil.checkGlError("glFramebufferTexture2D");

        // See if GLES is happy with all this.
        int status = GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER);
        if (status != GLES20.GL_FRAMEBUFFER_COMPLETE) {
            throw new RuntimeException("Framebuffer not complete, status=" + status);
        }

        // Switch back to the default framebuffer.
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);

        glBitmap.createTexture(mTexNames,getDrawBitmap(),width,height);
        GlUtil.checkGlError("prepareFramebuffer done");
    }

    /**
     * Releases most of the GL resources we currently hold.
     * <p>
     * Does not release EglCore.
     */
    private void releaseGl() {
        GlUtil.checkGlError("releaseGl start");

        int[] values = new int[1];

        if (mWindowSurface != null) {
            mWindowSurface.release();
            mWindowSurface = null;
        }
        if (mProgram != null) {
            mProgram.release();
            mProgram = null;
        }
        if (mOffscreenTexture > 0) {
            values[0] = mOffscreenTexture;
            GLES20.glDeleteTextures(1, values, 0);
            mOffscreenTexture = -1;
        }
        if (mFramebuffer > 0) {
            values[0] = mFramebuffer;
            GLES20.glDeleteFramebuffers(1, values, 0);
            mFramebuffer = -1;
        }
        if (mDepthBuffer > 0) {
            values[0] = mDepthBuffer;
            GLES20.glDeleteRenderbuffers(1, values, 0);
            mDepthBuffer = -1;
        }
        if (mFullScreen != null) {
            mFullScreen.release(false); // TODO: should be "true"; must ensure mEglCore current
            mFullScreen = null;
        }

        GlUtil.checkGlError("releaseGl done");

        mEglCore.makeNothingCurrent();
    }

    /**
     * Updates the recording state.  Stops or starts recording as needed.
     */
    private void setRecordingEnabled(boolean enabled) {
        if (enabled == mRecordingEnabled) {
            return;
        }
        if (enabled) {
            startEncoder();
        } else {
            stopEncoder();
        }
        mRecordingEnabled = enabled;
    }

    /**
     * Changes the method we use to render frames to the encoder.
     */
    public void setRecordMethod(int recordMethod) {
        Log.d(TAG, "RT: setRecordMethod " + recordMethod);
        mRecordMethod = recordMethod;
    }

    /**
     * Creates the video encoder object and starts the encoder thread.  Creates an EGL
     * surface for encoder input.
     */
    private void startEncoder() {
        Log.d(TAG, "starting to record");
        // Record at 1280x720, regardless of the window dimensions.  The encoder may
        // explode if given "strange" dimensions, e.g. a width that is not a multiple
        // of 16.  We can box it as needed to preserve dimensions.
        final int VIDEO_WIDTH = SystemInfoUtil.getWidth(mContext);
        final int VIDEO_HEIGHT = SystemInfoUtil.getHeight(mContext);
        int windowWidth = mWindowSurface.getWidth();
        int windowHeight = mWindowSurface.getHeight();
        float windowAspect = (float) windowHeight / (float) windowWidth;
        int outWidth, outHeight;
        if (VIDEO_HEIGHT > VIDEO_WIDTH * windowAspect) {
            // limited by narrow width; reduce height
            outWidth = VIDEO_WIDTH;
            outHeight = (int) (VIDEO_WIDTH * windowAspect);
        } else {
            // limited by short height; restrict width
            outHeight = VIDEO_HEIGHT;
            outWidth = (int) (VIDEO_HEIGHT / windowAspect);
        }
        int offX = (VIDEO_WIDTH - outWidth) / 2;
        int offY = (VIDEO_HEIGHT - outHeight) / 2;
        mVideoRect.set(offX, offY, offX + outWidth, offY + outHeight);
        Log.d(TAG, "Adjusting window " + windowWidth + "x" + windowHeight +
                " to +" + offX + ",+" + offY + " " +
                mVideoRect.width() + "x" + mVideoRect.height());

        try {
            mMuxer = new MediaMuxerWrapper(".mp4");

            encoderCore = new MediaVideoEncoder(mMuxer,new MediaEncoder.MediaEncoderListener() {
                @Override
                public void onPrepared(MediaEncoder encoder) {

                }

                @Override
                public void onStopped(MediaEncoder encoder) {

                }
            },VIDEO_WIDTH, VIDEO_HEIGHT);

            audioEncoder = new MediaAudioEncoder(mMuxer,new MediaEncoder.MediaEncoderListener() {
                @Override
                public void onPrepared(MediaEncoder encoder) {

                }

                @Override
                public void onStopped(MediaEncoder encoder) {

                }
            });

            mMuxer.prepare();

            mInputWindowSurface = new WindowSurface(mEglCore, encoderCore.getInputSurface(), true);

            mMuxer.startRecording();


        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }


    }

    /**
     * Stops the video encoder if it's running.
     */
    private void stopEncoder() {
        if (mMuxer != null){
            mMuxer.stopRecording();
        }

        if (mInputWindowSurface != null) {
            mInputWindowSurface.release();
            mInputWindowSurface = null;
        }
    }

    /**
     * Advance state and draw frame in response to a vsync event.
     */
    private void doFrame(long timeStampNanos) {
        // If we're not keeping up 60fps -- maybe something in the system is busy, maybe
        // recording is too expensive, maybe the CPU frequency governor thinks we're
        // not doing and wants to drop the clock frequencies -- we need to drop frames
        // to catch up.  The "timeStampNanos" value is based on the system monotonic
        // clock, as is System.nanoTime(), so we can compare the values directly.
        //
        // Our clumsy collision detection isn't sophisticated enough to deal with large
        // time gaps, but it's nearly cost-free, so we go ahead and do the computation
        // either way.
        //
        // We can reduce the overhead of recording, as well as the size of the movie,
        // by recording at ~30fps instead of the display refresh rate.  As a quick hack
        // we just record every-other frame, using a "recorded previous" flag.


        long diff = System.nanoTime() - timeStampNanos;
        long max = mRefreshPeriodNanos - 2000000;   // if we're within 2ms, don't bother
        if (diff > max) {
            // too much, drop a frame
            Log.d(TAG, "diff is " + (diff / 1000000.0) + " ms, max " + (max / 1000000.0) +
                    ", skipping render");
            mRecordedPrevious = false;
            mDroppedFrames++;
            return;
        }

        boolean swapResult;

        if (!mRecordingEnabled || mRecordedPrevious) {
            mRecordedPrevious = false;
            // Render the scene, swap back to front.
            draw();
            swapResult = mWindowSurface.swapBuffers();
        } else {
            mRecordedPrevious = true;

            // recording
            if (mRecordMethod == RECMETHOD_DRAW_TWICE) {
                //Log.d(TAG, "MODE: draw 2x");

                // Draw for display, swap.
                draw();
                swapResult = mWindowSurface.swapBuffers();

                // Draw for recording, swap.
//                    mVideoEncoder.frameAvailableSoon();
                encoderCore.frameAvailableSoon();
                mInputWindowSurface.makeCurrent();
                // If we don't set the scissor rect, the glClear() we use to draw the
                // light-grey background will draw outside the viewport and muck up our
                // letterboxing.  Might be better if we disabled the test immediately after
                // the glClear().  Of course, if we were clearing the frame background to
                // black it wouldn't matter.
                //
                // We do still need to clear the pixels outside the scissor rect, of course,
                // or we'll get garbage at the edges of the recording.  We can either clear
                // the whole thing and accept that there will be a lot of overdraw, or we
                // can issue multiple scissor/clear calls.  Some GPUs may have a special
                // optimization for zeroing out the color buffer.
                //
                // For now, be lazy and zero the whole thing.  At some point we need to
                // examine the performance here.
                GLES20.glClearColor(0f, 0f, 0f, 1f);
                GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

                GLES20.glViewport(mVideoRect.left, mVideoRect.top,
                        mVideoRect.width(), mVideoRect.height());
                GLES20.glEnable(GLES20.GL_SCISSOR_TEST);
                GLES20.glScissor(mVideoRect.left, mVideoRect.top,
                        mVideoRect.width(), mVideoRect.height());
                draw();
                GLES20.glDisable(GLES20.GL_SCISSOR_TEST);
                mInputWindowSurface.setPresentationTime(timeStampNanos);
                mInputWindowSurface.swapBuffers();

                // Restore.
                GLES20.glViewport(0, 0, mWindowSurface.getWidth(), mWindowSurface.getHeight());
                mWindowSurface.makeCurrent();

            } else /*if (mEglCore.getGlVersion() >= 3 &&
                        mRecordMethod == RECMETHOD_BLIT_FRAMEBUFFER)*/ {
                //Log.d(TAG, "MODE: blitFramebuffer");
                // Draw the frame, but don't swap it yet.
                draw();
                encoderCore.frameAvailableSoon();
                mInputWindowSurface.makeCurrentReadFrom(mWindowSurface);
                // Clear the pixels we're not going to overwrite with the blit.  Once again,
                // this is excessive -- we don't need to clear the entire screen.
                GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
                GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
                GlUtil.checkGlError("before glBlitFramebuffer");
                Log.v(TAG, "glBlitFramebuffer: 0,0," + mWindowSurface.getWidth() + "," +
                        mWindowSurface.getHeight() + "  " + mVideoRect.left + "," +
                        mVideoRect.top + "," + mVideoRect.right + "," + mVideoRect.bottom +
                        "  COLOR_BUFFER GL_NEAREST");
                GLES30.glBlitFramebuffer(
                        0, 0, mWindowSurface.getWidth(), mWindowSurface.getHeight(),
                        mVideoRect.left, mVideoRect.top, mVideoRect.right, mVideoRect.bottom,
                        GLES30.GL_COLOR_BUFFER_BIT, GLES30.GL_NEAREST);
                int err;
                if ((err = GLES30.glGetError()) != GLES30.GL_NO_ERROR) {
                    Log.w(TAG, "ERROR: glBlitFramebuffer failed: 0x" +
                            Integer.toHexString(err));
                }
                mInputWindowSurface.setPresentationTime(timeStampNanos);
                mInputWindowSurface.swapBuffers();

                // Now swap the display buffer.
                mWindowSurface.makeCurrent();
                swapResult = mWindowSurface.swapBuffers();

            }
        }

        if (!swapResult) {
            // This can happen if the Activity stops without waiting for us to halt.
            Log.w(TAG, "swapBuffers failed, killing renderer thread");
            shutdown();
            return;
        }

        // Update the FPS counter.
        //
        // Ideally we'd generate something approximate quickly to make the UI look
        // reasonable, then ease into longer sampling periods.
        final int NUM_FRAMES = 120;
        final long ONE_TRILLION = 1000000000000L;
        if (mFpsCountStartNanos == 0) {
            mFpsCountStartNanos = timeStampNanos;
            mFpsCountFrame = 0;
        } else {
            mFpsCountFrame++;
            if (mFpsCountFrame == NUM_FRAMES) {
                // compute thousands of frames per second
                long elapsed = timeStampNanos - mFpsCountStartNanos;
                mActivityHandler.sendFpsUpdate((int)(NUM_FRAMES * ONE_TRILLION / elapsed),
                        mDroppedFrames);

                // reset
                mFpsCountStartNanos = timeStampNanos;
                mFpsCountFrame = 0;
            }
        }
    }

    /**
     * Draws the scene.
     */
    private void draw() {
        GlUtil.checkGlError("draw start");

        glBitmap.onDrawFrame(getDrawBitmap());

        GlUtil.checkGlError("draw done");
    }

    /**
     * 获取view界面
     * @return
     */
    public Bitmap getDrawBitmap(){
        viewTarget.setDrawingCacheEnabled(true);
        viewTarget.buildDrawingCache();
        Bitmap bitmap= viewTarget.getDrawingCache();

        return bitmap;
    }

    /**
     * Handler for RenderThread.  Used for messages sent from the UI thread to the render thread.
     * <p>
     * The object is created on the render thread, and the various "send" methods are called
     * from the UI thread.
     */
    public static class RenderHandler extends Handler {
        private static final int MSG_SURFACE_CREATED = 0;
        private static final int MSG_SURFACE_CHANGED = 1;
        private static final int MSG_DO_FRAME = 2;
        private static final int MSG_RECORDING_ENABLED = 3;
        private static final int MSG_RECORD_METHOD = 4;
        private static final int MSG_SHUTDOWN = 5;

        // This shouldn't need to be a weak ref, since we'll go away when the Looper quits,
        // but no real harm in it.
        private WeakReference<RenderThread> mWeakRenderThread;

        /**
         * Call from render thread.
         */
        public RenderHandler(RenderThread rt) {
            mWeakRenderThread = new WeakReference<RenderThread>(rt);
        }

        /**
         * Sends the "surface created" message.
         * <p>
         * Call from UI thread.
         */
        public void sendSurfaceCreated() {
            sendMessage(obtainMessage(MSG_SURFACE_CREATED));
        }

        /**
         * Sends the "surface changed" message, forwarding what we got from the SurfaceHolder.
         * <p>
         * Call from UI thread.
         */
        public void sendSurfaceChanged(@SuppressWarnings("unused") int format,
                                       int width, int height) {
            // ignore format
            sendMessage(obtainMessage(MSG_SURFACE_CHANGED, width, height));
        }

        /**
         * Sends the "do frame" message, forwarding the Choreographer event.
         * <p>
         * Call from UI thread.
         */
        public void sendDoFrame(long frameTimeNanos) {
            sendMessage(obtainMessage(MSG_DO_FRAME,
                    (int) (frameTimeNanos >> 32), (int) frameTimeNanos));
        }

        /**
         * Enable or disable recording.
         * <p>
         * Call from non-UI thread.
         */
        public void setRecordingEnabled(boolean enabled) {
            sendMessage(obtainMessage(MSG_RECORDING_ENABLED, enabled ? 1 : 0, 0));
        }

        /**
         * Set the method used to render a frame for the encoder.
         * <p>
         * Call from non-UI thread.
         */
        public void setRecordMethod(int recordMethod) {
            sendMessage(obtainMessage(MSG_RECORD_METHOD, recordMethod, 0));
        }

        /**
         * Sends the "shutdown" message, which tells the render thread to halt.
         * <p>
         * Call from UI thread.
         */
        public void sendShutdown() {
            sendMessage(obtainMessage(MSG_SHUTDOWN));
        }

        @Override  // runs on RenderThread
        public void handleMessage(Message msg) {
            int what = msg.what;
            //Log.d(TAG, "RenderHandler [" + this + "]: what=" + what);

            RenderThread renderThread = mWeakRenderThread.get();
            if (renderThread == null) {
                Log.w(TAG, "RenderHandler.handleMessage: weak ref is null");
                return;
            }

            switch (what) {
                case MSG_SURFACE_CREATED:
                    renderThread.surfaceCreated();
                    break;
                case MSG_SURFACE_CHANGED:
                    renderThread.surfaceChanged(msg.arg1, msg.arg2);
                    break;
                case MSG_DO_FRAME:
                    long timestamp = (((long) msg.arg1) << 32) |
                            (((long) msg.arg2) & 0xffffffffL);
                    renderThread.doFrame(timestamp);
                    break;
                case MSG_RECORDING_ENABLED:
                    renderThread.setRecordingEnabled(msg.arg1 != 0);
                    break;
                case MSG_RECORD_METHOD:
                    renderThread.setRecordMethod(msg.arg1);
                    break;
                case MSG_SHUTDOWN:
                    renderThread.shutdown();
                    break;
                default:
                    throw new RuntimeException("unknown message " + what);
            }
        }
    }
}
