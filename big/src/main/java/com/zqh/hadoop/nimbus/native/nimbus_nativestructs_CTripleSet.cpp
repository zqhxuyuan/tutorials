#include <jni.h>
#include <stdio.h>
#include "JNIUtils.h"
#include "nimbus_nativestructs_CTripleSet.h"
#include <map>
#include <set>
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

typedef std::set<const char*, ltstr> lastSet;
typedef std::map<const char*, lastSet*, ltstr> secondMap;
typedef std::map<const char*, secondMap*, ltstr> triplesetmap;


typedef std::set<const char*, ltstr>::iterator setiter;
typedef std::map<const char*, std::set<const char*, ltstr>*, ltstr>::iterator secondmapiter;
typedef std::map<const char*, std::map<const char*, std::set<const char*, ltstr>*, ltstr>*, ltstr>::iterator triplesetiter;

#define NO_ITER_TYPE -1
#define SET_ITER_TYPE 0
#define MAP_ITER_TYPE 1
#define TRIPLE_SET_ITER_TYPE 2

setiter sIter;
setiter sIterEnd;
secondmapiter sMapIter;
secondmapiter sMapIterEnd;
triplesetiter tSetIter;
triplesetiter tSetIterEnd;
int iterType = NO_ITER_TYPE;

triplesetmap tripleset;
int num = 0;
/*
 * Class:     nimbus_nativestructs_CTripleSet
 * Method:    c_iterHasNext
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_nimbus_nativestructs_CTripleSet_c_1iterHasNext(JNIEnv *, jobject)
{
   return iterType == NO_ITER_TYPE;
}

/*
 * Class:     nimbus_nativestructs_CTripleSet
 * Method:    c_iterNext
 * Signature: (Lnimbus/nativestructs/Triple;)V
 */
JNIEXPORT void JNICALL Java_nimbus_nativestructs_CTripleSet_c_1iterNext(JNIEnv *env, jobject, jobject obj)
{
    switch (iterType)
    {
    case NO_ITER_TYPE:
        break;
    case SET_ITER_TYPE:

        break;
    case MAP_ITER_TYPE:
        break;
    case TRIPLE_SET_ITER_TYPE:

        /*
        jclass cls = (*env)->GetObjectClass(env, obj);
        jfieldID fid1 = (*env)->GetFieldID(env, cls, "first", "Ljava/lang/String;");
        jfieldID fid2 = (*env)->GetFieldID(env, cls, "second", "Ljava/lang/String;");
        jfieldID fid3 = (*env)->GetFieldID(env, cls, "third", "Ljava/lang/String;");

        ++tSetIter;
	    if (mapIter != tripleset.end())
	    {        
            secondmapiter sMapIter = mapIter->second->begin();
	        secondmapiter sMapEnd = mapIter->second->end();

            while (sMapIter != sMapEnd) {
                
                setiter setIter = sMapIter->second->begin();
                setiter setEnd = sMapIter->second->end();

                while (setIter != setEnd)
                {
		            delete [] *setIter;
                    ++setIter;
                }

                delete [] sMapIter->first;
                delete sMapIter->second;
                ++sMapIter;
            }

            delete [] mapIter->first;
            delete mapIter->second;

		    ++mapIter;
	    }
        */

        break;
    }
}


/*
 * Class:     nimbus_nativestructs_CTripleSet
 * Method:    c_iterRemove
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_nimbus_nativestructs_CTripleSet_c_1iterRemove
  (JNIEnv *, jobject);

/*
 * Class:     nimbus_nativestructs_CTripleSet
 * Method:    c_setiter
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_nimbus_nativestructs_CTripleSet_c_1setiter__(JNIEnv *, jobject)
{
    tSetIter = tripleset.begin();
    tSetIterEnd = tripleset.end();
    iterType = TRIPLE_SET_ITER_TYPE;
}

/*
 * Class:     nimbus_nativestructs_CTripleSet
 * Method:    c_setiter
 * Signature: (Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_nimbus_nativestructs_CTripleSet_c_1setiter__Ljava_lang_String_2(JNIEnv *env, jobject obj, jstring s1)
{
    triplesetiter iter = tripleset.find(JNIUtils::strFromJString(env, s1));
    if (iter != tripleset.end())
    {
        sMapIter = iter->second->begin();
        sMapIterEnd = iter->second->end();
        iterType = MAP_ITER_TYPE;
    }
    else
    {
        iterType = NO_ITER_TYPE;
    }
}

/*
 * Class:     nimbus_nativestructs_CTripleSet
 * Method:    c_setiter
 * Signature: (Ljava/lang/String;Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_nimbus_nativestructs_CTripleSet_c_1setiter__Ljava_lang_String_2Ljava_lang_String_2(JNIEnv *env, jobject obj, jstring s1, jstring s2)
{
    triplesetiter iter = tripleset.find(JNIUtils::strFromJString(env, s1));
    if (iter != tripleset.end())
    {
        secondmapiter sMapIter = iter->second->find(JNIUtils::strFromJString(env, s2));        
        if (sMapIter != iter->second->end())
        {
            sIter = sMapIter->second->begin();
            sIterEnd = sMapIter->second->end();
            iterType = SET_ITER_TYPE;
        }
        else
        {
            iterType = NO_ITER_TYPE;
        }
    }
    else
    {
        iterType = NO_ITER_TYPE;
    }
}

/*
 * Class:     nimbus_nativestructs_CTripleSet
 * Method:    c_freeiter
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_nimbus_nativestructs_CTripleSet_c_1freeiter(JNIEnv *, jobject)
{
    iterType = -1;
}

/*
 * Class:     nimbus_nativestructs_CTripleSet
 * Method:    c_add
 * Signature: (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Z
 */
JNIEXPORT jboolean JNICALL Java_nimbus_nativestructs_CTripleSet_c_1add(JNIEnv *env, jobject obj, jstring s1, jstring s2, jstring s3)
{
    const char* cs1 = JNIUtils::strFromJString(env, s1);
    const char* cs2 = JNIUtils::strFromJString(env, s2);
    const char* cs3 = JNIUtils::strFromJString(env, s3);

    triplesetiter iter = tripleset.find(cs1);
    if (iter == tripleset.end())
    {
        // create a new record from scratch        
        lastSet* set = new lastSet();
        set->insert(cs3);

        secondMap* toPut = new secondMap();
        toPut->insert(std::pair<const char*, lastSet*>(cs2, set));
        tripleset.insert(std::pair<const char*, secondMap*>(cs1, toPut));
    } 
    else
    {
        // the first element is in here
        secondMap* sMap = iter->second;
        secondmapiter sMapIter =  sMap->find(cs2);   
        if (sMapIter == sMap->end()) {  
            // the second string wasn't found
            lastSet* set = new lastSet();
            set->insert(cs3);
            sMap->insert(std::pair<const char*, lastSet*>(cs2, set));
        }
        else
        {            
            // the second element is also here
            // add this to the set
            if (!sMapIter->second->insert(cs3).second)
            {
                return false;
            }
        }
    }

    ++num;
    return true;
}

/*
 * Class:     nimbus_nativestructs_CTripleSet
 * Method:    c_clear
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_nimbus_nativestructs_CTripleSet_c_1clear(JNIEnv *, jobject)
{
    triplesetiter mapIter = tripleset.begin();
	triplesetiter mapEnd = tripleset.end();

	const char* value = 0;
	while (mapIter != mapEnd)
	{        
        secondmapiter sMapIter = mapIter->second->begin();
	    secondmapiter sMapEnd = mapIter->second->end();

        while (sMapIter != sMapEnd) {
            
            setiter setIter = sMapIter->second->begin();
            setiter setEnd = sMapIter->second->end();

            while (setIter != setEnd)
            {
		        delete [] *setIter;
                ++setIter;
            }

            delete [] sMapIter->first;
            delete sMapIter->second;
            ++sMapIter;
        }

        delete [] mapIter->first;
        delete mapIter->second;

		++mapIter;
	}

    tripleset.clear();
    num = 0;
}
/*
 * Class:     nimbus_nativestructs_CTripleSet
 * Method:    c_print
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_nimbus_nativestructs_CTripleSet_c_1print(JNIEnv *, jobject)
{
    std::cout << "Writing " << num << " elements\n";
    triplesetiter mapIter = tripleset.begin();
	triplesetiter mapEnd = tripleset.end();

	while (mapIter != mapEnd)
	{
        secondmapiter sMapIter = mapIter->second->begin();
	    secondmapiter sMapEnd = mapIter->second->end();

        while (sMapIter != sMapEnd) {
            setiter setIter = sMapIter->second->begin();
            setiter setEnd = sMapIter->second->end();

            while (setIter != setEnd)
            {
                std::cout << mapIter->first << "\t" << sMapIter->first << "\t" << *setIter << "\n";
                ++setIter;
            }
            ++sMapIter;
        }
		++mapIter;
	}
}

/*
 * Class:     nimbus_nativestructs_CTripleSet
 * Method:    c_contains
 * Signature: (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Z
 */
JNIEXPORT jboolean JNICALL Java_nimbus_nativestructs_CTripleSet_c_1contains(JNIEnv *env, jobject obj, jstring s1, jstring s2, jstring s3)
{
    triplesetiter iter = tripleset.find(JNIUtils::strFromJString(env, s1));
    if (iter != tripleset.end())
    {
        secondmapiter sMapIter =  iter->second->find(JNIUtils::strFromJString(env, s2));        
        if (sMapIter != iter->second->end())
        {
            return sMapIter->second->find(JNIUtils::strFromJString(env, s3)) != sMapIter->second->end();
        }
    }
    
    return false;
}

/*
 * Class:     nimbus_nativestructs_CTripleSet
 * Method:    c_isEmpty
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_nimbus_nativestructs_CTripleSet_c_1isEmpty
  (JNIEnv *, jobject)
{
    return num == 0;
}

/*
 * Class:     nimbus_nativestructs_CTripleSet
 * Method:    c_remove
 * Signature: (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Z
 */
JNIEXPORT jboolean JNICALL Java_nimbus_nativestructs_CTripleSet_c_1remove(JNIEnv *env, jobject obj, jstring s1, jstring s2, jstring s3)
{
    const char* cs1 = JNIUtils::strFromJString(env, s1);

    triplesetiter iter = tripleset.find(cs1);
    if (iter != tripleset.end())
    {
        const char* cs2 = JNIUtils::strFromJString(env, s2);
        secondMap* sMap = iter->second;
        secondmapiter sMapIter =  sMap->find(cs2);   
        if (sMapIter != sMap->end())
        {
            const char* cs3 = JNIUtils::strFromJString(env, s3);
            setiter setIter = sMapIter->second->find(cs3);
	        if (setIter != sMapIter->second->end())
	        {
		        delete [] *setIter;
		        sMapIter->second->erase(setIter);

                --num;
		        return true;
            }
        }
    }
    return false;
}

/*
 * Class:     nimbus_nativestructs_CTripleSet
 * Method:    c_size
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_nimbus_nativestructs_CTripleSet_c_1size
  (JNIEnv *, jobject)
{
    return num;
}