package com.hsns.gxplayer.utils;

import android.os.SystemClock;

public class GxUtils {
    /**
     * 将秒转换为时分秒格式
     *
     * @param time 秒
     * @return 时分秒格式
     */
    public static String timeConversion(int time) {
        time = time / 1000;
        int hour = 0;
        int minutes = 0;
        int sencond = 0;
        int temp = time % 3600;
        if (time > 3600) {
            hour = time / 3600;
            if (temp != 0) {
                if (temp > 60) {
                    minutes = temp / 60;
                    if (temp % 60 != 0) {
                        sencond = temp % 60;
                    }
                } else {
                    sencond = temp;
                }
            }
        } else {
            minutes = time / 60;
            if (time % 60 != 0) {
                sencond = time % 60;
            }
        }
        return (hour < 10 ? ("0" + hour) : hour) + ":" + (minutes < 10 ? ("0" + minutes) : minutes) + ":" + (sencond < 10 ? ("0" + sencond) : sencond);
    }


    // 这里不去采用System.currentTimeMillis()或System.nanoTime()/1000000L这一方法(java中采用这种方法),
// 因为它产生一个当前的毫秒，这个毫秒其实就是自1970年1月1日0时起的毫秒数,这个是受机器设定的时间影响较大
    private static long lastClickTime;

    public static boolean isFastDoubleClick() {
        long time = SystemClock.currentThreadTimeMillis(); // 此方法仅用于Android
        if (time - lastClickTime < 1000) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

}
