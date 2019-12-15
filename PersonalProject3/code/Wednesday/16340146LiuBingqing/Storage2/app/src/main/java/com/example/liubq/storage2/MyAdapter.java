package com.example.liubq.storage2;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MyAdapter extends SimpleAdapter {
    myDB mydb;

    public MyAdapter(Context context, List<Map<String, Object>> list, int id, String[] source, int[] dest) {
        super(context, list, id, source, dest);
        mydb = new myDB(context);
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        View v = super.getView(i, view, viewGroup);

        ImageView head = (ImageView)v.findViewById(R.id.head);
        TextView name = (TextView)v.findViewById(R.id.name);
        final TextView text = (TextView)v.findViewById(R.id.text);
        TextView num = (TextView)v.findViewById(R.id.num);
        final ImageView like = (ImageView)v.findViewById(R.id.like);
        TextView time = (TextView)v.findViewById(R.id.time);

        SQLiteDatabase db = mydb.getReadableDatabase();
        final String selection = "name = ?";
        String[] selectionArgs = {CommentActivity.getUser()};
        Cursor cursor = db.query("LIKE_TABLE", null, selection, selectionArgs, null,null,null);
        if (cursor.getCount() == 0)
        {
        }
        else
        {
            while (cursor.moveToNext())
            {
                if(time.getText().toString().equals(cursor.getString(2)))
                {
                    like.setTag("red");
                    like.setImageResource(R.mipmap.red);
                    break;
                }
                like.setTag("white");
                like.setImageResource(R.mipmap.white);
            }
        }
        db.close();


        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(like.getTag().toString().equals("white"))
                {
                    like.setTag("red");
                    like.setImageResource(R.mipmap.red);
                    mOnItemLikeListener.addone(i);
                }
                else
                {
                    like.setTag("white");
                    like.setImageResource(R.mipmap.white);
                    mOnItemLikeListener.subone(i);
                }
            }
        });

        return v;
    }


    public interface onItemLikeListener {
        void addone(int i);
        void subone(int i);
    }

    private onItemLikeListener mOnItemLikeListener;

    public void setOnItemLikeClickListener(onItemLikeListener mOnItemLikeListener) {
        this.mOnItemLikeListener = mOnItemLikeListener;
    }


}
