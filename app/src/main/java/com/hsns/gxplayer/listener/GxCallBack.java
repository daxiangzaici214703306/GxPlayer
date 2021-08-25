package com.hsns.gxplayer.listener;

import java.util.List;

public interface GxCallBack {
    /**
     * 搜索到的结果返回给viewmodel
     * @param videoPath  搜索的结果
     */
    void onSearchVideoCallBack(List<String> videoPath);
}
