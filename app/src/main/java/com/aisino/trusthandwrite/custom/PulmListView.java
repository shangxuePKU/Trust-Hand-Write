package com.aisino.trusthandwrite.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import com.aisino.trusthandwrite.R;


/**
 * 上拉加载更多的ListView
 */
public class PulmListView extends ListView {
    /**
     * 是否处于加载更多状态中.
     */
    private boolean mIsLoading;

    /**
     * 分页是否结束.
     */
    private boolean mIsPageFinished;

    /**
     * Footer View,支持自定义.
     */
    private View mLoadMoreView;

    private OnScrollListener mUserOnScrollListener;

    private OnPullUpLoadMoreListener mOnPullUpLoadMoreListener;

    public PulmListView(Context context) {
        this(context, null);
    }

    public PulmListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PulmListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mIsLoading = false;
        mIsPageFinished = false;
        mLoadMoreView = new LoadMoreView(getContext());
        super.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // 调用用户设置的OnScrollListener
                if (mUserOnScrollListener != null) {
                    mUserOnScrollListener.onScrollStateChanged(view, scrollState);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                // 调用用户设置的OnScrollListener
                if (mUserOnScrollListener != null) {
                    mUserOnScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
                }

                int lastVisibleItem = firstVisibleItem + visibleItemCount;
                if (!mIsLoading && !mIsPageFinished && lastVisibleItem == totalItemCount) {
                    if (mOnPullUpLoadMoreListener != null) {
                        mIsLoading = true;
                        showLoadMoreView();
                        mOnPullUpLoadMoreListener.onPullUpLoadMore();
                    }
                }
            }
        });
    }

    private void showLoadMoreView() {
        if (findViewById(R.id.id_load_more_layout) == null) {
            addFooterView(mLoadMoreView);
        }
    }

    @Override
    public void setOnScrollListener(OnScrollListener l) {
        this.mUserOnScrollListener = l;
    }

    /**
     * 加载更多结束后ListView回调方法.
     *
     * @param isPageFinished 分页是否结束
     */
    public void onFinishLoading(boolean isPageFinished) {
        mIsLoading = false;
        setIsPageFinished(isPageFinished);
    }

    private void setIsPageFinished(boolean isPageFinished) {
        mIsPageFinished = isPageFinished;
        removeFooterView(mLoadMoreView);
    }

    /**
     * 设置自定义的加载更多View
     *
     * @param view 加载更多View
     */
    public void setLoadMoreView(View view) {
        removeFooterView(mLoadMoreView);
        mLoadMoreView = view;
    }

    /**
     * 设置上拉加载更多的回调接口.
     <<<<<<< 04cb7c2c4e557f0b3cf0a2de1164f57839286bde
     =======
     * @param l 上拉加载更多的回调接口
    >>>>>>> 提交上拉加载更多ListView实现
     */
    public void setOnPullUpLoadMoreListener(OnPullUpLoadMoreListener l) {
        this.mOnPullUpLoadMoreListener = l;
    }

    /**
     * 上拉加载更多的回调接口
     */
    public interface OnPullUpLoadMoreListener {
        void onPullUpLoadMore();
    }
}
