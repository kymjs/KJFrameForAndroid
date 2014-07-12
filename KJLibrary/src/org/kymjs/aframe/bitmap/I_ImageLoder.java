package org.kymjs.aframe.bitmap;

/**
 * 图片载入接口协议，可自定义实现此协议的下载器
 * 
 * @author kymjs(kymjs123@gmail.com)
 * @version 1.0
 * @created 2014-7-11
 */
public interface I_ImageLoder {
    public byte[] loadImage(String imageUrl);
}
