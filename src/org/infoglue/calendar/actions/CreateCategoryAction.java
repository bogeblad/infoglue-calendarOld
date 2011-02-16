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

import org.infoglue.calendar.controllers.CategoryController;
import org.infoglue.calendar.databeans.AdministrationUCCBean;
import org.infoglue.calendar.entities.Category;
import org.infoglue.common.util.DBSessionWrapper;

import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.validator.ValidationException;

/**
 * This action represents a Calendar Administration screen.
 * 
 * @author Mattias Bogeblad
 */

public class CreateCategoryAction extends CalendarAbstractAction
{
    private String internalName;
    private String name;
    private String description;

    private Boolean active = new Boolean(false);
    private Long parentCategoryId;
    
    /**
     * This is the entry point for the main listing.
     */
    
    public String execute() throws Exception 
    {
        try
        {
            validateInput(this);
            CategoryController.getController().createCategory(this.internalName, this.name, description, active, parentCategoryId, getSession());
        }
        catch(ValidationException e)
        {
            return Action.ERROR;            
        }
        
        if(this.parentCategoryId == null)
            return "successList";
        else
            return Action.SUCCESS;
    } 

    /**
     * This is the entry point creating a new calendar.
     */
    
    public String input() throws Exception 
    {
        return Action.INPUT;
    } 
    
    public String getDescription()
    {
        return description;
    }
    
    public void setDescription(String description)
    {
        this.description = description;
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
        return this.name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }

    public Boolean getActive()
    {
        return active;
    }
    
    public void setActive(Boolean active)
    {
        this.active = active;
    }
    
    public Long getParentCategoryId()
    {
        return parentCategoryId;
    }
    
    public void setParentCategoryId(Long parentCategoryId)
    {
        this.parentCategoryId = parentCategoryId;
    }

    public Long getCategoryId()
    {
        return parentCategoryId;
    }

}
