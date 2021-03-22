package com.example.holychat.Fragment;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.holychat.Activity.LoginActivity;
import com.example.holychat.Drawable.HeadPictureDrawable;
import com.example.holychat.R;
import com.example.holychat.Utils.StatusBarUtil;
import com.example.holychat.Utils.StringUtils;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static String param_User_name="user_name";//传进来的bundle的参数名称key

    private String get_param_UserName;
    private Button user_frag_out_btn;
    private TextView user_frag_name_tv;
    private ImageView imageView;

    public UserFragment() {
        // Required empty public constructor
    }//应该用那个newInstance来构造一个fragment，而不应该使用一个构造函数，构造函数的数据不能保存很长时间

    public static UserFragment newInstance(String param1) {//初始化fragment对象并传进数据来
        UserFragment fragment = new UserFragment();
        Bundle args = new Bundle();
        args.putString(param_User_name, param1);
        fragment.setArguments(args);
        return fragment;
    }

    /*
    创建之后接收数据
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //设置状态栏
        StatusBarUtil.setRootViewFitsSystemWindows(getActivity(),false);
        StatusBarUtil.setTranslucentStatus(getActivity());
        StatusBarUtil.setStatusBarDarkTheme(getActivity(), true);


        if (getArguments() != null) {//getArguments就是一个返回bundle的函数
            get_param_UserName = getArguments().getString(param_User_name);
        }
    }

    /*
    实现状态栏随不同fragment改变颜色
     */
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (getActivity()==null)
        {//fragment启动时候会先调用这个方法,比onCreate都早,所以应该先判断一下是否为空
        }else
        { StatusBarUtil.setStatusBarDarkTheme(getActivity(), true); }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //获取控件
        user_frag_out_btn=getView().findViewById(R.id.User_frag_out_btn);
        user_frag_name_tv=getView().findViewById(R.id.User_frag_name_tv);
        imageView=getView().findViewById(R.id.User_frag_pic_ig);
        //设置头像
        String HeadPictureText_alphabet_get= StringUtils.getNameFirstAlphabet(get_param_UserName);//将获得的好友姓名，获取其名字首字母
        String HeadPictureText_final=HeadPictureText_alphabet_get.substring(HeadPictureText_alphabet_get.length()-2);//获取首字母的后两个字母
        imageView.setImageDrawable(new HeadPictureDrawable(
                getContext(),
                0,0,
                100,
                getContext().getResources().getColor(R.color.orange,null),
                getContext().getResources().getColor(R.color.white,null),
                HeadPictureText_final
        ));
        //设置用户名
        user_frag_name_tv.setText(get_param_UserName);

        user_frag_out_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root=inflater.inflate(R.layout.fragment_user, container, false);
        return root;
    }
}