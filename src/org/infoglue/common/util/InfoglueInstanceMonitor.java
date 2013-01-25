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

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.Thread.State;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.infoglue.common.util.mail.MailServiceFactory;

/**
 *  This thread tests the live servers continuously for signs of downtime or unresponsiveness.
 */

public class InfoglueInstanceMonitor implements Runnable
{
    private final static Logger logger = Logger.getLogger(InfoglueInstanceMonitor.class.getName());

    private static InfoglueInstanceMonitor instance = null;
    
    public static InfoglueInstanceMonitor getInstance()
    {
    	if(instance == null)
    	{
    		instance = new InfoglueInstanceMonitor();
    		Thread thread = new Thread(instance);
			thread.start();
    	}
    	
    	return instance;
    }
    
    private Map<String,Boolean> instanceStatus = new HashMap<String,Boolean>();
    private Map<String,Integer> instanceErrorInformation = new HashMap<String,Integer>();

    public Map<String,Boolean> getInstanceStatusMap()
    {
    	return instanceStatus;
    }

    public Boolean getServerStatus(String instanceUrl)
    {
    	return instanceStatus.get(instanceUrl);
    }
    
	private InfoglueInstanceMonitor()
	{
	}

	public synchronized void run()
	{
		System.out.println("Starting InfoglueInstanceMonitor....");
		while(true)
		{
			try
			{
				Thread.sleep(30000);
				logger.info("Validating instances");
				
				validateInstances();
			}
			catch (Exception e) 
			{
				logger.error("Error in monitor:" + e.getMessage(), e);
			}
		}
	}
	
	private void validateInstances()
    {
		//System.out.println("validateInstances");
		Map<String,Boolean> newInstanceStatus = new HashMap<String,Boolean>();
	    Map<String,Integer> newInstanceErrorInformation = new HashMap<String,Integer>();
	    newInstanceErrorInformation.putAll(instanceErrorInformation);
	    
		List<String> infoglueCacheInstanceBaseUrls = PropertyHelper.getInfoglueCacheInstanceBaseUrls();
		for(String infoglueCacheInstanceBaseUrl : infoglueCacheInstanceBaseUrls)
		{
			String address = infoglueCacheInstanceBaseUrl + "/UpdateCache!test.action";
			
			try
			{
    			String response = getUrlContent(address, new HashMap(), new HashMap(), "utf-8", 5000);
    			if(response.indexOf("test ok") == -1)
    			    throw new Exception("Got wrong answer");
    			else
    			{
    				newInstanceStatus.put(infoglueCacheInstanceBaseUrl, true);
    				newInstanceErrorInformation.remove(infoglueCacheInstanceBaseUrl);
    			}
    		}
			catch(Exception e)
			{
				String cause = "" + e.getMessage();
				if(e instanceof FileNotFoundException)
					cause = "Application not found";
				else if(e instanceof SocketTimeoutException)
					cause = "" + e.getMessage();
				
				//NotificationMessage serverErrorMessage = new NotificationMessage("Server down: " + cause, serverBase, "SYSTEM", NotificationMessage.SERVER_UNAVAILABLE, "n/a", serverBase);
				logger.error("Calendar Publication Failed. Could not reach: " + address + ": " + cause);
				
				logger.error("A deliver instance is down or unresponsive. Tested instance: " + address + ". Message: " + e.getMessage());
				sendWarningMail(infoglueCacheInstanceBaseUrl);

				newInstanceStatus.put(infoglueCacheInstanceBaseUrl, false);
				Integer retries = newInstanceErrorInformation.get(infoglueCacheInstanceBaseUrl);
				if(retries == null)
				{
					retries = 0;
				}
				newInstanceErrorInformation.put(infoglueCacheInstanceBaseUrl, retries+1);
			}
		}
				
		instanceStatus = newInstanceStatus;
		instanceErrorInformation = newInstanceErrorInformation;
    }

	
	
	private void sendWarningMail(String serverBase) 
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
			try
			{
				String subject = "Calendar Warning[" + serverBase + "]: A infoglue instance is down or unresponsive";
				StringBuilder message = new StringBuilder();
				message.append("The calendar notices that one of the infoglue-instances was unreachable or unresponsive at: " + getFormattedCurrentDateTime("yyyy-MM-dd HH:ss") + "\n");
				message.append("The url tested was:" + serverBase + "\n\n");
				message.append("This should be investigated to ensure that this server receives publications.\n");
				
				MailServiceFactory.getService().send(systemEmailSender, warningEmailReceiver, null, subject, "" + message, contentType, "UTF-8", null);
			}
			catch (Exception e) 
			{
				logger.error("Problem sending warning mail:" + e.getMessage());
			}
		}
	}
	
	
	/**
     * This method returns a Date initialized without milliseconds. 
     * This is very important when saving items i oracle as dates.
     * 
     * @return
     */
    
    public static String getFormattedCurrentDateTime(String pattern)
    {
    	Date date = new Date();
            
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        String dateString = formatter.format(date);

        return dateString;
    }
    
	public String getUrlContent(String urlAddress, Map requestProperties, Map requestParameters, String encoding, int timeout) throws MalformedURLException, IOException, Exception
	{
		String argString = "";
		if(requestParameters != null)
		{
			if(urlAddress.indexOf("?") > -1)
				argString = "&" + toEncodedString(requestParameters, encoding);
			else
				argString = "?" + toEncodedString(requestParameters, encoding);
		}
		if(logger.isInfoEnabled())
			logger.info("argString:" + argString);
		
		if(logger.isInfoEnabled())
			logger.info("Calling:" + urlAddress + argString);
		
	    URL url = new URL(urlAddress + argString);
	    URLConnection connection = url.openConnection();
	    connection.setConnectTimeout(timeout);
	    connection.setReadTimeout(timeout);
	    connection.setUseCaches(false);
	    
	    Iterator mapIterator = requestProperties.keySet().iterator();
	    while(mapIterator.hasNext())
	    {
	    	String key = (String)mapIterator.next();
	    	String value = (String)requestProperties.get(key);
	    	connection.setRequestProperty(key, value);
	    }
	    
	    InputStream inStream = null;
	    inStream = connection.getInputStream();

	    if(logger.isInfoEnabled())
			logger.info("getContentEncoding:" + connection.getContentEncoding());
	    if(logger.isInfoEnabled())
			logger.info("getContentType:" + connection.getContentType());

	    if(logger.isInfoEnabled())
			logger.info("encoding:" + encoding);

		InputStreamReader inStreamReader = null;
	    if(encoding == null)
	    	inStreamReader = new InputStreamReader(inStream);
	    else
	    	inStreamReader = new InputStreamReader(inStream, encoding);
	    	
	    BufferedReader buffer = new BufferedReader(inStreamReader);            
	    StringBuffer strbuf = new StringBuffer();   
	    int i;
	    while ((i = buffer.read()) != -1)
	    	strbuf.append((char)i);

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
		
	public String toEncodedString(Map inMap, String encoding) throws Exception
	{
	    StringBuffer buffer = new StringBuffer();
	    Iterator inMapKeyIterator = inMap.keySet().iterator();
	    while(inMapKeyIterator.hasNext())
	    {
	        String name = inMapKeyIterator.next().toString();
	        String value = inMap.get(name).toString();
	        buffer.append(URLEncoder.encode(name, encoding) + "=" + URLEncoder.encode(value, encoding));
	        if(inMapKeyIterator.hasNext())
	        {
	            buffer.append("&");
	        }
	    }
	    return buffer.toString();
	}
}
