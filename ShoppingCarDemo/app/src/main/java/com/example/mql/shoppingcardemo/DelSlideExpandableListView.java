package com.example.mql.shoppingcardemo;

/**
 * Created by maqianli on 16/12/12.
 */

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ExpandableListView;




public class DelSlideExpandableListView extends ExpandableListView implements GestureDetector.OnGestureListener, View.OnTouchListener {
    //手势监听
    private GestureDetector mDetector;
    private String TAG = "xiaoma";

    public DelSlideExpandableListView(Context context) {
        super(context);
        init(context);
    }

    public DelSlideExpandableListView(Context context, AttributeSet att) {
        super(context, att);
        init(context);
    }

    private int standard_touch_target_size = 0;
    private float mLastMotionX;
    // 有item被拉出
    public boolean deleteView = false;
    // 当前拉出的view
    private ScrollLinerLayout mScrollLinerLayout = null;
    // 滑动着
    private boolean scroll = false;
    // 禁止拖动
    private boolean forbidScroll = false;
    // 禁止拖动
    private boolean clicksameone = false;
    // 当前拉出的位置
    private int position;
    // 消息冻结
    private boolean freeze = false;

    private void init(Context mContext) {
        mDetector = new GestureDetector(mContext, this);//创建手势监听类
        // mDetector.setIsLongpressEnabled(false);
        //出现删除的最大距离
        standard_touch_target_size = (int) getResources().getDimension(R.dimen.delete_action_len);
        this.setOnTouchListener(this);//设置监听
    }

    public void reset() {
        reset(false);
    }

    //重置
    public void reset(boolean noaction) {
        position = -1;
        deleteView = false;
        if (mScrollLinerLayout != null) {
            if (!noaction) {
                //滑出
                mScrollLinerLayout.snapToScreen(0);
                Log.e("xiaoma",22+"");
            } else {
                //滑回去
                mScrollLinerLayout.scrollTo(0, 0);
                Log.e("xiaoma",11+"");
            }
            mScrollLinerLayout = null;
        }

        scroll = false;
    }

    public boolean onDown(MotionEvent e) {//如果手势向下滑，则禁止一切滑动动作，将其设置为不滑动状态
        // Log.i(TAG, "onDown");
        mLastMotionX = e.getX();
        int p = this.pointToPosition((int) e.getX(), (int) e.getY()) - this.getFirstVisiblePosition();
        if (deleteView) {
            if (p != position) {
                // 吃掉，不在有消息
                freeze = true;
                return true;
            } else {
                clicksameone = true;
            }
        }
        position = p;
        scroll = false;
        return false;
    }

    public void onLongPress(MotionEvent e) {
        // Log.i(TAG, "onLongPress");
    }

    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        // Log.i(TAG, "onScroll" + e1.getX() + ":" + distanceX);
        // 第二次
        if (scroll) {
            int deltaX = (int) (mLastMotionX - e2.getX());
            if (deleteView) {//如果被拉出，则拉出的距离为
                deltaX += standard_touch_target_size;//一直在变da
            }
            if (deltaX >= 0 && deltaX <= standard_touch_target_size) {//判断是否在拉出
                mScrollLinerLayout.scrollBy(deltaX - mScrollLinerLayout.getScrollX(), 0);//横向滑动
            }
            return true;
        }
        if (!forbidScroll) {//没有禁止滑动
            forbidScroll = true;
            // x方向滑动，才开始拉动
            if (Math.abs(distanceX) > Math.abs(distanceY)) {//判断横向滑动大于Y
                View v = this.getChildAt(position);
                boolean ischild = v instanceof ScrollLinerLayout;
                if (ischild) {
                    mScrollLinerLayout = (ScrollLinerLayout) v;
                    scroll = true;
                    int deltaX = (int) (mLastMotionX - e2.getX());
                    if (deleteView) {
                        // 再次点击的时候，要把deltax增加
                        deltaX += standard_touch_target_size;
                    }
                    if (deltaX >= 0 && deltaX <= standard_touch_target_size) {
                        mScrollLinerLayout.scrollBy((int) (e1.getX() - e2.getX()), 0);
                    }
                }
            }
        }
        return false;
    }

    public void onShowPress(MotionEvent e) {
        // Log.i(TAG, "onShowPress");
    }

    public boolean onSingleTapUp(MotionEvent e) {
        // Log.i(TAG, "onSingleTapUp");
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (scroll || deleteView) {
            return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
            boolean isfreeze = freeze;
            boolean isclicksameone = clicksameone;
            forbidScroll = false;
            clicksameone = false;
            freeze = false;
            if (isfreeze) {
                // 上一个跟当前点击不一致 还原
                reset();
                return true;
            }
            int deltaX2 = (int) (mLastMotionX - event.getX());
            // 不存在
            // Log.i(TAG, "scroll:" + scroll + "deltaX2:" + deltaX2);
            if (scroll && deltaX2 >= standard_touch_target_size / 2) {
                mScrollLinerLayout.snapToScreen(standard_touch_target_size);
                deleteView = true;
                scroll = false;
                return true;
            }
            if (deleteView && scroll && deltaX2 >= -standard_touch_target_size / 2) {
                mScrollLinerLayout.snapToScreen(standard_touch_target_size);
                deleteView = true;
                scroll = false;
                return true;
            }
            if(isclicksameone||scroll){
                reset();
                return true;
            }
            reset();
        }
        if (freeze) {
            return true;
        }
        // Log.i(TAG, "onTouchEvent");
        return mDetector.onTouchEvent(event);

    }

    public void deleteItem() {
        Log.i(TAG, "deleteItem");
        reset(true);

    }

    /*
     * (non-Javadoc)
     *
     * @see android.view.GestureDetector.OnGestureListener#onFling(android.view.
     * MotionEvent, android.view.MotionEvent, float, float)
     */
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }
}
