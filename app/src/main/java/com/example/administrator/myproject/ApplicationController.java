package com.example.administrator.myproject;

import android.app.Application;
import android.content.Context;

import com.example.administrator.myproject.utils.DeviceUuidFactory;
import com.example.administrator.myproject.utils.SystemInfoUtil;
import com.squareup.leakcanary.LeakCanary;

import java.util.UUID;

import me.drakeet.library.CrashWoodpecker;
import me.drakeet.library.PatchMode;

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

        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
        // Normal app init code...

        //全局异常捕获
//        CrashHandler crashHandler = CrashHandler.getInstance();
//        crashHandler.init(getApplicationContext());

        // initialize the singleton  
        sInstance = this;  

        deviceidInfo = deviceidInfo(getApplicationContext());
        deviceUuid = new DeviceUuidFactory(getApplicationContext()).getDeviceUuid();

        // 开发调试工具，上线注释掉
        if(BuildConfig.DEBUG_MODE){
            CrashWoodpecker.instance()
                    .withKeys("widget", "me.drakeet")
                    .setPatchMode(PatchMode.SHOW_LOG_PAGE)
                    .setPatchDialogUrlToOpen("https://drakeet.me")
                    .setPassToOriginalDefaultHandler(true)
                    .flyTo(this);

            com.facebook.stetho.Stetho.initializeWithDefaults(this);
        }
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