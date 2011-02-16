/* ===============================================================================
*
* Part of the InfoGlue Content Management Platform (www.infoglue.org)
*
* ===============================================================================
*
*  Copyright (C)
* 
* This program is free software; you can redistribute it and/or modify it under
* the terms of the GNU General Public License version 2, as published by the
* Free Software Foundation. See the file LICENSE.html for more information.
* 
* This program is distributed in the hope that it will be useful, but WITHOUT
* ANY WARRANTY, including the implied warranty of MERCHANTABILITY or FITNESS
* FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
* 
* You should have received a copy of the GNU General Public License along with
* this program; if not, write to the Free Software Foundation, Inc. / 59 Temple
* Place, Suite 330 / Boston, MA 02111-1307 / USA.
*
* ===============================================================================
*/

package org.infoglue.calendar.controllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.infoglue.calendar.actions.CalendarAbstractAction;
import org.infoglue.calendar.entities.Event;
import org.infoglue.calendar.entities.CalendarProperty;
import org.infoglue.common.settings.controllers.SettingsController;
import org.infoglue.common.settings.controllers.SettingsPersister;
import org.infoglue.common.settings.entities.Property;
import org.infoglue.common.settings.controllers.SettingsPersister;
import org.infoglue.common.util.PropertyHelper;
import org.infoglue.common.util.ResourceBundleHelper;
import org.infoglue.common.util.dom.DOMBuilder;

import com.opensymphony.xwork.ActionContext;

public class CalendarSettingsController implements SettingsPersister
{
	private static Log log = LogFactory.getLog(CalendarSettingsController.class);

	private DOMBuilder domBuilder = new DOMBuilder();
	private SettingsController settingsController = null;
	
	/**
	 * A simple factory method
	 */
	
	public static CalendarSettingsController getCalendarSettingsController()
	{
		return new CalendarSettingsController();
	}
	
	private CalendarSettingsController()
	{
		this.settingsController = SettingsController.getController(this);
	}

	/**
	 * This method returns a list (of strings) of all label-keys the system uses.
	 */
	
	public List getSystemSettings(String bundleName)
	{
		return settingsController.getSystemSettings(bundleName);
	}

	/**
	 * This method returns a list (of locales) of all defined label-languages.
	 */
	
	public List getSettingsVariations(String nameSpace, Session session) throws Exception
	{
		return settingsController.getSettingsVariations(nameSpace, session);
	}

	private Document getPropertyDocument(String nameSpace, Session session) throws Exception
	{
		return settingsController.getPropertyDocument(nameSpace, session);
	}
	
	public void addVariation(String nameSpace, String languageCode, Session session) throws Exception
	{
		settingsController.addVariation(nameSpace, languageCode, session);
	}

	public void updateSettings(String nameSpace, String variationId, Map properties, Session session) throws Exception
	{
		settingsController.updateSettings(nameSpace, variationId, properties, session);
	}

	public String getSetting(String nameSpace, String derivedValue, String variationId, Session session) throws Exception
	{
		return settingsController.getSetting(nameSpace, derivedValue, variationId, session);
	}

    /**
     * This method returns a Property based on it's primary key inside a transaction
     * @return Property
     * @throws Exception
     */

	public Property getProperty(Long id, Session session) throws Exception
    {
		Property property = (Property)session.load(CalendarProperty.class, id);
		
		return property;
    }
    
    
    /**
     * Gets a list of all events available for a particular day.
     * @return List of Event
     * @throws Exception
     */
    
    public Property getProperty(String nameSpace, String name, Session session) throws Exception 
    {
        Property property = null;
        
        List properties = session.createQuery("from CalendarProperty as property where property.nameSpace = ? AND property.name = ?").setString(0, nameSpace).setString(1, name).list();
        if(properties != null && properties.size() > 0)
        	property = (Property)properties.get(0);
        
        return property;
    }

    
    /**
     * This method is used to create a new Property object in the database inside a transaction.
     */
    
    public Property createProperty(String nameSpace, String name, String value, Session session) throws Exception 
    {
    	log.debug("Creating property with:" + nameSpace + ":" + name);
        Property property = new CalendarProperty();
        property.setNameSpace(nameSpace);
        property.setName(name);
        property.setValue(value);
        
        session.save(property);
        
        return property;
    }
    
    
    /**
     * Updates an property.
     * 
     * @throws Exception
     */
    
    public void updateProperty(String nameSpace, String name, String value, Session session) throws Exception 
    {
		Property property = getProperty(nameSpace, name, session);
		updateProperty(property, value, session);
    }
    
    /**
     * Updates an property inside an transaction.
     * 
     * @throws Exception
     */
    
    public void updateProperty(Property property, String value, Session session) throws Exception 
    {
        property.setValue(value);
	
		session.update(property);
	}


    public String getLabel(String key, String variationId, boolean skipProperty, boolean fallbackToDefault, boolean fallbackToKey, Session session)
    {
    	String label = "";
    	if(fallbackToKey)
    		label = key;
	    
	    try
	    {
	    	Object derivedObject = findOnValueStack(key);
	        String derivedValue = null;
	        if(derivedObject != null)
	        	derivedValue = derivedObject.toString();
	        
	        if(!skipProperty)
	        {
		        if(derivedValue != null)
		    	    label = CalendarSettingsController.getCalendarSettingsController().getSetting("infoglueCalendar", derivedValue, variationId, session);
		        else
		    	    label = CalendarSettingsController.getCalendarSettingsController().getSetting("infoglueCalendar", key, variationId, session);
	        }
	        
	        if(skipProperty || ((label == null || label.equals("")) && fallbackToDefault))
	        {
		    	if(derivedValue != null)
		        	label = PropertyHelper.getProperty(derivedValue);
		        else
		        	label = PropertyHelper.getProperty(key);
	        }
	        
	        if((label == null || label.equals("")) && fallbackToKey)
	            label = key;
	    }
	    catch(Exception e)
	    {
	        log.info("An label was not found for key: " + key + ": " + e.getMessage(), e);
	    }
	    
	    return label;
    }

    public static Object findOnValueStack(String expr) 
    {
		ActionContext a = ActionContext.getContext();
		Object value = a.getValueStack().findValue(expr);
		return value;
	}

}
