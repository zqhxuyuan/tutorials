package com.ibm.classloader.example;

public class Sample {
	private Sample instance;
	
	public void setSample(Object instance) {
		this.instance = (Sample) instance;
	}
}
