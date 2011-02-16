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

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoglue.calendar.controllers.CategoryController;
import org.infoglue.calendar.controllers.LanguageController;
import org.infoglue.calendar.entities.Category;
import org.infoglue.calendar.entities.Language;
import org.infoglue.calendar.entities.Location;

import com.opensymphony.webwork.ServletActionContext;
import com.opensymphony.xwork.Action;

/**
 * This action represents a Category Administration screen.
 * 
 * @author Mattias Bogeblad
 */

public class ViewCategoryAction extends CalendarAbstractAction
{
    private static Log log = LogFactory.getLog(ViewCategoryAction.class);

    private Long categoryId;
    private Category category;
    private List categories;
    private Location location;
    private Long systemLanguageId;
    
    private List availableLanguages = new ArrayList();

    /**
     * This is the entry point for the main listing.
     */
    
    public String execute() throws Exception 
    {
        log.info("*********************************");
        log.info("*********************************");
        log.info("*********************************");
        Enumeration e = ServletActionContext.getRequest().getParameterNames();
        while(e.hasMoreElements())
        {
            String name = (String)e.nextElement();
            String value = ServletActionContext.getRequest().getParameter(name);
            log.info(name + "=" + value);
        }
        log.info("categoryId:" + categoryId);
        if(categoryId == null)
        {
            String categoryIdString = ServletActionContext.getRequest().getParameter("categoryId");
            if(categoryIdString != null && categoryIdString.length() > 0)
            	this.categoryId = new Long(categoryIdString);
        }
        
        this.availableLanguages = LanguageController.getController().getLanguageList(getSession());
        if(this.systemLanguageId == null && this.availableLanguages.size() > 0)
        {
        	this.systemLanguageId = ((Language)this.availableLanguages.get(0)).getId();
        }

        if(categoryId != null)
        {
            this.category = CategoryController.getController().getCategory(categoryId, getSession());
            log.info("category:" + this.category);
            return "successDetail";
        }
        else
        {
            this.categories = CategoryController.getController().getRootCategoryList(getSession());
            log.info("categories:" + this.category);
            return Action.SUCCESS;
        }
        
    } 

    public Long getCategoryId()
    {
        return categoryId;
    }
    
    public void setCategoryId(Long categoryId)
    {
        this.categoryId = categoryId;
    }

    public Category getCategory()
    {
        return category;
    }


    public List getCategories()
    {
        return categories;
    }
    
	public List getAvailableLanguages() 
	{
		return availableLanguages;
	}

	public Long getSystemLanguageId() 
	{
		return systemLanguageId;
	}

	public void setSystemLanguageId(Long systemLanguageId) 
	{
		this.systemLanguageId = systemLanguageId;
	}

	public Language getLanguage() throws Exception 
	{
		return LanguageController.getController().getLanguage(getSystemLanguageId(), getSession());
	}

}
