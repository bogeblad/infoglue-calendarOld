/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork.portlet.dispatcher;

import java.io.Serializable;

import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoglue.calendar.actions.UpdateEventTypeAction;


/**
 * A simple implementation of the {@link java.util.Map} interface to handle a collection of HTTP session
 * attributes. The {@link #entrySet()} method enumerates over all session attributes and creates a Set of entries.
 * Note, this will occur lazily - only when the entry set is asked for.
 *
 * @author <a href="mailto:rickard@middleware-company.com">Rickard Öberg</a>
 * @author Bill Lynch (docs)
 */
public class PortletSessionMap extends AbstractMap implements Serializable 
{
	private static Log log = LogFactory.getLog(PortletSessionMap.class);

    //~ Instance fields ////////////////////////////////////////////////////////

    PortletSession session;
    Set entries;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     * Creates a new session map given a http servlet request. Note, ths enumeration of request
     * attributes will occur when the map entries are asked for.
     *
     * @param request the http servlet request object.
     */
    public PortletSessionMap(PortletRequest request) 
    {
        this.session = request.getPortletSession();
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Removes all attributes from the session as well as clears entries in this map.
     */
    public void clear() {
        synchronized (session) {
            entries = null;
            session.invalidate();
        }
    }

    /**
     * Returns a Set of attributes from the http session.
     *
     * @return a Set of attributes from the http session.
     */
    public Set entrySet() 
    {
        synchronized (session) 
        {
        	log.debug("Session:" + session);
        	log.debug("Session:" + session.getAttribute("infogluePrincipal"));
        	log.debug("Session entries:" + entries);
            if (entries == null) 
            {
                entries = new HashSet();

                log.debug("Session:" + session.getAttribute("infogluePrincipal"));
                Enumeration enumeration = session.getAttributeNames();
                log.debug("enumeration:" + enumeration.hasMoreElements());
                
                while (enumeration.hasMoreElements()) 
                {
                    final String key = enumeration.nextElement().toString();
                    log.debug("key:" + key);
                    final Object value = session.getAttribute(key);
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
                                session.setAttribute(key.toString(), obj);

                                return value;
                            }
                        });
                }
            }
        }

        return entries;
    }

    /**
     * Returns the session attribute associated with the given key or <tt>null</tt> if it doesn't exist.
     *
     * @param key the name of the session attribute.
     * @return the session attribute or <tt>null</tt> if it doesn't exist.
     */
    public Object get(Object key) {
        synchronized (session) {
            return session.getAttribute(key.toString());
        }
    }

    /**
     * Saves an attribute in the session.
     *
     * @param key   the name of the session attribute.
     * @param value the value to set.
     * @return the object that was just set.
     */
    public Object put(Object key, Object value) {
        synchronized (session) {
            entries = null;
            session.setAttribute(key.toString(), value);

            return get(key);
        }
    }

    /**
     * Removes the specified session attribute.
     *
     * @param key the name of the attribute to remove.
     * @return the value that was removed or <tt>null</tt> if the value was not found (and hence, not removed).
     */
    public Object remove(Object key) {
        synchronized (session) {
            entries = null;

            Object value = get(key);
            session.removeAttribute(key.toString());

            return value;
        }
    }
}
