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

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.servlet.ServletInputStream;

import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.portlet.PortletFileUpload;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.Element;
import org.infoglue.calendar.controllers.CalendarController;
import org.infoglue.calendar.controllers.EventController;
import org.infoglue.calendar.controllers.LocationController;
import org.infoglue.calendar.controllers.ResourceController;
import org.infoglue.calendar.entities.Event;
import org.infoglue.calendar.entities.EventType;
import org.infoglue.calendar.entities.EventVersion;
import org.infoglue.common.util.ConstraintExceptionBuffer;
import org.infoglue.common.util.dom.DOMBuilder;

import com.opensymphony.webwork.ServletActionContext;
import com.opensymphony.webwork.util.AttributeMap;
import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.util.OgnlValueStack;
import com.opensymphony.xwork.validator.ValidationException;

/**
 * This action represents a Calendar Administration screen.
 * 
 * @author Mattias Bogeblad
 */

public class UpdateEventAction extends CalendarUploadAbstractAction
{
	private static Log log = LogFactory.getLog(UpdateEventAction.class);

    private Long eventId;
    private Long versionLanguageId;
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
    private String[] participantUserName;

    private Long entryFormId;

    private Long calendarId;
    private String mode;
        
    private Map categoryAttributes = new HashMap();
    private List attributes;

    private Event event;
    private List assetKeys;
    
    private Calendar startCalendar;
    private Calendar endCalendar;
    private Calendar lastRegistrationCalendar;

    private String publishEventUrl;
    private String publishedEventUrl;

    /**
     * This is the entry point for the main listing.
     */
    
    public String execute() throws Exception 
    {
        if(startDateTime != null && (endDateTime == null || endDateTime.equals("")))
            endDateTime = startDateTime;

        startCalendar 	= getCalendar(startDateTime + " " + startTime, "yyyy-MM-dd HH:mm", false); 
        endCalendar 	= getCalendar(endDateTime + " " + endTime, "yyyy-MM-dd HH:mm", false); 
        lastRegistrationCalendar = getCalendar(lastRegistrationDateTime + " " + lastRegistrationTime, "yyyy-MM-dd HH:mm", false); 

        try
        {
            int i = 0;
            String idKey = ServletActionContext.getRequest().getParameter("categoryAttributeId_" + i);
            log.info("idKey:" + idKey);
            while(idKey != null && idKey.length() > 0)
            {
                String[] categoryIds = ServletActionContext.getRequest().getParameterValues("categoryAttribute_" + idKey + "_categoryId");
                log.info("categoryIds:" + categoryIds);
                
                if(categoryIds == null || categoryIds.length == 0)
                	this.addFieldError("categoryAttribute_" + idKey + "_categoryId", "errors.atLeastOneItem");

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
	            ActionContext.getContext().getValueStack().getContext().put("errorEvent", event);
	            
	            validateInput(this, ceb);
            }
            else
            {
            	validateInput(this);
            }
            
            //TEST
            
            log.info("SystemUserName:" + this.participantUserName);
            log.info("name:" + this.name);
            System.out.println("Title: " + title);
            EventController.getController().updateEvent(
                    eventId, 
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
                    entryFormId,
                    xml,
                    getSession());
            
        }
        catch(ValidationException e)
        {
            return "evaluateError";            
        }

        ServletActionContext.getRequest().getSession().removeAttribute("categoryAttributes");

        return Action.SUCCESS;
    } 
    
    
    /**
     * This is the action command for submitting an event to publication.
     */
    
    public String submitForPublishEvent() throws Exception 
    {
    	EventController.getController().submitForPublishEvent(eventId, this.publishEventUrl, this.getLanguageCode(), this.getInfoGluePrincipal(), getSession());

        return "successSubmitForPublish";
    } 

    /**
     * This is the action command for publishing an event.
     */
    
    public String publishEvent() throws Exception 
    {
        EventController.getController().publishEvent(eventId, this.publishedEventUrl, this.getLanguageCode(), this.getInfoGluePrincipal(), getSession());

        return "successPublish";
    } 

    /**
     * This is the action command for publishing an event.
     */
    
    public String uploadForm() throws Exception 
    {
        this.event = EventController.getController().getEvent(eventId, getSession());

        this.assetKeys = EventController.getController().getAssetKeys();

        return "uploadForm";
    } 

    /**
     * This is the entry point for the main listing.
     */
    
    public String upload() throws Exception 
    {
        
        try
        {
	        DiskFileItemFactory factory = new DiskFileItemFactory();
	        // Configure the factory here, if desired.
	        PortletFileUpload upload = new PortletFileUpload(factory);
	        // Configure the uploader here, if desired.
	        List fileItems = upload.parseRequest(ServletActionContext.getRequest());
            log.debug("fileItems:" + fileItems.size());
	        Iterator i = fileItems.iterator();
	        while(i.hasNext())
	        {
	            Object o = i.next();
	            DiskFileItem dfi = (DiskFileItem)o;
	            log.debug("dfi:" + dfi.getFieldName());
	            log.debug("dfi:" + dfi);
	            
	            if (!dfi.isFormField()) {
	                String fieldName = dfi.getFieldName();
	                String fileName = dfi.getName();
	                String contentType = dfi.getContentType();
	                boolean isInMemory = dfi.isInMemory();
	                long sizeInBytes = dfi.getSize();
	                
	                log.debug("fieldName:" + fieldName);
	                log.debug("fileName:" + fileName);
	                log.debug("contentType:" + contentType);
	                log.debug("isInMemory:" + isInMemory);
	                log.debug("sizeInBytes:" + sizeInBytes);
	                File uploadedFile = new File(getTempFilePath() + File.separator + fileName);
	                dfi.write(uploadedFile);
	            }

	        }
	        
	    }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        log.debug("");
        ResourceController.getController().createResource(this.eventId, this.getAssetKey(), this.getFileContentType(), this.getFileFileName(), this.getFile(), getSession());
        
        return "successUpload";
    } 
    
    public String getDescription()
    {
        return description;
    }
    
    public void setDescription(String description)
    {
        this.description = description;
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
        if(endTime.equalsIgnoreCase(""))
            this.endTime = "23.59";
        else
            this.endTime = (endTime.indexOf(":") == -1 ? (endTime + ":00") : endTime);
    }
    
    public Long getEventId()
    {
        return eventId;
    }
    
    public void setEventId(Long eventId)
    {
        this.eventId = eventId;
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
    
    public void setLocationId(String[] locationId)
    {
        this.locationId = locationId;
    }
    
    public Long getCalendarId()
    {
        return calendarId;
    }
    
    public void setCalendarId(Long calendarId)
    {
        this.calendarId = calendarId;
    }
    
    public String getMode()
    {
        return mode;
    }
    
    public void setMode(String mode)
    {
        this.mode = mode;
    }
    
    public String[] getParticipantUserName()
    {
        return participantUserName;
    }
    
    public void setParticipantUserName(String[] participantUserName)
    {
        this.participantUserName = participantUserName;
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
    public String[] getLocationId()
    {
        return locationId;
    }
    public String getLastRegistrationTime()
    {
        return lastRegistrationTime;
    }
    public void setLastRegistrationTime(String lastRegistrationTime)
    {
        if(lastRegistrationTime.equalsIgnoreCase(""))
            this.lastRegistrationTime = "12:00";
        else
            this.lastRegistrationTime = (lastRegistrationTime.indexOf(":") == -1 ? (lastRegistrationTime + ":00") : lastRegistrationTime);
    }
    
    public List getAssetKeys()
    {
        return assetKeys;
    }

    public Event getEvent()
    {
        return event;
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
    
    public void setPublishEventUrl(String publishEventUrl)
    {
        this.publishEventUrl = publishEventUrl;
    }

    public void setPublishedEventUrl(String publishedEventUrl)
    {
        this.publishedEventUrl = publishedEventUrl;
    }

    public Map getCategoryAttributes()
    {
        return categoryAttributes;
    }

    public String getAlternativeLocation()
    {
        return alternativeLocation;
    }
    
    public void setAlternativeLocation(String alternativeLocation)
    {
        this.alternativeLocation = alternativeLocation;
    }

	public Long getEntryFormId()
	{
		return entryFormId;
	}

	public void setEntryFormId(Long entryFormId)
	{
		this.entryFormId = entryFormId;
	}

	public Long getVersionLanguageId()
	{
		return versionLanguageId;
	}

	public void setVersionLanguageId(Long versionLanguageId)
	{
		this.versionLanguageId = versionLanguageId;
	}

}
