package com.example.administrator.yanfoxconn.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.baoyz.swipemenulistview.SwipeMenuListView;

public class SwipterMenuTest extends SwipeMenuListView
{

    private GestureDetector mGestureDetector;

    public SwipterMenuTest(Context context) {
        super(context);
        mGestureDetector = new GestureDetector(context, onGestureListener);
    }

    public SwipterMenuTest(Context context, AttributeSet attrs) {
        super(context, attrs);
        mGestureDetector = new GestureDetector(context, onGestureListener);
    }

    public SwipterMenuTest(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mGestureDetector = new GestureDetector(context, onGestureListener);
    }

    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        boolean b = mGestureDetector.onTouchEvent(ev);
//        LogUtil.w("onTouchEvent", "mGestureDetector.onTouchEvent(ev)->" + b);
        return super.onTouchEvent(ev);
    }

    private GestureDetector.OnGestureListener onGestureListener = new GestureDetector.SimpleOnGestureListener() {

        //distanceX 左右滑动距离,左滑动正值,右滑动负值
        //distanceY 上下滑动距离,上滑动正值,下滑动负值
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (Math.abs(distanceY) >= Math.abs(distanceX)) {//上下滑动距离大于左右滑动距离,当作上下滑动
//                LogUtil.w("onScroll", "distanceX=" + distanceX + ":distanceY=" + distanceY);
//                LogUtil.w("onScroll", "true");
                //上下滑动不做任何操作,在这里父ScrollView已经交出onTouch权限,否则如果权限在父ScrollView的话这里接收不到事件
                //所以执行到这里是因为下面的setParentScrollAble(false);已经执行过了
                return true;
            }
            //当滑动NoRollSwipeMenuListView的时候，让父ScrollView交出onTouch权限，也就是让父ScrollView停住不能滚动
            setParentScrollAble(false);
//            LogUtil.w("onScroll", "false");
            return false;
        }
    };

    /**
     * 是否把滚动事件交给父ScrollView
     *
     * @param flag
     */
    private void setParentScrollAble(boolean flag) {
        //这里的parentScrollView就是NoRollSwipeMenuListView外面的那个ScrollView
//        LogUtil.w("setParentScrollAble", "flag->" + flag);
        getParent().requestDisallowInterceptTouchEvent(!flag);
    }

}
