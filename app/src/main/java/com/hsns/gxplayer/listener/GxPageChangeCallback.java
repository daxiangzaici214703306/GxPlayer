package com.hsns.gxplayer.listener;

import android.util.Log;

import androidx.viewpager2.widget.ViewPager2;

public class GxPageChangeCallback extends ViewPager2.OnPageChangeCallback {
    private GxPageChangeToMainCallback mCallback;
    private int position;
    private int state=0;
    public GxPageChangeCallback(GxPageChangeToMainCallback mCallback) {
        super();
        this.mCallback=mCallback;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        super.onPageScrolled(position, positionOffset, positionOffsetPixels);
        Log.d("daxiang","onPageScrolled position==>"+position);
        this.position=position;
    }

    @Override
    public void onPageSelected(int position) {
        super.onPageSelected(position);
        Log.d("daxiang","onPageSelected position==>"+position);
        if(mCallback!=null&&state==0){
            mCallback.onPageChange(position);
        }
        this.position=position;
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        super.onPageScrollStateChanged(state);
        Log.d("daxiang","onPageScrollStateChanged state==>"+state);
        this.state=state;
        if(mCallback!=null&&state==ViewPager2.SCROLL_STATE_IDLE){
            mCallback.onPageChange(position);
        }
    }
}
