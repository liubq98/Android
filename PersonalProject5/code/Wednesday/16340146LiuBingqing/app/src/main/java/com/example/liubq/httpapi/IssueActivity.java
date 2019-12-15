package com.example.liubq.httpapi;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class IssueActivity extends AppCompatActivity {
    private String user_name;
    private String repo_name;
    private static String BASE_URL = "https://api.github.com";
    private MyRecyclerViewAdapter myAdapter2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.issues);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        user_name = bundle.getString("user_name");
        repo_name = bundle.getString("repo_name");

        myAdapter2 = new MyRecyclerViewAdapter<Issue>(IssueActivity.this, R.layout.issue_item, new ArrayList<Issue>()){
            @Override
            public void convert(MyViewHolder holder, Issue issue_item) {
                ((TextView)holder.getView(R.id.issue_title)).setText("Title：" + issue_item.getTitle());
                ((TextView)holder.getView(R.id.created_at)).setText("创建时间：" + issue_item.getCreated_at());
                ((TextView)holder.getView(R.id.issue_state)).setText("问题状态：" + issue_item.getState());
                ((TextView)holder.getView(R.id.issue_body)).setText("问题描述：" + issue_item.getBody());
            }
        };

        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.recyclerView3);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(myAdapter2);

        getIssues();

        Button addIssue = (Button)findViewById(R.id.addIssue);
        addIssue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectivityManager connectivityManager = (ConnectivityManager) IssueActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                boolean flag = networkInfo != null && networkInfo.isConnected();
                if (!flag)
                {
                    Toast.makeText(IssueActivity.this, "网络连接失败", Toast.LENGTH_SHORT).show();
                    return;
                }

                final String title = ((EditText)findViewById(R.id.issue_title_input)).getText().toString();
                final String body = ((EditText)findViewById(R.id.issue_body_input)).getText().toString();
                try {
                    OkHttpClient build = new OkHttpClient.Builder()
                            .connectTimeout(2, TimeUnit.SECONDS)
                            .readTimeout(2, TimeUnit.SECONDS)
                            .writeTimeout(2, TimeUnit.SECONDS)
                            .build();
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(BASE_URL) //设置网络请求的Url地址
                            .addConverterFactory(GsonConverterFactory.create()) //设置数据解析器
                            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                            .client(build)
                            .build();
                    String message = new Gson().toJson(new PostMessage(title, body));
                    RequestBody req_body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=utf-8"), message);
                    GitHubService request = retrofit.create(GitHubService.class);
                    request.postIssue(user_name, repo_name, req_body)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe(new Observer<Issue>() {
                                @Override
                                public void onNext(Issue issue) {
                                    Toast.makeText(IssueActivity.this, "提交issue成功", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onError(Throwable e) {
                                    Toast.makeText(IssueActivity.this, "提交issues失败："+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onCompleted() {
                                    getIssues();
                                }
                            });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void getIssues() {
        ConnectivityManager connectivityManager = (ConnectivityManager) IssueActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        boolean flag = networkInfo != null && networkInfo.isConnected();
        if (!flag)
        {
            Toast.makeText(IssueActivity.this, "网络连接失败", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            OkHttpClient build = new OkHttpClient.Builder()
                    .connectTimeout(2, TimeUnit.SECONDS)
                    .readTimeout(2, TimeUnit.SECONDS)
                    .writeTimeout(2, TimeUnit.SECONDS)
                    .build();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL) //设置网络请求的Url地址
                    .addConverterFactory(GsonConverterFactory.create()) //设置数据解析器
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .client(build)
                    .build();
            GitHubService request = retrofit.create(GitHubService.class);
            request.getIssue(user_name, repo_name)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Observer<List<Issue>>() {
                        @Override
                        public void onNext(List<Issue> list) {
                            myAdapter2.refresh(list);
                            if (list.size() == 0) {
                                Toast.makeText(IssueActivity.this,"这个项目没有issues", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            List<Issue> list = new ArrayList<>();
                            myAdapter2.refresh(list);
                            Toast.makeText(IssueActivity.this,
                                    e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCompleted() {

                        }
                    });
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

}
