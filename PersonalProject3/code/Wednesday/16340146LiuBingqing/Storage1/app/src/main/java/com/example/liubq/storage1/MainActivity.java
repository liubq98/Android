package com.example.liubq.storage1;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    boolean flag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button ok = (Button)findViewById(R.id.ok);
        Button clear = (Button)findViewById(R.id.clear);
        final EditText newp = (EditText)findViewById(R.id.newp);
        final EditText conp = (EditText)findViewById(R.id.conp);

        final SharedPreferences sharedPref = getSharedPreferences("MY_PREFERENCE", Context.MODE_PRIVATE);
        String password = sharedPref.getString("password","defValue");
        if(password.equals("defValue"))
        {

        }
        else
        {
            flag = false;
            newp.setVisibility(View.GONE);
            conp.setHint("Password");
        }

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(flag)
                {
                    if(TextUtils.isEmpty(newp.getText().toString()))
                    {
                        Toast.makeText(MainActivity.this, "Password cannot be empty.", Toast.LENGTH_SHORT).show();
                    }
                    else if(newp.getText().toString().equals(conp.getText().toString()))
                    {
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("password", conp.getText().toString());
                        editor.commit();

                        Intent intent = new Intent(MainActivity.this, FileEditorActivity.class);
                        startActivityForResult(intent, 1);
                    }
                    else
                    {
                        Toast.makeText(MainActivity.this, "Password Mismatch.", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    if(sharedPref.getString("password","defValue").equals(conp.getText().toString())){
                        Intent intent = new Intent();
                        intent.setClass(MainActivity.this,FileEditorActivity.class);
                        startActivityForResult(intent, 1);
                    }
                    else
                    {
                        Toast.makeText(MainActivity.this, "Invalid Password", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newp.setText("");
                conp.setText("");
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        finish();
    }
}
