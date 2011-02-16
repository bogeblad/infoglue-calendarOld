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
import java.util.Set;

import javax.portlet.PortletURL;

import org.infoglue.calendar.controllers.AccessRightController;
import org.infoglue.calendar.controllers.CalendarController;
import org.infoglue.calendar.controllers.EventTypeController;
import org.infoglue.calendar.databeans.AdministrationUCCBean;
import org.infoglue.common.util.DBSessionWrapper;

import com.opensymphony.webwork.ServletActionContext;
import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionContext;

/**
 * This action represents a Calendar Administration screen.
 * 
 * @author Mattias Bogeblad
 */

public class ViewAuthorizationSwitchManagementAction extends CalendarAbstractAction
{
    private Set users;
    private Set roles;
    private Set groups;

    private List infoglueUsers;
    private List infoglueRoles;
    private List infoglueGroups;

    /**
     * This is the entry point for the main listing.
     */
    
    public String input() throws Exception 
    {
    	this.users = AccessRightController.getController().getCalendarParticipant(getSession());
    	this.roles = AccessRightController.getController().getCalendarRole(getSession());
    	this.groups = AccessRightController.getController().getCalendarGroup(getSession());

    	this.infoglueUsers = AccessRightController.getController().getPrincipals();
    	this.infoglueRoles = AccessRightController.getController().getRoles();
    	this.infoglueGroups = AccessRightController.getController().getGroups();

        return Action.INPUT;
    }

    /**
     * This is the entry point for the main listing.
     */
    
    public String execute() throws Exception 
    {    	
    	int i=0;
    	String userName = getRequestParameterValue("" + i + "_userName");
    	while(userName != null)
    	{
    		String newUserName = getRequestParameterValue("" + i + "_newUserName");
    		if(newUserName != null && !newUserName.equals(""))
    		{
    			//Transfer
    			AccessRightController.getController().complementEventParticipant(getSession(), userName, newUserName);
    		}
    		i++;
    		userName = getRequestParameterValue("" + i + "_userName");
    	}


    	i=0;
    	String name = getRequestParameterValue("" + i + "_roleName");
    	while(name != null)
    	{
    		String newName = getRequestParameterValue("" + i + "_newRoleName");
    		if(newName != null && !newName.equals(""))
    		{
    			//Transfer
    			AccessRightController.getController().complementCalendarRole(getSession(), name, newName);
    		}
    		i++;
    		name = getRequestParameterValue("" + i + "_roleName");
    	}

    	i=0;
    	name = getRequestParameterValue("" + i + "_groupName");
    	while(name != null)
    	{
    		String newName = getRequestParameterValue("" + i + "_newGroupName");
    		if(newName != null && !newName.equals(""))
    		{
    			//Transfer
    			AccessRightController.getController().complementCalendarGroup(getSession(), name, newName);
    		}
    		i++;
    		name = getRequestParameterValue("" + i + "_groupName");
    	}

    	return Action.SUCCESS;
    }

	public Set getGroups()
	{
		return groups;
	}

	public Set getRoles()
	{
		return roles;
	}

	public Set getUsers()
	{
		return users;
	}

	public List getInfoglueGroups()
	{
		return infoglueGroups;
	}

	public List getInfoglueRoles()
	{
		return infoglueRoles;
	}

	public List getInfoglueUsers()
	{
		return infoglueUsers;
	} 

    
}
