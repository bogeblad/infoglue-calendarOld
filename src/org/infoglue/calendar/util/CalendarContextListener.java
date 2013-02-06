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

package org.infoglue.calendar.util;

import javax.servlet.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoglue.calendar.actions.ViewApplicationStateAction;
import org.infoglue.common.util.InfoglueInstanceMonitor;
import org.infoglue.common.util.PropertyHelper;

import java.io.File;

/**
* This class functions as the entry-point for all initialization of the Cms-tool.
* The class responds to the startup or reload of a whole context.
*/

public final class CalendarContextListener implements ServletContextListener 
{
	private static Log log = LogFactory.getLog(ViewApplicationStateAction.class);

	/**
	 * This method is called when the servlet context is 
	 * initialized(when the Web Application is deployed). 
	 * You can initialize servlet context related data here.
    */
	 
   public void contextInitialized(ServletContextEvent event) 
   {
		System.out.println("contextInitialized for calendar...");
		try
		{
			String contextRootPath = event.getServletContext().getRealPath("/");
			if(!contextRootPath.endsWith("/") && !contextRootPath.endsWith("\\")) 
				contextRootPath = contextRootPath + "/";
							
			PropertyHelper.setProperty("contextRootPath", contextRootPath); 			

			InfoglueInstanceMonitor.getInstance();
			//CmsPropertyHandler.setApplicationName("application");

			//InfoGlueAuthenticationFilter.initializeProperties();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
   }

   /**
    * This method is invoked when the Servlet Context 
    * (the Web Application) is undeployed or 
    * WebLogic Server shuts down.
    */			    

   public void contextDestroyed(ServletContextEvent event) 
   {
   }
}

