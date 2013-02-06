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
import org.infoglue.calendar.entities.Calendar;
import org.infoglue.calendar.entities.Language;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

public class LanguageController extends BasicController
{    
    //Logger for this class
    private static Log log = LogFactory.getLog(LanguageController.class);
        
    
    /**
     * Factory method to get LanguageController
     * 
     * @return LanguageController
     */
    
    public static LanguageController getController()
    {
        return new LanguageController();
    }
        
        
    /**
     * This method is used to create a new Language object in the database inside a transaction.
     */
    
    public Language createLanguage(String name, String isoCode, Session session) throws HibernateException, Exception 
    {
        Language language = new Language();
        language.setName(name);
        language.setIsoCode(isoCode);
        
        session.save(language);
        
        return language;
    }
    
    
    /**
     * Updates an language.
     * 
     * @throws Exception
     */
    
    public void updateLanguage(Long id, String name, String description, Session session) throws Exception 
    {
		Language language = getLanguage(id, session);
		updateLanguage(language, name, description, session);
    }
    
    /**
     * Updates an language inside an transaction.
     * 
     * @throws Exception
     */
    
    public void updateLanguage(Language language, String name, String isoCode, Session session) throws Exception 
    {
        language.setName(name);
        language.setIsoCode(isoCode);
	
		session.update(language);
	}
    
 
    
    /**
     * This method returns a Language based on it's primary key inside a transaction
     * @return Language
     * @throws Exception
     */
    
    public Language getLanguage(Long id, Session session) throws Exception
    {
        Language language = (Language)session.load(Language.class, id);
		
		return language;
    }
    
    
    
    /**
     * Gets a list of all languages available sorted by primary key.
     * @return List of Language
     * @throws Exception
     */
    
    public List getLanguageList(Session session) throws Exception 
    {
        List result = null;
        
        Query q = session.createQuery("from Language language order by language.id asc");
   
        result = q.list();
        
        return result;
    }
    
    /**
     * Gets a list of all languages available sorted by primary key.
     * @return List of Language
     * @throws Exception
     */
    
    public Language getMasterLanguage(Session session) throws Exception 
    {
        Language language = null;
        
        Query q = session.createQuery("from Language language order by language.id asc");
   
        List result = q.list();
        if(result.size() > 0)
        	language = (Language)result.get(0);
        
        return language;
    }

    /**
     * Gets a list of languages fetched by name.
     * @return List of Language
     * @throws Exception
     */
    
    public Language getLanguage(String name, Session session) throws Exception 
    {
        Language language = null;
        
        language = (Language)session.createQuery("from Language as language where language.name = ?").setString(0, name).uniqueResult();
        
        return language;
    }
    

    /**
     * Gets a list of languages fetched by name.
     * @return List of Language
     * @throws Exception
     */
    
    final static Map<String,Long> languageCodes = new HashMap<String,Long>();
    
    public Language getLanguageWithCode(String isoCode, Session session) throws Exception 
    {
        Language language = null;

        language = (Language)session.createQuery("from Language as language where language.isoCode = ?").setString(0, isoCode).uniqueResult();

        return language;
    }

    public Long getLanguageIdForCode(String isoCode, Session session) throws Exception 
    {
    	Long languageId = null;
        if(languageCodes.get(isoCode) != null)
        {
        	languageId = languageCodes.get(isoCode);
        }
        else
        {
        	Language language = getLanguageWithCode(isoCode, session);
        	languageCodes.put(isoCode, language.getId());
        	languageId = language.getId();
        }
        
        return languageId;
    }

    /**
     * Deletes a language object in the database. Also cascades all events associated to it.
     * @throws Exception
     */
    
    public void deleteLanguage(Long id, Session session) throws Exception 
    {
        Language language = this.getLanguage(id, session);
        
        session.delete(language);
    }
    
}