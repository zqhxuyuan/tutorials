#include <jni.h>
#include "SetFactory.h"

#ifndef _Included_JNIUtils
#define _Included_JNIUtils


class JNIUtils
{  
public:
    static const char* strFromJString(JNIEnv* env, jstring element)
    {    
	    int length = env->GetStringUTFLength(element);
	    char* chars = new char[length+1];
	    env->GetStringUTFRegion(element, 0, length, chars);
	    chars[length] = '\0';
	    return chars;
    }

    static jstring strToJString(JNIEnv* env, const char* element)
    {
	    return env->NewStringUTF(element);
    }
};

#endif