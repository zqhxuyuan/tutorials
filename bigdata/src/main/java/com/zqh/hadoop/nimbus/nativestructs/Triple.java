package com.zqh.hadoop.nimbus.nativestructs;

public class Triple {

	private static final String TAB = "\t";
	private String[] array = new String[3];

	public Triple() {
	}

	public Triple(String first, String second, String third) {
		setFirst(first);
		setSecond(second);
		setThird(third);
	}

	public String getFirst() {
		return array[0];
	}

	public void setFirst(String first) {
		array[0] = first;
	}

	public String getSecond() {
		return array[1];
	}

	public void setSecond(String second) {
		array[1] = second;
	}

	public String getThird() {
		return array[2];
	}

	public void setThird(String third) {
		array[2] = third;
	}

	public void set(Triple t) {
		setFirst(t.getFirst());
		setSecond(t.getSecond());
		setThird(t.getThird());
	}

	public void set(String f, String s, String t) {
		setFirst(f);
		setSecond(s);
		setThird(t);
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return obj.toString().equals(this.toString());
	}

	@Override
	public String toString() {
		return array[0] + TAB + array[1] + TAB + array[2];
	}

	public String[] getTripleArray() {
		return array;
	}
}
