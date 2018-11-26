/**
 *
 */
package com.yonyou.i18n.model;

import java.util.*;

/**
 * @author wenfa
 */
public class OrderedProperties extends Properties {

    private static final long serialVersionUID = -4627607243846121965L;

    private final LinkedHashSet<Object> keys = new LinkedHashSet<Object>();

    public Enumeration<Object> keys() {
        return Collections.<Object>enumeration(keys);
    }

    public Object put(Object key, Object value) {
        keys.add(key);
        return super.put(key, value);
    }

    public Set<Object> keySet() {
        return keys;
    }

    public Set<String> stringPropertyNames() {
        Set<String> set = new LinkedHashSet<String>();

        for (Object key : this.keys) {
            set.add((String) key);
        }

        return set;
    }

    public void add(OrderedProperties op) {
        for (String key : op.stringPropertyNames()){
            this.put(key, op.get(key));
        }
    }

}
