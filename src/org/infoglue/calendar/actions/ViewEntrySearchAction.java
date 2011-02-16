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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.infoglue.calendar.controllers.CalendarController;
import org.infoglue.calendar.controllers.CategoryController;
import org.infoglue.calendar.controllers.ContentTypeDefinitionController;
import org.infoglue.calendar.controllers.EntryController;
import org.infoglue.calendar.controllers.EventController;
import org.infoglue.calendar.controllers.EventTypeCategoryAttributeController;
import org.infoglue.calendar.controllers.EventTypeController;
import org.infoglue.calendar.controllers.LocationController;
import org.infoglue.calendar.controllers.ResourceController;
import org.infoglue.calendar.entities.Entry;
import org.infoglue.calendar.entities.Event;
import org.infoglue.calendar.entities.EventType;
import org.infoglue.calendar.entities.EventVersion;
import org.infoglue.calendar.entities.Location;
import org.infoglue.calendar.util.EntrySearchResultfilesConstructor;
import org.infoglue.common.contenttypeeditor.entities.ContentTypeAttribute;
import org.infoglue.common.util.PropertyHelper;

import com.opensymphony.webwork.ServletActionContext;
import com.opensymphony.xwork.Action;

/**
 * This action represents a Location Administration screen.
 * 
 * @author Mattias Bogeblad
 */

public class ViewEntrySearchAction extends CalendarAbstractAction
{
    private static Log log = LogFactory.getLog(ViewEntrySearchAction.class);

    private Long[] searchEventId;
    private String searchFirstName;
    private String searchLastName;
    private String searchEmail;
    private boolean onlyFutureEvents = true;
    private String[] categoryId;
    private String[] locationId;
    
    private String searchHashCode = "";
    
    private Set eventList;
    private List categoryList;
    private List locationList;
    private List categoryAttributes;
    
    private String andSearch = "false";
    
    private Set entries;
    //private String emailAddresses = "";
    //private String emailEntryIds = "";

	private Map searchResultFiles;
	private List resultValues = new LinkedList(); 
    
    private Map categoryAttributesMap = new HashMap();

    private void initialize(Session session) throws Exception
    {
    	this.eventList = EventController.getController().getPublishedEventList(this.getInfoGlueRemoteUser(), this.getInfoGlueRemoteUserRoles(), this.getInfoGlueRemoteUserGroups(), null, session);
        this.categoryList = CategoryController.getController().getRootCategoryList(session);
        this.locationList = LocationController.getController().getLocationList(session);
        this.categoryAttributes = EventTypeCategoryAttributeController.getController().getEventTypeCategoryAttributeList(session);
        log.debug("calendars:" + categoryAttributes.size());
		String entryResultValues = PropertyHelper.getProperty("entryResultsValues");
        StringTokenizer st = new StringTokenizer( entryResultValues, ",", false );
        while( st.hasMoreTokens() ) 
        {
        	String resultValue = st.nextToken();
        	resultValues.add( resultValue );
        }
    }
    
    /**
     * This is the entry point for the main listing.
     */
    
    public String execute() throws Exception
    {
    	Session dbSession = getSession(true);
    	
        initialize(dbSession);

        if(this.searchHashCode != null && !this.searchHashCode.equals(""))
        {
        	log.debug("Getting search args from session..." + this.searchHashCode);
        	HttpSession session = ServletActionContext.getRequest().getSession();

            this.searchFirstName = (String)session.getAttribute("request_" + searchHashCode + "_searchFirstName");
            this.searchLastName = (String)session.getAttribute("request_" + searchHashCode + "_searchLastName");
            this.searchEmail = (String)session.getAttribute("request_" + searchHashCode + "_searchEmail");
            this.onlyFutureEvents = Boolean.parseBoolean((String)session.getAttribute("request_" + searchHashCode + "_onlyFutureEvents"));
            this.searchEventId = (Long[])session.getAttribute("request_" + searchHashCode + "_searchEventId");
            this.categoryAttributesMap = (Map)session.getAttribute("request_" + searchHashCode + "_categoryAttributesMap");
            this.andSearch = (String)session.getAttribute("request_" + searchHashCode + "_andSearch");
            this.locationId = (String[])session.getAttribute("request_" + searchHashCode + "_locationId");
        }
        else
        {
        	log.debug("Getting search args from request...");
        	
	        int i = 0;
	        String idKey = ServletActionContext.getRequest().getParameter("categoryAttributeId_" + i);
	        log.info("idKey:" + idKey);
	        while(idKey != null && idKey.length() > 0)
	        {
	            String[] categoryIds = ServletActionContext.getRequest().getParameterValues("categoryAttribute_" + idKey + "_categoryId");
	            log.info("categoryIds:" + categoryIds);
	            if(categoryIds != null)
	            {
		            Long[] categoryIdsLong = new Long[categoryIds.length];
		            for(int j=0; j<categoryIds.length; j++)
		            	categoryIdsLong[j] = new Long(categoryIds[j]);
		            
		            categoryAttributesMap.put(idKey, categoryIdsLong);
	            }
	            
	            i++;
	            idKey = ServletActionContext.getRequest().getParameter("categoryAttributeId_" + i);
	            log.info("idKey:" + idKey);
	        }
	        log.info("searchEventId:::::" + this.searchEventId);
	        log.info("andSearch:" + this.andSearch);
	        
	        this.andSearch = ServletActionContext.getRequest().getParameter("andSearch");
	        log.info("andSearch:" + andSearch);
        }
        
        this.entries = EntryController.getController().getEntryList(this.getInfoGlueRemoteUser(), 
        															this.getInfoGlueRemoteUserRoles(), 
        															this.getInfoGlueRemoteUserGroups(),
        															searchFirstName, 
                													searchLastName, 
                													searchEmail,
                													onlyFutureEvents,
                													searchEventId, 
                													categoryAttributesMap,
                													Boolean.parseBoolean(andSearch),
                													locationId,
                													dbSession);

        List<Event> events = new ArrayList<Event>();
        String eventName = "";
        
        String emailAddresses = "";
        Long entryTypeId = null;
        Iterator entriesIterator = entries.iterator();
        while(entriesIterator.hasNext())
        {
        	Entry entry = (Entry)entriesIterator.next();
        	if(!events.contains(entry.getEvent()))
        	{
        		String name = entry.getEvent().getLocalizedName(this.getLanguageCode(), "sv");
        		if(name == null)
        		{
        			EventVersion eventVersion = this.getEventVersion(entry.getEvent());
        			if(eventVersion != null)
        				name = eventVersion.getLocalizedName(this.getLanguageCode(), "sv");
        		}
        		
        		eventName += (eventName.equals("") ? "" : ", ") + name;
        		events.add(entry.getEvent());
        	}
        		        	
        	if(entryTypeId == null)
        		entryTypeId = entry.getEvent().getEntryFormId();
        	
            if(emailAddresses.length() != 0)
                emailAddresses += ";" + entry.getEmail();
            else
                emailAddresses += entry.getEmail();
        }
        
        HttpSession session = ServletActionContext.getRequest().getSession();
        
        this.searchHashCode = "" + ServletActionContext.getRequest().hashCode();
        log.debug("searchHashCode:" + searchHashCode);
        session.setAttribute("request_" + searchHashCode + "_emailAddresses", emailAddresses);
        
        session.setAttribute("request_" + searchHashCode + "_searchFirstName", searchFirstName);
        session.setAttribute("request_" + searchHashCode + "_searchLastName", searchLastName);
        session.setAttribute("request_" + searchHashCode + "_searchEmail", searchEmail);
        session.setAttribute("request_" + searchHashCode + "_onlyFutureEvents", "" + onlyFutureEvents);
        session.setAttribute("request_" + searchHashCode + "_searchEventId", searchEventId);
        session.setAttribute("request_" + searchHashCode + "_categoryAttributesMap", categoryAttributesMap);
        session.setAttribute("request_" + searchHashCode + "_andSearch", andSearch);
        session.setAttribute("request_" + searchHashCode + "_locationId", locationId);

        
        // should we create result files?
        boolean exportEntryResults = PropertyHelper.getBooleanProperty("exportEntryResults");
        if( entries.size() > 0 && exportEntryResults ) 
        {
        	Map parameters = new HashMap();
        	parameters.put("headLine", "Entries found for: " + eventName);

        	EventType eventType = EventTypeController.getController().getEventType(entryTypeId, getSession());
    		List attributes = ContentTypeDefinitionController.getController().getContentTypeAttributes(eventType.getSchemaValue());
    		List attributeNames = new ArrayList();
    		Iterator attributesIterator = attributes.iterator();
    		while(attributesIterator.hasNext())
    		{
    			ContentTypeAttribute attribute = (ContentTypeAttribute)attributesIterator.next();
    			attributeNames.add(attribute.getName());
    		}
        	parameters.put("attributes", attributes);
        	parameters.put("attributeNames", attributeNames);

        	HttpServletRequest request = ServletActionContext.getRequest();
        	EntrySearchResultfilesConstructor results = new EntrySearchResultfilesConstructor(parameters, entries, getTempFilePath(), request.getScheme(), request.getServerName(), request.getServerPort(), resultValues, this, entryTypeId.toString());
        	searchResultFiles = results.getResults();
        }
        
        return Action.SUCCESS;
    } 

    /**
     * This is the entry point for the search form.
     */
    
    public String doInput() throws Exception 
    {
    	Session session = getSession(true);
    	
        initialize(session);

        return Action.INPUT;
    } 
        
    public Set getEntries()
    {
        return entries;
    }

    public List getEntriesAsList()
    {
    	List result = new ArrayList();
    	result.addAll(entries);
    	log.debug("result:" + result.size());
        return result;
    }

    public List getCategoryList()
    {
        return categoryList;
    }
    
    public void setCategoryList(List categoryList)
    {
        this.categoryList = categoryList;
    }
    
    public List getLocationList()
    {
        return locationList;
    }
    
    public void setLocationList(List locationList)
    {
        this.locationList = locationList;
    }
    
    public String[] getCategoryId()
    {
        return categoryId;
    }
    
    public void setCategoryId(String[] categoryId)
    {
        this.categoryId = categoryId;
    }
    
    public String[] getLocationId()
    {
        return locationId;
    }
    
    public void setLocationId(String[] locationId)
    {
        this.locationId = locationId;
    }
    
    public Set getEventList()
    {
        return eventList;
    }
 
    public Long[] getSearchEventId()
    {
        return searchEventId;
    }
    
    public void setSearchEventId(Long[] searchEventId)
    {
        this.searchEventId = searchEventId;
    }
    
    public String getSearchEmail()
    {
        return searchEmail;
    }
    public void setSearchEmail(String searchEmail)
    {
        this.searchEmail = searchEmail;
    }
    public String getSearchFirstName()
    {
        return searchFirstName;
    }
    public void setSearchFirstName(String searchFirstName)
    {
        this.searchFirstName = searchFirstName;
    }
    public String getSearchLastName()
    {
        return searchLastName;
    }
    public void setSearchLastName(String searchLastName)
    {
        this.searchLastName = searchLastName;
    }
    /*
    public String getEmailAddresses()
    {
        return emailAddresses;
    }
    */
	public void setOnlyFutureEvents(boolean onlyFutureEvents)
	{
		this.onlyFutureEvents = onlyFutureEvents;
	}

	public boolean getOnlyFutureEvents()
	{
		return this.onlyFutureEvents;
	}

	public List getCategoryAttributes()
	{
		return categoryAttributes;
	}

	public void setAndSearch(String andSearch)
	{
		this.andSearch = andSearch;
	}

	/**
	 * @return Returns the searchResultFiles.
	 */
	public Map getSearchResultFiles() {
		return searchResultFiles;
	}

	/**
	 * @param searchResultFiles The searchResultFiles to set.
	 */
	public void setSearchResultFiles(Map searchResultFiles) {
		this.searchResultFiles = searchResultFiles;
	}

	/**
	 * @return Returns the resultValues.
	 */
	public List getResultValues() {
		return resultValues;
	}

	/**
	 * @param resultValues The resultValues to set.
	 */
	public void setResultValues(List resultValues) {
		this.resultValues = resultValues;
	}
	
	public boolean getSingleEventSearch()
	{
		if(searchEventId == null || searchEventId.length > 1 || searchEventId[0] == null)
			return false;
		else
			return true;
	}
	/*
	public String getEmailEntryIds()
	{
		return emailEntryIds;
	}

	public void setEmailEntryIds(String emailEntryIds)
	{
		this.emailEntryIds = emailEntryIds;
	}
	*/

	public String getSearchHashCode()
	{
		return searchHashCode;
	}

	public void setSearchHashCode(String searchHashCode)
	{
		this.searchHashCode = searchHashCode;
	}
}
