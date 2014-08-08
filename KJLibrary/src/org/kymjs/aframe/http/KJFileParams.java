package org.kymjs.aframe.http;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import org.kymjs.aframe.KJException;

public class KJFileParams implements I_HttpParams {
    private ConcurrentHashMap<String, String> urlParams;
    public ArrayList<InputStream> fileParams;

    private void init(int i) {
        urlParams = new ConcurrentHashMap<String, String>(6);
        fileParams = new ArrayList<InputStream>(i);
    }

    private void init() {
        init(3);
    }

    public KJFileParams() {
        init();
    }

    public KJFileParams(int i) {
        init(i);
    }

    public void put(String key, String value) {
        if (key != null && value != null) {
            urlParams.put(key, value);
        } else {
            throw new KJException("key or value is NULL");
        }
    }

    public void put(byte[] file) {
        put(new ByteArrayInputStream(file));
    }

    public void put(File file) throws FileNotFoundException {
        put(new FileInputStream(file));
    }

    public void put(InputStream value) {
        if (value != null) {
            fileParams.add(value);
        } else {
            throw new KJException("key or value is NULL");
        }
    }
}
