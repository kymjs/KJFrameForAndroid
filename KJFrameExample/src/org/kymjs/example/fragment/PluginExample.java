package org.kymjs.example.fragment;

import java.util.ArrayList;

import org.kymjs.aframe.plugin.CJTool;
import org.kymjs.aframe.plugin.activity.CJActivityUtils;
import org.kymjs.aframe.ui.BindView;
import org.kymjs.aframe.ui.fragment.BaseFragment;
import org.kymjs.example.R;
import org.kymjs.example.bean.PluginBean;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 插件项目演示
 */
public class PluginExample extends BaseFragment {
    @BindView(id = R.id.plugin)
    private TextView mTvNoPlugin;
    @BindView(id = R.id.plugin_list)
    private ListView mList;

    private ArrayList<PluginBean> datas;
    private String apkPath;
    private Activity aty;

    @Override
    protected View inflaterView(LayoutInflater inflater,
            ViewGroup container, Bundle bundle) {
        return inflater.inflate(R.layout.frag_plugin, null);
    }

    @Override
    protected void initData() {
        super.initData();
        aty = getActivity();
        apkPath = Environment.getExternalStorageDirectory()
                + "/PluginExample.apk";
        datas = new ArrayList<PluginBean>();

        try {
            CharSequence name = CJTool.getAppName(aty, apkPath);
            Drawable icon = CJTool.getAppIcon(aty, apkPath);
            PluginBean bean = new PluginBean();
            bean.setName(name.toString());
            bean.setIcon(icon);
            datas.add(bean);
        } catch (Exception e) {
        }
    }

    @Override
    protected void initWidget(View parentView) {
        super.initWidget(parentView);
        mList.setAdapter(new PluginAdapter());
        mList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                CJActivityUtils.launchPlugin(aty, apkPath);
            }
        });
    }

    /*********************************/
    private class PluginAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (datas.size() != 0) {
                mTvNoPlugin.setVisibility(View.GONE);
            }
            return datas.size();
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
                v = View.inflate(aty, R.layout.plugin_item, null);
                holder = new ViewHolder();
                holder.img = (ImageView) v.findViewById(R.id.image);
                holder.name = (TextView) v
                        .findViewById(R.id.text_name);
                v.setTag(holder);
            } else {
                holder = (ViewHolder) v.getTag();
            }
            holder.name.setText(datas.get(position).getName());
            holder.img
                    .setImageDrawable(datas.get(position).getIcon());
            return v;
        }
    }

    static class ViewHolder {
        ImageView img;
        TextView name;
    }
}
