/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork.portlet.dispatcher;

import java.io.Serializable;

import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.portlet.PortletRequest;


/**
 * A simple implementation of the {@link java.util.Map} interface to handle a collection of request attributes.
 *
 * @author Patrick Lightbody
 * @author Bill Lynch (docs)
 */
public class PortletRequestMap extends AbstractMap implements Serializable {
    //~ Instance fields ////////////////////////////////////////////////////////

    Set entries;
    private PortletRequest request;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     * Saves the request to use as the backing for getting and setting values
     *
     * @param request the http servlet request.
     */
    public PortletRequestMap(final PortletRequest request) {
        this.request = request;
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Removes all attributes from the request as well as clears entries in this map.
     */
    public void clear() {
        Enumeration keys = request.getAttributeNames();

        while (keys.hasMoreElements()) {
            String key = (String) keys.nextElement();
            request.removeAttribute(key);
        }
    }

    /**
     * Returns a Set of attributes from the http request.
     *
     * @return a Set of attributes from the http request.
     */
    public Set entrySet() {
        if (entries == null) {
            entries = new HashSet();

            Enumeration enumeration = request.getAttributeNames();

            while (enumeration.hasMoreElements()) {
                final String key = enumeration.nextElement().toString();
                final Object value = request.getAttribute(key);
                entries.add(new Entry() {
                        public boolean equals(Object obj) {
                            Entry entry = (Entry) obj;

                            return ((key == null) ? (entry.getKey() == null) : key.equals(entry.getKey())) && ((value == null) ? (entry.getValue() == null) : value.equals(entry.getValue()));
                        }

                        public int hashCode() {
                            return ((key == null) ? 0 : key.hashCode()) ^ ((value == null) ? 0 : value.hashCode());
                        }

                        public Object getKey() {
                            return key;
                        }

                        public Object getValue() {
                            return value;
                        }

                        public Object setValue(Object obj) {
                            request.setAttribute(key.toString(), obj);

                            return value;
                        }
                    });
            }
        }

        return entries;
    }

    /**
     * Returns the request attribute associated with the given key or <tt>null</tt> if it doesn't exist.
     *
     * @param key the name of the request attribute.
     * @return the request attribute or <tt>null</tt> if it doesn't exist.
     */
    public Object get(Object key) {
        return request.getAttribute(key.toString());
    }

    /**
     * Saves an attribute in the request.
     *
     * @param key   the name of the request attribute.
     * @param value the value to set.
     * @return the object that was just set.
     */
    public Object put(Object key, Object value) {
        entries = null;
        request.setAttribute(key.toString(), value);

        return get(key);
    }

    /**
     * Removes the specified request attribute.
     *
     * @param key the name of the attribute to remove.
     * @return the value that was removed or <tt>null</tt> if the value was not found (and hence, not removed).
     */
    public Object remove(Object key) {
        entries = null;

        Object value = get(key);
        request.removeAttribute(key.toString());

        return value;
    }
}
