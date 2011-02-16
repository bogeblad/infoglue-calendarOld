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

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2ddl.SchemaExport;

import org.infoglue.calendar.entities.Calendar;
import org.infoglue.calendar.entities.Category;
import org.infoglue.calendar.entities.Event;
import org.infoglue.calendar.entities.Location;
import org.infoglue.calendar.entities.Participant;
import org.infoglue.calendar.entities.Resource;

/**
 * This class basically exports the entity model into a database schema.
 * 
 * @author Mattias Bogeblad
 */

public class SchemaExportController
{
    public static void main(String[] args)
    {
        try
        {
            new SchemaExportController().exportTables();
        } 
        catch (HibernateException e)
        {
            e.printStackTrace();
        }
    }
    
    public void exportTables() throws HibernateException 
    {
        Configuration cfg = new Configuration()
            .addClass(Calendar.class)
            .addClass(Location.class)
            .addClass(Category.class)
            .addClass(Participant.class)
            .addClass(Resource.class)
            .addClass(Event.class);
        
        cfg.setProperty("hibernate.dialect", "net.sf.hibernate.dialect.MySQLDialect");
        SchemaExport schemaExport = new SchemaExport(cfg);
        schemaExport.setOutputFile("calendar.sql");
        schemaExport.create(true, true);
    }

}
