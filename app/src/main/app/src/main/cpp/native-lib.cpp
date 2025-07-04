#include <jni.h>

extern "C" JNIEXPORT jstring JNICALL
Java_com_example_stockscreener_util_ApiKeyProvider_getApiKey(JNIEnv *env, jobject /* this */) {
    return env->NewStringUTF("SPQW6R3LUGH0LK27");
}
