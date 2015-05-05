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

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeSet;

/**
 * 基于JsonObject修改
 * @author noah
 */
public class DocObject {

    /**
     * DocObject.NULL is equivalent to the value that JavaScript calls null,
     * whilst Java's null is equivalent to the value that JavaScript calls
     * undefined.
     */
     private static final class Null {

        /**
         * There is only intended to be a single instance of the NULL object,
         * so the clone method returns itself.
         * @return     NULL.
         */
        protected final Object clone() {
            return this;
        }

        /**
         * A Null object is equal to the null value and to itself.
         * @param object    An object to test for nullness.
         * @return true if the object parameter is the DocObject.NULL object
         *  or null.
         */
        public boolean equals(Object object) {
            return object == null || object == this;
        }

        /**
         * Get the "null" string value.
         * @return The string "null".
         */
        public String toString() {
            return "null";
        }
    }


    /**
     * The map where the DocObject's properties are kept.
     */
    private Map<String, Object> map;


    /**
     * It is sometimes more convenient and less ambiguous to have a
     * <code>NULL</code> object than to use Java's <code>null</code> value.
     * <code>DocObject.NULL.equals(null)</code> returns <code>true</code>.
     * <code>DocObject.NULL.toString()</code> returns <code>"null"</code>.
     */
    public static final Object NULL = new Null();


    /**
     * Construct an empty DocObject.
     */
    public DocObject() {
        this.map = new HashMap<String, Object>();
    }


    /**
     * Construct a DocObject from a DocTokener.
     * @param x A DocTokener object containing the source string.
     */
    public DocObject(DocTokener x) {
        this();
        char c;
        String key;

        if (x.nextClean() != '{') {
            throw x.syntaxError("A DocObject text must begin with '{'");
        }
        for (;;) {
            c = x.nextClean();
            switch (c) {
            case 0:
                throw x.syntaxError("A DocObject text must end with '}'");
            case '}':
                return;
            default:
                x.back();
                key = x.nextValue().toString();
            }

// The key is followed by ':'. We will also tolerate '=' or '=>'.

            c = x.nextClean();
            if (c == '=') {
                if (x.next() != '>') {
                    x.back();
                }
            } else if (c != ':') {
                throw x.syntaxError("Expected a ':' after a key");
            }
            putOnce(key, x.nextValue());

// Pairs are separated by ','. We will also tolerate ';'.

            switch (x.nextClean()) {
            case ';':
            case ',':
                if (x.nextClean() == '}') {
                    return;
                }
                x.back();
                break;
            case '}':
                return;
            default:
                throw x.syntaxError("Expected a ',' or '}'");
            }
        }
    }


    /**
     * Construct a DocObject from a source Doc text string.
     * This is the most commonly used DocObject constructor.
     * @param source    A string beginning
     *  with <code>{</code>&nbsp;<small>(left brace)</small> and ending
     *  with <code>}</code>&nbsp;<small>(right brace)</small>.
     * @exception   If there is a syntax error in the source
     *  string or a duplicated key.
     */
    public DocObject(String source) {
        this(new DocTokener(source));
    }


    /**
     * Construct a DocObject from a ResourceBundle.
     * @param baseName The ResourceBundle base name.
     * @param locale The Locale to load the ResourceBundle for.
     */
    public DocObject(String baseName, Locale locale) {
        this();
        ResourceBundle r = ResourceBundle.getBundle(baseName, locale, 
                Thread.currentThread().getContextClassLoader());

// Iterate through the keys in the bundle.
        
        Enumeration<?> keys = r.getKeys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            if (key instanceof String) {
    
// Go through the path, ensuring that there is a nested DocObject for each 
// segment except the last. Add the value using the last segment's name into
// the deepest nested DocObject.
                
                String[] path = ((String)key).split("\\.");
                int last = path.length - 1;
                DocObject target = this;
                for (int i = 0; i < last; i += 1) {
                    String segment = path[i];
                    DocObject nextTarget = target.getDocObject(segment);
                    if (nextTarget == null) {
                        nextTarget = new DocObject();
                        target.put(segment, nextTarget);
                    }
                    target = nextTarget;
                }
                target.put(path[last], r.getString((String)key));
            }
        }
    }

    
    /**
     * Accumulate values under a key. It is similar to the put method except
     * that if there is already an object stored under the key then a
     * DocArray is stored under the key to hold all of the accumulated values.
     * If there is already a DocArray, then the new value is appended to it.
     * In contrast, the put method replaces the previous value.
     * @param key   A key string.
     * @param value An object to be accumulated under the key.
     * @return this.
     */
    public DocObject accumulate(String key, Object value) {
        testValidity(value);
        Object object = get(key);
        if (object == null) {
            put(key, value instanceof DocArray ?
                    new DocArray().put(value) : value);
        } else if (object instanceof DocArray) {
            ((DocArray)object).put(value);
        } else {
            put(key, new DocArray().put(object).put(value));
        }
        return this;
    }


    /**
     * Append values to the array under a key. If the key does not exist in the
     * DocObject, then the key is put in the DocObject with its value being a
     * DocArray containing the value parameter. If the key was already
     * associated with a DocArray, then the value parameter is appended to it.
     * @param key   A key string.
     * @param value An object to be accumulated under the key.
     * @return this.
     */
    public DocObject append(String key, Object value) {
        testValidity(value);
        Object object = get(key);
        if (object == null) {
            put(key, new DocArray().put(value));
        } else if (object instanceof DocArray) {
            put(key, ((DocArray)object).put(value));
        } else {
            throw new DocException("DocObject[" + key +
                    "] is not a DocArray.");
        }
        return this;
    }


    /**
     * Produce a string from a double. The string "null" will be returned if
     * the number is not finite.
     * @param  d A double.
     * @return A String.
     */
    public static String doubleToString(double d) {
        if (Double.isInfinite(d) || Double.isNaN(d)) {
            return "null";
        }

// Shave off trailing zeros and decimal point, if possible.

        String string = Double.toString(d);
        if (string.indexOf('.') > 0 && string.indexOf('e') < 0 && 
        		string.indexOf('E') < 0) {
            while (string.endsWith("0")) {
                string = string.substring(0, string.length() - 1);
            }
            if (string.endsWith(".")) {
                string = string.substring(0, string.length() - 1);
            }
        }
        return string;
    }


    /**
     * Determine if the DocObject contains a specific key.
     * @param key   A key string.
     * @return      true if the key exists in the DocObject.
     */
    public boolean has(String key) {
        return this.map.containsKey(key);
    }
    
    
    /**
     * Increment a property of a DocObject. If there is no such property,
     * create one with a value of 1. If there is such a property, and if
     * it is an Integer, Long, Double, or Float, then add one to it.
     * @param key  A key string.
     * @return this.
     */
    public DocObject increment(String key) {
        Object value = get(key);
        if (value == null) {
            put(key, 1);
        } else if (value instanceof Integer) {
            put(key, ((Integer)value).intValue() + 1);
        } else if (value instanceof Long) {
            put(key, ((Long)value).longValue() + 1);                
        } else if (value instanceof Double) {
            put(key, ((Double)value).doubleValue() + 1);                
        } else if (value instanceof Float) {
            put(key, ((Float)value).floatValue() + 1);                
        } else {
            throw new DocException("Unable to increment [" + quote(key) + "].");
        }
        return this;
    }


    /**
     * Determine if the value associated with the key is null or if there is
     *  no value.
     * @param key   A key string.
     * @return      true if there is no value associated with the key or if
     *  the value is the DocObject.NULL object.
     */
    public boolean isNull(String key) {
        return DocObject.NULL.equals(get(key));
    }


    /**
     * Get an enumeration of the keys of the DocObject.
     *
     * @return An iterator of the keys.
     */
    public Iterator<String> keys() {
        return this.map.keySet().iterator();
    }


    /**
     * Get the number of keys stored in the DocObject.
     *
     * @return The number of keys in the DocObject.
     */
    public int length() {
        return this.map.size();
    }

    /**
     * Produce a string from a Number.
     * @param  number A Number
     * @return A String.
     */
    public static String numberToString(Number number) {
        if (number == null) {
            throw new DocException("Null pointer");
        }
        testValidity(number);

// Shave off trailing zeros and decimal point, if possible.

        String string = number.toString();
        if (string.indexOf('.') > 0 && string.indexOf('e') < 0 && 
        		string.indexOf('E') < 0) {
            while (string.endsWith("0")) {
                string = string.substring(0, string.length() - 1);
            }
            if (string.endsWith(".")) {
                string = string.substring(0, string.length() - 1);
            }
        }
        return string;
    }


    /**
     * Get an value associated with a key.
     * @param key   A key string.
     * @return      An object which is the value, or null if there is no value.
     */
    public Object get(String key) {
        return key == null ? null : this.map.get(key);
    }


    /**
     * Get an boolean associated with a key.
     * It returns false if there is no such key, or if the value is not
     * Boolean.TRUE or the String "true".
     *
     * @param key   A key string.
     * @return      The truth.
     */
    public boolean getBoolean(String key) {
    	Object object = get(key);
        if (object.equals(Boolean.FALSE) ||
                (object instanceof String &&
                ((String)object).equalsIgnoreCase("false"))) {
            return false;
        } else if (object.equals(Boolean.TRUE) ||
                (object instanceof String &&
                ((String)object).equalsIgnoreCase("true"))) {
            return true;
        }
        throw new DocException("DocObject[" + quote(key) +
                "] is not a Boolean.");
    }


    /**
     * Get an double associated with a key,
     * or NaN if there is no such key or if its value is not a number.
     * If the value is a string, an attempt will be made to evaluate it as
     * a number.
     *
     * @param key   A string which is the key.
     * @return      An object which is the value.
     */
    public double getDouble(String key) {
    	Object object = get(key);
        try {
            return object instanceof Number ?
                ((Number)object).doubleValue() :
                Double.parseDouble((String)object);
        } catch (Exception e) {
            throw new DocException("DocObject[" + quote(key) +
                "] is not a number.");
        }
    }


    /**
     * Get an int value associated with a key,
     * or zero if there is no such key or if the value is not a number.
     * If the value is a string, an attempt will be made to evaluate it as
     * a number.
     *
     * @param key   A key string.
     * @return      An object which is the value.
     */
    public int getInt(String key) {
    	Object object = get(key);
        try {
            return object instanceof Number ?
                ((Number)object).intValue() :
                Integer.parseInt((String)object);
        } catch (Exception e) {
            throw new DocException("DocObject[" + quote(key) +
                "] is not an int.");
        }
    }


    /**
     * Get an DocArray associated with a key.
     * It returns null if there is no such key, or if its value is not a
     * DocArray.
     *
     * @param key   A key string.
     * @return      A DocArray which is the value.
     */
    public DocArray getDocArray(String key) {
        Object o = get(key);
        return o instanceof DocArray ? (DocArray)o : null;
    }


    /**
     * Get an DocObject associated with a key.
     * It returns null if there is no such key, or if its value is not a
     * DocObject.
     *
     * @param key   A key string.
     * @return      A DocObject which is the value.
     */
    public DocObject getDocObject(String key) {
        Object object = get(key);
        return object instanceof DocObject ? (DocObject)object : null;
    }


    /**
     * Get an long value associated with a key,
     * or zero if there is no such key or if the value is not a number.
     * If the value is a string, an attempt will be made to evaluate it as
     * a number.
     *
     * @param key   A key string.
     * @return      An object which is the value.
     */
    public long getLong(String key) {
    	Object object = get(key);
        try {
            return object instanceof Number ?
                ((Number)object).longValue() :
                Long.parseLong((String)object);
        } catch (Exception e) {
            throw new DocException("DocObject[" + quote(key) +
                "] is not a long.");
        }
    }


    /**
     * Get an string associated with a key.
     * It returns an empty string if there is no such key. If the value is not
     * a string and is not null, then it is converted to a string.
     *
     * @param key   A key string.
     * @return      A string which is the value.
     */
    public String getString(String key) {
    	Object object = get(key);
        return object == NULL ? null : object.toString();
    }


    /**
     * Put a key/boolean pair in the DocObject.
     *
     * @param key   A key string.
     * @param value A boolean which is the value.
     * @return this.
     */
    public DocObject put(String key, boolean value) {
        put(key, value ? Boolean.TRUE : Boolean.FALSE);
        return this;
    }


    /**
     * Put a key/double pair in the DocObject.
     *
     * @param key   A key string.
     * @param value A double which is the value.
     * @return this.
     */
    public DocObject put(String key, double value) {
        put(key, new Double(value));
        return this;
    }


    /**
     * Put a key/int pair in the DocObject.
     *
     * @param key   A key string.
     * @param value An int which is the value.
     * @return this.
     */
    public DocObject put(String key, int value) {
        put(key, new Integer(value));
        return this;
    }


    /**
     * Put a key/long pair in the DocObject.
     *
     * @param key   A key string.
     * @param value A long which is the value.
     * @return this.
     */
    public DocObject put(String key, long value) {
        put(key, new Long(value));
        return this;
    }


    /**
     * Put a key/value pair in the DocObject. If the value is null,
     * then the key will be removed from the DocObject if it is present.
     * @param key   A key string.
     * @param value An object which is the value. It should be of one of these
     *  types: Boolean, Double, Integer, DocArray, DocObject, Long, String,
     *  or the DocObject.NULL object.
     * @return this.
     */
    public DocObject put(String key, Object value) {
        if (key == null) {
            throw new DocException("Null key.");
        }
        if (value != null) {
            testValidity(value);
            this.map.put(key, value);
        } else {
            remove(key);
        }
        return this;
    }


    /**
     * Put a key/value pair in the DocObject, but only if the key and the
     * value are both non-null, and only if there is not already a member
     * with that name.
     * @param key
     * @param value
     * @return his.
     */
    public DocObject putOnce(String key, Object value) {
        if (key != null && value != null) {
            if (get(key) != null) {
                throw new DocException("Duplicate key \"" + key + "\"");
            }
            put(key, value);
        }
        return this;
    }


    /**
     * Put a key/value pair in the DocObject, but only if the
     * key and the value are both non-null.
     * @param key   A key string.
     * @param value An object which is the value. It should be of one of these
     *  types: Boolean, Double, Integer, DocArray, DocObject, Long, String,
     *  or the DocObject.NULL object.
     * @return this.
     */
    public DocObject putOpt(String key, Object value) {
        if (key != null && value != null) {
            put(key, value);
        }
        return this;
    }


    /**
     * Produce a string in double quotes with backslash sequences in all the
     * right places. A backslash will be inserted within </, producing <\/,
     * allowing Doc text to be delivered in HTML. In Doc text, a string 
     * cannot contain a control character or an unescaped quote or backslash.
     * @param string A String
     * @return  A String correctly formatted for insertion in a Doc text.
     */
    public static String quote(String string) {
        if (string == null || string.length() == 0) {
            return "\"\"";
        }

        char         b;
        char         c = 0;
        String       hhhh;
        int          i;
        int          len = string.length();
        StringBuffer sb = new StringBuffer(len + 4);

        sb.append('"');
        for (i = 0; i < len; i += 1) {
            b = c;
            c = string.charAt(i);
            switch (c) {
            case '\\':
            case '"':
                sb.append('\\');
                sb.append(c);
                break;
            case '/':
                if (b == '<') {
                    sb.append('\\');
                }
                sb.append(c);
                break;
            case '\b':
                sb.append("\\b");
                break;
            case '\t':
                sb.append("\\t");
                break;
            case '\n':
                sb.append("\\n");
                break;
            case '\f':
                sb.append("\\f");
                break;
            case '\r':
                sb.append("\\r");
                break;
            default:
                if (c < ' ' || (c >= '\u0080' && c < '\u00a0') ||
                               (c >= '\u2000' && c < '\u2100')) {
                    hhhh = "000" + Integer.toHexString(c);
                    sb.append("\\u" + hhhh.substring(hhhh.length() - 4));
                } else {
                    sb.append(c);
                }
            }
        }
        sb.append('"');
        return sb.toString();
    }

    /**
     * Remove a name and its value, if present.
     * @param key The name to be removed.
     * @return The value that was associated with the name,
     * or null if there was no value.
     */
    public Object remove(String key) {
        return this.map.remove(key);
    }

    /**
     * Get an enumeration of the keys of the DocObject.
     * The keys will be sorted alphabetically.
     *
     * @return An iterator of the keys.
     */
    public Iterator<String> sortedKeys() {
      return new TreeSet<String>(this.map.keySet()).iterator();
    }

    /**
     * Try to convert a string into a number, boolean, or null. If the string
     * can't be converted, return the string.
     * @param string A String.
     * @return A simple Doc value.
     */
    public static Object stringToValue(String string) {
        if (string.equals("")) {
            return string;
        }
        if (string.equalsIgnoreCase("true")) {
            return Boolean.TRUE;
        }
        if (string.equalsIgnoreCase("false")) {
            return Boolean.FALSE;
        }
        if (string.equalsIgnoreCase("null")) {
            return DocObject.NULL;
        }

        /*
         * If it might be a number, try converting it. 
         * We support the non-standard 0x- convention. 
         * If a number cannot be produced, then the value will just
         * be a string. Note that the 0x-, plus, and implied string
         * conventions are non-standard. A Doc parser may accept
         * non-Doc forms as long as it accepts all correct Doc forms.
         */

        char b = string.charAt(0);
        if ((b >= '0' && b <= '9') || b == '.' || b == '-' || b == '+') {
            if (b == '0' && string.length() > 2 &&
                        (string.charAt(1) == 'x' || string.charAt(1) == 'X')) {
                try {
                    return new Integer(Integer.parseInt(string.substring(2), 16));
                } catch (Exception ignore) {
                }
            }
            try {
                if (string.indexOf('.') > -1 || 
                        string.indexOf('e') > -1 || string.indexOf('E') > -1) {
                    return Double.valueOf(string);
                } else {
                    Long myLong = new Long(string);
                    if (myLong.longValue() == myLong.intValue()) {
                        return new Integer(myLong.intValue());
                    } else {
                        return myLong;
                    }
                }
            }  catch (Exception ignore) {
            }
        }
        return string;
    }


    /**
     * Throw an exception if the object is a NaN or infinite number.
     * @param o The object to test.
     */
    public static void testValidity(Object o) {
        if (o != null) {
            if (o instanceof Double) {
                if (((Double)o).isInfinite() || ((Double)o).isNaN()) {
                    throw new DocException(
                        "Doc does not allow non-finite numbers.");
                }
            } else if (o instanceof Float) {
                if (((Float)o).isInfinite() || ((Float)o).isNaN()) {
                    throw new DocException(
                        "Doc does not allow non-finite numbers.");
                }
            }
        }
    }


    /**
     * Produce a DocArray containing the values of the members of this
     * DocObject.
     * @param names A DocArray containing a list of key strings. This
     * determines the sequence of the values in the result.
     * @return A DocArray of values.
     */
    public DocArray toDocArray(DocArray names) {
        if (names == null || names.length() == 0) {
            return null;
        }
        DocArray ja = new DocArray();
        for (int i = 0; i < names.length(); i += 1) {
            ja.put(this.get(names.getString(i)));
        }
        return ja;
    }

    /**
     * Make a Doc text of this DocObject. For compactness, no whitespace
     * is added. If this would not result in a syntactically correct Doc text,
     * then null will be returned instead.
     * <p>
     * Warning: This method assumes that the data structure is acyclical.
     *
     * @return a printable, displayable, portable, transmittable
     *  representation of the object, beginning
     *  with <code>{</code>&nbsp;<small>(left brace)</small> and ending
     *  with <code>}</code>&nbsp;<small>(right brace)</small>.
     */
    public String toString() {
        try {
            Iterator<String>     keys = keys();
            StringBuffer sb = new StringBuffer("{");

            while (keys.hasNext()) {
                if (sb.length() > 1) {
                    sb.append(',');
                }
                Object o = keys.next();
                sb.append(quote(o.toString()));
                sb.append(':');
                sb.append(valueToString(this.map.get(o)));
            }
            sb.append('}');
            return sb.toString();
        } catch (Exception e) {
            return null;
        }
    }


    /**
     * Make a prettyprinted Doc text of this DocObject.
     * <p>
     * Warning: This method assumes that the data structure is acyclical.
     * @param indentFactor The number of spaces to add to each level of
     *  indentation.
     * @return a printable, displayable, portable, transmittable
     *  representation of the object, beginning
     *  with <code>{</code>&nbsp;<small>(left brace)</small> and ending
     *  with <code>}</code>&nbsp;<small>(right brace)</small>.
     */
    public String toString(int indentFactor) {
        return toString(indentFactor, 0);
    }


    /**
     * Make a prettyprinted Doc text of this DocObject.
     * <p>
     * Warning: This method assumes that the data structure is acyclical.
     * @param indentFactor The number of spaces to add to each level of
     *  indentation.
     * @param indent The indentation of the top level.
     * @return a printable, displayable, transmittable
     *  representation of the object, beginning
     *  with <code>{</code>&nbsp;<small>(left brace)</small> and ending
     *  with <code>}</code>&nbsp;<small>(right brace)</small>.
     */
    String toString(int indentFactor, int indent) {
        int i;
        int length = this.length();
        if (length == 0) {
            return "{}";
        }
        Iterator<String>     keys = sortedKeys();
        int          newindent = indent + indentFactor;
        Object       object;
        StringBuffer sb = new StringBuffer("{");
        if (length == 1) {
            object = keys.next();
            sb.append(quote(object.toString()));
            sb.append(": ");
            sb.append(valueToString(this.map.get(object), indentFactor,
                    indent));
        } else {
            while (keys.hasNext()) {
                object = keys.next();
                if (sb.length() > 1) {
                    sb.append(",\n");
                } else {
                    sb.append('\n');
                }
                for (i = 0; i < newindent; i += 1) {
                    sb.append(' ');
                }
                sb.append(quote(object.toString()));
                sb.append(": ");
                sb.append(valueToString(this.map.get(object), indentFactor,
                        newindent));
            }
            if (sb.length() > 1) {
                sb.append('\n');
                for (i = 0; i < indent; i += 1) {
                    sb.append(' ');
                }
            }
        }
        sb.append('}');
        return sb.toString();
    }

    public DocObject merge(DocObject otherDoc) {
    	Iterator<String> it = otherDoc.keys();
    	while (it.hasNext()) {
    		String otherKey = it.next();
    		Object otherValue = otherDoc.get(otherKey);
    		Object value = this.get(otherKey);
    		if (value != null 
    				&& value instanceof DocObject
    				&& otherValue instanceof DocObject) {
    			DocObject valueObj = (DocObject) value;
    			DocObject otherValueObj = (DocObject) otherValue;
    			this.put(otherKey, valueObj.merge(otherValueObj));
    		} else {
    			this.put(otherKey, otherValue);
    		}
    	}
    	return this;
    };

    /**
     * Make a Doc text of an Object value. If the object has an
     * value.toDocString() method, then that method will be used to produce
     * the Doc text. The method is required to produce a strictly
     * conforming text. If the object does not contain a toDocString
     * method (which is the most common case), then a text will be
     * produced by other means. If the value is an array or Collection,
     * then a DocArray will be made from it and its toDocString method
     * will be called. If the value is a MAP, then a DocObject will be made
     * from it and its toDocString method will be called. Otherwise, the
     * value's toString method will be called, and the result will be quoted.
     *
     * <p>
     * Warning: This method assumes that the data structure is acyclical.
     * @param value The value to be serialized.
     * @return a printable, displayable, transmittable
     *  representation of the object, beginning
     *  with <code>{</code>&nbsp;<small>(left brace)</small> and ending
     *  with <code>}</code>&nbsp;<small>(right brace)</small>.
     */
	public static String valueToString(Object value) {
        if (value == null || value.equals(null)) {
            return "null";
        }
        if (value instanceof DocString) {
            Object object;
            try {
                object = ((DocString)value).toDocString();
            } catch (Exception e) {
                throw new DocException(e);
            }
            if (object instanceof String) {
                return (String)object;
            }
            throw new DocException("Bad value from toDocString: " + object);
        }
        if (value instanceof Number) {
            return numberToString((Number) value);
        }
        if (value instanceof Boolean || value instanceof DocObject ||
                value instanceof DocArray) {
            return value.toString();
        }
        return quote(value.toString());
    }


    /**
     * Make a prettyprinted Doc text of an object value.
     * <p>
     * Warning: This method assumes that the data structure is acyclical.
     * @param value The value to be serialized.
     * @param indentFactor The number of spaces to add to each level of
     *  indentation.
     * @param indent The indentation of the top level.
     * @return a printable, displayable, transmittable
     *  representation of the object, beginning
     *  with <code>{</code>&nbsp;<small>(left brace)</small> and ending
     *  with <code>}</code>&nbsp;<small>(right brace)</small>.
     */
	static String valueToString(Object value, int indentFactor, int indent) {
        if (value == null || value.equals(null)) {
            return "null";
        }
        try {
            if (value instanceof DocString) {
                Object o = ((DocString)value).toDocString();
                if (o instanceof String) {
                    return (String)o;
                }
            }
        } catch (Exception ignore) {
        }
        if (value instanceof Number) {
            return numberToString((Number) value);
        }
        if (value instanceof Boolean) {
            return value.toString();
        }
        if (value instanceof DocObject) {
            return ((DocObject)value).toString(indentFactor, indent);
        }
        if (value instanceof DocArray) {
            return ((DocArray)value).toString(indentFactor, indent);
        }
        return quote(value.toString());
    }

}