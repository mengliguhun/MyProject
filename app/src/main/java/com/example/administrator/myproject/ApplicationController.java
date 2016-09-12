package com.example.administrator.myproject;

import android.app.Application;
import android.content.Context;

import com.example.administrator.myproject.utils.DeviceUuidFactory;
import com.example.administrator.myproject.utils.ImageLoaderUtil;
import com.example.administrator.myproject.utils.SystemInfoUtil;

import java.util.UUID;

/**
 * Example application for adding an image cache to Volley. 
 * 
 * @author Trey Robinson
 *
 */
public class ApplicationController extends Application {
	
    /**
     * A singleton instance of the application class for easy access in other places 
     */  
    private static ApplicationController sInstance;  
    public String deviceidInfo;
    public UUID deviceUuid;
    @Override
    public void onCreate() {  
        super.onCreate();  
        //全局异常捕获
//        CrashHandler crashHandler = CrashHandler.getInstance();
//        crashHandler.init(getApplicationContext());
        ImageLoaderUtil.initImageLoaderConfiguration(getApplicationContext());
        
        // initialize the singleton  
        sInstance = this;  

//        JPushInterface.setDebugMode(true); 	// 设置开启日志,发布时请关闭日志
//        JPushInterface.init(this);     		// 初始化 JPush

        deviceidInfo = deviceidInfo(getApplicationContext());
        deviceUuid = new DeviceUuidFactory(getApplicationContext()).getDeviceUuid();

    }
  
    /** 
     * @return ApplicationController singleton instance 
     */  
    public static synchronized ApplicationController getInstance() {  
        return sInstance;  
    }
    public  String deviceidInfo(Context context) {
        String Deviceidinfo = "{\"DEVICEID\":"+ SystemInfoUtil.getPhoneImei(context) +",\"RANDOM\":\"\",\"ANDROID_ID\":"+SystemInfoUtil.getAndroidId(context)+",\"SIMNO\":"+SystemInfoUtil.getSimSerialNumber(context)+",\"IMSI\":"+SystemInfoUtil.getSimImsi(context)+",\"SERIAL\":"+SystemInfoUtil.getSerialNumber()+",\"MAC\":"+SystemInfoUtil.getWifiMac(context)+",\"SDK_INT\":"+android.os.Build.VERSION.SDK_INT+"}";
//        String phoneInfo = "Product: " + android.os.Build.PRODUCT;
//        phoneInfo += ", CPU_ABI: " + android.os.Build.CPU_ABI;
//        phoneInfo += ", TAGS: " + android.os.Build.TAGS;
//        phoneInfo += ", VERSION_CODES.BASE: " + android.os.Build.VERSION_CODES.BASE;
//        phoneInfo += ", MODEL: " + android.os.Build.MODEL;
//        phoneInfo += ", SDK: " + android.os.Build.VERSION.SDK;
//        phoneInfo += ", VERSION.RELEASE: " + android.os.Build.VERSION.RELEASE;
//        phoneInfo += ", DEVICE: " + android.os.Build.DEVICE;
//        phoneInfo += ", DISPLAY: " + android.os.Build.DISPLAY;
//        phoneInfo += ", BRAND: " + android.os.Build.BRAND;
//        phoneInfo += ", BOARD: " + android.os.Build.BOARD;
//        phoneInfo += ", FINGERPRINT: " + android.os.Build.FINGERPRINT;
//        phoneInfo += ", ID: " + android.os.Build.ID;
//        phoneInfo += ", MANUFACTURER: " + android.os.Build.MANUFACTURER;
//        phoneInfo += ", USER: " + android.os.Build.USER;
        return Deviceidinfo;
    }
}  