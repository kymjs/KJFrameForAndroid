package org.kymjs.aframe.http;

public abstract class StringCallBack implements I_HttpRespond {
    private boolean progress = false;

    public boolean isProgress() {
        return progress;
    }

    public void setProgress(boolean open) {
        progress = open;
    }

    abstract public void onSuccess(String json);

    @Override
    public void onLoading(long count, long current) {}

    @Override
    public void onFailure(Throwable t, int errorNo, String strMsg) {}

    @Override
    public void onSuccess(Object t) {
        onSuccess(t.toString());
    }
}
