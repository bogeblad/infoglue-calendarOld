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

import org.infoglue.calendar.controllers.AccessRightController;
import org.infoglue.calendar.controllers.CalendarController;
import org.infoglue.calendar.controllers.EventTypeController;
import org.infoglue.calendar.entities.EventType;
import org.infoglue.common.util.WebServiceHelper;

import com.opensymphony.webwork.ServletActionContext;
import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.validator.ValidationException;

/**
 * This action represents a Calendar Administration screen.
 * 
 * @author Mattias Bogeblad
 */

public class CreateCalendarAction extends CalendarAbstractAction
{
    private String name;
    private String description;
    private String[] roles;
    private String[] groups;
    private Long eventTypeId;
    
    private List infoglueRoles;
    private List infoglueGroups;
    private List eventTypes;

    
    /**
     * This is the entry point for the main listing.
     */
    
    public String execute() throws Exception 
    {
        try
        {
            validateInput(this);
            CalendarController.getController().createCalendar(name, description, roles, groups, eventTypeId, getSession());
        }
        catch(ValidationException e)
        {
            return Action.ERROR;            
        }
        
        return Action.SUCCESS;
    } 

    /**
     * This is the entry point creating a new calendar.
     */
    
    public String input() throws Exception 
    {
        this.infoglueRoles = AccessRightController.getController().getRoles();
        this.infoglueGroups = AccessRightController.getController().getGroups();

        this.eventTypes = EventTypeController.getController().getEventTypeList(EventType.EVENT_DEFINITION, getSession());

        return Action.INPUT;
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
    public List getEventTypes()
    {
        return eventTypes;
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

    public List getInfoglueGroups()
    {
        return infoglueGroups;
    }
    
    public List getInfoglueRoles()
    {
        return infoglueRoles;
    }

}
