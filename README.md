# android-learning-master
安卓重要细分领域学习总结


# NDK
```
谷歌开发者网站介绍：
https://developer.android.google.cn/ndk/guides/index.html

谷歌源码案例：
https://github.com/googlesamples/android-ndk
```

## Android NDK
```
Android Native Development Kit
Android本地开发包
```
## JNI
```
Java Native Interface
Java本地接口



在Java源文件中声明Native方法
通过 javac 编译 Java源文件（.class文件）
通过 javah 命令导出JNI的头文件（.h文件）
cd app/src/main/java
javah -d ../jni -jni me.chaozhouzhang.ndklearning.NdkJni
使用需要与Java交互的本地代码来实现在Java中声明的Native方法 
编译.so库文件
通过Java命令执行Java程序，最终实现Java调用本地代码
```
## NDK与JNI关系
```
通过NDK实现Java在Android中使用JNI与C、C++本地代码进行交互的目的。
```
## 编译方案
### NDK-Build
```
Android.mk
Application.mk
```
### CMake
```
CmakeLists.txt
```
## 基础
```
Java
C
C++
```
## ABI
```
Application Binary Interface
应用程序二进制接口
```
## 学习书籍
```
1、《细说Android 4.0 NDK编程》
2、《Android C++高级编程：使用NDK》中文版
```
### 头文件

## 编写方案
```
1、包名类名方法名
2、JNI_OnLoad
```
# Framework

# 



# 错误

```
ERROR: ABIs [armeabi, mips, mips64] are not supported for platform. Supported ABIs are [arm64-v8a, armeabi-v7a, x86, x86_64].
```
解决
把NDK改为：android-ndk-r16b




