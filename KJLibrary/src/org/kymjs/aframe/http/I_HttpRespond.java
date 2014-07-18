package org.kymjs.aframe.http;

public interface I_HttpRespond {
    boolean progress = false;

    void loading(long count, long current);

    void success(Object t);

    void failure(Throwable t, int errorNo, String strMsg);
}
