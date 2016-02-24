package com.luomo.demo.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import com.luomo.demo.R;
import com.luomo.demo.view.AutoScrollTextView;
import com.luomo.demo.view.LazyViewPager;
import com.luomo.demo.view.SlideShowView;

public class LViewPagerAdapter extends PagerAdapter {
    private final SlideShowView slideShowView;
    private Context context;
    private String[] texts;

    public LViewPagerAdapter(Context context, String[] texts,SlideShowView slideShowView) {
        this.context = context;
        this.texts = texts;
        this.slideShowView = slideShowView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((LazyViewPager) container).removeView((View)object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = View.inflate(context, R.layout.layout_slide_item, null);
        AutoScrollTextView autoScrollTextView = (AutoScrollTextView) view.findViewById(R.id.tv_text);
        autoScrollTextView.init(((Activity) context).getWindowManager());
        autoScrollTextView.setText(texts[position]);
        if (autoScrollTextView.needScrolled()) {//textview的文字需要滚动
            slideShowView.suspendPlay();//暂停轮播
            autoScrollTextView.setOnMoveStatusListener(new AutoScrollTextView.OnMoveStatusListener() {
                @Override
                public void onMoveEnded() {
                    slideShowView.contuniuePlay();//继续轮播
                }
            });
        }
        ((LazyViewPager) container).addView(view);
        return view;
    }

    @Override
    public int getCount() {
        return texts.length;
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }
}
