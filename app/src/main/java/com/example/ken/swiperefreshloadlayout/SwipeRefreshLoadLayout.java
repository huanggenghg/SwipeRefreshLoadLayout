package com.example.ken.swiperefreshloadlayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AbsListView;
import android.widget.ListView;

/**
 * Created by ken on 17-3-10.
 */
public class SwipeRefreshLoadLayout extends SwipeRefreshLayout {

    private final int mScaledTouchSlop;
    private final View mFooterView;

    private int CHILD_TYPE = 0; // 0:ListView 1:RecyclerView
    private ListView mListView;
    private RecyclerView mRecyclerView;

    private float mDownY, mUpY;
    private boolean mIsLoading;

    private OnLoadListener mOnLoadListener;

    public SwipeRefreshLoadLayout(Context context) {
        super(context);

        // 控件移动的最小距离，手移动的距离大于此才能移动控件
        mScaledTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

        mFooterView = View.inflate(context, R.layout.view_footer, null);
        System.out.println("scaled touch slop : " + mScaledTouchSlop);
    }

    public SwipeRefreshLoadLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        // 控件移动的最小距离，手移动的距离大于此才能移动控件
        mScaledTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

        mFooterView = View.inflate(context, R.layout.view_footer, null);
        System.out.println("scaled touch slop : " + mScaledTouchSlop);

        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.SwipeRefreshLoadLayout);
        CHILD_TYPE = a.getInteger(R.styleable.SwipeRefreshLoadLayout_type, 0);
        a.recycle();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (mRecyclerView == null) {
            if (getChildCount() > 1) {
                // just demo to test both ListView and RecyclerView
                if (getChildAt(0) instanceof ListView) {
                    mListView = (ListView) getChildAt(0);
                    CHILD_TYPE = 0;
                } else if (getChildAt(1) instanceof RecyclerView) {
                    mRecyclerView = (RecyclerView) getChildAt(1);
                    CHILD_TYPE = 1;
                }
                setChildViewOnScroll();
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                if (canLoadMore())
                    loadData();
                break;
            case MotionEvent.ACTION_UP:
                mUpY = ev.getY();
        }
        return super.dispatchTouchEvent(ev);
    }

    public void setLoading(boolean loading) {
        mIsLoading = loading;
        if (mIsLoading) {
            if (CHILD_TYPE == 0) {
                mListView.addFooterView(mFooterView);
            } else {
                ((RecyclerViewAdapter) mRecyclerView.getAdapter()).setFooterOn();
            }
        } else {
            if (CHILD_TYPE == 0) {
                mListView.removeFooterView(mFooterView);
            } else {
                ((RecyclerViewAdapter) mRecyclerView.getAdapter()).setFooterOff();
            }
            mDownY = 0;
            mUpY = 0;
        }
    }

    public void setOnLoadListener(OnLoadListener listener) {
        mOnLoadListener = listener;
    }

    private void setChildViewOnScroll() {
        if (CHILD_TYPE == 0) {
            mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                    if (canLoadMore())
                        loadData();
                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                }
            });
        } else {
            mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if (canLoadMore())
                        loadData();
                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                }
            });
        }
    }

    private boolean canLoadMore() {
        boolean isUp = (mDownY - mUpY) >= mScaledTouchSlop;
        if (isUp)
            System.out.println("moving up...");

        boolean isLastItem = false;
        if (CHILD_TYPE == 0) {
            if (mListView != null && mListView.getAdapter() != null)
                isLastItem = mListView.getLastVisiblePosition() == (mListView.getAdapter().getCount() - 1);
        } else {
            if (mRecyclerView != null && mRecyclerView.getAdapter() != null) {
                LinearLayoutManager manager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
                if (manager != null)
                    isLastItem = manager.findLastCompletelyVisibleItemPosition() == manager.getItemCount() - 1;
            }
        }
        if (isLastItem) {
            System.out.println("is last item");
        }

        if (!mIsLoading)
            System.out.println("not loading.");

        return isUp && isLastItem && !mIsLoading;
    }

    private void loadData() {
        System.out.println("loading data...");
        if (mOnLoadListener != null) {
            setLoading(true);
            mOnLoadListener.onLoad();
        }
    }


    public interface OnLoadListener {
        void onLoad();
    }

}
