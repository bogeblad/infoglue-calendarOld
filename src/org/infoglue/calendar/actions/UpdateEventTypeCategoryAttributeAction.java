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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoglue.calendar.controllers.EventController;
import org.infoglue.calendar.controllers.EventTypeCategoryAttributeController;
import org.infoglue.calendar.controllers.EventTypeController;
import org.infoglue.calendar.entities.EventType;
import org.infoglue.calendar.entities.EventTypeCategoryAttribute;

import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.validator.ValidationException;

/**
 * This action represents a EventType Administration screen.
 * 
 * @author Mattias Bogeblad
 */

public class UpdateEventTypeCategoryAttributeAction extends CalendarAbstractAction
{
    private static Log log = LogFactory.getLog(UpdateEventTypeCategoryAttributeAction.class);

    private Long eventTypeCategoryAttributeId;
    private String internalName;
    private String name;
    private Long categoryId;
    
    private EventTypeCategoryAttribute eventTypeCategoryAttribute = null;
    
    /**
     * This is the entry point for the update procedure.
     */
    
    public String execute() throws Exception 
    {
        try
        {
            validateInput(this);
            this.eventTypeCategoryAttribute = EventTypeCategoryAttributeController.getController().updateEventTypeCategoryAttribute(this.eventTypeCategoryAttributeId, this.internalName, this.name, this.categoryId, getSession());
            log.info("eventTypeCategoryAttribute:" + eventTypeCategoryAttribute.getEventType().getId());
        }
        catch(ValidationException e)
        {
            return Action.ERROR;            
        }
        
        return Action.SUCCESS;
    } 
    
    

    public Long getEventTypeCategoryAttributeId()
    {
        return eventTypeCategoryAttributeId;
    }
    
    public void setEventTypeCategoryAttributeId(Long eventTypeCategoryAttributeId)
    {
        this.eventTypeCategoryAttributeId = eventTypeCategoryAttributeId;
    }
    
    public String getInternalName()
    {
        return internalName;
    }
    
    public void setInternalName(String internalName)
    {
        this.internalName = internalName;
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
        return eventTypeCategoryAttribute.getEventType().getId();
    }
    
    public Long getCategoryId()
    {
        return categoryId;
    }
    
    public void setCategoryId(Long categoryId)
    {
        this.categoryId = categoryId;
    }
    
}
