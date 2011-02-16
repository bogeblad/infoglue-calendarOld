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
import java.util.Collection;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import org.infoglue.calendar.controllers.AccessRightController;
import org.infoglue.calendar.controllers.EntryController;
import org.infoglue.calendar.controllers.InterceptionPointController;
import org.infoglue.calendar.entities.Entry;
import org.infoglue.calendar.entities.Event;
import org.infoglue.common.exceptions.Bug;
import org.infoglue.common.exceptions.SystemException;
import org.infoglue.common.util.WebServiceHelper;

import com.opensymphony.xwork.Action;

/**
 * This action represents a Location Administration screen.
 * 
 * @author Mattias Bogeblad
 */

public class ViewAccessRightsAction extends CalendarAbstractAction
{
	private static Log log = LogFactory.getLog(ViewAccessRightsAction.class);

	private Long interceptionPointId = null;
	private String interceptionPointName = null;
	private String interceptionPointCategory = null;
	private String extraParameters = "";
	private String returnAddress;
	private String colorScheme;

	private List interceptionPointList = new ArrayList();
	private List roleList = null;
	private List groupList = null;
	private Collection accessRightsUserRows = null;

        
    /**
     * This is the entry point for the main listing.
     */
    
    public String execute() throws Exception 
    {
    	log.debug("interceptionPointCategory:" + interceptionPointCategory);
    	log.debug("extraParameters:" + extraParameters);
		this.interceptionPointList = InterceptionPointController.getController().getInterceptionPointList(interceptionPointCategory, getSession());
		
        this.roleList = AccessRightController.getController().getRoles();
        this.groupList = AccessRightController.getController().getGroups();

		//this.roleList = RoleControllerProxy.getController().getAllRoles();
        //this.groupList = GroupControllerProxy.getController().getAllGroups();

        //this.infogluePrincipals = UserControllerProxy.getController().getAllUsers();
        //this.accessRightsUserRows = AccessRightController.getController().getAccessRightsUserRows(interceptionPointCategory, extraParameters);

        return Action.SUCCESS;
    } 

	public boolean getHasAccessRight(Long interceptionPointId, String extraParameters, String roleName) throws SystemException, Bug
	{
	    try
	    {
			List accessRights = AccessRightController.getController().getAccessRightList(interceptionPointId, extraParameters, roleName, getSession());
			boolean hasAccessRight = (accessRights.size() > 0) ? true : false;
			return hasAccessRight;
	    }
	    catch(Exception e)
	    {
	        log.warn(e);
	        throw new SystemException(e);
	    }
	}
	/*
	public Integer getAccessRightId(Integer interceptionPointId, String extraParameters) throws SystemException, Bug
	{
		List accessRights = AccessRightController.getController().getAccessRightVOListOnly(interceptionPointId, extraParameters);
		return accessRights.size() > 0 ? ((AccessRightVO)accessRights.get(0)).getAccessRightId() : null;
	}
	
	public Collection getAccessRightGroups(Integer accessRightId) throws SystemException, Bug
	{
	    Collection accessRightGroups = AccessRightController.getController().getAccessRightGroupVOList(accessRightId);
		return accessRightGroups;
	}
	*/
	
	public List getRoleList()
	{
		return this.roleList;
	}
	
	public List getGroupList()
	{
		return this.groupList;
	}

	public String getReturnAddress()
	{
		return returnAddress;
	}

	public void setReturnAddress(String returnAddress)
	{
		this.returnAddress = returnAddress;
	}

	public String getColorScheme()
	{
		return this.colorScheme;
	}

	public void setColorScheme(String colorScheme)
	{
		this.colorScheme = colorScheme;
	}

	public Long getInterceptionPointId()
	{
		return this.interceptionPointId;
	}

	public void setInterceptionPointId(Long interceptionPointId)
	{
		this.interceptionPointId = interceptionPointId;
	}

	public String getExtraParameters()
	{
		return this.extraParameters;
	}

	public String getInterceptionPointName()
	{
		return this.interceptionPointName;
	}

	public void setExtraParameters(String extraParameters)
	{
		this.extraParameters = extraParameters;
	}

	public void setInterceptionPointName(String interceptionPointName)
	{
		this.interceptionPointName = interceptionPointName;
	}

	public String getInterceptionPointCategory()
	{
		return this.interceptionPointCategory;
	}

	public void setInterceptionPointCategory(String interceptionPointCategory)
	{
		this.interceptionPointCategory = interceptionPointCategory;
	}

	public List getInterceptionPointList()
	{
		return this.interceptionPointList;
	}

    public Collection getAccessRightsUserRows()
    {
        return accessRightsUserRows;
    }

}
