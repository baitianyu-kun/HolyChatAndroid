package com.example.holychat.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;

import com.example.holychat.Entity.Chat;
import com.example.holychat.Entity.FriendsList;
import com.example.holychat.Fragment.ChatFragment;
import com.example.holychat.Fragment.FriendsFragment;
import com.example.holychat.Fragment.UserFragment;
import com.example.holychat.R;
import com.example.holychat.State.HashMapKeyType;
import com.example.holychat.State.ListUtils;
import com.example.holychat.State.Status;
import com.example.holychat.Utils.StatusBarUtil;
import com.example.holychat.Utils.StringUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    //widgets
    public BottomNavigationView bottomNavigationView;

    //normal variable
    private int lastIndex;
    public List<Fragment> mFragments;
    private String MyAccount_get;
    private String MyName_get;

    //params 参数名
    private static final String param_MyName="MyName";
    private static final String param_MyAccount="MyAccount";

    //interface
    public interface MyTouchListener
    {
        public void onTouch(MotionEvent motionEvent);
    }
    private MyTouchListener myTouchListener;
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        //将触摸事件传递给回调函数
        if (null != myTouchListener) {
            myTouchListener.onTouch(ev);
        }
        return super.dispatchTouchEvent(ev);
    }
    /**
     * 用于注册回调事件
     */
    public void registerMyTouchListener(MyTouchListener myTouchListener) {
        this.myTouchListener = myTouchListener;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        //从其他activity中获取传输过来的数据
        Intent intent=getIntent();
        MyAccount_get=intent.getStringExtra(param_MyAccount);
        MyName_get=intent.getStringExtra(param_MyName);
        //测试用
        //MyName_get="baitanyu";
        //MyAccount_get="123456";
        //MyName_get="朱毅";
        //MyAccount_get="123457";
        initFragment();//初始化fragment
        bottomNavigationView=findViewById(R.id.bottom_navigation_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.navigation_chat:
                        ChangeFragment(0);
                        break;
                    case R.id.navigation_friends:
                        ChangeFragment(1);
                        break;
                    case R.id.navigation_me:
                        ChangeFragment(2);
                        break;
                }
                return true;
            }
        });
    }
    public void initFragment()//初始化fragment
    {
        mFragments = new ArrayList<>();
        //mFragments.add(new ChatFragment().newInstance("baitiayu","wang","null","123457"));
        //mFragments.add(new FriendsFragment().newInstance("123456","baitiayu"));//初始化fragment时候传参数进去
        mFragments.add(new ChatFragment().newInstance(MyName_get,"null","null","null"));
        mFragments.add(new FriendsFragment().newInstance(MyAccount_get,MyName_get));
        mFragments.add(new UserFragment().newInstance(MyName_get));//传进去用户名
        // 初始化展示第一个Fragment
        ChangeFragment(0);
    }
    public void removeFragment(int position)
    {
        Fragment currentfragment=mFragments.get(position);
        getSupportFragmentManager().beginTransaction().remove(currentfragment).commit();
        mFragments.remove(position);//去掉首个fragment

    }
    public void addFragment(int position ,Fragment fragment)
    {
        mFragments.add(position,fragment);//添加fragment到首个位置
    }
    public void setBottomNavigation_to_chat()
    {
        bottomNavigationView.setSelectedItemId(R.id.navigation_chat);//设置页面跳转到chat页面
    }

    public void ChangeFragment(int position)//更改fragment
    {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment currentFragment = mFragments.get(position);
        Fragment lastFragment = mFragments.get(lastIndex);
        lastIndex = position;//上一个fragment的位置
        ft.hide(lastFragment);
        if (!currentFragment.isAdded()) {//判断当前页面有没有被加进来
            getSupportFragmentManager().beginTransaction().remove(currentFragment).commit();
            ft.add(R.id.fragmentlayout, currentFragment);
        }
        ft.show(currentFragment);
        ft.commit();
    }
}