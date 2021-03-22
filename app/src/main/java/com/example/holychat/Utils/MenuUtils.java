package com.example.holychat.Utils;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.Toast;


import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.appcompat.widget.PopupMenu;

import java.lang.reflect.Field;


public class MenuUtils {
    @SuppressLint("RestrictedApi")
    public static void ShowPopupMenu(View view, Context context, int menu_layout, int x, int y, AlertDialog dialog) {
        //定义PopupMenu对象
        PopupMenu popupMenu = new PopupMenu(context, view);
        //设置PopupMenu对象的布局
        popupMenu.getMenuInflater().inflate(menu_layout, popupMenu.getMenu());
        //设置PopupMenu的点击事件
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                //ToastUtils.show_ordinary_toast(context, (String) item.getTitle(), Toast.LENGTH_SHORT);
                dialog.show();
                return true;
            }
        });
        try {
            /*
            通过反射来确定popupmenu的位置，即手点击的位置
             */
            Field field = popupMenu.getClass().getDeclaredField("mPopup");
            field.setAccessible(true);
            MenuPopupHelper helper = (MenuPopupHelper) field.get(popupMenu);
            int []position=new int[2];
            view.getLocationInWindow(position);//获取当前view在屏幕上的位置
            int xoffset=x-position[0];
            int yoffset=y-position[1]-view.getHeight();
            //y = y - view.getHeight();//如果y取的是触摸点的位置，可能需要作此处理，经测试android5.1的设备会弹窗在屏幕之外
            helper.show(xoffset, yoffset);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
    
    public static PopupWindow PopUpWindowInit(View pop_window_view,int Width,int Height)
    {
        PopupWindow popupWindow=new PopupWindow(pop_window_view,Width,Height);
        return popupWindow;
    }
    
    
}
