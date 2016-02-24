package com.luomo.demo;

import android.app.Activity;
import android.os.Bundle;
import com.luomo.demo.view.SlideShowView;

public class MainActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        SlideShowView slideShowView= (SlideShowView) findViewById(R.id.slideshowView);
        //设置字符串
        String[] texts = new String[]{
                "美国声称中国在南海搞“军事化” 中方回应",
                "美国发生枪击案致7死 嫌疑人被捕作案动机不明,美国发生枪击案致7死 ",
                "入境邮包检出“植物界大熊猫”",
                "山东奶孙失联事件官方通报:孩子遇害 尸体已找到,山东奶孙失联事件官"
        };
        slideShowView.setDisplayStrings(texts);
    }
}
