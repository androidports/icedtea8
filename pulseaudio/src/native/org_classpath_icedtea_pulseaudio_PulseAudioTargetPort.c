#include "org_classpath_icedtea_pulseaudio_PulseAudioTargetPort.h"

#include "jni-common.h"
#include <pulse/pulseaudio.h>
#include <string.h>

typedef struct java_context {
	JNIEnv* env;
	jobject obj;
} java_context;

extern JNIEnv* pulse_thread_env;

void sink_callback(pa_context *context, int success, void *userdata) {
	notifyWaitingOperations(pulse_thread_env);
}

void get_sink_volume_callback(pa_context *context, const pa_sink_info *i,
		int eol, void *userdata) {
	assert(context);
	assert(pulse_thread_env);

	if (eol == 0) {
		// printf("%s\n", i->name); 
		jobject obj = (jobject) userdata;
		assert(obj);
		jclass cls = (*pulse_thread_env)->GetObjectClass(pulse_thread_env, obj);
		assert(cls);
		jmethodID mid1 = (*pulse_thread_env)->GetMethodID(pulse_thread_env, cls,
				"update_channels_and_volume", "(IF)V");
		assert(mid1);
		(*pulse_thread_env)->CallVoidMethod(pulse_thread_env, obj, mid1,
				(int) (i->volume).channels, (float) (i->volume).values[0]) ;
	} else {
		notifyWaitingOperations(pulse_thread_env);
	}

}


/*
 * Class:     org_classpath_icedtea_pulseaudio_PulseAudioTargetPort
 * Method:    native_updateVolumeInfo
 * Signature: ()[B
 */
JNIEXPORT jbyteArray JNICALL Java_org_classpath_icedtea_pulseaudio_PulseAudioTargetPort_native_1updateVolumeInfo
(JNIEnv *env, jobject obj) {
	jclass cls = (*env)->GetObjectClass(env, obj);
	assert(cls);
	
	jfieldID fid = (*env)->GetFieldID(env, cls, "name", "Ljava/lang/String;");
	assert(fid);
	
	jstring jstr = (*env)->GetObjectField(env, obj, fid);
	assert(jstr);
	
	const char *name = (*env)->GetStringUTFChars(env, jstr, NULL);
	if (name == NULL) {
		return NULL;	// oome
	}
	
	pa_context* context = (pa_context*) getJavaPointer(env, obj, "contextPointer");
	assert(context);
	
	obj = (*env)->NewGlobalRef(env, obj);
	
	pa_operation *o = pa_context_get_sink_info_by_name (context, (char*) name, get_sink_volume_callback, obj);
	assert(o);
	
	return convertNativePointerToJava(env, o);
}

/*
 * Class:     org_classpath_icedtea_pulseaudio_PulseAudioTargetPort
 * Method:    native_setVolume
 * Signature: (F)[B
 */
JNIEXPORT jbyteArray JNICALL Java_org_classpath_icedtea_pulseaudio_PulseAudioTargetPort_native_1setVolume
(JNIEnv *env, jobject obj, jfloat value) {
	jclass cls = (*env)->GetObjectClass(env, obj);
	assert(cls);
	
	jfieldID fid = (*env)->GetFieldID(env, cls, "name", "Ljava/lang/String;");
	assert(fid);
	
	jstring jstr = (*env)->GetObjectField(env, obj, fid);
	assert(jstr);
	
	const char *name = (*env)->GetStringUTFChars(env, jstr, NULL);
	if (name == NULL) {
		return NULL;	// return oome
	}
	
	pa_context* context = (pa_context*) getJavaPointer(env, obj, "contextPointer");
	assert(context);
	
	obj = (*env)->NewGlobalRef(env, obj);
	fid = (*env)->GetFieldID(env, cls, "channels", "I");
	assert(fid);
	
	jint channels = (*env)->GetIntField(env, obj, fid);
	pa_cvolume cv;
	pa_operation *o = pa_context_set_sink_volume_by_name (context, (char*) name,pa_cvolume_set(&cv, channels, value), sink_callback, obj);
	assert(o);
	
	return convertNativePointerToJava(env, o);
}
