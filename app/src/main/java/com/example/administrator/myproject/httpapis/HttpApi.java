package com.example.administrator.myproject.httpapis;

import com.example.administrator.myproject.ApplicationController;
import com.example.administrator.myproject.bean.FunnyListResult;
import com.example.administrator.myproject.utils.SystemInfoUtil;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2015/12/23.
 */
public class HttpApi {
    private static final String API_URL_QB = "http://m2.qiushibaike.com";
    private static final String API_URL_QB_PIC ="http://pic.qiushibaike.com/system/pictures/";//http://pic.qiushibaike.com/system/pictures/11457/114570376/medium/app114570376.jpg

    private static HttpApi instance;
    private static OkHttpClient okHttpClient;
    private static QiuBaiServer qiuBaiServer;

    public static HttpApi getInstance(){
        if (instance == null){
            instance = new HttpApi();
        }
        return instance;
    }

    public HttpApi(){
        //手动创建一个OkHttpClient并设置超时时间
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
        httpClientBuilder.connectTimeout(30, TimeUnit.SECONDS);
        httpClientBuilder.addInterceptor(headers);

        okHttpClient = httpClientBuilder.build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL_QB)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(okHttpClient)
                .build();

        qiuBaiServer = retrofit.create(QiuBaiServer.class);
    }

    /**
     * http headers
     */
    private static Interceptor headers = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request().newBuilder()
                    .addHeader("User-Agent","qiushibalke_9.1.0_WIFI_auto_19")
                    .addHeader("Source","android_9.1.0")
                    .addHeader("Model",SystemInfoUtil.getFingerPrint())
                    .addHeader("Uuid","IMEI_"+ApplicationController.getInstance().deviceUuid.toString().replaceAll("-",""))
                    .addHeader("Deviceidinfo", ApplicationController.getInstance().deviceidInfo)
                    .build();
            return chain.proceed(request);
        }
    };


    public void getFunnyTextListResult(final Subscriber<FunnyListResult> subscriber, String page, String count, String readarticles, String rqcnt) {

        String r= ApplicationController.getInstance().deviceUuid.toString().substring(ApplicationController.getInstance().deviceUuid.toString().length()-8)+System.currentTimeMillis();
        qiuBaiServer.funnyTextListResult(page, count, readarticles,rqcnt,r)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<FunnyListResult>() {
                    @Override
                    public void onCompleted() {
                        subscriber.onCompleted();
                    }

                    @Override
                    public void onError(Throwable e) {
                        subscriber.onError(e);
                    }

                    @Override
                    public void onNext(FunnyListResult funnyListResult) {
                        subscriber.onNext(funnyListResult);
                    }
                });

    }
    public void getFunnyImgListResult(final Subscriber<FunnyListResult> subscriber,
                                              String page,String count,String readarticles,String rqcnt) {

        String r= ApplicationController.getInstance().deviceUuid.toString().substring(ApplicationController.getInstance().deviceUuid.toString().length()-8)+System.currentTimeMillis();
        qiuBaiServer.funnyImgListResult(page, count, readarticles, rqcnt, r)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<FunnyListResult>() {
                    @Override
                    public void onCompleted() {
                        subscriber.onCompleted();
                    }

                    @Override
                    public void onError(Throwable e) {
                        subscriber.onError(e);
                    }

                    @Override
                    public void onNext(FunnyListResult funnyListResult) {
                        subscriber.onNext(funnyListResult);
                    }
                });


    }
    public void getFunnyVideoListResult(final Subscriber<FunnyListResult> subscriber,
                                        String page, String count, String readarticles, String rqcnt) {

        String r= ApplicationController.getInstance().deviceUuid.toString().substring(ApplicationController.getInstance().deviceUuid.toString().length()-8)+System.currentTimeMillis();
        qiuBaiServer.funnyVideoListResult(page, count, readarticles, rqcnt, r)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<FunnyListResult>() {
                    @Override
                    public void onCompleted() {
                        subscriber.onCompleted();
                    }

                    @Override
                    public void onError(Throwable e) {
                        subscriber.onError(e);
                    }

                    @Override
                    public void onNext(FunnyListResult funnyListResult) {
                        subscriber.onNext(funnyListResult);
                    }
                });

    }
    public String getImageUrl(String name,String id){
        String url;
        url = API_URL_QB_PIC+id.substring(0,5)+"/"+id+"/medium/"+name;
        return url;

    }
}
