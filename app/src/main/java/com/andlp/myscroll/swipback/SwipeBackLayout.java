package com.andlp.myscroll.swipback;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntDef;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.andlp.myscroll.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;


public class SwipeBackLayout extends FrameLayout {

    public static final int EDGE_LEFT = ViewDragHelper.EDGE_LEFT;       //边缘
    public static final int EDGE_RIGHT = ViewDragHelper.EDGE_RIGHT;
    public static final int EDGE_ALL = EDGE_LEFT | EDGE_RIGHT;

    public static final int STATE_IDLE = ViewDragHelper.STATE_IDLE;                         //空闲状态
    public static final int STATE_DRAGGING = ViewDragHelper.STATE_DRAGGING; //拖动状态
    public static final int STATE_SETTLING = ViewDragHelper.STATE_SETTLING;       //状态 设置中

    private static final int DEFAULT_SCRIM_COLOR = 0x99000000;  //默认遮罩层  颜色
    private static final int FULL_ALPHA = 255;                                      //不透明度
    private static final float DEFAULT_SCROLL_THRESHOLD = 0.75f; //默认滑动阈值
    private static final int OVERSCROLL_DISTANCE = 2;                     //反弹时距离

    private float mScrollFinishThreshold = DEFAULT_SCROLL_THRESHOLD;    //滚动完成的阈值    默认屏幕宽的0.75
    private ViewDragHelper mHelper;                                                                  //触摸封装事件
    private float mScrollPercent;                                                                           //滚动百分比
    private float mScrimOpacity;                                                                          //遮罩层 不透明度

    private FragmentActivity mActivity;                                                              //当前的activity
    private View mContentView;                                                                           //内容视图
    private SwipeBackFragment mFragment;                                                     //当前的fragment
    private Fragment mPreFragment;                                                                 //之前的fragment

    private Drawable mShadowLeft;                                                                   //左边的阴影   图
    private Drawable mShadowRight;                                                                 //右边的阴影  图
    private Rect mTmpRect = new Rect();                                                          //矩形  绘制使用

    private int mEdgeFlag;                                                                                  //边界标识   left/right/all
    private boolean mEnable = true;                                                                 //是否可以滑动
    private int mCurrentSwipeOrientation;                                                      //当前滑动方向

    private Context context;
    private EdgeLevel edgeLevel;                                                                      //边缘等级

    public enum EdgeLevel { MAX, MIN, MED  }//中等
    private List<OnSwipeListener> mListeners;//要通过发送事件的监听器 的集合

    public SwipeBackLayout(Context context) {
        this(context, null);
    }
    public SwipeBackLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public SwipeBackLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    private void init() {//初始化 阴影与方向
        mHelper = ViewDragHelper.create(this, new ViewDragCallback());
        setShadow(R.drawable.shadow_left, EDGE_LEFT);
        setEdgeOrientation(EDGE_ALL);
    }

    public void setScrollThresHold(float threshold) {
        if (threshold >= 1.0f || threshold <= 0) {
            throw new IllegalArgumentException("Threshold value should be between 0 and 1.0");
        }
        mScrollFinishThreshold = threshold;
    }//设置滚动阈值，当scrollPercent超过此值时，我们将关闭活动
    public void setEdgeOrientation(int orientation) {
        mEdgeFlag = orientation;
        mHelper.setEdgeTrackingEnabled(orientation);

        if (orientation == EDGE_RIGHT || orientation == EDGE_ALL) {
            setShadow(R.drawable.shadow_right, EDGE_RIGHT);
        }
    }//启用父视图的选定边的边缘跟踪  参数left  right
    public void setEdgeLevel(EdgeLevel edgeLevel) {
        this.edgeLevel = edgeLevel;
        validateEdgeLevel(0, edgeLevel);
    }
    public void setEdgeLevel(int widthPixel) {
        validateEdgeLevel(widthPixel, null);
    }
    public EdgeLevel getEdgeLevel() {
        return edgeLevel;
    }
    private void validateEdgeLevel(int widthPixel, EdgeLevel edgeLevel) {
        try {
            DisplayMetrics metrics = new DisplayMetrics();
            WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            windowManager.getDefaultDisplay().getMetrics(metrics);
            Field mEdgeSize = mHelper.getClass().getDeclaredField("mEdgeSize");
            mEdgeSize.setAccessible(true);
            if (widthPixel != 0) {
                mEdgeSize.setInt(mHelper, widthPixel);
            } else {
                if (edgeLevel == EdgeLevel.MAX) {
                    mEdgeSize.setInt(mHelper, metrics.widthPixels);
                } else if (edgeLevel == EdgeLevel.MED) {
                    mEdgeSize.setInt(mHelper, metrics.widthPixels / 2);
                } else if (edgeLevel == EdgeLevel.MIN) {
                    mEdgeSize.setInt(mHelper, ((int) (20 * metrics.density + 0.5f)));
                }
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @IntDef({EDGE_LEFT, EDGE_RIGHT, EDGE_ALL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface EdgeOrientation {  }

    public void setShadow(Drawable shadow, int edgeFlag) {
        if ((edgeFlag & EDGE_LEFT) != 0) {
            mShadowLeft = shadow;
        } else if ((edgeFlag & EDGE_RIGHT) != 0) {
            mShadowRight = shadow;
        }
        invalidate();//进行刷新
    }
    public void setShadow(int resId, int edgeFlag) {
        setShadow(getResources().getDrawable(resId), edgeFlag);
    }
    public void addSwipeListener(OnSwipeListener listener) {
        if (mListeners == null) {
            mListeners = new ArrayList<>();
        }
        mListeners.add(listener);
    }//在向此视图发送滑动事件时添加要调用的回调。

    public void removeSwipeListener(OnSwipeListener listener) {
        if (mListeners == null) {
            return;
        }
        mListeners.remove(listener);
    }//从一组侦听器中移除一个侦听器

    public interface OnSwipeListener {
        void onDragStateChange(int state);                 //状态改变时 回调此函数    STATE_IDLE/STATE_DRAGGING/空闲 拖动等
        void onEdgeTouch(int oritentationEdgeFlag);//触摸边缘时 回调此函数
        void onDragScrolled(float scrollPercent);         //当第一次滚动百分比超过阈值时 回调此函数
    }// 供外部使用的一些回调

    //进行绘制---------------
    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
//        Log.i("app中","在drawChild----"+child);
        boolean isDrawView = child == mContentView;
        boolean drawChild = super.drawChild(canvas, child, drawingTime);
        if (isDrawView && mScrimOpacity > 0 && mHelper.getViewDragState() != ViewDragHelper.STATE_IDLE) {
            drawShadow(canvas, child);//阴影和遮罩层
//            drawScrim(canvas, child); //
        }
        return drawChild;
    }//绘制 子布局和阴影等
    private void drawShadow(Canvas canvas, View child) {
        final Rect childRect = mTmpRect;
        child.getHitRect(childRect);

        if ((mCurrentSwipeOrientation & EDGE_LEFT) != 0) {
            mShadowLeft.setBounds(childRect.left - mShadowLeft.getIntrinsicWidth(), childRect.top, childRect.left, childRect.bottom);
            mShadowLeft.setAlpha((int) (mScrimOpacity * FULL_ALPHA));
            mShadowLeft.draw(canvas);
        } else if ((mCurrentSwipeOrientation & EDGE_RIGHT) != 0) {
            mShadowRight.setBounds(childRect.right, childRect.top, childRect.right + mShadowRight.getIntrinsicWidth(), childRect.bottom);
            mShadowRight.setAlpha((int) (mScrimOpacity * FULL_ALPHA));
            mShadowRight.draw(canvas);
        }
    }//绘制阴影
    private void drawScrim(Canvas canvas, View child) {
        final int baseAlpha = (DEFAULT_SCRIM_COLOR & 0xff000000) >>> 24;
        final int alpha = (int) (baseAlpha * mScrimOpacity);
        final int color = alpha << 24;

        if ((mCurrentSwipeOrientation & EDGE_LEFT) != 0) {
            canvas.clipRect(0, 0, child.getLeft(), getHeight());
        } else if ((mCurrentSwipeOrientation & EDGE_RIGHT) != 0) {
            canvas.clipRect(child.getRight(), 0, getRight(), getHeight());
        }
        canvas.drawColor(color);
    }    //绘制遮罩层
    //进行绘制

    @Override
    public void computeScroll() {  //通过draw调用
        mScrimOpacity = 1 - mScrollPercent;
        if (mScrimOpacity >= 0) {
            if (mHelper.continueSettling(false)) {
                ViewCompat.postInvalidateOnAnimation(this);
            }//自动滚动
        }
    }
    public void setFragment(SwipeBackFragment fragment, View view) {
        this.mFragment = fragment;//设置当前fragment
        mContentView = view;           //设置当前子view
    }
    public void hiddenFragment() {
        if (mPreFragment != null && mPreFragment.getView() != null) {
            mPreFragment.getView().setVisibility(GONE);
        }
    }
    public void attachToActivity(FragmentActivity activity) {
        mActivity = activity;
        TypedArray typedArray = activity.getTheme().obtainStyledAttributes(new int[]{
                android.R.attr.windowBackground
        });
        int background = typedArray.getResourceId(0, 0);
        typedArray.recycle();

        ViewGroup decor = (ViewGroup) activity.getWindow().getDecorView();
        ViewGroup decorChild = (ViewGroup) decor.getChildAt(0);
        decorChild.setBackgroundResource(background);
        decor.removeView(decorChild);
        addView(decorChild);
        setContentView(decorChild);
        decor.addView(this);
    }//activity最外层包裹
    public void attachToFragment(SwipeBackFragment swipeBackFragment, View view) {
        Log.i("app中","在 attachToFragment ----"+swipeBackFragment);
        addView(view);//在当前布局中直接添加view
        setFragment(swipeBackFragment, view);
    }//fragment最外层包裹

    private void setContentView(View view) {
        mContentView = view;
    }     //设置当前主界面
    public void setEnableGesture(boolean enable) {
        mEnable = enable;
    }//是否支持手势
    //滑动事件处理---------------
    class ViewDragCallback extends ViewDragHelper.Callback {

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
//            boolean dragEnable = mHelper.isEdgeTouched(mEdgeFlag, pointerId);
            boolean dragEnable =true;
            Log.i("app中","在 tryCaptureView ----"+dragEnable);
            if (dragEnable) {
//                if (mHelper.isEdgeTouched(EDGE_LEFT, pointerId)) {
//                    mCurrentSwipeOrientation = EDGE_LEFT;
//                } else if (mHelper.isEdgeTouched(EDGE_RIGHT, pointerId)) {
//                    mCurrentSwipeOrientation = EDGE_RIGHT;
//                }
                mCurrentSwipeOrientation= EDGE_ALL;
                if (mListeners != null && !mListeners.isEmpty()) {
                    for (OnSwipeListener listener : mListeners) {
                        listener.onEdgeTouch(mCurrentSwipeOrientation);
                    }
                }

                if (mPreFragment == null) {
                    if (mFragment != null) {
                        @SuppressLint("RestrictedApi")
                        List<Fragment> fragmentList = mFragment.getFragmentManager().getFragments();
                        if (fragmentList != null && fragmentList.size() > 1) {
                            int index = fragmentList.indexOf(mFragment);
                            for (int i = index - 1; i >= 0; i--) {
                                Fragment fragment = fragmentList.get(i);
                                if (fragment != null && fragment.getView() != null) {
                                    fragment.getView().setVisibility(VISIBLE);
                                    mPreFragment = fragment;
                                    break;
                                }
                            }
                        }
                    }
                } else {
                    View preView = mPreFragment.getView();
                    if (preView != null && preView.getVisibility() != VISIBLE) {
                        preView.setVisibility(VISIBLE);
                    }
                }
            }
            return dragEnable;
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            int ret = 0;
            if ((mCurrentSwipeOrientation & EDGE_LEFT) != 0) {
                ret = Math.min(child.getWidth(), Math.max(left, 0));
            } else if ((mCurrentSwipeOrientation & EDGE_RIGHT) != 0) {
                ret = Math.min(0, Math.max(left, -child.getWidth()));
            }
            return ret;
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);
            if ((mCurrentSwipeOrientation & EDGE_LEFT) != 0) {
                mScrollPercent = Math.abs((float) left / (getWidth() + mShadowLeft.getIntrinsicWidth()));
            } else if ((mCurrentSwipeOrientation & EDGE_RIGHT) != 0) {
                mScrollPercent = Math.abs((float) left / (mContentView.getWidth() + mShadowRight.getIntrinsicWidth()));
            }
            invalidate();

            if (mListeners != null && !mListeners.isEmpty()
                    && mHelper.getViewDragState() == STATE_DRAGGING && mScrollPercent <= 1 && mScrollPercent > 0) {
                for (OnSwipeListener listener : mListeners) {
                    listener.onDragScrolled(mScrollPercent);
                }
            }

            if (mScrollPercent > 1) {
                if (mFragment != null) {
                    if (mPreFragment instanceof SwipeBackFragment) {
                        ((SwipeBackFragment) mPreFragment).mLocking = true;
                    }
                    if (!mFragment.isDetached()) {
                        mFragment.mLocking = true;
                        mFragment.getFragmentManager().popBackStackImmediate();
                        mFragment.mLocking = false;
                    }
                    if (mPreFragment instanceof SwipeBackFragment) {
                        ((SwipeBackFragment) mPreFragment).mLocking = false;
                    }
                } else {
                    if (!mActivity.isFinishing()) {
                        mActivity.finish();
                        mActivity.overridePendingTransition(0, 0);
                    }
                }
            }
        }

        @Override
        public int getViewHorizontalDragRange(View child) {
            if (mFragment != null) {
                return 1;
            } else if (mActivity != null && ((SwipeBackActivity) mActivity).swipeBackPriority()) {
                return 1;
            }
            
            return 0;
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            final int childWidth = releasedChild.getWidth();

            int left = 0, top = 0;
            if ((mCurrentSwipeOrientation & EDGE_LEFT) != 0) {
                left = xvel > 0 || xvel == 0 && mScrollPercent > mScrollFinishThreshold ? (childWidth+ mShadowLeft.getIntrinsicWidth() + OVERSCROLL_DISTANCE) : 0;
            } else if ((mCurrentSwipeOrientation & EDGE_RIGHT) != 0) {
                left = xvel < 0 || xvel == 0 && mScrollPercent > mScrollFinishThreshold ? -(childWidth+ mShadowRight.getIntrinsicWidth() + OVERSCROLL_DISTANCE) : 0;
            }

            mHelper.settleCapturedViewAt(left, top);
            invalidate();
        }

        @Override
        public void onViewDragStateChanged(int state) {
            super.onViewDragStateChanged(state);
            if (mListeners != null && !mListeners.isEmpty()) {
                for (OnSwipeListener listener : mListeners) {
                    listener.onDragStateChange(state);
                }
            }
        }

        @Override
        public void onEdgeTouched(int edgeFlags, int pointerId) {
            super.onEdgeTouched(edgeFlags, pointerId);
            if ((mEdgeFlag & edgeFlags) != 0) {
                mCurrentSwipeOrientation = edgeFlags;
            }
        }

    }
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!mEnable) return super.onInterceptTouchEvent(ev);
        return mHelper.shouldInterceptTouchEvent(ev);
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!mEnable) return super.onTouchEvent(event);
        mHelper.processTouchEvent(event);
        return true;
    }
   //滑动事件处理结束------------

}