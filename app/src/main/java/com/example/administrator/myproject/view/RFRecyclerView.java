package com.example.administrator.myproject.view;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by shichaohui on 2015/8/3 0003.
 * <br/>
 * 可以添加HanderView、FooterView
 */
public class RFRecyclerView extends RecyclerView {

    private  Context mContext;

    private ArrayList<View> mHeaderViews = new ArrayList<View>();
    private ArrayList<View> mFootViews = new ArrayList<View>();
    private Adapter mRecyclerViewAdapter;
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
    }

    /**
     * 添加头部视图，可以添加多个
     *
     * @param view
     */
    public void addHeaderView(View view) {
        mHeaderViews.add(view);
        if (mRecyclerViewAdapter != null) {
            if (!(mRecyclerViewAdapter instanceof WrapAdapter)) {
                mRecyclerViewAdapter = new WrapAdapter(mHeaderViews, mFootViews, mRecyclerViewAdapter);
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
        if (mRecyclerViewAdapter != null) {
            if (!(mRecyclerViewAdapter instanceof WrapAdapter)) {
                mRecyclerViewAdapter = new WrapAdapter(mHeaderViews, mFootViews, mRecyclerViewAdapter);
            }
        }
    }

    @Override
    public void setAdapter(Adapter adapter) {
        // 使用包装了头部和脚部的适配器

        if (mHeaderViews.size()>0 || mFootViews.size() >0){
            mRecyclerViewAdapter = new WrapAdapter(mHeaderViews, mFootViews, adapter);
        }
       else {
            mRecyclerViewAdapter =adapter;
        }
        super.setAdapter(mRecyclerViewAdapter);
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
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
            RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
            if(manager instanceof GridLayoutManager) {
                final GridLayoutManager gridManager = ((GridLayoutManager) manager);
                gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        return getItemViewType(position) == 0 ? 1 : gridManager.getSpanCount();
                    }
                });
            }
        }

        @Override
        public void onViewAttachedToWindow(ViewHolder holder) {
            super.onViewAttachedToWindow(holder);
            ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
            if(getItemViewType(holder.getLayoutPosition()) != 0){
                if(lp != null && lp instanceof StaggeredGridLayoutManager.LayoutParams) {
                    StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;
                    p.setFullSpan(true);
                }
            }

        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == RecyclerView.INVALID_TYPE) {
                return new HeaderViewHolder(mHeaderViews.get(headerPosition++));
            } else if (viewType == RecyclerView.INVALID_TYPE - 1) {
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
            Log.e("position","position:"+position);
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
    public void notifyDataSetChanged(){
        getAdapter().notifyDataSetChanged();
    }
}
