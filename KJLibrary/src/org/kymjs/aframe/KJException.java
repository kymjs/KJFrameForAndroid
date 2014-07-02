package org.kymjs.aframe;

/**
 * KJLibrary's base exception class
 * 
 * @author zhangtao
 */
public class KJException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public KJException() {
        super();
    }

    public KJException(String msg) {
        super(msg);
    }

    public KJException(Throwable ex) {
        super(ex);
    }

    public KJException(String msg, Throwable ex) {
        super(msg, ex);
    }
}
