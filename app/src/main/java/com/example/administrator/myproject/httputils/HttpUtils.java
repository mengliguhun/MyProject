package com.example.administrator.myproject.httputils;

import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.http.GET;
import retrofit.http.Path;
/**
 * Created by Administrator on 2015/12/23.
 */
public class HttpUtils {
    private static final String API_URL = "https://api.github.com";
    public static class Contributor {
        String login;
        int contributions;

        @Override
        public String toString() {
            return login + ", " + contributions;
        }

    }

    interface GitHub{
        @GET("/repos/{owner}/{repo}/contributors")
        Call<List<Contributor>> contributors(@Path("owner") String owner, @Path("repo") String repo);

    }

    public static void getContributors(Callback<List<Contributor>> callback) {
        // Create a very simple REST adapter which points the GitHub API
        // endpoint.
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        GitHub github = retrofit.create(GitHub.class);

        // Fetch and print a list of the contributors to this library.
        Call<List<Contributor>> call =  github.contributors("square", "retrofit");
        call.enqueue(callback);

    }
}
