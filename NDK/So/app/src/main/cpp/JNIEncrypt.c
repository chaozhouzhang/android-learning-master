#include <jni.h>
#include "aes.h"
#include "checksignature.h"
#include "check_emulator.h"
#include <string.h>
#include <malloc.h>
#include "string_interception.h"
#include "logger.h"


#define CBC 1
#define ECB 1


//CRYPT CONFIG
#define MAX_LEN (2*1024*1024)
#define ENCRYPT 0
#define DECRYPT 1
#define AES_KEY_SIZE 256
#define READ_LEN 10

/**
 * 获取数组的数量大小
 */
# define NELEM(x) ((int) (sizeof(x) / sizeof((x)[0])))




/**
 * 指定要注册的类，对应完整的java类名
 */
#define JNIREG_CLASS "cn/bmob/v3/helper/BmobNative"

const char *UN_SIGN_NATURE = "UN_SIGN_NATURE";


/**
 * 字符转字符串
 * @param envPtr JNI环境
 * @param src 源字符
 * @return
 */
jstring charToJstring(JNIEnv *envPtr, char *src) {
    JNIEnv env = *envPtr;

    jsize len = strlen(src);
    /**
     * String类的Java类
     */
    jclass classString = env->FindClass(envPtr, "java/lang/String");
    /**
     * 加密的字符格式
     */
    jstring strEncode = env->NewStringUTF(envPtr, "UTF-8");
    /**
     * 初始化的方法id
     */
    jmethodID mid = env->GetMethodID(envPtr, classString, "<init>", "([BLjava/lang/String;)V");
    /**
     * byte数组
     */
    jbyteArray barr = env->NewByteArray(envPtr, len);
    /**
     * 设置byte数组的临界区域
     */
    env->SetByteArrayRegion(envPtr, barr, 0, len, (jbyte *) src);

    /**
     * 新建String对象
     */
    return (jstring) env->NewObject(envPtr, classString, mid, barr, strEncode);
}

///**
// * 获取加解密的密钥
// * @return
// */
//char *getKey() {
//    int n = 0;
//    //"NMTIzNDU2Nzg5MGFiY2RlZg";
//    char s[23];
//    s[n++] = 'N';
//    s[n++] = 'M';
//    s[n++] = 'T';
//    s[n++] = 'I';
//    s[n++] = 'z';
//    s[n++] = 'N';
//    s[n++] = 'D';
//    s[n++] = 'U';
//    s[n++] = '2';
//    s[n++] = 'N';
//    s[n++] = 'z';
//    s[n++] = 'g';
//    s[n++] = '5';
//    s[n++] = 'M';
//    s[n++] = 'G';
//    s[n++] = 'F';
//    s[n++] = 'i';
//    s[n++] = 'Y';
//    s[n++] = '2';
//    s[n++] = 'R';
//    s[n++] = 'l';
//    s[n++] = 'Z';
//    s[n++] = 'g';
//    char *encode_str = s + 1;
//    return b64_decode(encode_str, strlen(encode_str));
//
//}
JNIEXPORT jbyteArray JNICALL android_native_aes(JNIEnv *env, jobject instance,
                                                unsigned char *data, jint jmode,
                                                unsigned char *AES_KEY,
                                                unsigned char *AES_IV) {


    unsigned int len = sizeof(data);
    //check input data
//    unsigned int len = (unsigned int) ((*env)->GetArrayLength(env, jarray));
//    if (len <= 0 || len >= MAX_LEN) {
//        return NULL;
//    }

//    unsigned char *data = (unsigned char *) (*env)->GetByteArrayElements(env,
//                                                                         jarray, NULL);
//    if (!data) {
//        return NULL;
//    }


    //计算填充长度，当为加密方式且长度不为16的整数倍时，则填充，与3DES填充类似(DESede/CBC/PKCS5Padding)
    unsigned int mode = (unsigned int) jmode;
    unsigned int rest_len = len % AES_BLOCK_SIZE;
    unsigned int padding_len = (
            (ENCRYPT == mode) ? (AES_BLOCK_SIZE - rest_len) : 0);
    unsigned int src_len = len + padding_len;

    //设置输入
    unsigned char *input = (unsigned char *) malloc(src_len);
    memset(input, 0, src_len);
    memcpy(input, data, len);
    if (padding_len > 0) {
        memset(input + len, (unsigned char) padding_len, padding_len);
    }
    //data不再使用
//    (*env)->ReleaseByteArrayElements(env, jarray, data, 0);

    //设置输出Buffer
    unsigned char *buff = (unsigned char *) malloc(src_len);
    if (!buff) {
        free(input);
        return NULL;
    }
    memset(buff, src_len, 0);

    //set key & iv
    unsigned int key_schedule[AES_BLOCK_SIZE * 4] = {0}; //>=53(这里取64)
    aes_key_setup(AES_KEY, key_schedule, AES_KEY_SIZE);

    //执行加解密计算(CBC mode)
    if (mode == ENCRYPT) {
        aes_encrypt_cbc(input, src_len, buff, key_schedule, AES_KEY_SIZE,
                        AES_IV);
    } else {
        aes_decrypt_cbc(input, src_len, buff, key_schedule, AES_KEY_SIZE,
                        AES_IV);
    }

    //解密时计算填充长度
    if (ENCRYPT != mode) {
        unsigned char *ptr = buff;
        ptr += (src_len - 1);
        padding_len = (unsigned int) *ptr;
        if (padding_len > 0 && padding_len <= AES_BLOCK_SIZE) {
            src_len -= padding_len;
        }
        ptr = NULL;
    }

    //设置返回变量
    jbyteArray bytes = (*env)->NewByteArray(env, src_len);
    (*env)->SetByteArrayRegion(env, bytes, 0, src_len, (jbyte *) buff);

    //内存释放
    free(input);
    free(buff);

    return bytes;
}
/**
 * 加密
 * @param env
 * @param instance
 * @param context
 * @param str_
 * @return
 */
JNIEXPORT jstring JNICALL encode(JNIEnv *env, jobject instance, jstring str_, char *key, char *iv) {

    /**
     * 先进行apk被二次打包的校验：检验签名和是否是虚拟器
     */
//    if (check_signature(env, instance, context) != 1 || check_is_emulator(env) != 1) {
//        char *str = UN_SIGN_NATURE;
//        return charToJstring(env, str);
//    }

//    uint8_t *AES_KEY = (uint8_t *) getKey();
//    uint8_t *AES_KEY = (uint8_t *) key;
//    const char *in = (*env)->GetStringUTFChars(env, str_, JNI_FALSE);
//
//    uint8_t *IN = (uint8_t *) in;
//    char *baseResult = AES_128_ECB_PKCS5Padding_Encrypt(in, AES_KEY);

//    char *baseResult = AES_128_ECB_PKCS5Padding_Encrypt(IN, AES_KEY);


    char *str = (*env)->GetStringUTFChars(env, str_, JNI_FALSE);


    jbyteArray jbyteArray1 = android_native_aes(env, instance, str, ENCRYPT, key, iv);

    char *szStr = NULL;

    szStr = (char *) (*env)->GetByteArrayElements(env, jbyteArray1, NULL);
    //然后去用szStr吧，就是对jbyteArray szLics的使用
    (*env)->ReleaseByteArrayElements(env, jbyteArray1, szStr, 0);
//    (*env)->ReleaseStringUTFChars(env, str_, in);
    return (*env)->NewStringUTF(env, szStr);
}


/**
 * 解密
 * @param env
 * @param instance
 * @param context
 * @param str_
 * @return
 */
JNIEXPORT jstring JNICALL
decode(JNIEnv *env, jobject instance, jstring str_, jstring key_, jstring iv_) {

    /**
     *先进行apk被二次打包的校验
     */
//    if (check_signature(env, instance, context) != 1 || check_is_emulator(env) != 1) {
//        char *str = UN_SIGN_NATURE;
//        return charToJstring(env, str);
//    }

//    uint8_t *AES_KEY = (uint8_t *) getKey();
    const char *str = (*env)->GetStringUTFChars(env, str_, JNI_FALSE);

    const char *key = (*env)->GetStringUTFChars(env, key_, JNI_FALSE);

    const char *iv = (*env)->GetStringUTFChars(env, iv_, JNI_FALSE);

    jbyteArray jbyteArray1 = android_native_aes(env, instance, str, DECRYPT, key, iv);
//    char *desResult = android_native_aes(env,instance,str, AES_KEY);
    (*env)->ReleaseStringUTFChars(env, str_, str);
    /**
     * 不用系统自带的方法NewStringUTF是因为如果desResult是乱码，会抛出异常
     * return (*env)->NewStringUTF(env, desResult);
     */

    char *szStr = NULL;

    szStr = (char *) (*env)->GetByteArrayElements(env, jbyteArray1, NULL);
    //然后去用szStr吧，就是对jbyteArray szLics的使用
    (*env)->ReleaseByteArrayElements(env, jbyteArray1, szStr, 0);
    return charToJstring(env, szStr);
}




/**
 * if return 1 ,is check pass.
 */
JNIEXPORT jint JNICALL
check_jni(JNIEnv *env, jobject instance, jobject con) {
    return check_signature(env, instance, con);
}


jstring mAppid = NULL;
jstring mSecretKey = NULL;
jstring mInternal = NULL;

/**
 * 初始化appid
 * @param env
 * @param instance
 * @param context
 * @param appid
 * @return
 */
JNIEXPORT jboolean JNICALL init(JNIEnv *env, jobject instance, jobject context, jstring appid) {
    char *id = (char *) (*env)->GetStringUTFChars(env, appid, JNI_FALSE);
    LOGI("%s", id);
    mAppid = (*env)->NewGlobalRef(env, appid);
    return JNI_TRUE;
}

/**
 * 返回init保存的appid
 * @param env
 * @param instance
 * @return
 */
JNIEXPORT jstring JNICALL getAppId(JNIEnv *env, jobject instance) {
    return mAppid;
}

/**
 * 保存secret key
 * @param env
 * @param instance
 * @param secretKey
 */
JNIEXPORT void JNICALL saveKey(JNIEnv *env, jobject instance, jstring secretKey) {
    char *key = (char *) (*env)->GetStringUTFChars(env, secretKey, JNI_FALSE);
    LOGI("%s", key);
    mSecretKey = (*env)->NewGlobalRef(env, secretKey);
}



/**
 *
 * @param env
 * @param instance
 * @return
 */
JNIEXPORT jstring JNICALL getSecretKey(JNIEnv *env, jobject instance) {
    return mSecretKey;
}



/**
 * 判断是否存在secret key
 * @param env
 * @param instance
 * @return
 */
JNIEXPORT jboolean JNICALL hasKey(JNIEnv *env, jobject instance) {
    if (mSecretKey == NULL) {
        return JNI_FALSE;
    }
    return JNI_TRUE;
}

/**
 * 保存
 * @param env
 * @param instance
 * @param time
 */
JNIEXPORT void  JNICALL saveInterval(JNIEnv *env, jobject instance, jstring time) {
    char *ti = (char *) (*env)->GetStringUTFChars(env, time, JNI_FALSE);
    LOGI("%s", ti);
    mInternal = (*env)->NewGlobalRef(env, time);
}


/**
 *
 * @param env
 * @param instance
 * @return
 */
JNIEXPORT jstring JNICALL getInterval(JNIEnv *env, jobject instance) {
    return mInternal;
}


/**
 *
 * @param env
 * @param instance
 * @return
 */
JNIEXPORT jstring JNICALL getAcceptId(JNIEnv *env, jobject instance) {

}


/**
 *
 * @param env
 * @param instance
 * @param agent
 * @param source
 * @return
 */
JNIEXPORT jstring JNICALL encrypt(JNIEnv *env, jobject instance, jstring agent, jstring source) {
//    String key1 = agent.substring(agent.length() - 16)



    const char *age = (*env)->GetStringUTFChars(env, agent, JNI_FALSE);

    LOGI("%s", age);

    char *key = NULL;
    key = right(key, age, 16);

    LOGI("%s", key);

    encode(env, instance, source, key, key);


    

}


/**
 *
 * @param env
 * @param instance
 * @param responseId
 * @param source
 * @return
 */
JNIEXPORT jstring JNICALL
decrypt(JNIEnv *env, jobject instance, jstring responseId, jstring source) {

}


/**
 *
 * @param env
 * @param instance
 * @param source
 * @return
 */
JNIEXPORT jstring JNICALL encryptByKey(JNIEnv *env, jobject instance, jstring source) {

}

/**
 *
 * @param env
 * @param instance
 * @param source
 * @return
 */
JNIEXPORT jstring JNICALL decryptByKey(JNIEnv *env, jobject instance, jstring source) {

}

/**
 * 清除存储的全局数据
 * @param env
 * @param instance
 */
JNIEXPORT void JNICALL clear(JNIEnv *env, jobject instance) {
    mAppid = NULL;
    mSecretKey = NULL;
    mInternal = NULL;
}

/**
 * Java和JNI函数的绑定表
 *
 *
 * public static final native boolean init(Context context, String appid);
 * public static final native String getAppId();
 * public static String SECRET_KEY = "";
 * public static final native void saveKey(String key);
 * public static final native String getSecretKey();
 * public static final native boolean hasKey();
 * public static final native void saveInterval(String time);
 * public static final native String getInterval();
 * public static final native String getAcceptId();
 * public static final native String encrypt(String agent, String source);
 * public static final native String decrypt(String responseId, String source);
 * public static final native String encryptByKey(String source);
 * public static final native String decryptByKey(String source);
 * public static final native void clear();
 */
static JNINativeMethod method_table[] = {
        {"init",         "(Landroid/content/Context;Ljava/lang/String;)Z",           (void *) init},
        {"getAppId",     "()Ljava/lang/String;",                                     (void *) getAppId},
        {"saveKey",      "(Ljava/lang/String;)V",                                    (void *) saveKey},
        {"getSecretKey", "()Ljava/lang/String;",                                     (void *) getSecretKey},
        {"hasKey",       "()Z",                                                      (void *) hasKey},
        {"saveInterval", "(Ljava/lang/String;)V",                                    (void *) saveInterval},
        {"getInterval",  "()Ljava/lang/String;",                                     (void *) getInterval},
        {"getAcceptId",  "()Ljava/lang/String;",                                     (void *) getAcceptId},
        {"encrypt",      "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;", (void *) encrypt},
        {"decrypt",      "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;", (void *) decrypt},
        {"encryptByKey", "(Ljava/lang/String;)Ljava/lang/String;",                   (void *) encryptByKey},
        {"decryptByKey", "(Ljava/lang/String;)Ljava/lang/String;",                   (void *) decryptByKey},
        {"clear",        "()V",                                                      (void *) clear},
//        {"checkSignature", "(Ljava/lang/Object;)I",                                    (void *) check_jni},
//        {"decode",         "(Ljava/lang/Object;Ljava/lang/String;)Ljava/lang/String;", (void *) decode},
//        {"encode",         "(Ljava/lang/Object;Ljava/lang/String;)Ljava/lang/String;", (void *) encode},
};

/**
 * 注册native方法到java中
 * @param env jni环境
 * @param className 类名
 * @param gMethods 方法列表
 * @param numMethods 方法数
 * @return
 */
static int registerNativeMethods(JNIEnv *env, const char *className, JNINativeMethod *gMethods,
                                 int numMethods) {
    /**
     * 初始化
     */
    jclass clazz;
    /**
     * 通过JNI环境和类名获得Java类
     */
    clazz = (*env)->FindClass(env, className);
    if (clazz == NULL) {
        return JNI_FALSE;
    }
    /**
     * 通过JNI环境、Java类、方法列表、方法数进行本地方法的注册
     */
    if ((*env)->RegisterNatives(env, clazz, gMethods, numMethods) < 0) {
        return JNI_FALSE;
    }

    return JNI_TRUE;
}



/**
 *
 * JNI加载入口
 *
 * @param vm Java虚拟机
 * @param reserved 保留的，预订的
 * @return
 */
JNIEXPORT jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    /**
     * 初始化
     */
    JNIEnv *env = NULL;
    jint result = -1;



    /**
     * 从JavaVM获取JNIEnv，一般使用1.4的版本
     */
    if ((*vm)->GetEnv(vm, (void **) &env, JNI_VERSION_1_4) != JNI_OK) {
        return result;
    }

    /**
     * 注册本地方法，传入JNI环境，类名，方法列表，方法数量
     */
    registerNativeMethods(env, JNIREG_CLASS,
                          method_table, NELEM(method_table));
    /**
     * 返回jni的版本
     */
    return JNI_VERSION_1_4;
}

