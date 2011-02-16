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

package org.infoglue.calendar.labels.actions;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoglue.calendar.actions.CalendarAbstractAction;
import org.infoglue.calendar.controllers.CalendarLabelsController;

import com.opensymphony.webwork.ServletActionContext;
import com.opensymphony.xwork.ActionSupport;

public class ViewLabelsAction extends CalendarAbstractAction
{
	private static Log log = LogFactory.getLog(ViewLabelsAction.class);

	private List labels;
	private List locales;
	
	private String selectedLanguageCode;
	
	private final static String UPDATED = "updated";
	
    /**
     * This is the entry point for the main listing.
     */
    
    public String execute() throws Exception 
    {
    	this.labels = CalendarLabelsController.getCalendarLabelsController().getSystemLabels("infoglueCalendar");
    	this.locales = CalendarLabelsController.getCalendarLabelsController().getLabelLocales("infoglueCalendar", getSession());
 
    	return SUCCESS;
    }

    /**
     * This adds a language to the xml.
     */
    
    public String addLanguage() throws Exception 
    {
    	CalendarLabelsController.getCalendarLabelsController().addLabelLocale("infoglueCalendar", selectedLanguageCode, getSession());
    	return UPDATED;
    }

    /**
     * This adds a language to the xml.
     */
    
    public String update() throws Exception 
    {
    	List labels = CalendarLabelsController.getCalendarLabelsController().getSystemLabels("infoglueCalendar");

    	Map properties = new HashMap();
    	
    	Iterator labelsIterator = labels.iterator();
    	while(labelsIterator.hasNext())
    	{
            String key = (String)labelsIterator.next();
            String value = ServletActionContext.getRequest().getParameter(key);
            if(key != null && value != null && value.length() > 0)
            {
            	value = value.replaceAll("\"", "");
            	properties.put(key, value);         	
            }
    	}

    	CalendarLabelsController.getCalendarLabelsController().updateLabels("infoglueCalendar", selectedLanguageCode, properties, getSession());
    	return UPDATED;
    }

    
	public List getLabels()
	{
		return labels;
	}

	public List getLocales()
	{
		return locales;
	}

	public String getSelectedLanguageCode()
	{
		if(selectedLanguageCode == null)
			return Locale.ENGLISH.getLanguage();
		else
			return selectedLanguageCode;
	}

	public void setSelectedLanguageCode(String selectedLanguageCode)
	{
		this.selectedLanguageCode = selectedLanguageCode;
	}

}
