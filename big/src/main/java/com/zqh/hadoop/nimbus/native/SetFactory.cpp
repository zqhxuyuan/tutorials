

#include <set>
#include <vector>
#include "SetFactory.h"

SetFactory::SetFactory()
	: pSets(0)
{
#ifdef WINDOWS
	pSets = new std::hash_map<int, std::set<const char*, ltstr>*>();
#else
	pSets = new __gnu_cxx::hash_map<int, std::set<const char*, ltstr>*>();
#endif
}

SetFactory::~SetFactory()
{
	mapsetiter iter = pSets->begin();

	while (iter != pSets->end()) {
		delete iter->second;
		iter->second = 0;
		++iter;
	}

	pSets->clear();

	delete pSets;
	pSets = 0;
}

int SetFactory::CreateNewSet()
{
	std::set<const char*, ltstr>* newSet = new std::set<const char*, ltstr>();
	int index = pSets->size();
	pSets->insert(std::pair<int, std::set<const char*, ltstr>*>(index, newSet));
	return index;
}

void SetFactory::DeleteSet(int index)
{
	mapsetiter iter = pSets->find(index);
	if (iter != pSets->end())
	{
		delete iter->second;
		iter->second = 0;
		pSets->erase(iter);
	}
}

std::set<const char*, SetFactory::ltstr>* SetFactory::Get(int index)
{
	mapsetiter iter = pSets->find(index);
	if (iter != pSets->end())
	{
		return (*iter).second;
	}
	else
	{
		return 0;
	}
}

void SetFactory::Destroy(int index)
{
	mapsetiter iter = pSets->find(index);
	if (iter != pSets->end())
	{
		delete (*iter).second;
		(*iter).second = 0;
		pSets->erase(iter);
	}
}