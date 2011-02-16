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

package org.infoglue.calendar.controllers;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoglue.calendar.entities.Category;
import org.infoglue.calendar.entities.Event;
import org.infoglue.calendar.entities.EventCategory;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

public class CategoryController extends BasicController
{    
    //Logger for this class
    private static Log log = LogFactory.getLog(CategoryController.class);
        
    
    /**
     * Factory method to get CategoryController
     * 
     * @return CategoryController
     */
    
    public static CategoryController getController()
    {
        return new CategoryController();
    }
        
    
    /**
     * This method is used to create a new Category object in the database.
     */
    /*
    public Category createCategory(String name, String description) throws HibernateException, Exception 
    {
        Category category = null;
        
        Session session = getSession();
        
		Transaction tx = null;
		try 
		{
			tx = session.beginTransaction();
			category = createCategory(name, description, session);
			tx.commit();
		}
		catch (Exception e) 
		{
		    if (tx!=null) 
		        tx.rollback();
		    throw e;
		}
		finally 
		{
		    session.close();
		}
		
        return category;
    }
    */

    
    /**
     * This method is used to create a new Category object in the database inside a transaction.
     */
    
    public Category createCategory(String internalName, String name, String description, Boolean active, Long parentCategoryId, Session session) throws HibernateException, Exception 
    {
        Category category = new Category();
        category.setInternalName(internalName);
        category.setName(name);
        category.setDescription(description);
        category.setActive(active);
        
        if(parentCategoryId != null)
        {
	        Category parentCategory = this.getCategory(parentCategoryId, session);
	        parentCategory.getChildren().add(category);
	        category.setParent(parentCategory);
        }
        
        session.save(category);
        
        return category;
    }
    
    
    /**
     * Updates an category.
     * 
     * @throws Exception
     */

    public Category updateCategory(Long id, String internalName, String name, String description, Boolean active, Session session) throws Exception 
    {
		Category category = getCategory(id, session);
		return updateCategory(category, internalName, name, description, active, session);
    }
    
    /**
     * Updates an category inside an transaction.
     * 
     * @throws Exception
     */
    
    public Category updateCategory(Category category, String internalName, String name, String description, Boolean active, Session session) throws Exception 
    {
        category.setInternalName(internalName);
        category.setName(name);
        category.setDescription(description);
        category.setActive(active);
        
		session.update(category);
		
		return category;
	}
    
 
    
    /**
     * This method returns a Category based on it's primary key inside a transaction
     * @return Category
     * @throws Exception
     */
    
    public Category getCategory(Long id, Session session) throws Exception
    {
        Category category = (Category)session.load(Category.class, id);
		
		return category;
    }
    
    
    
    /**
     * Gets a list of all categorys available sorted by primary key.
     * @return List of Category
     * @throws Exception
     */
    
    public List getRootCategoryList(Session session) throws Exception 
    {
        List result = null;
        
        Query q = session.createQuery("from Category category order by category.name where category.parent is null");
   
        result = q.list();
        
        return result;
    }
    

    /**
     * Gets a list of all categorys available sorted by primary key.
     * @return List of Category
     * @throws Exception
     */
    
    public Category getCategoryByPath(Session session, String path) throws Exception 
    {
    	Category category = null;
    	
    	if(path.startsWith("/"))
    		path = path.substring(1);
    	
    	String[] categories = path.split("/");
    	int position = 0;
    	
    	List rootCategories = getRootCategoryList(session);
        
    	Iterator rootCategoryIterator = rootCategories.iterator();
    	while(rootCategoryIterator.hasNext())
    	{
    		Category currentCategory = (Category)rootCategoryIterator.next();
    		
    		if(currentCategory.getInternalName() != null && currentCategory.getInternalName().equals(categories[position]))
    		{
    			category = currentCategory;
    		}
    	}

		position++;
		while(category != null && categories.length > position)
		{
			category = getMatchingChildCategory(categories[position], category);
			position++;
		}

        return category;
    }
    
    private Category getMatchingChildCategory(String name, Category parent)
    {
    	Category category = null;
    	
    	Iterator categoryIterator = parent.getChildren().iterator();
    	while(categoryIterator.hasNext())
    	{
    		Category currentCategory = (Category)categoryIterator.next();

    		if(currentCategory.getInternalName() != null && currentCategory.getInternalName().equalsIgnoreCase(name))
    		{
    			category = currentCategory;
    			break;
    		}
    	}
    	
        return category;
    }

    /**
     * Deletes a category object in the database. Also cascades all events associated to it.
     * @throws Exception
     */
    
    public void deleteCategory(Long id, Session session) throws Exception 
    {
        Category category = this.getCategory(id, session);
        Category parentCategory = category.getParent();
        
        deleteRecursiveCategories(category, null, session);
		
        if(parentCategory != null)
		{
            parentCategory.getChildren().remove(category);
		}
    }

    /**
     * Deletes a category object in the database. Also cascades all events associated to it.
     * @throws Exception
     */
    
    public void deleteRecursiveCategories(Category category, Iterator parentIterator, Session session) throws Exception 
    {
        Iterator childCategoryIterator = category.getChildren().iterator();
        while(childCategoryIterator.hasNext())
        {
            Category childCategory = (Category)childCategoryIterator.next();
            deleteRecursiveCategories(childCategory, childCategoryIterator, session);   			
   		}
		category.setChildren(new HashSet());
   		
		if(parentIterator != null) 
		    parentIterator.remove();
    	
		//Deletes all relations from events - for performance reasons the back-reference was removed.
		Set<EventCategory> eventCategoryList = EventController.getController().getEventCategoryList(category.getId(), session);
		for(EventCategory eventCategory : eventCategoryList)
		{
			eventCategory.getEvent().getEventCategories().remove(eventCategory);
			session.delete(eventCategory);
		}
		
    	session.delete(category);
    }


	public int clearBrokenCategoryReferences(Session session) throws Exception 
    {
		int numberOfFixes = 0;
		
        Query q = session.createQuery("from EventCategory eventCategory");
   
        List<EventCategory> result = (List<EventCategory>)q.list();
        log.debug("All eventCategories:" + result.size());
        for(EventCategory eventCategory : result)
        {
        	log.debug("eventCategory:" + eventCategory.getId());
        	try
        	{
        		Category category = eventCategory.getCategory();
        		log.debug("category:" + category);
        		if(category == null)
        		{
        			log.warn("Broken reference to category");
            		numberOfFixes++;
            		//eventCategory.getEvent().getEventCategories().remove(eventCategory);
            		//session.delete(eventCategory);
        		}
        	}
        	catch (Exception e) 
        	{
        		//e.printStackTrace();
        		log.error("Broken reference to category:" + e.getMessage());
        		numberOfFixes++;
        		eventCategory.getEvent().getEventCategories().remove(eventCategory);
        		session.delete(eventCategory);
			}
        }
        
        return numberOfFixes;
    }

}