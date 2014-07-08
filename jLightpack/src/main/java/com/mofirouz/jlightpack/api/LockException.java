package com.mofirouz.jlightpack.api;

import java.io.IOException;

public class LockException extends IOException {
    public LockException() {}
    public LockException(Exception e) {super(e);}

}
