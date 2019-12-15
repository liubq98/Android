package com.example.liubq.httpapi;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class GithubActivity extends AppCompatActivity {
    private EditText editText;
    private Button search;
    private RecyclerView recyclerView;

    private static String baseURL = "https://api.github.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.github);

        search = (Button)findViewById(R.id.search2);
        editText = (EditText)findViewById(R.id.editText2);
        final MyRecyclerViewAdapter myAdapter = new MyRecyclerViewAdapter<Repo>(GithubActivity.this, R.layout.repos, new ArrayList<Repo>()) {
            @Override
            public void convert(MyViewHolder holder, Repo repo_item) {
                ((TextView)holder.getView(R.id.reponame)).setText("项目名：" + repo_item.getName());
                ((TextView)holder.getView(R.id.repoid)).setText("项目id：" + String.valueOf(repo_item.getId()));
                ((TextView)holder.getView(R.id.has)).setText("存在问题：" + String.valueOf(repo_item.getOpen_issues()));
                ((TextView)holder.getView(R.id.repodes)).setText("项目描述：" + repo_item.getDescription());
            }
        };

        recyclerView = (RecyclerView)findViewById(R.id.recyclerView2);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(myAdapter);
        myAdapter.setOnItemClickListener(new MyRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                Intent intent = new Intent(GithubActivity.this, IssueActivity.class);
                Bundle bundle = new Bundle();
                Repo item = (Repo)myAdapter.getItem(position);
                bundle.putSerializable("repo_name", item.getName());
                bundle.putSerializable("user_name", item.getUserName());
                intent.putExtras(bundle);
                startActivityForResult(intent, 1);
            }
        });


        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectivityManager connectivityManager = (ConnectivityManager) GithubActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                boolean flag = networkInfo != null && networkInfo.isConnected();
                if (!flag) {
                    Toast.makeText(GithubActivity.this, "网络连接失败", Toast.LENGTH_SHORT).show();
                    return;
                }
                final String username = ((EditText)findViewById(R.id.editText2)).getText().toString();

                OkHttpClient build = new OkHttpClient.Builder()
                        .connectTimeout(2, TimeUnit.SECONDS)
                        .readTimeout(2, TimeUnit.SECONDS)
                        .writeTimeout(2, TimeUnit.SECONDS)
                        .build();
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(baseURL) //设置网络请求的Url地址
                        .addConverterFactory(GsonConverterFactory.create()) //设置数据解析器
                        .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                        .client(build)
                        .build();

                GitHubService request = retrofit.create(GitHubService.class);
                request.getRepo(username)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<List<Repo>>() {
                            @Override
                            public void onNext(List<Repo> list) {
                                list.removeIf(new Predicate<Repo>() {
                                    @Override
                                    public boolean test(Repo repoItem) {
                                        return !repoItem.getHas_issues();
                                    }
                                });
                                myAdapter.refresh(list);
                                if (list.size() == 0) {
                                    Toast.makeText(GithubActivity.this,
                                            "该用户不存在可提交issue的项目", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                List<Repo> list = new ArrayList<>();
                                myAdapter.refresh(list);
                                Toast.makeText(GithubActivity.this,
                                        "该用户不存在", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCompleted() {

                            }
                        });
            }
        });


    }

}

