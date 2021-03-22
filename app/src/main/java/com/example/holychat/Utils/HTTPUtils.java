package com.example.holychat.Utils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class HTTPUtils {
    public static HttpURLConnection geturlconnection(String serverlet_content)
    {
        HttpURLConnection connection=null;
        try {
            URL url = new URL("http://10.0.2.2:8081//HolyChatServer_war_exploded/"+serverlet_content);
            //URL url = new URL("http://192.168.1.105:8081//HolyChatServer_war_exploded/"+serverlet_content);
           // URL url = new URL("http://39.101.203.10:8080//HolyChatServer-1.0-SNAPSHOT/"+serverlet_content);
            //SpringUrl测试
            //URL url = new URL("http://10.0.2.2:8081/SpringMVC_09_Jackson_war_exploded/m1/j2");
            connection = (HttpURLConnection) url.openConnection();
            //设置属性
            connection.setRequestMethod("POST");
            connection.setReadTimeout(8000);
            connection.setConnectTimeout(8000);
            //设置输入流和输出流,都设置为true
            connection.setDoOutput(true);
            connection.setDoInput(true);
            //设置http请求头(可以参照:http://tools.jb51.net/table/http_header)
            connection.setRequestProperty("Accept", "text/plain, text/html,text/json");//指定客户端能够接收的内容类型
            connection.setRequestProperty("Connection", "keep-alive"); //http1.1
        }catch (IOException e)
        {
            e.printStackTrace();
        }
        return connection;
    }
    public static HttpURLConnection geturlconnection(URL url,String get_type)
    {
        HttpURLConnection connection=null;
        try {
            connection = (HttpURLConnection) url.openConnection();
            //设置属性
            connection.setRequestMethod(get_type);
            connection.setReadTimeout(8000);
            connection.setConnectTimeout(8000);
            //设置输入流和输出流,都设置为true
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestProperty("Accept", "text/plain, text/html,text/json");//指定客户端能够接收的内容类型
            connection.setRequestProperty("Connection", "keep-alive"); //http1.1
        }catch (IOException e)
        {
            e.printStackTrace();
        }
        return connection;
    }
}
