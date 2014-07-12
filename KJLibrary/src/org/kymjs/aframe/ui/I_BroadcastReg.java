package org.kymjs.aframe.ui;

/**
 * 规范Activity中广播接受者注册的接口协议
 * 
 * @author kymjs
 */
public interface I_BroadcastReg {
    void registerBroadcast();
    
    void unRegisterBroadcast();
}
