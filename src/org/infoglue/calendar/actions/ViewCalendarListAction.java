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

import org.infoglue.calendar.controllers.CalendarController;
import org.infoglue.calendar.databeans.AdministrationUCCBean;
import org.infoglue.common.util.DBSessionWrapper;

import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionContext;

/**
 * This action represents a Calendar Administration screen.
 * 
 * @author Mattias Bogeblad
 */

public class ViewCalendarListAction extends CalendarAbstractAction
{
    private Set calendars;
    private Long eventId;
    
    /**
     * This is the entry point for the main listing.
     */
    
    public String execute() throws Exception 
    {
        this.calendars = CalendarController.getController().getCalendarList(this.getSession());

        return Action.SUCCESS;
    } 

    /**
     * This is the entry point for the main listing.
     */
    
    public String choose() throws Exception 
    {
        execute();

        return "successChoose";
    } 
    
    /**
     * This is the entry point for the main listing.
     */
    
    public String chooseCopyTarget() throws Exception 
    {
        execute();

        return "successChooseForCopy";
    } 

    /**
     * This is the entry point for the main listing.
     */
    
    public String chooseLinkTarget() throws Exception 
    {
        this.calendars = CalendarController.getController().getCalendarList(this.getInfoGlueRemoteUserRoles(), this.getInfoGlueRemoteUserGroups(), this.getSession());

        return "successChooseForLink";
    } 

    /**
     * This is the entry point for the main listing.
     */
    
    public String chooseDeleteLink() throws Exception 
    {
        this.calendars = CalendarController.getController().getCalendarList(this.getInfoGlueRemoteUserRoles(), this.getInfoGlueRemoteUserGroups(), this.getSession());

        return "successChooseForDeleteLink";
    } 

    public Set getCalendars()
    {
        return calendars;
    }
    
    public Long getEventId()
    {
        return eventId;
    }
    
    public void setEventId(Long eventId)
    {
        this.eventId = eventId;
    }
}
