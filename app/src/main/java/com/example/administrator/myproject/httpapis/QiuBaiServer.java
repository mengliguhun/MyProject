package com.example.administrator.myproject.httpapis;

import com.example.administrator.myproject.bean.FunnyListResult;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Administrator on 2016/9/8.
 */
public interface QiuBaiServer{

    @GET("/article/list/text")
    Observable<FunnyListResult> funnyTextListResult(
            @Query("page") String page,
            @Query("count") String count,
            @Query("readarticles") String readarticles,
            @Query("rqcnt") String rqcnt,
            @Query("r") String r);
    @GET("/article/list/imgrank")
    Observable<FunnyListResult> funnyImgListResult(
            @Query("page") String page,
            @Query("count") String count,
            @Query("readarticles") String readarticles,
            @Query("rqcnt") String rqcnt,
            @Query("r") String r);
    @GET("/article/list/video")
    Observable<FunnyListResult> funnyVideoListResult(
            @Query("page") String page,
            @Query("count") String count,
            @Query("readarticles") String readarticles,
            @Query("rqcnt") String rqcnt,
            @Query("r") String r);

}
