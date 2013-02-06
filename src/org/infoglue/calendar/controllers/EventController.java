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

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoglue.calendar.entities.Calendar;
import org.infoglue.calendar.entities.Category;
import org.infoglue.calendar.entities.Entry;
import org.infoglue.calendar.entities.Event;
import org.infoglue.calendar.entities.EventCategory;
import org.infoglue.calendar.entities.EventTiny;
import org.infoglue.calendar.entities.EventTypeCategoryAttribute;
import org.infoglue.calendar.entities.EventVersion;
import org.infoglue.calendar.entities.Group;
import org.infoglue.calendar.entities.Language;
import org.infoglue.calendar.entities.Location;
import org.infoglue.calendar.entities.Participant;
import org.infoglue.calendar.entities.Role;
import org.infoglue.calendar.entities.Subscriber;
import org.infoglue.calendar.util.EventComparator;
import org.infoglue.common.security.beans.InfoGluePrincipalBean;
import org.infoglue.common.security.beans.InfoGlueRoleBean;
import org.infoglue.common.util.PropertyHelper;
import org.infoglue.common.util.RemoteCacheUpdater;
import org.infoglue.common.util.Timer;
import org.infoglue.common.util.VelocityTemplateProcessor;
import org.infoglue.common.util.WebServiceHelper;
import org.infoglue.common.util.io.FileHelper;
import org.infoglue.common.util.mail.MailServiceFactory;


import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.NotExpression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.hql.classic.OrderByParser;

public class EventController extends BasicController
{    
    //Logger for this class
    private static Log log = LogFactory.getLog(EventController.class);
        
    
    /**
     * Factory method to get EventController
     * 
     * @return EventController
     */
    
    public static EventController getController()
    {
        return new EventController();
    }
        
    
    /**
     * This method is used to create a new Event object in the database.
     */
    
    public Event createEvent(Long calendarId, 
            				Event originalEvent, 
            	            Integer stateId,
            	            String creator,
            	            Long entryFormId,
            	            Session session) throws HibernateException, Exception 
    {
        Event event = null;
 
		Calendar calendar = CalendarController.getController().getCalendar(calendarId, session);
		
		Set locations = new HashSet();
		Iterator oldLocationsIterator = originalEvent.getLocations().iterator();
		while(oldLocationsIterator.hasNext())
		{
		    Location location = (Location)oldLocationsIterator.next();
		    locations.add(location);
		}
		
		Set participants = new HashSet();
		Iterator oldParticipantsIterator = originalEvent.getParticipants().iterator();
		while(oldParticipantsIterator.hasNext())
		{
		    Participant oldParticipant = (Participant)oldParticipantsIterator.next();
		    Participant participant = new Participant();
		    participant.setUserName(oldParticipant.getUserName());
		    participant.setEvent(event);
		    session.save(participant);
		    participants.add(participant);
		}
		
		event = createEvent(calendar, 
			                originalEvent.getIsInternal(), 
			                originalEvent.getIsOrganizedByGU(), 
			                originalEvent.getLastRegistrationDateTime(),
			                originalEvent.getMaximumParticipants(),
			                originalEvent.getStartDateTime(), 
			                originalEvent.getEndDateTime(),
			                originalEvent.getContactEmail(),
			                originalEvent.getContactName(),
			                originalEvent.getContactPhone(),
			                originalEvent.getPrice(),
			                locations, 
		        			participants,
		        			stateId,
		        			creator,
		        			entryFormId,
		        			session);
		
		Set eventVersions = new HashSet();
		Iterator eventVersionIterator = originalEvent.getVersions().iterator();
		while(eventVersionIterator.hasNext())
		{
			EventVersion originalEventVersion = (EventVersion)eventVersionIterator.next();
			
			EventVersion eventVersion = new EventVersion();

			eventVersion.setName(originalEventVersion.getName());
			eventVersion.setTitle(originalEventVersion.getTitle());
			eventVersion.setDescription(originalEventVersion.getDescription());
			eventVersion.setOrganizerName(originalEventVersion.getOrganizerName());
			eventVersion.setLecturer(originalEventVersion.getLecturer());
			eventVersion.setCustomLocation(originalEventVersion.getCustomLocation());
			eventVersion.setAlternativeLocation(originalEventVersion.getAlternativeLocation());
			eventVersion.setShortDescription(originalEventVersion.getShortDescription());
			eventVersion.setLongDescription(originalEventVersion.getLongDescription());
			eventVersion.setEventUrl(originalEventVersion.getEventUrl());
			//eventVersion.setContactName(originalEventVersion.getContactName());
			//eventVersion.setContactEmail(originalEventVersion.getContactEmail());
			//eventVersion.setContactPhone(originalEventVersion.getContactPhone());
			//eventVersion.setPrice(originalEventVersion.getPrice());
			
			eventVersion.setEvent(event);
			eventVersion.setLanguage(originalEventVersion.getLanguage());

			session.save(eventVersion);
		
			eventVersions.add(eventVersion);
		}

		Set eventCategories = new HashSet();
		Iterator oldEventCategoriesIterator = originalEvent.getEventCategories().iterator();
		while(oldEventCategoriesIterator.hasNext())
		{
		    EventCategory oldEventCategory = (EventCategory)oldEventCategoriesIterator.next();
		    
		    EventCategory eventCategory = new EventCategory();
		    eventCategory.setEvent(event);
		    eventCategory.setCategory(oldEventCategory.getCategory());
		    eventCategory.setEventTypeCategoryAttribute(oldEventCategory.getEventTypeCategoryAttribute());

		    session.save(eventCategory);
		    
	        eventCategories.add(eventCategory);
		}

		event.setEventCategories(eventCategories);
		event.setVersions(eventVersions);
		
        return event;
    }

    
    /**
     * This method is used to create a new Event object in the database.
     */
    
    public Event createEvent(Long calendarId, 
    						Long languageId,
            				String name, 
            				String title, 
            				String description, 
            				Boolean isInternal, 
            	            Boolean isOrganizedByGU, 
            	            String organizerName, 
            	            String lecturer, 
            	            String customLocation,
            	            String alternativeLocation,
            	            String shortDescription,
            	            String longDescription,
            	            String eventUrl,
            	            String contactName,
            	            String contactEmail,
            	            String contactPhone,
            	            String price,
            	            java.util.Calendar lastRegistrationCalendar,
            	            Integer maximumParticipants,
            	            java.util.Calendar startDateTime, 
            	            java.util.Calendar endDateTime, 
            	            Set oldLocations, 
            	            Set oldEventCategories, 
            	            Set oldParticipants,
            	            Integer stateId,
            	            String creator,
            	            Long entryFormId,
            	            String xml,
            	            Session session) throws HibernateException, Exception 
    {
        Event event = null;
 
		Calendar calendar = CalendarController.getController().getCalendar(calendarId, session);
		Language language = LanguageController.getController().getLanguage(languageId, session);
		
		Set locations = new HashSet();
		Iterator oldLocationsIterator = oldLocations.iterator();
		while(oldLocationsIterator.hasNext())
		{
		    Location location = (Location)oldLocationsIterator.next();
		    locations.add(location);
		}
		
		Set participants = new HashSet();
		Iterator oldParticipantsIterator = oldParticipants.iterator();
		while(oldParticipantsIterator.hasNext())
		{
		    Participant oldParticipant = (Participant)oldParticipantsIterator.next();
		    Participant participant = new Participant();
		    participant.setUserName(oldParticipant.getUserName());
		    participant.setEvent(event);
		    session.save(participant);
		    participants.add(participant);
		}

		event = createEvent(calendar, 
		        			isInternal, 
		                    isOrganizedByGU, 
		                    lastRegistrationCalendar,
		                    maximumParticipants,
		        			startDateTime, 
		        			endDateTime, 
		        			contactEmail,
		        			contactName,
		        			contactPhone,
		        			price,
		        			locations, 
		        			participants,
		        			stateId,
		        			creator,
		        			entryFormId,
		        			session);
		
		//Creates the master language version
		Set eventVersions = new HashSet();
		EventVersion eventVersion = new EventVersion();

		eventVersion.setName(name);
		eventVersion.setTitle(title);
		eventVersion.setDescription(description);
		eventVersion.setOrganizerName(organizerName);
		eventVersion.setLecturer(lecturer);
		eventVersion.setCustomLocation(customLocation);
		eventVersion.setAlternativeLocation(alternativeLocation);
		eventVersion.setShortDescription(shortDescription);
		eventVersion.setLongDescription(longDescription);
		eventVersion.setEventUrl(eventUrl);
		//eventVersion.setContactName(contactName);
		//eventVersion.setContactEmail(contactEmail);
		//eventVersion.setContactPhone(contactPhone);
		//eventVersion.setPrice(price);

		eventVersion.setEvent(event);
		eventVersion.setLanguage(language);

		session.save(eventVersion);

		eventVersions.add(eventVersion);

		
		Set eventCategories = new HashSet();
		Iterator oldEventCategoriesIterator = oldEventCategories.iterator();
		while(oldEventCategoriesIterator.hasNext())
		{
		    EventCategory oldEventCategory = (EventCategory)oldEventCategoriesIterator.next();
		    
		    EventCategory eventCategory = new EventCategory();
		    eventCategory.setEvent(event);
		    eventCategory.setCategory(oldEventCategory.getCategory());
		    eventCategory.setEventTypeCategoryAttribute(oldEventCategory.getEventTypeCategoryAttribute());
		    session.save(eventCategory);
		    
	        eventCategories.add(eventCategory);
		}

		event.setVersions(eventVersions);
		event.setEventCategories(eventCategories);
		
        return event;
    }

    
    
    /**
     * This method is used to create a new Event object in the database.
     */

    public Event createEvent(Long calendarId, 
    		Long languageId,
			String name, 
			String title, 
			String description, 
			Boolean isInternal, 
            Boolean isOrganizedByGU, 
            String organizerName, 
            String lecturer, 
            String customLocation,
            String alternativeLocation,
            String shortDescription,
            String longDescription,
            String eventUrl,
            String contactName,
            String contactEmail,
            String contactPhone,
            String price,
            java.util.Calendar lastRegistrationCalendar,
            Integer maximumParticipants,
            java.util.Calendar startDateTime, 
            java.util.Calendar endDateTime, 
            String[] locationId, 
            Map categoryAttributes, 
            String[] participantUserName,
            Integer stateId,
            String creator,
            Long entryFormId,
            String xml,
            Session session) throws HibernateException, Exception 
	{
		Event event = null;
		
		Calendar calendar = CalendarController.getController().getCalendar(calendarId, session);
		Language language = null;
		if(languageId != null)
			language = LanguageController.getController().getLanguage(languageId, session);
		else
			language = LanguageController.getController().getMasterLanguage(session);
		
		Set locations = new HashSet();
		if(locationId != null)
		{
			for(int i=0; i<locationId.length; i++)
			{
				if(!locationId[i].equals(""))
				{
					Location location = LocationController.getController().getLocation(new Long(locationId[i]), session);
					locations.add(location);
				}
			}
		}
		
		Set participants = new HashSet();
		if(participantUserName != null)
		{
			for(int i=0; i<participantUserName.length; i++)
			{
				Participant participant = new Participant();
				participant.setUserName(participantUserName[i]);
				participant.setEvent(event);
				session.save(participant);
				participants.add(participant);
			}
		}
		
		event = createEvent(calendar, 
					isInternal, 
		            isOrganizedByGU, 
		            lastRegistrationCalendar,
		            maximumParticipants,
					startDateTime, 
					endDateTime, 
					contactEmail,
					contactName,
					contactPhone,
					price,
					locations, 
					participants,
					stateId,
					creator,
					entryFormId,
					session);
		
		//Creates the master language version
		Set eventVersions = new HashSet();
		EventVersion eventVersion = new EventVersion();
		
		eventVersion.setName(name);
		eventVersion.setTitle(title);
		eventVersion.setDescription(description);
		eventVersion.setOrganizerName(organizerName);
		eventVersion.setLecturer(lecturer);
		eventVersion.setCustomLocation(customLocation);
		eventVersion.setAlternativeLocation(alternativeLocation);
		eventVersion.setShortDescription(shortDescription);
		eventVersion.setLongDescription(longDescription);
		eventVersion.setEventUrl(eventUrl);
		eventVersion.setAttributes(xml);
		//eventVersion.setContactName(contactName);
		//eventVersion.setContactEmail(contactEmail);
		//eventVersion.setContactPhone(contactPhone);
		//eventVersion.setPrice(price);

		eventVersion.setEvent(event);
		eventVersion.setLanguage(language);

		session.save(eventVersion);

		eventVersions.add(eventVersion);
		
		
		Set eventCategories = new HashSet();
		if(categoryAttributes != null)
		{
			Iterator categoryAttributesIterator = categoryAttributes.keySet().iterator();
			while(categoryAttributesIterator.hasNext())
			{
				String categoryAttributeId = (String)categoryAttributesIterator.next(); 
				log.info("categoryAttributeId:" + categoryAttributeId);
				EventTypeCategoryAttribute eventTypeCategoryAttribute = EventTypeCategoryAttributeController.getController().getEventTypeCategoryAttribute(new Long(categoryAttributeId), session);
				 
				String[] categoriesArray = (String[])categoryAttributes.get(categoryAttributeId);
				for(int i=0; i < categoriesArray.length; i++)
				{
				    Category category = CategoryController.getController().getCategory(new Long(categoriesArray[i]), session);
				    
				    EventCategory eventCategory = new EventCategory();
				    eventCategory.setEvent(event);
				    eventCategory.setCategory(category);
				    eventCategory.setEventTypeCategoryAttribute(eventTypeCategoryAttribute);
				    session.save(eventCategory);
				    
				    eventCategories.add(eventCategory);
				}
			}
		}
		
		event.setEventCategories(eventCategories);
		event.setVersions(eventVersions);
		
		return event;
	}
    
    
    /**
     * This method is used to create a new Event object in the database inside a transaction.
     */
    
    public Event createEvent(Calendar owningCalendar, 
            				Boolean isInternal, 
            	            Boolean isOrganizedByGU, 
            	            java.util.Calendar lastRegistrationCalendar,
            	            Integer maximumParticipants,
            	            java.util.Calendar startDateTime, 
            				java.util.Calendar endDateTime, 
            				String contactEmail,
            				String contactName,
    						String contactPhone,
    						String price,
            				Set locations, 
            				Set participants,
            				Integer stateId,
            				String creator,
            				Long entryFormId,
            				Session session) throws HibernateException, Exception 
    {
        Event event = new Event();
        event.setIsInternal(isInternal);
        event.setIsOrganizedByGU(isOrganizedByGU);
        event.setMaximumParticipants(maximumParticipants);
        event.setLastRegistrationDateTime(lastRegistrationCalendar);
        event.setStartDateTime(startDateTime);
        event.setEndDateTime(endDateTime);
        event.setContactEmail(contactEmail);
        event.setContactName(contactName);
        event.setContactPhone(contactPhone);
        event.setPrice(price);
        event.setStateId(stateId);
        event.setCreator(creator);
        event.setEntryFormId(entryFormId);
        
        event.setOwningCalendar(owningCalendar);
        event.getCalendars().add(owningCalendar);
        event.setLocations(locations);
        event.setParticipants(participants);
        owningCalendar.getEvents().add(event);
        
        session.save(event);
        
        return event;
    }
    
    /**
     * Updates an event.
     * 
     * @throws Exception
     */
    
    public void updateEvent(
            Long id, 
            Long languageId,
            String name, 
            String title,
            String description, 
            Boolean isInternal, 
            Boolean isOrganizedByGU, 
            String organizerName, 
            String lecturer, 
            String customLocation,
            String alternativeLocation,
            String shortDescription,
            String longDescription,
            String eventUrl,
            String contactName,
            String contactEmail,
            String contactPhone,
            String price,
            java.util.Calendar lastRegistrationCalendar,
            Integer maximumParticipants,
            java.util.Calendar startDateTime, 
            java.util.Calendar endDateTime, 
            String[] locationId, 
            Map categoryAttributes, 
            String[] participantUserName,
            Long entryFormId,
            String xml,
            Session session) throws Exception 
    {

        Event event = getEvent(id, session);
        Language language = LanguageController.getController().getLanguage(languageId, session);
        
		Set locations = new HashSet();
		if(locationId != null)
		{
			for(int i=0; i<locationId.length; i++)
			{
			    if(!locationId[i].equalsIgnoreCase(""))
			    {
			        Location location = LocationController.getController().getLocation(new Long(locationId[i]), session);
			        locations.add(location);
			    }
			}
		}
		
	    log.info("participantUserName: " + participantUserName);
		Set participants = new HashSet();
		if(participantUserName != null)
		{
			for(int i=0; i<participantUserName.length; i++)
			{
			    Participant participant = new Participant();
			    participant.setUserName(participantUserName[i]);
			    participant.setEvent(event);
			    log.info("Adding " + participantUserName[i]);

			    session.save(participant);
			    participants.add(participant);
			}
		}
		
		updateEvent(
		        event,
		        language,
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
		        startDateTime, 
		        endDateTime, 
		        locations, 
		        categoryAttributes, 
		        participants, 
		        entryFormId,
		        xml,
		        session);
		
    }
    
    /**
     * Updates an event inside an transaction.
     * 
     * @throws Exception
     */
    
    public void updateEvent(
            Event event, 
            Language language,
            String name, 
            String title, 
            String description, 
            Boolean isInternal, 
            Boolean isOrganizedByGU, 
            String organizerName, 
            String lecturer, 
            String customLocation,
            String alternativeLocation,
            String shortDescription,
            String longDescription,
            String eventUrl,
            String contactName,
            String contactEmail,
            String contactPhone,
            String price,
            java.util.Calendar lastRegistrationCalendar,
            Integer maximumParticipants,
            java.util.Calendar startDateTime, 
            java.util.Calendar endDateTime, 
            Set locations, 
            Map categoryAttributes, 
            Set participants, 
            Long entryFormId,
            String xml,
            Session session) throws Exception 
    {
    	EventVersion eventVersion = null;
        Iterator eventVersions = event.getVersions().iterator();
        while(eventVersions.hasNext())
        {
        	EventVersion currentEventVersion = (EventVersion)eventVersions.next();
        	if(currentEventVersion.getVersionLanguageId().equals(language.getId()))
        	{
        		eventVersion = currentEventVersion;
        		break;
        	}
        }
        
        if(eventVersion == null)
        {
        	eventVersion = new EventVersion();
        	eventVersion.setLanguage(language);
        	eventVersion.setEvent(event);
        	eventVersion.setName(name);
    		eventVersion.setTitle(title);
        	eventVersion.setDescription(description);
            eventVersion.setOrganizerName(organizerName);
            eventVersion.setLecturer(lecturer);
            eventVersion.setCustomLocation(customLocation);
            eventVersion.setAlternativeLocation(alternativeLocation);
            eventVersion.setShortDescription(shortDescription);
            eventVersion.setLongDescription(longDescription);
            eventVersion.setEventUrl(eventUrl);
            //eventVersion.setContactName(contactName);
            //eventVersion.setContactEmail(contactEmail);
            //eventVersion.setContactPhone(contactPhone);
            //eventVersion.setPrice(price);
            eventVersion.setAttributes(xml);
            
        	session.save(eventVersion);
        }
        else
        {
        	eventVersion.setName(name);
    		eventVersion.setTitle(title);
        	eventVersion.setDescription(description);
            eventVersion.setOrganizerName(organizerName);
            eventVersion.setLecturer(lecturer);
            eventVersion.setCustomLocation(customLocation);
            eventVersion.setAlternativeLocation(alternativeLocation);
            eventVersion.setShortDescription(shortDescription);
            eventVersion.setLongDescription(longDescription);
            eventVersion.setEventUrl(eventUrl);
            //eventVersion.setContactName(contactName);
            //eventVersion.setContactEmail(contactEmail);
            //eventVersion.setContactPhone(contactPhone);
            //eventVersion.setPrice(price);
            eventVersion.setAttributes(xml);
            
    		session.update(eventVersion);
        }

//        event.setName(name);
        //event.setDescription(description);
        event.setIsInternal(isInternal);
        event.setIsOrganizedByGU(isOrganizedByGU);
//        event.setOrganizerName(organizerName);
//        event.setLecturer(lecturer);
//        event.setCustomLocation(customLocation);
//        event.setAlternativeLocation(alternativeLocation);
//        event.setShortDescription(shortDescription);
//        event.setLongDescription(longDescription);
//        event.setEventUrl(eventUrl);
        event.setContactName(contactName);
        event.setContactEmail(contactEmail);
        event.setContactPhone(contactPhone);
        event.setPrice(price);
        event.setMaximumParticipants(maximumParticipants);
        event.setLastRegistrationDateTime(lastRegistrationCalendar);
        event.setStartDateTime(startDateTime);
        event.setEndDateTime(endDateTime);
        event.setLocations(locations);
        event.setEntryFormId(entryFormId);
        //event.setAttributes(xml);
        
        Iterator eventCategoryIterator = event.getEventCategories().iterator();
		while(eventCategoryIterator.hasNext())
		{
		    EventCategory eventCategory = (EventCategory)eventCategoryIterator.next();
		    session.delete(eventCategory);
		}
		
        Set eventCategories = new HashSet();
		if(categoryAttributes != null)
		{
			Iterator categoryAttributesIterator = categoryAttributes.keySet().iterator();
			while(categoryAttributesIterator.hasNext())
			{
			    String categoryAttributeId = (String)categoryAttributesIterator.next(); 
			    log.info("categoryAttributeId:" + categoryAttributeId);
			    EventTypeCategoryAttribute eventTypeCategoryAttribute = EventTypeCategoryAttributeController.getController().getEventTypeCategoryAttribute(new Long(categoryAttributeId), session);
			     
			    String[] categoriesArray = (String[])categoryAttributes.get(categoryAttributeId);
			    for(int i=0; i < categoriesArray.length; i++)
			    {
			        Category category = CategoryController.getController().getCategory(new Long(categoriesArray[i]), session);
			        
			        EventCategory eventCategory = new EventCategory();
				    eventCategory.setEvent(event);
			    	
				    eventCategory.setCategory(category);
				    eventCategory.setEventTypeCategoryAttribute(eventTypeCategoryAttribute);
				    session.save(eventCategory);
				    
			        eventCategories.add(eventCategory);
			    }
			}
		}
		event.setEventCategories(eventCategories);
        
        event.setParticipants(participants);
        
		session.update(event);
		
		if(event.getStateId().equals(Event.STATE_PUBLISHED))
		    new RemoteCacheUpdater().updateRemoteCaches(event.getCalendars());
	}
    

    /**
     * This method is used to create a new Event object in the database.
     */
    
    public void linkEvent(Long calendarId, Long eventId, Session session) throws HibernateException, Exception 
    {
        Calendar calendar = CalendarController.getController().getCalendar(calendarId, session);
        Event event = EventController.getController().getEvent(eventId, session);		

        event.getCalendars().add(calendar);
        
		new RemoteCacheUpdater().updateRemoteCaches(event.getCalendars());
		//new RemoteCacheUpdater().updateRemoteCaches(calendarId);
    }

    /**
     * Submits an event for publication.
     * 
     * @throws Exception
     */
    
    public void submitForPublishEvent(Long id, String publishEventUrl, String languageCode, InfoGluePrincipalBean infoGluePrincipal, Session session) throws Exception 
    {
		Event event = getEvent(id, session);
		event.setStateId(Event.STATE_PUBLISH);
		EventVersion eventVersion = getEventVersion(event, languageCode, session);
		
        if(useEventPublishing())
        {
            try
            {
                EventController.getController().notifyPublisher(event, eventVersion, publishEventUrl, infoGluePrincipal);
            }
            catch(Exception e)
            {
                log.warn("An error occcurred:" + e.getMessage(), e);
            }
        }

    }    

    
    /**
     * Publishes an event.
     * 
     * @throws Exception
     */
    
    public void publishEvent(Long id, String publishedEventUrl, String languageCode, InfoGluePrincipalBean infoGluePrincipal, Session session) throws Exception 
    {
		Event event = getEvent(id, session);
		event.setStateId(Event.STATE_PUBLISHED);
		EventVersion eventVersion = getEventVersion(event, languageCode, session);

		new RemoteCacheUpdater().updateRemoteCaches(event.getCalendars());
		//new RemoteCacheUpdater().updateRemoteCaches(event.getOwningCalendar().getId());
		
        if(useGlobalEventNotification())
        {
            try
            {
                EventController.getController().notifySubscribers(event, eventVersion, publishedEventUrl, infoGluePrincipal);
            }
            catch(Exception e)
            {
                log.warn("An error occcurred:" + e.getMessage(), e);
            }
        }

    }    
    
    /**
     * This method returns a Event based on it's primary key inside a transaction
     * @return Event
     * @throws Exception
     */

    public Event getEvent(Long id, Session session) throws Exception
    {
        Event event = (Event)session.load(Event.class, id);
		
		return event;
    }

    /**
     * This method returns a Event based on it's primary key inside a transaction
     * @return Event
     * @throws Exception
     */

    public EventVersion getEventVersion(Long id, Session session) throws Exception
    {
        EventVersion eventVersion = (EventVersion)session.load(EventVersion.class, id);
		
		return eventVersion;
    }

    public EventVersion getEventVersion(Event event, String languageCode, Session session)
    {        
        if(event == null)
    		return null;

    	EventVersion eventVersion = null;

    	try
    	{
    		Language language = LanguageController.getController().getLanguageWithCode(languageCode, session);
	    	
	    	Iterator eventVersionsIterator = event.getVersions().iterator();
	        while(eventVersionsIterator.hasNext())
	        {
	        	EventVersion currentEventVersion = (EventVersion)eventVersionsIterator.next();
	        	if(currentEventVersion.getVersionLanguageId().equals(language.getId()))
	        	{
	        		eventVersion = currentEventVersion;
	        		break;
	        	}
	        }
	        
	        if(eventVersion == null && event.getVersions().size() > 0)
	        	eventVersion = (EventVersion)event.getVersions().toArray()[0];
    	}
    	catch(Exception e)
    	{
    		log.error("Error when getting event version for event: " + event + ":" + e.getMessage(), e); 
    	}
    	
        return eventVersion;
    }
    
    public List<EventVersion> getEventVersions(List<Long> ids, Language language, Session session) throws Exception
    {
    	if(ids != null && ids.size() != 0){
        Criteria criteria = session.createCriteria(EventVersion.class);
        criteria.createCriteria("event").add(Restrictions.in("id", ids));
        criteria.add(Restrictions.eq("language", language));

		return criteria.list();
    }
    	else{
    		return new ArrayList<EventVersion>();
    	}	
    }

    /**
     * This method returns a Event based on it's primary key inside a transaction and states if it's a read only or not
     * @return Event
     * @throws Exception
     */
/*    
    public Event getEvent(Long id, Session session, boolean readOnly) throws Exception
    {
        Event event = (Event)session.load(Event.class, id);
		session.setReadOnly(event, readOnly);
		
		return event;
    }
*/    
    
    /**
     * Gets a list of all events available for a particular day.
     * @return List of Event
     * @throws Exception
     */
    
    public List getEventList(Session session) throws Exception 
    {
        List result = null;
        
        Query q = session.createQuery("from Event event order by event.id");
   
        result = q.list();
        
        return result;
    }
    
    
    /**
     * Gets a list of all events matching the arguments given.
     * @return List of Event
     * @throws Exception
     */
    
    public List getExpiredEventList(java.util.Calendar now/*, java.util.Calendar lastCheckedDate*/, Session session) throws Exception 
    {
        java.util.Calendar recentExpirations = java.util.Calendar.getInstance();
        recentExpirations.setTime(now.getTime());
        recentExpirations.add(java.util.Calendar.HOUR_OF_DAY, -1);
        
        List result = null;
        log.info("Checking for any events which are published and which have expired just now..");
        
        Criteria criteria = session.createCriteria(Event.class);
        criteria.add(Expression.lt("endDateTime", now));
        criteria.add(Expression.gt("endDateTime", recentExpirations));

        //criteria.add(Expression.gt("endDateTime", lastCheckedDate));

        log.info("endDateTime:" + now.getTime());
        
        result = criteria.list();
        
        return result;
    }

    
    /**
     * Gets a list of all events matching the arguments given.
     * @return List of Event
     * @throws Exception
     */
    
    public List getEventList(String name,
    		java.util.Calendar startDateTime,
    		java.util.Calendar endDateTime,
    		String organizerName,
    		String lecturer,
    		String customLocation,
    		String alternativeLocation,
    		String contactName,
    		String contactEmail,
    		String contactPhone,
    		String price,
    		Integer maximumParticipants,
    		Boolean sortAscending,
    		Long categoryId,
    		Long calendarId,
    		Long locationId,
    		Session session) throws Exception 
	{
    	return getEventList(name, startDateTime, endDateTime, organizerName, lecturer, customLocation, alternativeLocation, contactName, contactEmail,
    				contactPhone, price, maximumParticipants, sortAscending, categoryId, calendarId, locationId, null, session);
	}
    /**
     * Gets a list of all events matching the arguments given.
     * @return List of Event
     * @throws Exception
     */
    
    public List getEventList(String name,
            java.util.Calendar startDateTime,
            java.util.Calendar endDateTime,
        	String organizerName,
        	String lecturer,
            String customLocation,
            String alternativeLocation,
            String contactName,
            String contactEmail,
            String contactPhone,
            String price,
            Integer maximumParticipants,
            Boolean sortAscending,
            Long categoryId,
            Long calendarId,
            Long locationId,
            Integer stateId,
            Session session) throws Exception 
    {
        List result = null;
                
        Criteria criteria = session.createCriteria(Event.class);
        
        Criteria eventVersionCriteria = criteria.createCriteria("versions");
        if(name != null && name.length() > 0)
        	eventVersionCriteria.add(Restrictions.like("name", "%" + name + "%"));
        
        if(organizerName != null && organizerName.length() > 0)
        	eventVersionCriteria.add(Restrictions.like("organizerName", "%" + organizerName + "%"));

        if(lecturer != null && lecturer.length() > 0)
        	eventVersionCriteria.add(Restrictions.like("lecturer", "%" + lecturer + "%"));

        if(customLocation != null && customLocation.length() > 0)
        	eventVersionCriteria.add(Restrictions.like("customLocation", "%" + customLocation + "%"));

        if(alternativeLocation != null && alternativeLocation.length() > 0)
        	eventVersionCriteria.add(Restrictions.like("alternativeLocation", "%" + alternativeLocation + "%"));

        if(contactName != null && contactName.length() > 0)
        	criteria.add(Restrictions.like("contactName", "%" + contactName + "%"));

        if(contactEmail != null && contactEmail.length() > 0)
        	criteria.add(Restrictions.like("contactEmail", "%" + contactEmail + "%"));

        if(contactPhone != null && contactPhone.length() > 0)
        	criteria.add(Restrictions.like("contactPhone", "%" + contactPhone + "%"));

        if(price != null && price.length() > 0)
        	criteria.add(Restrictions.eq("price", "%" + price + "%"));

        if(maximumParticipants != null)
        	criteria.add(Restrictions.le("maximumParticipants", maximumParticipants));

        if(startDateTime != null)
        	criteria.add(Restrictions.ge("startDateTime", startDateTime));

        if(endDateTime != null)
        	criteria.add(Restrictions.le("endDateTime", endDateTime));
        
        if(sortAscending.booleanValue())
        {
        	criteria.addOrder(Order.asc("startDateTime"));
        }
        else
        {
        	criteria.addOrder(Order.desc("startDateTime"));	
        }
        
        if (stateId != null)
        {
        	criteria.add(Expression.eq("stateId", stateId));
        }
        
        result = criteria.list();
        /*
        if(name != null && name.length() > 0)
        {
            arguments.add("event.versions.name like ?");
            values.add("%" + name + "%");
        }
        if(organizerName != null && organizerName.length() > 0)
        {
            arguments.add("event.organizerName like ?");
            values.add("%" + organizerName + "%");
        }
        if(lecturer != null && lecturer.length() > 0)
        {
            arguments.add("event.lecturer like ?");
            values.add("%" + lecturer + "%");
        }
        if(customLocation != null && customLocation.length() > 0)
        {
            arguments.add("event.customLocation like ?");
            values.add("%" + customLocation + "%");
        }
        if(alternativeLocation != null && alternativeLocation.length() > 0)
        {
            arguments.add("event.alternativeLocation like ?");
            values.add("%" + alternativeLocation + "%");
        }
        if(contactName != null && contactName.length() > 0)
        {
            arguments.add("event.contactName like ?");
            values.add("%" + contactName + "%");
        }
        if(contactEmail != null && contactEmail.length() > 0)
        {
            arguments.add("event.contactEmail like ?");
            values.add("%" + contactEmail + "%");
        }
        if(contactPhone != null && contactPhone.length() > 0)
        {
            arguments.add("event.contactPhone like ?");
            values.add("%" + contactPhone + "%");
        }
        if(price != null && price.length() > 0)
        {
            arguments.add("event.price = ?");
            values.add(price);
        }
        if(maximumParticipants != null)
        {						 
            arguments.add("event.maximumParticipants = ?");
            values.add(maximumParticipants);
        }
        if(startDateTime != null)
        {						 
            arguments.add("event.startDateTime >= ?");
            values.add(startDateTime);
        }
        if(endDateTime != null)
        {						 
            arguments.add("event.endDateTime <= ?");
            values.add(endDateTime);
        }

        String argumentsSQL = "";
        Iterator argumentsIterator = arguments.iterator();
        while(argumentsIterator.hasNext())
        {
            if(argumentsSQL.length() > 0)
                argumentsSQL += " AND ";
            argumentsSQL += (String)argumentsIterator.next();
        }
        log.info("argumentsSQL:" + argumentsSQL);
        
        String order = "desc";
        if(sortAscending.booleanValue())
            order = "asc";
        
        Query q = session.createQuery("from Event event " + (argumentsSQL.length() > 0 ? "WHERE " + argumentsSQL : "") + " order by event.startDateTime " + order);
   		
        int i = 0;
        Iterator valuesIterator = values.iterator();
        while(valuesIterator.hasNext())
        {
            Object o = valuesIterator.next();
            if(o instanceof Float)
                q.setFloat(i, ((Float)o).floatValue());
            else if(o instanceof Integer)
                q.setInteger(i, ((Integer)o).intValue());
            else if(o instanceof String)
                q.setString(i, (String)o);
            else if(o instanceof java.util.Calendar)
                q.setCalendar(i, (java.util.Calendar)o);
            
            i++;
        }
        
        result = q.list();
        */
   
        if(categoryId != null)
        {
	        Iterator resultIterator = result.iterator();
	        while(resultIterator.hasNext())
	        {
	        	Event event = (Event)resultIterator.next();
	        	if(!getHasCategory(event, categoryId))
	        		resultIterator.remove();
	        }
        }

        if(calendarId != null)
        {
	        Iterator resultIterator = result.iterator();
	        while(resultIterator.hasNext())
	        {
	        	Event event = (Event)resultIterator.next();
	        	if(!getHasCalendar(event, calendarId))
	        		resultIterator.remove();
	        }
        }

        if(locationId != null)
        {
	        Iterator resultIterator = result.iterator();
	        while(resultIterator.hasNext())
	        {
	        	Event event = (Event)resultIterator.next();
	        	if(!getHasLocation(event, locationId))
	        		resultIterator.remove();
	        }
        }

        return result;
    }
    
    
    
    /**
     * Gets a list of all events available for a particular user.
     * @return List of Event
     * @throws Exception
     */
    
    public Set getEventList(java.util.Calendar startDate, java.util.Calendar endDate, String userName, List roles, List groups, Integer stateId, boolean includeLinked, boolean includeEventsCreatedByUser, Session session) throws Exception 
    {
        List result = new ArrayList();
        
        if(includeLinked == true)
        {
            String rolesSQL = getRoleSQL(roles);
            log.info("groups:" + groups.size());
	        String groupsSQL = getGroupsSQL(groups);
	        log.info("groupsSQL:" + groupsSQL);
	        String sql = "select c from Calendar c, Role cr, Group g where cr.calendar = c AND g.calendar = c " + (rolesSQL != null ? " AND cr.name IN " + rolesSQL : "") + (groupsSQL != null ? " AND g.name IN " + groupsSQL : "") + " order by c.id";
	        //String sql = "select distinct c from Calendar c, Role cr, Group g where cr.calendar = c AND g.calendar = c " + (rolesSQL != null ? " AND cr.name IN " + rolesSQL : "") + (groupsSQL != null ? " AND g.name IN " + groupsSQL : "") + " order by c.id";
	        log.info("sql:" + sql);
	        Query q = session.createQuery(sql);
	        setRoleNames(0, q, roles);
	        setGroupNames(roles.size(), q, groups);
	        List calendars = q.list();

	        Object[] calendarIdArray = new Object[calendars.size()];

	        int i = 0;
	        Iterator calendarsIterator = calendars.iterator();
	        while(calendarsIterator.hasNext())
	        {
	            Calendar calendar = (Calendar)calendarsIterator.next();
	            log.info("calendar: " + calendar.getName());
	            calendarIdArray[i] = calendar.getId();
	            i++;                
	        }
	        
            if(calendarIdArray.length > 0)
            {
	            Criteria criteria = session.createCriteria(Event.class);
	            criteria.add(Restrictions.eq("stateId", stateId));
	            criteria.add(Expression.gt("endDateTime", startDate));
	            criteria.addOrder(Order.asc("startDateTime"));
	            
	            /*
	            criteria.add(Expression.gt("endDateTime", endDate));
	            criteria.add(Expression.lt("startDateTime", startDate));
	            */
	            criteria.createCriteria("owningCalendar")
	            .add(Restrictions.not(Restrictions.in("id", calendarIdArray)));
	
	            criteria.createCriteria("calendars")
	            .add(Restrictions.in("id", calendarIdArray));

	            result = criteria.list();
            }
            	        
        }
        else
        {
	        String rolesSQL = getRoleSQL(roles);
	        log.info("groups:" + groups.size());
	        String groupsSQL = getGroupsSQL(groups);
	        log.info("groupsSQL:" + groupsSQL);
	        String sql = "select event from Event event, Calendar c, Role cr, Group g where event.owningCalendar = c AND cr.calendar = c AND g.calendar = c AND event.stateId = ? AND event.endDateTime >= ? " + (rolesSQL != null ? " AND cr.name IN " + rolesSQL : "") + (groupsSQL != null ? " AND g.name IN " + groupsSQL : "") + " order by event.startDateTime";
	        //String sql = "select distinct event from Event event, Calendar c, Role cr, Group g where event.owningCalendar = c AND cr.calendar = c AND g.calendar = c AND event.stateId = ? AND event.endDateTime >= ? " + (rolesSQL != null ? " AND cr.name IN " + rolesSQL : "") + (groupsSQL != null ? " AND g.name IN " + groupsSQL : "") + " order by event.startDateTime";
	        log.info("sql:" + sql);
	        Query q = session.createQuery(sql);
	        q.setInteger(0, stateId.intValue());
	        q.setCalendar(1, startDate);
	        log.info("startDate:" + startDate.getTime());
	        setRoleNames(2, q, roles);
	        /*
	        q.setCalendar(1, startDate);
	        q.setCalendar(2, endDate);
	        setRoleNames(3, q, roles);
	        */
	        setGroupNames(roles.size() + 2, q, groups);
	        
	        result = q.list();
        }
        
        log.info("result:" + result.size());
        
        Set set = new LinkedHashSet();
        set.addAll(result);	

        if(includeEventsCreatedByUser)
        {
            Criteria criteria = session.createCriteria(Event.class);
            criteria.add(Restrictions.eq("stateId", stateId));
            criteria.add(Restrictions.eq("creator", userName));
            criteria.add(Expression.gt("endDateTime", startDate));
            criteria.addOrder(Order.asc("startDateTime"));

            /*
            criteria.add(Expression.gt("endDateTime", endDate));
            criteria.add(Expression.lt("startDateTime", startDate));
            */
            
            set.addAll(criteria.list());	
        }
        List sortedList = new ArrayList();
        sortedList.addAll(set);
        
        Collections.sort(sortedList, new EventComparator());
        set.clear();
        
        set.addAll(sortedList);
        
        return set;
    }


    /**
     * Gets a list of all events available for a particular user which are in working mode.
     * @return List of Event
     * @throws Exception
     */
    
    public Set getMyWorkingEventList(String userName, List roles, List groups, Session session) throws Exception 
    {
        java.util.Calendar now = java.util.Calendar.getInstance();
        java.util.Calendar endDate = java.util.Calendar.getInstance();
        endDate.add(java.util.Calendar.YEAR, 5);
        
        Set result = getEventList(now, endDate, userName, roles, groups, Event.STATE_WORKING, false, true, session);
        
        return result;
    }

    
    /**
     * Gets a list of all events available for a particular user which are in working mode.
     * @return List of Event
     * @throws Exception
     */
    
    public Set getWaitingEventList(String userName, List roles, List groups, Session session) throws Exception 
    {
        java.util.Calendar now = java.util.Calendar.getInstance();
        java.util.Calendar endDate = java.util.Calendar.getInstance();
        endDate.add(java.util.Calendar.YEAR, 5);
        
        Set result = getEventList(now, endDate, userName, roles, groups, Event.STATE_PUBLISH, false, false, session);
        
        return result;
    }

    /**
     * Gets a list of all events available for a particular user which are in working mode.
     * @return List of Event
     * @throws Exception
     */
    
    public Set getPublishedEventList(String userName, List roles, List groups, Long categoryId, Session session) throws Exception 
    {
        java.util.Calendar now = java.util.Calendar.getInstance();
        java.util.Calendar endDate = java.util.Calendar.getInstance();
        endDate.add(java.util.Calendar.YEAR, 5);
        
        Set result = getEventList(now, endDate, userName, roles, groups, Event.STATE_PUBLISHED, false, true, session);
        
        if(categoryId != null)
        {
	        Iterator resultIterator = result.iterator();
	        while(resultIterator.hasNext())
	        {
	        	Event event = (Event)resultIterator.next();
	        	if(!getHasCategory(event, categoryId))
	        		resultIterator.remove();
	        }
        }
        
        return result;
    }

    public boolean getHasCategory(Event event, Long categoryId)
    {        
        Iterator i = event.getEventCategories().iterator();
        while(i.hasNext())
        {
            EventCategory eventCategory = (EventCategory)i.next();
            if(eventCategory.getCategory().getId().equals(categoryId))
                return true;
        }

        return false;
    }

    public boolean getHasCalendar(Event event, Long calendarId)
    {        
    	if(event.getOwningCalendar() != null)
    		return calendarId.equals(event.getOwningCalendar().getId());
    	else
    		return false;
    }

    public boolean getHasLocation(Event event, Long locationId)
    {        
        Iterator i = event.getLocations().iterator();
        while(i.hasNext())
        {
            Location location = (Location)i.next();
            if(location.getId().equals(locationId))
                return true;
        }

        return false;
    }

    /**
     * Gets a list of all events available for a particular user which are in working mode.
     * @return List of Event
     * @throws Exception
     */
    
    public Set getLinkedPublishedEventList(String userName, List roles, List groups, Long categoryId, Session session) throws Exception 
    {
        java.util.Calendar now = java.util.Calendar.getInstance();
        java.util.Calendar endDate = java.util.Calendar.getInstance();
        endDate.add(java.util.Calendar.YEAR, 5);
        
        Set result = getEventList(now, endDate, userName, roles, groups, Event.STATE_PUBLISHED, true, true, session);
        
        if(categoryId != null)
        {
	        Iterator resultIterator = result.iterator();
	        while(resultIterator.hasNext())
	        {
	        	Event event = (Event)resultIterator.next();
	        	if(!getHasCategory(event, categoryId))
	        		resultIterator.remove();
	        }
        }

        return result;
    }

    /**
     * This method returns a list of Events based on a number of parameters
     * @return List
     * @throws Exception
     */
    
    public Set getEventList(Long id, Integer stateId, java.util.Calendar startDate, java.util.Calendar endDate, Session session) throws Exception
    {
        Set list = null;
        
		Calendar calendar = CalendarController.getController().getCalendar(id, session);
		list = getEventList(calendar, stateId, startDate, endDate, session);
		
		return list;
    }

    public Set getEventList(String[] calendarIds, String categoryAttribute, String[] categoryNames, String includedLanguages, java.util.Calendar startCalendar, java.util.Calendar endCalendar, String freeText, Session session) throws Exception 
    {
    	return getEventList(calendarIds, categoryAttribute, categoryNames, includedLanguages, startCalendar, endCalendar, freeText, null, session);
    }
    
    /**
     * Gets a list of all events available for a particular calendar with the optional categories.
     * @return List of Event
     * @throws Exception
     */
    
    public Set getEventList(String[] calendarIds, String categoryAttribute, String[] categoryNames, String includedLanguages, java.util.Calendar startCalendar, java.util.Calendar endCalendar, String freeText, Integer numberOfItems, Session session) throws Exception 
    {
        List result = null;
        
        String calendarSQL = null;
        if(calendarIds != null && calendarIds.length > 0)
        {
            calendarSQL = "(";
	        for(int i=0; i<calendarIds.length; i++)
	        {
	            String calendarIdString = calendarIds[i];

	            try
	            {
	                Integer calendarId = new Integer(calendarIdString);
	            }
	            catch(Exception e)
	            {
	                log.warn("An invalid calendarId was given:" + e.getMessage());
	                return null;
	            }
	            
	            if(i > 0)
	                calendarSQL += ",";
	            
	            calendarSQL += calendarIdString;
	        }
	        calendarSQL += ")";
        }
        else
        {
            return null;
        }

        Object[] calendarIdArray = new Object[calendarIds.length];
        for(int i=0; i<calendarIds.length; i++)
            calendarIdArray[i] = new Long(calendarIds[i]);

        Set set = new LinkedHashSet();

        if(calendarIdArray.length > 0)
        {
	        Criteria criteria = session.createCriteria(Event.class);
	        criteria.add(Expression.eq("stateId", Event.STATE_PUBLISHED));

	        Criteria versionsCriteria = criteria.createAlias("versions", "v");

	        if(startCalendar != null && endCalendar != null)
	        {
		        if(startCalendar.get(java.util.Calendar.YEAR) == endCalendar.get(java.util.Calendar.YEAR) && startCalendar.get(java.util.Calendar.DAY_OF_YEAR) == endCalendar.get(java.util.Calendar.DAY_OF_YEAR))
		        {
		        	startCalendar.set(java.util.Calendar.HOUR_OF_DAY, 23);
		        	endCalendar.set(java.util.Calendar.HOUR_OF_DAY, 1);
		        	criteria.add(Expression.and(Expression.le("startDateTime", startCalendar), Expression.ge("endDateTime", endCalendar)));
		        }
		        else
		        	criteria.add(Expression.or(Expression.and(Expression.ge("startDateTime", startCalendar), Expression.le("startDateTime", endCalendar)), Expression.and(Expression.ge("endDateTime", endCalendar),Expression.le("endDateTime", endCalendar))));
	        }
	        else
	        {
	        	criteria.add(Expression.gt("endDateTime", java.util.Calendar.getInstance()));
	        }
	        
	        criteria.add(Expression.eq("stateId", Event.STATE_PUBLISHED));
	        criteria.addOrder(Order.asc("startDateTime"));
	        criteria.createCriteria("calendars")
	        .add(Expression.in("id", calendarIdArray));

	        Criteria eventCategoriesCriteria = null;
	        log.info("categoryAttribute:" + categoryAttribute);
	        if(categoryAttribute != null && !categoryAttribute.equalsIgnoreCase(""))
	        {
	            log.info("categoryAttribute:" + categoryAttribute);
	            eventCategoriesCriteria = criteria.createCriteria("eventCategories");
	            eventCategoriesCriteria.createCriteria("eventTypeCategoryAttribute")
	            .add(Expression.eq("internalName", categoryAttribute));
	        }

	        Criteria languageVersionCriteria = null;
	        log.info("includedLanguages:" + includedLanguages);
	        if(includedLanguages != null && !includedLanguages.equalsIgnoreCase("") && !includedLanguages.equalsIgnoreCase("*"))
	        {
	        	//languageVersionCriteria = criteria.createCriteria("versions");
	        	versionsCriteria.createCriteria("v.language").add(Expression.eq("isoCode", includedLanguages));
	        }

	        if(categoryNames != null && categoryNames.length > 0 && !categoryNames[0].equalsIgnoreCase(""))
	        {
	            log.info("categoryNames[0]:" + categoryNames[0]);
	            if(eventCategoriesCriteria == null)
		            eventCategoriesCriteria = criteria.createCriteria("eventCategories");

	            eventCategoriesCriteria.createCriteria("category")
	            .add(Expression.in("internalName", categoryNames));
	        }

	        if(freeText != null && !freeText.equals(""))
	        {
	        	Criterion nameRestriction = Restrictions.like("name", "%" + freeText + "%");
	        	Criterion organizerNameRestriction = Restrictions.like("organizerName", "%" + freeText + "%");
	        	
	        	Junction d1 = Restrictions.disjunction()
	            .add(Restrictions.like("v.name", "%" + freeText + "%"))
	            .add(Restrictions.like("v.description", "%" + freeText + "%"))
	            .add(Restrictions.like("v.lecturer", "%" + freeText + "%"))
	            .add(Restrictions.like("v.longDescription", "%" + freeText + "%"))
	            .add(Restrictions.like("v.shortDescription", "%" + freeText + "%"))
	            .add(Restrictions.like("v.organizerName", "%" + freeText + "%"))
	            .add(Restrictions.like("v.customLocation", "%" + freeText + "%"))
	            .add(Restrictions.like("v.eventUrl", "%" + freeText + "%"))
	            .add(Restrictions.like("v.alternativeLocation", "%" + freeText + "%"))
	            .add(Restrictions.like("name", "%" + freeText + "%"))
	            .add(Restrictions.like("description", "%" + freeText + "%"))
	            .add(Restrictions.like("contactName", "%" + freeText + "%"))
	            .add(Restrictions.like("lecturer", "%" + freeText + "%"))
	            .add(Restrictions.like("longDescription", "%" + freeText + "%"))
	            .add(Restrictions.like("contactEmail", "%" + freeText + "%"))
	            .add(Restrictions.like("shortDescription", "%" + freeText + "%"))
	            .add(Restrictions.like("organizerName", "%" + freeText + "%"))
	            .add(Restrictions.like("contactPhone", "%" + freeText + "%"))
	            .add(Restrictions.like("price", "%" + freeText + "%"))
	            .add(Restrictions.like("customLocation", "%" + freeText + "%"))
	            .add(Restrictions.like("eventUrl", "%" + freeText + "%"))
	            .add(Restrictions.like("alternativeLocation", "%" + freeText + "%"));
	            
	        	criteria.add(d1);
	        }
	        
	        if(numberOfItems != null)
	        	criteria.setMaxResults(numberOfItems);
	        
	        result = criteria.list();

	        log.info("result:" + result.size());
	        
	        set.addAll(result);	
        }
        
        return set;
    }
    
    /**
     * Gets a list of all events available for a particular calendar with the optional categories.
     * @return List of Event
     * @throws Exception
     */
    
    public Set getTinyEventList(String[] calendarIds, String categoryAttribute, String[] categoryNames, String includedLanguages, java.util.Calendar startCalendar, java.util.Calendar endCalendar, String freeText, Integer numberOfItems, Session session) throws Exception 
    {
    	Timer t = new Timer();
    	
        List result = null;
        
        String calendarSQL = null;
        if(calendarIds != null && calendarIds.length > 0)
        {
            calendarSQL = "(";
	        for(int i=0; i<calendarIds.length; i++)
	        {
	            String calendarIdString = calendarIds[i];

	            try
	            {
	                Integer calendarId = new Integer(calendarIdString);
	            }
	            catch(Exception e)
	            {
	                log.warn("An invalid calendarId was given:" + e.getMessage());
	                return null;
	            }
	            
	            if(i > 0)
	                calendarSQL += ",";
	            
	            calendarSQL += calendarIdString;
	        }
	        calendarSQL += ")";
        }
        else
        {
            return null;
        }

        Object[] calendarIdArray = new Object[calendarIds.length];
        for(int i=0; i<calendarIds.length; i++)
            calendarIdArray[i] = new Long(calendarIds[i]);

        Set set = new LinkedHashSet();

        if(calendarIdArray.length > 0)
        {
	        Criteria criteria = session.createCriteria(EventTiny.class);
	        criteria.add(Expression.eq("stateId", Event.STATE_PUBLISHED));

	        Criteria versionsCriteria = criteria.createAlias("versions", "v");

	        if(startCalendar != null && endCalendar != null)
	        {
		        if(startCalendar.get(java.util.Calendar.YEAR) == endCalendar.get(java.util.Calendar.YEAR) && startCalendar.get(java.util.Calendar.DAY_OF_YEAR) == endCalendar.get(java.util.Calendar.DAY_OF_YEAR))
		        {
		        	startCalendar.set(java.util.Calendar.HOUR_OF_DAY, 23);
		        	endCalendar.set(java.util.Calendar.HOUR_OF_DAY, 1);
		        	criteria.add(Expression.and(Expression.le("startDateTime", startCalendar), Expression.ge("endDateTime", endCalendar)));
		        }
		        else
		        	criteria.add(Expression.or(Expression.and(Expression.ge("startDateTime", startCalendar), Expression.le("startDateTime", endCalendar)), Expression.and(Expression.ge("endDateTime", endCalendar),Expression.le("endDateTime", endCalendar))));
	        }
	        else
	        {
	        	criteria.add(Expression.gt("endDateTime", java.util.Calendar.getInstance()));
	        }
	        
	        criteria.add(Expression.eq("stateId", Event.STATE_PUBLISHED));
	        criteria.addOrder(Order.asc("startDateTime"));
	        criteria.createCriteria("calendars")
	        .add(Expression.in("id", calendarIdArray));

	        Criteria eventCategoriesCriteria = null;
	        log.info("categoryAttribute:" + categoryAttribute);
	        if(categoryAttribute != null && !categoryAttribute.equalsIgnoreCase(""))
	        {
	            log.info("categoryAttribute:" + categoryAttribute);
	            eventCategoriesCriteria = criteria.createCriteria("eventCategories");
	            eventCategoriesCriteria.createCriteria("eventTypeCategoryAttribute")
	            .add(Expression.eq("internalName", categoryAttribute));
	        }

	        Criteria languageVersionCriteria = null;
	        log.info("includedLanguages:" + includedLanguages);
	        if(includedLanguages != null && !includedLanguages.equalsIgnoreCase("") && !includedLanguages.equalsIgnoreCase("*"))
	        {
	        	Long includedLanguageId = LanguageController.getController().getLanguageIdForCode(includedLanguages, session);
	        	versionsCriteria.add(Expression.eq("v.languageId", includedLanguageId));
	        }

	        if(categoryNames != null && categoryNames.length > 0 && !categoryNames[0].equalsIgnoreCase(""))
	        {
	            log.info("categoryNames[0]:" + categoryNames[0]);
	            if(eventCategoriesCriteria == null)
		            eventCategoriesCriteria = criteria.createCriteria("eventCategories");

	            eventCategoriesCriteria.createCriteria("category")
	            .add(Expression.in("internalName", categoryNames));
	        }

	        if(freeText != null && !freeText.equals(""))
	        {
	        	Criterion nameRestriction = Restrictions.like("name", "%" + freeText + "%");
	        	Criterion organizerNameRestriction = Restrictions.like("organizerName", "%" + freeText + "%");
	        	
	        	Junction d1 = Restrictions.disjunction()
	            .add(Restrictions.like("v.name", "%" + freeText + "%"))
	            .add(Restrictions.like("v.description", "%" + freeText + "%"))
	            .add(Restrictions.like("v.lecturer", "%" + freeText + "%"))
	            .add(Restrictions.like("v.longDescription", "%" + freeText + "%"))
	            .add(Restrictions.like("v.shortDescription", "%" + freeText + "%"))
	            .add(Restrictions.like("v.organizerName", "%" + freeText + "%"))
	            .add(Restrictions.like("v.customLocation", "%" + freeText + "%"))
	            .add(Restrictions.like("v.eventUrl", "%" + freeText + "%"))
	            .add(Restrictions.like("v.alternativeLocation", "%" + freeText + "%"))
	            .add(Restrictions.like("name", "%" + freeText + "%"))
	            .add(Restrictions.like("description", "%" + freeText + "%"))
	            .add(Restrictions.like("contactName", "%" + freeText + "%"))
	            .add(Restrictions.like("lecturer", "%" + freeText + "%"))
	            .add(Restrictions.like("longDescription", "%" + freeText + "%"))
	            .add(Restrictions.like("contactEmail", "%" + freeText + "%"))
	            .add(Restrictions.like("shortDescription", "%" + freeText + "%"))
	            .add(Restrictions.like("organizerName", "%" + freeText + "%"))
	            .add(Restrictions.like("contactPhone", "%" + freeText + "%"))
	            .add(Restrictions.like("price", "%" + freeText + "%"))
	            .add(Restrictions.like("customLocation", "%" + freeText + "%"))
	            .add(Restrictions.like("eventUrl", "%" + freeText + "%"))
	            .add(Restrictions.like("alternativeLocation", "%" + freeText + "%"));
	            
	        	criteria.add(d1);
	        }
	        
	        if(numberOfItems != null)
	        	criteria.setMaxResults(numberOfItems);
	        
	        //t.printElapsedTime("before list");
	        try
	        {
		        result = criteria.list();
	        }
	        catch (Exception e) 
	        {
	        	log.error("Error query for tiny events:" + e.getMessage(), e);
	        	throw e;
			}
	        //t.printElapsedTime("after list: " + result.size());
	        log.info("result:" + result.size());
	        
	        set.addAll(result);	
        }
        
        return set;
    }
    
    /**
     * Gets a list of all events available for a particular calendar with the optional categories.
     * @return List of Event
     * @throws Exception
     */
    
    public Set<EventCategory> getEventCategoryList(Long categoryId, Session session) throws Exception 
    {
        List result = null;

        Set<EventCategory> set = new LinkedHashSet<EventCategory>();

        Criteria criteria = session.createCriteria(EventCategory.class);
        Criteria categoryCriteria = criteria.createCriteria("category");
        categoryCriteria.add(Expression.eq("id", categoryId));
        
        result = criteria.list();
        
        System.out.println("Antal events med denna kategori:" + result.size());
        log.info("result:" + result.size());
        
        set.addAll(result);	        
        
        return set;
    }
    
    /**
     * This method returns a list of Events based on a number of parameters within a transaction
     * @return List
     * @throws Exception
     */
    
    public Set getEventList(Calendar calendar, Integer stateId, java.util.Calendar startDate, java.util.Calendar endDate, Session session) throws Exception
    {
        Query q = session.createQuery("from Event as event inner join fetch event.owningCalendar as calendar where event.owningCalendar = ? AND event.stateId = ? AND event.startDateTime >= ? AND event.endDateTime <= ? order by event.startDateTime");
        q.setEntity(0, calendar);
        q.setInteger(1, stateId.intValue());
        q.setCalendar(2, startDate);
        q.setCalendar(3, endDate);
        
        List list = q.list();

        Set set = new LinkedHashSet();
        set.addAll(list);	

		return set;
    }


    /**
     * Gets a list of events fetched by name.
     * @return List of Event
     * @throws Exception
     */
    
    public List getEvent(String name, Session session) throws Exception 
    {
        List events = null;
        
        events = session.createQuery("from Event as event where event.name = ?").setString(0, name).list();
        
        return events;
    }
    
    
    /**
     * Deletes a event object in the database. Also cascades all events associated to it.
     * @throws Exception
     */
    
    public void deleteEventVersion(Long id, Session session) throws Exception 
    {
        EventVersion eventVersion = this.getEventVersion(id, session);
        
        if(eventVersion.getEvent().getStateId().equals(Event.STATE_PUBLISHED))
            new RemoteCacheUpdater().updateRemoteCaches(eventVersion.getEvent().getCalendars());
        
        eventVersion.getEvent().getVersions().remove(eventVersion);
        
        session.delete(eventVersion);
    }

    /**
     * Deletes a event object in the database. Also cascades all events associated to it.
     * @throws Exception
     */
    
    public void deleteEvent(Long id, Session session) throws Exception 
    {
        Event event = this.getEvent(id, session);
        
        Iterator eventCategoriesIterator = event.getEventCategories().iterator();
        while(eventCategoriesIterator.hasNext())
        {
            EventCategory eventCategory = (EventCategory)eventCategoriesIterator.next();
            session.delete(eventCategory);
            eventCategoriesIterator.remove();
        }
        
        if(event.getStateId().equals(Event.STATE_PUBLISHED))
            new RemoteCacheUpdater().updateRemoteCaches(event.getCalendars());

        session.delete(event);
    }
    
    /**
     * Deletes a link to a event object in the database.
     * @throws Exception
     */
    
    public void deleteLinkedEvent(Long id, Long calendarId, Session session) throws Exception 
    {
        Event event = this.getEvent(id, session);
        Calendar calendar = CalendarController.getController().getCalendar(calendarId, session);
        event.getCalendars().remove(calendar);
        calendar.getEvents().remove(event);
        
		new RemoteCacheUpdater().updateRemoteCaches(event.getCalendars());
		//new RemoteCacheUpdater().updateRemoteCaches(calendarId);
    }
    
    /**
     * This method emails the owner of an event the new information and an address to visit.
     * @throws Exception
     */
    
    public void notifyPublisher(Event event, EventVersion eventVersion, String publishEventUrl, InfoGluePrincipalBean infoGluePrincipal) throws Exception
    {
	    String email = "";
	    
	    try
	    {
	        List allPrincipals = new ArrayList();
	        Collection owningRoles = event.getOwningCalendar().getOwningRoles();
	        Iterator owningRolesIterator = owningRoles.iterator();
	        while(owningRolesIterator.hasNext())
	        {
	            Role role = (Role)owningRolesIterator.next();
	            
	            List principals = new ArrayList();
	            principals.addAll(AccessRightController.getController().getPrincipalsWithRole(role.getName()));
	            //List principals = RoleControllerProxy.getController().getInfoGluePrincipals(role.getName());
	            
	            Iterator userIterator = principals.iterator();
	            while(userIterator.hasNext())
	            {
	            	
	                InfoGluePrincipalBean principal = (InfoGluePrincipalBean)userIterator.next();
	                boolean hasGroup = hasUserGroup(principal, event);
	                if(hasGroup)
	                    allPrincipals.add(principal);
	            }
	        }

	        String addresses = "";
	        Iterator allPrincipalsIterator = allPrincipals.iterator();
	        while(allPrincipalsIterator.hasNext())
	        {
		        InfoGluePrincipalBean inforgluePrincipal = (InfoGluePrincipalBean)allPrincipalsIterator.next();
		        addresses += inforgluePrincipal.getEmail() + ";";
	        }

            String template;
	        
	        String contentType = PropertyHelper.getProperty("mail.contentType");
	        if(contentType == null || contentType.length() == 0)
	            contentType = "text/html";
	        
	        if(contentType.equalsIgnoreCase("text/plain"))
	            template = FileHelper.getFileAsString(new File(PropertyHelper.getProperty("contextRootPath") + "templates/newEventNotification_plain.vm"));
		    else
	            template = FileHelper.getFileAsString(new File(PropertyHelper.getProperty("contextRootPath") + "templates/newEventNotification_html.vm"));
		    
	        publishEventUrl = publishEventUrl.replaceAll("j_username", "fold1");
	        publishEventUrl = publishEventUrl.replaceAll("j_password", "fold2");
	        
		    Map parameters = new HashMap();
		    
		    parameters.put("principal", infoGluePrincipal);
		    parameters.put("event", event);
		    parameters.put("eventVersion", eventVersion);
		    parameters.put("publishEventUrl", publishEventUrl.replaceAll("\\{eventId\\}", event.getId().toString()));
		    
			StringWriter tempString = new StringWriter();
			PrintWriter pw = new PrintWriter(tempString);
			new VelocityTemplateProcessor().renderTemplate(parameters, pw, template);
			email = tempString.toString();
	    
			String systemEmailSender = PropertyHelper.getProperty("systemEmailSender");
			if(systemEmailSender == null || systemEmailSender.equalsIgnoreCase(""))
				systemEmailSender = "infoglueCalendar@" + PropertyHelper.getProperty("mail.smtp.host");

			log.info("Sending mail to:" + systemEmailSender + " and " + addresses);
			MailServiceFactory.getService().send(systemEmailSender, addresses, null, "InfoGlue Calendar - new event waiting", email, contentType, "UTF-8", null);
	    }
		catch(Exception e)
		{
			log.error("The notification was not sent. Reason:" + e.getMessage());
			log.info("The notification was not sent. Reason:" + e.getMessage(), e);
		}
		
    }

    
    /**
     * This method emails the owner of an event the new information and an address to visit.
     * @throws Exception
     */
    
    public void notifySubscribers(Event event, EventVersion eventVersion, String publishedEventUrl, InfoGluePrincipalBean infoGluePrincipal) throws Exception
    {
	    String subscriberEmails = PropertyHelper.getProperty("subscriberEmails");
	    
	    try
	    {
            String template;
	        
	        String contentType = PropertyHelper.getProperty("mail.contentType");
	        if(contentType == null || contentType.length() == 0)
	            contentType = "text/html";
	        
	        if(contentType.equalsIgnoreCase("text/plain"))
	            template = FileHelper.getFileAsString(new File(PropertyHelper.getProperty("contextRootPath") + "templates/newEventPublishedNotification_plain.vm"));
		    else
	            template = FileHelper.getFileAsString(new File(PropertyHelper.getProperty("contextRootPath") + "templates/newEventPublishedNotification_html.vm"));
		    
	        publishedEventUrl = publishedEventUrl.replaceAll("j_username", "fold1");
	        publishedEventUrl = publishedEventUrl.replaceAll("j_password", "fold2");
	        
	        Map parameters = new HashMap();
		    parameters.put("principal", infoGluePrincipal);
		    parameters.put("event", event);
		    parameters.put("eventVersion", eventVersion);
		    parameters.put("publishedEventUrl", publishedEventUrl.replaceAll("\\{eventId\\}", event.getId().toString()));
		    
			StringWriter tempString = new StringWriter();
			PrintWriter pw = new PrintWriter(tempString);
			new VelocityTemplateProcessor().renderTemplate(parameters, pw, template);
			String email = tempString.toString();
	    
			String systemEmailSender = PropertyHelper.getProperty("systemEmailSender");
			if(systemEmailSender == null || systemEmailSender.equalsIgnoreCase(""))
				systemEmailSender = "infoglueCalendar@" + PropertyHelper.getProperty("mail.smtp.host");

			log.info("Sending mail to:" + systemEmailSender + " and " + subscriberEmails);
			MailServiceFactory.getService().send(systemEmailSender, subscriberEmails, null, "InfoGlue Calendar - new event published", email, contentType, "UTF-8", null);
	    
			String subscriberString = "";
			Set subscribers = event.getOwningCalendar().getSubscriptions();
			Iterator subscribersIterator = subscribers.iterator();
			while(subscribersIterator.hasNext())
			{
			    Subscriber subscriber = (Subscriber)subscribersIterator.next();

			    if(subscriberString.length() > 0)
			        subscriberString += ";";
		        
			    subscriberString += subscriber.getEmail();
			}

		    try
		    {
		        
				log.info("Sending mail to:" + systemEmailSender + " and " + subscriberString);
				if(subscriberString != null && !subscriberString.equals(""))
					MailServiceFactory.getService().send(systemEmailSender, subscriberString, null, "InfoGlue Calendar - new event published", email, contentType, "UTF-8", null);
		    }
			catch(Exception e)
			{
				log.error("The notification was not sent to persons. Reason:" + e.getMessage());
				log.error("systemEmailSender:" + systemEmailSender);
				log.error("subscriberString:" + subscriberString);
			}

	    }
		catch(Exception e)
		{
			log.error("The notification was not sent. Reason:" + e.getMessage());
			log.info("The notification was not sent. Reason:" + e.getMessage(), e);
		}
		
    }

    /**
     * This method checks if a user has one of the roles defined in the event.
     * @param principal
     * @param event
     * @return
     * @throws Exception
     */
    public boolean hasUserGroup(InfoGluePrincipalBean principal, Event event) throws Exception
    {
        Collection owningGroups = event.getOwningCalendar().getOwningGroups();
        if(owningGroups == null || owningGroups.size() == 0)
            return true;
        
        Iterator owningGroupsIterator = owningGroups.iterator();
        while(owningGroupsIterator.hasNext())
        {
            Group group = (Group)owningGroupsIterator.next();
            
            List principals = new ArrayList();
            principals.addAll(AccessRightController.getController().getPrincipalsWithGroup(group.getName()));
            //List principals = GroupControllerProxy.getController().getInfoGluePrincipals(group.getName());

            if(principals.contains(principal))
                return true;
        }
        
        return false;
    }
    
    public List getAssetKeys()
    {
        List assetKeys = new ArrayList();
        
        int i = 0;
        String assetKey = PropertyHelper.getProperty("assetKey." + i);
        while(assetKey != null && assetKey.length() > 0)
        {
            assetKeys.add(assetKey);
            
            i++;
            assetKey = PropertyHelper.getProperty("assetKey." + i);
        }
        
        return assetKeys;
    }


    private String getRoleSQL(List roles)
    {
        String rolesSQL = null;
        if(roles != null && roles.size() > 0)
        {
            rolesSQL = "(";
	        int i = 0;
	    	Iterator iterator = roles.iterator();
	        while(iterator.hasNext())
	        {
	            String roleName = (String)iterator.next();
	            
	            if(i > 0)
	                rolesSQL += ",";
	            
	            rolesSQL += "?";
	            i++;
	        }
	        rolesSQL += ")";
        }
   
        return rolesSQL;
    }
    
    private void setRoleNames(int index, Query q, List roles)
    {
        Iterator iterator = roles.iterator();
        while(iterator.hasNext())
        {
            String roleName = (String)iterator.next();
            log.info("roleName:" + roleName);
            q.setString(index, roleName);
            index++;
        }
    }

    private String getGroupsSQL(List groups)
    {
        String groupsSQL = null;
        if(groups != null && groups.size() > 0)
        {
            groupsSQL = "(";
	        int i = 0;
	    	Iterator iterator = groups.iterator();
	        while(iterator.hasNext())
	        {
	            String roleName = (String)iterator.next();
	            
	            if(i > 0)
	                groupsSQL += ",";
	            
	            groupsSQL += "?";
	            i++;
	        }
	        groupsSQL += ")";
        }
   
        return groupsSQL;
    }
    
    private void setGroupNames(int index, Query q, List groups)
    {
        Iterator iterator = groups.iterator();
        while(iterator.hasNext())
        {
            String groupName = (String)iterator.next();
            log.info("groupName:" + groupName);

            q.setString(index, groupName);
            index++;
        }
    }

}