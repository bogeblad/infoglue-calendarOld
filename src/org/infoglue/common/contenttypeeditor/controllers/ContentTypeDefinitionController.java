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

package org.infoglue.common.contenttypeeditor.controllers;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.xml.transform.TransformerException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xerces.parsers.DOMParser;
import org.apache.xpath.XPathAPI;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.infoglue.common.contenttypeeditor.entities.AssetKeyDefinition;
import org.infoglue.common.contenttypeeditor.entities.ContentTypeAttribute;
import org.infoglue.common.contenttypeeditor.entities.ContentTypeAttributeParameter;
import org.infoglue.common.contenttypeeditor.entities.ContentTypeAttributeParameterValue;
import org.infoglue.common.contenttypeeditor.entities.ContentTypeAttributeValidator;
import org.infoglue.common.contenttypeeditor.entities.ContentTypeDefinition;
import org.infoglue.common.util.VisualFormatter;
import org.infoglue.common.util.XMLHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * @author Mattias Bogeblad
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class ContentTypeDefinitionController
{
	
    //Logger for this class
    private static Log log = LogFactory.getLog(ContentTypeDefinitionController.class);
        
    
    /**
     * Factory method to get SubscriberController
     * 
     * @return ContentTypeDefinitionController
     */
    
    public static ContentTypeDefinitionController getController()
    {
        return new ContentTypeDefinitionController();
    }
            
    
    /**
     * Updates a ContentTypeDefinition.
     * 
     * @throws Exception
     */
    
    public void updateContentTypeDefinition(Long id, String name, String schemaValue, Integer type, Session session) throws Exception 
    {
    	ContentTypeDefinition contentTypeDefintion = getContentTypeDefinition(id, session);
		updateContentTypeDefinition(contentTypeDefintion, name, schemaValue, type, session);
    }
    
    /**
     * Updates an ContentTypeDefinition inside an transaction.
     * 
     * @throws Exception
     */
    
    public void updateContentTypeDefinition(ContentTypeDefinition contentTypeDefinition, String name, String schemaValue, Integer type, Session session) throws Exception 
    {
    	contentTypeDefinition.setName(name);
    	contentTypeDefinition.setSchemaValue(schemaValue);
    	contentTypeDefinition.setType(type);
	
		session.update(contentTypeDefinition);
	}
    
 
    /**
     * This method returns a ContentTypeDefinition based on it's primary key inside a transaction
     * @return ContentTypeDefinition
     * @throws Exception
     */
    
    public ContentTypeDefinition getContentTypeDefinition(Long id, Session session) throws Exception
    {
    	ContentTypeDefinition contentTypeDefinition = (ContentTypeDefinition)session.load(ContentTypeDefinition.class, id);
		
		return contentTypeDefinition;
    }
    
    
    
    /**
     * Gets a list of all contentTypeDefinitions available sorted by primary key.
     * @return List of ContentTypeDefinition
     * @throws Exception
     */
    
    public List getContentTypeDefinitionList(Session session) throws Exception 
    {
        List result = null;
        
        Query q = session.createQuery("from ContentTypeDefinition contentTypeDefinition order by contentTypeDefinition.id");
   
        result = q.list();
        
        return result;
    }

    /**
     * Gets a list of all ContentTypeDefinitions available whith a certain name.
     * @return List of ContentTypeDefinition
     * @throws Exception
     */
    /*
    public Set getContentTypeDefinitionList(String email, Session session) throws Exception 
    {
        List subscribers = null;
        
        subscribers = session.createQuery("from Subscriber subscriber where subscriber.email = ?").setString(0, email).list();
   
        Set set = new LinkedHashSet();
        set.addAll(subscribers);

        return set;
    }  
    */
    
    /**
     * Deletes a ContentTypeDefinition object in the database.
     * @throws Exception
     */
    
    public void deleteContentTypeDefinition(Long id, Session session) throws Exception 
    {
    	ContentTypeDefinition contentTypeDefinition = this.getContentTypeDefinition(id, session);
        session.delete(contentTypeDefinition);
    }
	
	
	
	
	
	
	
	
	
	public static final String ASSET_KEYS = "assetKeys";
	public static final String CATEGORY_KEYS = "categoryKeys";

	private static final NodeList EMPTY_NODELIST = new NodeList()
	{
		public int getLength()	{ return 0; }
		public Node item(int i)	{ return null; }
	};

/*
	public AssetKeyDefinition getDefinedAssetKey(String contentTypeDefinitionString, String assetKey)
	{
	    AssetKeyDefinition assetKeyDefinition = null;
	    
	    List definedAssetKeys = getDefinedAssetKeys(contentTypeDefinitionString);
	    Iterator i = definedAssetKeys.iterator();
	    while(i.hasNext())
	    {
	        AssetKeyDefinition currentAssetKeyDefinition = (AssetKeyDefinition)i.next();
	        if(currentAssetKeyDefinition.getAssetKey().equals(assetKey))
	        {
	            assetKeyDefinition = currentAssetKeyDefinition;
	        	break;
	        }
	    }
	    
	    return assetKeyDefinition;
	}

	public List getDefinedAssetKeys(String contentTypeDefinitionString)
	{
		NodeList nodes = getEnumerationNodeList(contentTypeDefinitionString, ASSET_KEYS);

		return getEnumValues(nodes);
	}


	public List getDefinedCategoryKeys(String contentTypeDefinitionString)
	{
		NodeList nodes = getEnumerationNodeList(contentTypeDefinitionString, CATEGORY_KEYS);
		return getCategoryInfo(nodes);
	}
*/
	
	protected List getEnumValues(NodeList nodes)
	{
	   List keys = new ArrayList();
		for(int i = 0; i < nodes.getLength(); i++)
		{
		    Node ichild = nodes.item(i);
		    
		    log.info("ichild:" + ichild.getNodeName() + ":" + ichild.getNodeValue());
			
			try
			{
			    Node assetKeyValue = ichild.getAttributes().getNamedItem("value");

			    Element params = (Element)XPathAPI.selectSingleNode(ichild, "xs:annotation/xs:appinfo/params");

			    String descriptionValue = "";
			    String maximumSizeValue = "1000000";
			    String allowedContentTypesValue = "*";
			    String imageWidthValue = "*";
			    String imageHeightValue = "*";
		    
			    if(params != null)
			    {
				    descriptionValue = getElementValue(params, "description");
				    maximumSizeValue = getElementValue(params, "maximumSize");
				    allowedContentTypesValue = getElementValue(params, "allowedContentTypes");
				    imageWidthValue = getElementValue(params, "imageWidth");
				    imageHeightValue = getElementValue(params, "imageHeight");
			    }
			    
				AssetKeyDefinition assetKeyDefinition = new AssetKeyDefinition(); 
				
				assetKeyDefinition.setAssetKey(assetKeyValue.getNodeValue());
				assetKeyDefinition.setDescription(descriptionValue);
				assetKeyDefinition.setMaximumSize(new Integer(maximumSizeValue));
				assetKeyDefinition.setAllowedContentTypes(allowedContentTypesValue);
				assetKeyDefinition.setImageWidth(imageWidthValue);
				assetKeyDefinition.setImageHeight(imageHeightValue);
				
				log.info("Adding assetKeyDefinition " + assetKeyDefinition.getAssetKey());
				keys.add(assetKeyDefinition);
			}
			catch(Exception e)
			{
			    e.printStackTrace();
			}
		}
		
		log.info("keys:" + keys.size());
		
		return keys;
	}

	/**
	 * Returns a List of CategoryInfos for the category atributes of the NodeList
	 */
	/*
	protected List getCategoryInfo(NodeList nodes)
	{
		String attributesXPath = "xs:annotation/xs:appinfo/params";

		List keys = new ArrayList();
		for(int i = 0; i < nodes.getLength(); i++)
		{
			Node enumeration = nodes.item(i);
			String value = enumeration.getAttributes().getNamedItem("value").getNodeValue();
			try
			{
				CategoryAttribute category = new CategoryAttribute(value);
				keys.add(category);

				Element params = (Element)XPathAPI.selectSingleNode(enumeration, attributesXPath);
				if(params != null)
				{
					category.setTitle(getElementValue(params, "title"));
					category.setDescription(getElementValue(params, "description"));
					category.setCategoryId(getElementValue(params, "categoryId"));
				}
			}
			catch (TransformerException e)
			{
				keys.add(new CategoryAttribute(value));
			}
		}
		return keys;
	}
	*/
	
	/**
	 * Returns a list of xs:enumeration nodes base on the provided key.
	 * @param keyType The key to find enumerations for
	 */
	protected NodeList getEnumerationNodeList(String contentTypeDefinitionString, String keyType)
	{
        try
        {
        	if(contentTypeDefinitionString != null)
        	{
		        InputSource xmlSource = new InputSource(new StringReader(contentTypeDefinitionString));

				DOMParser parser = new DOMParser();
				parser.parse(xmlSource);
				Document document = parser.getDocument();

				String attributesXPath = "/xs:schema/xs:simpleType[@name = '" + keyType + "']/xs:restriction/xs:enumeration";
				return XPathAPI.selectNodeList(document.getDocumentElement(), attributesXPath);
        	}
        }
        catch(Exception e)
        {
        	log.warn("An error occurred when trying to fetch the asset keys:" + e.getMessage(), e);
        }

		return EMPTY_NODELIST;
	}

	/**
	 * Get the CDATA value from the provided elements child tag
	 * @param root The root element to find the child tag
	 * @param tagName The tag name of the child to get the CDATA value
	 * @return The String CDATA or null if the tag does not exist or no value is set.
	 */
	protected String getElementValue(Element root, String tagName)
	{
		NodeList nodes = root.getElementsByTagName(tagName);
		if(nodes.getLength() > 0)
		{
			Node cdata = nodes.item(0).getFirstChild();
			return (cdata != null)? cdata.getNodeValue() : null;
		}

		return null;
	}

	/**
	 * This method returns the attributes in the content type definition for generation.
	 */

	public List getContentTypeAttributes(String schemaValue)
	{
		List attributes = new ArrayList();

		try
		{
			InputSource xmlSource = new InputSource(new StringReader(schemaValue));

			DOMParser parser = new DOMParser();
			parser.parse(xmlSource);
			Document document = parser.getDocument();

			String attributesXPath = "/xs:schema/xs:complexType/xs:all/xs:element/xs:complexType/xs:all/xs:element";
			NodeList anl = org.apache.xpath.XPathAPI.selectNodeList(document.getDocumentElement(), attributesXPath);
			for(int i=0; i < anl.getLength(); i++)
			{
				Element child = (Element)anl.item(i);
				String attributeName = child.getAttribute("name");
				String attributeType = child.getAttribute("type");

				ContentTypeAttribute contentTypeAttribute = new ContentTypeAttribute();
				contentTypeAttribute.setPosition(i);
				contentTypeAttribute.setName(attributeName);
				contentTypeAttribute.setInputType(attributeType);

				String validatorsXPath = "/xs:schema/xs:complexType[@name = 'Validation']/xs:annotation/xs:appinfo/form-validation/formset/form/field[@property = '"+ attributeName +"']";

				// Get validators
				NodeList validatorNodeList = org.apache.xpath.XPathAPI.selectNodeList(document.getDocumentElement(), validatorsXPath);
				for(int j=0; j < validatorNodeList.getLength(); j++)
				{
					Element validatorNode = (Element)validatorNodeList.item(j);
					if (validatorNode != null)
					{
					    Map arguments = new HashMap();
					    
					    NodeList varNodeList = validatorNode.getElementsByTagName("var");
					    for(int k=0; k < varNodeList.getLength(); k++)
						{
							Element varNode = (Element)varNodeList.item(k);
							
							String varName = getElementValue(varNode, "var-name");
							String varValue = getElementValue(varNode, "var-value");

							arguments.put(varName, varValue);
						}	    
					    
					    String attribute = ((Element)validatorNode).getAttribute("depends");
					    String[] depends = attribute.split(",");
					    for(int dependsIndex=0; dependsIndex < depends.length; dependsIndex++)
					    {
					        String name = depends[dependsIndex];

					        ContentTypeAttributeValidator contentTypeAttributeValidator = new ContentTypeAttributeValidator();
					        contentTypeAttributeValidator.setName(name);
					        contentTypeAttributeValidator.setArguments(arguments);
					        contentTypeAttribute.getValidators().add(contentTypeAttributeValidator);					        
					    }
					    
					    
					}
				}
				
				// Get extra parameters
				Node paramsNode = org.apache.xpath.XPathAPI.selectSingleNode(child, "xs:annotation/xs:appinfo/params");
				if (paramsNode != null)
				{
					NodeList childnl = ((Element)paramsNode).getElementsByTagName("param");
					for(int ci=0; ci < childnl.getLength(); ci++)
					{
						Element param = (Element)childnl.item(ci);
						String paramId = param.getAttribute("id");
						String paramInputTypeId = param.getAttribute("inputTypeId");

						ContentTypeAttributeParameter contentTypeAttributeParameter = new ContentTypeAttributeParameter();
						contentTypeAttributeParameter.setId(paramId);
						if(paramInputTypeId != null && paramInputTypeId.length() > 0)
							contentTypeAttributeParameter.setType(Integer.parseInt(paramInputTypeId));

						contentTypeAttribute.putContentTypeAttributeParameter(paramId, contentTypeAttributeParameter);

						NodeList valuesNodeList = param.getElementsByTagName("values");
						for(int vsnli=0; vsnli < valuesNodeList.getLength(); vsnli++)
						{
							NodeList valueNodeList = param.getElementsByTagName("value");
							for(int vnli=0; vnli < valueNodeList.getLength(); vnli++)
							{
								Element value = (Element)valueNodeList.item(vnli);
								String valueId = value.getAttribute("id");
								
								ContentTypeAttributeParameterValue contentTypeAttributeParameterValue = new ContentTypeAttributeParameterValue();
								contentTypeAttributeParameterValue.setId(valueId);

								NamedNodeMap nodeMap = value.getAttributes();
								for(int nmi =0; nmi < nodeMap.getLength(); nmi++)
								{
									Node attribute = (Node)nodeMap.item(nmi);
									String valueAttributeName = attribute.getNodeName();
									String valueAttributeValue = attribute.getNodeValue();
									
									contentTypeAttributeParameterValue.addAttribute(valueAttributeName, valueAttributeValue);
								}

								contentTypeAttributeParameter.addContentTypeAttributeParameterValue(valueId, contentTypeAttributeParameterValue);
							}
						}
					}
				}
				// End extra parameters

				attributes.add(contentTypeAttribute);
			}

		}
		catch(Exception e)
		{
			log.error("An error occurred when we tried to get the attributes of the content type: " + e.getMessage(), e);
		}

		return attributes;
	}

	/**
	 * This method adds a new content type attribute to the contentTypeDefinition. It sets some default values.
	 */

	public String insertContentTypeAttribute(String schemaValue, String inputTypeId, List activatedName)
	{
		String newSchemaValue = schemaValue;

		try
		{
			InputSource xmlSource = new InputSource(new StringReader(schemaValue));

			DOMParser parser = new DOMParser();
			parser.parse(xmlSource);
			Document document = parser.getDocument();

			//Build the entire structure for a contenttype...

			String attributesXPath = "/xs:schema/xs:complexType/xs:all/xs:element/xs:complexType/xs:all";
			NodeList anl = org.apache.xpath.XPathAPI.selectNodeList(document.getDocumentElement(), attributesXPath);
			for(int i=0; i < anl.getLength(); i++)
			{
				Node child = anl.item(i);
				Element childElement = (Element)child;
				Element newAttribute = document.createElement("xs:element");
				String name = "newAttributeName" + (int)(Math.random() * 100);
				activatedName.add(name);
				newAttribute.setAttribute("name", name);
				newAttribute.setAttribute("type", inputTypeId);
				childElement.appendChild(newAttribute);

				Element annotation = document.createElement("xs:annotation");
				Element appInfo    = document.createElement("xs:appinfo");
				Element params     = document.createElement("params");

				addParameterElement(params, "title", "0");
				addParameterElement(params, "description", "0");
				addParameterElement(params, "initialData", "");
				addParameterElement(params, "class", "0");

				newAttribute.appendChild(annotation);
				annotation.appendChild(appInfo);
				appInfo.appendChild(params);

				if(inputTypeId.equalsIgnoreCase("checkbox") || inputTypeId.equalsIgnoreCase("select") || inputTypeId.equalsIgnoreCase("radiobutton"))
				{
					addParameterElement(params, "values", "1");
				}

				if(inputTypeId.equalsIgnoreCase("textarea"))
				{
					addParameterElement(params, "width", "0", "700");
					addParameterElement(params, "height", "0", "150");
					addParameterElement(params, "enableWYSIWYG", "0", "false");
					addParameterElement(params, "enableTemplateEditor", "0", "false");
					addParameterElement(params, "enableFormEditor", "0", "false");
					addParameterElement(params, "enableContentRelationEditor", "0", "false");
					addParameterElement(params, "enableStructureRelationEditor", "0", "false");
					addParameterElement(params, "enableComponentPropertiesEditor", "0", "false");
					addParameterElement(params, "activateExtendedEditorOnLoad", "0", "false");
				}
			}

			StringBuffer sb = new StringBuffer();
			XMLHelper.serializeDom(document.getDocumentElement(), sb);
			newSchemaValue = sb.toString();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return newSchemaValue;
	}


	/**
	 * This method creates a parameter for the given input type.
	 * This is to support form steering information later.
	 */

	private void addParameterElement(Element parent, String id, String inputTypeId)
	{
		Element parameter = parent.getOwnerDocument().createElement("param");
		parameter.setAttribute("id", id);
		parameter.setAttribute("inputTypeId", inputTypeId);  //Multiple values
		Element parameterValuesValues = parent.getOwnerDocument().createElement("values");
		Element parameterValuesValue  = parent.getOwnerDocument().createElement("value");
		parameterValuesValue.setAttribute("id", "undefined" + (int)(Math.random() * 100));
		parameterValuesValue.setAttribute("label", "undefined" + (int)(Math.random() * 100));
		parameterValuesValues.appendChild(parameterValuesValue);
		parameter.appendChild(parameterValuesValues);
		parent.appendChild(parameter);
	}


	/**
	 * This method creates a parameter for the given input type and the default value.
	 * This is to support form steering information later.
	 */

	private void addParameterElement(Element parent, String id, String inputTypeId, String defaultValue)
	{
		Element parameter = parent.getOwnerDocument().createElement("param");
		parameter.setAttribute("id", id);
		parameter.setAttribute("inputTypeId", inputTypeId);  //Multiple values
		Element parameterValuesValues = parent.getOwnerDocument().createElement("values");
		Element parameterValuesValue  = parent.getOwnerDocument().createElement("value");
		parameterValuesValue.setAttribute("id", id);
		parameterValuesValue.setAttribute("label", defaultValue);
		parameterValuesValues.appendChild(parameterValuesValue);
		parameter.appendChild(parameterValuesValues);
		parent.appendChild(parameter);
	}


	/**
	 * This method creates a parameter for the given input type and the default value.
	 * This is to support form steering information later.
	 */

	private void addParameterElementIfNotExists(Element parent, String id, String inputTypeId, String defaultValue) throws Exception
	{
		String parameterXPath = "param[@id='" + id + "']";
		NodeList anl = org.apache.xpath.XPathAPI.selectNodeList(parent, parameterXPath);
		if(anl.getLength() == 0)
		{
			Element parameter = parent.getOwnerDocument().createElement("param");
			parameter.setAttribute("id", id);
			parameter.setAttribute("inputTypeId", inputTypeId);  //Multiple values
			Element parameterValuesValues = parent.getOwnerDocument().createElement("values");
			Element parameterValuesValue  = parent.getOwnerDocument().createElement("value");
			parameterValuesValue.setAttribute("id", id);
			parameterValuesValue.setAttribute("label", defaultValue);
			parameterValuesValues.appendChild(parameterValuesValue);
			parameter.appendChild(parameterValuesValues);
			parent.appendChild(parameter);

		}
	}

	/**
	 * This method validates the current content type and updates it to be valid in the future.
	 */
	/*
	public ContentTypeDefinition validateAndUpdateContentType(ContentTypeDefinition contentTypeDefinition)
	{
		try
		{
			boolean isModified = false;

			InputSource xmlSource = new InputSource(new StringReader(contentTypeDefinition.getSchemaValue()));
			DOMParser parser = new DOMParser();
			parser.parse(xmlSource);
			Document document = parser.getDocument();

			//Set the new versionId
			String rootXPath = "/xs:schema";
			NodeList schemaList = org.apache.xpath.XPathAPI.selectNodeList(document.getDocumentElement(), rootXPath);
			for(int i=0; i < schemaList.getLength(); i++)
			{
				Element schemaElement = (Element)schemaList.item(i);
				if(schemaElement.getAttribute("version") == null || schemaElement.getAttribute("version").equalsIgnoreCase(""))
				{
					isModified = true;
					schemaElement.setAttribute("version", "2.0");

					//First check out if the old/wrong definitions are there and delete them
					String definitionsXPath = "/xs:schema/xs:simpleType";
					NodeList definitionList = org.apache.xpath.XPathAPI.selectNodeList(document.getDocumentElement(), definitionsXPath);
					for(int j=0; j < definitionList.getLength(); j++)
					{
						Element childElement = (Element)definitionList.item(j);
						if(!childElement.getAttribute("name").equalsIgnoreCase("assetKeys"))
							childElement.getParentNode().removeChild(childElement);
					}

					//Now we create the new definitions
					Element textFieldDefinition = document.createElement("xs:simpleType");
					textFieldDefinition.setAttribute("name", "textfield");
					Element restriction = document.createElement("xs:restriction");
					restriction.setAttribute("base", "xs:string");
					Element maxLength = document.createElement("xs:maxLength");
					maxLength.setAttribute("value", "100");
					restriction.appendChild(maxLength);
					textFieldDefinition.appendChild(restriction);
					schemaElement.insertBefore(textFieldDefinition, schemaElement.getFirstChild());

					Element selectDefinition = document.createElement("xs:simpleType");
					selectDefinition.setAttribute("name", "select");
					restriction = document.createElement("xs:restriction");
					restriction.setAttribute("base", "xs:string");
					maxLength = document.createElement("xs:maxLength");
					maxLength.setAttribute("value", "100");
					restriction.appendChild(maxLength);
					selectDefinition.appendChild(restriction);
					schemaElement.insertBefore(selectDefinition, schemaElement.getFirstChild());

					Element checkboxDefinition = document.createElement("xs:simpleType");
					checkboxDefinition.setAttribute("name", "checkbox");
					restriction = document.createElement("xs:restriction");
					restriction.setAttribute("base", "xs:string");
					maxLength = document.createElement("xs:maxLength");
					maxLength.setAttribute("value", "100");
					restriction.appendChild(maxLength);
					checkboxDefinition.appendChild(restriction);
					schemaElement.insertBefore(checkboxDefinition, schemaElement.getFirstChild());

					Element radiobuttonDefinition = document.createElement("xs:simpleType");
					radiobuttonDefinition.setAttribute("name", "radiobutton");
					restriction = document.createElement("xs:restriction");
					restriction.setAttribute("base", "xs:string");
					maxLength = document.createElement("xs:maxLength");
					maxLength.setAttribute("value", "100");
					restriction.appendChild(maxLength);
					radiobuttonDefinition.appendChild(restriction);
					schemaElement.insertBefore(radiobuttonDefinition, schemaElement.getFirstChild());

					Element textareaDefinition = document.createElement("xs:simpleType");
					textareaDefinition.setAttribute("name", "textarea");
					restriction = document.createElement("xs:restriction");
					restriction.setAttribute("base", "xs:string");
					maxLength = document.createElement("xs:maxLength");
					maxLength.setAttribute("value", "100");
					restriction.appendChild(maxLength);
					textareaDefinition.appendChild(restriction);
					schemaElement.insertBefore(textareaDefinition, schemaElement.getFirstChild());


					//Now we deal with the individual attributes and parameters
					String attributesXPath = "/xs:schema/xs:complexType/xs:all/xs:element/xs:complexType/xs:all/xs:element";
					NodeList anl = org.apache.xpath.XPathAPI.selectNodeList(document.getDocumentElement(), attributesXPath);
					for(int k=0; k < anl.getLength(); k++)
					{
						Element childElement = (Element)anl.item(k);

						if(childElement.getAttribute("type").equals("shortString"))
						{
							childElement.setAttribute("type", "textfield");
							isModified = true;
						}
						else if(childElement.getAttribute("type").equals("shortText"))
						{
							childElement.setAttribute("type", "textarea");
							isModified = true;
						}
						else if(childElement.getAttribute("type").equals("fullText"))
						{
							childElement.setAttribute("type", "textarea");
							isModified = true;
						}
						else if(childElement.getAttribute("type").equals("hugeText"))
						{
							childElement.setAttribute("type", "textarea");
							isModified = true;
						}

						String inputTypeId = childElement.getAttribute("type");

						NodeList annotationNodeList = childElement.getElementsByTagName("xs:annotation");
						if(annotationNodeList != null && annotationNodeList.getLength() > 0)
						{
							Element annotationElement = (Element)annotationNodeList.item(0);
							NodeList appinfoNodeList = childElement.getElementsByTagName("xs:appinfo");
							if(appinfoNodeList != null && appinfoNodeList.getLength() > 0)
							{
								Element appinfoElement = (Element)appinfoNodeList.item(0);
								NodeList paramsNodeList = childElement.getElementsByTagName("params");
								if(paramsNodeList != null && paramsNodeList.getLength() > 0)
								{
									Element paramsElement = (Element)paramsNodeList.item(0);
									addParameterElement(paramsElement, "title", "0");
									addParameterElement(paramsElement, "description", "0");
									addParameterElement(paramsElement, "class", "0");

									if(inputTypeId.equalsIgnoreCase("checkbox") || inputTypeId.equalsIgnoreCase("select") || inputTypeId.equalsIgnoreCase("radiobutton"))
									{
										addParameterElement(paramsElement, "values", "1");
									}

									if(inputTypeId.equalsIgnoreCase("textarea"))
									{
										addParameterElement(paramsElement, "width", "0", "700");
										addParameterElement(paramsElement, "height", "0", "150");
										addParameterElement(paramsElement, "enableWYSIWYG", "0", "false");
										addParameterElement(paramsElement, "enableTemplateEditor", "0", "false");
										addParameterElement(paramsElement, "enableFormEditor", "0", "false");
										addParameterElement(paramsElement, "enableContentRelationEditor", "0", "false");
										addParameterElement(paramsElement, "enableStructureRelationEditor", "0", "false");
										addParameterElement(paramsElement, "activateExtendedEditorOnLoad", "0", "false");
									}
								}
								else
								{
									Element paramsElement = document.createElement("params");

									addParameterElement(paramsElement, "title", "0");
									addParameterElement(paramsElement, "description", "0");
									addParameterElement(paramsElement, "class", "0");

									if(inputTypeId.equalsIgnoreCase("checkbox") || inputTypeId.equalsIgnoreCase("select") || inputTypeId.equalsIgnoreCase("radiobutton"))
									{
										addParameterElement(paramsElement, "values", "1");
									}

									if(inputTypeId.equalsIgnoreCase("textarea"))
									{
										addParameterElement(paramsElement, "width", "0", "700");
										addParameterElement(paramsElement, "height", "0", "150");
										addParameterElement(paramsElement, "enableWYSIWYG", "0", "false");
										addParameterElement(paramsElement, "enableTemplateEditor", "0", "false");
										addParameterElement(paramsElement, "enableFormEditor", "0", "false");
										addParameterElement(paramsElement, "enableContentRelationEditor", "0", "false");
										addParameterElement(paramsElement, "enableStructureRelationEditor", "0", "false");
										addParameterElement(paramsElement, "activateExtendedEditorOnLoad", "0", "false");
									}

									appinfoElement.appendChild(paramsElement);
									isModified = true;
								}
							}
							else
							{
								Element appInfo    	  = document.createElement("xs:appinfo");
								Element paramsElement = document.createElement("params");

								addParameterElement(paramsElement, "title", "0");
								addParameterElement(paramsElement, "description", "0");
								addParameterElement(paramsElement, "class", "0");

								if(inputTypeId.equalsIgnoreCase("checkbox") || inputTypeId.equalsIgnoreCase("select") || inputTypeId.equalsIgnoreCase("radiobutton"))
								{
									addParameterElement(paramsElement, "values", "1");
								}

								if(inputTypeId.equalsIgnoreCase("textarea"))
								{
									addParameterElement(paramsElement, "width", "0", "700");
									addParameterElement(paramsElement, "height", "0", "150");
									addParameterElement(paramsElement, "enableWYSIWYG", "0", "false");
									addParameterElement(paramsElement, "enableTemplateEditor", "0", "false");
									addParameterElement(paramsElement, "enableFormEditor", "0", "false");
									addParameterElement(paramsElement, "enableContentRelationEditor", "0", "false");
									addParameterElement(paramsElement, "enableStructureRelationEditor", "0", "false");
									addParameterElement(paramsElement, "activateExtendedEditorOnLoad", "0", "false");
								}

								annotationElement.appendChild(appInfo);
								appInfo.appendChild(paramsElement);
								isModified = true;
							}
						}
						else
						{
							Element annotation    = document.createElement("xs:annotation");
							Element appInfo       = document.createElement("xs:appinfo");
							Element paramsElement = document.createElement("params");

							addParameterElement(paramsElement, "title", "0");
							addParameterElement(paramsElement, "description", "0");
							addParameterElement(paramsElement, "class", "0");

							if(inputTypeId.equalsIgnoreCase("checkbox") || inputTypeId.equalsIgnoreCase("select") || inputTypeId.equalsIgnoreCase("radiobutton"))
							{
								addParameterElement(paramsElement, "values", "1");
							}

							if(inputTypeId.equalsIgnoreCase("textarea"))
							{
								addParameterElement(paramsElement, "width", "0", "700");
								addParameterElement(paramsElement, "height", "0", "150");
								addParameterElement(paramsElement, "enableWYSIWYG", "0", "false");
								addParameterElement(paramsElement, "enableTemplateEditor", "0", "false");
								addParameterElement(paramsElement, "enableFormEditor", "0", "false");
								addParameterElement(paramsElement, "enableContentRelationEditor", "0", "false");
								addParameterElement(paramsElement, "enableStructureRelationEditor", "0", "false");
								addParameterElement(paramsElement, "activateExtendedEditorOnLoad", "0", "false");
							}

							childElement.appendChild(annotation);
							annotation.appendChild(appInfo);
							appInfo.appendChild(paramsElement);
							isModified = true;
						}

					}
				}
				else if(schemaElement.getAttribute("version") != null && schemaElement.getAttribute("version").equalsIgnoreCase("2.0"))
				{
					isModified = true;
					schemaElement.setAttribute("version", "2.1");

					//Now we deal with the individual attributes and parameters
					String attributesXPath = "/xs:schema/xs:complexType/xs:all/xs:element/xs:complexType/xs:all/xs:element";
					NodeList anl = org.apache.xpath.XPathAPI.selectNodeList(document.getDocumentElement(), attributesXPath);
					for(int k=0; k < anl.getLength(); k++)
					{
						Element childElement = (Element)anl.item(k);

						String inputTypeId = childElement.getAttribute("type");

						NodeList annotationNodeList = childElement.getElementsByTagName("xs:annotation");
						if(annotationNodeList != null && annotationNodeList.getLength() > 0)
						{
							NodeList appinfoNodeList = childElement.getElementsByTagName("xs:appinfo");
							if(appinfoNodeList != null && appinfoNodeList.getLength() > 0)
							{
								NodeList paramsNodeList = childElement.getElementsByTagName("params");
								if(paramsNodeList != null && paramsNodeList.getLength() > 0)
								{
									Element paramsElement = (Element)paramsNodeList.item(0);

									if(inputTypeId.equalsIgnoreCase("textarea"))
									{
										addParameterElementIfNotExists(paramsElement, "width", "0", "700");
										addParameterElementIfNotExists(paramsElement, "height", "0", "150");
										addParameterElementIfNotExists(paramsElement, "enableWYSIWYG", "0", "false");
										addParameterElementIfNotExists(paramsElement, "enableTemplateEditor", "0", "false");
										addParameterElementIfNotExists(paramsElement, "enableFormEditor", "0", "false");
										addParameterElementIfNotExists(paramsElement, "enableContentRelationEditor", "0", "false");
										addParameterElementIfNotExists(paramsElement, "enableStructureRelationEditor", "0", "false");
										addParameterElementIfNotExists(paramsElement, "activateExtendedEditorOnLoad", "0", "false");
										
										isModified = true;
									}
								}
							}
						}
					}
				}
				else if(schemaElement.getAttribute("version") != null && schemaElement.getAttribute("version").equalsIgnoreCase("2.1"))
				{
					isModified = true;
					schemaElement.setAttribute("version", "2.2");

					//Now we deal with adding the validation part if not existent
					String validatorsXPath = "/xs:schema/xs:complexType[@name = 'Validation']";
					Node formNode = org.apache.xpath.XPathAPI.selectSingleNode(document.getDocumentElement(), validatorsXPath);
					if(formNode == null)
					{
					    String schemaXPath = "/xs:schema";
						Node schemaNode = org.apache.xpath.XPathAPI.selectSingleNode(document.getDocumentElement(), schemaXPath);
						
					    Element element = (Element)schemaNode;
					    
					    String validationXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><xs:complexType name=\"Validation\" xmlns:xi=\"http://www.w3.org/2001/XInclude\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\"><xs:annotation><xs:appinfo><form-validation><global><validator name=\"required\" classname=\"org.infoglue.cms.util.validators.CommonsValidator\" method=\"validateRequired\" methodParams=\"java.lang.Object,org.apache.commons.validator.Field\" msg=\"300\"/><validator name=\"requiredif\" classname=\"org.infoglue.cms.util.validators.CommonsValidator\" method=\"validateRequiredIf\" methodParams=\"java.lang.Object,org.apache.commons.validator.Field,org.apache.commons.validator.Validator\" msg=\"315\"/><validator name=\"matchRegexp\" classname=\"org.infoglue.cms.util.validators.CommonsValidator\" method=\"validateRegexp\" methodParams=\"java.lang.Object,org.apache.commons.validator.Field\" msg=\"300\"/></global><formset><form name=\"requiredForm\"></form></formset></form-validation></xs:appinfo></xs:annotation></xs:complexType>";
					    
					    InputSource validationXMLSource = new InputSource(new StringReader(validationXML));
						DOMParser parser2 = new DOMParser();
						parser2.parse(validationXMLSource);
						Document document2 = parser2.getDocument();

						Node node = document.importNode(document2.getDocumentElement(), true);
						element.appendChild(node);
					}
				}
				else if(schemaElement.getAttribute("version") != null && schemaElement.getAttribute("version").equalsIgnoreCase("2.2"))
				{
					isModified = true;
					schemaElement.setAttribute("version", "2.3");

					//Now we deal with the individual attributes and parameters
					String attributesXPath = "/xs:schema/xs:complexType/xs:all/xs:element/xs:complexType/xs:all/xs:element";
					NodeList anl = org.apache.xpath.XPathAPI.selectNodeList(document.getDocumentElement(), attributesXPath);
					for(int k=0; k < anl.getLength(); k++)
					{
						Element childElement = (Element)anl.item(k);

						String inputTypeId = childElement.getAttribute("type");

						NodeList annotationNodeList = childElement.getElementsByTagName("xs:annotation");
						if(annotationNodeList != null && annotationNodeList.getLength() > 0)
						{
							NodeList appinfoNodeList = childElement.getElementsByTagName("xs:appinfo");
							if(appinfoNodeList != null && appinfoNodeList.getLength() > 0)
							{
								NodeList paramsNodeList = childElement.getElementsByTagName("params");
								if(paramsNodeList != null && paramsNodeList.getLength() > 0)
								{
									Element paramsElement = (Element)paramsNodeList.item(0);

									addParameterElementIfNotExists(paramsElement, "initialData", "0", "");
									isModified = true;
								}
							}
						}
					}

				}
				else if(schemaElement.getAttribute("version") != null && schemaElement.getAttribute("version").equalsIgnoreCase("2.3"))
				{
					isModified = true;
					schemaElement.setAttribute("version", "2.4");

					//Now we deal with the individual attributes and parameters
					String attributesXPath = "/xs:schema/xs:complexType/xs:all/xs:element/xs:complexType/xs:all/xs:element";
					NodeList anl = org.apache.xpath.XPathAPI.selectNodeList(document.getDocumentElement(), attributesXPath);
					for(int k=0; k < anl.getLength(); k++)
					{
						Element childElement = (Element)anl.item(k);

						String inputTypeId = childElement.getAttribute("type");

						NodeList annotationNodeList = childElement.getElementsByTagName("xs:annotation");
						if(annotationNodeList != null && annotationNodeList.getLength() > 0)
						{
							NodeList appinfoNodeList = childElement.getElementsByTagName("xs:appinfo");
							if(appinfoNodeList != null && appinfoNodeList.getLength() > 0)
							{
								NodeList paramsNodeList = childElement.getElementsByTagName("params");
								if(paramsNodeList != null && paramsNodeList.getLength() > 0)
								{
									Element paramsElement = (Element)paramsNodeList.item(0);

									addParameterElementIfNotExists(paramsElement, "enableComponentPropertiesEditor", "0", "false");

									isModified = true;
								}
							}
						}
					}

				}

				
			}

			if(isModified)
			{
				StringBuffer sb = new StringBuffer();
				org.infoglue.cms.util.XMLHelper.serializeDom(document.getDocumentElement(), sb);
				contentTypeDefinition.setSchemaValue(sb.toString());

				update(contentTypeDefinition);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return contentTypeDefinition;
	}
	*/

	/**
	 * This method adds a parameter node with some default values if not allready existing.
	 */

	private boolean addParameterElement(Element parent, String name, String inputTypeId, String value, boolean isAllreadyModified)
	{
		boolean isModified = isAllreadyModified;

		NodeList titleNodeList = parent.getElementsByTagName(name);
		if(titleNodeList != null && titleNodeList.getLength() > 0)
		{
			Element titleElement = (Element)titleNodeList.item(0);
			if(!titleElement.hasChildNodes())
			{
				titleElement.appendChild(parent.getOwnerDocument().createTextNode(value));
				isModified = true;
			}
		}
		else
		{
			Element title = parent.getOwnerDocument().createElement(name);
			title.appendChild(parent.getOwnerDocument().createTextNode(value));
			parent.appendChild(title);
			isModified = true;
		}

		return isModified;
	}

	/**
	 * Returns an attribute value from the Entry
	 *
	 * @param entry The entry on which to find the value
	 * @param attributeName THe name of the attribute whose value is wanted
	 * @param escapeHTML A boolean indicating if the result should be escaped
	 * @return The String vlaue of the attribute, or blank if it doe snot exist.
	 */
	public String getAttributeValue(String attributes, String attributeName, boolean escapeHTML)
	{
		String value = "";
		String xml = attributes;

		int startTagIndex = xml.indexOf("<" + attributeName + ">");
		int endTagIndex   = xml.indexOf("]]></" + attributeName + ">");

		if(startTagIndex > 0 && startTagIndex < xml.length() && endTagIndex > startTagIndex && endTagIndex <  xml.length())
		{
			value = xml.substring(startTagIndex + attributeName.length() + 11, endTagIndex);
			if(escapeHTML)
				value = new VisualFormatter().escapeHTML(value);
		}		

		return value;
	}

}
