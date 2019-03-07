//
// Created by 张潮州 on 2019/3/7.
//

#include "me_chaozhouzhang_ndklearning_NdkJni.h"
JNIEXPORT jstring JNICALL Java_me_chaozhouzhang_ndklearning_NdkJni_callNative(JNIEnv *env,jobject thiz){



    jclass  jclass1 = (*env)->GetObjectClass(env,thiz);
    jclass jclass2 = (jclass)(*env)->NewGlobalRef(env,jclass1);
    jobject  jobject1 = (jobject)(*env)->NewGlobalRef(env,thiz);
    jmethodID jmethodID1 = (*env)->GetMethodID(env,jclass2,"callJava","(I)V");

    (*env)->CallVoidMethod(env,jobject1,jmethodID1,100);


    return (*env)->NewStringUTF(env,"Hello NDK JNI");
}

JNIEXPORT void JNICALL
Java_me_chaozhouzhang_ndklearning_NdkJni_callNativeInitialPerson(JNIEnv *env, jobject instance,
                                                                 jobject person) {

    // TODO

}