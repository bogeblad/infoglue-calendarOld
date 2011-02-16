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
package org.infoglue.calendar.taglib;

import java.io.IOException;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.jsp.JspTagException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.infoglue.calendar.util.CalendarLabelHelper;
import org.infoglue.common.util.ResourceBundleHelper;

import com.opensymphony.webwork.ServletActionContext;
import com.opensymphony.webwork.portlet.alternative.action.ActionResult;
import com.opensymphony.xwork.ActionContext;


/**
 * Base class for all Tags operating on the TemplateController.
 */

public abstract class AbstractCalendarTag extends AbstractTag 
{
	private static Log log = LogFactory.getLog(AbstractCalendarTag.class);

	private String id;
	
	
	protected AbstractCalendarTag()
	{
		super();
	}
	
    public void setId(String id)
    {
        this.id = id;
    }

	protected void setResultAttribute(Object value)
	{
		if(value == null)
			pageContext.removeAttribute(id);
		else
			pageContext.setAttribute(id, value);
		
	}
	
	protected void produceResult(Object value) throws JspTagException
	{
	    if(id == null)
			write(value.toString());
		else
			setResultAttribute(value);
	}
	
	protected void write(String s) throws JspTagException
	{
		try 
		{
			pageContext.getOut().write(s);
		}
		catch(IOException e)
		{
			e.printStackTrace();
			throw new JspTagException("IO error: " + e.getMessage());
		}
	}
	
    public static Object findOnValueStack(String expr) 
    {
		ActionContext a = ActionContext.getContext();
		Object value = a.getValueStack().findValue(expr);
		return value;
	}
    
	public String getLanguageCode()
    {
        return (String)ServletActionContext.getRequest().getAttribute("languageCode");
    }
	
	
	public String getLabel(String key)
    {
	    String label = key;
	    
	    try
	    {
	    	label = CalendarLabelHelper.getHelper().getLabel(key, this.getLanguageCode(), getSession());
	    	/*
	        Object derivedValue = findOnValueStack(key);
	    	Locale locale = new Locale(this.getLanguageCode());
	    	ResourceBundle resourceBundle = ResourceBundleHelper.getResourceBundle("infoglueCalendar", locale);
	    	if(derivedValue != null)
	    	    label = resourceBundle.getString("" + derivedValue.toString());
	        else
	            label = resourceBundle.getString(key);
	        */    
	    	if(label == null || label.equals(""))
	            label = key;
	    }
	    catch(Exception e)
	    {
	        log.info("An label was not found:" + e.getMessage(), e);
	    }
	    
        return label;
    }

	
	public String getLabel(String key, Map values)
    {
	    String label = key;
	    
	    try
	    {
	        Object derivedValue = findOnValueStack(key);
	    	Locale locale = new Locale(this.getLanguageCode());
	    	ResourceBundle resourceBundle = ResourceBundleHelper.getResourceBundle("infoglueCalendar", locale);
	    	
	        if(values != null)
	        {
	            Iterator valuesIterator = values.keySet().iterator();
	            while(valuesIterator.hasNext())
	    	    {
	                String id = (String)valuesIterator.next();
	                log.info("Id:" + id);
	                String optionText 	= (String)values.get(id);
	                log.info("optionText:" + optionText);

	                log.info("derivedValue:" + derivedValue);
	                String checked = "";
	                if(key != null)
	                {
	                    if(id.equalsIgnoreCase(key.toString()))
	                        derivedValue = optionText;
	                }
	            }
	            
		    	if(derivedValue != null)
		    	    label = resourceBundle.getString("" + derivedValue.toString());
		        else
		            label = resourceBundle.getString(key);

	        }
	        else
	        {
		    	if(derivedValue != null)
		    	    label = resourceBundle.getString("" + derivedValue.toString());
		        else
		            label = resourceBundle.getString(key);
	        }

	        if(label == null || label.equals(""))
	            label = key;
	    }
	    catch(Exception e)
	    {
	        log.warn("An label was not found:" + e.getMessage(), e);
	    }
	    
        return label;
    }

	public Session getSession() throws HibernateException {
	    return (Session)ServletActionContext.getRequest().getAttribute("HIBERNATE_SESSION");
	}

}
