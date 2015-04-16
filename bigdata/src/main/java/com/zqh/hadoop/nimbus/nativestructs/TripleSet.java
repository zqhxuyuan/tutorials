package com.zqh.hadoop.nimbus.nativestructs;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.zqh.hadoop.nimbus.utils.NullIterator;

public class TripleSet {

	private Map<String, Map<String, Set<String>>> triples = new HashMap<String, Map<String, Set<String>>>();
	private long size = 0;

	protected class TripleSetIterator implements Iterator<Triple> {

		Iterator<Entry<String, Map<String, Set<String>>>> topLevel = null;
		Iterator<Entry<String, Set<String>>> midLevel = null;
		Iterator<String> botLevel = null;
		private Triple nextTriple = new Triple();

		public TripleSetIterator() {
			topLevel = triples.entrySet().iterator();
			if (topLevel.hasNext()) {
				Entry<String, Map<String, Set<String>>> topElement = topLevel
						.next();
				nextTriple.setFirst(topElement.getKey());
				midLevel = topElement.getValue().entrySet().iterator();
				if (midLevel.hasNext()) {
					Entry<String, Set<String>> midElement = midLevel.next();
					nextTriple.setSecond(midElement.getKey());
					botLevel = midElement.getValue().iterator();
				} else {
					botLevel = new NullIterator<String>();
				}
			} else {
				midLevel = new NullIterator<Entry<String, Set<String>>>();
				botLevel = new NullIterator<String>();
			}
		}

		public TripleSetIterator(String s1) {

			topLevel = new NullIterator<Entry<String, Map<String, Set<String>>>>();
			nextTriple.setFirst(s1);

			Map<String, Set<String>> topElement = triples.get(s1);
			if (topElement != null) {
				midLevel = topElement.entrySet().iterator();
				if (midLevel.hasNext()) {
					Entry<String, Set<String>> midElement = midLevel.next();
					nextTriple.setSecond(midElement.getKey());
					botLevel = midElement.getValue().iterator();
				} else {
					botLevel = new NullIterator<String>();
				}
			} else {
				midLevel = new NullIterator<Entry<String, Set<String>>>();
				botLevel = new NullIterator<String>();
			}
		}

		public TripleSetIterator(String s1, String s2) {

			nextTriple.setFirst(s1);
			nextTriple.setSecond(s2);
			topLevel = new NullIterator<Entry<String, Map<String, Set<String>>>>();
			midLevel = new NullIterator<Entry<String, Set<String>>>();

			Map<String, Set<String>> topElement = triples.get(s1);
			if (topElement != null) {
				Set<String> midElements = topElement.get(s2);
				if (midElements != null) {
					botLevel = midElements.iterator();
				} else {
					botLevel = new NullIterator<String>();
				}
			} else {
				botLevel = new NullIterator<String>();
			}
		}

		@Override
		public boolean hasNext() {

			boolean cont = false;
			boolean retval = true;
			do {
				cont = false;
				// if bottom level has a value, set the
				// triple and exit this loop
				if (!botLevel.hasNext()) {
					if (midLevel.hasNext()) {
						Entry<String, Set<String>> midElement = midLevel.next();
						nextTriple.setSecond(midElement.getKey());
						botLevel = midElement.getValue().iterator();
						cont = true;
					} else {
						if (topLevel.hasNext()) {
							cont = true;
							Entry<String, Map<String, Set<String>>> topElement = topLevel
									.next();
							nextTriple.setFirst(topElement.getKey());
							midLevel = topElement.getValue().entrySet()
									.iterator();
							if (midLevel.hasNext()) {
								Entry<String, Set<String>> midElement = midLevel
										.next();
								nextTriple.setSecond(midElement.getKey());
								botLevel = midElement.getValue().iterator();
							} else {
								botLevel = new NullIterator<String>();
							}
						} else {
							// bail out!
							retval = false;
							nextTriple = null;
						}
					}
				}
			} while (cont);

			return retval;
		}

		@Override
		public Triple next() {
			if (hasNext()) {
				nextTriple.setThird(botLevel.next());
				return nextTriple;
			} else {
				return null;
			}
		}

		@Override
		public void remove() {
			botLevel.remove();
		}
	}

	public boolean contains(String s1, String s2, String s3) {
		Map<String, Set<String>> key1 = triples.get(s1);
		if (key1 != null) {
			Set<String> key2 = key1.get(s2);
			if (key2 != null && key2.contains(s3)) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public boolean contains(Triple t) {
		return contains(t.getFirst(), t.getSecond(), t.getThird());
	}

	public boolean containsAll(Collection<Triple> triples) {
		boolean retval = true;
		for (Triple t : triples) {
			if (!contains(t.getFirst(), t.getSecond(), t.getThird())) {
				retval = false;
				break;
			}
		}
		return retval;
	}

	public boolean add(String s1, String s2, String s3) {
		boolean retval = true;
		Map<String, Set<String>> key2 = triples.get(s1);
		if (key2 == null) {
			Set<String> newSet = new HashSet<String>();
			newSet.add(s3);

			Map<String, Set<String>> newMap = new HashMap<String, Set<String>>();
			newMap.put(s2, newSet);

			triples.put(s1, newMap);
		} else {
			Set<String> key3 = key2.get(s2);
			if (key3 != null) {
				retval = key3.add(s3);
				if (!retval) {
					--size;
				}
			} else {
				Set<String> newSet = new HashSet<String>();
				newSet.add(s3);
				key2.put(s2, newSet);
			}
		}

		++size;
		return retval;
	}

	public boolean add(String[] split) {
		return add(split[0], split[1], split[2]);
	}

	public boolean add(Triple t) {
		return add(t.getFirst(), t.getSecond(), t.getThird());
	}

	public boolean addAll(Collection<Triple> triples) {
		boolean retval = false;
		for (Triple t : triples) {
			retval |= add(t.getFirst(), t.getSecond(), t.getThird());
		}
		return retval;
	}

	public Iterator<Triple> iterator() {
		return new TripleSetIterator();
	}

	public Iterator<Triple> iterator(String s1) {
		return new TripleSetIterator(s1);

	}

	public Iterator<Triple> iterator(String s1, String s2) {
		return new TripleSetIterator(s1, s2);

	}

	public boolean remove(String s1, String s2, String s3) {
		boolean retval = false;
		Map<String, Set<String>> key2 = triples.get(s1);
		if (key2 != null) {
			Set<String> key3 = key2.get(s2);
			if (key3 != null) {
				retval = key3.remove(s3);
				if (!retval) {
					++size;
				} else {
					if (key3.size() == 0) {
						key2.remove(s2);
						if (key2.size() == 0) {
							triples.remove(s1);
						}
					}
				}
			}
		}
		--size;
		return retval;
	}

	public boolean isEmpty() {
		return size == 0;
	}

	public long size() {
		return size;
	}

	public long sizeOf(String s1) {

		long sum = 0;
		Map<String, Set<String>> map = triples.get(s1);
		if (map != null) {
			for (Entry<String, Set<String>> entry : map.entrySet()) {
				sum += entry.getValue().size();
			}
		}

		return sum;
	}

	public long sizeOf(String s1, String s2) {
		long sum = 0;
		Map<String, Set<String>> map = triples.get(s1);
		if (map != null) {
			Set<String> set = map.get(s2);
			if (set != null) {
				sum = set.size();
			}
		}

		return sum;
	}

	public void clear() {
		triples.clear();
		size = 0;
	}

	public boolean removeAll(Collection<Triple> triples) {
		boolean retval = false;
		for (Triple t : triples) {
			retval |= remove(t.getFirst(), t.getSecond(), t.getThird());
		}
		return retval;
	}

	public boolean remove(Triple t) {
		return remove(t.getFirst(), t.getSecond(), t.getThird());
	}
}
