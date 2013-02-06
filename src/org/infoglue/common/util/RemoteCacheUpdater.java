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

package org.infoglue.common.util;

import java.io.*; 
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoglue.calendar.controllers.CalendarSettingsController;
import org.infoglue.calendar.entities.Calendar;


/**
* This class is a class that sends a update-message to all registered instances of delivery-engine.
*
* @author Mattias Bogeblad 
* 
*/

public class RemoteCacheUpdater implements Runnable
{	
   private static Log log = LogFactory.getLog(RemoteCacheUpdater.class);

   private static Map usageMap = new HashMap();
   
   private String address;
   private Hashtable hashedMessage;
   
   public RemoteCacheUpdater(String address, Hashtable hashedMessage)
   {
	   this.address = address;
	   this.hashedMessage = hashedMessage;
   }
   
   /**
    * This method registers that a calendar has been used on a page.
    * 
    * @param siteNodeId
    * @param calendarIdArray
    */
   
   public static void setUsage(Integer siteNodeId, String[] calendarIdArray)
   {
       for(int i=0; i<calendarIdArray.length; i++)
       {
           String calendarId = calendarIdArray[i];
           log.info("Registering use of calendar " + calendarId + " on siteNode " + siteNodeId);
           List siteNodeIdList = (List)usageMap.get(calendarId);
           if(siteNodeIdList == null)
           {
               siteNodeIdList = new ArrayList();
               usageMap.put(calendarId, siteNodeIdList);
           }
           siteNodeIdList.add(siteNodeId);
       }
   }
   
   
	/**
	 * Default Constructor	
	 */
	
	public RemoteCacheUpdater()
	{
	}


	/**
	 * This method serializes the notificationMessage and calls the remote actions.
	 * As a content-tool can have several preview instances we iterate through the instances that have 
	 * been indexed in the propertyfile starting with 0.
	 */
	public void updateRemoteCaches(Set calendars) throws Exception
	{
		if(PropertyHelper.getInfoglueCacheInstanceBaseUrls().size() > 0)
		{
			log.info("Using the new publication method....");
			for(String instanceBaseUrl : PropertyHelper.getInfoglueCacheInstanceBaseUrls())
			{
				log.info("instanceBaseUrl:" + instanceBaseUrl);
				String address = instanceBaseUrl + "/UpdateCache!passThroughPublication.action";

				StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
				String caller = stackTraceElements[2].getClassName().substring(stackTraceElements[2].getClassName().lastIndexOf(".") + 1) + "." + stackTraceElements[2].getMethodName();
				
				String objectName = "Infoglue Calendar update";
				String objectDescription = "No detailed information was given. Origin: " + caller;
				
				log.info("caller: " + caller);
				if(caller.indexOf("EntryController.createEntry") > -1)
				{
					objectName = "New event entry";
					objectDescription = "An entry was added to an event";
				}
				else if(caller.indexOf("EntryController.deleteEntry") > -1)
				{
					objectName = "Entry deleted";
					objectDescription = "An entry was deleted from an event";
				}
				else if(caller.indexOf("EventController.deleteEvent") > -1)
				{
					objectName = "Event deleted";
					objectDescription = "An event was deleted from it's calendar";
				}
				else if(caller.indexOf("EventController.deleteEventVersion") > -1)
				{
					objectName = "Event version deleted";
					objectDescription = "An event version was deleted from it's event";
				}
				else if(caller.indexOf("EventController.deleteLikedEvent") > -1)
				{
					objectName = "Liked event deleted";
					objectDescription = "A linked event was deleted from it's calendar";
				}
				else if(caller.indexOf("ResourceController.deleteResource") > -1)
				{
					objectName = "Event resource deleted";
					objectDescription = "An event resource was deleted";
				}
				else if(caller.indexOf("EventController.linkEvent") > -1)
				{
					objectName = "Event linked";
					objectDescription = "An event was liked to a calendar";
				}
				else if(caller.indexOf("EventController.publishEvent") > -1)
				{
					objectName = "Event published";
					objectDescription = "An event was published";
				}
				else if(caller.indexOf("EventController.updateEvent") > -1)
				{
					objectName = "Event updated";
					objectDescription = "An existing event was updated";
				}
				else if(caller.indexOf("ResourceController.createResource") > -1)
				{
					objectName = "New resource";
					objectDescription = "A new resource was added to an event";
				}
				
				String repositoryID = PropertyHelper.getProperty("infoglue.repositoryId");
				if(repositoryID == null || repositoryID.equals("") || repositoryID.indexOf("infoglue.repositoryId") > -1)
				{
					repositoryID = "InfoglueCalendar";
				}
				
				Hashtable<String,String> publicMessage = new Hashtable<String,String>();
				publicMessage.put("publisherName", "calendarTester");
				publicMessage.put("publicationName", "Infoglue Calendar publication");
				publicMessage.put("publicationDescription", "A publication was made from the calendar system");
				//publicMessage.put("repositoryId", "2");
				publicMessage.put("repositoryId", repositoryID);
				publicMessage.put("objectDescription", objectDescription);
				publicMessage.put("className", "pageCache:portlet_infoglueCalendar.WebworkDispatcherPortlet");
				publicMessage.put("objectId", "1");
				publicMessage.put("objectName", objectName);

				try
				{
					String response = postToUrl(address, publicMessage);
					log.info("response:" + response);
					if(response == null || response.indexOf("ok") == -1 || response.indexOf("error") > 0)
						throw new Exception("Not ok response from infoglue.");
				}
				catch(Exception e)
				{
					log.error("Error updating cache at " + address + ":" + e.getMessage() + ". Adding it to the queue-thread");
					PublicationQueueBean pqb = new PublicationQueueBean();
					pqb.setUrlAddress(address);
					pqb.setRequestParameters(publicMessage);
					pqb.setSerializedParameters(toEncodedString(publicMessage));
					PublicationQueue.getPublicationQueue().addPublicationQueueBean(instanceBaseUrl, pqb);
				}

			}
		}
		else
		{
		    Iterator calendarsIterator = calendars.iterator();
		    while(calendarsIterator.hasNext())
		    {
		        Calendar calendar = (Calendar)calendarsIterator.next();
		        updateRemoteCaches(calendar.getId());
		    }
		}
	}


	/**
	 * This method serializes the notificationMessage and calls the remote actions.
	 * As a content-tool can have several preview instances we iterate through the instances that have 
	 * been indexed in the propertyfile starting with 0.
	 */
	
	public void updateRemoteCachesOld(Long calendarId) throws Exception
	{
		List siteNodeIdList = (List)usageMap.get("" + calendarId);
		if(siteNodeIdList == null)
		    return;
		
		String appPrefix = "notificationUrl";
		
		Iterator siteNodeIdListIterator = siteNodeIdList.iterator();
		while(siteNodeIdListIterator.hasNext())
		{
		    Hashtable hashedMessage = new Hashtable();
			
		    Integer siteNodeId = (Integer)siteNodeIdListIterator.next();
		    log.info("Updating siteNodeId:" + siteNodeId);
		    hashedMessage.put("className", "org.infoglue.cms.entities.structure.SiteNode");
		    hashedMessage.put("objectId", "" + siteNodeId);

		    int i = 0;
			String deliverUrl = null;
			while((deliverUrl = PropertyHelper.getProperty(appPrefix + "." + i)) != null)
			{ 
				String address = deliverUrl;
				log.info("Updating cache at " + address);
			
			    try
			    {
			    	if(address.indexOf("infoglueDeliverWorking") > -1 || address.indexOf("infoglueDeliverPreview") > -1)
			    	{
				    	String response = postToUrl(address, hashedMessage);
			    	}
			    	else
			    	{
			    		Thread thread = new Thread(new RemoteCacheUpdater(address, hashedMessage));
						thread.start();
			    	}
				}
			    catch(Exception e)
			    {
				    log.warn("Error calling " + address + ":" + e.getMessage(), e);			    	
			    }
			    
				i++;
			}
			
			log.info("Removing usage as we now cleared that...");
			siteNodeIdListIterator.remove();
		}
			
	}	
	

	public void updateRemoteCaches(Long calendarId) throws Exception
	{
		if(PropertyHelper.getProperty("useOldRemoteCacheUpdate") != null && PropertyHelper.getProperty("useOldRemoteCacheUpdate").equals("true"))
			updateRemoteCachesOld(calendarId);
		
		String appPrefix = "notificationUrl";
		
	    int i = 0;
		String deliverUrl = null;
		while((deliverUrl = PropertyHelper.getProperty(appPrefix + "." + i)) != null)
		{ 
			String address = deliverUrl;
			log.info("Updating cache at " + address);
			try
		    {
		    	if(PropertyHelper.getProperty("useThreadedRemoteCacheUpdate") != null && PropertyHelper.getProperty("useThreadedRemoteCacheUpdate").equals("true"))
		    	{
			    	Thread thread = new Thread(new RemoteCacheUpdater(address, hashedMessage));
		    		thread.start();
		    	}
		    	else
		    	{
		    		String response = postToUrl(address, hashedMessage);
		    	}
			}
		    catch(Exception e)
		    {
			    log.warn("Error calling " + address + ":" + e.getMessage(), e);			    	
		    }
		    
			i++;
		}			
	}	

	public synchronized void run()
	{
		try
	    {
			//System.out.println("Calling publish url");
		    String response = postToUrl(address, hashedMessage);
		}
	    catch(Exception e)
	    {
		    log.warn("Error calling " + address + ":" + e.getMessage(), e);			    	
	    }
	}
	
   /**
    * This method post information to an URL and returns a string.It throws
    * an exception if anything goes wrong.
    * (Works like most 'doPost' methods)
    * 
    * @param urlAddress The address of the URL you would like to post to.
    * @param inHash The parameters you would like to post to the URL.
    * @return The result of the postToUrl method as a string.
    * @exception java.lang.Exception
    */
   
   private String postToUrl(String urlAddress, Hashtable inHash) throws Exception
   {        
       URL url = new URL(urlAddress);
       URLConnection urlConn = url.openConnection();
       urlConn.setConnectTimeout(3000);
       urlConn.setReadTimeout(3000);
	   urlConn.setAllowUserInteraction(false); 
       urlConn.setDoOutput (true); 
       urlConn.setDoInput (true); 
       urlConn.setUseCaches (false); 
       urlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
       PrintWriter printout = new PrintWriter(urlConn.getOutputStream(), true); 
       String argString = "";
       if(inHash != null)
       {
           argString = toEncodedString(inHash);
       }
       printout.print(argString);
       printout.flush();
       printout.close (); 
       InputStream inStream = null;
       inStream = urlConn.getInputStream();
       InputStreamReader inStreamReader = new InputStreamReader(inStream);
       BufferedReader buffer = new BufferedReader(inStreamReader);            
       StringBuffer strbuf = new StringBuffer();   
       String line; 
       while((line = buffer.readLine()) != null) 
       {
           strbuf.append(line); 
       }                                              
       String readData = strbuf.toString();   
       buffer.close();
       return readData;             
   }
 
 
   /**
	 * Encodes a hash table to an URL encoded string.
	 * 
	 * @param inHash The hash table you would like to encode
	 * @return A URL encoded string.
	 */
		
	private String toEncodedString(Hashtable inHash) throws Exception
	{
	    StringBuffer buffer = new StringBuffer();
	    Enumeration names = inHash.keys();
	    while(names.hasMoreElements())
	    {
	        String name = names.nextElement().toString();
	        String value = inHash.get(name).toString();
	        buffer.append(URLEncoder.encode(name, "UTF-8") + "=" + URLEncoder.encode(value, "UTF-8"));
	        if(names.hasMoreElements())
	        {
	            buffer.append("&");
	        }
	    }
	    return buffer.toString();
	}
	
	
	

}
