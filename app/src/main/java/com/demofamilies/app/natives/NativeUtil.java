package com.demofamilies.app.natives;

public class NativeUtil
{
    static
    {
        System.loadLibrary("native-lib");
    }

    public native static String stringFromJNI();
}