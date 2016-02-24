package com.luomo.demo.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Display;
import android.view.WindowManager;
import android.widget.TextView;

/**
 * @AUTHOR:renpan
 * @VERSION:v1.0
 * @DATE:2016-02-19 15:25
 */
public class AutoScrollTextView extends TextView {
    private Paint paint = null;//绘图样式

    private float moveSpeed = 2f;//移动的速度,速度设置：1-10
    private float textLength = 0f;//文本长度
    private float viewWidth = 0f;//AutoScrollTextView控件的宽度
    private float step = 0f, y = 0f;//文字的横坐标;文字的纵坐标
    private float temp_view_plus_text_length = 0.0f;//TextView的长度+TextView文本的长度
    private float temp_view_plus_two_text_length = 0.0f;//TextView的长度+TextView文本的长度*2

    private boolean isStarting = false;//是否开始滚动

    private String text = "";//文本内容

    private OnMoveStatusListener onMoveStatusListener = null;

    public AutoScrollTextView(Context context) {
        super(context);
    }

    public AutoScrollTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AutoScrollTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void init(WindowManager windowManager) {
        paint = getPaint();
        paint.setColor(this.getCurrentTextColor());//文本颜色

        text = getText().toString();
        textLength = paint.measureText(text); //measure()方法获取text的长度
        viewWidth = getWidth();
        if (viewWidth == 0 && windowManager != null) {
            Display display = windowManager.getDefaultDisplay();
            viewWidth = display.getWidth();
        }
        step = viewWidth + textLength;
        temp_view_plus_text_length = viewWidth + textLength;
        temp_view_plus_two_text_length = viewWidth + textLength * 2;
        y = getTextSize() + getPaddingTop();
    }

    /**
     * 重写系统的setText
     * @param text
     * @param type
     */
    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(text, type);
        if(!TextUtils.isEmpty(text.toString())){
            init(((Activity)getContext()).getWindowManager());
            startScroll();
        }
    }

    /**
     * 覆写TextView的onDraw()方法，实现文本滚动显示的效果
     *
     * @param canvas
     */
    @Override
    public void onDraw(Canvas canvas) {
        /**
         * Draw the text, with origin at (x,y), using the specified paint. The
         * origin is interpreted based on the Align setting in the paint.
         * @param text  The text to be drawn  要显示的文本
         * @param x     The step-coordinate of the origin of the text being drawn  文本显示的x坐标，TextView的最左端的x坐标是0，最右端的x坐标是TextView.getWidth();
         * @param y     The y-coordinate of the origin of the text being drawn  文本显示的y坐标，TextView的顶端所在的y坐标。
         * @param paint The paint used for the text (e.g. color, size, style)  描绘文本的画笔
         */
        canvas.drawText(text, temp_view_plus_text_length - step, y, paint);
        //System.out.println(text);
        if (!isStarting) {
            return;
        }
        step += moveSpeed;//速度设置：1-10
        if (step > temp_view_plus_two_text_length-viewWidth*3/4) {
            step = textLength;
            if(null != onMoveStatusListener){//有文字移动到最后的监听
                onMoveStatusListener.onMoveEnded();
                stopScroll();
            }
        }else {
            invalidate();
        }
    }

    /**
     * 开始滚动
     */
    private void startScroll() {
        if(textLength>viewWidth){
            isStarting = true;
            invalidate();
        }
    }

    /**
     * 停止滚动
     */
    public void stopScroll() {
        isStarting = false;
    }

    /**
     * 文本内容是否需要滚动
     * @return
     */
    public boolean needScrolled() {
        return textLength>viewWidth;
    }

    /**
     * 设置移动到最后的监听类
     * @param onMoveStatusListener
     */
    public void setOnMoveStatusListener(OnMoveStatusListener onMoveStatusListener) {
        this.onMoveStatusListener = onMoveStatusListener;
    }

    /**
     * 文字滑动的监听类
     */
    public interface OnMoveStatusListener {
        /**
         * 最后一个文字移出控件区域
         */
        public void onMoveEnded();
    }
}
