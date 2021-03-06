package com.example.holychat.Fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.example.holychat.Activity.AddNewFriendActivity;
import com.example.holychat.Activity.MainActivity;
import com.example.holychat.Adapter.FriendsListAdapter;
import com.example.holychat.Entity.FriendsList;
import com.example.holychat.R;
import com.example.holychat.State.ListUtils;
import com.example.holychat.State.Status;
import com.example.holychat.State.URLParameterTpye;
import com.example.holychat.Utils.DialogUtils;
import com.example.holychat.Utils.HTTPUtils;
import com.example.holychat.Utils.MenuUtils;
import com.example.holychat.Utils.StatusBarUtil;
import com.example.holychat.Utils.StreamUtils;
import com.example.holychat.Utils.StringUtils;
import com.example.holychat.Utils.ToastUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

import mehdi.sakout.dynamicbox.DynamicBox;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FriendsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FriendsFragment extends Fragment {
    //widgets
    private ListView friendslistview;
    private FloatingActionButton addfriendsfloatbtn;
    private ImageView refresh_friend_list;

    //adapter
    private FriendsListAdapter friendsListAdapter;
    //??????activity????????????
    private int lastTouchX,lastTouchY;
    private MainActivity.MyTouchListener myTouchListener=new MainActivity.MyTouchListener() {
        @Override
        public void onTouch(MotionEvent motionEvent) {
            if (motionEvent.getAction()==MotionEvent.ACTION_DOWN)
            {
                lastTouchX= (int) motionEvent.getX();
                //motionEvent.getRawX()
                lastTouchY=(int)motionEvent.getY();
            }
        }
    };

    //params_get_result
    private String MyAccount_get;
    private String MyName_get;

    //params
    private static final String param_MyAccount="MyAccount";
    private static final String param_MyName="MyName";

    //????????????
    private ArrayList<FriendsList>friendsLists=new ArrayList<>();
    private DynamicBox box;//????????????????????????
    private boolean ServerNotConnected=true;//????????????????????????????????????????????????

    //handler??????
    Handler handler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            Bundle bundle=msg.getData();
            String friendlist_getJson=bundle.getString("friendlist_getJson");
            if (friendlist_getJson.length()==2)
            {
                box.hideAll();
            }
            friendsLists= JSON.parseObject(friendlist_getJson,new TypeReference<ArrayList<FriendsList>>(){});
            //??????list?????????
            friendsListAdapter=new FriendsListAdapter(friendsLists,getContext());
            friendslistview.setAdapter(friendsListAdapter);
            return true;
        }
    });
    Handler handler_delete_friend=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            Bundle bundle=msg.getData();
            int delete_friend_status=bundle.getInt("delete_friend_status");
            if (delete_friend_status== Status.FRIEND_DELETE_SUCCESS)
            {
                ToastUtils.show_ordinary_toast(getContext(),"???????????????",Toast.LENGTH_SHORT);
                initFriendsList();
            }
            else
            {
                ToastUtils.show_ordinary_toast(getContext(),"???????????????",Toast.LENGTH_SHORT);
                initFriendsList();
            }
            return true;
        }
    });

    public FriendsFragment() {
        // Required empty public constructor
    }

    //?????????fragment???activity???????????????
    public static FriendsFragment newInstance(String MyAccount,String MyName) {
        FriendsFragment fragment = new FriendsFragment();
        Bundle args = new Bundle();
        args.putString(param_MyAccount,MyAccount);
        args.putString(param_MyName,MyName);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onResume() {//????????????????????????
        super.onResume();
        ((MainActivity) this.getActivity()).registerMyTouchListener(myTouchListener);//??????????????????
        //??????????????????
        initFriendsList();
    }
    /*
    ????????????????????????fragment????????????
     */
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (getActivity()==null)
        {//fragment????????????????????????????????????,???onCreate??????,???????????????????????????????????????
        }else
        { StatusBarUtil.setStatusBarDarkTheme(getActivity(), false); }
    }

    /*
        ??????fragment??????????????????
        */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //???????????????
        StatusBarUtil.setRootViewFitsSystemWindows(getActivity(),false);
        StatusBarUtil.setTranslucentStatus(getActivity());
        StatusBarUtil.setStatusBarDarkTheme(getActivity(), false);

        new GetFriendList_Thread().start();
        if (getArguments() != null) {
            MyAccount_get=getArguments().getString(param_MyAccount);
            MyName_get=getArguments().getString(param_MyName);
        }
    }


    /*
    ???????????????????????????
    */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i("life","onactivitycreated");
        //????????????
        friendslistview=getView().findViewById(R.id.Friends_ListView);
        friendslistview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                MenuUtils.ShowPopupMenu(view,getContext(),R.menu.menu_friends_list_options,lastTouchX,lastTouchY,showDeleteDialog(position));
                return true;
            }
        });
        friendslistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FriendsList friendsList=(FriendsList) friendslistview.getItemAtPosition(position);
                String MyName=MyName_get;
                String MyAccount=MyAccount_get;
                String Friend_Account=friendsList.getFriends_Account();
                String Friend_Name=friendsList.getFriends_Name();
                //Log.i("test",MyName+MyAccount+Friend_Name+Friend_Account);
                //???getActivity????????????????????????????????????activity???public????????????
                ((MainActivity)getActivity()).removeFragment(0);
                ((MainActivity)getActivity()).addFragment(0,new ChatFragment().newInstance(MyName,Friend_Name,MyAccount,Friend_Account));
                //((MainActivity)getActivity()).ChangeFragment(0);
                ((MainActivity)getActivity()).setBottomNavigation_to_chat();//???????????????????????????????????????id????????????????????????????????????changefragment???????????????????????????????????????????????????????????????????????????????????????????????????.
            }
        });
        addfriendsfloatbtn=getView().findViewById(R.id.Add_Friend_float_btn);
        //??????????????????
        box=new DynamicBox(getContext(),friendslistview);
        box.showLoadingLayout();
        addfriendsfloatbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.putExtra(param_MyAccount,MyAccount_get);
                intent.setClass(getActivity(),AddNewFriendActivity.class);
                startActivity(intent);//????????????????????????
            }
        });

        refresh_friend_list=getView().findViewById(R.id.Friends_refresh_pic);
        refresh_friend_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //?????????????????????????????????
                Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.refresh_anim);
                refresh_friend_list.startAnimation(animation);
                initFriendsList();
            }
        });

    }
    public AlertDialog showDeleteDialog(int item_position)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        final AlertDialog dialog = builder.create();
        dialog.setCancelable(true);//??????????????????????????????????????????dialog
        View dialogview=View.inflate(getContext(),R.layout.dialog_delete_friend,null);
        Button delete_sure_btn=dialogview.findViewById(R.id.Delete_friend_sure_btn);
        Button delete_cancel_btn=dialogview.findViewById(R.id.Delete_friend_cancel_btn);
        delete_sure_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ToastUtils.show_ordinary_toast(getContext(),"delete",Toast.LENGTH_SHORT);
                FriendsList friendsList=(FriendsList) friendslistview.getItemAtPosition(item_position);
                if (friendsList.getAccount().equals(friendsList.getFriends_Account()))
                {
                    ToastUtils.show_ordinary_toast(getContext(),"???????????????????????????~",Toast.LENGTH_SHORT);
                }
                new DeleteFriend_Thread(friendsList).start();
                dialog.dismiss();
            }
        });
        delete_cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ToastUtils.show_ordinary_toast(getContext(),"cancel",Toast.LENGTH_SHORT);
                dialog.dismiss();
            }
        });
        dialog.setView(dialogview);
        return dialog;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View root=inflater.inflate(R.layout.fragment_friends, container, false);
        return root;
    }
    public void initFriendsList()//?????????????????????????????????????????????????????????????????????????????????????????????????????????
    {
        //box.showLoadingLayout();
        View loading_view=LayoutInflater.from(getContext()).inflate(R.layout.layout_loading,null,false);
        TextView loading_text=loading_view.findViewById(R.id.loading_exception_message);
        loading_text.setText(R.string.friend_loading_text);
        box.addCustomView(loading_view,"friend_list_loading");
        box.showCustomView("friend_list_loading");
        new GetFriendList_Thread().start();
    }
    class DeleteFriend_Thread extends Thread{
        FriendsList friendsList;
        public DeleteFriend_Thread(FriendsList friendsList)
        {
            this.friendsList=friendsList;
        }
        @Override
        public void run() {
            super.run();
            String friendsList_JSON=JSON.toJSONString(friendsList);
            try {
                String friendsList_JSON_encode= URLEncoder.encode(friendsList_JSON,"UTF-8");//?????????????????????
                HttpURLConnection connection=HTTPUtils.geturlconnection("Friends_Servlet");
                String message=URLParameterTpye.FRIEND_LIST_JSON+"="+friendsList_JSON_encode+"&type="+URLParameterTpye.FRIENDS_DELETE;
                OutputStream outputStream= connection.getOutputStream();
                outputStream.write(message.getBytes());
                if (connection.getResponseCode()==200)
                {
                    InputStream in=connection.getInputStream();
                    int delete_friend_status=StreamUtils.GetIntFromServer(in);
                    Bundle bundle=new Bundle();
                    bundle.putInt("delete_friend_status",delete_friend_status);
                    Message msg=handler_delete_friend.obtainMessage();
                    msg.setData(bundle);
                    handler_delete_friend.sendMessage(msg);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    class GetFriendList_Thread extends Thread{
        @Override
        public void run() {
            super.run();
            while (ServerNotConnected) {
                HttpURLConnection connection = HTTPUtils.geturlconnection("Friends_Servlet");
                String message = URLParameterTpye.ACCOUNT + "=" + MyAccount_get + "&type=" + URLParameterTpye.SHOW_FRIENDS_LIST;
                try {
                    OutputStream outputStream = connection.getOutputStream();
                    outputStream.write(message.getBytes());
                    if (connection.getResponseCode() == 200) {
                        InputStream in = connection.getInputStream();
                        String friendlist_getJson = StreamUtils.GetStringFromServer(in);
                        Bundle bundle = new Bundle();
                        bundle.putString("friendlist_getJson", friendlist_getJson);
                        Message msg = handler.obtainMessage();
                        msg.setData(bundle);
                        handler.sendMessage(msg);
                        break;
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }
}