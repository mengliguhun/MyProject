package com.example.administrator.myproject.utils;

import android.content.Context;
import android.graphics.Bitmap;

import com.example.administrator.myproject.R;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;
/**
 * 图片初始化工具类
 * @author Administrator
 *
 */
public class ImageLoaderUtil {
	/**
	 * 初始化图片下载
	 * @param context
	 */
	public static void initImageLoaderConfiguration(Context context) {
		File cacheDir = StorageUtils.getCacheDirectory(context);
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				context)
				.threadPoolSize(5)
				// 线程池内加载的数量
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024))
				// You can pass your own memory cache
				// implementation/你可以通过自己的内存缓存实现
				.memoryCacheSize(2 * 1024 * 1024)
				.diskCacheSize(50 * 1024 * 1024)
				.diskCacheFileNameGenerator(new Md5FileNameGenerator())
				// 将保存的时候的URI名称用MD5 加密
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.diskCache(new UnlimitedDiskCache(cacheDir))// 自定义缓存路径
				.build();// 开始构建
		ImageLoader.getInstance().init(config);
	}
	 /**
     * 设置常用的设置项
     * @return
     */
	public static DisplayImageOptions getDisplayImageOptions(){

		DisplayImageOptions options = new DisplayImageOptions.Builder()  
	        .showImageOnLoading(R.drawable.common_signin_btn_icon_disabled_light)            //加载图片时的图片
	        .showImageForEmptyUri(R.drawable.common_signin_btn_icon_disabled_light)         //没有图片资源时的默认图片
	        .showImageOnFail(R.drawable.common_signin_btn_icon_disabled_light)              //加载失败时的图片
	        .cacheInMemory(true)                               //启用内存缓存  
	        .cacheOnDisk(true)                                 //启用外存缓存  
	        .considerExifParams(true)                          //启用EXIF和JPEG图像格式  
	        .bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类型
	        .build();
		
		return options;  
	}
}
