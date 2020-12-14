package com.example.todo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class CommentActivity extends AppCompatActivity implements View.OnClickListener {

    EditText comment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        ImageButton back = findViewById(R.id.back);
        back.setOnClickListener(this);

        Intent intent = getIntent();
        String text = intent.getStringExtra("text");
        comment = findViewById(R.id.comment);
        comment.setText(text);
        comment.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                return false;
            }
        });

        comment.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus && comment.getText().toString().equals("在此添加备注"))
                {
                    comment.setText("");
                }
                else if(!hasFocus)
                {

                }
            }
        });

        //监听

        Button done = findViewById(R.id.complete);
        done.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(CommentActivity.this,ThirdActivity.class);
        String t = comment.getText().toString();
        intent.putExtra("text",t.length() > 0 ? t : "在此添加备注");
        setResult(1,intent);
        finish();
    }
}