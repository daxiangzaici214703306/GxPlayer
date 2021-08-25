package com.hsns.gxplayer.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.hsns.gxplayer.listener.GxCallBack;
import com.hsns.gxplayer.model.GxModel;

import java.util.List;

public class GxViewModel extends ViewModel implements IViewModel, GxCallBack {
    private GxModel mGxModel;
    //设置视频路径集合的对象
    private MutableLiveData videoData = new MutableLiveData();

    public GxViewModel() {
        mGxModel = new GxModel();
        mGxModel.setGxCallBack(this);
    }

    @Override
    public void onSearchVideoCallBack(List<String> videoPath) {
        videoData.postValue(videoPath);
    }

    /**
     * 获取视频路径集合的对象
     * @return 视频路径集合的对象
     */
    public MutableLiveData getVideoLiveData(){
        return videoData;
    }

    /**
     * 获取所有视频列表数据
     */
    public void getVideoList(){
        mGxModel.searchAllVideo();
    }
}
