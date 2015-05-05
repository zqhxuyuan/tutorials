package com.github.NoahShen.jue.doc;

/*
Copyright (c) 2002 JSON.org

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

The Software shall be used for Good, not Evil.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

import java.util.ArrayList;

/**
 * 基于org.json的DocArray修改
 * @author noah
 */
public class DocArray {


    /**
     * The arrayList where the DocArray's properties are kept.
     */
    private ArrayList<Object> myArrayList;


    /**
     * Construct an empty DocArray.
     */
    public DocArray() {
        this.myArrayList = new ArrayList<Object>();
    }

    /**
     * Construct a DocArray from a DocTokener.
     * @param x A DocTokener
     */
    public DocArray(DocTokener x) {
        this();
        if (x.nextClean() != '[') {
            throw x.syntaxError("A DocArray text must start with '['");
        }
        if (x.nextClean() != ']') {
	        x.back();
	        for (;;) {
	            if (x.nextClean() == ',') {
	                x.back();
	                this.myArrayList.add(DocObject.NULL);
	            } else {
	                x.back();
	                this.myArrayList.add(x.nextValue());
	            }
	            switch (x.nextClean()) {
	            case ';':
	            case ',':
	                if (x.nextClean() == ']') {
	                    return;
	                }
	                x.back();
	                break;
	            case ']':
	            	return;
	            default:
	                throw x.syntaxError("Expected a ',' or ']'");
	            }
	        }
        }
    }


    /**
     * Construct a DocArray from a source Doc text.
     * @param source     A string that begins with
     * <code>[</code>&nbsp;<small>(left bracket)</small>
     *  and ends with <code>]</code>&nbsp;<small>(right bracket)</small>.
     */
    public DocArray(String source) {
        this(new DocTokener(source));
    }
    
    
    /**
     * Make a string from the contents of this DocArray. The
     * <code>separator</code> string is inserted between each element.
     * Warning: This method assumes that the data structure is acyclical.
     * @param separator A string that will be inserted between the elements.
     * @return a string.
     */
    public String join(String separator) {
        int len = length();
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < len; i += 1) {
            if (i > 0) {
                sb.append(separator);
            }
            sb.append(DocObject.valueToString(this.myArrayList.get(i)));
        }
        return sb.toString();
    }


    /**
     * Get the number of elements in the DocArray, included nulls.
     *
     * @return The length (or size).
     */
    public int length() {
        return this.myArrayList.size();
    }


    /**
     * Get the object value associated with an index.
     * @param index The index must be between 0 and length() - 1.
     * @return      An object value, or null if there is no
     *              object at that index.
     */
    public Object get(int index) {
        return (index < 0 || index >= length()) ?
            null : this.myArrayList.get(index);
    }


    /**
     * Get the boolean value associated with an index.
     * It returns false if there is no value at that index,
     * or if the value is not Boolean.TRUE or the String "true".
     *
     * @param index The index must be between 0 and length() - 1.
     * @return      The truth.
     */
    public boolean getBoolean(int index)  {
        return getBoolean(index, false);
    }


    /**
     * Get the boolean value associated with an index.
     * It returns the defaultValue if there is no value at that index or if
     * it is not a Boolean or the String "true" or "false" (case insensitive).
     *
     * @param index The index must be between 0 and length() - 1.
     * @param defaultValue     A boolean default.
     * @return      The truth.
     */
    public boolean getBoolean(int index, boolean defaultValue)  {
        try {
            return getBoolean(index);
        } catch (Exception e) {
            return defaultValue;
        }
    }


    /**
     * Get the double value associated with an index.
     * NaN is returned if there is no value for the index,
     * or if the value is not a number and cannot be converted to a number.
     *
     * @param index The index must be between 0 and length() - 1.
     * @return      The value.
     */
    public double getDouble(int index) {
        return getDouble(index, Double.NaN);
    }


    /**
     * Get the double value associated with an index.
     * The defaultValue is returned if there is no value for the index,
     * or if the value is not a number and cannot be converted to a number.
     *
     * @param index subscript
     * @param defaultValue     The default value.
     * @return      The value.
     */
    public double getDouble(int index, double defaultValue) {
        try {
            return getDouble(index);
        } catch (Exception e) {
            return defaultValue;
        }
    }


    /**
     * Get the int value associated with an index.
     * Zero is returned if there is no value for the index,
     * or if the value is not a number and cannot be converted to a number.
     *
     * @param index The index must be between 0 and length() - 1.
     * @return      The value.
     */
    public int getInt(int index) {
        return getInt(index, 0);
    }


    /**
     * Get the int value associated with an index.
     * The defaultValue is returned if there is no value for the index,
     * or if the value is not a number and cannot be converted to a number.
     * @param index The index must be between 0 and length() - 1.
     * @param defaultValue     The default value.
     * @return      The value.
     */
    public int getInt(int index, int defaultValue) {
        try {
            return getInt(index);
        } catch (Exception e) {
            return defaultValue;
        }
    }


    /**
     * Get the DocArray associated with an index.
     * @param index subscript
     * @return      A DocArray value, or null if the index has no value,
     * or if the value is not a DocArray.
     */
    public DocArray getDocArray(int index) {
        Object o = get(index);
        return o instanceof DocArray ? (DocArray)o : null;
    }


    /**
     * Get the DocObject associated with an index.
     * Null is returned if the key is not found, or null if the index has
     * no value, or if the value is not a DocObject.
     *
     * @param index The index must be between 0 and length() - 1.
     * @return      A DocObject value.
     */
    public DocObject getDocObject(int index) {
        Object o = get(index);
        return o instanceof DocObject ? (DocObject)o : null;
    }


    /**
     * Get the long value associated with an index.
     * Zero is returned if there is no value for the index,
     * or if the value is not a number and cannot be converted to a number.
     *
     * @param index The index must be between 0 and length() - 1.
     * @return      The value.
     */
    public long getLong(int index) {
        return getLong(index, 0);
    }


    /**
     * Get the long value associated with an index.
     * The defaultValue is returned if there is no value for the index,
     * or if the value is not a number and cannot be converted to a number.
     * @param index The index must be between 0 and length() - 1.
     * @param defaultValue     The default value.
     * @return      The value.
     */
    public long getLong(int index, long defaultValue) {
        try {
            return getLong(index);
        } catch (Exception e) {
            return defaultValue;
        }
    }


    /**
     * Get the string value associated with an index. It returns an
     * empty string if there is no value at that index. If the value
     * is not a string and is not null, then it is coverted to a string.
     *
     * @param index The index must be between 0 and length() - 1.
     * @return      A String value.
     */
    public String getString(int index) {
        return getString(index, "");
    }


    /**
     * Get the string associated with an index.
     * The defaultValue is returned if the key is not found.
     *
     * @param index The index must be between 0 and length() - 1.
     * @param defaultValue     The default value.
     * @return      A String value.
     */
    public String getString(int index, String defaultValue) {
        Object object = get(index);
        return object != null ? object.toString() : defaultValue;
    }


    /**
     * Append a boolean value. This increases the array's length by one.
     *
     * @param value A boolean value.
     * @return this.
     */
    public DocArray put(boolean value) {
        put(value ? Boolean.TRUE : Boolean.FALSE);
        return this;
    }


    /**
     * Append a double value. This increases the array's length by one.
     *
     * @param value A double value.
     * @return this.
     */
    public DocArray put(double value) {
        Double d = new Double(value);
        DocObject.testValidity(d);
        put(d);
        return this;
    }


    /**
     * Append an int value. This increases the array's length by one.
     *
     * @param value An int value.
     * @return this.
     */
    public DocArray put(int value) {
        put(new Integer(value));
        return this;
    }


    /**
     * Append an long value. This increases the array's length by one.
     *
     * @param value A long value.
     * @return this.
     */
    public DocArray put(long value) {
        put(new Long(value));
        return this;
    }


    /**
     * Append an object value. This increases the array's length by one.
     * @param value An object value.  The value should be a
     *  Boolean, Double, Integer, DocArray, DocObject, Long, or String, or the
     *  DocObject.NULL object.
     * @return this.
     */
    public DocArray put(Object value) {
        this.myArrayList.add(value);
        return this;
    }


    /**
     * Put or replace a boolean value in the DocArray. If the index is greater
     * than the length of the DocArray, then null elements will be added as
     * necessary to pad it out.
     * @param index The subscript.
     * @param value A boolean value.
     * @return this.
     */
    public DocArray put(int index, boolean value) {
        put(index, value ? Boolean.TRUE : Boolean.FALSE);
        return this;
    }

    /**
     * Put or replace a double value. If the index is greater than the length of
     *  the DocArray, then null elements will be added as necessary to pad
     *  it out.
     * @param index The subscript.
     * @param value A double value.
     * @return this.
     */
    public DocArray put(int index, double value) {
        put(index, new Double(value));
        return this;
    }


    /**
     * Put or replace an int value. If the index is greater than the length of
     *  the DocArray, then null elements will be added as necessary to pad
     *  it out.
     * @param index The subscript.
     * @param value An int value.
     * @return this.
     */
    public DocArray put(int index, int value) {
        put(index, new Integer(value));
        return this;
    }


    /**
     * Put or replace a long value. If the index is greater than the length of
     *  the DocArray, then null elements will be added as necessary to pad
     *  it out.
     * @param index The subscript.
     * @param value A long value.
     * @return this.
     */
    public DocArray put(int index, long value) {
        put(index, new Long(value));
        return this;
    }

    /**
     * Put or replace an object value in the DocArray. If the index is greater
     *  than the length of the DocArray, then null elements will be added as
     *  necessary to pad it out.
     * @param index The subscript.
     * @param value The value to put into the array. The value should be a
     *  Boolean, Double, Integer, DocArray, DocObject, Long, or String, or the
     *  DocObject.NULL object.
     * @return this.
     */
    public DocArray put(int index, Object value) {
        DocObject.testValidity(value);
        if (index < 0) {
            throw new DocException("DocArray[" + index + "] not found.");
        }
        if (index < length()) {
            this.myArrayList.set(index, value);
        } else {
            while (index != length()) {
                put(DocObject.NULL);
            }
            put(value);
        }
        return this;
    }
    
    
    /**
     * Remove an index and close the hole.
     * @param index The index of the element to be removed.
     * @return The value that was associated with the index,
     * or null if there was no value.
     */
    public Object remove(int index) {
    	Object o = get(index);
        this.myArrayList.remove(index);
        return o;
    }


    /**
     * Produce a DocObject by combining a DocArray of names with the values
     * of this DocArray.
     * @param names A DocArray containing a list of key strings. These will be
     * paired with the values.
     * @return A DocObject, or null if there are no names or if this DocArray
     * has no values.
     */
    public DocObject toDocObject(DocArray names) {
        if (names == null || names.length() == 0 || length() == 0) {
            return null;
        }
        DocObject jo = new DocObject();
        for (int i = 0; i < names.length(); i += 1) {
            jo.put(names.getString(i), this.get(i));
        }
        return jo;
    }


    /**
     * Make a Doc text of this DocArray. For compactness, no
     * unnecessary whitespace is added. If it is not possible to produce a
     * syntactically correct Doc text then null will be returned instead. This
     * could occur if the array contains an invalid number.
     * <p>
     * Warning: This method assumes that the data structure is acyclical.
     *
     * @return a printable, displayable, transmittable
     *  representation of the array.
     */
    public String toString() {
        try {
            return '[' + join(",") + ']';
        } catch (Exception e) {
            return null;
        }
    }


    /**
     * Make a prettyprinted Doc text of this DocArray.
     * Warning: This method assumes that the data structure is acyclical.
     * @param indentFactor The number of spaces to add to each level of
     *  indentation.
     * @return a printable, displayable, transmittable
     *  representation of the object, beginning
     *  with <code>[</code>&nbsp;<small>(left bracket)</small> and ending
     *  with <code>]</code>&nbsp;<small>(right bracket)</small>.
     */
    public String toString(int indentFactor) {
        return toString(indentFactor, 0);
    }


    /**
     * Make a prettyprinted Doc text of this DocArray.
     * Warning: This method assumes that the data structure is acyclical.
     * @param indentFactor The number of spaces to add to each level of
     *  indentation.
     * @param indent The indention of the top level.
     * @return a printable, displayable, transmittable
     *  representation of the array.
     */
    String toString(int indentFactor, int indent) {
        int len = length();
        if (len == 0) {
            return "[]";
        }
        int i;
        StringBuffer sb = new StringBuffer("[");
        if (len == 1) {
            sb.append(DocObject.valueToString(this.myArrayList.get(0),
                    indentFactor, indent));
        } else {
            int newindent = indent + indentFactor;
            sb.append('\n');
            for (i = 0; i < len; i += 1) {
                if (i > 0) {
                    sb.append(",\n");
                }
                for (int j = 0; j < newindent; j += 1) {
                    sb.append(' ');
                }
                sb.append(DocObject.valueToString(this.myArrayList.get(i),
                        indentFactor, newindent));
            }
            sb.append('\n');
            for (i = 0; i < indent; i += 1) {
                sb.append(' ');
            }
        }
        sb.append(']');
        return sb.toString();
    }
}