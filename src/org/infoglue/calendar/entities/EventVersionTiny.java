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
package org.infoglue.calendar.entities;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.infoglue.calendar.util.validators.BaseValidator;
import org.infoglue.common.contenttypeeditor.entities.ContentTypeDefinition;
import org.infoglue.common.util.ConstraintExceptionBuffer;

/**
 * This class represents an event version. An event version contains the langauge versions of the event - so that an event can be described in different languages.
 * 
 * @author mattias
 * 
 * @hibernate.class table="EventVersion"
 */

public class EventVersionTiny implements BaseEntity
{

	/*
    public static final Integer STATE_WORKING = new Integer(0);
    public static final Integer STATE_PUBLISH = new Integer(2);
    public static final Integer STATE_PUBLISHED = new Integer(3);
    
    private Long id;
    private java.util.Calendar startDateTime;
    private java.util.Calendar endDateTime;
    
    private Boolean isInternal;
    private Boolean isOrganizedByGU = new Boolean(false);
    private java.util.Calendar lastRegistrationDateTime;
    private Integer maximumParticipants;
    private Integer stateId = STATE_WORKING; //Default if not otherwise set
    private String creator;
    private String attributes;
    
    private Long entryFormId;
    
    private Calendar owningCalendar;
    private Set calendars = new HashSet();
    private Set locations = new HashSet();;
    private Set participants = new HashSet();;
    private Set resources = new HashSet();;
    private Set eventCategories = new HashSet();;
    private Set entries = new HashSet();;
    */
    
	private Long id;
    //private Integer stateId				= new Integer(0);
    //private Date modifiedDateTime			= new Date();
    //private String versionComment			= "No comment";
    //private Boolean isCheckedOut			= new Boolean(false);
   	//private Boolean isActive				= new Boolean(true);
	
	private Long versionLanguageId					= null;
	//private String languageName 			= "";
   	private Long eventId					= null;
    //private String eventName 				= "";
    //private Long eventTypeId				= null;
    //private String versionModifier			= null;

    private String name;
    private String title;
    private String description;
    private String organizerName;
    private String lecturer;
    private String alternativeLocation;
    private String customLocation;
    private String shortDescription;
    private String longDescription;
    private String eventUrl;
    //private String contactName;
    //private String contactEmail;
    //private String contactPhone;
    //private String price;
	private String attributes = "";

	private Event event;
	private Long languageId;
	//private Language language;
	
    
    /**
     * @hibernate.id generator-class="native" type="long" column="id" unsaved-value="null"
     * 
     * @return long
     */    
    public Long getId()
    {
        return id;
    }
    
    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getLanguageId()
    {
        return languageId;
    }
    
    public void setLanguageId(Long languageId)
    {
        this.languageId = languageId;
    }

    /**
     * @hibernate.property name="getName" column="name" type="string" not-null="false" unique="true"
     * 
     * @return String
     */
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * @hibernate.property name="getTitle" column="title" type="string" not-null="false" unique="true"
     * 
     * @return String
     */
    public String getTitle()
    {
        return title;
    }
    
    public void setTitle(String title)
    {
        this.title = title;
    }

    /**
     * @hibernate.property name="getDescription" column="description" type="string" not-null="false" unique="false"
     * 
     * @return String
     */
    public String getDescription()
    {
        return description;
    }
    
    public void setDescription(String description)
    {
        this.description = description;
    }
    
    /**
     * @hibernate.property name="getContactEmail" column="contactEmail" type="string" not-null="false" unique="false"
     * 
     * @return String
     */
    /*
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

    public String getPrice()
    {
        if(price == null || price.equals("0") || price.equals("0.0"))
            return "";
        
        return price;
    }
    public void setPrice(String price)
    {
        this.price = price;
    }

    */
        
    /**
     * @hibernate.property name="getCustomLocation" column="customLocation" type="string" not-null="false" unique="false"
     * 
     * @return String
     */
    public String getCustomLocation()
    {
        return customLocation;
    }
    
    public void setCustomLocation(String customLocation)
    {
        this.customLocation = customLocation;
    }
    
    /**
     * @hibernate.property name="getAlternativeLocation" column="alternativeLocation" type="string" not-null="false" unique="false"
     * 
     * @return String
     */
    public String getAlternativeLocation()
    {
        return alternativeLocation;
    }

    public void setAlternativeLocation(String alternativeLocation)
    {
        this.alternativeLocation = alternativeLocation;
    }

    /**
     * @hibernate.property name="getEventUrl" column="eventUrl" type="string" not-null="false" unique="false"
     * 
     * @return String
     */
    public String getEventUrl()
    {
        if(eventUrl != null && !eventUrl.equalsIgnoreCase("") && eventUrl.indexOf("http") == -1)
            eventUrl = "http://" + eventUrl;
        
        return eventUrl;
    }
    
    public void setEventUrl(String eventUrl)
    {
        if(eventUrl != null && !eventUrl.equalsIgnoreCase("") && eventUrl.indexOf("http") == -1)
            eventUrl = "http://" + eventUrl;
        
        this.eventUrl = eventUrl;
    }   

    /**
     * @hibernate.property name="getLecturer" column="lecturer" type="string" not-null="false" unique="false"
     * 
     * @return String
     */
    public String getLecturer()
    {
        return lecturer;
    }
    
    public void setLecturer(String lecturer)
    {
        this.lecturer = lecturer;
    }
    
    /**
     * @hibernate.property name="getOrganizerName" column="organizerName" type="string" not-null="false" unique="false"
     * 
     * @return String
     */
    public String getOrganizerName()
    {
        return organizerName;
    }
    
    public void setOrganizerName(String organizerName)
    {
        this.organizerName = organizerName;
    }
        
    /**
     * @hibernate.property name="getShortDescription" column="shortDescription" type="string" not-null="false" unique="false"
     * 
     * @return String
     */
    public String getShortDescription()
    {
        return shortDescription;
    }

    public String getDecoratedShortDescription()
    {
        if(shortDescription != null)
        {
            String lineSep = System.getProperty("line.separator");
            return shortDescription.replaceAll(lineSep, "<br/>");
        }
        
        return shortDescription;
    }

    
    public void setShortDescription(String shortDescription)
    {
        this.shortDescription = shortDescription;
    }
    
    /**
     * @hibernate.property name="getLongDescription" column="longDescription" type="string" not-null="false" unique="false"
     * 
     * @return String
     */
    public String getLongDescription()
    {
        return longDescription;
    }

    public String getDecoratedLongDescription()
    {
        if(longDescription != null)
        {
            String lineSep = System.getProperty("line.separator");
            return longDescription.replaceAll(lineSep, "<br/>");
        }
        
        return longDescription;
    }

    public void setLongDescription(String longDescription)
    {
        this.longDescription = longDescription;
    }

	public String getAttributes()
	{
		return attributes;
	}

	public void setAttributes(String attributes)
	{
		this.attributes = attributes;
	}

    /**
     * @hibernate.id generator-class="native" type="long" column="languageId" unsaved-value="null"
     * 
     * @return long
     */    
    public Long getVersionLanguageId()
    {
        return versionLanguageId;
    }
    
    public void setVersionLanguageId(Long versionLanguageId)
    {
        this.versionLanguageId = versionLanguageId;
    }

    /**
     * @hibernate.id generator-class="native" type="long" column="languageId" unsaved-value="null"
     * 
     * @return long
     */    
    public Long getEventId()
    {
        return eventId;
    }
    
    public void setEventId(Long eventId)
    {
        this.eventId = eventId;
    }
    
    /** 
     * @hibernate.many-to-one class="org.infoglue.calendar.entities.Event" column="event_id"
     */  
    
    public Event getEvent()
    {
        return event;
    }
    
    public void setEvent(Event event)
    {
        this.event = event;
    }

    /** 
     * @hibernate.many-to-one class="org.infoglue.calendar.entities.Language" column="language_id"
     */  
    /*
    public Language getLanguage()
    {
        return language;
    }
    
    public void setLanguage(Language language)
    {
    	if(language != null)
            this.versionLanguageId = language.getId();

    	this.language = language;
    }
    */

	public ConstraintExceptionBuffer validate(ContentTypeDefinition contentTypeDefinition)
	{
		ConstraintExceptionBuffer ceb = new ConstraintExceptionBuffer();
		ceb.add(new BaseValidator().validate(contentTypeDefinition, this.getAttributes()));
		
		return ceb;
	}
    
	public String getLocalizedName(String isoCode, String fallbackIsoCode) 
	{
		return this.getName();
	}

}
