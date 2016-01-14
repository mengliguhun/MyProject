package com.example.administrator.myproject.view;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by shichaohui on 2015/8/3 0003.
 * <br/>
 * 可以添加HanderView、FooterView，并且HanderView的背景可以伸缩的RecyclerView
 */
public class RFRecyclerView extends RecyclerView implements Runnable {

    private static Context mContext;

    private ArrayList<View> mHeaderViews = new ArrayList<View>();
    private ArrayList<View> mFootViews = new ArrayList<View>();
    private Adapter mAdapter;

    int bgColor = Color.WHITE; // 刷新View的颜色



    public RFRecyclerView(Context context) {
        this(context, null);
    }

    public RFRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RFRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
       
        setOverScrollMode(OVER_SCROLL_NEVER);
        post(this);
    }

    /**
     * 添加头部视图，可以添加多个
     *
     * @param view
     */
    public void addHeaderView(View view) {
        mHeaderViews.add(view);
        if (mAdapter != null) {
            if (!(mAdapter instanceof WrapAdapter)) {
                mAdapter = new WrapAdapter(mHeaderViews, mFootViews, mAdapter);
            }
        }
    }

    /**
     * 添加脚部视图，此视图只能添加一个，添加多个时，默认最后添加的一个。
     *
     * @param view
     */
    public void addFootView(final View view) {
        mFootViews.clear();
        mFootViews.add(view);
        if (mAdapter != null) {
            if (!(mAdapter instanceof WrapAdapter)) {
                mAdapter = new WrapAdapter(mHeaderViews, mFootViews, mAdapter);
            }
        }
    }

    @Override
    public void setAdapter(Adapter adapter) {
        
        // 使用包装了头部和脚部的适配器
        adapter = new WrapAdapter(mHeaderViews, mFootViews, adapter);
        super.setAdapter(adapter);
        
        mAdapter = adapter;
    }

    @Override
    public void setLayoutManager(LayoutManager layout) {
        super.setLayoutManager(layout);
        // 根据布局管理器设置分割线
        if (layout instanceof LinearLayoutManager) {
            super.addItemDecoration(new DividerItemDecoration(mContext,
                    ((LinearLayoutManager) layout).getOrientation(), true));
        } else {
            super.addItemDecoration(new DividerGridItemDecoration(mContext, true));
        }
    }

    @Override
    public void run() {
        LayoutManager manager = getLayoutManager();
        
        layoutStaggeredGridHeadAttach((StaggeredGridLayoutManager) manager);
        
//        if (((WrapAdapter) mAdapter).getFootersCount() > 0) {
//            // 脚部先隐藏
//            mFootViews.get(0).setVisibility(GONE);
//        }
    }

    /**
     * 给StaggeredGridLayoutManager附加头部和滑动过度监听
     *
     * @param manager
     */
    private void layoutStaggeredGridHeadAttach(StaggeredGridLayoutManager manager) {
        
        // 从前向后查找Header并设置为充满一行
        View view;
        for (int i = 0; i < mAdapter.getItemCount(); i++) {
            if (((WrapAdapter) mAdapter).isHeader(i)) {
                view = getChildAt(i);
                ((StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams())
                        .setFullSpan(true);
                view.requestLayout();
            } else {
                break;
            }
        }
    }

    /**
     * 给StaggeredGridLayoutManager附加脚部
     *
     * @param view
     */
    private void layoutStaggeredGridFootAttach(View view) {
        // Footer设置为充满一行
        ((StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams())
                .setFullSpan(true);
        // view.requestLayout();
    }
    /**
     * 自定义带有头部/脚部的适配器
     */
    private class WrapAdapter extends Adapter<ViewHolder> {

        private Adapter mAdapter;

        private ArrayList<View> mHeaderViews;

        private ArrayList<View> mFootViews;

        final ArrayList<View> EMPTY_INFO_LIST =
                new ArrayList<View>();
        private int headerPosition = 0;

        public WrapAdapter(ArrayList<View> mHeaderViews, ArrayList<View> mFootViews, Adapter mAdapter) {
            this.mAdapter = mAdapter;
            if (mHeaderViews == null) {
                this.mHeaderViews = EMPTY_INFO_LIST;
            } else {
                this.mHeaderViews = mHeaderViews;
            }
            if (mFootViews == null) {
                this.mFootViews = EMPTY_INFO_LIST;
            } else {
                this.mFootViews = mFootViews;
            }
        }

        /**
         * 当前布局是否为Header
         *
         * @param position
         * @return
         */
        public boolean isHeader(int position) {
            return position >= 0 && position < mHeaderViews.size();
        }

        /**
         * 当前布局是否为Footer
         *
         * @param position
         * @return
         */
        public boolean isFooter(int position) {
            return position < getItemCount() && position >= getItemCount() - mFootViews.size();
        }

        /**
         * Header的数量
         *
         * @return
         */
        public int getHeadersCount() {
            return mHeaderViews.size();
        }

        /**
         * Footer的数量
         *
         * @return
         */
        public int getFootersCount() {
            return mFootViews.size();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == RecyclerView.INVALID_TYPE) {
                return new HeaderViewHolder(mHeaderViews.get(headerPosition++));
            } else if (viewType == RecyclerView.INVALID_TYPE - 1) {
                StaggeredGridLayoutManager.LayoutParams params = new StaggeredGridLayoutManager.LayoutParams(
                        StaggeredGridLayoutManager.LayoutParams.MATCH_PARENT, StaggeredGridLayoutManager.LayoutParams.WRAP_CONTENT);
                params.setFullSpan(true);
                mFootViews.get(0).setLayoutParams(params);
                return new HeaderViewHolder(mFootViews.get(0));
            }
            return mAdapter.onCreateViewHolder(parent, viewType);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            int numHeaders = getHeadersCount();
            if (position < numHeaders) {
                return;
            }
            int adjPosition = position - numHeaders;
            int adapterCount;
            if (mAdapter != null) {
                adapterCount = mAdapter.getItemCount();
                if (adjPosition < adapterCount) {
                    mAdapter.onBindViewHolder(holder, adjPosition);
                    return;
                }
            }
        }

        @Override
        public int getItemCount() {
            if (mAdapter != null) {
                return getHeadersCount() + getFootersCount() + mAdapter.getItemCount();
            } else {
                return getHeadersCount() + getFootersCount();
            }
        }

        @Override
        public int getItemViewType(int position) {
            int numHeaders = getHeadersCount();
            if (position < numHeaders) {
                return RecyclerView.INVALID_TYPE;
            }
            int adjPosition = position - numHeaders;
            int adapterCount;
            if (mAdapter != null) {
                adapterCount = mAdapter.getItemCount();
                if (adjPosition < adapterCount) {
                    return mAdapter.getItemViewType(adjPosition);
                }
            }
            return RecyclerView.INVALID_TYPE - 1;
        }

        @Override
        public long getItemId(int position) {
            int numHeaders = getHeadersCount();
            if (mAdapter != null && position >= numHeaders) {
                int adjPosition = position - numHeaders;
                int adapterCount = mAdapter.getItemCount();
                if (adjPosition < adapterCount) {
                    return mAdapter.getItemId(adjPosition);
                }
            }
            return -1;
        }

        private class HeaderViewHolder extends ViewHolder {
            public HeaderViewHolder(View itemView) {
                super(itemView);
            }
        }
    }

    /**
     * 刷新和加载更多数据的监听接口
     */
    public interface LoadDataListener {

        /**
         * 执行刷新
         */
        void onRefresh();

        /**
         * 执行加载更多
         */
        void onLoadMore();

    }

}
