package com.example.notesave;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MenuAct extends AppCompatActivity {

    LinearLayout btn_notebook;
    TextView textView2;

    //Exit apps
    public void onBackPressed() {
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        btn_notebook = findViewById(R.id.btn_notebook);
        textView2 = findViewById(R.id.textView2);


        btn_notebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gotonote = new Intent(MenuAct.this,MainActivity.class);
                startActivity(gotonote);
            }
        });

        textView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gotomenu2 = new Intent(MenuAct.this,MenuAct.class);
                startActivity(gotomenu2);
            }
        });
    }

}