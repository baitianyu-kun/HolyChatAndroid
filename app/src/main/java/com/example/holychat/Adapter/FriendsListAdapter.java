package com.example.holychat.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.holychat.Drawable.HeadPictureDrawable;
import com.example.holychat.Entity.FriendsList;
import com.example.holychat.R;
import com.example.holychat.Utils.StringUtils;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class FriendsListAdapter extends BaseAdapter {
    private ArrayList<FriendsList> friendslist;
    private Context context;//把上下文传进来，用来获取convertview的组件
    String HeadPictureText_get;
    String HeadPictureText_final;


    public FriendsListAdapter(ArrayList<FriendsList> getfriendslist, Context context)
    {
        this.friendslist=getfriendslist;
        this.context=context;
    }
    @Override
    public int getCount() {
        return friendslist.size();
    }

    @Override
    public Object getItem(int position) {
        return friendslist.get(position);
    }



    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder{
        public ImageView head_picture;
        public TextView friend_name,friend_account;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        /*
        convertview和viewHolder
         */
        if (convertView==null)
        {
            viewHolder=new ViewHolder();
            convertView= LayoutInflater.from(context).inflate(R.layout.item_friendslist,parent,false);
            viewHolder.head_picture=(ImageView)convertView.findViewById(R.id.Friends_list_head_pic);
            viewHolder.friend_account=(TextView)convertView.findViewById(R.id.Friends_list_account);
            viewHolder.friend_name=(TextView)convertView.findViewById(R.id.Friends_list_name);
            convertView.setTag(viewHolder);
        }else {
            viewHolder=(ViewHolder)convertView.getTag();
        }
        /*
        设置viewholder中的组件
         */
        viewHolder.friend_name.setText(friendslist.get(position).getFriends_Name());
        viewHolder.friend_account.setText(friendslist.get(position).getFriends_Account());

        HeadPictureText_get=friendslist.get(position).getFriends_Name();//从friendslist当中获得好友姓名
        HeadPictureText_final=StringUtils.getEndTwoAlphabet(HeadPictureText_get);//获取首字母的后两个字母
        //viewHolder.head_picture.setImageResource(R.mipmap.ic_launcher);
        viewHolder.head_picture.setImageDrawable(new HeadPictureDrawable(
                context,
                0,0,//这里的高度和宽度都得为0，否则viewholder滚动之后width发生了变化，就不能正常显示了
                100,
                context.getResources().getColor(R.color.orange,null),
                context.getResources().getColor(R.color.white,null),
                HeadPictureText_final
        ));
        /*viewHolder.head_picture.setImageDrawable(new HeadPictureDrawable(
                context,
                viewHolder.head_picture.getWidth(),
                0,
                100,
                context.getResources().getColor(R.color.orange,null),
                context.getResources().getColor(R.color.white,null),
                HeadPictureText_final
        ));*/
        return convertView;
    }
}
