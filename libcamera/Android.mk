LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

# HAL module implemenation stored in
# hw/<COPYPIX_HARDWARE_MODULE_ID>.<ro.product.board>.so
LOCAL_MODULE_PATH := $(TARGET_OUT_SHARED_LIBRARIES)/hw

LOCAL_C_INCLUDES += $(LOCAL_PATH)/../include
LOCAL_C_INCLUDES += $(LOCAL_PATH)/../libs5pjpeg

LOCAL_SRC_FILES:= \
	SecCamera.cpp \
	SecCameraHWInterface.cpp \
	SecCameraUtils.cpp \

LOCAL_SHARED_LIBRARIES:= libutils libcutils libbinder liblog libcamera_client libhardware
LOCAL_SHARED_LIBRARIES+= libs5pjpeg

LOCAL_MODULE := camera.exynos4

LOCAL_MODULE_TAGS := optional

LOCAL_CFLAGS += -DFFC_PRESENT
LOCAL_CFLAGS += -DHAVE_FLASH

include $(BUILD_SHARED_LIBRARY)

