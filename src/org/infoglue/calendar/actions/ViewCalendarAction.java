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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoglue.calendar.controllers.AccessRightController;
import org.infoglue.calendar.controllers.CalendarController;
import org.infoglue.calendar.controllers.EventController;
import org.infoglue.calendar.controllers.EventTypeController;
import org.infoglue.calendar.controllers.LanguageController;
import org.infoglue.calendar.entities.Calendar;
import org.infoglue.calendar.entities.Event;
import org.infoglue.calendar.entities.EventType;
import org.infoglue.common.util.WebServiceHelper;

import com.opensymphony.xwork.Action;

/**
 * This action represents a Calendar Administration screen.
 * 
 * @author Mattias Bogeblad
 */

public class ViewCalendarAction extends CalendarAbstractAction
{
	private static Log log = LogFactory.getLog(ViewCalendarAction.class);

    private Integer componentId;
    private Long calendarId;
    private String mode;
    private String selectedFormattedDate;
    private String startDateTime;
    private String endDateTime;
    
    private java.util.Calendar startCalendar = null;
    private java.util.Calendar endCalendar = null;
    
    private Calendar calendar;
    private Set waitingEvents;
    private Set publishedEvents;

    private Set events;
    private Set weekEvents;
    private Set monthEvents;
    private List dates;
    
    private List infoglueRoles;
    private List infoglueGroups;
    private List eventTypes;
    private List languages;
    
    private String[] roles;
    private String[] groups;

    private String[] systemLanguageId;

    /**
     * This is the entry point for the main listing.
     */
    
    public String execute() throws Exception 
    {
        this.componentId = getComponentId();
        log.debug("****************************");
        log.debug("calendarId:" + calendarId);
        log.debug("componentId:" + componentId);
        log.debug("startDateTime:" + startDateTime);
        log.debug("endDateTime:" + endDateTime);
        log.debug("mode:" + mode);
        log.debug("****************************");
        this.calendar = CalendarController.getController().getCalendar(calendarId, this.getSession());
        log.debug("calendar: " + calendar.getName());
        
        this.startCalendar = super.getCalendar(startDateTime + " 00:00", "yyyy-MM-dd HH:mm", true);
        this.endCalendar   = super.getCalendar(endDateTime + " 23:59", "yyyy-MM-dd HH:mm", true);
        //log.debug("startDateTime:" + startDateTime);
        //log.debug("startCalendar:" + startCalendar.getTime());
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.setTime(new Date(startCalendar.getTime().getTime()));

        java.util.Calendar monthStartCalendar = java.util.Calendar.getInstance();
        monthStartCalendar.setTime(new Date(startCalendar.getTime().getTime()));
        monthStartCalendar.set(java.util.Calendar.DAY_OF_MONTH, 1);
        monthStartCalendar.set(java.util.Calendar.HOUR_OF_DAY, 0);
        monthStartCalendar.set(java.util.Calendar.MINUTE, 0);
        monthStartCalendar.set(java.util.Calendar.SECOND, 0);
        monthStartCalendar.set(java.util.Calendar.MILLISECOND, 0);
        log.debug("monthStartCalendar:" + monthStartCalendar.getTime());
        
        java.util.Calendar monthEndCalendar = java.util.Calendar.getInstance();
        monthEndCalendar.setTime(new Date(startCalendar.getTime().getTime()));
        monthEndCalendar.set(java.util.Calendar.DAY_OF_MONTH, monthEndCalendar.getActualMaximum(java.util.Calendar.DAY_OF_MONTH) );
        monthEndCalendar.set(java.util.Calendar.HOUR_OF_DAY, 23);
        monthEndCalendar.set(java.util.Calendar.MINUTE, 59);
        monthEndCalendar.set(java.util.Calendar.SECOND, 59);
        monthEndCalendar.set(java.util.Calendar.MILLISECOND, 999);
        log.debug("monthEndCalendar:" + monthEndCalendar.getTime());

        java.util.Calendar weekStartCalendar = java.util.Calendar.getInstance();
        weekStartCalendar.setTime(new Date(startCalendar.getTime().getTime()));
        weekStartCalendar.set(java.util.Calendar.DAY_OF_WEEK, java.util.Calendar.MONDAY);
        weekStartCalendar.set(java.util.Calendar.HOUR_OF_DAY, 0);
        weekStartCalendar.set(java.util.Calendar.MINUTE, 0);
        weekStartCalendar.set(java.util.Calendar.SECOND, 0);
        weekStartCalendar.set(java.util.Calendar.MILLISECOND, 0);
        
        java.util.Calendar weekEndCalendar = java.util.Calendar.getInstance();
        weekEndCalendar.setTime(new Date(startCalendar.getTime().getTime()));
        weekEndCalendar.set(java.util.Calendar.DAY_OF_WEEK, weekEndCalendar.getActualMaximum(java.util.Calendar.DAY_OF_WEEK));
        weekEndCalendar.set(java.util.Calendar.HOUR_OF_DAY, 23);
        weekEndCalendar.set(java.util.Calendar.MINUTE, 59);
        weekEndCalendar.set(java.util.Calendar.SECOND, 59);
        weekEndCalendar.set(java.util.Calendar.MILLISECOND, 999);

        //log.debug("DAY_OF_WEEK: " + weekStartCalendar.get(java.util.Calendar.DAY_OF_WEEK));
        //log.debug("DAY_OF_WEEK_IN_MONTH: " + weekStartCalendar.get(java.util.Calendar.DAY_OF_WEEK_IN_MONTH));
        //log.debug("DATE:" + weekStartCalendar.getTime());
        //log.debug("DAY_OF_WEEK: " + weekEndCalendar.get(java.util.Calendar.DAY_OF_WEEK));
        //log.debug("DAY_OF_WEEK_IN_MONTH: " + weekEndCalendar.get(java.util.Calendar.DAY_OF_WEEK_IN_MONTH));
        //log.debug("DATE:" + weekEndCalendar.getTime());

        //log.debug("weekStartCalendar:" + weekStartCalendar.getTime());
        //log.debug("weekEndCalendar:" + weekEndCalendar.getTime());

        if(useEventPublishing())
        {
            this.waitingEvents = EventController.getController().getEventList(calendarId, Event.STATE_WORKING, startCalendar, endCalendar, getSession());
        }
        
        this.publishedEvents = EventController.getController().getEventList(calendarId, Event.STATE_PUBLISHED, startCalendar, endCalendar, getSession());
        
        this.events = EventController.getController().getEventList(calendarId, Event.STATE_PUBLISHED, startCalendar, endCalendar, getSession());
        this.weekEvents = EventController.getController().getEventList(calendarId, Event.STATE_PUBLISHED, weekStartCalendar, weekEndCalendar, getSession());
        this.monthEvents = EventController.getController().getEventList(calendarId, Event.STATE_PUBLISHED, monthStartCalendar, monthEndCalendar, getSession());
        this.dates = getDateList(calendar);
        
        this.infoglueRoles = AccessRightController.getController().getRoles();
        this.infoglueGroups = AccessRightController.getController().getGroups();
        
        /*
        this.infogluePrincipals = UserControllerProxy.getController().getAllUsers();
        this.infoglueRoles = RoleControllerProxy.getController().getAllRoles();
        this.infoglueGroups = GroupControllerProxy.getController().getAllGroups();
		*/
        
        this.eventTypes = EventTypeController.getController().getEventTypeList(EventType.EVENT_DEFINITION, getSession());
        this.languages = LanguageController.getController().getLanguageList(getSession());

        log.debug("startCalendar:" + startCalendar.getTime());
        
        return Action.SUCCESS;
    } 

    public String doGui() throws Exception 
    {
        this.execute();
        return "successGui";
    }

    public String doPublic() throws Exception 
    {
        this.execute();
        return "successPublic";
    }
    
    /**
     * Gets all dates that should be shown
     * @param hour
     * @return
     */
    
    private List getDateList(java.util.Calendar calendar)
    {
        //log.debug("DAY_OF_WEEK: " + calendar.get(java.util.Calendar.DAY_OF_WEEK));
        //log.debug("DAY_OF_WEEK_IN_MONTH: " + calendar.get(java.util.Calendar.DAY_OF_WEEK_IN_MONTH));
        //log.debug("DATE:" + calendar.getTime());
        calendar.set(java.util.Calendar.DAY_OF_WEEK, java.util.Calendar.MONDAY);
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 12);
        //log.debug("DAY_OF_WEEK: " + calendar.get(java.util.Calendar.DAY_OF_WEEK));
        //log.debug("DAY_OF_WEEK_IN_MONTH: " + calendar.get(java.util.Calendar.DAY_OF_WEEK_IN_MONTH));
        //log.debug("DATE:" + calendar.getTime());
           
        List dateList = new ArrayList();
        for(int i=0; i<7; i++)
        {
            //log.debug("DATE:" + calendar.getTime());
            dateList.add(calendar.getTime());
            calendar.add(java.util.Calendar.DAY_OF_YEAR, 1);
        }
        
        return dateList;
    }
    
    /**
     * 
     * @param hour
     * @return
     */

    public List getEvents(Date date, String hour)
    {
        List hourEvents = new ArrayList();
        
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.setTime(date);
        
        Iterator eventIterator = this.events.iterator();
        while(eventIterator.hasNext())
        {
            Event event = (Event)eventIterator.next();
            int startDayOfYear = event.getStartDateTime().get(java.util.Calendar.DAY_OF_YEAR);
            int endDayOfYear = event.getEndDateTime().get(java.util.Calendar.DAY_OF_YEAR);
            int eventDayOfYear = calendar.get(java.util.Calendar.DAY_OF_YEAR);
            
            int startHourOfDay = event.getStartDateTime().get(java.util.Calendar.HOUR_OF_DAY);
            int endHourOfDay = event.getEndDateTime().get(java.util.Calendar.HOUR_OF_DAY);
            int eventHourOfDay = calendar.get(java.util.Calendar.HOUR_OF_DAY);
            
            //log.debug("event:" + event.getName());
            boolean singleHourEvent = false;
            if(startDayOfYear == endDayOfYear && endDayOfYear == eventDayOfYear && (endHourOfDay - startHourOfDay < 2))
                singleHourEvent = true;
            
            if(startDayOfYear <= eventDayOfYear && endDayOfYear >= eventDayOfYear)
            {
                log.debug("hour:" + hour + "=" + singleHourEvent);
                if(singleHourEvent && startHourOfDay <= Integer.parseInt(hour) && endHourOfDay >= Integer.parseInt(hour))
                {
                    hourEvents.add(event);
                }
                else if(startHourOfDay <= Integer.parseInt(hour) && endHourOfDay > Integer.parseInt(hour))
                {
                    hourEvents.add(event);                    
                }

            }
        }
        
        /*
        if(hourEvents.size() > 0)
        {
            log.debug("getEvents with hour:" + date + ":" + hour);
            log.debug("returning:" + hourEvents.size());
        }
        */
        
        return hourEvents;
    }

    
    /**
     * 
     * @param hour
     * @return
     */

    public List getWeekEvents(Date date, String hour)
    {
        List hourEvents = new ArrayList();
        
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.setTime(date);
        
        //log.debug("this.weekEvents:" + this.weekEvents.size());
        
        Iterator eventIterator = this.weekEvents.iterator();
        while(eventIterator.hasNext())
        {
            Event event = (Event)eventIterator.next();
            //log.debug("event:" + event.getName());
            
            int startDayOfYear = event.getStartDateTime().get(java.util.Calendar.DAY_OF_YEAR);
            int endDayOfYear = event.getEndDateTime().get(java.util.Calendar.DAY_OF_YEAR);
            int eventDayOfYear = calendar.get(java.util.Calendar.DAY_OF_YEAR);
            
            int startHourOfDay = event.getStartDateTime().get(java.util.Calendar.HOUR_OF_DAY);
            int endHourOfDay = event.getEndDateTime().get(java.util.Calendar.HOUR_OF_DAY);
            int eventHourOfDay = calendar.get(java.util.Calendar.HOUR_OF_DAY);
            
            //log.debug("event:" + event.getName());
            boolean singleHourEvent = false;
            if(startDayOfYear == endDayOfYear && endDayOfYear == eventDayOfYear && (endHourOfDay - startHourOfDay < 2))
                singleHourEvent = true;
            
            if(startDayOfYear <= eventDayOfYear && endDayOfYear >= eventDayOfYear)
            {
                log.debug("hour:" + hour + "=" + singleHourEvent);
                if(singleHourEvent && startHourOfDay <= Integer.parseInt(hour) && endHourOfDay >= Integer.parseInt(hour))
                {
                    hourEvents.add(event);
                }
                else if(startHourOfDay <= Integer.parseInt(hour) && endHourOfDay > Integer.parseInt(hour))
                {
                    hourEvents.add(event);                    
                }

            }
            /*
            if(event.getStartDateTime().get(java.util.Calendar.DAY_OF_YEAR) == calendar.get(java.util.Calendar.DAY_OF_YEAR) && (event.getStartDateTime().get(java.util.Calendar.HOUR_OF_DAY) <= Integer.parseInt(hour) && event.getEndDateTime().get(java.util.Calendar.HOUR_OF_DAY) >= Integer.parseInt(hour)))
            {
                hourEvents.add(event);
            }
            */
        }
        
        return hourEvents;
    }
    
    /**
     * 
     * @param hour
     * @return
     */

    public List getMonthEvents(Date date)
    {
        List hourEvents = new ArrayList();
        
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.setTime(date);
        
        Iterator eventIterator = this.monthEvents.iterator();
        while(eventIterator.hasNext())
        {
            Event event = (Event)eventIterator.next();
            //log.debug("event:" + event.getName());
            if(event.getStartDateTime().get(java.util.Calendar.DAY_OF_YEAR) == calendar.get(java.util.Calendar.DAY_OF_YEAR))
            {
                hourEvents.add(event);
            }
        }
        
        return hourEvents;
    }

    
    public Long getCalendarId()
    {
        return calendarId;
    }
    
    public void setCalendarId(Long calendarId)
    {
        this.calendarId = calendarId;
    }

    public void setCalendarId(String calendarId)
    {
        this.calendarId = new Long(calendarId);
    }

    public void setCalendarId(String[] calendarId)
    {
        this.calendarId = new Long(calendarId[0]);
    }

    public Calendar getCalendar()
    {
        return calendar;
    }

    public String getMode()
    {
        return mode;
    }
    
    public void setMode(String mode)
    {
        this.mode = mode;
    }

    
    public String getFormattedStartDate()
    {
        Date parsedDate = this.parseDate(startDateTime, "yyyy-MM-dd");
        
        return this.formatDate(parsedDate, "yyyy/MM/dd");
    }

    public String getFormattedEndDate()
    {
        Date parsedDate = this.parseDate(endDateTime, "yyyy-MM-dd");
        
        return this.formatDate(parsedDate, "yyyy/MM/dd");
    }
    
    public String getFormattedDate(String date, String pattern)
    {
        Date parsedDate = this.parseDate(date, "yyyy-MM-dd");
        
        return this.formatDate(parsedDate, pattern);
    }

    public String getFormattedDate(Date date, String pattern)
    {
        return this.formatDate(date, pattern);
    }

    public String getEndDateTime()
    {
        return endDateTime;
    }
    
    public void setEndDateTime(String endDateTime)
    {
        this.endDateTime = endDateTime;
    }

    public Set getWaitingEvents()
    {
        return waitingEvents;
    }

    public Set getPublishedEvents()
    {
        return publishedEvents;
    }
    
    public Set getEvents()
    {
        return events;
    }
    
    public void setEvents(Set events)
    {
        this.events = events;
    }
    
    public String getStartDateTime()
    {
        return startDateTime;
    }
    
    public void setStartDateTime(String startDateTime)
    {
        this.startDateTime = startDateTime;
    }
    
    public List getDates()
    {
        return dates;
    }
        
    public java.util.Calendar getStartCalendar()
    {
        return startCalendar;
    }

    public java.util.Calendar getEndCalendar()
    {
        return endCalendar;
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

    public void setSelectedLanguages(String[] systemLanguageId)
    {
        this.systemLanguageId = systemLanguageId;
    }

    public List getInfoglueGroups()
    {
        return infoglueGroups;
    }
    
    public List getInfoglueRoles()
    {
        return infoglueRoles;
    }

	public List getLanguages() {
		return languages;
	}
}
