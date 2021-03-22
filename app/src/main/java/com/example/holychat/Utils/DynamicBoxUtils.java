package com.example.holychat.Utils;

import android.content.Context;
import android.view.View;

import mehdi.sakout.dynamicbox.DynamicBox;

public class DynamicBoxUtils {
    public static void showCustomBoxWidget(View view, Context context,int widget)
    {
        DynamicBox box=new DynamicBox(context,widget);
        box.addCustomView(view,"thisview");
        box.showCustomView("thisview");
    }
    public static void showCustomBoxActivity()
    {

    }
}
