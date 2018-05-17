#include <jni.h>
#include <string>

extern "C"
JNIEXPORT jstring JNICALL
Java_com_demofamilies_app_natives_NativeUtil_stringFromJNI(JNIEnv *env, jclass type) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}