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

import org.hibernate.Session;
import org.infoglue.calendar.controllers.CalendarController;
import org.infoglue.calendar.controllers.EventController;
import org.infoglue.calendar.controllers.LocationController;
import org.infoglue.calendar.databeans.AdministrationUCCBean;
import org.infoglue.calendar.entities.Calendar;
import org.infoglue.common.util.DBSessionWrapper;

import com.opensymphony.webwork.ServletActionContext;
import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionContext;

/**
 * This action represents a Calendar Administration screen.
 * 
 * @author Mattias Bogeblad
 */

public class ViewMyWorkingEventListAction extends CalendarAbstractAction
{
    private Set events;
    
    /**
     * This is the entry point for the main listing.
     */
    
    public String execute() throws Exception 
    {
    	Session session = getSession(true);
    	
        this.events = EventController.getController().getMyWorkingEventList(this.getInfoGlueRemoteUser(), this.getInfoGlueRemoteUserRoles(), this.getInfoGlueRemoteUserGroups(), session);

        return Action.SUCCESS;
    } 
    
    public Set getEvents()
    {
        return events;
    }
    
}
