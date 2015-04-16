#include <jni.h>
#include <stdio.h>
#include "JNIUtils.h"
#include "nimbus_nativestructs_CSet.h"
#include "SetFactory.h"
#include <map>
#include <set>
#include <cstring>
#include <sstream>
#include <iostream>

SetFactory csetSetFactory;

typedef std::set<const char*, SetFactory::ltstr> settype;
typedef std::set<const char*, SetFactory::ltstr>::const_iterator setconstiter;
typedef std::set<const char*, SetFactory::ltstr>::iterator setiter;

typedef std::map<int, setconstiter*> IndexedIterMapType;
typedef IndexedIterMapType::const_iterator IndexedToIterMapConstIter;
typedef IndexedIterMapType::iterator IndexedToIterMapIter;

typedef std::map<int, IndexedIterMapType*> SetToIndexedIterMapType;
typedef SetToIndexedIterMapType::const_iterator SetToItersMapConstIter;
typedef SetToIndexedIterMapType::iterator SetToItersMapIter;

SetToIndexedIterMapType setToIterMap;

/*
 * Class:     nimbus_nativestructs_CSet
 * Method:    delete
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_nimbus_nativestructs_CSet_delete(JNIEnv *, jobject, jint si)
{
	// delete the actual set
	csetSetFactory.DeleteSet(si);

	SetToItersMapIter iter = setToIterMap.find(si);
	if (iter != setToIterMap.end())
	{
		// delete the setconstiter*
		IndexedIterMapType* indexedMap = iter->second;
		IndexedToIterMapIter indexedIter = indexedMap->begin();
		while (indexedIter != indexedMap->end())
		{
			delete indexedIter->second;
			indexedIter->second = 0;
			++indexedIter;
		}

		delete iter->second;
		iter->second = 0;
		setToIterMap.erase(iter);
	}	
}

/*
 * Class:     nimbus_nativestructs_CSet
 * Method:    newSet
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_nimbus_nativestructs_CSet_newSet(JNIEnv *, jobject)
{
	return csetSetFactory.CreateNewSet();
}

/*
 * Class:     CSet
 * Method:    c_add
 * Signature: (Ljava/lang/String;)Z
 */
JNIEXPORT jboolean JNICALL Java_nimbus_nativestructs_CSet_c_1add(JNIEnv *env, jobject obj, jint si, jstring element)
{
	settype* set = csetSetFactory.Get(si);
	return set->insert(JNIUtils::strFromJString(env, element)).second;
}

/*
 * Class:     CSet
 * Method:    c_clear
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_nimbus_nativestructs_CSet_c_1clear(JNIEnv *env, jobject obj, jint si)
{
	settype* set = csetSetFactory.Get(si);
	setiter iter = set->begin();
	setiter end = set->end();
	const char* value = 0;
	while (iter != end)
	{
		value = *iter;
		delete [] value;
		value = 0;
		++iter;
	}

	set->clear();
}

/*
 * Class:     CSet
 * Method:    c_contains
 * Signature: (Ljava/lang/Object;)Z
 */
JNIEXPORT jboolean JNICALL Java_nimbus_nativestructs_CSet_c_1contains(JNIEnv *env, jobject obj, jint si, jstring element)
{
	settype* set = csetSetFactory.Get(si);
	return set->find(JNIUtils::strFromJString(env, element)) != set->end();
}

/*
 * Class:     CSet
 * Method:    c_isEmpty
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_nimbus_nativestructs_CSet_c_1isEmpty(JNIEnv *, jobject, jint si)
{
	settype* set = csetSetFactory.Get(si);
	return set->empty();
}

/*
 * Class:     CSet
 * Method:    c_remove
 * Signature: (Ljava/lang/Object;)Z
 */
JNIEXPORT jboolean JNICALL Java_nimbus_nativestructs_CSet_c_1remove(JNIEnv *env, jobject obj, jint si, jstring element)
{
	settype* set = csetSetFactory.Get(si);
	setiter iter = set->find(JNIUtils::strFromJString(env, element));
	if (iter != set->end())
	{
		const char* value = *iter;

		set->erase(iter);

		delete [] value;
		value = 0;

		return true;
	}
	else
	{
		return false;
	}
}

/*
 * Class:     CSet
 * Method:    c_size
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_nimbus_nativestructs_CSet_c_1size(JNIEnv *, jobject, jint si)
{
	settype* set = csetSetFactory.Get(si);
	return set->size();
}


/*
 * Class:     nimbus_nativestructs_CSet
 * Method:    c_iterInit
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_nimbus_nativestructs_CSet_c_1iterInit(JNIEnv *, jobject, jint si)
{	
	settype* set = csetSetFactory.Get(si);

	// create a new iterator for this set
	setconstiter* pIter = new setconstiter(set->begin());
	
	// check whether or not we have created an iterator for this map before
	IndexedIterMapType* indexedIters = 0;
	SetToItersMapConstIter iter = setToIterMap.find(si);
	if (iter != setToIterMap.end())
	{
		// if we have, then we will want to add a new set const iter with a new index
		indexedIters = iter->second;
	}
	else
	{
		// if we haven't yet, we will need to make a new map for the indexed iterators and add it to the map
		indexedIters = new IndexedIterMapType();
		setToIterMap.insert(std::pair<int, IndexedIterMapType*>(si, indexedIters));
	}
	
	int newIndex = indexedIters->size();
	indexedIters->insert(std::pair<int, setconstiter*>(newIndex, pIter));
	return newIndex;
}
/*
 * Class:     nimbus_nativestructs_CSet
 * Method:    c_iterHasNext
 * Signature: (II)Z
 */
JNIEXPORT jboolean JNICALL Java_nimbus_nativestructs_CSet_c_1iterHasNext(JNIEnv *, jobject, jint si, jint index)
{	
	settype* set = csetSetFactory.Get(si);		
	// check whether or not we have created an iterator for this map before
	SetToItersMapConstIter iter = setToIterMap.find(si);
	if (iter != setToIterMap.end())
	{
		// if we have, then we will want to add a new set const iter with a new index
		IndexedToIterMapConstIter indexIter = iter->second->find(index);
		return (*(*indexIter).second) != set->end();
	}

	return false;	
}

/*
 * Class:     nimbus_nativestructs_CSet
 * Method:    c_iterNext
 * Signature: (II)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_nimbus_nativestructs_CSet_c_1iterNext(JNIEnv* env, jobject, jint si, jint index)
{
	settype* set = csetSetFactory.Get(si);
	// check whether or not we have created an iterator for this map before
	SetToItersMapConstIter iter = setToIterMap.find(si);
	if (iter != setToIterMap.end())
	{
		// if we have, then we will want to add a new set const iter with a new index
		IndexedToIterMapConstIter indexIter = iter->second->find(index);

		setconstiter* setIter = indexIter->second;
		if (*setIter != set->end()) {
			jstring retval = JNIUtils::strToJString(env, *(*setIter));
			(*setIter)++;
			return retval;
		}
	}

	return 0;
}
