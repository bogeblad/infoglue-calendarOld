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

package org.infoglue.common.util;

import java.util.Properties;
import java.io.*;
import java.util.Enumeration;


/**
* CMSPropertyHandler.java
* Created on 2002-sep-12 
* 
* This class is used to get properties for the system in a transparent way.
* 
* @author Stefan Sik, ss@frovi.com
* @author Mattias Bogeblad 
*/

public class PropertyHelper
{
	private static Properties cachedProperties = null;
	private static File propertyFile = null;
		
	public static void setPropertyFile(File aPropertyFile)
	{
		propertyFile = aPropertyFile;
	}
	
	/**
	 * This method initializes the parameter hash with values.
	 */

	private static void initializeProperties()
	{
		try
		{
			System.out.println("**************************************");
			System.out.println("Initializing properties from file.....");
			System.out.println("**************************************");
			
			cachedProperties = new Properties();
			if(propertyFile != null)
			    cachedProperties.load(new FileInputStream(propertyFile));
			else
			    cachedProperties.load(PropertyHelper.class.getResourceAsStream("/application.properties"));
			
		}	
		catch(Exception e)
		{
			cachedProperties = null;
			e.printStackTrace();
		}
		
	}

	/**
	 * This method returns all properties .
	 */

	public static Properties getProperties()
	{
		if(cachedProperties == null)
			initializeProperties();
				
		return cachedProperties;
	}	


	/**
	 * This method returns a propertyValue corresponding to the key supplied.
	 */

	public static String getProperty(String key)
	{
		String value;
		if(cachedProperties == null)
			initializeProperties();
		
		value = cachedProperties.getProperty(key);
		if (value != null)
			value = value.trim();
				
		return value;
	}	


	/**
	 * This method sets a property during runtime.
	 */

	public static void setProperty(String key, String value)
	{
		if(cachedProperties == null)
			initializeProperties();
		
		cachedProperties.setProperty(key, value);
	}	

	public static boolean getBooleanProperty( String key, boolean def ) {
		if( cachedProperties == null ) {
			initializeProperties();		
		}
		String value = cachedProperties.getProperty( key );
		Boolean bool = new Boolean( value );
		return bool.booleanValue();
	}
	
	public static boolean getBooleanProperty( String key  ) {
		return getBooleanProperty( key, false );
	}

	public static long getLongProperty( String key, long def ) {
		try {
			String value = cachedProperties.getProperty( key );
			Long propertyAsLong = new Long( value );
			return propertyAsLong.longValue();
		} catch( Exception e ) {}
		return def;
	}		

}
