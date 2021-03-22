package com.example.holychat.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.example.holychat.Entity.UserInfo;
import com.example.holychat.R;
import com.example.holychat.Utils.DialogUtils;
import com.example.holychat.Utils.StatusBarUtil;
import com.example.holychat.Utils.ToastUtils;

public class TestActivitys extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_activitys);

    }

}