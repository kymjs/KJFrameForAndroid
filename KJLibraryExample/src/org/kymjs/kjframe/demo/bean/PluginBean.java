package org.kymjs.kjframe.demo.bean;

import android.graphics.drawable.Drawable;

public class PluginBean {

    private String name;
    private Drawable icon;
    private String path;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
