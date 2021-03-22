package com.example.holychat.Fragment;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.example.holychat.Activity.LoginActivity;
import com.example.holychat.Activity.MainActivity;
import com.example.holychat.Adapter.ChatListAdapter;
import com.example.holychat.Entity.Chat;
import com.example.holychat.Listener.KeyboardChangeListener;
import com.example.holychat.R;
import com.example.holychat.State.ListUtils;
import com.example.holychat.State.Status;
import com.example.holychat.State.URLParameterTpye;
import com.example.holychat.Utils.DateUtils;
import com.example.holychat.Utils.HTTPUtils;
import com.example.holychat.Utils.StatusBarUtil;
import com.example.holychat.Utils.StreamUtils;
import com.example.holychat.Utils.ToastUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import mehdi.sakout.dynamicbox.DynamicBox;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatFragment extends Fragment {
    //widgets
    private ListView chatlistview;
    private Button send_btn;
    private TextView friends_title;
    private EditText send_content_text;
    private TextView no_choose_friend_tv;
    private DynamicBox box;//聊天记录加载页面
    private RelativeLayout chat_innerRelativeLayout;//发送和编辑框那里的layout
    private KeyboardChangeListener softKeyboardStateHelper;//键盘监听

    //adapter
    private ChatListAdapter chatListAdapter;

    //params_get_result 获得的参数的值
    private String MyName_get;
    private String FriendName_get;
    private String MyAccount_get;
    private String FriendAccount_get;

    //params 参数名
    private static final String param_MyName="MyName";
    private static final String param_FriendName="FriendName";
    private static final String param_MyAccount="MyAccount";
    private static final String param_FriendAccount="FriendAccount";

    //普通变量
    private final ArrayList<Chat> chatArrayList=new ArrayList<>();
    private boolean ThreadRun=true;
    private boolean MyAccountIsNotNULL=true;//如果我的账户为NULL的话是不会加载任何数据的
    private boolean ServerNotConnected=true;//如果没有从服务器获得数据就一直获
    private int stay_in_this_page_time=0;

    //Handler监听
    Handler handler_chat_get_now=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            Bundle bundle=msg.getData();
            String chatlist_getJson=bundle.getString("chatlist_getJson");
            if (chatlist_getJson.length()==2){
                //数据为空的话就不刷新列表了
            }else
            {//数据不为空再刷新
                ArrayList<Chat> newchatArrayList=JSON.parseObject(chatlist_getJson,new TypeReference<ArrayList<Chat>>(){});//获取以前的聊天记录
                chatArrayList.addAll(newchatArrayList);
                chatListAdapter.notifyDataSetChanged();
                chatlistview.setSelection(ListView.FOCUS_DOWN);
                //System.out.println("success");
                /*for (int i=0;i<newchatArrayList.size();i++)
                {
                    chatArrayList.add(newchatArrayList.get(i));
                    Log.i("new_message",newchatArrayList.get(i).toString());
                }*/
                //Log.i("new_message", String.valueOf(chatArrayList.size()));

                /*for (int i=0;i<chatArrayList.size();i++)
                {
                    System.out.println(i+" "+chatArrayList.get(i).toString());
                }*/
                //no_choose_friend_tv.setVisibility(View.VISIBLE);
                //no_choose_friend_tv.setText(new_message.getMessage_content());
                //chatlistview.setAdapter(new ChatListAdapter(chatArrayList,MyAccount_get,FriendAccount_get,MyName_get,FriendName_get,getContext()));
                //if (chatListAdapter==null)
                //{
                    //chatListAdapter=new ChatListAdapter(chatArrayList,MyAccount_get,FriendAccount_get,MyName_get,FriendName_get,getContext());
                    //chatlistview=getView().findViewById(R.id.Chat_list);
                //}else {

                //}
            }
            return true;
        }
    });
    Handler handler_chat_send=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            Bundle bundle=msg.getData();
            int send_status=bundle.getInt("send_status");
            if (send_status== Status.CHAT_SEND_SUCCESS)
                ToastUtils.show_ordinary_toast(getContext(),"发送成功", Toast.LENGTH_SHORT);
            else
                ToastUtils.show_ordinary_toast(getContext(),"发送失败",Toast.LENGTH_SHORT);
            return true;
        }
    });
    Handler handler_chat_get_before=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            Bundle bundle=msg.getData();
            String chatlist_get_beforeJson=bundle.getString("chatlist_get_beforeJson");
            Log.i("before",chatlist_get_beforeJson);
            if (chatlist_get_beforeJson.length()==2)
            {
                box.hideAll();//没数据也隐藏加载页面
            }
            chatArrayList.clear();
            ArrayList<Chat> newchatArrayList=JSON.parseObject(chatlist_get_beforeJson,new TypeReference<ArrayList<Chat>>(){});//获取以前的聊天记录
            chatArrayList.addAll(newchatArrayList);
            chatListAdapter.notifyDataSetChanged();
            chatlistview.setSelection(ListView.FOCUS_DOWN);
            new Chat_get_now_Thread().start();//加载完以前的数据才启动实时获取信息

            return true;
        }
    });
    Handler handler_chat_get_AI_text=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            Bundle bundle=msg.getData();
            String get_AI_text_json=bundle.getString("get_AI_text");
            JSONObject get_AI_text_json_object=JSON.parseObject(get_AI_text_json);
            String get_AI_text=get_AI_text_json_object.getString("content");
            return true;
        }
    });
    public ChatFragment() {
        // Required empty public constructor
    }

    public static ChatFragment newInstance(String MyName,String FriendName,String MyAccount,String FriendAccount) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putString(param_MyName,MyName);
        args.putString(param_FriendName,FriendName);
        args.putString(param_MyAccount,MyAccount);
        args.putString(param_FriendAccount,FriendAccount);
        fragment.setArguments(args);
        return fragment;
    }

    /*
    实现状态栏随不同fragment改变颜色
     */
    @Override
    //隐藏和显示时候会进入该声明周期,可以设置切换过来时候状态栏颜色还是这个fragment下的颜色
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Log.i("life","onHiddenChanged");
        if (getActivity()==null)
        {//fragment启动时候会先调用这个方法,比onCreate都早,所以应该先判断一下是否为空
        }else
        { StatusBarUtil.setStatusBarDarkTheme(getActivity(), false); }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置状态栏
        StatusBarUtil.setRootViewFitsSystemWindows(getActivity(),false);
        StatusBarUtil.setTranslucentStatus(getActivity());
        StatusBarUtil.setStatusBarDarkTheme(getActivity(), false);
        Log.i("life","onCreates");
        if (getArguments() != null) {
            MyName_get=getArguments().getString(param_MyName);
            FriendName_get=getArguments().getString(param_FriendName);
            MyAccount_get=getArguments().getString(param_MyAccount);
            FriendAccount_get=getArguments().getString(param_FriendAccount);
        }
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        new stay_time_calculate_Thread().start();
        Log.i("life","onactivitycreated");
        if (MyAccount_get.equals("null"))
        {
            chatlistview.setVisibility(View.INVISIBLE);
            send_btn.setVisibility(View.INVISIBLE);
            friends_title.setText(R.string.no_choose);
            send_content_text.setVisibility(View.INVISIBLE);
            chat_innerRelativeLayout.setVisibility(View.INVISIBLE);
            no_choose_friend_tv.setVisibility(View.VISIBLE);//没有选择好友的页面可见
            MyAccountIsNotNULL=false;
        }else {
            MyAccountIsNotNULL=true;
            no_choose_friend_tv.setVisibility(View.INVISIBLE);//没有选择好友的页面不可见
            box=new DynamicBox(getContext(),chatlistview);
            //设置加载内容
            View loading_view=LayoutInflater.from(getContext()).inflate(R.layout.layout_loading,null,false);
            TextView loading_text=loading_view.findViewById(R.id.loading_exception_message);
            loading_text.setText(R.string.chat_loading_text);
            box.showLoadingLayout();//展示聊天记录加载页面

            chatListAdapter=new ChatListAdapter(chatArrayList,MyAccount_get,FriendAccount_get,MyName_get,FriendName_get,getContext());
            chatlistview.setAdapter(chatListAdapter);
            friends_title.setText(FriendName_get);
            send_btn.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onClick(View v) {
                    if (send_content_text.getText().toString().length()==0)
                    {//没有输入东西的话就不让发送~
                        ToastUtils.show_ordinary_toast(getContext(),R.string.no_chat_text_content,Toast.LENGTH_SHORT);
                    }else
                    {
                        chatArrayList.add(new Chat(MyAccount_get,FriendAccount_get,send_content_text.getText().toString(),"2021"));
                        chatListAdapter.notifyDataSetChanged();
                        new Chat_send_Thread((new Chat(
                                MyAccount_get,
                                FriendAccount_get,
                                send_content_text.getText().toString(),
                                DateUtils.getLocalDate_AND_Time()
                        ))).start();
                        //发送消息
                        send_content_text.setText("");//清空编辑栏
                        chatlistview.setSelection(ListView.FOCUS_DOWN);
                    }
                }
            });
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //在这里初始化控件
        View root=inflater.inflate(R.layout.fragment_chat, container, false);
        chatlistview=root.findViewById(R.id.Chat_list);
        send_btn=root.findViewById(R.id.Chat_send_btn);
        friends_title=root.findViewById(R.id.Chat_Friends_title_tv);
        send_content_text=root.findViewById(R.id.Chat_edittext);
        no_choose_friend_tv=root.findViewById(R.id.no_choose_friend_tv);
        chat_innerRelativeLayout=root.findViewById(R.id.Chat_innerRelativeLayout);
        return root;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("life","ondestroy");
        chatArrayList.clear();
        chatListAdapter=null;
        chatlistview=null;
        ThreadRun=false;//停止线程
    }

    @Override
    public void onResume() {
        super.onResume();
        if (MyAccountIsNotNULL)
        {
            new Chat_get_before_Thread().start();//账户不为空才加载数据
        }
        Log.i("life","onResume");
    }

    class stay_time_calculate_Thread extends Thread{
        @Override
        public void run() {
            super.run();
            while (stay_in_this_page_time!=5)
            {
                try {
                    sleep(1000);
                    stay_in_this_page_time=stay_in_this_page_time+1;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class Chat_send_Thread extends Thread
    {
        Chat chat;
        public Chat_send_Thread(Chat chat){
            this.chat=chat;
        }
        @Override
        public void run() {
            super.run();
            HttpURLConnection connection=HTTPUtils.geturlconnection("Chat_Servlet_send");
            String chatsendjson=JSON.toJSONString(chat);
            try {
                String chatsendjson_encode= URLEncoder.encode(chatsendjson,"UTF-8");
                String message=URLParameterTpye.CHAT_SEND_JSON+"="+chatsendjson_encode+"&type="+URLParameterTpye.CHAT_SEND;
                OutputStream outputStream=connection.getOutputStream();
                outputStream.write(message.getBytes());
                if (connection.getResponseCode()==200)
                {
                    InputStream in= connection.getInputStream();
                    int send_status=StreamUtils.GetIntFromServer(in);
                    Bundle bundle=new Bundle();
                    bundle.putInt("send_status",send_status);
                    Message msg=handler_chat_send.obtainMessage();
                    msg.setData(bundle);
                    handler_chat_send.sendMessage(msg);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    class Chat_get_before_Thread extends Thread{//获取以前的信息
        @Override
        public void run() {
            super.run();
            while (ServerNotConnected)
            {
                HttpURLConnection connection=HTTPUtils.geturlconnection("Chat_Servlet_get_before");
                String message= URLParameterTpye.ACCOUNT+"="+MyAccount_get+"&"+URLParameterTpye.FRIENDS_ACCOUNT+"="+FriendAccount_get+"&type="+URLParameterTpye.CHAT_GET_BEFORE;
                try {
                    OutputStream outputStream=connection.getOutputStream();
                    outputStream.write(message.getBytes());
                    if (connection.getResponseCode()==200)
                    {
                        InputStream in=connection.getInputStream();
                        String chatlist_get_beforeJson= StreamUtils.GetStringFromServer(in);
                        Bundle bundle=new Bundle();
                        bundle.putString("chatlist_get_beforeJson",chatlist_get_beforeJson);
                        Message msg=handler_chat_get_before.obtainMessage();
                        msg.setData(bundle);
                        handler_chat_get_before.sendMessage(msg);
                        ServerNotConnected=false;
                        break;
                    }
                }catch (IOException e)
                {
                    e.printStackTrace();
                }
                try {
                    sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    class Chat_get_AI_text_Thread extends Thread{//获取ai回复信息
        String lastcontent;
        public Chat_get_AI_text_Thread(String lastcontent)
        {
            this.lastcontent=lastcontent;
        }
        @Override
        public void run() {
            super.run();
            try {
                URL url = new URL("http://api.qingyunke.com/api.php?key=free&appid=0&msg="+lastcontent);
                HttpURLConnection connection= HTTPUtils.geturlconnection(url,"GET");
                InputStream inputStream=connection.getInputStream();
                String get_AI_text= StreamUtils.GetStringFromServer(inputStream);
                Bundle bundle=new Bundle();
                bundle.putString("get_AI_text",get_AI_text);
                Message msg=handler_chat_get_AI_text.obtainMessage();
                msg.setData(bundle);
                handler_chat_get_AI_text.sendMessage(msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class Chat_get_now_Thread extends Thread//实时获取信息
    {
        @Override
        public void run() {
            super.run();
            while (ThreadRun)
            {
                HttpURLConnection connection=HTTPUtils.geturlconnection("Chat_Servlet_get_now");
                String message= URLParameterTpye.ACCOUNT+"="+MyAccount_get+"&"+URLParameterTpye.FRIENDS_ACCOUNT+"="+FriendAccount_get+"&type="+URLParameterTpye.CHAT_GET;
                try {
                    OutputStream outputStream=connection.getOutputStream();
                    outputStream.write(message.getBytes());
                    if (connection.getResponseCode()==200)
                    {
                        InputStream in=connection.getInputStream();
                        String chatlist_getJson= StreamUtils.GetStringFromServer(in);
                        Log.i("chatlistget",chatlist_getJson);
                        Bundle bundle=new Bundle();
                        bundle.putString("chatlist_getJson",chatlist_getJson);
                        Message msg=handler_chat_get_now.obtainMessage();
                        msg.setData(bundle);
                        handler_chat_get_now.sendMessage(msg);
                    }
                }catch (IOException e)
                {
                    e.printStackTrace();
                }
                try {
                    sleep(4000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}