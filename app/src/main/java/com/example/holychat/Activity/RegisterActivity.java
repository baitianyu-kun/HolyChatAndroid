package com.example.holychat.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.example.holychat.Entity.UserInfo;
import com.example.holychat.R;
import com.example.holychat.State.URLParameterTpye;
import com.example.holychat.Utils.DialogUtils;
import com.example.holychat.Utils.HTTPUtils;
import com.example.holychat.Utils.StatusBarUtil;
import com.example.holychat.Utils.StreamUtils;
import com.example.holychat.Utils.ToastUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URLEncoder;

public class RegisterActivity extends AppCompatActivity {
    private Button register_Btn;
    private EditText user_name_et,register_user_psw_et,security_qu_et,security_an_et,register_psw_ensure_et;
    private Toast toast;
    private ImageButton back_login_Btn;

    //params 参数名
    private static final String param_MyName="MyName";
    private static final String param_MyAccount="MyAccount";


    Handler handler=new Handler(new Handler.Callback() {//接收子线程发来的状态值并更新UI
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            Bundle bundle=msg.getData();
            String account=bundle.getString("Account");
            if (account!=null)
            {
                //DialogUtils.show_ordinary_dialog("提示","您申请的账号为："+account+" 请牢记!", RegisterActivity.this);
                show_register_success_dialog(account);
                return true;
            }
            else{
                return false;
            }
        }
    });
    public void show_register_success_dialog(String account)
    {
        AlertDialog.Builder dialog=new AlertDialog.Builder(RegisterActivity.this);
        dialog.setTitle("提示");
        dialog.setMessage("您申请的账号为："+account+" 请牢记!");
        dialog.setNegativeButton("知道了", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ToastUtils.show_ordinary_toast(RegisterActivity.this,"即将为您自动登录",Toast.LENGTH_SHORT);
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        try {
                            sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
                dialog.dismiss();//关闭dialog
                //注册完成后自动登录
                Intent intent=new Intent(RegisterActivity.this,MainActivity.class);
                intent.putExtra(param_MyName,user_name_et.getText().toString());
                intent.putExtra(param_MyAccount,account);
                startActivity(intent);
                finish();
            }
        });
        dialog.show();
    }
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


        setContentView(R.layout.activity_register);
        register_Btn=findViewById(R.id.Register_btn);
        back_login_Btn=findViewById(R.id.Back_login_btn);
        user_name_et=findViewById(R.id.User_name_et);
        register_user_psw_et=findViewById(R.id.Register_user_psw_et);
        security_qu_et=findViewById(R.id.Security_qu_et);
        security_an_et=findViewById(R.id.Security_an_et);
        register_psw_ensure_et=findViewById(R.id.Register_psw_ensure_et);
        back_login_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        register_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (    user_name_et.getText().toString().equals("")
                        ||register_user_psw_et.getText().toString().equals("")
                        ||security_qu_et.getText().toString().equals("")
                        ||security_an_et.getText().toString().equals("")
                        ||register_psw_ensure_et.getText().toString().equals(""))
                {
                    ToastUtils.show_ordinary_toast(RegisterActivity.this,"请填写以上信息",Toast.LENGTH_SHORT);
                }
                else {
                    if (register_user_psw_et.getText().toString().equals(register_psw_ensure_et.getText().toString())) {
                        new Register_Thread().start();
                    }
                    else{
                        ToastUtils.show_ordinary_toast(RegisterActivity.this,"密码和确认密码不匹配",Toast.LENGTH_SHORT);
                    }
                }


            }
        });
    }
    class Register_Thread extends Thread{
        @Override
        public void run() {
            super.run();
            //获取连接
            HttpURLConnection connection= HTTPUtils.geturlconnection("Register_Servlet");
            //封装实体类
            UserInfo userInfo=new UserInfo();
            userInfo.setUser_name(user_name_et.getText().toString());
            userInfo.setPassword(register_user_psw_et.getText().toString());
            userInfo.setSecurity_Question(security_qu_et.getText().toString());
            userInfo.setSecurity_Answer(security_an_et.getText().toString());
            userInfo.setAccount("");//将账户先置为空，方便json转换
            try {
                String register_json= URLEncoder.encode(JSON.toJSONString(userInfo),"UTF-8");
                String message= URLParameterTpye.REGISTER_JSON+"="+register_json;
                //将json字符串传给后端
                OutputStream outputStream=connection.getOutputStream();
                outputStream.write(message.getBytes());
                if (connection.getResponseCode()==200)
                {
                    InputStream in=connection.getInputStream();
                    String account= StreamUtils.GetStringFromServer(in);
                    //发送给handler
                    Bundle bundle=new Bundle();
                    bundle.putString("Account",account);
                    Message msg=handler.obtainMessage();
                    msg.setData(bundle);
                    handler.sendMessage(msg);
                }


            }catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}