#include <jni.h>
#include "http_parser.h"
#include <stdlib.h>
#include <string.h>
#include  <android/log.h>

#define  TAG    "JNILOG"
// 定义info信息
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,TAG,__VA_ARGS__)
// 定义debug信息
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, TAG, __VA_ARGS__)
// 定义error信息
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,TAG,__VA_ARGS__)

static http_parser_settings settings_null =
 {.on_message_begin = 0
 ,.on_header_field = 0
 ,.on_header_value = 0
 ,.on_url = 0
 ,.on_status = 0
 ,.on_body = 0
 ,.on_headers_complete = 0
 ,.on_message_complete = 0
 ,.on_chunk_header = 0
 ,.on_chunk_complete = 0
 };

struct http_request_info {
    char *method;//请求方法
    char *url;//请求url
    char *protocol;//协议版本
    char *headers;//请求头
};

struct http_request_info info;

int my_url_callback(http_parser* parser, const char *at, size_t length) {
    char *temp;
    strncpy(temp,at,length);
    LOGI("temp=%s",temp);
    strcat(info.url,temp);
    LOGI("info.url",info.url);
    return 0;
}
int my_header_field_callback(http_parser* parser, const char *at, size_t length) {
    char temp[length];
    strncpy(&temp,at,length);
    LOGI("temp=%s",temp);
    strcat(info.headers,&temp);
    LOGI("info.headers",info.headers);
    return 0;
}

JNIEXPORT jstring JNICALL Java_terminal_spectre_com_terminalsystem_http_HttpParser_parseHttpRequest
  (JNIEnv *env, jobject obj, jstring  string){

      char *buf = (*env)->GetStringUTFChars(env, string, NULL);

      http_parser parser;
      http_parser_init(&parser, HTTP_REQUEST);//初始化parser

      settings_null.on_url = my_url_callback;
      settings_null.on_header_value = my_header_field_callback;

      size_t parsed;
      parsed = http_parser_execute(&parser, &settings_null, buf, strlen(buf));
      jstring  j_url = (*env)->NewStringUTF(env,info.url);

      return j_url;
  }
