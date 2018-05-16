#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring JNICALL
Java_com_xujiaji_exercises_natives_NativeUtil_stringFromJNI(JNIEnv *env, jobject) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}