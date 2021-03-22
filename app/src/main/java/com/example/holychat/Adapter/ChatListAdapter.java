package com.example.holychat.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.holychat.Drawable.HeadPictureDrawable;
import com.example.holychat.Entity.Chat;
import com.example.holychat.R;
import com.example.holychat.State.Status;
import com.example.holychat.Utils.StringUtils;

import java.util.ArrayList;

public class ChatListAdapter extends BaseAdapter {
    private ArrayList<Chat> chatArrayList;
    private String MyAccount;
    private String FriendAccount;
    private String MyName;//用来画头像
    private String FriendsName;//用来画头像
    private Context context;
    private final static int MESSAGE_SEND=0;//必须从0开始
    private final static int MESSAGE_GET=1;
    private final static int MESSAGE_TYPE=2;//且有几种类型就写几种，否则会类型转换错误，如果是两种的话，那具体的必须是0，1


    public ChatListAdapter(ArrayList<Chat> chatArrayList, String myAccount, String friendAccount, String myName, String friendsName, Context context) {
        this.chatArrayList = chatArrayList;
        MyAccount = myAccount;
        FriendAccount = friendAccount;
        MyName = myName;
        FriendsName = friendsName;
        this.context = context;
    }



    //设置接收消息和发送消息的viewholder
    public final class Send_ViewHolder{
        public ImageView send_head_picture;
        public TextView send_friend_content;
    }
    public final class Get_ViewHolder{
        public ImageView get_head_picture;
        public TextView get_friend_content;
    }

    //初始化viewholder中的数据
    public void initSendMsg(Send_ViewHolder send_viewHolder, int position)
    {
        Chat chat=chatArrayList.get(position);

        String HeadPictureText_get=MyName;//获取我姓名
        String HeadPictureText_final= StringUtils.getEndTwoAlphabet(HeadPictureText_get);//获取首字母的后两个字母
        send_viewHolder.send_head_picture.setImageDrawable(new HeadPictureDrawable(
                context,
                0,0,
                100,
                context.getResources().getColor(R.color.orange,null),
                context.getResources().getColor(R.color.white,null),
                HeadPictureText_final
        ));
        send_viewHolder.send_friend_content.setText(chat.getMessage_content());
    }
    public void initGetMsg(Get_ViewHolder get_viewHolder,int position)
    {
        Chat chat=chatArrayList.get(position);
        String HeadPictureText_get=FriendsName;//获取好友姓名
        String HeadPictureText_final= StringUtils.getEndTwoAlphabet(HeadPictureText_get);//获取首字母的后两个字母
        get_viewHolder.get_head_picture.setImageDrawable(new HeadPictureDrawable(
                context,
                0,0,
                100,
                context.getResources().getColor(R.color.orange,null),
                context.getResources().getColor(R.color.white,null),
                HeadPictureText_final
        ));
        get_viewHolder.get_friend_content.setText(chat.getMessage_content());
    }

    @Override
    public int getItemViewType(int position) {
        Chat chat=chatArrayList.get(position);
        if (chat.getAccount().equals(MyAccount))
            return MESSAGE_SEND;
        else
            return MESSAGE_GET;
    }

    @Override
    public int getViewTypeCount() {
        return MESSAGE_TYPE;
    }

    @Override
    public int getCount() {
        return chatArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return chatArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Send_ViewHolder send_viewHolder;
        Get_ViewHolder get_viewHolder;
        int MsgType=getItemViewType(position);
        if (convertView==null)
        {
            switch (MsgType)
            {
                case MESSAGE_SEND:
                    send_viewHolder=new Send_ViewHolder();
                    convertView=LayoutInflater.from(context).inflate(R.layout.item_send_message,parent,false);
                    send_viewHolder.send_friend_content=convertView.findViewById(R.id.Chat_send_content);
                    send_viewHolder.send_head_picture=convertView.findViewById(R.id.Chat_send_head_pic);
                    convertView.setTag(send_viewHolder);
                    initSendMsg(send_viewHolder,position);
                    break;
                case MESSAGE_GET:
                    get_viewHolder=new Get_ViewHolder();
                    convertView=LayoutInflater.from(context).inflate(R.layout.item_get_message,parent,false);
                    get_viewHolder.get_friend_content=convertView.findViewById(R.id.Chat_get_content);
                    get_viewHolder.get_head_picture=convertView.findViewById(R.id.Chat_get_head_pic);
                    convertView.setTag(get_viewHolder);
                    initGetMsg(get_viewHolder,position);
                    break;
                default:
                    break;
            }
        }else {
            switch (MsgType){
                case MESSAGE_SEND:
                    send_viewHolder= (Send_ViewHolder) convertView.getTag();
                    initSendMsg(send_viewHolder,position);
                    break;
                case MESSAGE_GET:
                    get_viewHolder= (Get_ViewHolder) convertView.getTag();
                    initGetMsg(get_viewHolder,position);
                    break;
                default:
                    break;
            }
        }

        return convertView;
    }
}
