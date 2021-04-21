/**
 *
 */
package com.example.administrator.yanfoxconn.utils;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/** 水印
 * <pre>
 * author  : Fantasy
 * version : 1.0, 2019-07-29
 * since   : 1.0, 2019-07-29
 * </pre>
 */
public class WatermarkUtil {
    /**
     * 水印文本
     */
    private String mText;
    private String mText2;
    /**
     * 字体颜色，十六进制形式，例如：0xAEAEAEAE
     */
    private int mTextColor;
    /**
     * 字体大小，单位为sp
     */
    private float mTextSize;
    /**
     * 旋转角度
     */
    private float mRotation;
    private static WatermarkUtil sInstance;

    private WatermarkUtil() {
        mText = "";
        mText2 = "";
        mTextColor = 0xAEAEAEAE;
        mTextSize = 14;
        mRotation = -25;
    }

    public static WatermarkUtil getInstance() {
        if (sInstance == null) {
            synchronized (WatermarkUtil.class) {
                sInstance = new WatermarkUtil();
            }
        }
        return sInstance;
    }

    /**
     * 设置水印文本
     * @param text 文本
     * @return Watermark实例
     */
    public WatermarkUtil setText(String text, String text2) {
        mText = text;
        mText2 = text2;
        return sInstance;
    }

    /**
     * 设置字体颜色
     * @param color 颜色，十六进制形式，例如：0xAEAEAEAE
     * @return Watermark实例
     */
    public WatermarkUtil setTextColor(int color) {
        mTextColor = color;
        return sInstance;
    }

    /**
     * 设置字体大小
     * @param size 大小，单位为sp
     * @return Watermark实例
     */
    public WatermarkUtil setTextSize(float size) {
        mTextSize = size;
        return sInstance;
    }

    /**
     * 设置旋转角度
     * @param degrees 度数
     * @return Watermark实例
     */
    public WatermarkUtil setRotation(float degrees) {
        mRotation = degrees;
        return sInstance;
    }

    /**
     *  显示水印，铺满整个页面
     * @param activity 活动
     */
    public void show(Activity activity) {
        show(activity, mText,mText2);
    }

    /**
     * 显示水印，铺满整个页面
     * @param activity 活动
     * @param text     水印
     */
    public void show(Activity activity, String text,String text2) {
        WatermarkDrawable drawable = new WatermarkDrawable();
        drawable.mText = text;
        drawable.mText2 = text2;
        drawable.mTextColor = mTextColor;
        drawable.mTextSize = mTextSize;
        drawable.mRotation = mRotation;
        ViewGroup rootView = activity.findViewById(android.R.id.content);
        FrameLayout layout = new FrameLayout(activity);
        layout.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        layout.setBackgroundDrawable(drawable);
        rootView.addView(layout);
    }

    private class WatermarkDrawable extends Drawable {
        private Paint mPaint;
        /**
         * 水印文本
         */
        private String mText;
        private String mText2;
        /**
         * 字体颜色，十六进制形式，例如：0xAEAEAEAE
         */
        private int mTextColor;
        /**
         * 字体大小，单位为sp
         */
        private float mTextSize;
        /**
         * 旋转角度
         */
        private float mRotation;

        private WatermarkDrawable() {
            mPaint = new Paint();
        }

        @Override
        public void draw(@NonNull Canvas canvas) {
            int width = getBounds().right;
            int height = getBounds().bottom;
            int diagonal = (int) Math.sqrt(width * width + height * height); // 对角线的长度
            mPaint.setColor(mTextColor);
            mPaint.setTextSize(ConvertUtils.spToPx(mTextSize));
            //ConvertUtils.spToPx()这个方法是将sp转换成px，ConvertUtils这个工具类在我提供的demo里面有
            mPaint.setAntiAlias(true);
            float textWidth = mPaint.measureText(mText);
            canvas.drawColor(0x00000000);
            canvas.rotate(mRotation);
            int index = 0;
            float fromX;//以对角线的长度来做高度，这样可以保证竖屏和横屏整个屏幕都能布满水印
            for (int positionY = diagonal / 10; positionY <= diagonal; positionY += diagonal / 10) {
                fromX = -width + (index++ % 2) * textWidth; // 上下两行的X轴起始点不一样，错开显示
                for (float positionX = fromX; positionX < width; positionX += textWidth * 2) {
                    canvas.drawText(mText, positionX, positionY, mPaint);
                    canvas.drawText(mText2, positionX, positionY+30, mPaint);
                }
            }
            canvas.save();
            canvas.restore();
        }

        @Override
        public void setAlpha(@IntRange(from = 0, to = 255) int alpha) {
        }

        @Override
        public void setColorFilter(@Nullable ColorFilter colorFilter) {
        }

        @Override
        public int getOpacity() {
            return PixelFormat.TRANSLUCENT;
        }
    }
}
