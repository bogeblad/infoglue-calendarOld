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

import java.util.List;

import javax.portlet.PortletURL;

import org.infoglue.calendar.controllers.CalendarController;
import org.infoglue.calendar.controllers.LocationController;
import org.infoglue.calendar.databeans.AdministrationUCCBean;
import org.infoglue.calendar.entities.Calendar;
import org.infoglue.common.util.DBSessionWrapper;

import com.opensymphony.webwork.ServletActionContext;
import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.validator.ValidationException;

/**
 * This action represents a Calendar Administration screen.
 * 
 * @author Mattias Bogeblad
 */

public class UpdateCalendarAction extends CalendarAbstractAction
{
    private Long calendarId;
    private String name;
    private String description;
    private String[] roles;
    private String[] groups;
    private Long eventTypeId;

    /**
     * This is the entry point for the main listing.
     */
    
    public String execute() throws Exception 
    {
        try
        {
            validateInput(this);
            //log.debug("calendarId: " + calendarId);
            if(this.eventTypeId == null)
            {
                String eventTypeIdString = ServletActionContext.getRequest().getParameter("eventTypeId");
                if(eventTypeIdString != null && !eventTypeIdString.equals(""))
                	this.eventTypeId = new Long(ServletActionContext.getRequest().getParameter("eventTypeId"));
            }
            
            CalendarController.getController().updateCalendar(calendarId, name, description, roles, groups, eventTypeId, getSession());
        }
        catch(ValidationException e)
        {
            return Action.ERROR;            
        }
        
        return Action.SUCCESS;
    } 
    
 
    public Long getCalendarId()
    {
        return calendarId;
    }
    public void setCalendarId(Long calendarId)
    {
    	this.calendarId = calendarId;
    }
    public String getDescription()
    {
        return description;
    }
    public void setDescription(String description)
    {
        this.description = description;
    }
    public String getName()
    {
        return name;
    }
    public void setName(String name)
    {
        this.name = name;
    }
    
    public Long getEventTypeId()
    {
        return eventTypeId;
    }
    
    public void setEventTypeId(Long eventTypeId)
    {
        this.eventTypeId = eventTypeId;
    }
    
    public String[] getGroups()
    {
        return groups;
    }
    
    public void setGroups(String[] groups)
    {
        this.groups = groups;
    }
    
    public String[] getRoles()
    {
        return roles;
    }

    public void setRoles(String[] roles)
    {
        this.roles = roles;
    }
    
    public void setGroups(String groups)
    {
        this.groups = new String[] {groups};
    }
    
    public void setRoles(String roles)
    {
        this.groups = new String[] {roles};
    }

}
