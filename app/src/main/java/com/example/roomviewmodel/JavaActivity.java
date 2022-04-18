
package com.example.roomviewmodel;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.apt_annotation.BindView;
import com.example.apt_library.BindViewTools;

public class JavaActivity extends AppCompatActivity {

    @BindView(R.id.button)
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_java);
        BindViewTools.bind(this);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(JavaActivity.this, "ssssssssss", Toast.LENGTH_SHORT).show();
            }
        });
    }
}