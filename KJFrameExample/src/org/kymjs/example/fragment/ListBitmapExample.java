package org.kymjs.example.fragment;

import org.kymjs.aframe.bitmap.KJBitmap;
import org.kymjs.aframe.bitmap.utils.BitmapCreate;
import org.kymjs.aframe.ui.BindView;
import org.kymjs.aframe.ui.fragment.BaseFragment;
import org.kymjs.aframe.ui.widget.KJListView;
import org.kymjs.aframe.ui.widget.KJListView.KJListViewListener;
import org.kymjs.example.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class ListBitmapExample extends BaseFragment {
    @BindView(id = R.id.listview)
    private KJListView listview;

    private Activity aty;
    private KJBitmap kjb;
    private int count = 20;

    @Override
    protected View inflaterView(LayoutInflater inflater,
            ViewGroup container, Bundle bundle) {
        aty = getActivity();
        kjb = KJBitmap.create();
        kjb.config.width = 520;
        kjb.config.height = (int) (520 * 0.45);
        kjb.config.loadingBitmap = BitmapCreate.bitmapFromResource(
                getResources(), R.drawable.ic_launcher,
                kjb.config.width, kjb.config.height);
        return inflater.inflate(R.layout.listview, null);
    }

    @Override
    protected void initWidget(View parentView) {
        super.initWidget(parentView);
        final ListviewAdapter adapter = new ListviewAdapter();
        listview.setAdapter(adapter);
        listview.getHeadView().setBackgroundResource(R.drawable.bg);
        // 上拉刷新需要手动开启，下拉刷新默认开启
        listview.setPullLoadEnable(true);
        listview.setKJListViewListener(new KJListViewListener() {
            @Override
            public void onRefresh() { // 下拉刷新完成
                count += 5;
                listview.stopRefreshData();
                adapter.refresh();
            }

            @Override
            public void onLoadMore() { // 上拉刷新完成
                count += 5;
                listview.stopRefreshData();
                adapter.refresh();
            }
        });
    }

    private class ListviewAdapter extends BaseAdapter {

        private ImageView image;

        @Override
        public int getCount() {
            return count;
        }

        public void refresh() {
            // notifyDataSetChanged();
            notifyDataSetInvalidated();
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
        public View getView(int position, View convertView,
                ViewGroup parent) {
            if (convertView == null) {
                convertView = image = new ImageView(aty);
                AbsListView.LayoutParams params = new AbsListView.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                params.width = kjb.config.width;
                params.height = kjb.config.height;
                image.setLayoutParams(params);
            } else {
                image = (ImageView) convertView;
            }
            // kjb.display(image, "/storage/sdcard0/1.png");
            if (position % 2 == 0) {
                // 5M的图片
                kjb.display(
                        image,
                        "https://raw.githubusercontent.com/kymjs/KJFrameForAndroid/master/KJFrameExample/big_image.png");
            } else {
                // 2M的图片
                kjb.display(
                        image,
                        "https://raw.githubusercontent.com/kymjs/KJFrameForAndroid/master/KJFrameExample/big_image2.jpg");
            }
            return convertView;
        }
    }
}
