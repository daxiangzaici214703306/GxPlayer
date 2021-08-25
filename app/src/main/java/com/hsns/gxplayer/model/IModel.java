package com.hsns.gxplayer.model;

public interface IModel {
    /**
     * 播放的接口 position -1表示直接播放，大于等于0表示播放指定位置歌曲
     */
    void play(int position);

    /**
     * 暂停的接口
     */
    void pause();

    /**
     * 下一首的接口
     */
    void next();

    /**
     * 上一首的接口
     */
    void prev();

    /**
     * 搜索根目录下所有视频
     */
    void searchAllVideo();

    /**
     * 设置播放模式 mode 0表示随机播放，1表示顺序循环，2表示单曲循环
     * @param mode
     */
    void setPlayMode(int mode);

    /**
     * 获取视频总时长
     */
    void getVideoTotalTime();

    /**
     * 获取当前视频播放时间
     */
    void getCurrentVideoTime();

}
