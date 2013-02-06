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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.infoglue.calendar.entities.Calendar;
import org.infoglue.calendar.entities.Event;
import org.infoglue.calendar.entities.Resource;
import org.infoglue.calendar.util.graphics.ThumbnailGenerator;
import org.infoglue.common.util.PropertyHelper;
import org.infoglue.common.util.RemoteCacheUpdater;


public class ResourceController extends BasicController
{    
    //Logger for this class
    private static Log log = LogFactory.getLog(ResourceController.class);
        
    
    /**
     * Factory method to get EventController
     * 
     * @return EventController
     */
    
    public static ResourceController getController()
    {
        return new ResourceController();
    }
        
    
    /**
     * This method is used to create a new Event object in the database.
     */
    
    public Resource createResource(Long eventId, String assetKey, String contentType, String fileName, File file, Session session) throws HibernateException, Exception 
    {
        Resource resource = null;
        
		Event event = EventController.getController().getEvent(eventId, session);
		resource = createResource(event, assetKey, contentType, fileName, file, session);
		
        return resource;
    }

    
    /**
     * This method is used to create a new Event object in the database inside a transaction.
     */
    
    public Resource createResource(Event event, String assetKey, String contentType, String fileName, File file, Session session) throws HibernateException, Exception 
    {
        Resource resource = new Resource();
        resource.setAssetKey(assetKey);
        resource.setFileName(fileName);
        resource.setResource(Hibernate.createBlob(new FileInputStream(file)));
        
        event.getResources().add(resource);
        
        session.save(resource);
        
        if(event.getStateId().equals(Event.STATE_PUBLISHED))
		    new RemoteCacheUpdater().updateRemoteCaches(event.getCalendars());
        
        return resource;
    }
    
    
    /**
     * Updates an event.
     * 
     * @throws Exception
     */
    /*
    public void updateEvent(Long id, String name, String description, java.util.Calendar startDateTime, java.util.Calendar endDateTime, String[] locationId, String[] categoryId, String[] participantUserName) throws Exception 
    {
	    Session session = getSession();
	    
		Transaction tx = null;
		try 
		{
			tx = session.beginTransaction();
		
			Event event = getEvent(id, session);
			
			Set locations = new HashSet();
			for(int i=0; i<locationId.length; i++)
			{
			    Location location = LocationController.getController().getLocation(new Long(locationId[i]), session);
			    locations.add(location);
			}

			Set categories = new HashSet();
			for(int i=0; i<categoryId.length; i++)
			{
			    Category category = CategoryController.getController().getCategory(new Long(categoryId[i]), session);
			    categories.add(category);
			}

			Set participants = new HashSet();
			for(int i=0; i<participantUserName.length; i++)
			{
			    Participant participant = new Participant();
			    participant.setUserName(participantUserName[i]);
			    participant.setEvent(event);
			    session.save(participant);
			    participants.add(participant);
			}

			updateEvent(event, name, description, startDateTime, endDateTime, locations, categories, participants, session);
			
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
    }
    */
    
    /**
     * Updates an event inside an transaction.
     * 
     * @throws Exception
     */
    /*
    public void updateEvent(Event event, String name, String description, java.util.Calendar startDateTime, java.util.Calendar endDateTime, Set locations, Set categories, Set participants, Session session) throws Exception 
    {
        event.setName(name);
        event.setDescription(description);
        event.setStartDateTime(startDateTime);
        event.setEndDateTime(endDateTime);
        event.setLocations(locations);
        event.setCategories(categories);
        event.setParticipants(participants);
        
		session.update(event);
	}
    */

    /**
     * 
     * @param resource
     * @return
     */
    private String getResourceFileName(Resource resource)
    {
    	return getResourceFileName(resource, null, null);
    }

    private String getResourceFileName(Resource resource, Integer width, Integer height)
    {
    	if (resource == null)
    	{
    		return null;
    	}
    	StringBuilder sb = new StringBuilder();
    	sb.append(resource.getId());
    	sb.append("_");
    	sb.append(resource.getAssetKey());
    	if (width != null)
    	{
    		sb.append("_");
    		sb.append(width.intValue());
    	}
    	if (height != null)
    	{
    		sb.append("_");
    		sb.append(height.intValue());
    	}
    	sb.append("_");
		String fileName = resource.getFileName();
		if(fileName.lastIndexOf("/") > -1)
		    fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
		if(fileName.lastIndexOf("\\") > -1)
		    fileName = fileName.substring(fileName.lastIndexOf("\\") + 1);
    	sb.append(fileName);
    	if (log.isDebugEnabled())
    	{
    		log.debug("Generated Resource file name. Resource: " + resource.getFileName() + ". Width: " + width + ". Height: " + height + ". Generated name: " + sb.toString());
    	}
    	return sb.toString();
    }

    private File getResourceFile(Resource resource)
    {
    	return getResourceFile(resource, null, null);
    }

    private File getResourceFile(Resource resource, Integer width, Integer height)
    {
    	String digitalAssetPath = PropertyHelper.getProperty("digitalAssetPath");
    	String fileName = getResourceFileName(resource, width, height);
    	File file = new File(digitalAssetPath + fileName);

    	return file;
    }

    private String getResourceUrlString(Resource resource)
    {
    	return getResourceUrlString(resource, null, null);
    }

    private String getResourceUrlString(Resource resource, Integer width, Integer height)
    {
    	String fileName = getResourceFileName(resource, width, height);
    	String urlBase = PropertyHelper.getProperty("urlBase");

		return urlBase + "digitalAssets/" + fileName;
    }
    
    private void dumpResource(Resource resource)
    {
    	if (resource == null)
    	{
    		log.warn("Tried to dump event resource to disk but the reference was null");
    	}
    	FileOutputStream fos = null;
		try
		{
			File outputFile = getResourceFile(resource);
			fos = new FileOutputStream(outputFile);

			Blob blob = resource.getResource();
			byte[] bytes = blob.getBytes(1, (int) blob.length());
			fos.write(bytes);
			fos.flush();
			fos.close();
		}
		catch (FileNotFoundException ex)
		{
			log.error("FileNotFoundException when dumping calendar resource to disk. " + resource.getFileName() + ". Exception message: " + ex.getMessage());
			log.warn("FileNotFoundException when dumping calendar resource to disk. " + resource.getFileName(), ex);
		}
		catch (SQLException ex)
		{
			log.error("SQLException when dumping calendar resource to disk. " + resource.getFileName() + ". Exception message: " + ex.getMessage());
			log.warn("SQLException when dumping calendar resource to disk. " + resource.getFileName(), ex);
		}
		catch (IOException ex)
		{
			log.error("IOException when dumping calendar resource to disk. " + resource.getFileName() + ". Exception message: " + ex.getMessage());
			log.warn("IOException when dumping calendar resource to disk. " + resource.getFileName(), ex);
		}
		finally
		{
			if (fos != null)
			{
				try
				{
					fos.close();
				}
				catch (IOException ex)
				{
					log.error("Could not close file after an exception occured while dumping resource to disk. Resource: " + resource.getFileName() + " . Message: " + ex.getMessage());
				}
			}
		}
    }

    private void dumpResource(Resource resource, int width, int height)
    {
    	File outputFile = getResourceFile(resource, width, height);
    	log.debug("Will generate thumbnail with file name: " + outputFile.getName());
		Blob blob = resource.getResource();
		if (blob != null)
		{
			ThumbnailGenerator tg = ThumbnailGenerator.getInstance();
			try
			{
				tg.transform(blob.getBinaryStream(), outputFile, width, height, 100);
			}
			catch (SQLException ex)
			{
				log.error("SQLException when dumping calendar resource thumbnail to disk. " + resource.getFileName() + ". Exception message: " + ex.getMessage());
				log.warn("SQLException when dumping calendar resource thumbnail to disk. " + resource.getFileName(), ex);
			}
			catch (IOException ex)
			{
				log.error("IOException when dumping calendar resource thumbnail to disk. " + resource.getFileName() + ". Exception message: " + ex.getMessage());
				log.warn("IOException when dumping calendar resource thumbnail to disk. " + resource.getFileName(), ex);
			}
			if (log.isInfoEnabled())
			{
				log.info("File was generated successfully: " + outputFile.exists());
			}
		}
    }

    @SuppressWarnings("unchecked")
	private Resource getResourceFromEvent(Event event, String assetKey)
    {
    	if(event == null)
    		return null;
    	
    	for (Resource resource : (Set<Resource>)event.getResources())
    	{
    		if (resource.getAssetKey().equals(assetKey))
    		{
    			if (log.isDebugEnabled())
    			{
    				log.debug("Found resource for key " + assetKey + " on Event " + event);
    			}

    			return resource;
    		}
    	}
    	return null;
    }

    /**
     * This method returns a Resource based on it's owning content and it's asset key.
     * @return Resource
     */
    public String getResourceUrl(Event event, String assetKey, Session session)
    {
    	String url = null;

    	try
    	{
    		Resource resource = getResourceFromEvent(event, assetKey);
	        if (resource != null)
	        {
	        	File resourceFile = getResourceFile(resource);
	        	if (!resourceFile.exists())
	        	{
	        		log.info("Will generate resource with file name: " + resourceFile.getName());
	        		dumpResource(resource);
	        	}
	        	else
				{
					log.info("File already exists. Resource file name: " + resource.getFileName());
				}

	        	url = getResourceUrlString(resource);
	        }
    	}
    	catch(Exception ex)
		{
			log.error("An error occured while generating resource from assetKey. Exception type: " + ex.getClass() + ". Exception message: " + ex.getMessage());
			log.warn("An error occured while generating resource from assetKey.", ex);
		}
        return url;
    }

    /**
     * This method returns a Resource based on it's primary key
     * @return Resource
     */
    public String getResourceUrl(Long id, Session session)
    {
    	String url = null;

    	try
    	{
			Resource resource = getResource(id, session);

			File file = getResourceFile(resource);
			if(!file.exists())
			{
				log.info("Will generate resource with file name: " + file.getName());
				dumpResource(resource);
			}
			else
			{
				log.info("File already exists. Resource file name: " + resource.getFileName());
			}

			url = getResourceUrlString(resource);
	    }
		catch(Exception ex)
		{
			log.error("An error occured while generating resource from id. Exception type: " + ex.getClass() + ". Exception message: " + ex.getMessage());
			log.warn("An error occured while generating resource from id.", ex);
		}
    	return url;
    }

    /**
     * Returns an URL to the resource on the given event for the given asset key. If an error occurs or if no resource is found
     * null is returned. If a thumb nail with the given size is found on disk that file will be used, otherwise a new version is 
     * generated from the Resource's blob.
     *
     * @param event The event which asset keys will be searched
     * @param assetKey The asset key that references the desired resource
     * @param thumbWidth The desired width of the thumb nail
     * @param thumbHeight The desired height of the thumb nail
     * @param session An active Hibernate session to use
     * @return A relative URL to a thumb nail of the resource. Null if an error occurs or if no resource is found.
     */
    public String getResourceThumbnailUrl(Event event, String assetKey, int thumbWidth, int thumbHeight, Session session)
    {
    	String url = null;

    	try
    	{
    		Resource resource = getResourceFromEvent(event, assetKey);
	    	if (resource != null)
	    	{
	    		File file = getResourceFile(resource, thumbWidth, thumbHeight);
	
	    		if(!file.exists())
	    		{
	    			log.info("Will generate thumb nail with file name: " + file.getName());
	    			dumpResource(resource, thumbWidth, thumbHeight);
	    		}
	    		else
	    		{
	    			log.info("File already existed:" + file.getName());
	    		}
	
	    		url = getResourceUrlString(resource, thumbWidth, thumbHeight);
	    	}
	    }
		catch(Exception ex)
		{
			log.error("An error occured while generating resource thumbnail from assetKey. Exception type: " + ex.getClass() + ". Exception message: " + ex.getMessage());
			log.warn("An error occured while generating resource thumbnail from assetKey.", ex);
		}

    	return url;
    }

    /**
     * Returns an URL to the resource with the given resource ID. If an error occurs or if no resource is found
     * null is returned. If a thumb nail with the given size is found on disk that file will be used, otherwise a new version is 
     * generated from the Resource's blob.
     *
     * @param resourceId The Resource ID to use
     * @param thumbWidth The desired width of the thumb nail
     * @param thumbHeight The desired height of the thumb nail
     * @param session An active Hibernate session to use
     * @return A relative URL to a thumb nail of the resource. Null if an error occurs or if no resource is found.
     */
    public String getResourceThumbnailUrl(Long resourceId, int thumbWidth, int thumbHeight, Session session)
    {
    	String url = null;

    	try
    	{
	    	Resource resource = getResource(resourceId, session);
	    	if (resource != null)
	    	{
		    	File file = getResourceFile(resource);
	
		    	if(!file.exists())
				{
		    		log.info("Will generate thumb nail with file name: " + file.getName());
		    		dumpResource(resource, thumbWidth, thumbHeight);
				}
				else
				{
					log.info("Thumbnail already existed:" + file.getName());
				}
	
		    	url = getResourceUrlString(resource, thumbWidth, thumbHeight);
	    	}
    	}
    	catch(Exception ex)
    	{
    		log.error("An error occured while generating resource thumbnail from id. Exception type: " + ex.getClass() + ". Exception message: " + ex.getMessage());
    		log.warn("An error occured while generating resource thumbnail from id.", ex);
    	}

    	return url;
    }

    /**
     * This method returns a Resource based on it's primary key inside a transaction
     * @return Resource
     * @throws Exception
     */
    public Resource getResource(Long id, Session session) throws Exception
    {
        Resource resource = (Resource)session.load(Resource.class, id);

		return resource;
    }

    /**
     * Gets a list of all events available sorted by primary key.
     * @return List of Resource
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
     * Gets a list of all events available for a particular day.
     * @return List of Event
     * @throws Exception
     */
    
    public List getEventList(Calendar calendar, java.util.Calendar date, Session session) throws Exception 
    {
        List result = null;
        
        Query q = session.createQuery("from Event event order by event.id");
   
        result = q.list();
        
        return result;
    }
    
    
    /**
     * This method returns a list of Events based on a number of parameters
     * @return List
     * @throws Exception
     */
    /*
    public List getEventList(Long id, java.util.Calendar startDate, java.util.Calendar endDate) throws Exception
    {
        List list = null;
        
        Session session = getSession();
        
		Transaction tx = null;
		try 
		{
			tx = session.beginTransaction();
			Calendar calendar = CalendarController.getController().getCalendar(id);
			list = getEventList(calendar, startDate, endDate, session);
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
     * This method returns a list of Events based on a number of parameters within a transaction
     * @return List
     * @throws Exception
     */
    
    public List getEventList(Calendar calendar, java.util.Calendar startDate, java.util.Calendar endDate, Session session) throws Exception
    {
        Query q = session.createQuery("from Event as event inner join fetch event.owningCalendar as calendar where event.owningCalendar = ? AND event.startDateTime >= ? AND event.endDateTime <= ? order by event.startDateTime");
        q.setEntity(0, calendar);
        q.setCalendar(1, startDate);
        q.setCalendar(2, endDate);
        
        List list = q.list();
        
        Iterator iterator = list.iterator();
        while(iterator.hasNext())
        {
            Object o = iterator.next();
            Event event = (Event)o;
        }
        
		return list;
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
     * Deletes a resource object in the database. Also cascades all resources associated to it.
     * @throws Exception
     */
    
    public void deleteResource(Long id, Session session) throws Exception 
    {
        Resource resource = this.getResource(id, session);
        
        Event event = resource.getEvent();
        
        session.delete(resource);

        if(event.getStateId().equals(Event.STATE_PUBLISHED))
		    new RemoteCacheUpdater().updateRemoteCaches(event.getCalendars());
    }
    
}