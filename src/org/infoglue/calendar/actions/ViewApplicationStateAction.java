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

package org.infoglue.calendar.actions;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Category;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.infoglue.calendar.controllers.CategoryController;
import org.infoglue.calendar.controllers.EventController;
import org.infoglue.calendar.controllers.LanguageController;
import org.infoglue.calendar.entities.Event;
import org.infoglue.calendar.entities.EventVersion;
import org.infoglue.calendar.entities.Language;
import org.infoglue.common.util.CacheController;

import com.opensymphony.webwork.ServletActionContext;
import com.opensymphony.xwork.ActionContext;

/**
 * This is the action that shows the application state and also can be used to set up surveilence.
 * The idea is to have one command which allways returns a known resultpage if it's ok. Otherwise it prints
 * an error-statement. This action is then called every x minutes by the surveilence and an alarm is raised if something is wrong.
 * We also have a command which can list more status about the application.
 *
 * @author Mattias Bogeblad
 */

public class ViewApplicationStateAction extends CalendarAbstractAction 
{
	private static Log log = LogFactory.getLog(ViewApplicationStateAction.class);

    private List states 					= new ArrayList();
    
	private boolean databaseConnectionOk 	= false;
	private boolean applicationSettingsOk 	= false;
	private boolean testQueriesOk			= false;
	private boolean diskPermissionOk 		= false;
	
	private String cacheName				= "";
	private String message					= "";

	private String className				= "";
	private String logLevel					= "";

	/**
	 * The constructor for this action - contains nothing right now.
	 */
    
    public ViewApplicationStateAction() 
    {
	
    }
    
    private Category getDeliverCategory()
    {
        Enumeration enumeration = Logger.getCurrentCategories();
        while(enumeration.hasMoreElements())
        {
            Category category = (Category)enumeration.nextElement();
            if(category.getName().equalsIgnoreCase("org.infoglue.deliver"))
                return category;
        }
        
        return null;
    }

    private Category getCastorJDOCategory()
    {
        Enumeration enumeration = Logger.getCurrentCategories();
        while(enumeration.hasMoreElements())
        {
            Category category = (Category)enumeration.nextElement();
            if(category.getName().equalsIgnoreCase("org.exolab.castor.jdo"))
                return category;
        }
        
        return null;
    }

    private Category getCategory(String className)
    {
        Enumeration enumeration = Logger.getCurrentCategories();
        while(enumeration.hasMoreElements())
        {
            Category category = (Category)enumeration.nextElement();
            if(category.getName().equalsIgnoreCase(className))
                return category;
        }
        
        Category category = Category.getInstance(className);
       
        return category;
    }

    /**
     * This action allows clearing of the given cache manually.
     */
    public String doClearCache() throws Exception
    {
        
        return "cleared";
    }

    /**
     * This action allows clearing of the given cache manually.
     */
    public String doClearAllCaches() throws Exception
    {
        CacheController.clearCaches();
        
    	Session session = this.getSession();
    	
    	session.getSessionFactory().evictQueries();
    	session.getSessionFactory().evict(org.infoglue.calendar.entities.Calendar.class);
    	session.getSessionFactory().evict(org.infoglue.calendar.entities.Category.class);
    	session.getSessionFactory().evict(org.infoglue.calendar.entities.Event.class);
    	session.getSessionFactory().evict(org.infoglue.calendar.entities.EventType.class);
    	session.getSessionFactory().evict(org.infoglue.calendar.entities.EventTypeCategoryAttribute.class);
    	session.getSessionFactory().evict(org.infoglue.calendar.entities.EventCategory.class);
    	session.getSessionFactory().evict(org.infoglue.calendar.entities.Location.class);
    	session.getSessionFactory().evict(org.infoglue.calendar.entities.Participant.class);
    	session.getSessionFactory().evict(org.infoglue.calendar.entities.Resource.class);
    	session.getSessionFactory().evict(org.infoglue.calendar.entities.Entry.class);
    	session.getSessionFactory().evict(org.infoglue.calendar.entities.Role.class);
    	session.getSessionFactory().evict(org.infoglue.calendar.entities.Group.class);
    	session.getSessionFactory().evict(org.infoglue.calendar.entities.Subscriber.class);

        return "cleared";
    }

    /**
     * This action allows setting of the loglevel on any class.
     */
    public String logLevel() throws Exception
    {
    	log.debug("Setting loglevel....");
    	
    	Level newLevel = Level.ERROR;
    	if(this.logLevel.equalsIgnoreCase("debug"))
    		newLevel = Level.DEBUG;
    	if(this.logLevel.equalsIgnoreCase("info"))
    		newLevel = Level.INFO;
    	else if(this.logLevel.equalsIgnoreCase("warn"))
    		newLevel = Level.WARN;
    	else if(this.logLevel.equalsIgnoreCase("error"))
    		newLevel = Level.ERROR;
    	
    	Category category = getCategory(this.className);
    	if(category != null)
    		category.setLevel(newLevel);
        
    	log.debug("Setting loglevel end....");
    	
        return "cleared";
    }


    /**
     * This action allows clearing of the caches manually.
     */
    public String doClearCaches() throws Exception
    {
    	CacheController.clearCache("groupsCache");
    	CacheController.clearCache("groupsCache");
 		CacheController.clearCache("principalsCache");

 		return "cleared";
    }

    /**
     * This action allows clearing of the caches manually.
     */
    public String doFixInconsistencies() throws Exception
    {
    	int fixed = CategoryController.getController().clearBrokenCategoryReferences(getSession());
    	ServletActionContext.getRequest().getSession().setAttribute("message", "Number of fixed inconsistencies: " + fixed);
    	ActionContext.getContext().getValueStack().getContext().put("message", "Number of fixed inconsistencies: " + fixed);
    	
 		return "cleared";
    }
    
    /**
     * This action allows upgrade of model from a versionless situation to a version based event handling.
     */
    public String doUpgradeModel() throws Exception
    {
    	/*
        Language language = LanguageController.getController().getMasterLanguage(getSession());

        List events = EventController.getController().getEventList(getSession());
        Iterator eventsIterator = events.iterator();
        while(eventsIterator.hasNext())
        {
        	Event event = (Event)eventsIterator.next();
        	if(event.getVersions().size() == 0)
        	{
        		EventVersion eventVersion = new EventVersion();
            	eventVersion.setLanguage(language);
            	eventVersion.setEvent(event);
            	eventVersion.setName(event.getName());
            	eventVersion.setDescription(event.getDescription());
                eventVersion.setOrganizerName(event.getOrganizerName());
                eventVersion.setLecturer(event.getLecturer());
                eventVersion.setCustomLocation(event.getCustomLocation());
                eventVersion.setAlternativeLocation(event.getAlternativeLocation());
                eventVersion.setShortDescription(event.getShortDescription());
                eventVersion.setLongDescription(event.getLongDescription());
                eventVersion.setEventUrl(event.getEventUrl());
                //eventVersion.setContactName(event.getContactName());
                //eventVersion.setContactEmail(event.getContactEmail());
                //eventVersion.setContactPhone(event.getContactPhone());
                //eventVersion.setPrice(event.getPrice());
                eventVersion.setAttributes(event.getAttributes());
                
            	getSession().save(eventVersion);
        	}
        	
        	getSession().update(event);
        }
        */
    	
    	return "success";
    }


    
    private List getList(String key, String value)
    {
        List list = new ArrayList();
        list.add(key);
        list.add(value);

        return list;
    }
    
    
    /**
     * This method is the application entry-point. The method does a lot of checks to see if infoglue
     * is installed correctly and if all resources needed are available.
     */
         
    public String doExecute() throws Exception
    {
        long start = System.currentTimeMillis();
        
        return "success";
    }
        
	public boolean getIsApplicationSettingsOk()
	{
		return applicationSettingsOk;
	}

	public boolean getIsDatabaseConnectionOk()
	{
		return databaseConnectionOk;
	}

	public boolean getIsDiskPermissionOk()
	{
		return diskPermissionOk;
	}

	public boolean getIsTestQueriesOk()
	{
		return testQueriesOk;
	}

    public List getStates()
    {
        return states;
    }
    
    public void setCacheName(String cacheName)
    {
        this.cacheName = cacheName;
    }
    
    public String getServerName()
    {
    	String serverName = "Unknown";

    	try
    	{
		    InetAddress localhost = InetAddress.getLocalHost();
		    serverName = localhost.getHostName();
    	}
    	catch(Exception e)
    	{
    		
    	}
    	
	    return serverName;
    }

	public void setClassName(String className) {
		this.className = className;
	}

	public void setLogLevel(String logLevel) {
		this.logLevel = logLevel;
	}
	
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
