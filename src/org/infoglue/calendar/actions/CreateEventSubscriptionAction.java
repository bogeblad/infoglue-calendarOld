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

import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoglue.calendar.controllers.AccessRightController;
import org.infoglue.calendar.controllers.CalendarController;
import org.infoglue.calendar.controllers.LocationController;
import org.infoglue.calendar.controllers.ResourceController;
import org.infoglue.calendar.controllers.SubscriptionController;
import org.infoglue.calendar.entities.Calendar;
import org.infoglue.calendar.entities.Subscriber;
import org.infoglue.calendar.taglib.AbstractCalendarTag;
import org.infoglue.common.security.beans.InfoGluePrincipalBean;
import org.infoglue.common.util.WebServiceHelper;

import com.opensymphony.webwork.ServletActionContext;
import com.opensymphony.webwork.util.AttributeMap;
import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.util.OgnlValueStack;

/**
 * This action represents a Calendar Administration screen.
 * 
 * @author Mattias Bogeblad
 */

public class CreateEventSubscriptionAction extends CalendarUploadAbstractAction
{
	private static Log log = LogFactory.getLog(CreateEventSubscriptionAction.class);

    private Long subscriptionId;
    private Long calendarId;
    
    private Subscriber subscriper;
    private Set calendars;
    
    /**
     * This is the entry point for the main listing.
     */
    
    public String execute() throws Exception 
    {
        InfoGluePrincipalBean principal = (InfoGluePrincipalBean)AccessRightController.getController().getPrincipal(this.getInfoGlueRemoteUser());
        //InfoGluePrincipal principal = UserControllerProxy.getController().getUser(this.getInfoGlueRemoteUser());
        
        Calendar calendar = CalendarController.getController().getCalendar(calendarId, getSession());
        
        this.subscriper = SubscriptionController.getController().createSubscriber(principal.getEmail(), calendar, getSession());
        
        return Action.SUCCESS;
    } 

    /**
     * This is the entry point creating a new subscriptions.
     */
    
    public String input() throws Exception 
    {
        InfoGluePrincipalBean principal = (InfoGluePrincipalBean)AccessRightController.getController().getPrincipal(this.getInfoGlueRemoteUser());
        //InfoGluePrincipal principal = UserControllerProxy.getController().getUser(this.getInfoGlueRemoteUser());

        this.calendars = CalendarController.getController().getUnsubscribedCalendarList(principal.getEmail(), getSession());
            
        return Action.INPUT;
    } 

    public Long getSubscriptionId()
    {
        return subscriptionId;
    }
    
    public void setSubscriptionId(Long subscriptionId)
    {
        this.subscriptionId = subscriptionId;
    }
    
    public Long getCalendarId()
    {
        return calendarId;
    }
    
    public void setCalendarId(Long calendarId)
    {
        this.calendarId = calendarId;
    }
    
    public Subscriber getSubscriper()
    {
        return subscriper;
    }
    
    public Set getCalendars()
    {
        return calendars;
    }
}
