package com.example.holychat.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.example.holychat.Adapter.AddFriendListAdapter;
import com.example.holychat.Entity.FriendsList;
import com.example.holychat.Entity.UserList;
import com.example.holychat.R;
import com.example.holychat.State.Status;
import com.example.holychat.State.URLParameterTpye;
import com.example.holychat.Utils.DynamicBoxUtils;
import com.example.holychat.Utils.HTTPUtils;
import com.example.holychat.Utils.MenuUtils;
import com.example.holychat.Utils.StreamUtils;
import com.example.holychat.Utils.ToastUtils;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import mehdi.sakout.dynamicbox.DynamicBox;

public class AddNewFriendActivity extends AppCompatActivity {

    private TextView no_content_tv;
    private EditText add_Friend_et;
    private Button add_Friend_cancel_btn;
    private ListView add_friend_list;
    private PopupWindow popupWindow=new PopupWindow();
    private ImageButton add_Friend_back_btn;
    private int thisEditText_Width,thisEditText_Height;
    private View popupview;
    private DynamicBox box;
    private static final String param_MyAccount="MyAccount";//加载这个页面时候把我的账号传过来
    private Intent intent;//加载这个页面时候把我的账号传过来
    //数据
    private ArrayList<UserList> userLists;
    //监听
    Handler handler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            Bundle bundle=msg.getData();
            if (msg.what==Status.FRIEND_SEARCH_ACCOUNT)
            {
                String friend_search_account_list= bundle.getString("friend_search_account_list");
                if (friend_search_account_list.length()==2)
                {
                    add_friend_list.setVisibility(View.INVISIBLE);
                    no_content_tv.setVisibility(View.VISIBLE);
                    box.hideAll();
                }else{
                    userLists= JSON.parseObject(friend_search_account_list,new TypeReference<ArrayList<UserList>>(){});
                    add_friend_list.setAdapter(new AddFriendListAdapter(userLists,AddNewFriendActivity.this));
                }
            }
            else
            {
                String friend_search_name_list=bundle.getString("friend_search_name_list");
                if (friend_search_name_list.length()==2)
                {
                    add_friend_list.setVisibility(View.INVISIBLE);
                    no_content_tv.setVisibility(View.VISIBLE);
                    box.hideAll();
                }else {
                    userLists=JSON.parseObject(friend_search_name_list,new TypeReference<ArrayList<UserList>>(){});
                    add_friend_list.setAdapter(new AddFriendListAdapter(userLists,AddNewFriendActivity.this));
                }
            }
            return true;
        }
    });
    Handler handler_add_friend=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            Bundle bundle=msg.getData();
            int add_friend_status=bundle.getInt("add_friend_status");
            if (add_friend_status==Status.FRIEND_ADD_SUCCESS)
            {
                ToastUtils.show_ordinary_toast(AddNewFriendActivity.this,"添加成功！",Toast.LENGTH_SHORT);
                box.hideAll();
            }else {
                ToastUtils.show_ordinary_toast(AddNewFriendActivity.this,"添加失败！您好友列表中可能已经有了该好友",Toast.LENGTH_SHORT);
                box.hideAll();
            }
            return true;
        }
    });
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_friend);
        add_Friend_et=findViewById(R.id.Add_Friend_et);
        add_Friend_cancel_btn=findViewById(R.id.Add_Friend_cancel_btn);
        add_friend_list=findViewById(R.id.Add_Friend_list);
        add_Friend_back_btn=findViewById(R.id.Add_Friend_back_btn);
        no_content_tv=findViewById(R.id.Add_Friend_No_content_tv);
        //事件
        no_content_tv.setVisibility(View.INVISIBLE);
        add_friend_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showAddFriendDialog(position);
            }
        });
        add_Friend_back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        add_Friend_cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        //这是view一个特别的方法，在子线程中延迟执行获取大小，否则在onCreate里面还没画好UI呢，就得到的肯定是0，这么写就没问题了
        add_Friend_et.post(new Runnable() {
            @Override
            public void run() {
                if (add_Friend_et.getWidth()!=0)
                {
                    thisEditText_Width=add_Friend_et.getWidth();

                }
                if (add_Friend_et.getHeight()!=0)
                {
                    thisEditText_Height=add_Friend_et.getHeight();

                }

                popupWindow.setHeight(500);
                popupWindow.setWidth(thisEditText_Width);//延迟获取edittext的大小，为了给popUpWindow设置宽度。
            }
        });
        add_Friend_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                popUpWindowInit(add_Friend_et.getText().toString().trim());
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }
    public void popUpWindowInit(String text)//获取edittext中输入的内容，并显示在这里
    {
        popupview= LayoutInflater.from(AddNewFriendActivity.this).inflate(R.layout.item_add_friend_popwindow,null,false);
        TextView search_friends_account_tv=popupview.findViewById(R.id.Search_friend_by_name);
        TextView search_friends_name_tv=popupview.findViewById(R.id.Search_friend_by_account);
        search_friends_account_tv.setText("搜索好友账号： “"+text+"” ");
        search_friends_name_tv.setText("搜索好友姓名： “"+text+"” ");
        //设置textview的点击事件
        search_friends_account_tv.setClickable(true);
        search_friends_name_tv.setClickable(true);
        search_friends_account_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                no_content_tv.setVisibility(View.INVISIBLE);//每次点击之前都得把那个没有内容时候的提示字体隐藏掉
                new Search_Friend_Thread(URLParameterTpye.FRIENDS_SEARCH_ACCOUNT).start();
                box=new DynamicBox(AddNewFriendActivity.this,add_friend_list);
                box.showLoadingLayout();
                popupWindow.dismiss();
            }
        });
        search_friends_name_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                no_content_tv.setVisibility(View.INVISIBLE);
                new Search_Friend_Thread(URLParameterTpye.FRIENDS_SEARCH_NAME).start();
                box=new DynamicBox(AddNewFriendActivity.this,add_friend_list);
                box.showLoadingLayout();
                popupWindow.dismiss();
            }
        });
        if (popupWindow.isShowing())//如果已经显示了，就应该把它销毁掉，然后重新建立一个，就实现了文字的更新
        {
            popupWindow.dismiss();
            popupWindow.setContentView(popupview);
            popupWindow.showAsDropDown(add_Friend_et);
        }
        else
        {
            popupWindow.setContentView(popupview);
            popupWindow.showAsDropDown(add_Friend_et);
        }
    }
    public void showAddFriendDialog(int item_position)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(AddNewFriendActivity.this);
        final AlertDialog dialog = builder.create();
        dialog.setCancelable(true);//用来设置点击空白处是否退出该dialog
        View dialogview=View.inflate(AddNewFriendActivity.this,R.layout.dialog_add_friend,null);
        Button add_friend_sure_btn=dialogview.findViewById(R.id.Add_friend_sure_btn);
        Button add_friend_cancel_btn=dialogview.findViewById(R.id.Add_friend_cancel_btn);
        add_friend_sure_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //String MyAccount="123456";
                intent=getIntent();
                String MyAccount=intent.getStringExtra(param_MyAccount);//获取传过来的用户名
                UserList userList=(UserList)add_friend_list.getItemAtPosition(item_position);
                FriendsList friendsList=new FriendsList();
                friendsList.setAccount(MyAccount);
                friendsList.setFriends_Account(userList.getUserAccount());
                if (MyAccount.equals(userList.getUserAccount()))
                {
                    ToastUtils.show_ordinary_toast(AddNewFriendActivity.this,"哇！你竟然想着添加你自己~",Toast.LENGTH_SHORT);
                }
                friendsList.setFriends_Name(userList.getUserName());
                Log.i("friend_list",friendsList.toString());
                new Add_Friend_Thread(friendsList).start();
                box.showLoadingLayout();
                dialog.dismiss();
            }
        });
        add_friend_cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setView(dialogview);
        dialog.show();
    }
    class Add_Friend_Thread extends Thread{
        FriendsList friendsList;
        public Add_Friend_Thread(FriendsList friendsList)
        {
            this.friendsList=friendsList;
        }
        @Override
        public void run() {
            super.run();
            String friendsList_JSON=JSON.toJSONString(friendsList);
            try {
                String friendsList_JSON_encode= URLEncoder.encode(friendsList_JSON,"UTF-8");//编码，防止乱码
                HttpURLConnection connection=HTTPUtils.geturlconnection("Friends_Servlet");
                String message=URLParameterTpye.FRIEND_LIST_JSON+"="+friendsList_JSON_encode+"&type="+ URLParameterTpye.FRIENDS_ADD;
                OutputStream outputStream=connection.getOutputStream();
                outputStream.write(message.getBytes());
                if (connection.getResponseCode()==200)
                {
                    InputStream in= connection.getInputStream();
                    int add_friend_status=StreamUtils.GetIntFromServer(in);
                    Bundle bundle=new Bundle();
                    bundle.putInt("add_friend_status",add_friend_status);
                    Message msg=handler_add_friend.obtainMessage();
                    msg.setData(bundle);
                    handler_add_friend.sendMessage(msg);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class Search_Friend_Thread extends Thread{
        String type;
        public Search_Friend_Thread(String type)
        {
            this.type=type;
        }
        @Override
        public void run() {
            super.run();
            HttpURLConnection connection= HTTPUtils.geturlconnection("Friends_Servlet");
            if (type.equals(URLParameterTpye.FRIENDS_SEARCH_ACCOUNT))
            {
                String message=URLParameterTpye.FRIENDS_ACCOUNT+"="+add_Friend_et.getText().toString()+"&type="+URLParameterTpye.FRIENDS_SEARCH_ACCOUNT;
                try {
                    OutputStream outputStream= connection.getOutputStream();
                    outputStream.write(message.getBytes());
                    if (connection.getResponseCode()==200)
                    {
                        InputStream in= connection.getInputStream();
                        String friend_search_account_list= StreamUtils.GetStringFromServer(in);
                        Bundle bundle=new Bundle();
                        bundle.putString("friend_search_account_list",friend_search_account_list);
                        Message msg=handler.obtainMessage();
                        msg.what=Status.FRIEND_SEARCH_ACCOUNT;
                        msg.setData(bundle);
                        handler.sendMessage(msg);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else {
                String mesage=URLParameterTpye.FRIENDS_NAME+"="+add_Friend_et.getText().toString()+"&type="+URLParameterTpye.FRIENDS_SEARCH_NAME;
                try {
                    OutputStream outputStream= connection.getOutputStream();
                    outputStream.write(mesage.getBytes());
                    if (connection.getResponseCode()==200)
                    {
                        InputStream in=connection.getInputStream();
                        String friend_search_name_list=StreamUtils.GetStringFromServer(in);
                        Bundle bundle=new Bundle();
                        bundle.putString("friend_search_name_list",friend_search_name_list);
                        Message msg=handler.obtainMessage();
                        msg.what=Status.FRIEND_SEARCH_NAME;
                        msg.setData(bundle);
                        handler.sendMessage(msg);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}