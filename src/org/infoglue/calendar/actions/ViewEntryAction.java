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

import javax.servlet.RequestDispatcher;

import org.hibernate.Session;
import org.infoglue.calendar.controllers.ContentTypeDefinitionController;
import org.infoglue.calendar.controllers.EntryController;
import org.infoglue.calendar.controllers.EventTypeController;
import org.infoglue.calendar.entities.Entry;
import org.infoglue.calendar.entities.Event;
import org.infoglue.calendar.entities.EventType;

import com.opensymphony.webwork.ServletActionContext;
import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionContext;

/**
 * This action represents a Location Administration screen.
 * 
 * @author Mattias Bogeblad
 */

public class ViewEntryAction extends CalendarAbstractAction
{
    private Long entryId;
    private Entry entry;
    
    private Long[] searchEventId;
    private String searchFirstName;
    private String searchLastName;
    private String searchEmail;
    private String searchHashCode;
    
    private List attributes;

    private String view = null;

    /**
     * This is the entry point for the main listing.
     */
    
    public String execute() throws Exception 
    {
    	Session session = getSession(true);
    	
        this.entry = EntryController.getController().getEntry(entryId, session);
        EventType eventType = EventTypeController.getController().getEventType(entry.getEvent().getEntryFormId(), session);
		this.attributes = ContentTypeDefinitionController.getController().getContentTypeAttributes(eventType.getSchemaValue());

		if(view != null && view.length() > 0)
		{			
		    return "successEntryReceiptGU";
		}
		else
		{
	        return Action.SUCCESS;			
		}
    } 

    public Entry getEntry()
    {
        return entry;
    }
    
    public void setEntry(Entry entry)
    {
        this.entry = entry;
    }
    
    public Long getEntryId()
    {
        return entryId;
    }
    
    public void setEntryId(Long entryId)
    {
        this.entryId = entryId;
    }
    
    public String getSearchEmail()
    {
        return searchEmail;
    }
    public void setSearchEmail(String searchEmail)
    {
        this.searchEmail = searchEmail;
    }
    public Long[] getSearchEventId()
    {
        return searchEventId;
    }
    public void setSearchEventId(Long[] searchEventId)
    {
        this.searchEventId = searchEventId;
    }
    public String getSearchFirstName()
    {
        return searchFirstName;
    }
    public void setSearchFirstName(String searchFirstName)
    {
        this.searchFirstName = searchFirstName;
    }
    public String getSearchLastName()
    {
        return searchLastName;
    }
    public void setSearchLastName(String searchLastName)
    {
        this.searchLastName = searchLastName;
    }

	public List getAttributes()
	{
		return attributes;
	}

	public void setView(String view) {
		this.view = view;
	}

	public String getSearchHashCode()
	{
		return searchHashCode;
	}

	public void setSearchHashCode(String searchHashCode)
	{
		this.searchHashCode = searchHashCode;
	}
}
