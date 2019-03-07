# 编译、链接
LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE    := NdkJni
LOCAL_SRC_FILES := me_chaozhouzhang_ndklearning_NdkJni.c

include $(BUILD_SHARED_LIBRARY)