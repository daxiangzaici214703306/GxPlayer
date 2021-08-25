package com.hsns.gxplayer.manager;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import com.hsns.gxplayer.listener.GxCallBack;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileManager {
    /**
     * 单例模式
     **/
    private static volatile FileManager mFileManager = null;
    //视频的格式
    private static final String VIDEO_NAME = ".mp4";
    //搜索到的结果返回给model
    private GxCallBack callBack;
    //usb 挂载路径
    public static final String PATH_USB_SRC = "/mnt/media_rw/udisk";

    /**
     * 构造函数私有化
     **/
    private FileManager() {
    }

    /**
     * 公有的静态函数，对外暴露获取单例对象的接口
     **/
    public static FileManager getInstance() {
        if (mFileManager == null) {
            synchronized (FileManager.class) {
                if (mFileManager == null) {
                    mFileManager = new FileManager();
                }
            }
        }
        return mFileManager;
    }

    /**
     * 获取搜索到的结果
     */
    public void getVideoList() {
        ExecutorsThreadManager.getInstance().submitThread(new Runnable() {
            @Override
            public void run() {
                List<String> list = new ArrayList<>();
                File file = new File(PATH_USB_SRC);
//                Log.d("daxiang",file.getName());
                if (file.exists()) {
                    File[] files = file.listFiles();
                    if (files != null && files.length != 0) {
                        for (int i = 0; i < files.length; i++) {
                            File result = files[i];
                            Log.d("daxiang", result.getName());
                            if (!result.isDirectory() && result.getName().endsWith(VIDEO_NAME)) {
                                String videoPath = PATH_USB_SRC +"/"+result.getName();
                                list.add(videoPath);
                            }
                        }
                        Message msg = mFileHandler.obtainMessage();
                        msg.obj = list;
                        mFileHandler.sendMessage(msg);
                    }
                }
            }
        });
    }

    /**
     * 设置搜索到结果的回调
     *
     * @param callBack 回调
     */
    public void setGxCallBack(GxCallBack callBack) {
        this.callBack = callBack;
    }


    private Handler mFileHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            List<String> list = (List<String>) msg.obj;
            if (callBack != null) {
                callBack.onSearchVideoCallBack(list);
            }
        }
    };
}
