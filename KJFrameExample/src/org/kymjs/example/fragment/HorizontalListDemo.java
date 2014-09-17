package org.kymjs.example.fragment;

import org.kymjs.aframe.ui.BindView;
import org.kymjs.aframe.ui.ViewInject;
import org.kymjs.aframe.ui.fragment.BaseFragment;
import org.kymjs.aframe.ui.widget.HorizontalListView;
import org.kymjs.aframe.ui.widget.RoundImageView;
import org.kymjs.example.R;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;

public class HorizontalListDemo extends BaseFragment {

    @BindView(id = R.id.list)
    private HorizontalListView mListview;

    @Override
    protected View inflaterView(LayoutInflater inflater,
            ViewGroup container, Bundle bundle) {
        return View.inflate(getActivity(), R.layout.horizontal, null);
    }

    @Override
    protected void initWidget(View parentView) {
        super.initWidget(parentView);
        mListview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                ViewInject.toast("点击了第" + position + "项");
            }
        });
        mListview.setAdapter(new ListDemoAdapter());
    }

    private class ListDemoAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return 20;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View v, ViewGroup parent) {
            ViewHolder holder = null;
            if (v == null) {
                v = View.inflate(getActivity(),
                        R.layout.item_round_image, null);
                holder = new ViewHolder();
                holder.image = (RoundImageView) v
                        .findViewById(R.id.item_round_imageview);
                v.setTag(holder);
            } else {
                holder = (ViewHolder) v.getTag();
            }
            // 设置圆形颜色
            holder.image.setBorderOutsideColor(0xffffffff);
            // 设置圆形宽度
            holder.image.setBorderThickness(2);
            return v;
        }
    }

    static class ViewHolder {
        RoundImageView image;
    }
}
