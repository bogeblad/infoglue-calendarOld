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
import org.infoglue.calendar.controllers.EventController;
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
	    Iterator calendarsIterator = calendars.iterator();
	    while(calendarsIterator.hasNext())
	    {
	        Calendar calendar = (Calendar)calendarsIterator.next();
	        updateRemoteCaches(calendar.getId());
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
			    		System.out.println("1");
				    	String response = postToUrl(address, hashedMessage);
			    	}
			    	else
			    	{
			    		System.out.println("2");
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
