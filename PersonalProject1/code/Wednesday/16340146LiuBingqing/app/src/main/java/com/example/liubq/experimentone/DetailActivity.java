package com.example.liubq.experimentone;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class DetailActivity extends AppCompatActivity {
    private int times;
    private boolean clickshop;
    private String name;
    private String bgcolor;
    private String kind;
    private String contain;
    private String circle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_message);
        Bundle bundle = this.getIntent().getExtras();
        name = bundle.getString("name");
        kind = bundle.getString("kind");
        contain = bundle.getString("contain");
        bgcolor = bundle.getString("bgcolor");
        circle = bundle.getString("circle");
        times = 0;
        clickshop = false;
        ImageView addshop = (ImageView) findViewById(R.id.addshop);
        final ImageView star = (ImageView) findViewById(R.id.star);
        ImageView back = (ImageView) findViewById(R.id.back);
        TextView goodsbg = (TextView) findViewById(R.id.bg);
        TextView goodsname = (TextView) findViewById(R.id.goodsname) ;
        TextView goodsdetail = (TextView) findViewById(R.id.detail);
        TextView goodsdetail2 = (TextView) findViewById(R.id.detail2);


        goodsbg.setBackgroundColor(Color.parseColor(bgcolor));
        goodsdetail.setText(kind);
        goodsdetail2.setText("富含 "+contain);
        goodsname.setText(name);


        String[] operation2 = {"分享信息", "不敢兴趣", "查看更多信息", "出错反馈"};
        ArrayAdapter<String> arrayAdapter2 = new ArrayAdapter<String>(this, R.layout.four_info, operation2);
        final ListView listView2 = (ListView) findViewById(R.id.listView2);
        listView2.setAdapter(arrayAdapter2);

        addshop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickshop = true;
                Toast.makeText(DetailActivity.this,"已收藏",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(DetailActivity.this, MainActivity.class);
                intent.putExtra("circle", circle);
                intent.putExtra("name", name);
                intent.putExtra("kind", kind);
                intent.putExtra("contain", contain);
                intent.putExtra("bgcolor", bgcolor);
                setResult(1, intent);
            }
        });
        star.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                times++;
                if (times%2 == 1) {
                    star.setImageResource(R.drawable.full_star);
                } else {
                    star.setImageResource(R.drawable.empty_star);
                }
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (clickshop) {
//                    Intent intent = new Intent();
//                    intent.putExtra("name", name);
//                    intent.putExtra("kind",kind);
//                    intent.putExtra("contain", contain);
//                    intent.putExtra("bgcolor", bgcolor);
//                    DetailActivity.this.setResult(RESULT_OK, intent);
//                } else {
//                    Intent intent = new Intent(DetailActivity.this, MainActivity.class);
//                    Bundle bundle = new Bundle();
//                    bundle.putString("name", name);
//                    bundle.putString("circle", circle);
//                    intent.putExtras(bundle);
//                    setResult(RESULT_CANCELED, intent);
//                }
                DetailActivity.this.finish();
            }
        });

    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
//            if (clickshop) {
//                Intent intent = new Intent();
//                intent.putExtra("name", name);
//                intent.putExtra("kind",kind);
//                intent.putExtra("contain", contain);
//                intent.putExtra("bgcolor", bgcolor);
//                DetailActivity.this.setResult(RESULT_OK, intent);
//            } else {
//                Intent intent = new Intent(DetailActivity.this, MainActivity.class);
//                Bundle bundle = new Bundle();
//                bundle.putString("name", name);
//                bundle.putString("circle", circle);
//                intent.putExtras(bundle);
//                setResult(RESULT_CANCELED, intent);
//            }
            DetailActivity.this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
