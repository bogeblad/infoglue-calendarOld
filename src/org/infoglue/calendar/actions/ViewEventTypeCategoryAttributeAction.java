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

import org.infoglue.calendar.controllers.CategoryController;
import org.infoglue.calendar.controllers.EventTypeCategoryAttributeController;
import org.infoglue.calendar.controllers.EventTypeController;
import org.infoglue.calendar.entities.EventType;
import org.infoglue.calendar.entities.EventTypeCategoryAttribute;

import com.opensymphony.xwork.Action;

/**
 * This action represents a EventTypeCategoryAttribute Administration screen.
 * 
 * @author Mattias Bogeblad
 */

public class ViewEventTypeCategoryAttributeAction extends CalendarAbstractAction
{
    private Long eventTypeCategoryAttributeId;
    private EventTypeCategoryAttribute eventTypeCategoryAttribute;

    private List categories;

    /**
     * This is the entry point for the main listing.
     */
    
    public String execute() throws Exception 
    {
        this.eventTypeCategoryAttribute = EventTypeCategoryAttributeController.getController().getEventTypeCategoryAttribute(eventTypeCategoryAttributeId, getSession());
        this.categories = CategoryController.getController().getRootCategoryList(getSession());

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

    public EventTypeCategoryAttribute getEventTypeCategoryAttribute()
    {
        return eventTypeCategoryAttribute;
    }

    public List getCategories()
    {
        return categories;
    }
}
