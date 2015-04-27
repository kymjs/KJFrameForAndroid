package org.kymjs.kjframe.widget;

import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;

public abstract class KJAdapter<T> extends BaseAdapter implements
        AbsListView.OnScrollListener {

    protected LayoutInflater mInflater;
    protected List<T> mDatas;
    protected final int mItemLayoutId;
    protected AbsListView mList;
    protected boolean isScrolling;

    private AbsListView.OnScrollListener listener;

    public KJAdapter(AbsListView view, List<T> mDatas, int itemLayoutId) {
        this.mInflater = LayoutInflater.from(view.getContext());
        this.mDatas = mDatas;
        this.mItemLayoutId = itemLayoutId;
        this.mList = view;
        mList.setOnScrollListener(this);
    }

    public void refresh(List<T> datas) {
        this.mDatas = datas;
        notifyDataSetChanged();
    }

    public void addOnScrollListener(AbsListView.OnScrollListener l) {
        this.listener = l;
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public T getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final AdapterHolder viewHolder = getViewHolder(position, convertView,
                parent);
        convert(viewHolder, getItem(position), isScrolling);
        return viewHolder.getConvertView();

    }

    private AdapterHolder getViewHolder(int position, View convertView,
            ViewGroup parent) {
        return AdapterHolder.get(convertView, parent, mItemLayoutId, position);
    }

    public abstract void convert(AdapterHolder helper, T item,
            boolean isScrolling);

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        // 设置是否滚动的状态
        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
            isScrolling = false;
            this.notifyDataSetChanged();
        } else {
            isScrolling = true;
        }
        if (listener != null) {
            listener.onScrollStateChanged(view, scrollState);
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
            int visibleItemCount, int totalItemCount) {
        if (listener != null) {
            listener.onScroll(view, firstVisibleItem, visibleItemCount,
                    totalItemCount);
        }
    }
}
