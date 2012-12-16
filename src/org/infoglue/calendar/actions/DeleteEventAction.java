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
import org.infoglue.calendar.controllers.EventController;
import org.infoglue.calendar.databeans.AdministrationUCCBean;
import org.infoglue.calendar.entities.Calendar;
import org.infoglue.common.util.DBSessionWrapper;

import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionContext;

/**
 * This action represents a Calendar Administration screen.
 * 
 * @author Mattias Bogeblad
 */

public class DeleteEventAction extends CalendarAbstractAction
{
    private Long eventId;
    
    private Long calendarId;
    private String mode;

    /**
     * This is the entry point for the main listing.
     */
    
    public String execute() throws Exception 
    {
    	if(this.getInfoGluePrincipal().getName().equals("eventPublisher"))
    		return Action.ERROR;  
    	else
    	{
			EventController.getController().deleteEvent(eventId, getSession());
	        return Action.SUCCESS;
    	}
    } 

    public String linkedPublished() throws Exception 
    {
    	if(this.getInfoGluePrincipal().getName().equals("eventPublisher"))
    		return Action.ERROR;  
    	else
    	{
    		EventController.getController().deleteLinkedEvent(eventId, calendarId, getSession());
            return "successPublished";
    	}
    } 

    public String published() throws Exception 
    {
        execute();
        
        return "successPublished";
    } 

    public String working() throws Exception 
    {
        execute();
        
        return "successWorking";
    } 

    public String search() throws Exception 
    {
        execute();
        
        return "successSearch";
    } 

    public Long getEventId()
    {
        return eventId;
    }
    
    public void setEventId(Long eventId)
    {
        this.eventId = eventId;
    }
    
    public Long getCalendarId()
    {
        return calendarId;
    }
    
    public void setCalendarId(Long calendarId)
    {
        this.calendarId = calendarId;
    }
    
    public String getMode()
    {
        return mode;
    }
    
    public void setMode(String mode)
    {
        this.mode = mode;
    }
    

}
