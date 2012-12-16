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
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;



/**
 * URL utility class. Used for tasks involving getting url-contents from remote addresses 
 * within a program.
 * 
 * @author Mattias Bogeblad
 * @version 1
 * @since 1999-10-05
 */


public class HttpUtilities 
{            
    private final static Logger logger = Logger.getLogger(HttpUtilities.class.getName());

    /*
     *
     */
    public HttpUtilities()
    {
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
    
    public static String postToUrl(String urlAddress, Map<String,String> arguments, int timeout) throws Exception
    {      
    	URL url = new URL(urlAddress);
	    URLConnection urlConn = url.openConnection();
	    urlConn.setConnectTimeout(timeout);
	    urlConn.setReadTimeout(timeout);
	    urlConn.setUseCaches(false);
	    
	    urlConn.setAllowUserInteraction(false); 
        urlConn.setDoOutput (true); 
        urlConn.setDoInput (true); 
        urlConn.setUseCaches (false); 
        urlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        
        String returnData = null;
        InputStreamReader inStreamReader = null;
        try
        {
	        PrintWriter printout = new PrintWriter(urlConn.getOutputStream(), true); 
	        String argString = "";
	        if(arguments != null)
	        {
	            argString = toEncodedString(arguments);
	        }
	        printout.print(argString);
	        printout.flush();
	        printout.close (); 
	        InputStream inStream = null;
	        inStream = urlConn.getInputStream();
	        inStreamReader = new InputStreamReader(inStream);
	        BufferedReader buffer = new BufferedReader(inStreamReader);            
	        StringBuffer strbuf = new StringBuffer();   
	        String line; 
	        while((line = buffer.readLine()) != null) 
	        {
	            strbuf.append(line); 
	        }                                              
	        returnData = strbuf.toString();   
	        buffer.close();
        }
        catch (Exception e) 
        {
        	inStreamReader.close();
			throw e;
		}
        
        return returnData;
    }


	 
	public static String getUrlContent(String urlAddress, HttpServletRequest request, boolean includeRequest) throws Exception
	{
		if(includeRequest)
			return getUrlContent(urlAddress, requestToHashtable(request));
		else
			return getUrlContent(urlAddress);
	}

	public static String getUrlContent(String urlAddress, HttpServletRequest request, boolean includeRequest, String encoding) throws Exception
	{
		if(includeRequest)
			return getUrlContent(urlAddress, requestToHashtable(request), encoding);
		else
			return getUrlContent(urlAddress, encoding);
	}

	public static String getUrlContent(String urlAddress, Hashtable inHash) throws Exception
	{
	    String argString = "";
	    if(inHash != null)
	    {
	        if(urlAddress.indexOf("?") > -1)
		        argString = "&" + toEncodedString(inHash);
			else
				argString = "?" + toEncodedString(inHash);
	    }

		logger.info("Getting content from url: " + urlAddress + argString);
		
	    URL url = new URL(urlAddress + argString);
	    URLConnection connection = url.openConnection();
	    connection.setUseCaches(false);
	    InputStream inStream = null;
	    inStream = connection.getInputStream();
	    InputStreamReader inStreamReader = new InputStreamReader(inStream, "ISO-8859-1");
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


	public static String getUrlContent(String urlAddress, Hashtable inHash, String encoding) throws Exception
	{
		String argString = "";
		if(inHash != null)
		{
			if(urlAddress.indexOf("?") > -1)
				argString = "&" + toEncodedString(inHash);
			else
				argString = "?" + toEncodedString(inHash);
		}

		logger.info("Getting content from url: " + urlAddress + argString);
		
		URL url = new URL(urlAddress + argString);
		URLConnection connection = url.openConnection();
		connection.setUseCaches(false);
		InputStream inStream = null;
		inStream = connection.getInputStream();
		InputStreamReader inStreamReader = new InputStreamReader(inStream, encoding);
		BufferedReader buffer = new BufferedReader(inStreamReader);            
		StringBuffer strbuf = new StringBuffer();   
		String line; 
		while((line = buffer.readLine()) != null) 
		{
			strbuf.append(line); 
		}                                              
		String readData = strbuf.toString();   
		buffer.close();

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(baos, "UTF-8"));
		writer.write(readData);

		baos.flush();
		baos.close();
		writer.flush();
		writer.close();
		
		readData = new String(baos.toString(encoding));
												
		return readData;   
	}

	
	public static String getUrlContent(String urlAddress) throws Exception
	{
	    URL url = new URL(urlAddress);
	    URLConnection connection = url.openConnection();
	    connection.setUseCaches(false);
	    InputStream inStream = null;
	    inStream = connection.getInputStream();
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
	
	
	public static String getUrlContent(String urlAddress, String encoding) throws Exception
	{
		URL url = new URL(urlAddress);
		URLConnection connection = url.openConnection();
		connection.setUseCaches(false);
		InputStream inStream = null;
		inStream = connection.getInputStream();
		InputStreamReader inStreamReader = new InputStreamReader(inStream, encoding);
		BufferedReader buffer = new BufferedReader(inStreamReader);            
		StringBuffer strbuf = new StringBuffer();   
		String line; 
		while((line = buffer.readLine()) != null) 
		{
			strbuf.append(line); 
		}                                              
		String readData = strbuf.toString();   
		buffer.close();
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(baos, "UTF-8"));
		writer.write(readData);

		baos.flush();
		baos.close();
		writer.flush();
		writer.close();

		readData = new String(baos.toString(encoding));
		
		return readData;   
	}
	
	
	/**
	 * This method gets information form an URL and returns an input stream.It 
	 * throws an exception if anything goes wrong.
	 * It returns a input stream so that you can return objects.
	 * (Works like most 'doGet' methods)
	 * 
	 * @param urlAddress The address of the URL you would like to get information from.
	 * @param inHash The parameters you would like to get from the URL.
	 * @return A input stream.
	 * @exception java.lang.Exception
	 */
	
	public static InputStream getURLStream(String urlAddress, Hashtable inHash) throws Exception
	{
	    String argString = "";
	    if(inHash != null)
	    {
	        argString = "?" + toEncodedString(inHash);
	    }
	    URL url = new URL(urlAddress + argString);
	    URLConnection connection = url.openConnection();
	    connection.setUseCaches(false);
	    InputStream inStream = null;
	    inStream = connection.getInputStream();
	    return inStream;   
	}
	
	

	/**
	 * This method converts the request-object to a hashtable instead.
	 */
	
	public static Hashtable requestToHashtable(HttpServletRequest request) 
	{	
        Hashtable parameters = new Hashtable();
		
		if(request != null)
		{
			for (Enumeration e = request.getParameterNames(); e.hasMoreElements() ;) 
		    {		        
		        String name  = (String)e.nextElement();
		        String value = (String)request.getParameter(name);
	            parameters.put(name, value);
		    }        
		}
		        
        return parameters;	
		
	}

	
	/**
	 * Encodes a hash table to an URL encoded string.
	 * 
	 * @param inHash The hash table you would like to encode
	 * @return A URL encoded string.
	 */
		
	public static String toEncodedString(Map<String,String> map) throws Exception
	{
	    StringBuffer buffer = new StringBuffer();
	    int i=0;
	    for(String key : map.keySet())
	    {
	        String value = map.get(key).toString();
	        if(i > 0)
	        	buffer.append("&");
	        buffer.append(URLEncoder.encode(key, "UTF-8") + "=" + URLEncoder.encode(value, "UTF-8"));
	        i++;
	    }
	    return buffer.toString();
	}
	
	public static String toString(Hashtable inHash)
	{
	    StringBuffer buffer = new StringBuffer();
	    Enumeration names = inHash.keys();
	    while(names.hasMoreElements())
	    {
	        String name = names.nextElement().toString();
	        String value = inHash.get(name).toString();
	        buffer.append(name + "=" + value);
	        if(names.hasMoreElements())
	        {
	            buffer.append("&");
	        }
	    }
	    return buffer.toString();
	}

	/**
	 * This method removes a parameter from a given queryString
	 * @param queryString The querystring to search
	 * @param parameterName The parameter to replace
	 * @return The new queryString
	 */
	public static String removeParameter(String queryString, final String parameterName) {
	    List<String> list = new ArrayList<String>(Arrays.asList(queryString.split("&")));
	    Iterator<String> listIterator = list.iterator();
	    while(listIterator.hasNext())
	    {
	    	String parameter = listIterator.next();
	    	if(parameter.startsWith(parameterName + "="))
	    		listIterator.remove();
	    }

	    String newQueryString = "";
	    for(String param : list)
	    {
	    	if(newQueryString.length() > 0)
	    		newQueryString += "&";
	    	newQueryString += param;
	    }
	    
	    return newQueryString;
	}
}
