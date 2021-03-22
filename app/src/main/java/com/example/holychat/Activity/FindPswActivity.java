package com.example.holychat.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.example.holychat.Entity.UserInfo;
import com.example.holychat.R;
import com.example.holychat.State.Status;
import com.example.holychat.State.URLParameterTpye;
import com.example.holychat.Utils.DialogUtils;
import com.example.holychat.Utils.HTTPUtils;
import com.example.holychat.Utils.StatusBarUtil;
import com.example.holychat.Utils.StreamUtils;
import com.example.holychat.Utils.ToastUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;

public class FindPswActivity extends AppCompatActivity {
    private Button find_psw_sure_btn;
    private EditText find_psw_et;
    private ImageButton find_psw_back_btn;
    Handler handler=new Handler(new Handler.Callback() {//处理密保问题和答案的handler
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            Bundle bundle=msg.getData();
            String security_content=bundle.getString("security_qu");
            if (security_content.equals("user_not_exist"))
            {
                ToastUtils.show_ordinary_toast(FindPswActivity.this,"该用户不存在",Toast.LENGTH_SHORT);
            }
            else
            {
                ShowSecurityQuestionDialog(security_content);
            }
            return true;
        }
    });
    Handler handler1=new Handler(new Handler.Callback() {//处理更新密码的handler
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            Bundle bundle=msg.getData();
            int Update_Psw_Status=bundle.getInt("Update_Psw_Status");
            if (Update_Psw_Status== Status.PASSWORD_CHANGE_SUCCESS)
                ToastUtils.show_ordinary_toast(FindPswActivity.this,"密码修改成功",Toast.LENGTH_SHORT);
            else
                ToastUtils.show_ordinary_toast(FindPswActivity.this,"密码修改失败",Toast.LENGTH_SHORT);
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


        setContentView(R.layout.activity_find_psw);
        find_psw_back_btn=findViewById(R.id.Find_psw_back_btn);
        find_psw_sure_btn=findViewById(R.id.Find_psw_sure_btn);
        find_psw_et=findViewById(R.id.Find_psw_et);
        find_psw_back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        find_psw_sure_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new FindPsw_GetSecurity_Thread().start();
            }
        });
    }
    //用来返回密保问题和答案的线程
    class FindPsw_GetSecurity_Thread extends Thread
    {
        @Override
        public void run() {
            super.run();
            HttpURLConnection connection= HTTPUtils.geturlconnection("FindPsw_Serverlet");
            String message_= URLParameterTpye.ACCOUNT+"="+find_psw_et.getText().toString().trim()+"&type="+URLParameterTpye.GET_SECURITY_QU;
            try {
                OutputStream outputStream=connection.getOutputStream();
                outputStream.write(message_.getBytes());
               if (connection.getResponseCode()==200)
               {
                   InputStream in=connection.getInputStream();
                   String security_qu= StreamUtils.GetStringFromServer(in);
                   Bundle bundle=new Bundle();
                   bundle.putString("security_qu",security_qu);
                   Message msg=handler.obtainMessage();
                   msg.setData(bundle);
                   handler.sendMessage(msg);
               }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    //用来返回更新密码之后的状态值的类
    class FindPsw_UpdatePsw_Thread extends Thread
    {
        String account,password;
        public FindPsw_UpdatePsw_Thread(String account,String psw) {
            this.account=account;
            this.password=psw;
        }
        @Override
        public void run() {
            super.run();
            HttpURLConnection connection= HTTPUtils.geturlconnection("FindPsw_Serverlet");
            String message_= URLParameterTpye.ACCOUNT+"="+account+"&type="+URLParameterTpye.UPDATE_PSW+"&"+URLParameterTpye.PASSWORD+"="+password;
            try {
                OutputStream outputStream = connection.getOutputStream();
                outputStream.write(message_.getBytes());
                if (connection.getResponseCode() == 200) {
                    InputStream in = connection.getInputStream();
                    int update_psw_status = StreamUtils.GetIntFromServer(in);
                    Bundle bundle = new Bundle();
                    bundle.putInt("Update_Psw_Status",update_psw_status);
                    Message msg = handler1.obtainMessage();
                    msg.setData(bundle);
                    handler1.sendMessage(msg);
                }
            }catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
    public void ShowInputNewPsw(String account)
    {
        View dialogView = View.inflate(FindPswActivity.this, R.layout.dialog_input_new_psw, null);
        AlertDialog dialog=DialogUtils.show_custom_dialog(dialogView,FindPswActivity.this,false);
        Button sure_btn=dialogView.findViewById(R.id.Set_new_psw_sure_btn);
        Button cancel_btn=dialogView.findViewById(R.id.Set_new_psw_cancel_btn);
        EditText psw_et=dialogView.findViewById(R.id.Set_new_psw_et);
        EditText psw_sure_et=dialogView.findViewById(R.id.Set_new_psw_sure_et);
        sure_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (psw_et.getText().toString().equals(psw_sure_et.getText().toString()))
                {
                    new FindPsw_UpdatePsw_Thread(account,psw_et.getText().toString()).start();
                    dialog.dismiss();
                }
                else
                {
                    ToastUtils.show_ordinary_toast(FindPswActivity.this,"两次密码不一致",Toast.LENGTH_SHORT);
                }
            }
        });
        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    public void ShowSecurityQuestionDialog(String security_content)
    {
        View dialogView = View.inflate(FindPswActivity.this, R.layout.dialog_security_qu, null);
        AlertDialog dialog=DialogUtils.show_custom_dialog(dialogView,FindPswActivity.this,false);
        Button cancel_btn=dialogView.findViewById(R.id.An_secu_qu_cancel_btn);
        TextView textView=dialogView.findViewById(R.id.An_security_qu_tv);
        Button sure_btn=dialogView.findViewById(R.id.An_secu_qu_sure_btn);
        EditText input_an=dialogView.findViewById(R.id.An_security_qu_et);
        //将json字符串转换为对象
        UserInfo userInfo=JSON.parseObject(security_content,UserInfo.class);
        textView.setText(userInfo.getSecurity_Question());
        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        sure_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (input_an.getText().toString().trim().equals(userInfo.getSecurity_Answer()))
                {
                    ToastUtils.show_ordinary_toast(FindPswActivity.this,"回答正确",Toast.LENGTH_SHORT);
                    ShowInputNewPsw(find_psw_et.getText().toString());
                    dialog.dismiss();
                }
                else
                {
                    ToastUtils.show_ordinary_toast(FindPswActivity.this,"回答错误",Toast.LENGTH_SHORT);
                }
            }
        });
    }
}