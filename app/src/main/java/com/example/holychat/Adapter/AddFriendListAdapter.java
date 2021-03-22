package com.example.holychat.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.holychat.Drawable.HeadPictureDrawable;
import com.example.holychat.Entity.FriendsList;
import com.example.holychat.Entity.UserList;
import com.example.holychat.R;
import com.example.holychat.Utils.StringUtils;

import java.util.ArrayList;

public class AddFriendListAdapter extends BaseAdapter {
    private ArrayList<UserList> add_friends_list;
    private Context context;
    private String HeadPictureText_get;
    private String HeadPictureText_final;

    public AddFriendListAdapter(ArrayList<UserList> add_friends_list, Context context) {
        this.add_friends_list = add_friends_list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return add_friends_list.size();
    }

    @Override
    public Object getItem(int position) {
        return add_friends_list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class  ViewHolder{
        public ImageView head_pic;
        public TextView friend_name,friend_account;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView==null)
        {
            viewHolder=new ViewHolder();
            convertView= LayoutInflater.from(context).inflate(R.layout.item_add_friend_list,parent,false);
            viewHolder.head_pic=convertView.findViewById(R.id.Add_friends_list_head_pic);
            viewHolder.friend_account=convertView.findViewById(R.id.Add_friends_list_account);
            viewHolder.friend_name=convertView.findViewById(R.id.Add_friends_list_name);
            convertView.setTag(viewHolder);
        }else {
            viewHolder=(ViewHolder)convertView.getTag();
        }

        viewHolder.friend_name.setText(add_friends_list.get(position).getUserName());
        viewHolder.friend_account.setText(add_friends_list.get(position).getUserAccount());
        HeadPictureText_get=add_friends_list.get(position).getUserName();//从friendslist当中获得好友姓名
        HeadPictureText_final=StringUtils.getEndTwoAlphabet(HeadPictureText_get);//获取首字母的后两个字母
        viewHolder.head_pic.setImageDrawable(new HeadPictureDrawable(
                context,
                0,0,
                100,
                context.getResources().getColor(R.color.orange,null),
                context.getResources().getColor(R.color.white,null),
                HeadPictureText_final
        ));
        return convertView;
    }
}
