package org.kymjs.kjframe.demo;

import java.io.File;
import java.util.ArrayList;

import org.kymjs.kjframe.KJActivity;
import org.kymjs.kjframe.demo.bean.PluginBean;
import org.kymjs.kjframe.plugin.CJActivityUtils;
import org.kymjs.kjframe.plugin.CJTool;
import org.kymjs.kjframe.ui.BindView;
import org.kymjs.kjframe.utils.FileUtils;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class PluginActivity extends KJActivity {
    @BindView(id = R.id.plugin)
    private TextView mTvNoPlugin;
    @BindView(id = R.id.plugin_list)
    private ListView mList;

    private ArrayList<PluginBean> datas;

    @Override
    public void setRootView() {
        setContentView(R.layout.frag_plugin);
    }

    @Override
    public void initData() {
        super.initData();
        datas = new ArrayList<PluginBean>();
        File folder = FileUtils.getSaveFolder("/KJLibrary/plugin");
        for (File file : folder.listFiles()) {
            if (file.getName().endsWith(".apk")) {
                try {
                    CharSequence name = CJTool.getAppName(aty,
                            file.getAbsolutePath());
                    Drawable icon = CJTool.getAppIcon(aty,
                            file.getAbsolutePath());
                    PluginBean bean = new PluginBean();
                    bean.setName(name.toString());
                    bean.setIcon(icon);
                    bean.setPath(file.getAbsolutePath());
                    datas.add(bean);
                } catch (Exception e) {
                }
            }
        }
    }

    @Override
    public void initWidget() {
        mList.setAdapter(new PluginAdapter());
        mList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                CJActivityUtils
                        .launchPlugin(aty, datas.get(position).getPath());
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
                holder.name = (TextView) v.findViewById(R.id.text_name);
                v.setTag(holder);
            } else {
                holder = (ViewHolder) v.getTag();
            }
            holder.name.setText(datas.get(position).getName());
            holder.img.setImageDrawable(datas.get(position).getIcon());
            return v;
        }
    }

    static class ViewHolder {
        ImageView img;
        TextView name;
    }
}
