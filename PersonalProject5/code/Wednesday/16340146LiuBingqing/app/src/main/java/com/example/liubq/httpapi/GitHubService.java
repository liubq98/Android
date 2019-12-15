package com.example.liubq.httpapi;

import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;

public interface GitHubService{
        @Headers("Authorization: token 4b70ee0d0ac98f8e6763978e37a07da8f1c71d36")
    @GET("/users/{user_name}/repos")
    Observable<List<Repo>> getRepo(@Path("user_name") String user_name);

        @Headers("Authorization: token 4b70ee0d0ac98f8e6763978e37a07da8f1c71d36")
    @GET("/repos/{user_name}/{repo_name}/issues")
    Observable<List<Issue>> getIssue(@Path("user_name") String user_name, @Path("repo_name") String repo_name);

    @Headers("Authorization: token 4b70ee0d0ac98f8e6763978e37a07da8f1c71d36")
    @POST("/repos/{user_name}/{repo_name}/issues")
    Observable<Issue> postIssue(@Path("user_name") String user_name, @Path("repo_name") String repo_name, @Body RequestBody postMessage);
}

class PostMessage{
    private String title;
    private String body;
    public PostMessage(String title, String body) {
        this.title = title;
        this.body = body;
    }
}
