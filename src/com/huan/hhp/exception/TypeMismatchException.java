package com.huan.hhp.exception;

/**
 * Created by Administrator on 2016/10/21.
 */
public class TypeMismatchException extends Exception {
    private Throwable e;
    public TypeMismatchException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
        this.e = throwable;
    }

    public Throwable getE() {
        return e;
    }
}
