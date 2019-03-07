//
// Created by 张潮州 on 2019/3/7.
//

#include "me_chaozhouzhang_ndklearning_NdkJni.h"
JNIEXPORT jstring JNICALL Java_me_chaozhouzhang_ndklearning_NdkJni_callNative(JNIEnv *env,jobject thiz){

    return (*env)->NewStringUTF(env,"Hello NDK JNI");
}