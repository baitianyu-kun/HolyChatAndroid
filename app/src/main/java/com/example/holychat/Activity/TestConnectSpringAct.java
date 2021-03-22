package com.example.holychat.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.holychat.R;
import com.example.holychat.Utils.HTTPUtils;
import com.example.holychat.Utils.StreamUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class TestConnectSpringAct extends AppCompatActivity {

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            return true;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_connect_spring);
        //new TestGetStringThread().start();
        new TestSendSpringThread().start();
    }

    class TestGetStringThread extends Thread {
        @Override
        public void run() {
            super.run();
            try {
                URL url = new URL("http://10.0.2.2:8081/SpringMVC_09_Jackson_war_exploded/m1/j3");
                //而使用输出流时，HttpURLConnection默认使用post请求。是无法更改的,所以只要设置了允许输出conn.setDoOutput(true)，则默认走的就是post请求，不走get请求
                HttpURLConnection connection = HTTPUtils.geturlconnection(url, "GET");//这里再设置post是无意义的，根本不起作用
                Log.e("testSpring", String.valueOf(connection.getResponseCode()));
                if (connection.getResponseCode() == 200) {
                    String get = StreamUtils.GetStringFromServer(connection.getInputStream());
                    Log.e("testSpring", get);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class TestSendSpringThread extends Thread {
        @Override
        public void run() {
            super.run();
            URL url = null;
            try {
                String url_str="http://10.0.2.2:8081/SpringMVC_09_Jackson_war_exploded/m1/j3";
                url = new URL(url_str);
                HttpURLConnection connection = HTTPUtils.geturlconnection(url, "POST");
                String message="name=baitianyu";//post请求是在请求体发出去的，所以直接写参数名和参数值就行，不用?name=baitianyu，这玩意还整了半天
                Log.e("testSpring",message);
                OutputStream outputStream=connection.getOutputStream();
                outputStream.write(message.getBytes());
                Log.e("testSpring", String.valueOf(connection.getResponseCode()));

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}