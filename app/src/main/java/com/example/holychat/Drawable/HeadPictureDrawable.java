package com.example.holychat.Drawable;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class HeadPictureDrawable extends Drawable {
    /*
    这个圆形应该是以这个imageView的左上角为原点
     */
    private Context context;
    private int CircleRadius;
    private int Width_to_Left;//画出来的图形靠ImageView原点左边距离
    private int Height_to_Top;//画出来的图形靠ImageView原点上边距离
    private int CircleColor;
    private int Image_Width,Image_Height;
    private int TextColor,TextSize;//字体大小,好像字体是圆圈半径的大小就很完美了
    private String TextContent="NULL";
    public HeadPictureDrawable(Context context,int Image_Width,int Image_Height,int CircleRadius,int CircleColor,int TextColor,String TextContent)
    {
        this.context=context;
        this.CircleRadius=CircleRadius;
        this.Width_to_Left=Image_Width+CircleRadius;//这个圆形应该是以这个imageView的左上角为原点，
        // 所以应当让圆形距离屏幕加上他的半径的长度就行，就能实现这个圆形在ImageView中央居中
        this.Height_to_Top=Image_Height+CircleRadius;
        this.CircleColor=CircleColor;
        this.Image_Width=Image_Width;
        this.Image_Height=Image_Height;
        this.TextColor=TextColor;//字体颜色
        this.TextSize=CircleRadius;//字体大小,好像字体是圆圈半径的大小就很完美了
        this.TextContent=TextContent;//文字内容
    }
    @Override
    public void draw(@NonNull Canvas canvas) {
        //获得圆圈paint
        Paint circlePaint=getCirclePaint(CircleColor);
        canvas.drawCircle(Width_to_Left,Height_to_Top,CircleRadius,getCirclePaint(CircleColor));
        //获得文字paint
        Paint textPaint=getTextPaint(TextSize,TextColor);
        canvas.drawText(TextContent,center_getTextLeftPosition(),center_getTextTopPosition(textPaint),textPaint);
    }
    public Paint getCirclePaint(int color)
    {
        Paint mPaint = new Paint();
        mPaint.setAntiAlias(true);          //抗锯齿
        mPaint.setColor(color);//画笔颜色
        mPaint.setStyle(Paint.Style.FILL);  //画笔风格
        mPaint.setStrokeWidth(5);           //画笔粗细
        return mPaint;
    }
    public Paint getTextPaint(int text_size, int color)
    {
        Paint mPaint = new Paint();
        mPaint.setAntiAlias(true);          //抗锯齿
        mPaint.setColor(color);//画笔颜色
        mPaint.setStyle(Paint.Style.FILL);  //画笔风格
        mPaint.setTextSize(text_size);             //绘制文字大小，单位px
        mPaint.setStrokeWidth(10);           //画笔粗细
        mPaint.setTextAlign(Paint.Align.CENTER);
        return mPaint;
    }
    /*
    让这个文字在圆圈中居中，返回距离左边的位置和距离上边的位置，通过计算baseline实现文字完美居中
     */
    public int center_getTextLeftPosition()
    {
        int baseLineX=Width_to_Left;
        return baseLineX;
    }
    public int center_getTextTopPosition(Paint textPaint)
    {
        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        float top = fontMetrics.top;//为基线到字体上边框的距离,即上图中的top
        float bottom = fontMetrics.bottom;//为基线到字体下边框的距离,即上图中的bottom
        int baseLineY = (int) (Height_to_Top - top/2 - bottom/2);//基线中间点的y轴计算公式
        return baseLineY;
    }

    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public int getIntrinsicWidth() {
        return 2*CircleRadius;//wrap_content时候的宽度，应当为这个圆形的半径的两倍，就一个直径的大小
    }

    @Override
    public int getIntrinsicHeight() {
        return 2*CircleRadius;//wrap_content时候的高度，应当为这个圆形的半径的两倍，就一个直径的大小
    }
}
