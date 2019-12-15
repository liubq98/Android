package com.example.liubq.experimentone;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.OvershootInLeftAnimator;


public class MainActivity extends AppCompatActivity {
    private  String message = "图片搜索成功";

    public List<Map<String, Object>> listdata;
    public List<Map<String, Object>> recyclelistdata;
    private SimpleAdapter simpleAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initdata();

//        RadioGroup radioGroup = (RadioGroup)findViewById(R.id.radioGroup);
//        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                if(checkedId == R.id.radioButton1){
//                    Toast.makeText(getApplication(), "图片被选中",Toast.LENGTH_SHORT).show();
//                    message = "图片搜索成功";
//                }
//                else if (checkedId == R.id.radioButton2){
//                    Toast.makeText(getApplication(), "视频被选中",Toast.LENGTH_SHORT).show();
//                    message = "视频搜索成功";
//                }
//                else if(checkedId == R.id.radioButton3){
//                    Toast.makeText(getApplication(), "问答被选中",Toast.LENGTH_SHORT).show();
//                    message = "问答搜索成功";
//                }
//                else{
//                    Toast.makeText(getApplication(), "资讯被选中",Toast.LENGTH_SHORT).show();
//                    message = "资讯搜索成功";
//                }
//            }
//        });
//
//        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
//        alertDialog.setTitle("提示").setMessage(message);
//        alertDialog.setPositiveButton("确定", new DialogInterface.OnClickListener(){
//                    @Override
//                    public void onClick(DialogInterface dialogInterface,int i) {
//                        Toast.makeText(getApplication(), "对话框“确定”按钮被点击", Toast.LENGTH_SHORT).show();
//                    }});
//        alertDialog.setNegativeButton("取消", new DialogInterface.OnClickListener(){
//                    @Override
//                    public  void onClick(DialogInterface dialogInterface,int i){
//                        Toast.makeText(getApplication(), "对话框“取消”按钮被点击", Toast.LENGTH_SHORT).show();
//                    }});
//
//        final AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(this);
//        alertDialog2.setTitle("提示").setMessage("搜索失败");
//        alertDialog2.setPositiveButton("确定", new DialogInterface.OnClickListener(){
//            @Override
//            public void onClick(DialogInterface dialogInterface,int i) {
//                Toast.makeText(getApplication(), "对话框“确定”按钮被点击", Toast.LENGTH_SHORT).show();
//            }});
//        alertDialog2.setNegativeButton("取消", new DialogInterface.OnClickListener(){
//            @Override
//            public  void onClick(DialogInterface dialogInterface,int i){
//                Toast.makeText(getApplication(), "对话框“取消”按钮被点击", Toast.LENGTH_SHORT).show();
//            }});
//
//        Button button = (Button)findViewById(R.id.button);
//        final EditText editText = (EditText)findViewById(R.id.editText);
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //事件处理
//                if(TextUtils.isEmpty(editText.getText().toString()))
//                {
//                    // 弹出Toast消息
//                    Toast.makeText(MainActivity.this, "搜索内容不能为空", Toast.LENGTH_SHORT).show();
//                }
//                else if(editText.getText().toString().equals("Health"))
//                {
//                    alertDialog.setMessage(message);
//                    alertDialog.show();
//                }
//                else
//                {
//                    alertDialog2.show();
//                }
//            }
//        });


        final RecyclerView recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        final MyRecyclerViewAdapter<Map<String, Object>> myAdapter = new MyRecyclerViewAdapter<Map<String, Object>>(MainActivity.this, R.layout.food_list, recyclelistdata) {
            @Override
            public void convert(MyViewHolder holder, Map<String, Object> s) {
                // Colloction是自定义的一个类，封装了数据信息，也可以直接将数据做成一个Map，那么这里就是Map<String, Object>
                TextView first = holder.getView(R.id.theClass);
                String m = s.get("circle").toString();
                first.setText(m);
                TextView name = holder.getView(R.id.theName);
                String m2 = s.get("name").toString();
                name.setText(m2);
            }
        };
        ScaleInAnimationAdapter animationAdapter = new ScaleInAnimationAdapter(myAdapter);
        animationAdapter.setDuration(1000);
        recyclerView.setAdapter(animationAdapter);
        recyclerView.setItemAnimator(new OvershootInLeftAnimator());

        myAdapter.setOnItemClickListener(new MyRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                List<Map<String, Object>> temp = myAdapter.getList();
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("name", temp.get(position).get("name").toString());
                bundle.putString("kind", temp.get(position).get("kind").toString());
                bundle.putString("contain", temp.get(position).get("contain").toString());
                bundle.putString("bgcolor", temp.get(position).get("bgcolor").toString());
                bundle.putString("circle", temp.get(position).get("circle").toString());
                intent.putExtras(bundle);
                startActivityForResult(intent, 1);
            }

            @Override
            public void onLongClick(int position) {
                List<Map<String, Object>> temp = myAdapter.getList();
                Toast.makeText(MainActivity.this, "删除"+temp.get(position).get("name").toString(), Toast.LENGTH_SHORT).show();
                myAdapter.remove(position);
            }
        });


        final ListView listview = (ListView) findViewById(R.id.listView);
        listdata = new ArrayList<>();
        Map<String, Object> temp = new LinkedHashMap<>();
        temp.put("circle","*");
        temp.put("name", "收藏夹");
        temp.put("bgcolor", "");
        temp.put("detail", "");
        temp.put("kind", "");
        listdata.add(temp);
        simpleAdapter = new SimpleAdapter(this, listdata, R.layout.collections_list, new String[]{"circle", "name"}, new int[] {R.id.theClass,R.id.theName});
        listview.setAdapter(simpleAdapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) return;
                //List<Map<String, Object>> temp =simpleAdapter.getList;
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("name", listdata.get(position).get("name").toString());
                bundle.putString("kind", listdata.get(position).get("kind").toString());
                bundle.putString("contain", listdata.get(position).get("contain").toString());
                bundle.putString("bgcolor", listdata.get(position).get("bgcolor").toString());
                bundle.putString("circle", listdata.get(position).get("circle").toString());
                intent.putExtras(bundle);
                startActivityForResult(intent, 1);
            }

        });
        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) return false;
                final int pos = position;

                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listdata.remove(pos);
                        simpleAdapter.notifyDataSetChanged();
                        listview.setAdapter(simpleAdapter);
                    }

                });
                dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                dialog.setTitle("删除");
                dialog.setMessage("确定删除" + listdata.get(pos).get("name").toString() + "?");
                dialog.show();
                return true;
            }
        });


        final FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.btn);
        floatingActionButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (recyclerView.getVisibility() == View.VISIBLE) {
                    floatingActionButton.setImageResource(R.mipmap.mainpage);
                    recyclerView.setVisibility(View.INVISIBLE);
                    listview.setVisibility(View.VISIBLE);

                } else {
                    floatingActionButton.setImageResource(R.mipmap.collect);
                    recyclerView.setVisibility(View.VISIBLE);
                    listview.setVisibility(View.INVISIBLE);
                }
            }
        });


    }

    public void initdata() {
        recyclelistdata = new ArrayList<>();
        recyclelistdata.add(addRMap("大豆", "粮", "粮食", "蛋白质", "#BB4C3B"));
        recyclelistdata.add(addRMap("十字花科蔬菜", "蔬", "蔬菜", "维生素C", "#C48D30"));
        recyclelistdata.add(addRMap("牛奶", "饮", "饮品", "钙", "#4469B0"));
        recyclelistdata.add(addRMap("海鱼", "肉", "肉食", "蛋白质", "#20A17B"));
        recyclelistdata.add(addRMap("菌菇类", "蔬", "蔬菜", "微量元素", "#BB4C3B"));
        recyclelistdata.add(addRMap("番茄", "蔬", "蔬菜", "番茄红素", "#4469B0"));
        recyclelistdata.add(addRMap("胡萝卜", "蔬", "蔬菜", "胡萝卜素", "#20A17B"));
        recyclelistdata.add(addRMap("荞麦", "粮", "粮食", "膳食纤维", "#BB4C3B"));
        recyclelistdata.add(addRMap("鸡蛋", "杂", "杂", "几乎所有营养物质", "#C48D30"));
    }
    public Map<String, Object> addRMap(String n, String c, String kind, String contain, String bgcolor) {
        Map<String, Object> temp = new LinkedHashMap<>();
        temp.put("name", n);
        temp.put("circle",c);
        temp.put("kind",kind);
        temp.put("contain",contain);
        temp.put("bgcolor",bgcolor);
        return temp;
    }


    @Override
    protected void onActivityResult(int resquestCode, int resultCode, Intent intentData) {
        super.onActivityResult(resquestCode, resultCode, intentData);
        if (resquestCode == 1) {
            if (resultCode == 1) {
                Map<String, Object> t = new LinkedHashMap<>();
                t.put("circle", intentData.getSerializableExtra("circle"));
                t.put("name", intentData.getSerializableExtra("name"));
                t.put("kind", intentData.getSerializableExtra("kind"));
                t.put("contain", intentData.getSerializableExtra("contain"));
                t.put("bgcolor", intentData.getSerializableExtra("bgcolor"));
                listdata.add(t);
                simpleAdapter.notifyDataSetChanged();
            }
        }
    }
}
