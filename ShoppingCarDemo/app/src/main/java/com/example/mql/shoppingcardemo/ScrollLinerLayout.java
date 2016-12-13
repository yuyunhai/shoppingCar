package com.example.mql.shoppingcardemo;

/**
 * Created by maqianli on 16/12/12.
 */
import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.Scroller;

public class ScrollLinerLayout extends LinearLayout {

	public ScrollLinerLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context) {
		mScroller = new Scroller(context);
	}

	private Scroller mScroller;

	@Override
	public void computeScroll() {
//		Log.i("Scroller", "computeScroll");
		if (mScroller.computeScrollOffset()) {//判断滑动是否结束
			scrollTo(mScroller.getCurrX(), 0);//估算滑动位移，使其滑动到指定位置
			postInvalidate();//刷新屏幕 ,调用后它会用handler通知UI线程重绘屏幕,
		}
	}


	public void snapToScreen(int whichScreen) {
		int curscrollerx = getScrollX();//得到目前的X坐标
		mScroller.startScroll(curscrollerx, 0, whichScreen - curscrollerx, 0, 500);//开始滚动起点，滚动的距离距离，和滚动的持续时间
		invalidate();//刷新View,必须是在UI线程中进行工作，重回界面

	}

}
