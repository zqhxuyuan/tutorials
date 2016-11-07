#include <jni.h>
#include <stdio.h>
#include "JNIUtils.h"
#include "nimbus_nativestructs_CList.h"
#include <list>
#include <cstring>
#include <sstream>
#include <iostream>

struct ltstr
{
  bool operator()(const char* s1, const char* s2) const
  {
    return strcmp(s1, s2) < 0;
  }
};

std::list<const char*> list;
typedef std::list<const char*>::const_iterator listconstiter;
typedef std::list<const char*>::reverse_iterator listreverseiter;
typedef std::list<const char*>::iterator listiter;

listiter str_find(listiter start, listiter end, const char* value)
{
    while (start != end)
    {
        if (strcmp(*start, value) == 0)
        {
            return start;
        }
        ++start;
    }

    return end;
}

/*
 * Class:     nimbus_utils_CList
 * Method:    c_add
 * Signature: (Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_nimbus_utils_CList_c_1add__Ljava_lang_String_2(JNIEnv *env, jobject obj, jstring value)
{
    list.push_back(JNIUtils::strFromJString(env, value));
}

/*
 * Class:     nimbus_utils_CList
 * Method:    c_add
 * Signature: (ILjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_nimbus_utils_CList_c_1add__ILjava_lang_String_2(JNIEnv *env, jobject obj, jint index, jstring value)
{
    listiter iter = list.begin();
    for (int i = 0; i < index; ++i)
    {
        ++iter;
    }

    list.insert(iter, JNIUtils::strFromJString(env, value));
}

/*
 * Class:     nimbus_utils_CList
 * Method:    c_clear
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_nimbus_utils_CList_c_1clear(JNIEnv *env, jobject obj)
{
    listiter iter = list.begin();
	listiter end = list.end();
	const char* value = 0;
	while (iter != end)
	{
		value = *iter;
		delete [] value;
		value = 0;
		++iter;
	}

    list.clear();
}

/*
 * Class:     nimbus_utils_CList
 * Method:    c_contains
 * Signature: (Ljava/lang/Object;)Z
 */
JNIEXPORT jboolean JNICALL Java_nimbus_utils_CList_c_1contains(JNIEnv *env, jobject obj, jobject value)
{
    const char* cValue = JNIUtils::strFromJString(env, (jstring)value);
    listconstiter findIter = str_find(list.begin(), list.end(), cValue);
    return findIter != list.end();
}

/*
 * Class:     nimbus_utils_CList
 * Method:    c_get
 * Signature: (I)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_nimbus_utils_CList_c_1get(JNIEnv *env, jobject obj, jint index)
{
    listconstiter iter = list.begin();
    for (int i = 0; i < index; ++i)
    {
        ++iter;
    }

    return JNIUtils::strToJString(env, *iter);
}

/*
 * Class:     nimbus_utils_CList
 * Method:    c_indexOf
 * Signature: (Ljava/lang/Object;)I
 */
JNIEXPORT jint JNICALL Java_nimbus_utils_CList_c_1indexOf(JNIEnv *env, jobject obj, jobject value)
{
    const char* cValue = JNIUtils::strFromJString(env, (jstring)value);
    listconstiter iter = list.begin();
    listconstiter end = list.end();

    int index = 0;
    while (iter != end)
    {
        if (strcmp(*iter, cValue) == 0)
        {
            return index;
        }
    
        ++index;
        ++iter;
    }

    return -1;
}

/*
 * Class:     nimbus_utils_CList
 * Method:    c_isEmpty
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_nimbus_utils_CList_c_1isEmpty(JNIEnv *env, jobject obj)
{
    return list.empty();
}

/*
 * Class:     nimbus_utils_CList
 * Method:    c_lastIndexOf
 * Signature: (Ljava/lang/Object;)I
 */
JNIEXPORT jint JNICALL Java_nimbus_utils_CList_c_1lastIndexOf(JNIEnv *env, jobject obj, jobject value)
{
    const char* cValue = JNIUtils::strFromJString(env, (jstring)value);
    listreverseiter iter = list.rbegin();
    listreverseiter end = list.rend();

    int index = list.size() - 1;
    while (iter != end)
    {
        if (strcmp(*iter, cValue) == 0)
        {
            return index;
        }
    
        --index;
        ++iter;
    }

    return -1;
}

/*
 * Class:     nimbus_utils_CList
 * Method:    c_remove
 * Signature: (Ljava/lang/Object;)Z
 */
JNIEXPORT jboolean JNICALL Java_nimbus_utils_CList_c_1remove__Ljava_lang_Object_2(JNIEnv *env, jobject obj, jobject value)
{
    bool retval = false;
    listiter iter = str_find(list.begin(), list.end(), JNIUtils::strFromJString(env, (jstring)value));
    if (iter != list.end())
    {
        delete [] *iter;
        *iter = 0;

        list.erase(iter);
        retval = true;
    }

    return retval;
}

/*
 * Class:     nimbus_utils_CList
 * Method:    c_remove
 * Signature: (I)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_nimbus_utils_CList_c_1remove__I(JNIEnv *env, jobject obj, jint index)
{
    listiter iter = list.begin();
    for (int i = 0; i < index; ++i)
    {
        ++iter;
    }

    jstring retval = JNIUtils::strToJString(env, *iter);

    delete [] *iter;
    *iter = 0;

    list.erase(iter);
    return retval;
}

/*
 * Class:     nimbus_utils_CList
 * Method:    c_set
 * Signature: (ILjava/lang/String;)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_nimbus_utils_CList_c_1set(JNIEnv *env, jobject obj, jint index, jstring value)
{
   listiter iter = list.begin();
    for (int i = 0; i < index; ++i)
    {
        ++iter;
    }

    jstring retval = JNIUtils::strToJString(env, *iter);

    delete [] *iter;
    *iter = JNIUtils::strFromJString(env, value);

    return retval;
}

/*
 * Class:     nimbus_utils_CList
 * Method:    c_size
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_nimbus_utils_CList_c_1size(JNIEnv *env, jobject obj)
{
    return list.size();
}