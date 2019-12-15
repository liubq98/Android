package com.example.liubq.storage2;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.transition.Scene;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CommentActivity extends AppCompatActivity {
    public List<Map<String, Object>> listdata;
    private MyAdapter myAdapter;
    static String theName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        Bundle bundle = this.getIntent().getExtras();
        theName = bundle.getString("name");

        final myDB mydb = new myDB(this);
        SQLiteDatabase db = mydb.getReadableDatabase();
        String selection = "name = ?";
        String[] selectionArgs = {theName};
        Cursor cursor1 = db.query("USER", null, selection, selectionArgs, null,null,null);
        cursor1.moveToNext();
        final String theUri = cursor1.getString(4);
        db.close();

        final ListView listview = (ListView) findViewById(R.id.listView);
        listdata = new ArrayList<>();

        db = mydb.getReadableDatabase();
//        String selection = "name = ?";
//        String[] selectionArgs = {theName};
        Cursor cursor = db.query("COMMENTS", null, null, null, null,null,null);
        if (cursor.getCount() == 0)
        {
        }
        else
        {
            while (cursor.moveToNext())
            {
                if(cursor.getString(3) == null)
                {
                    continue;
                }
                String name1 = cursor.getString(1);
                String text1 = cursor.getString(2);
                String time1 = cursor.getString(3);
                String uri1 = cursor.getString(4);
                String num1 = cursor.getString(5);
                Map<String, Object> t = new LinkedHashMap<>();
                t.put("name", name1);
                t.put("text", text1);
                t.put("time", time1);
                t.put("img", uri1);
                t.put("num", num1);
                listdata .add(t);
            }
        }
        myAdapter = new MyAdapter(this, listdata, R.layout.item, new String[]{"name", "time", "text", "img", "num"}, new int[] {R.id.name, R.id.time,R.id.text,R.id.head,R.id.num});
        myAdapter.setOnItemLikeClickListener(new MyAdapter.onItemLikeListener() {
            @Override
            public void addone(int i) {
                Map<String, Object> t = listdata.get(i);
                int count = Integer.parseInt(t.get("num").toString()) + 1;
                t.put("num", String.valueOf(count));
                myAdapter.notifyDataSetChanged();

                SQLiteDatabase db = mydb.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put("num", count);
                String whereClause = "time = ?";
                String[] whereArgs = {t.get("time").toString()};
                db.update("COMMENTS", values, whereClause, whereArgs);
                db.close();

                db = mydb.getWritableDatabase();
                ContentValues values1 = new ContentValues();
                values1.put("name", theName);
                values1.put("time", t.get("time").toString());
                db.insert("LIKE_TABLE", null, values1);
                db.close();
            }

            @Override
            public void subone(int i) {
                Map<String, Object> t = listdata.get(i);
                int count = Integer.parseInt(t.get("num").toString()) - 1;
                t.put("num", String.valueOf(count));
                myAdapter.notifyDataSetChanged();

                SQLiteDatabase db = mydb.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put("num", count);
                String whereClause = "time = ?";
                String[] whereArgs = {t.get("time").toString()};
                db.update("COMMENTS", values, whereClause, whereArgs);
                db.close();

                db = mydb.getWritableDatabase();
                ContentValues values1 = new ContentValues();
                String whereClause1 = "time = ?";
                String[] whereArgs1 = {t.get("time").toString()};
                db.delete("LIKE_TABLE", whereClause, whereArgs);
                db.close();
            }
        });

        listview.setAdapter(myAdapter);


        Button send = (Button)findViewById(R.id.send);
        final EditText comment = (EditText)findViewById(R.id.comment);
        ImageView head = (ImageView)findViewById(R.id.head);
        TextView name = (TextView)findViewById(R.id.name);
        final TextView text = (TextView)findViewById(R.id.text);
        TextView num = (TextView)findViewById(R.id.num);
        ImageView like = (ImageView)findViewById(R.id.like);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(comment.getText().toString()))
                {
                    Toast.makeText(CommentActivity.this, "Comment cannot be empty.", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Date date = new Date();
                    SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                    String time = dateFormat.format(date);

                    Map<String, Object> t = new LinkedHashMap<>();
                    t.put("name", theName);
                    t.put("time", time);
                    t.put("text", comment.getText().toString());
                    if(theUri.equals("default"))
                    {
                        t.put("img", R.mipmap.me);
                    }
                    else
                    {
                        t.put("img", theUri);
                    }
                    t.put("num", "0");
                    listdata.add(t);
                    myAdapter.notifyDataSetChanged();

                    SQLiteDatabase db = mydb.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put("name", theName);
                    values.put("text", comment.getText().toString());
                    values.put("time", time);
                    if(theUri.equals("default"))
                    {
                        values.put("img", R.mipmap.me);
                    }
                    else
                    {
                        values.put("img", theUri);
                    }
                    values.put("num", 0);
                    db.insert("COMMENTS",null,values);
                    db.close();

                    comment.setText("");
                }
            }
        });

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String phonenumber = "\nPhone: ";
                Cursor cursorinfo = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " = \"" + listdata.get(position).get("name").toString() + "\"", null, null);
                if(cursorinfo.getCount() == 0)
                {
                    phonenumber = "\nPhone number not exist.";
                }
                else
                {
                    cursorinfo.moveToFirst();
                    do {
                        phonenumber += cursorinfo.getString(cursorinfo.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)) + "         ";
                    } while (cursorinfo.moveToNext());
                }

                AlertDialog.Builder dialog = new AlertDialog.Builder(CommentActivity.this);
                dialog.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                dialog.setTitle("Info");
                dialog.setMessage("Username: " + listdata.get(position).get("name").toString() + phonenumber);
                dialog.show();
            }

        });
        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                if(theName.equals(listdata.get(position).get("name").toString()))
                {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(CommentActivity.this);
                    dialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            SQLiteDatabase db = mydb.getWritableDatabase();
                            String whereClause = "time = ?";
                            String[] whereArgs = {listdata.get(position).get("time").toString()};
                            db.delete("COMMENTS", whereClause, whereArgs);
                            db.close();

                            listdata.remove(position);
                            myAdapter.notifyDataSetChanged();
                            listview.setAdapter(myAdapter);
                        }

                    });
                    dialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    dialog.setMessage("Delete or not?");
                    dialog.show();
                }
                else
                {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(CommentActivity.this);
                    dialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(CommentActivity.this, "Already reported.", Toast.LENGTH_SHORT).show();
                        }

                    });
                    dialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    dialog.setMessage("Report or not?");
                    dialog.show();
                }

                return true;
            }
        });
    }

    public static String getUser() {
        return theName;
    }
}
