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
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.infoglue.calendar.actions.CalendarAbstractAction;
import org.infoglue.calendar.entities.Entry;
import org.infoglue.calendar.entities.Event;
import org.infoglue.calendar.entities.EventType;
import org.infoglue.calendar.entities.EventVersion;
import org.infoglue.calendar.entities.Language;
import org.infoglue.calendar.entities.Location;
import org.infoglue.common.contenttypeeditor.entities.ContentTypeAttribute;
import org.infoglue.common.security.beans.InfoGluePrincipalBean;
import org.infoglue.common.util.PropertyHelper;
import org.infoglue.common.util.RemoteCacheUpdater;
import org.infoglue.common.util.VelocityTemplateProcessor;
import org.infoglue.common.util.VisualFormatter;
import org.infoglue.common.util.io.FileHelper;
import org.infoglue.common.util.mail.MailServiceFactory;

public class EntryController extends BasicController
{    
    //Logger for this class
    private static Log log = LogFactory.getLog(EntryController.class);
        
    
    /**
     * Factory method to get EntryController
     * 
     * @return EntryController
     */
    
    public static EntryController getController()
    {
        return new EntryController();
    }
        
    
    /**
     * This method is used to create a new Entry object in the database.
     */
    
    public Entry createEntry(String firstName, 
				            String lastName, 
				            String email, 
				            String organisation,
				            String address,
				            String zipcode,
				            String city,
				            String phone,
				            String fax,
				            String message,
				            String xml,
							Long eventId,
							Session session) throws HibernateException, Exception 
    {
        Entry entry = null;
        
		Event event = EventController.getController().getEvent(eventId, session);
		entry = createEntry(firstName, 
					        lastName, 
					        email, 
					        organisation,
							address,
							zipcode,
							city,
							phone,
							fax,
							message,
							xml,
							event, 
							session);
			
        return entry;
    }
    
    /**
     * This method is used to create a new Entry object in the database inside a transaction.
     */
    
    public Entry createEntry(String firstName, 
            				 String lastName, 
            				 String email, 
            				 String organisation,
 				             String address,
 				             String zipcode,
 				             String city,
 				             String phone,
 				             String fax,
 				             String message,
 				             String xml,
 				             Event event, 
 				             Session session) throws HibernateException, Exception 
    {
        
        Entry entry = new Entry();
        entry.setFirstName(firstName);
        entry.setLastName(lastName);
        entry.setEmail(email);
        entry.setOrganisation(organisation);
        entry.setAddress(address);
        entry.setZipcode(zipcode);
        entry.setCity(city);
        entry.setPhone(phone);
        entry.setFax(fax);
        entry.setMessage(message);
        entry.setAttributes(xml);
        
        entry.setEvent(event);
        
        new RemoteCacheUpdater().updateRemoteCaches(event.getCalendars());
        
        session.save(entry);
        
        return entry;
    }
    
    
    /**
     * Updates an entry.
     * 
     * @throws Exception
     */

    public void updateEntry(Long id, 
            				String firstName, 
            				String lastName, 
            				String email,
            				String organisation,
				            String address,
				            String zipcode,
				            String city,
				            String phone,
				            String fax,
				            String message,
				            String xml,
				            Session session) throws Exception 
    {
		Entry entry = getEntry(id, session);
		
		updateEntry(entry, 
		        	firstName, 
		        	lastName, 
		        	email, 
		        	organisation,
					address,
					zipcode,
					city,
					phone,
					fax,
					message,
					xml,
					session);
    }

    
    /**
     * Updates an entry inside an transaction.
     * 
     * @throws Exception
     */
    
    public void updateEntry(Entry entry, 
            				String firstName, 
            				String lastName, 
            				String email,
            				String organisation,
				            String address,
				            String zipcode,
				            String city,
				            String phone,
				            String fax,
				            String message,
				            String xml,
            				Session session) throws Exception 
    {
        entry.setFirstName(firstName);
        entry.setLastName(lastName);
        entry.setEmail(email);
        entry.setOrganisation(organisation);
        entry.setAddress(address);
        entry.setZipcode(zipcode);
        entry.setCity(city);
        entry.setPhone(phone);
        entry.setFax(fax);
        entry.setMessage(message);
        entry.setAttributes(xml);
        
        new RemoteCacheUpdater().updateRemoteCaches(entry.getEvent().getCalendars());
        
		session.update(entry);
	}
    
 
    /**
     * This method returns a Entry based on it's primary key
     * @return Entry
     * @throws Exception
     */
    /*
    public Entry getEntry(Long id) throws Exception
    {
        Entry entry = null;
        
        Session session = getSession();
        
		Transaction tx = null;
		try 
		{
			tx = session.beginTransaction();
			entry = getEntry(id, session);
			tx.commit();
		}
		catch (Exception e) 
		{
		    if (tx!=null) 
		        tx.rollback();
		    throw e;
		}
		finally 
		{
		    session.close();
		}
		
		return entry;
    }
    */
    
    /**
     * This method returns a Entry based on it's primary key inside a transaction
     * @return Entry
     * @throws Exception
     */
    
    public Entry getEntry(Long id, Session session) throws Exception
    {
        Entry entry = (Entry)session.load(Entry.class, id);
		
		return entry;
    }
    
    
    /**
     * This method returns a list of Locations
     * @return List
     * @throws Exception
     */
    /*
    public List getEntryList() throws Exception
    {
        List list = null;
        
        Session session = getSession();
        
		Transaction tx = null;
		try 
		{
			tx = session.beginTransaction();
			list = getEntryList(session);
			tx.commit();
		}
		catch (Exception e) 
		{
		    if (tx!=null) 
		        tx.rollback();
		    throw e;
		}
		finally 
		{
		    session.close();
		}
		
		return list;
    }
    */
    
    /**
     * Gets a list of all entrys available sorted by primary key.
     * @return List of Entry
     * @throws Exception
     */
    
    public List getEntryList(Session session) throws Exception 
    {
        List result = null;
        
        Query q = session.createQuery("from Entry entry order by entry.id");
   
        result = q.list();
        
        return result;
    }
    
    
    /**
     * This method returns a list of Entries which matches a certain search
     * @return List
     * @throws Exception
     */
    /*
    public List getEntryList(String firstName, String lastName, String email, Long eventId, String[] categories, String[] locations, Session session) throws Exception
    {
		List list = getEntryList(eventId, firstName, lastName, email, session);
		
		Iterator entryListIterator = list.iterator();
		while(entryListIterator.hasNext())
		{
		    Entry entry = (Entry)entryListIterator.next();
		    
		    boolean isValid = true;

		    if(categories != null)
		    {
			    Map categoryHash = new HashMap();
			    Iterator categoryIterator = entry.getEvent().getEventCategories().iterator();
			    while(categoryIterator.hasNext())
			    {
			        Category category = (Category)categoryIterator.next();
			        categoryHash.put(category.getId().toString(), category.getId().toString());
			    }
		    
			    for(int i=0; i<categories.length; i++)
		        {
		            if(!categoryHash.containsKey(categories[i]))
			        {
			            isValid = false;
			            break;
			        }    
		        }
		    }
		    
		    if(locations != null)
		    {
			    Map locationHash = new HashMap();
			    Iterator locationIterator = entry.getEvent().getLocations().iterator();
			    while(locationIterator.hasNext())
			    {
			        Location location = (Location)locationIterator.next();
			        locationHash.put(location.getId().toString(), location.getId().toString());
			    }
			    
			    for(int i=0; i<locations.length; i++)
		        {
		            if(!locationHash.containsKey(locations[i]))
			        {
			            isValid = false;
			            break;
			        }    
		        }
		    }
		    
		    if(!isValid)
		        entryListIterator.remove();
		}
		
		return list;
    }
    */
    
    /**
     * This method returns a list of Entries which matches a certain search
     * @return List
     * @throws Exception
     */
    
    public Set getEntryList(String userName, List roles, List groups, String firstName, String lastName, String email, boolean onlyFutureEvents, Long[] eventId, Map categoryAttributesMap, boolean andSearch, String[] locations, Session session) throws Exception
    {
		Set set = getEntryList(userName, roles, groups, eventId, firstName, lastName, email, onlyFutureEvents, categoryAttributesMap, andSearch, session);
		
		Iterator entryListIterator = set.iterator();
		while(entryListIterator.hasNext())
		{
		    Entry entry = (Entry)entryListIterator.next();
		    /*List 
		    String value = getAttributeValue(entry, attributeName, false);
		    */
		    boolean isValid = true;
		    /*
		    Iterator categoryAttributeMapIterator = categoryAttributesMap.entrySet().iterator();
		    while(categoryAttributeMapIterator.hasNext())
		    {
		    	String[] categories = (String[])categoryAttributeMapIterator.next();
			    if(categories != null)
			    {
				    Map categoryHash = new HashMap();
				    Iterator categoryIterator = entry.getEvent().getEventCategories().iterator();
				    while(categoryIterator.hasNext())
				    {
				        Category category = (Category)categoryIterator.next();
				        
				        categoryHash.put(category.getId().toString(), category.getId().toString());
				    }
			    
				    for(int i=0; i<categories.length; i++)
			        {
			            if(!categoryHash.containsKey(categories[i]))
				        {
				            isValid = false;
				            break;
				        }    
			        }
			    }
		    }
		    */
		    
		    if(locations != null)
		    {
			    Map locationHash = new HashMap();
			    Iterator locationIterator = entry.getEvent().getLocations().iterator();
			    while(locationIterator.hasNext())
			    {
			        Location location = (Location)locationIterator.next();
			        locationHash.put(location.getId().toString(), location.getId().toString());
			    }
			    
			    for(int i=0; i<locations.length; i++)
		        {
		            if(!locationHash.containsKey(locations[i]))
			        {
			            isValid = false;
			            break;
			        }    
		        }
		    }
		    
		    if(!isValid)
		        entryListIterator.remove();
		}
		
		return set;
    }

    
    /**
     * Gets a list of all entrys available sorted by primary key.
     * @return List of Entry
     * @throws Exception
     */
    
    public List getEntryList(Long eventId, String firstName, String lastName, String email, Session session) throws Exception 
    {
        List result = null;

        String arguments = "";
        
        if(eventId != null)
            arguments += "entry.event.id = :eventId ";
        if(firstName != null && firstName.length() != 0)
            arguments += (arguments.length() == 0 ? "" : "and ") + "entry.firstName = :firstName ";
        if(lastName != null && lastName.length() != 0)
            arguments += (arguments.length() == 0 ? "" : "and ") + "entry.lastName = :lastName ";
        if(email != null && email.length() != 0)
            arguments += (arguments.length() == 0 ? "" : "and ") + "entry.email = :email ";
        
        String sql = "from Entry entry " + (arguments.length() > 0 ? "WHERE " + arguments : "") + " order by entry.id";
        log.info("SQL:" + sql);
        
        Query q = session.createQuery(sql);

        if(eventId != null)
            q.setParameter("eventId", eventId, Hibernate.LONG);
        if(firstName != null && firstName.length() != 0)
            q.setParameter("firstName", firstName, Hibernate.STRING);
        if(lastName != null && lastName.length() != 0)
            q.setParameter("lastName", lastName, Hibernate.STRING);
        if(email != null && email.length() != 0)
            q.setParameter("email", email, Hibernate.STRING);
        
        result = q.list();
        
        return result;
    }

    /**
     * Gets a list of all entrys available sorted by primary key.
     * @return List of Entry
     * @throws Exception
     */
    
    public Set getEntryList(String userName, List roles, List groups, Long[] eventId, String firstName, String lastName, String email, boolean onlyFutureEvents, Map selectedCategoryAttributes, boolean andSearch, Session session) throws Exception 
    {
        List result = null;

        Criteria criteria = session.createCriteria(Entry.class);

        Criteria eventCriteria = criteria.createCriteria("event");
        Criteria calendarCriteria = eventCriteria.createCriteria("owningCalendar");

        calendarCriteria.createCriteria("owningRoles").add(Expression.in("name", roles.toArray()));
        if(groups.size() > 0)
        	calendarCriteria.createCriteria("owningGroups").add(Expression.in("name", groups.toArray()));

        if(onlyFutureEvents)
        	eventCriteria.add(Expression.gt("endDateTime", java.util.Calendar.getInstance()));

        if(selectedCategoryAttributes != null && selectedCategoryAttributes.size() > 0)
        {
    		Criteria eventCategoriesCriteria = eventCriteria.createCriteria("eventCategories");
    		Criteria categoryAttributesCriteria = eventCategoriesCriteria.createCriteria("eventTypeCategoryAttribute");
    		Criteria categoryCriteria = eventCategoriesCriteria.createCriteria("category");

    		Junction junction = Restrictions.disjunction();
    		if(andSearch)
    			junction = Restrictions.conjunction();
    		
    		eventCategoriesCriteria.add(junction);
    		
        	Iterator selectedCategoryAttributesIterator = selectedCategoryAttributes.keySet().iterator();
        	while(selectedCategoryAttributesIterator.hasNext())
		    {
		    	String id = (String)selectedCategoryAttributesIterator.next();
		    	Long[] categories = (Long[])selectedCategoryAttributes.get(id);
		    	log.info("id:" + id);
		    	log.info("categories:" + categories);
		    	if(categories != null)
		    	{
		    		Criterion e1 = Restrictions.eq("eventTypeCategoryAttribute.id", new Long(id));
		    		Criterion e2 = Restrictions.in("category.id", categories);
		    		Criterion criterion = Restrictions.and(e1, e2);
		    			
		    		junction.add(criterion);
		    	}
		    }
        	log.info("junction:" + junction.toString());
        }
        
//        if(eventId != null)
//        	eventCriteria.add(Restrictions.idEq(eventId));

        if(eventId != null && eventId.length > 0 && eventId[0] != null)
        	eventCriteria.add(Restrictions.in("id", eventId));

        if(firstName != null && firstName.length() != 0)
        	criteria.add(Restrictions.like("firstName", firstName));
        if(lastName != null && lastName.length() != 0)
        	criteria.add(Restrictions.like("lastName", lastName));
        if(email != null && email.length() != 0)
        	criteria.add(Restrictions.like("email", email));

        criteria.addOrder(Order.asc("id"));
        
        Set set = new LinkedHashSet();
        set.addAll(criteria.list());
        
        return set;	
    }

    /**
     * Gets a list of entrys fetched by name.
     * @return List of Entry
     * @throws Exception
     */
    
    public List getEntry(String firstName, Session session) throws Exception 
    {
        List entrys = null;
        
        entrys = session.createQuery("from Entry as entry where entry.firstName = ?").setString(0, firstName).list();
        
        return entrys;
    }
    
    
    /**
     * Deletes a entry object in the database. Also cascades all events associated to it.
     * @throws Exception
     */
    
    public void deleteEntry(Long id, Session session) throws Exception 
    {
        Entry entry = this.getEntry(id, session);
        new RemoteCacheUpdater().updateRemoteCaches(entry.getEvent().getCalendars());
        session.delete(entry);
    }
    
    
	/**
	 * Returns an attribute value from the Entry
	 *
	 * @param entry The entry on which to find the value
	 * @param attributeName THe name of the attribute whose value is wanted
	 * @param escapeHTML A boolean indicating if the result should be escaped
	 * @return The String vlaue of the attribute, or blank if it doe snot exist.
	 */
	public String getAttributeValue(Entry entry, String attributeName, boolean escapeHTML)
	{
		String value = "";
		String xml = entry.getAttributes();

		int startTagIndex = xml.indexOf("<" + attributeName + ">");
		int endTagIndex   = xml.indexOf("]]></" + attributeName + ">");

		if(startTagIndex > 0 && startTagIndex < xml.length() && endTagIndex > startTagIndex && endTagIndex <  xml.length())
		{
			value = xml.substring(startTagIndex + attributeName.length() + 11, endTagIndex);
			if(escapeHTML)
				value = new VisualFormatter().escapeHTML(value);
		}		

		return value;
	}

	/**
	 * This method fetches a value from the xml that is the contentVersions Value. If the 
	 * contentVersioVO is null the contentVersion has not been created yet and no values are present.
	 */
	 
	public String getAttributeValue(String xml, String key, boolean escapeHTML)
	{
		String value = "";
		
		if(xml != null)
		{
			try
	        {
		        //log.info("key:" + key);
				
				int startTagIndex = xml.indexOf("<" + key + ">");
				int endTagIndex   = xml.indexOf("]]></" + key + ">");

				if(startTagIndex > 0 && startTagIndex < xml.length() && endTagIndex > startTagIndex && endTagIndex <  xml.length())
				{
					value = xml.substring(startTagIndex + key.length() + 11, endTagIndex);
					if(escapeHTML)
						value = new VisualFormatter().escapeHTML(value);
				}					
	        }
	        catch(Exception e)
	        {
	        	e.printStackTrace();
	        }
		}
				
		return value;
	}

    /**
     * This method emails all persons in an email-address string a message.
     * @throws Exception
     */
    
    public void mailEntries(String emailAddresses, String subject, String message, List attachments, Locale locale, Session session) throws Exception
    {
	    try
	    {
		    String email = "";
	        
	        String contentType = PropertyHelper.getProperty("mail.contentType");
	        if(contentType == null || contentType.length() == 0)
	            contentType = "text/html";
	        
	        String template = CalendarLabelsController.getCalendarLabelsController().getLabel("labels.public.entry.information.messageTemplate", locale, false, true, false, session);
	        log.debug("\n\ntemplate:" + template);
	        if(template == null || template.equals(""))
	        {
		        if(contentType.equalsIgnoreCase("text/plain"))
		            template = FileHelper.getFileAsString(new File(PropertyHelper.getProperty("contextRootPath") + "templates/entryMessage_plain.vm"));
			    else
		            template = FileHelper.getFileAsString(new File(PropertyHelper.getProperty("contextRootPath") + "templates/entryMessage_html.vm"));
	        }
	        
		    Map parameters = new HashMap();
		    parameters.put("subject", subject);
		    parameters.put("message", message);
		    parameters.put("formatter", new VisualFormatter());
	        
			StringWriter tempString = new StringWriter();
			PrintWriter pw = new PrintWriter(tempString);
			new VelocityTemplateProcessor().renderTemplate(parameters, pw, template);
			email = tempString.toString();
	        
			String systemEmailSender = PropertyHelper.getProperty("systemEmailSender");
			if(systemEmailSender == null || systemEmailSender.equalsIgnoreCase(""))
				systemEmailSender = "infoglueCalendar@" + PropertyHelper.getProperty("mail.smtp.host");

			MailServiceFactory.getService().send(systemEmailSender, systemEmailSender, emailAddresses, subject, email, contentType, "UTF-8", attachments);
		}
		catch(Exception e)
		{
			log.error("The notification was not sent. Reason:" + e.getMessage());
		}
		
    }
 
    
    /**
     * This method emails the person with a confirmation message.
     * @throws Exception
     */
    
    public void mailVerification(Entry entry, Locale locale, boolean isReserve, Session session) throws Exception
    {
	    String email = "";
	    
	    try
	    {
	        String contentType = PropertyHelper.getProperty("mail.contentType");
	        if(contentType == null || contentType.length() == 0)
	            contentType = "text/html";
	        
	        CalendarAbstractAction caa = new CalendarAbstractAction();
	        EventVersion eventVersion = caa.getEventVersion(entry.getEvent());
	        
	        String template = "";
	        String subject = "";
	        if(!isReserve)
	        {
		        template = CalendarLabelsController.getCalendarLabelsController().getLabel("labels.public.entry.verification.message", locale, false, true, false, session);
		        subject = eventVersion.getName() + " - " + CalendarLabelsController.getCalendarLabelsController().getLabel("labels.public.entry.verification.subject", locale, false, true, false, session);
		        if(template == null || template.equals(""))
		        {
			        if(contentType.equalsIgnoreCase("text/plain"))
			            template = FileHelper.getFileAsString(new File(PropertyHelper.getProperty("contextRootPath") + "templates/entryVerificationMessage_plain.vm"));
				    else
			            template = FileHelper.getFileAsString(new File(PropertyHelper.getProperty("contextRootPath") + "templates/entryVerificationMessage_html.vm"));
		        }
	        }
	        else
	        {
		        template = CalendarLabelsController.getCalendarLabelsController().getLabel("labels.public.entry.verificationReserve.message", locale, false, true, false, session);
		        subject = eventVersion.getName() + " - " + CalendarLabelsController.getCalendarLabelsController().getLabel("labels.public.entry.verificationReserve.subject", locale, false, true, false, session);
		        if(template == null || template.equals(""))
		        {
			        if(contentType.equalsIgnoreCase("text/plain"))
			            template = FileHelper.getFileAsString(new File(PropertyHelper.getProperty("contextRootPath") + "templates/entryVerificationReserveMessage_plain.vm"));
				    else
			            template = FileHelper.getFileAsString(new File(PropertyHelper.getProperty("contextRootPath") + "templates/entryVerificationReserveMessage_html.vm"));
		        }
	        }

	        EventType eventType = EventTypeController.getController().getEventType(entry.getEvent().getEntryFormId(), session);
    		List attributes = ContentTypeDefinitionController.getController().getContentTypeAttributes(eventType.getSchemaValue());

		    Map parameters = new HashMap();
		    parameters.put("entry", entry);
		    parameters.put("eventVersion", eventVersion);
        	parameters.put("attributes", attributes);
		    parameters.put("formatter", new VisualFormatter());
		    parameters.put("this", this);
		    
			StringWriter tempString = new StringWriter();
			PrintWriter pw = new PrintWriter(tempString);
			new VelocityTemplateProcessor().renderTemplate(parameters, pw, template);
			email = tempString.toString();
            
			log.info("---------------- VERIFICATION ----------------------" + email);				

			String systemEmailSender = PropertyHelper.getProperty("systemEmailSender");
			if(systemEmailSender == null || systemEmailSender.equalsIgnoreCase(""))
				systemEmailSender = "infoglueCalendar@" + PropertyHelper.getProperty("mail.smtp.host");

			//Setting up and generating a vcal/ical
	        Language language = LanguageController.getController().getLanguageWithCode(locale.getLanguage(), session);
			String calendarPath = PropertyHelper.getProperty("calendarsPath");
			String vFileName = "event_" + entry.getEvent().getId() + ".vcs";
			String iFileName = "event_" + entry.getEvent().getId() + ".ics";
			
			//ICalendarController.getICalendarController().getVCalendar(entry.getEvent(), language, calendarPath + vFileName);
			ICalendarController.getICalendarController().getICalendar(entry.getEvent(), language, calendarPath + iFileName);
			//File file = new File(calendarPath + vFileName);
			File file = new File(calendarPath + iFileName);
			List attachments = new ArrayList();
			attachments.add(file);
			
			MailServiceFactory.getService().send(systemEmailSender, entry.getEmail(), null, subject, email, contentType, "UTF-8", attachments);
		}
		catch(Exception e)
		{
		    log.error("The notification was not sent. Reason:" + e.getMessage());
			
			try
			{
		        String contentType = PropertyHelper.getProperty("mail.contentType");
		        if(contentType == null || contentType.length() == 0)
		            contentType = "text/html";

				String systemEmailSender = PropertyHelper.getProperty("systemEmailSender");
				if(systemEmailSender == null || systemEmailSender.equalsIgnoreCase(""))
					systemEmailSender = "infoglueCalendar@" + PropertyHelper.getProperty("mail.smtp.host");

				String warningEmailReceiver = PropertyHelper.getProperty("warningEmailReceiver");
				if(warningEmailReceiver != null && !warningEmailReceiver.equalsIgnoreCase(""))
				{
					MailServiceFactory.getService().send(systemEmailSender, warningEmailReceiver, null, "Could not send email verification...", "<div>Error reported:<br/>" + e.getMessage() + "<br/>Entry:" + entry.getFirstName() + " " + entry.getLastName() + " - " + entry.getEmail() + " was booked on " + entry.getEvent().getName() + "</div>", contentType, "UTF-8", null);
				}
			}
			catch(Exception e2)
			{
				log.error("The warning was not sent. Reason:" + e2.getMessage());
			}
		}
		
    }
 
    
    /**
     * This method emails the owner of an event the new information and an address to visit.
     * @throws Exception
     */
    
    public void notifyEventOwner(Entry entry, Locale locale, InfoGluePrincipalBean infoGluePrincipalBean, Session session) throws Exception
    {
    	Event event = entry.getEvent();
    	
    	if(event.getContactEmail() == null || event.getContactEmail().length() == 0)
    	{
    		log.info("There was no contact email set for event:" + event.getId());
    		return;
    	}
    	
	    String email = "";
	    
	    try
	    {
	    	String contentType = PropertyHelper.getProperty("mail.contentType");
	        if(contentType == null || contentType.length() == 0)
	            contentType = "text/html";
	        
	    	String addresses = event.getContactEmail();
	    	
	    	CalendarAbstractAction caa = new CalendarAbstractAction();
	        EventVersion eventVersion = caa.getEventVersion(entry.getEvent());	        
	    	
            String template = CalendarLabelsController.getCalendarLabelsController().getLabel("labels.public.entry.notification.message", locale, false, true, false, session);
	        String subject = "InfoGlue - " + CalendarLabelsController.getCalendarLabelsController().getLabel("labels.public.entry.notification.subject", locale, false, true, false, session) + " \"" + eventVersion.getName() + "\"";

	        log.debug("\n\ntemplate:" + template);
	        
	        if(template == null || template.equals(""))
	        {
		        if(contentType.equalsIgnoreCase("text/plain"))
		            template = FileHelper.getFileAsString(new File(PropertyHelper.getProperty("contextRootPath") + "templates/entryNotificationMessage_plain.vm"));
			    else
		            template = FileHelper.getFileAsString(new File(PropertyHelper.getProperty("contextRootPath") + "templates/entryNotificationMessage_html.vm"));
	        }
	        
        	EventType eventType = EventTypeController.getController().getEventType(entry.getEvent().getEntryFormId(), session);
    		List attributes = ContentTypeDefinitionController.getController().getContentTypeAttributes(eventType.getSchemaValue());

		    Map parameters = new HashMap();
		    parameters.put("principal", infoGluePrincipalBean);
		    parameters.put("entry", entry);
		    parameters.put("eventVersion", eventVersion);
		    parameters.put("event", event);
        	parameters.put("attributes", attributes);
		    parameters.put("formatter", new VisualFormatter());
		    parameters.put("this", this);

			StringWriter tempString = new StringWriter();
			PrintWriter pw = new PrintWriter(tempString);
			new VelocityTemplateProcessor().renderTemplate(parameters, pw, template);
			email = tempString.toString();
	    
			log.info("----------------- OWNER ---------------------" + email);			

			String systemEmailSender = PropertyHelper.getProperty("systemEmailSender");
			if(systemEmailSender == null || systemEmailSender.equalsIgnoreCase(""))
				systemEmailSender = "infoglueCalendar@" + PropertyHelper.getProperty("mail.smtp.host");

			log.info("Sending mail from:" + systemEmailSender + " to " + addresses);
			log.info("mail.smtp.host:" + PropertyHelper.getProperty("mail.smtp.host"));
			
			MailServiceFactory.getService().send(systemEmailSender, addresses, null, subject, email, contentType, "UTF-8", null);
	    }
		catch(Exception e)
		{
			log.error("The notification was not sent. Reason:" + e.getMessage(), e);
		}
		
    }

}