package com.duoduo.annotationpermission.simple;

import android.Manifest;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.duoduo.annotationpermission.R;
import com.duoduo.annotationpermission.library.annotation.AnnotationPermission;
import com.duoduo.annotationpermission.library.annotation.NeedPermission;

@AnnotationPermission
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button = (Button) findViewById(R.id.need_permission);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                needPermission();
            }
        });
    }

    @NeedPermission(permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE})
    private void needPermission() {
        Toast.makeText(getApplicationContext(), "need permission", Toast.LENGTH_LONG).show();
    }
}
