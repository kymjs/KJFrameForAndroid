package org.kymjs.example.fragment;

import java.io.File;
import java.util.ArrayList;

import org.kymjs.aframe.plugin.CJTool;
import org.kymjs.aframe.ui.BindView;
import org.kymjs.aframe.ui.ViewInject;
import org.kymjs.aframe.ui.fragment.BaseFragment;
import org.kymjs.aframe.utils.FileUtils;
import org.kymjs.example.R;
import org.kymjs.example.bean.PluginBean;

import android.app.Activity;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class PluginSkinExample extends BaseFragment {

    @BindView(id = R.id.skin)
    private ImageView skin;
    @BindView(id = R.id.list)
    private ListView list;
    @BindView(id = R.id.skin_text)
    private TextView text;

    private Activity aty;
    private ArrayList<PluginBean> datas;

    @Override
    protected View inflaterView(LayoutInflater inflater,
            ViewGroup container, Bundle bundle) {
        aty = getActivity();
        return inflater.inflate(R.layout.plugin_skin, null);
    }

    @Override
    protected void initData() {
        super.initData();
        final File folder = FileUtils.getSaveFolder("KJFrameSkin");
        String[] filesPath = folder.list();
        datas = new ArrayList<PluginBean>();
        for (int i = 0; i < filesPath.length; i++) {
            String apkPath = folder.getAbsolutePath()
                    + File.separator + filesPath[i];
            PluginBean data = new PluginBean();
            try {
                data.setIcon(CJTool.getAppIcon(aty, apkPath));
                data.setName(CJTool.getAppName(aty, apkPath)
                        .toString());
                data.setPath(apkPath);
                datas.add(data);// 如果出错就不添加了
            } catch (NameNotFoundException e) {
            }
        }
    }

    @Override
    protected void initWidget(View parentView) {
        super.initWidget(parentView);
        if (!datas.isEmpty()) {
            text.setVisibility(View.GONE);
        }
        list.setAdapter(new PluginAdapter());
        list.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                try {
                    // 调用插件资源中名为的R.drawable.bg的图片
                    // 唯一需要注意的就是插件项目的manifest标签下加入android:sharedUserId="org.kymjs.aframe.plugin"
                    // （这里就不给出项目了，因为本就没有代码可言）
                    skin.setImageDrawable(CJTool.getResFromPkgName(
                            aty, "com.example.hello").getDrawable(
                            R.drawable.bg));
                } catch (NameNotFoundException e) {
                    e.printStackTrace();
                    ViewInject.toast("出错啦，找不到皮肤");
                    // 还原为默认皮肤
                    skin.setImageDrawable(getResources().getDrawable(
                            R.drawable.bg));
                }
            }
        });
    }

    private class PluginAdapter extends BaseAdapter {

        @Override
        public int getCount() {
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
