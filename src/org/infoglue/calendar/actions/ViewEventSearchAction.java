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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.infoglue.calendar.controllers.CalendarController;
import org.infoglue.calendar.controllers.CategoryController;
import org.infoglue.calendar.controllers.ContentTypeDefinitionController;
import org.infoglue.calendar.controllers.EntryController;
import org.infoglue.calendar.controllers.EventController;
import org.infoglue.calendar.controllers.EventTypeController;
import org.infoglue.calendar.controllers.LocationController;
import org.infoglue.calendar.controllers.ResourceController;
import org.infoglue.calendar.entities.Category;
import org.infoglue.calendar.entities.Entry;
import org.infoglue.calendar.entities.Event;
import org.infoglue.calendar.entities.EventType;
import org.infoglue.calendar.entities.EventVersion;
import org.infoglue.calendar.entities.Location;
import org.infoglue.calendar.util.EntrySearchResultfilesConstructor;
import org.infoglue.calendar.util.EventSearchResultfilesConstructor;
import org.infoglue.common.contenttypeeditor.entities.ContentTypeAttribute;
import org.infoglue.common.util.PropertyHelper;

import com.opensymphony.webwork.ServletActionContext;
import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionContext;

/**
 * This action represents a Location Administration screen.
 * 
 * @author Mattias Bogeblad
 */

public class ViewEventSearchAction extends CalendarAbstractAction
{
    private static Log log = LogFactory.getLog(ViewEventSearchAction.class);

    private String name;
    private String title;
    private String startDateTime;
    private String endDateTime;
    private String startTime;
    private String endTime;
    private String lecturer;
    private String organizerName;
    private String customLocation;
    private String alternativeLocation;
    private String contactName;
    private String contactEmail;
    private String contactPhone;
    private String price = null;
    private Boolean sortAscending = new Boolean(true);
    private Integer maximumParticipants = null;
	private Boolean exportResult = new Boolean(false);
	private Integer stateId;
    
	private Calendar startCalendar;
    private Calendar endCalendar;

    private Long calendarId;
    private Long categoryId;
    private Long locationId;
    private Integer itemsPerPage;
    
    private List events;
    private Set categoriesList;
    private Set calendarList;
    private List locationList;
	private List resultValues = new LinkedList();
	private Map searchResultFiles;
	
	private void initialize() throws Exception
    {
    	Session session = getSession(true);
    	
        Category category = CategoryController.getController().getCategoryByPath(session, PropertyHelper.getProperty("filterCategoryPath"));
        if(category != null)
        	categoriesList = category.getChildren();
        calendarList = CalendarController.getController().getCalendarList(session);
        locationList = LocationController.getController().getLocationList(session);
    }
    
    /**
     * This is the entry point for the main listing.
     */
    
    public String execute() throws Exception
    {
    	Session session = getSession(true);
    	
        if(startDateTime != null && startDateTime.length() > 0)
            startCalendar = getCalendar(startDateTime + " " + startTime, "yyyy-MM-dd HH:mm", true); 
        
        if(endDateTime != null && endDateTime.length() > 0)
        	endCalendar = getCalendar(endDateTime + " " + endTime, "yyyy-MM-dd HH:mm", true); 

        log.info("price:" + price);
        
        this.events = EventController.getController().getEventList(name,
                													startCalendar,
                													endCalendar,
                													organizerName,
                													lecturer,
															        customLocation,
															        alternativeLocation,
															        contactName,
															        contactEmail,
															        contactPhone,
															        price,
															        maximumParticipants,
															        sortAscending,
															        categoryId,
															        calendarId,
															        locationId,
															        stateId,
															        session);
        
        // should we create result files?
        if( this.events.size() > 0 && exportResult) 
        {
        	
        	Map parameters = new HashMap();
        	parameters.put("headLine", "Events found");
        	        	
        	HttpServletRequest request = ServletActionContext.getRequest();
        	EventSearchResultfilesConstructor results = new EventSearchResultfilesConstructor(parameters, this.events, getTempFilePath(), request.getScheme(), request.getServerName(), request.getServerPort(), resultValues, (CalendarAbstractAction)this);
        	searchResultFiles = results.getResults();
        }
        
        return Action.SUCCESS;
    }
    
    public String doExternalBindingSearch() throws Exception
    {
    	setExportResult(false);
    	setSortAscending(false);
    	setStateId(Event.STATE_PUBLISHED);
    	initialize();
    	if (startDateTime != null)
    	{
    		execute();
    		if (this.events != null)
    		{
    			log.info("Filtering event results. We only want one post even if there are more versions...");
	    		// Remove duplicates
	    		HashSet<Long> currentIds = new HashSet<Long>();
	    		Iterator<Event> eventIterator = this.events.iterator();
	        	while (eventIterator.hasNext())
				{
					Event event = (Event) eventIterator.next();
					if (!currentIds.add(event.getId())) // if the id already exists
					{
						log.debug("Found duplicate event: " + event.getId());
						eventIterator.remove();
					}
				}
    		}
    	}
    	else
    	{
    		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
    		Calendar calendar = Calendar.getInstance();
    		startDateTime = dateFormatter.format(calendar.getTime());
    		calendar.roll(Calendar.WEEK_OF_YEAR, 1);
    		endDateTime = dateFormatter.format(calendar.getTime());

    		startTime = "00:00";
    		endTime = "23:59";
    	}

    	return "successExternalBinding";
    }

    /**
     * This is the entry point for the search form.
     */
    
    public String doInput() throws Exception 
    {
        initialize();

        return Action.INPUT;
    }

    public List getEvents()
    {
        return events;
    }

    public String getContactEmail()
    {
        return contactEmail;
    }
    
    public void setContactEmail(String contactEmail)
    {
        this.contactEmail = contactEmail;
    }
    
    public String getContactName()
    {
        return contactName;
    }
    
    public void setContactName(String contactName)
    {
        this.contactName = contactName;
    }
    
    public String getContactPhone()
    {
        return contactPhone;
    }
    
    public void setContactPhone(String contactPhone)
    {
        this.contactPhone = contactPhone;
    }
    
    public String getCustomLocation()
    {
        return customLocation;
    }
    
    public void setCustomLocation(String customLocation)
    {
        this.customLocation = customLocation;
    }
    
    public String getEndDateTime()
    {
        return endDateTime;
    }
    
    public void setEndDateTime(String endDateTime)
    {
        this.endDateTime = endDateTime;
    }
    
    public String getEndTime()
    {
        return endTime;
    }
    
    public void setEndTime(String endTime)
    {
        if(endTime == null || endTime.equalsIgnoreCase(""))
        	this.endTime = "23:59";
        else
            this.endTime = (endTime.indexOf(":") == -1 ? (endTime + ":00") : endTime);
    }
    
    public Integer getMaximumParticipants()
    {
        return maximumParticipants;
    }
    
    public void setMaximumParticipants(Integer maximumParticipants)
    {
        this.maximumParticipants = maximumParticipants;
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public String getTitle()
    {
        return title;
    }
    
    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getOrganizerName()
    {
        return organizerName;
    }
    
    public void setOrganizerName(String organizerName)
    {
        this.organizerName = organizerName;
    }
    
    public String getPrice()
    {
        return price;
    }
    
    public void setPrice(String price)
    {
        log.info("price:" + price);

        this.price = price;
    }
    
    public String getStartDateTime()
    {
        return startDateTime;
    }
    
    public void setStartDateTime(String startDateTime)
    {
        this.startDateTime = startDateTime;
    }
    
    public String getStartTime()
    {
        return startTime;
    }
    
    public void setStartTime(String startTime)
    {
        if(startTime == null || startTime.equalsIgnoreCase(""))
            this.startTime = "04:00";
        else
            this.startTime = (startTime.indexOf(":") == -1 ? (startTime + ":00") : startTime);
    }
    
    public void setEvents(List events)
    {
        this.events = events;
    }
    
    public String getAlternativeLocation()
    {
        return alternativeLocation;
    }
    
    public void setAlternativeLocation(String alternativeLocation)
    {
        this.alternativeLocation = alternativeLocation;
    }
    
    public Boolean getSortAscending()
    {
        return sortAscending;
    }
    
    public void setSortAscending(Boolean sortAscending)
    {
        this.sortAscending = sortAscending;
    }

	public String getLecturer()
	{
		return lecturer;
	}

	public void setLecturer(String lecturer)
	{
		this.lecturer = lecturer;
	}
	
	public Set getCategoriesList() 
	{
		return categoriesList;
	}

	public Set getCalendarList() 
	{
		return calendarList;
	}

	public List getLocationList() 
	{
		return locationList;
	}

	public Long getCategoryId() 
	{
		return categoryId;
	}

	public void setCategoryId(Long categoryId) 
	{
		this.categoryId = categoryId;
	}

	public Long getCalendarId()
	{
		return calendarId;
	}

	public void setCalendarId(Long calendarId)
	{
		this.calendarId = calendarId;
	}

	public Long getLocationId() 
	{
		return categoryId;
	}

	public void setLocationId(Long locationId) 
	{
		this.locationId = locationId;
	}

	public Integer getItemsPerPage() 
	{
		return itemsPerPage;
	}

	public void setItemsPerPage(Integer itemsPerPage) 
	{
		this.itemsPerPage = itemsPerPage;
	}

    public Boolean getExportResult()
	{
		return exportResult;
	}

	public void setExportResult(Boolean exportResult)
	{
		this.exportResult = exportResult;
	}

    public Map getSearchResultFiles()
	{
		return searchResultFiles;
	}
    
    public void setStateId(Integer stateId)
    {
    	this.stateId = stateId;
    }
    
    public Integer getStateId()
    {
    	return this.stateId;
    }

}

