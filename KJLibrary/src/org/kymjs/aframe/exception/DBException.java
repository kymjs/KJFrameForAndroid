package org.kymjs.aframe.exception;

/**
 * DataBase Exception
 * 
 * @author zhangtao
 */
public class DBException extends KJException {
    private static final long serialVersionUID = 1L;

    public DBException() {
        super();
    }

    public DBException(String msg) {
        super(msg);
    }

    public DBException(Throwable ex) {
        super(ex);
    }

    public DBException(String msg, Throwable ex) {
        super(msg, ex);
    }
}
