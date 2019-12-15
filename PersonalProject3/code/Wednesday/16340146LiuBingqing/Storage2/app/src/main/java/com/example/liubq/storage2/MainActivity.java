package com.example.liubq.storage2;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    boolean flag = true;
    int id = 0;
    Uri uri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button ok = (Button)findViewById(R.id.ok);
        Button clear = (Button)findViewById(R.id.clear);
        final EditText username = (EditText)findViewById(R.id.username);
        final EditText newp = (EditText)findViewById(R.id.newp);
        final EditText conp = (EditText)findViewById(R.id.conp);
        final ImageView picture = (ImageView)findViewById(R.id.picture);

        RadioButton login = (RadioButton) findViewById(R.id.login);
        RadioButton register = (RadioButton) findViewById(R.id.register);
        RadioGroup radioGroup = (RadioGroup)findViewById(R.id.radioGroup);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.login){
                    flag = true;
                    newp.setVisibility(View.GONE);
                    conp.setHint("Password");
                    conp.setText("");
                    picture.setVisibility(View.GONE);
                }
                else if (checkedId == R.id.register){
                    flag = false;
                    newp.setVisibility(View.VISIBLE);
                    conp.setHint("Confirm Password");
                    conp.setText("");
                    newp.setText("");
                    picture.setVisibility(View.VISIBLE);
                }
            }
        });

        login.setChecked(true);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myDB mydb = new myDB(MainActivity.this);

                String myName = username.getText().toString();
                String myPassword = conp.getText().toString();
                if(flag)
                {
                    if(TextUtils.isEmpty(username.getText().toString()))
                    {
                        Toast.makeText(MainActivity.this, "Username cannot be empty.", Toast.LENGTH_SHORT).show();
                    }
                    else if(TextUtils.isEmpty(conp.getText().toString()))
                    {
                        Toast.makeText(MainActivity.this, "Password cannot be empty.", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        SQLiteDatabase db = mydb.getReadableDatabase();
                        String selection = "name = ?";
                        String[] selectionArgs = {myName};
                        Cursor cursor2 = db.query("USER", null, selection, selectionArgs, null,null,null);
                        if(cursor2.getCount() == 0)
                        {
                            Toast.makeText(MainActivity.this, "Username not existed.", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            cursor2.moveToNext();
                            String pas = cursor2.getString(2);
                            if(pas.equals(myPassword))
                            {
                                Intent intent = new Intent(MainActivity.this, CommentActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("name", username.getText().toString());
                                intent.putExtras(bundle);
                                startActivityForResult(intent, 1);
                            }
                            else
                            {
                                Toast.makeText(MainActivity.this, "Invalid Password", Toast.LENGTH_SHORT).show();
                            }
                        }
                        db.close();
                    }
                }
                else
                {
                    if(TextUtils.isEmpty(username.getText().toString()))
                    {
                        Toast.makeText(MainActivity.this, "Username cannot be empty.", Toast.LENGTH_SHORT).show();
                    }
                    else if(TextUtils.isEmpty(newp.getText().toString()))
                    {
                        Toast.makeText(MainActivity.this, "Password cannot be empty.", Toast.LENGTH_SHORT).show();
                    }
                    else if(newp.getText().toString().equals(conp.getText().toString()))
                    {

                        SQLiteDatabase db = mydb.getReadableDatabase();
                        String selection = "name = ?";
                        String[] selectionArgs = {myName};
                        Cursor cursor1 = db.query("USER", null, selection, selectionArgs, null,null,null);
                        if(cursor1.getCount() != 0)
                        {
                            Toast.makeText(MainActivity.this, "Username already existed.", Toast.LENGTH_SHORT).show();
                            db.close();
                        }
                        else
                        {
                            db = mydb.getWritableDatabase();
                            ContentValues values = new ContentValues();
                            values.put("name", myName);
                            values.put("password", myPassword);
                            if(uri == null)
                            {
                                values.put("img", "default");
                            }
                            else
                            {
                                values.put("img", uri.toString());
                            }
                            db.insert("USER",null,values);
                            db.close();

                            Toast.makeText(MainActivity.this, "Register successfully!", Toast.LENGTH_SHORT).show();
                        }

                    }
                    else
                    {
                        Toast.makeText(MainActivity.this, "Password Mismatch.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username.setText("");
                newp.setText("");
                conp.setText("");
            }
        });

        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent, 0);
            }
        });
    }

    @Override
    protected void onActivityResult(int resquestCode, int resultCode, Intent intentData) {
        super.onActivityResult(resquestCode, resultCode, intentData);
        if(intentData != null)
        {
            uri = intentData.getData();
            ImageView picture = (ImageView)findViewById(R.id.picture);
            picture.setImageURI(uri);
        }
    }
}
