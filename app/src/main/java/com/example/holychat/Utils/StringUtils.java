package com.example.holychat.Utils;

import android.util.Log;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

public class StringUtils {
    public static String getCharacterPinYin(char c)//转换单个字符为拼音
    {
        String[]pinyin=null;
        pinyin= PinyinHelper.toHanyuPinyinStringArray(c);
        if (pinyin==null)
            return null;
        return pinyin[0];
    }
    public static String getEndTwoAlphabet(String str)//获取名字的首字母的最后两个
    {
        String getNameFirstAlphabet=getNameFirstAlphabet(str);//获取首字母
        return getNameFirstAlphabet.substring(getNameFirstAlphabet.length()-2);//获取首字母的后两个
    }
    public static String getNameFirstAlphabet(String str)//获得名字的首字母
    {
        if (str.length()<2)
        {
            str=str+"#";//不足两位用#补足,否则会崩溃
        }
        else
        {
            str=str;
        }
        StringBuilder builder=new StringBuilder();
        String tempPinyin=null;
        for (int i=0;i<str.length();i++)
        {
            String []pinyinchar=null;
            pinyinchar=PinyinHelper.toHanyuPinyinStringArray(str.charAt(i));
            if (pinyinchar!=null)
            {
                tempPinyin=pinyinchar[0];//取得第一声
            }else {
                tempPinyin=null;
            }
            if (tempPinyin==null)
            {
                builder.append(str.charAt(i));
            }else {
                builder.append(tempPinyin.charAt(0));//获取这个拼音的第一个字母
            }
        }
        return builder.toString().toUpperCase();
    }
    public static String getStringPinYin(String str)//转换多个汉字的，如果是英文的话就不动
    {
        StringBuilder builder=new StringBuilder();
        String tempPinyin=null;
        for (int i=0;i<str.length();i++)
        {
            String []pinyinchar=null;
            pinyinchar=PinyinHelper.toHanyuPinyinStringArray(str.charAt(i));
            if (pinyinchar!=null)
            {
                tempPinyin=pinyinchar[0];//取得第一声
            }else {
                tempPinyin=null;
            }

            if (tempPinyin==null)
            {
                builder.append(str.charAt(i));
            }else {
                builder.append(tempPinyin);
            }
        }
        return builder.toString();
    }

}
