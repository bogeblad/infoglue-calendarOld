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

package org.infoglue.calendar.settings.actions;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoglue.calendar.actions.CalendarAbstractAction;
import org.infoglue.calendar.controllers.CalendarLabelsController;
import org.infoglue.calendar.controllers.CalendarSettingsController;
import org.infoglue.common.util.PropertyHelper;
import org.infoglue.common.util.io.FileHelper;

import com.opensymphony.webwork.ServletActionContext;
import com.opensymphony.xwork.ActionSupport;

public class UpdateDatabaseConfigurationAction extends CalendarAbstractAction
{
	private static Log log = LogFactory.getLog(UpdateDatabaseConfigurationAction.class);
	
	private final static String UPDATED = "updated";
	
	private String driverName;
	private String jdbcUrl;
	private String userName;
	private String password;
	private String sqlDialect;
	
    /**
     * This is the entry point for the main listing.
     */
    
    public String execute() throws Exception 
    {
    	/*
		String contextRootPath = PropertyHelper.getProperty("contextRootPath"); 			

    	String hibernateXML = FileHelper.getFileAsString(new File(contextRootPath + File.separator + "WEB-INF/classes/hibernate.cfg.xml"));
    	System.out.println("hibernateXML:" + hibernateXML);
    	
    	hibernateXML = hibernateXML.replaceFirst("<property name=\"hibernate.connection.driver\">com.mysql.jdbc.Driver</property>", "<property name=\"hibernate.connection.driver\">" + driverName + "</property>"); 
    	hibernateXML = hibernateXML.replaceFirst("<property name=\"hibernate.connection.url\">jdbc:mysql://localhost/igcalendar</property>", "<property name=\"hibernate.connection.url\">" + jdbcUrl + "</property>"); 
    	hibernateXML = hibernateXML.replaceFirst("<property name=\"hibernate.connection.username\">XXXXXX</property>", "<property name=\"hibernate.connection.username\">" + userName + "</property>"); 
    	hibernateXML = hibernateXML.replaceFirst("<property name=\"hibernate.connection.password\">XXXXXX</property>", "<property name=\"hibernate.connection.password\">" + password + "</property>"); 
    	hibernateXML = hibernateXML.replaceFirst("<property name=\"dialect\">org.hibernate.dialect.MySQLDialect</property>", "<property name=\"dialect\">" + sqlDialect + "</property>"); 

    	System.out.println("updated hibernateXML:" + hibernateXML);
		*/
    	return SUCCESS;
    }

	public String getDriverName() 
	{
		return driverName;
	}

	public void setDriverName(String driverName) 
	{
		this.driverName = driverName;
	}

	public String getJdbcUrl() 
	{
		return jdbcUrl;
	}

	public void setJdbcUrl(String jdbcUrl) 
	{
		this.jdbcUrl = jdbcUrl;
	}

	public String getUserName() 
	{
		return userName;
	}

	public void setUserName(String userName) 
	{
		this.userName = userName;
	}

	public String getPassword() 
	{
		return password;
	}

	public void setPassword(String password) 
	{
		this.password = password;
	}

	public String getSqlDialect() 
	{
		return sqlDialect;
	}

	public void setSqlDialect(String sqlDialect) 
	{
		this.sqlDialect = sqlDialect;
	}
    
}
