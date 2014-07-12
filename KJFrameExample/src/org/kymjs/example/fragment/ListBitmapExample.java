package org.kymjs.example.fragment;

import org.kymjs.aframe.bitmap.KJBitmap;
import org.kymjs.aframe.bitmap.utils.BitmapCreate;
import org.kymjs.aframe.ui.BindView;
import org.kymjs.aframe.ui.fragment.BaseFragment;
import org.kymjs.example.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;

public class ListBitmapExample extends BaseFragment {
    @BindView(id = R.id.listview)
    private ListView listview;

    private Activity aty;
    private KJBitmap kjb;

    @Override
    protected View inflaterView(LayoutInflater inflater, ViewGroup container,
            Bundle bundle) {
        aty = getActivity();
        kjb = new KJBitmap(aty);
        KJBitmap.config.loadingBitmap = BitmapCreate.bitmapFromResource(
                getResources(), R.drawable.ic_launcher, 400, 400);
        return inflater.inflate(R.layout.listview, null);
    }

    @Override
    protected void initWidget(View parentView) {
        super.initWidget(parentView);
        listview.setAdapter(new ListviewAdapter());
    }

    class ListviewAdapter extends BaseAdapter {

        ImageView image;

        @Override
        public int getCount() {
            return 30;
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
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = image = new ImageView(aty);
            } else {
                image = (ImageView) convertView;
            }
            kjb.display(image, "/storage/sdcard0/1.jpg");
            // kjb.display(image,
            // "http://173.194.72.31/images/srpr/logo11w.png");
            return convertView;
        }
    }
}
