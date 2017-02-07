package com.example.tong.jiaowuxitong.view.custom;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.example.tong.jiaowuxitong.TestUtil;

/**
 * Created by TONG on 2017/1/16.
 */
public class MyViewPager extends ViewPager {
    public MyViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
//        context.getResources().getConfiguration().

    }

    private int x;
    private int y;

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int currentPosition = getCurrentItem();
        if (currentPosition < maxScrollablePosition) return super.onTouchEvent(ev);

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x = (int) ev.getX();
                y = (int) ev.getY();
//                TestUtil.log("scrolld:", x + " : " + y);
                break;

            case MotionEvent.ACTION_MOVE:
                int x2 = (int) ev.getX();
                int y2 = (int) ev.getY();
                int disX = Math.abs(x2 - x);
                int disY = Math.abs(y2 - y);


                if (disX > disY && x2 - x < 0) {
                    x = x2;
                    y = y2;
                    TestUtil.log("unhandle:", "x :" + disX);
                    return true;
                } else {
                    x = x2;
                    y = y2;
                    TestUtil.log("handle:", "y :" + disY);
                    return super.onTouchEvent(ev);
                }
        }
        return super.onTouchEvent(ev);
    }

    public void setMaxScrollablePosition(int position) {
        if (position > maxScrollablePosition)
            this.maxScrollablePosition = position;
    }

    private int maxScrollablePosition = 0;


    public void init() {
        maxScrollablePosition = 0;
    }
}
