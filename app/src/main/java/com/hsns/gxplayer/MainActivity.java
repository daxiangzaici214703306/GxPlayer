package com.hsns.gxplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import com.hsns.gxplayer.adapter.GxAdapter;
import com.hsns.gxplayer.databinding.ActivityMainBinding;
import com.hsns.gxplayer.listener.GxPageChangeCallback;
import com.hsns.gxplayer.listener.GxPageChangeToMainCallback;
import com.hsns.gxplayer.manager.ExecutorsThreadManager;
import com.hsns.gxplayer.utils.GxUtils;
import com.hsns.gxplayer.viewmodel.GxViewModel;

import java.io.IOException;
import java.util.List;
import java.util.Random;


public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, SurfaceHolder.Callback,
        GxPageChangeToMainCallback, GxAdapter.GxAdapterListener, SeekBar.OnSeekBarChangeListener, MediaPlayer.OnErrorListener {
    private ActivityMainBinding binding;
    private MutableLiveData videoData;
    private GxViewModel mGxViewModel;
    private static final String TAG = "daxiang";
    private MediaPlayer mMediaPlayer;
    private List<String> result;
    private GxAdapter mGxAdapter;
    private SurfaceView surfaceView;
    private int tempIndex = -1;
    private int countTime;//视频总时长
    private volatile boolean stopTimeLis = false;//是否停止时间监听
    private int playStyle = 0;//播放模式
    private static final int STYLE_SINGLE = 0;//单曲循环
    private static final int STYLE_SX = 1;//顺序循环
    private static final int STYLE_RAND = 2;//随机循环


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        ViewModelProvider.Factory factory = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication());
        mGxViewModel = new ViewModelProvider(this, factory).get(GxViewModel.class);
        videoData = mGxViewModel.getVideoLiveData();
        binding.btn.setOnClickListener(this);
        binding.playpause.setOnClickListener(this);
//        binding.heartlayout.setOnClickListener(this);
        binding.prev.setOnClickListener(this);
        binding.next.setOnClickListener(this);
        binding.replay.setOnClickListener(this);
        binding.loopSingle.setOnClickListener(this);
        binding.loopSx.setOnClickListener(this);
        binding.loopRand.setOnClickListener(this);
        binding.seekbar.setOnSeekBarChangeListener(this);
        mGxAdapter = new GxAdapter(result);
        mGxAdapter.setGxAdapterListener(this);
        binding.viewpage2.setAdapter(mGxAdapter);
        binding.viewpage2.registerOnPageChangeCallback(new GxPageChangeCallback(this));
        listenLiveData();
    }

    /**
     * 设置播放模式ui的背景
     */
    private void setLoopBg(){
       switch (playStyle){
           case 0:
               binding.loopSingle.setBackgroundResource(R.color.red);
               binding.loopSx.setBackgroundResource(R.color.colorPrimary);
               binding.loopRand.setBackgroundResource(R.color.colorPrimary);
               break;
           case 1:
               binding.loopSingle.setBackgroundResource(R.color.colorPrimary);
               binding.loopSx.setBackgroundResource(R.color.red);
               binding.loopRand.setBackgroundResource(R.color.colorPrimary);
               break;
           case 2:
               binding.loopSingle.setBackgroundResource(R.color.colorPrimary);
               binding.loopSx.setBackgroundResource(R.color.colorPrimary);
               binding.loopRand.setBackgroundResource(R.color.red);
               break;
       }
    }

    /**
     * 播放对应位置视频
     *
     * @param index 位置
     */
    private void play(int index) {
        String path = null;
        try {
            Log.d("daxiang", "index==>" + index + " result size==>" + result.size());
            if (result != null && result.size() > index) {
                path = result.get(index);
                binding.playpause.setVisibility(View.GONE);
                binding.seekbar.setVisibility(View.GONE);
                binding.currentTime.setVisibility(View.GONE);
                binding.totalTime.setVisibility(View.GONE);
                mMediaPlayer = new MediaPlayer();
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mMediaPlayer.setDataSource(path);
                surfaceView = mGxAdapter.getView(index);
                surfaceView.getHolder().addCallback(this);
                mMediaPlayer.setOnPreparedListener(this);
                mMediaPlayer.setOnCompletionListener(this);
                mMediaPlayer.prepare();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 处理多媒体播放视频的停止操作
     */
    private void stop() {
        if (mMediaPlayer != null) {//&&mMediaPlayer.isPlaying()
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }


    /**
     * 监听livedata获取到的数据
     */
    private void listenLiveData() {
        videoData.observe(this, new Observer() {
            @Override
            public void onChanged(Object o) {
                result = (List<String>) o;
                for (int i = 0; i < result.size(); i++) {
                    Log.d(TAG, "result==>" + result.get(i));
                }
                if (mGxAdapter != null) {
                    mGxAdapter.notifyAdapter(result);
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn:
                tempIndex = -1;
                mGxViewModel.getVideoList();
                break;
            case R.id.playpause:
                doClickPlayOrPause();
                break;
            case R.id.heartlayout:
                setViewPage2Click();
                break;
            case R.id.prev:
                doPrev();
                break;
            case R.id.next:
                doNext();
                break;
            case R.id.replay:
                rePlay();
                break;
            case R.id.loop_single:
                playStyle = STYLE_SINGLE;
                setLoopBg();
                break;
            case R.id.loop_sx:
                playStyle = STYLE_SX;
                setLoopBg();
                break;
            case R.id.loop_rand:
                playStyle = STYLE_RAND;
                setLoopBg();
                break;
        }
    }

    /**
     * 设置循环播放
     */
    private void setLooping() {
        if (mMediaPlayer != null) {
            mMediaPlayer.setLooping(true);
            Toast.makeText(this, getString(R.string.set_loop_suc), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 重播
     */
    private void rePlay() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.seekTo(0);
        }
    }

    /**
     * 播放暂停操作
     */
    private void doClickPlayOrPause() {
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();
                stopTimeLis = false;
            } else {
                mMediaPlayer.start();
                stopTimeLis = true;
                ExecutorsThreadManager.getInstance().submitThread(mPlayLisRunnable);
            }
        }
        setPlayPauseBg();
    }

    /**
     * viewpager2的点击操作
     */
    private void setViewPage2Click() {
        Log.d("daxiang", "setViewPage2Click");
        if (binding.playpause.getVisibility() == View.VISIBLE) {
            binding.playpause.setVisibility(View.GONE);
            binding.seekbar.setVisibility(View.GONE);
            binding.currentTime.setVisibility(View.GONE);
            binding.totalTime.setVisibility(View.GONE);
        } else {
            binding.playpause.setVisibility(View.VISIBLE);
            binding.seekbar.setVisibility(View.VISIBLE);
            binding.currentTime.setVisibility(View.VISIBLE);
            binding.totalTime.setVisibility(View.VISIBLE);
            setPlayPauseBg();
        }
    }

    /**
     * 设置播放暂停键的背景
     */
    private void setPlayPauseBg() {
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                binding.playpause.setBackgroundResource(R.mipmap.play);
            } else {
                binding.playpause.setBackgroundResource(R.mipmap.pause);
            }
        }
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        Log.d("daxiang", "onPrepared");
        if (mMediaPlayer != null) {
            mMediaPlayer.start();
            countTime = mMediaPlayer.getDuration();
            binding.seekbar.setMax(countTime);
            binding.currentTime.setText(GxUtils.timeConversion(0));
            binding.totalTime.setText(GxUtils.timeConversion(countTime));
            stopTimeLis = true;
            ExecutorsThreadManager.getInstance().submitThread(mPlayLisRunnable);
        }

        if (mMediaPlayer != null && surfaceView != null && surfaceView.isAttachedToWindow()) {
            mMediaPlayer.setDisplay(surfaceView.getHolder());
        }
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        Log.d("daxiang", "onCompletion");
        if(playStyle!=STYLE_SINGLE) {
            doNext();
        }else{
            rePlay();
            doClickPlayOrPause();
        }
    }

    /**
     * 下一首的操作
     */
    private void doNext() {
        if (getIndexByPlayStyle()) {
            tempIndex++;
        }
        if (tempIndex > mGxAdapter.getItemCount()) {
            tempIndex = 0;
        }
        binding.viewpage2.setCurrentItem(tempIndex);
    }

    /**
     * 上一首的操作
     */
    private void doPrev() {
        if (getIndexByPlayStyle()) {
            tempIndex--;
        }
        if (tempIndex < 0) {
            tempIndex = 0;
        }
        binding.viewpage2.setCurrentItem(tempIndex);
    }

    /**
     * 根据播放模式得到对应的播放歌曲位置
     */
    private boolean getIndexByPlayStyle(){
        switch (playStyle) {
            case STYLE_SINGLE:
            case STYLE_SX:
                return true;
            case STYLE_RAND:
                getRandIndex();
                return false;
        }
        return true;
    }

    /**
     * 取随机歌曲位置
     */
    private void getRandIndex(){
        if(mGxAdapter!=null){
            int maxIndex=mGxAdapter.getItemCount();
            Random random=new Random();
            tempIndex=random.nextInt(maxIndex);
        }
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
        Log.d("daxiang", "surfaceCreated");
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {
        Log.d("daxiang", "surfaceDestroyed");
    }


    @Override
    public void onPageChange(final int index) {
        Log.d("daxiang", "onPageChange index==>" + index);
        stop();
        play(index);
        tempIndex = index;
    }

    @Override
    public void onClickSurfaceViewCallback() {
//        if(!GxUtils.isFastDoubleClick()){
        setViewPage2Click();
//        }
    }

    @Override
    public boolean onTouchSurfaceViewCallback(MotionEvent motionEvent) {
        Log.d("daxiang", "onTouchSurfaceViewCallback");
        return binding.heartlayout.onTouchEvent(motionEvent);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        Log.d("daxiang", "onStopTrackingTouch");
        if (mMediaPlayer != null) {
            int currentTime = seekBar.getProgress();//(int)((float)seekBar.getProgress()/ 100 * countTime)
            Log.d("daxiang", "onStopTrackingTouch currentTime==>" + currentTime);
            mMediaPlayer.seekTo(currentTime);
            binding.currentTime.setText(GxUtils.timeConversion(currentTime));
        }
    }


    /**
     * 处理MediaPlayer播放时间变化进度条的UI变更
     */
    private Runnable mPlayLisRunnable = new Runnable() {
        @Override
        public void run() {
            Log.d("daxiang", "playLisRunnable run");
            while (stopTimeLis) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mMediaPlayer != null) {
//                            Log.d("daxiang", "playLisRunnable set currenttime");
                            binding.currentTime.setText(GxUtils.timeConversion(mMediaPlayer.getCurrentPosition()));
                            int seekBarPosition = mMediaPlayer.getCurrentPosition();//(int)((float)mMediaPlayer.getCurrentPosition()/mMediaPlayer.getDuration()*100)
                            binding.seekbar.setProgress(seekBarPosition);
                        }
                    }
                });
                SystemClock.sleep(1000);
            }
        }
    };

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        doNext();
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stop();
    }
}
