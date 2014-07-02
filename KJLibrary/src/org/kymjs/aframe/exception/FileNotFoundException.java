package org.kymjs.aframe.exception;

public class FileNotFoundException extends KJException {
    private static final long serialVersionUID = 1L;

    public FileNotFoundException() {
        super();
    }

    public FileNotFoundException(String msg) {
        super(msg);
    }

    public FileNotFoundException(Throwable ex) {
        super(ex);
    }

    public FileNotFoundException(String msg, Throwable ex) {
        super(msg, ex);
    }
}
