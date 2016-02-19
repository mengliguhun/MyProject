package com.example.administrator.myproject.httputils;

import com.example.administrator.myproject.ApplicationController;
import com.example.administrator.myproject.bean.FunnyListResult;
import com.example.administrator.myproject.utils.SystemInfoUtil;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by Administrator on 2015/12/23.
 */
public class HttpUtils {
    private static final String API_URL = "https://api.github.com";
    private static final String API_URL_QB = "http://m2.qiushibaike.com";
    private static final String API_URL_QB_PIC ="http://pic.qiushibaike.com/system/pictures/";//http://pic.qiushibaike.com/system/pictures/11457/114570376/medium/app114570376.jpg

    interface QiuBaiServer{

        @GET("/article/list/text")
        Call<FunnyListResult> funnyTextListResult(
                @Query("page") String page,
                @Query("count") String count,
                @Query("readarticles") String readarticles,
                @Query("rqcnt") String rqcnt,
                @Query("r") String r);
        @GET("/article/list/imgrank")
        Call<FunnyListResult> funnyImgListResult(
                @Query("page") String page,
                @Query("count") String count,
                @Query("readarticles") String readarticles,
                @Query("rqcnt") String rqcnt,
                @Query("r") String r);
        @GET("/article/list/video")
        Call<FunnyListResult> funnyVideoListResult(
                @Query("page") String page,
                @Query("count") String count,
                @Query("readarticles") String readarticles,
                @Query("rqcnt") String rqcnt,
                @Query("r") String r);

    }
    private static QiuBaiServer getQiuBaiServer(){
        Interceptor interceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request().newBuilder()
                        .addHeader("User-Agent","qiushibalke_9.1.0_WIFI_auto_19")
                        .addHeader("Source","android_9.1.0")
                        .addHeader("Model",SystemInfoUtil.getFingerPrint())
                        .addHeader("Uuid","IMEI_"+ApplicationController.getInstance().deviceUuid.toString().replaceAll("-",""))
                        .addHeader("Deviceidinfo", ApplicationController.getInstance().deviceidInfo)
//                        .addHeader("Origin", "http://stackexchange.com")
                        .build();
                return chain.proceed(request);
            }
        };

        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.interceptors().add(interceptor);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL_QB)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();

        QiuBaiServer qiuBaiServer = retrofit.create(QiuBaiServer.class);

        return qiuBaiServer;
    }
    public static void getFunnyTextListResult(Callback<FunnyListResult> callback,
           String page,String count,String readarticles,String rqcnt) {

        String r= ApplicationController.getInstance().deviceUuid.toString().substring(ApplicationController.getInstance().deviceUuid.toString().length()-8)+System.currentTimeMillis();
        Call<FunnyListResult> call =  getQiuBaiServer().funnyTextListResult(page, count, readarticles,rqcnt,r);
        call.enqueue(callback);

    }
    public static void getFunnyImgListResult(Callback<FunnyListResult> callback,
                                              String page,String count,String readarticles,String rqcnt) {

        String r= ApplicationController.getInstance().deviceUuid.toString().substring(ApplicationController.getInstance().deviceUuid.toString().length()-8)+System.currentTimeMillis();
        Call<FunnyListResult> call =  getQiuBaiServer().funnyImgListResult(page, count, readarticles, rqcnt, r);
        call.enqueue(callback);

    }
    public static void getFunnyVideoListResult(Callback<FunnyListResult> callback,
                                             String page,String count,String readarticles,String rqcnt) {

        String r= ApplicationController.getInstance().deviceUuid.toString().substring(ApplicationController.getInstance().deviceUuid.toString().length()-8)+System.currentTimeMillis();
        Call<FunnyListResult> call =  getQiuBaiServer().funnyVideoListResult(page, count, readarticles, rqcnt, r);
        call.enqueue(callback);

    }
    public static String getImageUrl(String name,String id){
        String url;
        url = API_URL_QB_PIC+id.substring(0,5)+"/"+id+"/medium/"+name;
        return url;

    }
}
