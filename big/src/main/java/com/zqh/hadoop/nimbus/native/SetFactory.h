
#ifdef WINDOWS
#include <hash_map>
#else
#include <ext/hash_map>
#endif

#include <set>
#include <vector>
#include <cstring>

#ifndef _Included_SetFactory
#define _Included_SetFactory

#ifndef WINDOWS
using namespace __gnu_cxx;
#endif

class SetFactory {
public:	
	
	struct eqstr
	{
		bool operator()(const char* s1, const char* s2) const
		{
			return strcmp(s1, s2) == 0;
		}
	};

	struct ltstr
	{
		bool operator()(const char* s1, const char* s2) const
		{
			return strcmp(s1, s2) < 0;
		}
	};

	SetFactory();
	~SetFactory();

	int CreateNewSet();
	void DeleteSet(int index);
	std::set<const char*, ltstr>* Get(int index);
	void Destroy(int index);

private:
#ifdef WINDOWS
	typedef std::hash_map<int, std::set<const char*, ltstr>*>::const_iterator constmapsetiter;
	typedef std::hash_map<int, std::set<const char*, ltstr>*>::iterator mapsetiter;
	std::hash_map<int, std::set<const char*, ltstr>*>* pSets;
#else
	typedef hash_map<int, std::set<const char*, ltstr>*>::const_iterator constmapsetiter;
	typedef hash_map<int, std::set<const char*, ltstr>*>::iterator mapsetiter;
	hash_map<int, std::set<const char*, ltstr>*>* pSets;
#endif
};

#endif