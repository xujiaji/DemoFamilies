package com.xujiaji.exercises.natives;

public class NativeUtil
{
    static
    {
        System.loadLibrary("native-lib");
    }

    public native static String stringFromJNI();
}