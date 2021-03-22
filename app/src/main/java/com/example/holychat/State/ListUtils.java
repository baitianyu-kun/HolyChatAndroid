package com.example.holychat.State;

import com.alibaba.fastjson.JSON;
import com.example.holychat.Entity.FriendsList;

import java.util.ArrayList;

public class ListUtils {
    /*
    因为Bundle无法传自定义泛型的ArrayList，所以这个函数把一个自定义泛型的ArrayList放到一个没有泛型的ArrayList当中，
    在这里ArrayList arrayList_original可以不用写泛型，直接写最原生的就行，也就是说ArrayList<T>可以转换为ArrayList，
    有种向上兼容的感觉，加入之后再返回这个无泛型的ArrayList
     */
    public static ArrayList BundlePutCustomArrayList(ArrayList arrayList_original)
    {
        ArrayList arrayList_new=new ArrayList();
        arrayList_new.add(arrayList_original);
        return arrayList_new;
    }
    /*
    因为Bundle无法传自定义泛型的ArrayList，所以这个函数把一个无泛型的第一个元素取出来，配合上面的函数实现了bundle中
    传自定义泛型的list，最后的强转需要，但是ArrayList<?>和ArrayList效果是一样的，都能实现，但是这里为了清晰就加上了任意类型
    的泛型的标志
     */
    public static ArrayList BundleGetCustomArrayList(ArrayList arrayList_original)
    {
        return (ArrayList<?>)arrayList_original.get(0);
    }
}
