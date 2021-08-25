package com.hsns.gxplayer.adapter;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hsns.gxplayer.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GxAdapter extends RecyclerView.Adapter<GxAdapter.MyViewHolder> {
    private List<String> result;
    private List<SurfaceView> surfaceViews = new ArrayList<>();
    private GxAdapterListener mGxAdapterListener;

    public GxAdapter(List<String> result) {
        this.result = result;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        surfaceViews.add(holder.mSurfaceView);
    }

    @Override
    public int getItemCount() {
        if (result != null && result.size() != 0) {
            return result.size();
        }
        return 0;
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnTouchListener {
        public SurfaceView mSurfaceView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mSurfaceView = (SurfaceView) itemView.findViewById(R.id.surface);
            mSurfaceView.setOnTouchListener(this);
            mSurfaceView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mGxAdapterListener != null) {
                mGxAdapterListener.onClickSurfaceViewCallback();
            }
        }

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (mGxAdapterListener != null) {
                mGxAdapterListener.onTouchSurfaceViewCallback(motionEvent);
            }
            return false;
        }
    }

    /**
     * 刷新viewpager数据
     *
     * @param result 数据
     */
    public void notifyAdapter(List<String> result) {
        surfaceViews.clear();
        this.result = result;
        notifyDataSetChanged();
    }

    /**
     * 获取对应的surfaceview
     *
     * @param index 位置
     * @return
     */
    public SurfaceView getView(int index) {
        if (surfaceViews.size() > index) {
            return surfaceViews.get(index);
        }
        return null;
    }

    public interface GxAdapterListener {
        /**
         * SurfaceView的点击回调
         */
        void onClickSurfaceViewCallback();

        /**
         * SurfaceView的touch时间
         *
         * @param motionEvent 移动时间
         * @return 是否touch
         */
        boolean onTouchSurfaceViewCallback(MotionEvent motionEvent);
    }

    /**
     * 设置SurfaceView的点击监听
     *
     * @param l 监听对象
     */
    public void setGxAdapterListener(GxAdapterListener l) {
        mGxAdapterListener = l;
    }


}
