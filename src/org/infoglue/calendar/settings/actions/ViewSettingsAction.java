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

package org.infoglue.calendar.settings.actions;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoglue.calendar.actions.CalendarAbstractAction;
import org.infoglue.calendar.controllers.CalendarLabelsController;
import org.infoglue.calendar.controllers.CalendarSettingsController;

import com.opensymphony.webwork.ServletActionContext;
import com.opensymphony.xwork.ActionSupport;

public class ViewSettingsAction extends CalendarAbstractAction
{
	private static Log log = LogFactory.getLog(ViewSettingsAction.class);

	private String variationId = "default";
	private List settings;
	
	private final static String UPDATED = "updated";
	
    /**
     * This is the entry point for the main listing.
     */
    
    public String execute() throws Exception 
    {
    	this.settings = CalendarSettingsController.getCalendarSettingsController().getSystemSettings("infoglueCalendar");
 
    	return SUCCESS;
    }

    /**
     * This adds a language to the xml.
     */
/*    
    public String addLanguage() throws Exception 
    {
    	CalendarLabelsController.getCalendarLabelsController().addLabelLocale("infoglueCalendar", selectedLanguageCode, getSession());
    	return UPDATED;
    }
*/
    
    /**
     * This adds a language to the xml.
     */
    
    public String update() throws Exception 
    {
    	List settings = CalendarSettingsController.getCalendarSettingsController().getSystemSettings("infoglueCalendar");

    	Map properties = new HashMap();
    	
    	Iterator settingsIterator = settings.iterator();
    	while(settingsIterator.hasNext())
    	{
            String key = (String)settingsIterator.next();
            String value = ServletActionContext.getRequest().getParameter(key);
            if(key != null && value != null && value.length() > 0)
            	properties.put(key, value);
    	}

    	CalendarSettingsController.getCalendarSettingsController().updateSettings("infoglueCalendar", variationId, properties, getSession());
    	
    	return UPDATED;
    }

	public List getSettings()
	{
		return settings;
	}

	public String getVariationId()
	{
		return variationId;
	}

	public void setVariationId(String variationId)
	{
		this.variationId = variationId;
	}

    

}
