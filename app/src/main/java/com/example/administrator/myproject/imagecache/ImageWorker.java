/*
 * Copyright (C) 2012 The Android Open Source Project
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

package com.example.administrator.myproject.imagecache;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.util.Log;
import android.widget.ImageView;

import com.example.administrator.myproject.BuildConfig;

import java.io.IOException;
import java.lang.ref.WeakReference;

import pl.droidsonroids.gif.GifDrawable;

/**
 * This class wraps up completing some arbitrary long running work when loading a bitmap to an
 * ImageView. It handles things like using a memory and disk cache, running the work in a background
 * thread and setting a placeholder image.
 */
public class ImageWorker {
    private static final String TAG = "ImageWorker";
    private static final int FADE_IN_TIME = 200;
    private static volatile ImageWorker imageWorker;
    private ImageCache mImageCache;
    private Bitmap mLoadingBitmap;
    private boolean mFadeInBitmap = true;
    private boolean mExitTasksEarly = false;
    protected boolean mPauseWork = false;
    private final Object mPauseWorkLock = new Object();

    protected Resources mResources;

    private static final int MESSAGE_CLEAR = 0;
    private static final int MESSAGE_INIT_DISK_CACHE = 1;
    private static final int MESSAGE_FLUSH = 2;
    private static final int MESSAGE_CLOSE = 3;

    public static ImageWorker getInstance(Context context) {
        if(imageWorker == null) {
            synchronized(ImageWorker.class) {
                if(imageWorker == null) {
                    imageWorker = new ImageWorker(context);
                }
            }
        }

        return imageWorker;
    }

    public ImageWorker(Context context) {
        mResources = context.getResources();
        addImageCache(context);

    }

    /**
     * Load an image specified by the data parameter into an ImageView (override
     * disk cache will be used if an {@link ImageCache} has been added using
     * {@link ImageWorker#addImageCache(Context,ImageCache.ImageCacheParams)}. If the
     * image is found in the memory cache, it is set immediately, otherwise an {@link AsyncTask}
     * will be created to asynchronously load the bitmap.
     *
     * @param data The URL of the image to download.
     * @param imageView The ImageView to bind the downloaded image to.
     * @param listener A listener that will be called back once the image has been loaded.
     */
    public void loadImage(Object data, ImageView imageView, OnImageLoadedListener listener) {
        if (data == null) {
            return;
        }

        byte[] value = null;

        if (mImageCache != null) {
            value = mImageCache.getBitmapFromMemCache(String.valueOf(data));
        }

        if (value != null) {
            // Bitmap found in memory cache
            try {
                imageView.setImageDrawable(new GifDrawable(value));
            } catch (IOException e) {
                e.printStackTrace();
                try {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(value,0,value.length);
                    imageView.setImageDrawable(new BitmapDrawable(bitmap));
                }catch (Exception e1){

                }

            }
            if (listener != null) {
                listener.onImageLoaded(true);
            }
        } else if (cancelPotentialWork(data, imageView)) {
            //BEGIN_INCLUDE(execute_background_task)
            final BitmapWorkerTask task = new BitmapWorkerTask(data, imageView, listener);
            final AsyncDrawable asyncDrawable =
                    new AsyncDrawable(mResources, mLoadingBitmap, task);
            imageView.setImageDrawable(asyncDrawable);

            // NOTE: This uses a custom version of AsyncTask that has been pulled from the
            // framework and slightly modified. Refer to the docs at the top of the class
            // for more info on what was changed.
            task.executeOnExecutor(AsyncTask.DUAL_THREAD_EXECUTOR);
            //END_INCLUDE(execute_background_task)
        }
    }

    /**
     * Load an image specified by the data parameter into an ImageView (override
     * disk cache will be used if an {@link ImageCache} has been added using
     * {@link ImageWorker#addImageCache(Context,ImageCache.ImageCacheParams)}. If the
     * image is found in the memory cache, it is set immediately, otherwise an {@link AsyncTask}
     * will be created to asynchronously load the bitmap.
     *
     * @param data The URL of the image to download.
     * @param imageView The ImageView to bind the downloaded image to.
     */
    public void loadImage(Object data, ImageView imageView) {
        loadImage(data, imageView, null);
    }

    /**
     * Set placeholder bitmap that shows when the the background thread is running.
     *
     * @param bitmap
     */
    public ImageWorker setLoadingImage(Bitmap bitmap) {
        mLoadingBitmap = bitmap;
        return this;
    }

    /**
     * Set placeholder bitmap that shows when the the background thread is running.
     *
     * @param resId
     */
    public ImageWorker setLoadingImage(int resId) {
        mLoadingBitmap = BitmapFactory.decodeResource(mResources, resId);
        return this;
    }


    public void addImageCache(Context context) {//default
        ImageCache.ImageCacheParams cacheParams = new ImageCache.ImageCacheParams(context);
        cacheParams.setMemCacheSizePercent(0.25f);
        mImageCache = ImageCache.getInstance(cacheParams);
        new CacheAsyncTask().execute(MESSAGE_INIT_DISK_CACHE);
    }
    /**
     * Adds an {@link ImageCache} to this {@link ImageWorker} to handle disk and memory bitmap
     * caching.
     * @param context
     * @param cacheParams The cache parameters to use for the image cache.
     * {@link ImageCache.ImageCacheParams#ImageCacheParams(Context, String)}.
     */
    public void addImageCache(Context context,ImageCache.ImageCacheParams cacheParams) {//custom
        if (cacheParams == null){
            addImageCache(context);
            return;
        }
        cacheParams.setMemCacheSizePercent(0.25f);
        mImageCache = ImageCache.getInstance(cacheParams);
        new CacheAsyncTask().execute(MESSAGE_INIT_DISK_CACHE);
    }

    /**
     * Adds an {@link ImageCache} to this {@link ImageWorker} to handle disk and memory bitmap
     * caching.
     * @param activity
     * @param diskCacheDirectoryName See
     * {@link ImageCache.ImageCacheParams#ImageCacheParams(Context, String)}.
     */
    public void addImageCache(Context activity, String diskCacheDirectoryName) {
        ImageCache.ImageCacheParams cacheParams = new ImageCache.ImageCacheParams(activity, diskCacheDirectoryName);
        cacheParams.setMemCacheSizePercent(0.25f);
        mImageCache = ImageCache.getInstance(cacheParams);
        new CacheAsyncTask().execute(MESSAGE_INIT_DISK_CACHE);
    }

    /**
     * If set to true, the image will fade-in once it has been loaded by the background thread.
     */
    public ImageWorker setImageFadeIn(boolean fadeIn) {
        mFadeInBitmap = fadeIn;
        return this;
    }

    public ImageWorker setExitTasksEarly(boolean exitTasksEarly) {
        mExitTasksEarly = exitTasksEarly;
        setPauseWork(false);
        return this;
    }

    /**
     * @return The {@link ImageCache} object currently being used by this ImageWorker.
     */
    protected ImageCache getImageCache() {
        return mImageCache;
    }

    /**
     * Cancels any pending work attached to the provided ImageView.
     * @param imageView
     */
    public static void cancelWork(ImageView imageView) {
        final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);
        if (bitmapWorkerTask != null) {
            bitmapWorkerTask.cancel(true);
            if (BuildConfig.DEBUG_MODE) {
                final Object bitmapData = bitmapWorkerTask.mData;
                Log.d(TAG, "cancelWork - cancelled work for " + bitmapData);
            }
        }
    }

    /**
     * Returns true if the current work has been canceled or if there was no work in
     * progress on this image view.
     * Returns false if the work in progress deals with the same data. The work is not
     * stopped in that case.
     */
    public static boolean cancelPotentialWork(Object data, ImageView imageView) {
        //BEGIN_INCLUDE(cancel_potential_work)
        final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

        if (bitmapWorkerTask != null) {
            final Object bitmapData = bitmapWorkerTask.mData;
            if (bitmapData == null || !bitmapData.equals(data)) {
                bitmapWorkerTask.cancel(true);
                if (BuildConfig.DEBUG_MODE) {
                    Log.d(TAG, "cancelPotentialWork - cancelled work for " + data);
                }
            } else {
                // The same work is already in progress.
                return false;
            }
        }
        return true;
        //END_INCLUDE(cancel_potential_work)
    }

    /**
     * @param imageView Any imageView
     * @return Retrieve the currently active work task (if any) associated with this imageView.
     * null if there is no such task.
     */
    private static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getBitmapWorkerTask();
            }
        }
        return null;
    }

    /**
     * The actual AsyncTask that will asynchronously process the image.
     */
    private class BitmapWorkerTask extends AsyncTask<Void, Void, byte[]> {
        private Object mData;
        private final WeakReference<ImageView> imageViewReference;
        private final OnImageLoadedListener mOnImageLoadedListener;

        public BitmapWorkerTask(Object data, ImageView imageView) {
            mData = data;
            imageViewReference = new WeakReference<ImageView>(imageView);
            mOnImageLoadedListener = null;
        }

        public BitmapWorkerTask(Object data, ImageView imageView, OnImageLoadedListener listener) {
            mData = data;
            imageViewReference = new WeakReference<ImageView>(imageView);
            mOnImageLoadedListener = listener;
        }

        /**
         * Background processing.
         */
        @Override
        protected byte[] doInBackground(Void... params) {
            //BEGIN_INCLUDE(load_bitmap_in_background)
            if (BuildConfig.DEBUG_MODE) {
                Log.d(TAG, "doInBackground - starting work");
            }

            final String dataString = String.valueOf(mData);
            byte[] drawable = null;

            // Wait here if work is paused and the task is not cancelled
            synchronized (mPauseWorkLock) {
                while (mPauseWork && !isCancelled()) {
                    try {
                        mPauseWorkLock.wait();
                    } catch (InterruptedException e) {}
                }
            }

            // If the image cache is available and this task has not been cancelled by another
            // thread and the ImageView that was originally bound to this task is still bound back
            // to this task and our "exit early" flag is not set then try and fetch the bitmap from
            // the cache
            if (mImageCache != null && !isCancelled() && getAttachedImageView() != null
                    && !mExitTasksEarly) {
                drawable = mImageCache.getBitmapFromDiskCache(dataString);
            }

            // If the bitmap was processed and the image cache is available, then add the processed
            // bitmap to the cache for future use. Note we don't check if the task was cancelled
            // here, if it was, and the thread is still running, we may as well add the processed
            // bitmap to our cache as it might be used again in the future
            if (drawable != null) {

                if (mImageCache != null) {
                    mImageCache.addBitmapToCache(dataString, drawable);
                }
            }

            if (BuildConfig.DEBUG_MODE) {
                Log.d(TAG, "doInBackground - finished work");
            }

            return drawable;
            //END_INCLUDE(load_bitmap_in_background)
        }

        /**
         * Once the image is processed, associates it to the imageView
         */
        @Override
        protected void onPostExecute(byte[] value) {
            //BEGIN_INCLUDE(complete_background_work)
            boolean success = false;
            // if cancel was called on this task or the "exit early" flag is set then we're done
            if (isCancelled() || mExitTasksEarly) {
                value = null;
            }

            final ImageView imageView = getAttachedImageView();
            if (value != null && imageView != null) {
                if (BuildConfig.DEBUG_MODE) {
                    Log.d(TAG, "onPostExecute - setting bitmap");
                }
                success = true;
                try {
                    setImageDrawable(imageView, new GifDrawable(value));
                } catch (IOException e) {
                    e.printStackTrace();
                    try {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(value,0,value.length);
                        setImageDrawable(imageView, new BitmapDrawable(bitmap));
                    }catch (Exception e1){

                    }
                }
            }
            if (mOnImageLoadedListener != null) {
                mOnImageLoadedListener.onImageLoaded(success);
            }
            //END_INCLUDE(complete_background_work)
        }

        @Override
        protected void onCancelled(byte[] value) {
            super.onCancelled(value);
            synchronized (mPauseWorkLock) {
                mPauseWorkLock.notifyAll();
            }
        }

        /**
         * Returns the ImageView associated with this task as long as the ImageView's task still
         * points to this task as well. Returns null otherwise.
         */
        private ImageView getAttachedImageView() {
            final ImageView imageView = imageViewReference.get();
            final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

            if (this == bitmapWorkerTask) {
                return imageView;
            }

            return null;
        }
    }

    /**
     * Interface definition for callback on image loaded successfully.
     */
    public interface OnImageLoadedListener {

        /**
         * Called once the image has been loaded.
         * @param success True if the image was loaded successfully, false if
         *                there was an error.
         */
        void onImageLoaded(boolean success);
    }

    /**
     * A custom Drawable that will be attached to the imageView while the work is in progress.
     * Contains a reference to the actual worker task, so that it can be stopped if a new binding is
     * required, and makes sure that only the last started worker process can bind its result,
     * independently of the finish order.
     */
    private static class AsyncDrawable extends BitmapDrawable {
        private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;

        public AsyncDrawable(Resources res, Bitmap bitmap, BitmapWorkerTask bitmapWorkerTask) {
            super(res, bitmap);
            bitmapWorkerTaskReference =
                new WeakReference<BitmapWorkerTask>(bitmapWorkerTask);
        }

        public BitmapWorkerTask getBitmapWorkerTask() {
            return bitmapWorkerTaskReference.get();
        }
    }

    /**
     * Called when the processing is complete and the final drawable should be 
     * set on the ImageView.
     *
     * @param imageView
     * @param drawable
     */
    private void setImageDrawable(ImageView imageView, Drawable drawable) {
        if (mFadeInBitmap) {
            // Transition drawable with a transparent drawable and the final drawable
            final TransitionDrawable td =
                    new TransitionDrawable(new Drawable[] {
                            new ColorDrawable(mResources.getColor(android.R.color.transparent)),
                            drawable
                    });
            // Set background to loading bitmap
            imageView.setBackgroundDrawable(
                    new BitmapDrawable(mResources, mLoadingBitmap));

            imageView.setImageDrawable(td);
            td.startTransition(FADE_IN_TIME);
        } else {
            imageView.setImageDrawable(drawable);
        }
    }

    /**
     * Pause any ongoing background work. This can be used as a temporary
     * measure to improve performance. For example background work could
     * be paused when a ListView or GridView is being scrolled using a
     * {@link android.widget.AbsListView.OnScrollListener} to keep
     * scrolling smooth.
     * <p>
     * If work is paused, be sure setPauseWork(false) is called again
     * before your fragment or activity is destroyed (for example during
     * {@link android.app.Activity#onPause()}), or there is a risk the
     * background thread will never finish.
     */
    public void setPauseWork(boolean pauseWork) {
        synchronized (mPauseWorkLock) {
            mPauseWork = pauseWork;
            if (!mPauseWork) {
                mPauseWorkLock.notifyAll();
            }
        }
    }

    protected class CacheAsyncTask extends AsyncTask<Object, Void, Void> {

        @Override
        protected Void doInBackground(Object... params) {
            switch ((Integer)params[0]) {
                case MESSAGE_CLEAR:
                    clearCacheInternal();
                    break;
                case MESSAGE_INIT_DISK_CACHE:
                    initDiskCacheInternal();
                    break;
                case MESSAGE_FLUSH:
                    flushCacheInternal();
                    break;
                case MESSAGE_CLOSE:
                    closeCacheInternal();
                    break;
            }
            return null;
        }
    }

    protected void initDiskCacheInternal() {
        if (mImageCache != null) {
            mImageCache.initDiskCache();
        }
    }

    protected void clearCacheInternal() {
        if (mImageCache != null) {
            mImageCache.clearCache();
        }
    }

    protected void flushCacheInternal() {
        if (mImageCache != null) {
            mImageCache.flush();
        }
    }

    protected void closeCacheInternal() {
        if (mImageCache != null) {
            mImageCache.close();
            mImageCache = null;
        }
    }

    public void clearCache() {
        new CacheAsyncTask().execute(MESSAGE_CLEAR);
    }

    public void flushCache() {
        new CacheAsyncTask().execute(MESSAGE_FLUSH);
    }

    public void closeCache() {
        new CacheAsyncTask().execute(MESSAGE_CLOSE);
    }
}
