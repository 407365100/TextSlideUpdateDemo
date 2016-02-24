package com.luomo.demo.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import com.luomo.demo.R;
import com.luomo.demo.adapter.LViewPagerAdapter;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * ViewPager实现的轮播图
 * 既支持自动轮播页面也支持手势滑动切换页面
 */
public class SlideShowView extends FrameLayout {
    private final static boolean isAutoPlay = true;//自动轮播启用开关
    private boolean isSuspendPlay;//暂停轮播
    private String[] texts;//自定义轮播文字资源

    private LazyViewPager viewPager;
    private int currentItem = 0;//当前轮播页
    private ScheduledExecutorService scheduledExecutorService;//定时任务

    private Context context;

    public SlideShowView(Context context) {
        this(context, null);
    }

    public SlideShowView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideShowView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
    }

    /**
     * 初始化Views等UI
     */
    private void initUI() {
        if (texts == null || texts.length == 0) {
            return;
        }
        LayoutInflater.from(context).inflate(R.layout.layout_slide, this, true);
        viewPager = (LazyViewPager) findViewById(R.id.viewPager);
        viewPager.setFocusable(true);
        viewPager.setAdapter(new LViewPagerAdapter(context, texts, this));
        viewPager.setOnPageChangeListener(new MyPageChangeListener());
    }

    /**
     * 外部设置要显示的文本
     * @param texts
     */
    public void setDisplayStrings(String[] texts) {
        this.texts = texts;
        initUI();
        if (isAutoPlay) {
            startPlay();
        }
    }

    /**
     * 开始轮播图切换
     */
    public void startPlay() {
        isSuspendPlay = false;
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(new SlideShowTask(), 5, 5, TimeUnit.SECONDS);
    }

    /**
     * 停止轮播图切换
     */
    public void stopPlay(){
        scheduledExecutorService.shutdown();
    }

    /**
     * 暂停轮播
     */
    public void suspendPlay(){
        isSuspendPlay = true;
    }

    /**
     * 继续轮播
     */
    public void contuniuePlay(){
        isSuspendPlay = false;
    }

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            viewPager.setCurrentItem(currentItem);
        }

    };

    /**
     * ViewPager的监听器
     * 当ViewPager中页面的状态发生改变时调用
     */
    private class MyPageChangeListener implements LazyViewPager.OnPageChangeListener {
        boolean isAutoPlay = false;

        @Override
        public void onPageScrollStateChanged(int arg0) {
            switch (arg0) {
                case 1:// 手势滑动，空闲中
                    isAutoPlay = false;
                    break;
                case 2:// 界面切换中
                    isAutoPlay = true;
                    break;
                case 0:// 滑动结束，即切换完毕或者加载完毕
                    // 当前为最后一张，此时从右向左滑，则切换到第一张
                    if (viewPager.getCurrentItem() == viewPager.getAdapter().getCount() - 1 && !isAutoPlay) {
                        viewPager.setCurrentItem(0);
                    } else if (viewPager.getCurrentItem() == 0 && !isAutoPlay) {// 当前为第一张，此时从左向右滑，则切换到最后一张
                        viewPager.setCurrentItem(viewPager.getAdapter().getCount() - 1);
                    }
                    isSuspendPlay = false;
                    break;
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageSelected(int pos) {
            currentItem = pos;
        }
    }


    /**
     * 执行轮播切换任务
     */
    private class SlideShowTask implements Runnable {

        @Override
        public void run() {
            synchronized (viewPager) {
                if(!isSuspendPlay) {
                    currentItem = (currentItem + 1) % texts.length;
                    handler.obtainMessage().sendToTarget();
                }
            }
        }
    }
}