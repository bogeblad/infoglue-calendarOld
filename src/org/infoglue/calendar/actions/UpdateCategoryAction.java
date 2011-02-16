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

import javax.portlet.ActionResponse;
import javax.servlet.RequestDispatcher;

import org.infoglue.calendar.controllers.CategoryController;
import org.infoglue.calendar.entities.Category;
import org.infoglue.common.exceptions.ConstraintException;
import org.infoglue.common.util.ActionValidatorManager;

import com.opensymphony.webwork.ServletActionContext;
import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.util.OgnlValueStack;
//import com.opensymphony.xwork.validator.ActionValidatorManager;
import com.opensymphony.xwork.validator.ValidationException;

/**
 * This action represents updating a category.
 * 
 * @author Mattias Bogeblad
 */

public class UpdateCategoryAction extends ViewCategoryAction
{
    private Long updateCategoryId;
    private Long categoryId;
    private String internalName;
    private String name;
    private String description;
    private Boolean active = new Boolean(false);

    /**
     * This is the entry point for the main listing.
     */
    
    public String execute() throws Exception 
    {
    	Category category = null;
    	
        try
        {
            validateInput(this); 
            //System.out.println("this.active:" + this.active);
            category = CategoryController.getController().updateCategory(this.updateCategoryId, this.internalName, this.name, this.description, this.active, getSession());
        }
        catch(ValidationException e)
        {
        	this.categoryId = this.updateCategoryId;
        	return Action.ERROR;            
        }
            
        if(category != null && category.getParent() != null)
        {
            this.categoryId = category.getParent().getId();
            return Action.SUCCESS;
        }
        else
        {
            return "successRootCategory";
        }
    } 
    
    
    public Long getCategoryId()
    {
        return categoryId;
    }
    
    public String getDescription()
    {
        return description;
    }
    
    public void setDescription(String description)
    {
        this.description = description;
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public Long getUpdateCategoryId()
    {
        return updateCategoryId;
    }
    
    public void setUpdateCategoryId(Long updateCategoryId)
    {
        this.updateCategoryId = updateCategoryId;
    }
    
    public String getInternalName()
    {
        return internalName;
    }
    
    public void setInternalName(String internalName)
    {
        this.internalName = internalName;
    }
    
    public Boolean getActive()
    {
        return active;
    }
    
    public void setActive(Boolean active)
    {
        this.active = active;
    }
}
