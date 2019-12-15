package com.example.liubq.httpapi;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class BilibiliActivity extends AppCompatActivity {
    private EditText editText;
    private Button search;
    private RecyclerView recyclerView;
    private Handler handler;

    private String content;
    private String picture;
    public List<Map<String, Object>> recyclelistdata;
    private MyRecyclerViewAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bilibili);

        recyclelistdata = new ArrayList<>();

        search = (Button)findViewById(R.id.search);
        editText = (EditText)findViewById(R.id.editText);

        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        myAdapter = new MyRecyclerViewAdapter<Map<String, Object>>(BilibiliActivity.this, R.layout.video_list, recyclelistdata) {
            @Override
            public void convert(MyViewHolder holder, Map<String, Object> s) {
                final ImageView image = holder.getView(R.id.image);
                TextView text1 = holder.getView(R.id.text1);
                TextView text2 = holder.getView(R.id.text2);
                TextView text3 = holder.getView(R.id.text3);
                TextView description = holder.getView(R.id.description);
                final ProgressBar progressBar = holder.getView(R.id.progressBar);

                final String cover = s.get("cover").toString();
                String title = s.get("title").toString();
                final String content = s.get("content").toString();
                String duration = s.get("duration").toString();
                String play = s.get("play").toString();
                String create = s.get("create").toString();
                String video_review = s.get("video_review").toString();

                text1.setText(title);
                text2.setText("播放：" + play + "  评论：" + video_review + "  时长：" + duration);
                text3.setText("创建时间：" + create);
                description.setText(content);


                @SuppressLint("HandlerLeak")
                final Handler handle = new Handler() {
                    public void handleMessage(Message msg) {
                        switch (msg.what) {
                            case 0: image.setImageBitmap((Bitmap) msg.obj);
                                break;
                            case 1: progressBar.setVisibility(View.INVISIBLE);

                        }
                        super.handleMessage(msg);
                    }
                };
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            URL url = new URL(cover);
                            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                            conn.setDoInput(true);
                            conn.connect();
                            InputStream is = conn.getInputStream();
                            Bitmap bitmap = BitmapFactory.decodeStream(is);
                            Message msg = new Message();
                            msg.obj = bitmap;
                            msg.what = 0;
                            handle.sendMessage(msg);
                            Message msg2 = new Message();
                            msg2.what = 1;
                            handle.sendMessage(msg2);
                        }
                        catch (MalformedURLException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

            }
        };

        recyclerView.setAdapter(myAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);

        handler = new Handler();

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread() {
                    public void run() {
                        connection();
                    }
                }.start();
            }
        });
    }

    private void connection() {
        try {
            String str = "https://space.bilibili.com/ajax/top/showTop?mid=";
            String id = editText.getText().toString().trim();
            Pattern pattern = Pattern.compile("[0-9]*");
            Integer.parseInt(id);
            if(pattern.matcher(id).matches())
            {
                String path = str + id ;
                URL url = new URL(path);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000);
                int code = conn.getResponseCode();
                if (code == 200)
                {
                    InputStream inputStream = conn.getInputStream();

                    content = readStream(inputStream);
                    handler.post(runnableUi);
                }
            }
            else
            {
                Looper.prepare();
                Toast.makeText(BilibiliActivity.this, "需要正整数类型数据", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }

        }  catch (NumberFormatException e) {
            Looper.prepare();
            Toast.makeText(BilibiliActivity.this, "需要正整数类型数据", Toast.LENGTH_SHORT).show();
            Looper.loop();
        }catch (Exception e) {
            Looper.prepare();
            Toast.makeText(BilibiliActivity.this, "网络连接失败", Toast.LENGTH_SHORT).show();
            Looper.loop();
            e.printStackTrace();
        }
    }

    // 构建Runnable对象，在runnable中更新界面
    Runnable runnableUi = new Runnable() {
        @Override
        public void run() {
            try {
                JSONObject obj = new JSONObject(content);
                if(obj.getString("status").equals("false"))
                {
                    Toast.makeText(BilibiliActivity.this, "数据库中不存在记录", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    JSONObject jsonobj = obj.getJSONObject("data");
                    Map<String, Object> temp = new LinkedHashMap<>();
                    temp.put("cover", jsonobj.getString("cover"));
                    temp.put("title", jsonobj.getString("title"));
                    temp.put("content",jsonobj.getString("content"));
                    temp.put("duration",jsonobj.getString("duration"));
                    temp.put("play",jsonobj.getString("play"));
                    temp.put("create",jsonobj.getString("create"));
                    temp.put("video_review",jsonobj.getString("video_review"));
                    recyclelistdata.add(temp);
                    myAdapter.notifyDataSetChanged();
                    //myAdapter.addData(temp, myAdapter.getItemCount());
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    private String readStream(InputStream inputStream) throws Exception {
        //定义内存输出流
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        //定义缓存区
        byte[] buffer = new byte[1024];
        int len = -1;
        while ((len = inputStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        inputStream.close();
        //把内存输入流读取到content中
        String c = new String(outStream.toByteArray());
        return c;
    }

}

