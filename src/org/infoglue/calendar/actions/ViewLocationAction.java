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

import java.util.ArrayList;
import java.util.List;

import org.hibernate.HibernateException;
import org.infoglue.calendar.controllers.LanguageController;
import org.infoglue.calendar.controllers.LocationController;
import org.infoglue.calendar.entities.Language;
import org.infoglue.calendar.entities.Location;

import com.opensymphony.xwork.Action;

/**
 * This action represents a Location Administration screen.
 * 
 * @author Mattias Bogeblad
 */

public class ViewLocationAction extends CalendarAbstractAction
{
    private Long locationId;
    private Location location;
    private Long systemLanguageId;
    
    private List availableLanguages = new ArrayList();

    /**
     * This is the entry point for the main listing.
     */
    
    public String execute() throws Exception 
    {
        this.location = LocationController.getController().getLocation(locationId, getSession());

        this.availableLanguages = LanguageController.getController().getLanguageList(getSession());
        if(this.systemLanguageId == null && this.availableLanguages.size() > 0)
        {
        	this.systemLanguageId = ((Language)this.availableLanguages.get(0)).getId();
        }

        return Action.SUCCESS;
    } 

    public Long getLocationId()
    {
        return locationId;
    }
    
    public void setLocationId(Long locationId)
    {
        this.locationId = locationId;
    }

    public Location getLocation()
    {
        return location;
    }

	public List getAvailableLanguages() 
	{
		return availableLanguages;
	}

	public Long getSystemLanguageId() 
	{
		return systemLanguageId;
	}

	public void setSystemLanguageId(Long systemLanguageId) 
	{
		this.systemLanguageId = systemLanguageId;
	}

	public Language getLanguage() throws Exception 
	{
		return LanguageController.getController().getLanguage(getSystemLanguageId(), getSession());
	}

}
