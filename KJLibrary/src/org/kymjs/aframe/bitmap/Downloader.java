package org.kymjs.aframe.bitmap;

import java.net.HttpURLConnection;
import java.net.URL;

import org.kymjs.aframe.utils.FileUtils;

/**
 * 图片下载器：可以从网络或本地加载一张Bitmap并返回
 * 
 * @author kymjs(kymjs123@gmail.com)
 * @version 1.0
 * @created 2014-7-11
 */
public class Downloader implements I_ImageLoder {

    @Override
    public byte[] loadImage(String imagePath) {
        return loadImgFromNet(imagePath);
    }

    /**
     * 从网络载入一张图片
     * 
     * @param imagePath
     *            图片的地址
     */
    private byte[] loadImgFromNet(String imagePath) {
        byte[] data = null;
        HttpURLConnection con = null;
        try {
            URL url = new URL(imagePath);
            con = (HttpURLConnection) url.openConnection();
            con.setConnectTimeout(KJBitmap.config.timeOut);
            con.setReadTimeout(KJBitmap.config.timeOut * 2);
            con.setRequestMethod("GET");
            con.setDoInput(true);
            con.connect();
            data = FileUtils.input2byte(con.getInputStream());
        } catch (Exception e) {
            KJBitmap.config.callBack.imgLoadFailure(imagePath, e.getMessage());
            e.printStackTrace();
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }
        return data;
    }
}
