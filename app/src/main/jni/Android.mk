LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_LDLIBS :=-llog

LOCAL_MODULE    := http-parse
LOCAL_SRC_FILES := \
          terminal_spectre_com_terminalsystem_http_HttpParser.c \
          http_parser.c

include $(BUILD_SHARED_LIBRARY)