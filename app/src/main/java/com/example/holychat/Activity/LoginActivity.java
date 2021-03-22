package com.example.holychat.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.holychat.R;
import com.example.holychat.State.Status;
import com.example.holychat.State.URLParameterTpye;
import com.example.holychat.Utils.HTTPUtils;
import com.example.holychat.Utils.StatusBarUtil;
import com.example.holychat.Utils.StreamUtils;
import com.example.holychat.Utils.ToastUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;

public class LoginActivity extends AppCompatActivity {
    private Button loginBtn;
    private EditText account_et,psw_et;
    private TextView forget_psw_tv,register_tv;

    //params 参数名
    private static final String param_MyName="MyName";
    private static final String param_MyAccount="MyAccount";

    Handler handler=new Handler(new Handler.Callback() {//接收子线程发来的状态值并更新UI
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            Bundle bundle=msg.getData();
            String MyName=bundle.getString("MyName");
            if (MyName.equals(Status.LOGIN_FAILED_String))
            {
                ToastUtils.show_ordinary_toast(LoginActivity.this,"登录失败",Toast.LENGTH_SHORT);
            }else
            {
                ToastUtils.show_ordinary_toast(LoginActivity.this,MyName,Toast.LENGTH_SHORT);
                Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                intent.putExtra(param_MyName,MyName);
                intent.putExtra(param_MyAccount,account_et.getText().toString());
                startActivity(intent);
                finish();
            }
            return true;
        }
    });
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //设置状态栏沉浸
        //当FitsSystemWindows设置 true 时，会在屏幕最上方预留出状态栏高度的 padding
        StatusBarUtil.setRootViewFitsSystemWindows(this,false);
        //设置状态栏透明
        StatusBarUtil.setTranslucentStatus(this);
        //true=黑色字体  false=白色
        StatusBarUtil.setStatusBarDarkTheme(this, true);

        //初始化控件
        setContentView(R.layout.activity_login);
        loginBtn=findViewById(R.id.Login_btn);
        register_tv=findViewById(R.id.Register_tv);
        account_et=findViewById(R.id.Account_et);
        psw_et=findViewById(R.id.Psw_et);
        forget_psw_tv=findViewById(R.id.ForgetPsw_tv);
        //添加事件
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Login_Thread().start();
                System.out.println(account_et.getText().toString());
            }
        });
        register_tv.setClickable(true);
        register_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
            }
        });
        forget_psw_tv.setClickable(true);
        forget_psw_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,FindPswActivity.class));
            }
        });
    }
    class Login_Thread extends Thread{
        @Override
        public void run() {
            super.run();
            HttpURLConnection connection= HTTPUtils.geturlconnection("Login_Servlet");
            String account=account_et.getText().toString();
            String psw=psw_et.getText().toString();
            String message= URLParameterTpye.ACCOUNT+"="+account+"&"+URLParameterTpye.PASSWORD+"="+psw;
            try {
                OutputStream outputStream=connection.getOutputStream();
                outputStream.write(message.getBytes());
                if (connection.getResponseCode()==200)
                {
                    InputStream in=connection.getInputStream();
                    //int Login_Status= StreamUtils.GetIntFromServer(in);//获取到登录状态
                    String MyName=StreamUtils.GetStringFromServer(in);
                    //将登录状态传给主线程，并更新UI界面
                    Bundle bundle=new Bundle();
                    //bundle.putInt("Login_Status",Login_Status);
                    bundle.putString("MyName",MyName);//登录成功返回用户名
                    Message msg=handler.obtainMessage();
                    msg.setData(bundle);
                    handler.sendMessage(msg);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}