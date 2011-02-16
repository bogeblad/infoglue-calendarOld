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

import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.Element;
import org.infoglue.calendar.controllers.CalendarController;
import org.infoglue.calendar.controllers.ContentTypeDefinitionController;
import org.infoglue.calendar.controllers.EventController;
import org.infoglue.calendar.controllers.EventTypeController;
import org.infoglue.calendar.controllers.LanguageController;
import org.infoglue.calendar.controllers.LocationController;
import org.infoglue.calendar.entities.Entry;
import org.infoglue.calendar.entities.Event;
import org.infoglue.calendar.entities.EventType;
import org.infoglue.calendar.entities.EventVersion;
import org.infoglue.calendar.entities.Language;
import org.infoglue.common.util.ConstraintExceptionBuffer;
import org.infoglue.common.util.dom.DOMBuilder;

import com.opensymphony.webwork.ServletActionContext;
import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.validator.ValidationException;

/**
 * This action represents a Calendar Administration screen.
 * 
 * @author Mattias Bogeblad
 */

public class CreateEventAction extends CalendarAbstractAction
{
	private static Log log = LogFactory.getLog(CreateEventAction.class);

    //private Map dataBean = new HashMap();

    private String name;
    private String title;
    private String description;
    private String startDateTime;
    private String endDateTime;
    private String startTime;
    private String endTime;
    private String lastRegistrationDateTime;
    private String lastRegistrationTime;

    private Boolean isInternal = new Boolean(false);
    private Boolean isOrganizedByGU = new Boolean(false);
    private String organizerName;
    private String lecturer;
    private String customLocation;
    private String alternativeLocation;
    private String shortDescription;
    private String longDescription;
    private String eventUrl;
    private String contactName;
    private String contactEmail;
    private String contactPhone;
    private String price;
    private Integer maximumParticipants;
    
    private String[] locationId;
    private String[] categoryId;
    private String[] participantUserName;
    
    private String date;
    private String time;
    private String mode;
    private Long calendarId;
    private Long versionLanguageId;
    private Long eventId;
    
    private org.infoglue.calendar.entities.Calendar calendar;
    private List locations;
    //private List categories;
    private List languages;
    private Map categoryAttributes = new HashMap();
    //private List infogluePrincipals;
    private List entryFormEventTypes;
    private Long entryFormId;
    
    private Calendar startCalendar;
    private Calendar endCalendar;
    private Calendar lastRegistrationCalendar;
    
    private Event newEvent = null;
        
    private List attributes;

    /**
     * This is the entry point for the main listing.
     */
    
    public String execute() throws Exception 
    {        
    	log.info("\n\nLanguage sent in:" + ServletActionContext.getRequest().getParameter("versionLanguageId") + ":" + this.versionLanguageId + "\n\n");

        if(startDateTime != null && (endDateTime == null || endDateTime.equals("")))
        {
            endDateTime = startDateTime;
        }
        
        Iterator keyIterator = this.getActionContext().getParameters().keySet().iterator();
        while(keyIterator.hasNext())
        {
        	String key = (String)keyIterator.next();
        	log.info("" + key + "=" + this.getActionContext().getParameters().get(key));
        }
        
        startCalendar 	= getCalendar(startDateTime + " " + startTime, "yyyy-MM-dd HH:mm", false); 
        endCalendar 	= getCalendar(endDateTime + " " + endTime, "yyyy-MM-dd HH:mm", false); 
            
        lastRegistrationCalendar = getCalendar(lastRegistrationDateTime + " " + lastRegistrationTime, "yyyy-MM-dd HH:mm", false); 

        log.info("isInternal:" + this.isInternal);

        try
        {
            int i = 0;
            String idKey = ServletActionContext.getRequest().getParameter("categoryAttributeId_" + i);
            log.info("idKey:" + idKey);
            while(idKey != null && idKey.length() > 0)
            {
                String[] categoryIds = ServletActionContext.getRequest().getParameterValues("categoryAttribute_" + idKey + "_categoryId");
                if(categoryIds == null || categoryIds.length == 0)
                	this.addFieldError("categoryAttribute_" + idKey + "_categoryId", "errors.atLeastOneItem");

                log.info("categoryIds:" + categoryIds);
                categoryAttributes.put(idKey, categoryIds);
                
                i++;
                idKey = ServletActionContext.getRequest().getParameter("categoryAttributeId_" + i);
                log.info("idKey:" + idKey);
            }

            ServletActionContext.getRequest().getSession().setAttribute("categoryAttributes", categoryAttributes);

            //TEST
            
        	Map attributeValues = new HashMap();
        	
        	DOMBuilder domBuilder = new DOMBuilder();
        	Document document = domBuilder.createDocument();
        	Element articleElement = domBuilder.addElement(document, "entry");
        	//domBuilder.addAttribute(articleElement, "xmlns", "x-schema:ArticleSchema.xml");
        	Element attributesElement = domBuilder.addElement(articleElement, "attributes");
        	        	
            int attributeIndex = 0;
            String attributeIdKey = ServletActionContext.getRequest().getParameter("attributeName_" + attributeIndex);
            log.debug("attributeIdKey:" + attributeIdKey);
            log.info("attributeIdKey:" + attributeIdKey);
            while(attributeIdKey != null && attributeIdKey.length() > 0)
            {
            	log.debug("attributeIdKey in loop: " + attributeIdKey);
            	
                String[] value = ServletActionContext.getRequest().getParameterValues(attributeIdKey);
                if(value == null || value.length == 0)
                	value = new String[0];
                    //this.addFieldError(attributeIdKey, "errors.atLeastOneItem");

                log.debug(attributeIdKey + "=" + value);
                log.info("value:" + value);
                
                String valueString = "";
                if(value != null)
                {
	                for(int j=0; j<value.length; j++)
	                {
	                	if(j>0)
	                		valueString += ",";
	                	
	                	valueString += value[j];
	                }
                }
                
                int index = attributeIdKey.indexOf("attribute_");
                if(index == -1)
                	index = 0;
                else
                	index = index + 10;
                
                Element element = domBuilder.addElement(attributesElement, attributeIdKey.substring(index));
                domBuilder.addCDATAElement(element, valueString);
            	
                attributeValues.put(attributeIdKey, value);
                
                attributeIndex++;
                attributeIdKey = ServletActionContext.getRequest().getParameter("attributeName_" + attributeIndex);
                log.info("attributeIdKey:" + attributeIdKey);
            }

            String xml = domBuilder.getFormattedDocument(document, "UTF-8");
            log.debug("xml:" + xml);
            
            ServletActionContext.getRequest().getSession().setAttribute("attributes", attributes);
        	
            org.infoglue.calendar.entities.Calendar calendar = CalendarController.getController().getCalendar(calendarId, getSession());
            EventType eventType = calendar.getEventType();
            
            if(eventType != null)
            {
            	EventVersion eventVersion = new EventVersion();
	            eventVersion.setAttributes(xml);
	            ConstraintExceptionBuffer ceb = eventVersion.validate(eventType);
	            ActionContext.getContext().getValueStack().getContext().put("errorEvent", eventVersion);
	            
	            validateInput(this, ceb);
            }
            else
            {
            	validateInput(this);
            }
            
            //TEST
            
            Integer stateId = Event.STATE_PUBLISHED;
            if(useEventPublishing())
                stateId = Event.STATE_WORKING;
                        
            log.info("\n\nLanguage sent in:" + ServletActionContext.getRequest().getParameter("versionLanguageId") + ":" + this.versionLanguageId + "\n\n");

            newEvent = EventController.getController().createEvent(calendarId,
            											versionLanguageId,
									                    name, 
									                    title,
									                    description,
									                    isInternal, 
									                    isOrganizedByGU, 
									                    organizerName, 
									                    lecturer, 
									                    customLocation,
									                    alternativeLocation,
									                    shortDescription,
									                    longDescription,
									                    eventUrl,
									                    contactName,
									                    contactEmail,
									                    contactPhone,
									                    price,
									                    lastRegistrationCalendar,
									                    maximumParticipants,
									                    startCalendar, 
									                    endCalendar, 
									                    locationId, 
									                    categoryAttributes, 
									                    participantUserName,
									                    stateId,
									                    this.getInfoGlueRemoteUser(),
									                    this.entryFormId,
									                    xml,
									                    getSession());

        }
        catch(ValidationException e)
        {
            return Action.ERROR;            
        }
        
        ServletActionContext.getRequest().getSession().removeAttribute("categoryAttributes");

        return Action.SUCCESS;
    } 

    
    /**
     * Creates an event by copying an old one
     */
    
    public String copy() throws Exception 
    {
        Event originalEvent = EventController.getController().getEvent(eventId, getSession());
        
        Integer stateId = Event.STATE_PUBLISHED;
        if(useEventPublishing())
            stateId = Event.STATE_WORKING;

        newEvent = EventController.getController().createEvent(calendarId,
                originalEvent, 
                stateId,
                this.getInfoGlueRemoteUser(),
                originalEvent.getEntryFormId(),
                getSession());
        
        /*
        newEvent = EventController.getController().createEvent(calendarId,
                originalEvent.getName(), 
                originalEvent.getDescription(),
                originalEvent.getIsInternal(), 
                originalEvent.getIsOrganizedByGU(), 
                originalEvent.getOrganizerName(), 
                originalEvent.getLecturer(), 
                originalEvent.getCustomLocation(),
                originalEvent.getAlternativeLocation(),
                originalEvent.getShortDescription(),
                originalEvent.getLongDescription(),
                originalEvent.getEventUrl(),
                originalEvent.getContactName(),
                originalEvent.getContactEmail(),
                originalEvent.getContactPhone(),
                originalEvent.getPrice(),
                originalEvent.getLastRegistrationDateTime(),
                originalEvent.getMaximumParticipants(),
                originalEvent.getStartDateTime(), 
                originalEvent.getEndDateTime(), 
                originalEvent.getLocations(), 
                originalEvent.getEventCategories(), 
                originalEvent.getParticipants(),
                stateId,
                this.getInfoGlueRemoteUser(),
                originalEvent.getEntryFormId(),
                originalEvent.getAttributes(),
                getSession());
		*/
        
        return Action.SUCCESS + "Copy";
    } 

    
    /**
     * Links an event to a new calendar
     */
    
    public String link() throws Exception 
    {
        EventController.getController().linkEvent(calendarId, eventId, getSession());

        return "successLinked";
    } 

    /**
     * This is the entry point creating a new calendar.
     */
    
    public String input() throws Exception 
    {
    	log.info("\n\nLanguage sent in:" + ServletActionContext.getRequest().getParameter("versionLanguageId") + ":" + this.versionLanguageId + "\n\n");
    	
        this.calendar = CalendarController.getController().getCalendar(this.calendarId, getSession());
        this.locations 	= LocationController.getController().getLocationList(getSession());
        //this.categories = CategoryController.getController().getRootCategoryList(getSession());
        //this.infogluePrincipals = UserControllerProxy.getController().getAllUsers();
        this.entryFormEventTypes = EventTypeController.getController().getEventTypeList(EventType.ENTRY_DEFINITION, getSession());
        this.languages = LanguageController.getController().getLanguageList(getSession());
        
        this.isOrganizedByGU = new Boolean(true);
        
        EventType eventType = this.calendar.getEventType();
        if(eventType != null)
        	this.attributes = ContentTypeDefinitionController.getController().getContentTypeAttributes(eventType.getSchemaValue());

        return Action.INPUT;
    } 
    
    public Long getCalendarId()
    {
        return calendarId;
    }
    
    public void setCalendarId(Long calendarId)
    {
        this.calendarId = calendarId;
    }
    
    public String getDescription()
    {
        return description;
    }
    
    public void setDescription(String description)
    {
        this.description = description;
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
    public String getEventUrl()
    {
        return eventUrl;
    }
    public void setEventUrl(String eventUrl)
    {
        this.eventUrl = eventUrl;
    }
    public Boolean getIsInternal()
    {
    	log.debug("isInternal in getter:" + isInternal);
        return isInternal;
    }
    public void setIsInternal(Boolean isInternal)
    {
        this.isInternal = isInternal;
    }
    public Boolean getIsOrganizedByGU()
    {
        return isOrganizedByGU;
    }
    public void setIsOrganizedByGU(Boolean isOrganizedByGU)
    {
        this.isOrganizedByGU = isOrganizedByGU;
    }
    public String getLastRegistrationDateTime()
    {
        return lastRegistrationDateTime;
    }
    public void setLastRegistrationDateTime(String lastRegistrationDateTime)
    {
        this.lastRegistrationDateTime = lastRegistrationDateTime;
    }
    public String getLecturer()
    {
        return lecturer;
    }
    public void setLecturer(String lecturer)
    {
        this.lecturer = lecturer;
    }
    public String getLongDescription()
    {
        return longDescription;
    }
    public void setLongDescription(String longDescription)
    {
        this.longDescription = longDescription;
    }
    public Integer getMaximumParticipants()
    {
        return maximumParticipants;
    }
    public void setMaximumParticipants(Integer maximumParticipants)
    {
        this.maximumParticipants = maximumParticipants;
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
        this.price = price;
    }
    public String getShortDescription()
    {
        return shortDescription;
    }
    public void setShortDescription(String shortDescription)
    {
        this.shortDescription = shortDescription;
    }

    public String getDate()
    {
        return date;
    }
    
    public void setDate(String date)
    {
        this.date = date;
    }
    
    public String getEndDateTime()
    {
        return endDateTime;
    }
    
    public void setEndDateTime(String endDateTime)
    {
        this.endDateTime = endDateTime;
    }
    
    public String getMode()
    {
        return mode;
    }
    
    public void setMode(String mode)
    {
        this.mode = mode;
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
        if(startTime.equalsIgnoreCase(""))
            this.startTime = "12:34";
        else
            this.startTime = (startTime.indexOf(":") == -1 ? (startTime + ":00") : startTime);
    }
    
    public String getTime()
    {
        return time;
    }
    
    public void setTime(String time)
    {
        this.time = time;
    }
    
    public String getEndTime()
    {
        return endTime;
    }
    
    public void setEndTime(String endTime)
    {
        if(endTime.equalsIgnoreCase(""))
            this.endTime = "23:59";
        else
            this.endTime = (endTime.indexOf(":") == -1 ? (endTime + ":00") : endTime);
    }
    
    public void setLastRegistrationTime(String lastRegistrationTime)
    {
        if(lastRegistrationTime.equalsIgnoreCase(""))
            this.lastRegistrationTime = "13:00";
        else
            this.lastRegistrationTime = (lastRegistrationTime.indexOf(":") == -1 ? (lastRegistrationTime + ":00") : lastRegistrationTime);
    }
    
    public String getLastRegistrationTime()
    {
        return lastRegistrationTime;
    }

    public List getLocations()
    {
        return locations;
    }
    
    public void setCategoryId(String[] categoryId)
    {
        this.categoryId = categoryId;
    }
    
    public void setLocationId(String[] locationId)
    {
        this.locationId = locationId;
    }
    
    public void setParticipantUserName(String[] participantUserName)
    {
        this.participantUserName = participantUserName;
    }
    /*
    public List getInfogluePrincipals()
    {
        return infogluePrincipals;
    }
    
    public void setInfogluePrincipals(List infogluePrincipals)
    {
        this.infogluePrincipals = infogluePrincipals;
    }
    */
    public String[] getCategoryId()
    {
        return categoryId;
    }
    
    public String[] getLocationId()
    {
        return locationId;
    }
    
    public String[] getParticipantUserName()
    {
        return participantUserName;
    }
    
    public Calendar getEndCalendar()
    {
        return endCalendar;
    }
    public Calendar getLastRegistrationCalendar()
    {
        return lastRegistrationCalendar;
    }
    public Calendar getStartCalendar()
    {
        return startCalendar;
    }
    
    public Event getNewEvent()
    {
        return newEvent;
    }
    
    public org.infoglue.calendar.entities.Calendar getCalendar()
    {
        return calendar;
    }
        
    public Long getEventId()
    {
        return eventId;
    }
    
    public void setEventId(Long eventId)
    {
        this.eventId = eventId;
    }
    
    public String getAlternativeLocation()
    {
        return alternativeLocation;
    }
    
    public void setAlternativeLocation(String alternativeLocation)
    {
        this.alternativeLocation = alternativeLocation;
    }
    
    public String[] getCategoryAttributeValues(Long key)
    {
        String[] value = (String[])((Map)ServletActionContext.getRequest().getSession().getAttribute("categoryAttributes")).get(key.toString());
        return value;
    }


	public List getEntryFormEventTypes()
	{
		return entryFormEventTypes;
	}


	public Long getEntryFormId()
	{
		return entryFormId;
	}


	public void setEntryFormId(Long entryFormId)
	{
		this.entryFormId = entryFormId;
	}


	public List getAttributes()
	{
		return attributes;
	}


	public Long getVersionLanguageId() 
	{
		return versionLanguageId;
	}

	
	public void setVersionLanguageId(Long versionLanguageId) 
	{
		this.versionLanguageId = versionLanguageId;
	}


	public List getLanguages() 
	{
		return languages;
	}
}
