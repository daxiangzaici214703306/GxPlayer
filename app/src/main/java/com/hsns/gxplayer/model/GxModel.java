package com.hsns.gxplayer.model;

import com.hsns.gxplayer.listener.GxCallBack;
import com.hsns.gxplayer.manager.FileManager;


public class GxModel implements IModel {

    @Override
    public void play(int position) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void next() {

    }

    @Override
    public void prev() {

    }

    @Override
    public void searchAllVideo() {
       FileManager.getInstance().getVideoList();
    }

    @Override
    public void setPlayMode(int mode) {

    }

    @Override
    public void getVideoTotalTime() {

    }

    @Override
    public void getCurrentVideoTime() {

    }

    /**
     * 设置搜索到结果的回调
     * @param callBack 回调
     */
    public void setGxCallBack(GxCallBack callBack) {
        FileManager.getInstance().setGxCallBack(callBack);
    }

}
