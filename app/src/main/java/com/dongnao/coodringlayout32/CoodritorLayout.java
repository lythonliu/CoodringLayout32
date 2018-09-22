package com.dongnao.coodringlayout32;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.NestedScrollingParentHelper;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;

import java.lang.reflect.Constructor;

/**
 * Created by Administrator on 2017/7/14.
 */

public class CoodritorLayout extends RelativeLayout  implements ViewTreeObserver.OnGlobalLayoutListener, NestedScrollingParent {
    private final NestedScrollingParentHelper mNestedScrollingParentHelper =
            new NestedScrollingParentHelper(this);
    public CoodritorLayout(Context context) {
        super(context);
    }

    public CoodritorLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CoodritorLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @Override
    public void onGlobalLayout() {

    }



    public static class LayoutParams extends RelativeLayout.LayoutParams {
        private IBehaivor mBehavior;

        public IBehaivor getBehavior() {
            return mBehavior;
        }

        public IBehaivor setBehavior(IBehaivor behavior) {
            return mBehavior = behavior;
        }

        public LayoutParams(Context c, AttributeSet attrs) {

            super(c, attrs);
            final TypedArray a = c.obtainStyledAttributes(attrs,
                    R.styleable.BehaviorRelativeLayout);
            mBehavior = parseBehavior(c, attrs, a.getString(
                    R.styleable.BehaviorRelativeLayout_behavior));
            a.recycle();
        }

        public LayoutParams(int w, int h) {
            super(w, h);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }

        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }

    }

    static IBehaivor parseBehavior(Context context, AttributeSet attrs, String name) {
        if (TextUtils.isEmpty(name)) {
            return null;
        }
        try {
            final Class clazz = Class.forName(name, true,
                    context.getClassLoader());
            Constructor c = clazz.getConstructor(new Class<?>[]{ Context.class, AttributeSet.class });
            c.setAccessible(true);
            return (IBehaivor) c.newInstance(context, attrs);
        } catch (Exception e) {
            throw new RuntimeException("Could not inflate Behavior subclass " + name, e);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            lastX = event.getRawX();
            lastY = event.getRawY();
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            onTouchMove(event);
        }
        return super.onTouchEvent(event);
    }

    private void onTouchMove(MotionEvent event) {
        float moveX = event.getRawX();
        float moveY = event.getRawY();
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            LayoutParams param = (LayoutParams) child.getLayoutParams();
            if (param.getBehavior() != null) {
                param.getBehavior().onTouchMove(this, child, event, moveX, moveY, lastX, lastY);
            }
        }
        lastY = moveY;
        lastX = moveX;
    }

    public float lastX;
    public float lastY;



    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        Log.d("cici","onStartNestedScroll");
        return true;
    }

    @Override
    public void onStopNestedScroll(View child) {
        Log.d("cici","onStopNestedScroll");
        mNestedScrollingParentHelper.onStopNestedScroll(child);
    }

    @Override
    public void onNestedScrollAccepted(View child, View target, int axes) {
        Log.d("cici","onNestedScrollAccepted");
        mNestedScrollingParentHelper.onNestedScrollAccepted(child,target,axes);
    }

    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        Log.d("cici","onNestedScroll");
        Log.d("cici","dxConsumed -->" +dxConsumed + " dyConsumed-->"+dyConsumed);
        Log.d("cici","dxUnconsumed -->" +dxUnconsumed + " dyUnconsumed-->"+dyUnconsumed);
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            LayoutParams param = (LayoutParams) child.getLayoutParams();
            if (param.getBehavior() != null) {
                param.getBehavior().onNestedScroll(target ,child, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
            }
        }
    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
    }

    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        return false;
    }

    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        return false;
    }

}
