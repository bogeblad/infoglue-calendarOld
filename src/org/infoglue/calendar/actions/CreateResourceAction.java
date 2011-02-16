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
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.servlet.ServletInputStream;

import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.portlet.PortletFileUpload;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoglue.calendar.controllers.EventController;
import org.infoglue.calendar.controllers.LocationController;
import org.infoglue.calendar.controllers.ResourceController;
import org.infoglue.calendar.taglib.AbstractCalendarTag;

import com.opensymphony.webwork.ServletActionContext;
import com.opensymphony.webwork.util.AttributeMap;
import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.util.OgnlValueStack;

/**
 * This action represents a Calendar Administration screen.
 * 
 * @author Mattias Bogeblad
 */

public class CreateResourceAction extends CalendarUploadAbstractAction
{
	private static Log log = LogFactory.getLog(CreateResourceAction.class);

    private Long eventId;

    private Long calendarId;
    private String mode;
        
    private String message = "";
	private String returnUrl = "";
    
    /**
     * This is the entry point for the main listing.
     */
    
    public String execute() throws Exception 
    {
        log.debug("-------------Uploading file.....");
        
        File uploadedFile = null;
        String maxUploadSize = "";
        String uploadSize = "";
        
        try
        {
	        DiskFileItemFactory factory = new DiskFileItemFactory();
	        factory.setSizeThreshold(2000000);
	        //factory.setRepository(yourTempDirectory);

	        PortletFileUpload upload = new PortletFileUpload(factory);
	        
	        String maxSize = getSetting("AssetUploadMaxFileSize");
	        log.debug("maxSize:" + maxSize);
	        if(maxSize != null && !maxSize.equals("") && !maxSize.equals("@AssetUploadMaxFileSize@"))
	        {
	        	try
	        	{
		        	maxUploadSize = maxSize;
	        		upload.setSizeMax((new Long(maxSize)*1000));
	        	}
	        	catch (Exception e) 
	        	{
	        		log.warn("Faulty max size parameter sent from portlet component:" + maxSize);
				}
	        }
	        else
	        {
	        	maxSize = "" + (10*1024);
	        	maxUploadSize = maxSize;
	        	upload.setSizeMax((new Long(maxSize)*1000));
	        }
	        
	        List fileItems = upload.parseRequest(ServletActionContext.getRequest());
            log.debug("fileItems:" + fileItems.size());
	        Iterator i = fileItems.iterator();
	        while(i.hasNext())
	        {
	            Object o = i.next();
	            DiskFileItem dfi = (DiskFileItem)o;
	            log.debug("dfi:" + dfi.getFieldName());
	            log.debug("dfi:" + dfi);
	            
	            if (dfi.isFormField()) 
	            {
	                String name = dfi.getFieldName();
	                String value = dfi.getString();
	                uploadSize = "" + (dfi.getSize() / 1000);
	                
	                log.debug("name:" + name);
	                log.debug("value:" + value);
	                if(name.equals("assetKey"))
	                {
	                    this.assetKey = value;
	                }
	                else if(name.equals("eventId"))
	                {
	                    this.eventId = new Long(value);
	                    ServletActionContext.getRequest().setAttribute("eventId", this.eventId);
	                }
	                else if(name.equals("calendarId"))
	                {
	                    this.calendarId = new Long(value);
	                	ServletActionContext.getRequest().setAttribute("calendarId", this.calendarId);
	            	}
	               	else if(name.equals("mode"))
	                    this.mode = value;
	            }
	            else
	            {
	                String fieldName = dfi.getFieldName();
	                String fileName = dfi.getName();
	                uploadSize = "" + (dfi.getSize() / 1000);
	                
	                this.fileName = fileName;
	                log.debug("FileName:" + this.fileName);
	                uploadedFile = new File(getTempFilePath() + File.separator + fileName);
	                dfi.write(uploadedFile);
	            }

	        }
	    }
        catch(Exception e)
        {
        	ServletActionContext.getRequest().getSession().setAttribute("errorMessage", "Exception uploading resource. " + e.getMessage() + ".<br/>Max upload size: " + maxUploadSize + " KB"); // + "<br/>Attempted upload size (in KB):" + uploadSize);
        	ActionContext.getContext().getValueStack().getContext().put("errorMessage", "Exception uploading resource. " + e.getMessage() + ".<br/>Max upload size: " + maxUploadSize + " KB"); // + "<br/>Attempted upload size (in KB):" + uploadSize);
        	log.error("Exception uploading resource. " + e.getMessage());
        	return Action.ERROR;
        }
 
        try
        {
	        log.debug("Creating resources.....:" + this.eventId + ":" + ServletActionContext.getRequest().getParameter("eventId") + ":" + ServletActionContext.getRequest().getParameter("calendarId"));
	        ResourceController.getController().createResource(this.eventId, this.getAssetKey(), this.getFileContentType(), this.getFileName(), uploadedFile, getSession());
        }
        catch (Exception e) 
        {
        	ServletActionContext.getRequest().getSession().setAttribute("errorMessage", "Exception saving resource to database: " + e.getMessage());
        	ActionContext.getContext().getValueStack().getContext().put("errorMessage", "Exception saving resource to database: " + e.getMessage());
        	log.error("Exception saving resource to database. " + e.getMessage());
        	return Action.ERROR;
		}
        
        return Action.SUCCESS;
    } 
    
    public String getEventIdAsString()
    {
        return eventId.toString();
    }

    public String getCalendarIdAsString()
    {
        return calendarId.toString();
    }
    
    public Long getEventId()
    {
        return eventId;
    }
    
    public void setEventId(Long eventId)
    {
        this.eventId = eventId;
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

    public String getMessage() 
    {
		return message;
	}

	public String getReturnUrl() 
	{
		return returnUrl;
	}

}
