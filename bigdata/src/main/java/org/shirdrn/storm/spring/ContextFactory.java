package org.shirdrn.storm.spring;

/**
 * A context object can be held by a {@link ContextFactory}
 * object, which has a map container to manage multiple context
 * objects based on the context <code>name</code>.
 * 
 * @author yanjun
 * @param <T> Context type
 */
public interface ContextFactory<T> {

	/**
	 * Register a context identified by <code>name</code> 
	 * related with <code>configs</code>.
	 * @param name
	 * @param configs
	 */
	void register(String name, String... configs);
	
	/**
	 * Obtain a context object by retrieving the given context <code>name</code>.
	 * @param name
	 * @return
	 */
	T getContext(String name);
	
}
