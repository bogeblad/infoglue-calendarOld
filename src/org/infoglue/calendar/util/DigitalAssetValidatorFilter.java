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

package org.infoglue.calendar.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.activation.FileDataSource;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.infoglue.calendar.controllers.ResourceController;
import org.infoglue.calendar.entities.Resource;
import org.infoglue.common.util.HibernateUtil;
import org.infoglue.common.util.PropertyHelper;

/**
 * This filter empties the caches waiting to be emptied.
 * 
 * @author Mattias Bogeblad
 */

public class DigitalAssetValidatorFilter implements Filter 
{
    public final static Logger logger = Logger.getLogger(DigitalAssetValidatorFilter.class.getName());

    public void init(FilterConfig filterConfig) throws ServletException 
    {
    }

    private boolean isNumeric(String s) 
    {
    	try 
    	{
    		Long.parseLong(s);
    	}
    	catch (NumberFormatException nfe) 
    	{
    		return false;
    	}
    	
    	return true;
    }

    
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException 
    {
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
                
        //System.out.println("httpRequest:" + httpRequest.getRequestURI());
        String assetFileName = httpRequest.getRequestURI().substring(httpRequest.getRequestURI().lastIndexOf("/") + 1);
        //System.out.println("assetFileName:" + assetFileName);
        if(assetFileName.indexOf("_") == -1)
        {
	        String digitalAssetPath = PropertyHelper.getProperty("digitalAssetPath");
			//String fileName = resource.getId() + "_" + resource.getAssetKey() + "_" + resource.getFileName();
	        File assetFile = new File(digitalAssetPath + File.separator + assetFileName);
	        //System.out.println("assetFile:" + assetFile.getPath() + " : " + assetFile.exists());
	        if(!assetFile.exists())
	        {
	        	//System.out.println("Now we must write the file to disk again....");
	        	String assetId = assetFileName.substring(0,assetFileName.indexOf("_"));
	        	//System.out.println("assetId:" + assetId);
	        	if(isNumeric(assetId))
	        	{
		        	Session session = HibernateUtil.currentSession();
		        	Transaction tx = null;
		        	try 
		        	{
		        		tx = session.beginTransaction();
		
		        		//Resource resource = ResourceController.getController().getResource(new Long(assetId), session);
		        		String url = ResourceController.getController().getResourceUrl(new Long(assetId), session);
		        		Thread.sleep(5000);
		        		//System.out.println("assetFile:" + assetFile.exists() + " for url:" + url);
		        		//FileDataSource fds = new FileDataSource(assetFile);
		        		//System.out.println("assetFile content type:" + fds.getContentType());
		        		String contentType = "application/octet-stream";
		        		if(url.endsWith(".png")) contentType = "image/png";
		        		else if(url.endsWith(".jpg")) contentType = "image/jpeg";
		        		else if(url.endsWith(".gif")) contentType = "image/gif";
		        		
		        		else if(url.endsWith(".pdf")) contentType = "application/pdf";
		        		else if(url.endsWith(".doc")) contentType = "application/msword";
		        		else if(url.endsWith(".docx")) contentType = "application/msword";
		        		else if(url.endsWith(".dot")) contentType = "application/msword";
		
		        		else if(url.endsWith(".eps")) contentType = "application/postscript";
		        		else if(url.endsWith(".xls")) contentType = "application/vnd.ms-excel";
		        		else if(url.endsWith(".ppt")) contentType = "application/vnd.ms-powerpoint";
		        		else if(url.endsWith(".dot")) contentType = "application/msword";
		
		        		else if(url.endsWith(".txt")) contentType = "text/plain";
		        		else if(url.endsWith(".html")) contentType = "text/html";
		        		else if(url.endsWith(".htm")) contentType = "text/html";
		        		
		        		httpResponse.setContentType(contentType);
		
		    	        // print some html
		    	        ServletOutputStream out = httpResponse.getOutputStream();
		    	        
		    	        // print the file
		    	        InputStream in = null;
		    	        try 
		    	        {
		    	            in = new BufferedInputStream(new FileInputStream(assetFile));
		    	            int ch;
		    	            while ((ch = in.read()) !=-1) 
		    	            {
		    	                out.print((char)ch);
		    	            }
		    	        }
		    	        finally 
		    	        {
		    	            if (in != null) in.close();  // very important
		    	        }
		
		        		//System.out.println("url:" + url);
		        		//httpResponse.sendRedirect(url + "?rand=" + System.currentTimeMillis());
		        		tx.commit();
		        	}
		        	catch (Exception e) 
		        	{
		        		e.printStackTrace();
		        		if (tx!=null) tx.rollback();
		        	}
		        	finally 
		        	{
		        		HibernateUtil.closeSession();
		        	}
	        	}
	        	
	    		return;
	        }
        }
        
        filterChain.doFilter(httpRequest, httpResponse);
    }

    public void destroy() 
    {
    }


}