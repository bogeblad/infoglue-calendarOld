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

package org.infoglue.common.settings.controllers;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.hibernate.Session;
import org.infoglue.common.settings.entities.Property;
import org.infoglue.common.util.CacheController;
import org.infoglue.common.util.NullObject;
import org.infoglue.common.util.PropertyHelper;
import org.infoglue.common.util.ResourceBundleHelper;
import org.infoglue.common.util.dom.DOMBuilder;
import org.infoglue.common.util.io.FileHelper;

public class SettingsController
{
	private DOMBuilder domBuilder = new DOMBuilder();
	private SettingsPersister labelsPersister = null;
	
	private static final String SETTINGSPROPERTIESCACHENAME = "settingsPropertiesCache";
	/**
	 * A simple factory method
	 */
	
	public static SettingsController getController(SettingsPersister labelsPersister)
	{
		return new SettingsController(labelsPersister);
	}
	
	private SettingsController(SettingsPersister labelsPersister)
	{
		this.labelsPersister = labelsPersister;
	}

	/**
	 * This method returns a list (of strings) of all label-keys the system uses.
	 */
	
	public List getSystemSettings(String bundleName)
	{
		List labels = new ArrayList();
		
		Properties properties = PropertyHelper.getProperties();
		
		Enumeration enumeration = properties.keys();
		while(enumeration.hasMoreElements())
		{
			String key = (String)enumeration.nextElement();
			labels.add(key);
		}
		
		Collections.sort(labels);
		
		return labels;
	}

	/**
	 * This method returns a list (of locales) of all defined label-languages.
	 */
	
	public List getSettingsVariations(String nameSpace, Session session) throws Exception
	{
		List locales = new ArrayList();
		
		Document document = getPropertyDocument(nameSpace, session);
		if(document != null)
		{
			List languageNodes = document.selectNodes("/variations/variation");
			Iterator languageNodesIterator = languageNodes.iterator();
			while(languageNodesIterator.hasNext())
			{
				Node node = (Node)languageNodesIterator.next();
				Element element = (Element)node;
				String id = element.attributeValue("id");
				
				locales.add(id);
			}
		}
		
		return locales;
	}

	
	
	public Document getPropertyDocument(String nameSpace, Session session) throws Exception
	{
		String key = "propertyDocument_" + nameSpace;
		
		Object object = CacheController.getCachedObject(SETTINGSPROPERTIESCACHENAME, key);
		//log.debug("Cached object:" + object);
		if(object instanceof NullObject)
			return null;
		
		Document document = (Document)object;
		
		if(document == null)
		{
			Property property = labelsPersister.getProperty(nameSpace, "systemSettings", session);
			if(property != null)
			{
				String xml = property.getValue();
				if(xml != null && xml.length() > 0)
				{
					//log.debug("xml:" + xml);
					try
					{
						document = domBuilder.getDocument(xml);
						//String xml2 = domBuilder.getFormattedDocument(document, false, false, "UTF-8");
						//log.debug("xml2:" + xml2);
					}
					catch(Exception e)
					{
						document = domBuilder.createDocument();
						Element languagesElement = domBuilder.addElement(document, "variations");
						Element languageElement = domBuilder.addElement(languagesElement, "variation");
						domBuilder.addAttribute(languageElement, "id", "default"); 
						Element labelsElement = domBuilder.addElement(languageElement, "setting");						
					}
				}
			}
			else
			{
				//log.debug("Property was null...");
				document = domBuilder.createDocument();
				Element languagesElement = domBuilder.addElement(document, "variations");
				Element languageElement = domBuilder.addElement(languagesElement, "variation");
				domBuilder.addAttribute(languageElement, "id", "default"); 
				Element labelsElement = domBuilder.addElement(languageElement, "setting");
				String xml = domBuilder.getFormattedDocument(document, "UTF-8");
				//log.debug("xml:" + xml);
	
	            labelsPersister.createProperty(nameSpace, "systemSettings", xml, session);
			
	            //log.debug("Creating property:" + xml);
	        	
				document = domBuilder.getDocument(xml);
			}
		
			//log.debug("document before cache:" + document);
			if(document != null)
			{
				//log.debug("caching document:" + cacheName + ":" + key + ":" + document);
				CacheController.cacheObject(SETTINGSPROPERTIESCACHENAME, key, document);
			}
			else
			{
				CacheController.cacheObject(SETTINGSPROPERTIESCACHENAME, key, new NullObject());
			}
		}
		
		return document;
	}

	public void addVariation(String nameSpace, String id, Session session) throws Exception
	{
		Document document = getPropertyDocument(nameSpace, session);
		Element languagesElement = (Element)document.selectSingleNode("/variations");
		Element languageElement = domBuilder.addElement(languagesElement, "variation");
		domBuilder.addAttribute(languageElement, "id", id); 
		Element labelsElement = domBuilder.addElement(languageElement, "setting");
        String xml = domBuilder.getFormattedDocument(document, "UTF-8");
        //log.debug("xml:" + xml);

        labelsPersister.updateProperty(nameSpace, "systemSettings", xml, session);

        CacheController.clearCache(SETTINGSPROPERTIESCACHENAME);
	}

	public void updateSettings(String nameSpace, String id, Map properties, Session session) throws Exception
	{
		Document document = getPropertyDocument(nameSpace, session);
        //String xml1 = domBuilder.getFormattedDocument(document, "UTF-8");
        //log.debug("xml1:" + xml1);
        String xpath = "/variations/variation[@id='" + id +"']/setting";
        //String xpath = "/languages/language[@languageCode='" + languageCode +"']/labels";
        //log.debug("xpath:" + xpath);
        
		Element labelsElement = (Element)document.selectSingleNode(xpath);
		//log.debug("labelsElement:" + labelsElement);
		
		Iterator keyInterator = properties.keySet().iterator();
		while(keyInterator.hasNext())
		{
			String key = (String)keyInterator.next();
			String value = (String)properties.get(key);
			if(!Character.isLetter(key.charAt(0)))
				key = "NP" + key;
			
			if(key != null && value != null && labelsElement != null)
			{
				Element labelElement = labelsElement.element(key);
				if(labelElement == null)
					labelElement = domBuilder.addElement(labelsElement, key);

				labelElement.clearContent();
				
				List elements = labelElement.elements();
				Iterator elementsIterator = elements.iterator();
				while(elementsIterator.hasNext())
				{
					Element element = (Element)elementsIterator.next();
					//log.debug("Removing element:" + element.asXML());
					labelElement.remove(element);
				}
				
				domBuilder.addCDATAElement(labelElement, value);
			}
		}
		
        String xml = domBuilder.getFormattedDocument(document, "UTF-8");
        //log.debug("xml:" + xml);

        labelsPersister.updateProperty(nameSpace, "systemSettings", xml, session);

        CacheController.clearCache(SETTINGSPROPERTIESCACHENAME);
	}

	/**
	 * This method returns a label from the label system
	 * @param nameSpace
	 * @param key
	 * @param locale
	 * @param session
	 * @return
	 * @throws Exception
	 */
	public String getSetting(String nameSpace, String key, String id, Session session) throws Exception
	{
		String label = null;
		
		if(!Character.isLetter(key.charAt(0)))
			key = "NP" + key;

		Document document = getPropertyDocument(nameSpace, session);
		//log.debug("key:" + key);
		//log.debug("locale.getLanguage():" + locale.getLanguage());
        if(document != null)
        {
			String xpath = "/variations/variation[@id='" + id +"']/setting/" + key;
	        //log.debug("xpath:" + xpath);
	        
			Element labelElement = (Element)document.selectSingleNode(xpath);
			//log.debug("labelElement:" + labelElement);
			
			if(labelElement != null)
				label = labelElement.getText();
        }
        
		return label;
	}

}
