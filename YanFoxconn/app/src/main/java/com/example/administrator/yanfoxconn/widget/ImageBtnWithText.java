package com.example.administrator.yanfoxconn.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.widget.ImageButton;

/**
 * @Description 帶文字的圖片
 * @Author song
 * @Date 2021/1/13 11:05
 */
public class ImageBtnWithText extends ImageButton {

    private String text = null;//要顯示的文字
    private int color;//文字顏色

    public ImageBtnWithText(Context context) {
        super(context);
    }

    public  void setText(String text){
        this.text = text;
        this.invalidate();//通知刷新
    }
    public void setColor(int color){
        this.color = color;//設置文字顏色
    }
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        if (this.text != null){
            Paint paint = new Paint();
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setAntiAlias(true);
            paint.setTypeface(Typeface.create("宋体",Typeface.BOLD_ITALIC));
            paint.setColor(color);
            paint.setTextSize(this.getHeight()/6);
            canvas.drawText(this.text,this.getWidth()/2,this.getHeight()-(float)(paint.getTextSize()-2)/2,paint);//繪製文字
        }
    }
}
