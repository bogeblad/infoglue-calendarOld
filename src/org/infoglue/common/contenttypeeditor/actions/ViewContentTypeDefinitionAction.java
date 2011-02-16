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

package org.infoglue.common.contenttypeeditor.actions;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.TransformerException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xerces.parsers.DOMParser;
import org.apache.xpath.XPathAPI;
import org.infoglue.calendar.actions.CalendarAbstractAction;
import org.infoglue.calendar.actions.ComposeEmailAction;
import org.infoglue.calendar.controllers.ContentTypeDefinitionController;
import org.infoglue.common.contenttypeeditor.entities.ContentTypeDefinition;
import org.infoglue.common.exceptions.ConstraintException;
import org.infoglue.common.exceptions.SystemException;
import org.infoglue.common.util.ConstraintExceptionBuffer;
import org.infoglue.common.util.XMLHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.opensymphony.webwork.ServletActionContext;

/**
 * This class implements the action class for the advanced ContentTypeDefinition-editor.
 * 
 * @author Mattias Bogeblad
 */

public class ViewContentTypeDefinitionAction extends CalendarAbstractAction
{
	private static Log log = LogFactory.getLog(ViewContentTypeDefinitionAction.class);

	private static final long serialVersionUID = 1L;

	public static final String PLAIN_EDITOR = "plain";
	public static final String UPDATED		= "updated";
	
	//private static CategoryController categoryController = CategoryController.getController();

	protected Long contentTypeDefinitionId;
	protected ContentTypeDefinition contentTypeDefinition;
	protected String currentContentTypeEditorViewLanguageCode;
	protected List attributes = null;
	protected List availableLanguages = null;
	protected List languageVOList;
	protected String title;
	protected String inputTypeId;
	protected String attributeName;
	protected String newAttributeName;
	protected String attributeParameterId;
	protected String attributeParameterValueId;
	protected String newAttributeParameterValueId;
	protected String attributeParameterValueLabel;
	protected String attributeParameterValueLocale;
	protected String attributeToExpand;
	protected String assetKey;
	protected String newAssetKey;
	protected String categoryKey;
	protected String newCategoryKey;
	
	protected String description = "";
	protected Integer maximumSize;
	protected String allowedContentTypes = "any";
	protected String imageWidth;
	protected String imageHeight;
	
	protected List activatedName = new ArrayList();

    protected void initialize(Long contentTypeDefinitionId) throws Exception
    {
    	//log.debug("contentTypeDefinitionId:" + contentTypeDefinitionId);
        this.contentTypeDefinition = ContentTypeDefinitionController.getController().getContentTypeDefinition(contentTypeDefinitionId, this.getSession());
    	
		this.contentTypeDefinition = ContentTypeDefinitionController.getController().validateAndUpdateContentType(contentTypeDefinitionId, this.contentTypeDefinition, this.getSession());
        this.contentTypeDefinition = ContentTypeDefinitionController.getController().getContentTypeDefinition(contentTypeDefinitionId, this.getSession());
		this.attributes = ContentTypeDefinitionController.getController().getContentTypeAttributes(this.contentTypeDefinition.getSchemaValue());
		//this.availableLanguages = LanguageController.getController().getLanguageVOList();
    }

    /**
     * The main method that fetches the Value-object for this use-case
     */

    public String execute() throws Exception
    {
        this.initialize(getContentTypeDefinitionId());
        
        ServletActionContext.getResponse().setContentType("text/html");
        
        return SUCCESS;
    }

	/**
	 * The method that initializes all for the editor mode
	 */

	public String doUseEditor() throws Exception
	{
		this.initialize(getContentTypeDefinitionId());
		return SUCCESS;
	}

	/**
	 * The method that initializes all for the simple mode
	 */

	public String doUseSimple() throws Exception
	{
		this.initialize(getContentTypeDefinitionId());
		return PLAIN_EDITOR;
	}

	public String doInsertAttribute() throws Exception
	{
		this.initialize(getContentTypeDefinitionId());
		log.debug("Inserting attribute..." + this.contentTypeDefinition.getSchemaValue().length());
		
		String newSchemaValue = ContentTypeDefinitionController.getController().insertContentTypeAttribute(this.contentTypeDefinition.getSchemaValue(), this.inputTypeId, this.activatedName);
		this.contentTypeDefinition.setSchemaValue(newSchemaValue);
		log.debug("Inserting attribute..." + this.contentTypeDefinition.getSchemaValue().length());

		ContentTypeDefinitionController.getController().update(getContentTypeDefinitionId(), this.contentTypeDefinition, getSession());

		this.initialize(getContentTypeDefinitionId());

		log.debug("Inserting attribute..." + this.contentTypeDefinition.getSchemaValue().length());
		
		return UPDATED;
	}


	public String doDeleteAttribute() throws Exception
	{
		log.debug("Deleting attribute....");
		this.initialize(getContentTypeDefinitionId());

		try
		{
			Document document = createDocumentFromDefinition();

			String attributesXPath = "/xs:schema/xs:complexType/xs:all/xs:element/xs:complexType/xs:all/xs:element[@name='" + this.attributeName + "']";
			NodeList anl = org.apache.xpath.XPathAPI.selectNodeList(document.getDocumentElement(), attributesXPath);
			if(anl != null && anl.getLength() > 0)
			{
				Element element = (Element)anl.item(0);
				element.getParentNode().removeChild(element);
			}

		    String validatorsXPath = "/xs:schema/xs:complexType[@name = 'Validation']/xs:annotation/xs:appinfo/form-validation/formset/form/field[@property = '" + attributeName + "']";
		    NodeList anl2 = org.apache.xpath.XPathAPI.selectNodeList(document.getDocumentElement(), validatorsXPath);
			for(int i=0; i<anl2.getLength(); i++)
			{
				Element element = (Element)anl2.item(i);
				element.getParentNode().removeChild(element);
			}

			saveUpdatedDefinition(document);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		this.initialize(getContentTypeDefinitionId());
	
		return UPDATED;
	}

	/**
	 * This method moves an content type attribute up one step.
	 */

	public String doMoveAttributeUp() throws Exception
	{
		this.initialize(getContentTypeDefinitionId());

		try
		{
			Document document = createDocumentFromDefinition();

			String attributesXPath = "/xs:schema/xs:complexType/xs:all/xs:element/xs:complexType/xs:all/xs:element";
			NodeList anl = org.apache.xpath.XPathAPI.selectNodeList(document.getDocumentElement(), attributesXPath);
			Element previousElement = null;
			for(int i=0; i < anl.getLength(); i++)
			{
				Element element = (Element)anl.item(i);
				if(element.getAttribute("name").equalsIgnoreCase(this.attributeName) && previousElement != null)
				{
					Element parent = (Element)element.getParentNode();
					parent.removeChild(element);
					parent.insertBefore(element, previousElement);
				}
				previousElement = element;
			}

			saveUpdatedDefinition(document);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		this.initialize(getContentTypeDefinitionId());
		
		return UPDATED;
	}


	/**
	 * This method moves an content type attribute down one step.
	 */

	public String doMoveAttributeDown() throws Exception
	{
		this.initialize(getContentTypeDefinitionId());

		try
		{
			Document document = createDocumentFromDefinition();

			String attributesXPath = "/xs:schema/xs:complexType/xs:all/xs:element/xs:complexType/xs:all/xs:element";
			NodeList anl = org.apache.xpath.XPathAPI.selectNodeList(document.getDocumentElement(), attributesXPath);
			Element parent = null;
			Element elementToMove = null;
			boolean isInserted = false;
			int position = 0;
			for(int i=0; i < anl.getLength(); i++)
			{
				Element element = (Element)anl.item(i);
				parent = (Element)element.getParentNode();

				if(elementToMove != null)
				{
					if(position == 2)
					{
						parent.insertBefore(elementToMove, element);
						isInserted = true;
						break;
					}
					else
						position++;
				}

				if(element.getAttribute("name").equalsIgnoreCase(this.attributeName))
				{
					elementToMove = element;
					parent.removeChild(elementToMove);
					position++;
				}
			}

			if(!isInserted && elementToMove != null)
				parent.appendChild(elementToMove);

			saveUpdatedDefinition(document);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		this.initialize(getContentTypeDefinitionId());
		
		return UPDATED;
	}

	
	/**
	 * This method moves an content type assetKey up one step.
	 */

	public String doMoveAssetKeyUp() throws Exception
	{
		this.initialize(getContentTypeDefinitionId());

		try
		{
			Document document = createDocumentFromDefinition();

			String attributesXPath = "/xs:schema/xs:simpleType[@name = '" + ContentTypeDefinitionController.ASSET_KEYS + "']/xs:restriction/xs:enumeration[@value='" + this.assetKey + "']";
			NodeList anl = XPathAPI.selectNodeList(document.getDocumentElement(), attributesXPath);
			if(anl != null && anl.getLength() > 0)
			{
				Element element = (Element)anl.item(0);
				Node parentElement = element.getParentNode();
				Node previuosSibling = element.getPreviousSibling();
				if(previuosSibling != null)
				{
				    parentElement.removeChild(element);
				    parentElement.insertBefore(element, previuosSibling);
				}
			}
						
			saveUpdatedDefinition(document);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		this.initialize(getContentTypeDefinitionId());
		
		return UPDATED;
	}


	/**
	 * This method moves an content type asset key down one step.
	 */

	public String doMoveAssetKeyDown() throws Exception
	{
		this.initialize(getContentTypeDefinitionId());

		try
		{
			Document document = createDocumentFromDefinition();

			String attributesXPath = "/xs:schema/xs:simpleType[@name = '" + ContentTypeDefinitionController.ASSET_KEYS + "']/xs:restriction/xs:enumeration[@value='" + this.assetKey + "']";
			NodeList anl = XPathAPI.selectNodeList(document.getDocumentElement(), attributesXPath);
			if(anl != null && anl.getLength() > 0)
			{
				Element element = (Element)anl.item(0);
				Node parentElement = element.getParentNode();
				Node nextSibling = element.getNextSibling();
				if(nextSibling != null)
				{
			        parentElement.removeChild(nextSibling);
			        parentElement.insertBefore(nextSibling, element);
				}
			}

			saveUpdatedDefinition(document);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		this.initialize(getContentTypeDefinitionId());
		
		return UPDATED;
	}


	public String doDeleteAttributeParameterValue() throws Exception
	{
		this.initialize(getContentTypeDefinitionId());

		try
		{
			Document document = createDocumentFromDefinition();

			String attributesXPath = "/xs:schema/xs:complexType/xs:all/xs:element/xs:complexType/xs:all/xs:element[@name='" + this.attributeName + "']/xs:annotation/xs:appinfo/params/param[@id='" + this.attributeParameterId +"']/values/value[@id='" + this.attributeParameterValueId + "']";
			NodeList anl = org.apache.xpath.XPathAPI.selectNodeList(document.getDocumentElement(), attributesXPath);
			if(anl != null && anl.getLength() > 0)
			{
				Element element = (Element)anl.item(0);
				element.getParentNode().removeChild(element);
			}

			saveUpdatedDefinition(document);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		this.initialize(getContentTypeDefinitionId());
		
		return UPDATED;
	}

	public String doInsertAttributeParameterValue() throws Exception
	{
		this.initialize(getContentTypeDefinitionId());

		try
		{
			Document document = createDocumentFromDefinition();

			String attributesXPath = "/xs:schema/xs:complexType/xs:all/xs:element/xs:complexType/xs:all/xs:element[@name='" + this.attributeName + "']/xs:annotation/xs:appinfo/params/param[@id='" + this.attributeParameterId +"']/values";
			log.debug("attributesXPath:" + attributesXPath);
			NodeList anl = org.apache.xpath.XPathAPI.selectNodeList(document.getDocumentElement(), attributesXPath);

			log.debug("anl:" + anl);
			if(anl != null)
				log.debug("anl:" + anl.getLength());

			if(anl != null && anl.getLength() > 0)
			{
				Element element = (Element)anl.item(0);
				Element newValue = document.createElement("value");
				newValue.setAttribute("id", getRandomName());
				newValue.setAttribute("label", getRandomName());
				element.appendChild(newValue);
			}

			saveUpdatedDefinition(document);

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		this.initialize(getContentTypeDefinitionId());
		
		return UPDATED;
	}

	/**
	 * We validate that ' ', '.', ''', '"' is not used in the attribute name as that will break the javascripts later.
	 */

	public String doUpdateAttribute() throws Exception
	{
		ConstraintExceptionBuffer ceb = new ConstraintExceptionBuffer();
		if(this.newAttributeName.indexOf(" ") > -1 || this.newAttributeName.indexOf(".") > -1 || this.newAttributeName.indexOf("'") > -1  || this.newAttributeName.indexOf("\"") > -1)
		{
			ceb.add(new ConstraintException("ContentTypeAttribute.updateAction", "3500"));
		}

		ceb.throwIfNotEmpty();
			
		this.initialize(getContentTypeDefinitionId());

		try
		{
			Document document = createDocumentFromDefinition();

			//Updating the content attribute
			String[] extraParameterNames = ServletActionContext.getRequest().getParameterValues("parameterNames");
			if(extraParameterNames != null)
			{
				for(int i=0; i < extraParameterNames.length; i++)
				{
					String extraParameterName = extraParameterNames[i];
					String value = ServletActionContext.getRequest().getParameter(extraParameterName);

					String extraParametersXPath = "/xs:schema/xs:complexType/xs:all/xs:element/xs:complexType/xs:all/xs:element[@name='" + this.attributeName + "']/xs:annotation/xs:appinfo/params/param[@id='" + extraParameterName +"']/values/value";
					NodeList extraParamsNodeList = org.apache.xpath.XPathAPI.selectNodeList(document.getDocumentElement(), extraParametersXPath);
					if(extraParamsNodeList != null && extraParamsNodeList.getLength() > 0)
					{
						Element element = (Element)extraParamsNodeList.item(0);

						if(extraParameterName.equalsIgnoreCase("values") && (this.inputTypeId.equalsIgnoreCase("select") || this.inputTypeId.equalsIgnoreCase("checkbox") || this.inputTypeId.equalsIgnoreCase("radiobutton")))
						{
							((Element)element.getParentNode().getParentNode()).setAttribute("inputTypeId", "1");
						}
						else
						{
							((Element)element.getParentNode().getParentNode()).setAttribute("inputTypeId", "0");
						}

						if(((Element)element.getParentNode().getParentNode()).getAttribute("inputTypeId").equals("0"))
						{
							if(this.currentContentTypeEditorViewLanguageCode != null && this.currentContentTypeEditorViewLanguageCode.length() > 0)
							{
								element.setAttribute("label_" + this.currentContentTypeEditorViewLanguageCode, value);
							}
							else
							{
								element.setAttribute("label", value);
							}
						}
					}
				}
			}

			//Updating the name and type
			String attributeXPath = "/xs:schema/xs:complexType/xs:all/xs:element/xs:complexType/xs:all/xs:element[@name='" + this.attributeName + "']";
			NodeList anl = org.apache.xpath.XPathAPI.selectNodeList(document.getDocumentElement(), attributeXPath);
			if(anl != null && anl.getLength() > 0)
			{
				Element element = (Element)anl.item(0);
				element.setAttribute("name", this.newAttributeName);
				element.setAttribute("type", this.inputTypeId);
			}

			try
			{
				//Updating the validation part
				String validationXPath = "//xs:complexType[@name='Validation']/xs:annotation/xs:appinfo/form-validation/formset/form/field[@property='" + this.attributeName + "']";
				NodeList fieldNodeList = org.apache.xpath.XPathAPI.selectNodeList(document.getDocumentElement(), validationXPath);
				if(fieldNodeList != null && fieldNodeList.getLength() > 0)
				{
					Element element = (Element)fieldNodeList.item(0);
					element.setAttribute("property", this.newAttributeName);
				}

				//Updating the dependent part
				String validationDependentXPath = "//xs:complexType[@name='Validation']/xs:annotation/xs:appinfo/form-validation/formset/form/field[@depends='requiredif']/var/var-value";
				NodeList requiredIfValueNodeList = org.apache.xpath.XPathAPI.selectNodeList(document.getDocumentElement(), validationDependentXPath);
				if(requiredIfValueNodeList != null && requiredIfValueNodeList.getLength() > 0)
				{
				    for(int i=0; i<requiredIfValueNodeList.getLength(); i++)
				    {
				        Element element = (Element)requiredIfValueNodeList.item(0);
				        if(element.getFirstChild() != null && element.getFirstChild().getNodeValue() != null && element.getFirstChild().getNodeValue().equals(this.attributeName))
				            element.getFirstChild().setNodeValue(this.newAttributeName);
				    }
				}
			}
			catch(Exception ve)
			{
			    ve.printStackTrace();
			}
				
			saveUpdatedDefinition(document);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		this.initialize(getContentTypeDefinitionId());

		return UPDATED;
	}


	public String doUpdateAttributeParameterValue() throws Exception
	{
		this.initialize(getContentTypeDefinitionId());

		try
		{
			Document document = createDocumentFromDefinition();

			String parameterValueXPath = "/xs:schema/xs:complexType/xs:all/xs:element/xs:complexType/xs:all/xs:element[@name='" + this.attributeName + "']/xs:annotation/xs:appinfo/params/param[@id='" + this.attributeParameterId +"']/values/value[@id='" + this.attributeParameterValueId + "']";
			NodeList parameterValuesNodeList = org.apache.xpath.XPathAPI.selectNodeList(document.getDocumentElement(), parameterValueXPath);
			if(parameterValuesNodeList != null && parameterValuesNodeList.getLength() > 0)
			{
				Element element = (Element)parameterValuesNodeList.item(0);
				element.setAttribute("id", this.newAttributeParameterValueId);

				if(this.currentContentTypeEditorViewLanguageCode != null && this.currentContentTypeEditorViewLanguageCode.length() > 0)
					element.setAttribute("label_" + this.currentContentTypeEditorViewLanguageCode, this.attributeParameterValueLabel);
				else
					element.setAttribute("label", this.attributeParameterValueLabel);
			}

			saveUpdatedDefinition(document);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		this.initialize(getContentTypeDefinitionId());

		return UPDATED;
	}


	public String doInsertAttributeValidator() throws Exception
	{
		this.initialize(getContentTypeDefinitionId());

		try
		{
			Document document = createDocumentFromDefinition();

			String validatorName = ServletActionContext.getRequest().getParameter("validatorName");
			
			if(validatorName != null && !validatorName.equalsIgnoreCase(""))
			{
			    String validatorsXPath = "/xs:schema/xs:complexType[@name = 'Validation']/xs:annotation/xs:appinfo/form-validation/formset/form";
				Node formNode = org.apache.xpath.XPathAPI.selectSingleNode(document.getDocumentElement(), validatorsXPath);
				if(formNode != null)
				{
					Element element = (Element)formNode;
					
					Element newField = document.createElement("field");
					newField.setAttribute("property", this.attributeName);
					newField.setAttribute("depends", validatorName);

					String errorKey = null;
					
					if(validatorName.equals("required"))
					{
					    errorKey = "300"; //Required
					}
					else if(validatorName.equals("requiredif"))
					{
					    errorKey = "300"; //Required

						Element newVar = document.createElement("var");
					    Element varNameElement = createTextElement(document, "var-name", "dependent");
						Element varValueElement = createTextElement(document, "var-value", "AttributeName");
						newVar.appendChild(varNameElement);
						newVar.appendChild(varValueElement);
						newField.appendChild(newVar);
					}
					else if(validatorName.equals("matchRegexp"))
					{
					    errorKey = "307"; //Invalid format

						Element newVar = document.createElement("var");
					    Element varNameElement = createTextElement(document, "var-name", "regexp");
						Element varValueElement = createTextElement(document, "var-value", ".*");
						newVar.appendChild(varNameElement);
						newVar.appendChild(varValueElement);
						newField.appendChild(newVar);
					}

					Element newMessage = document.createElement("msg");
					newMessage.setAttribute("name", validatorName);
					newMessage.setAttribute("key", errorKey);
					newField.appendChild(newMessage);
					
					element.appendChild(newField);
				}
			}
			
			saveUpdatedDefinition(document);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		this.initialize(getContentTypeDefinitionId());

		return UPDATED;
	}

	public String doUpdateAttributeValidatorArguments() throws Exception
	{
		this.initialize(getContentTypeDefinitionId());

		try
		{
			Document document = createDocumentFromDefinition();

			int i = 0;
			String attributeValidatorName = ServletActionContext.getRequest().getParameter("attributeValidatorName");
			String argumentName = ServletActionContext.getRequest().getParameter(i + "_argumentName");
			
			while(argumentName != null && !argumentName.equalsIgnoreCase(""))
			{
			    String argumentValue = ServletActionContext.getRequest().getParameter(i + "_argumentValue");
			    
			    String validatorsXPath = "/xs:schema/xs:complexType[@name = 'Validation']/xs:annotation/xs:appinfo/form-validation/formset/form/field[@property = '" + attributeName + "'][@depends = '" + attributeValidatorName + "']";
				Node fieldNode = org.apache.xpath.XPathAPI.selectSingleNode(document.getDocumentElement(), validatorsXPath);
				if(fieldNode != null)
				{
					Element element = (Element)fieldNode;
					NodeList nl = element.getElementsByTagName("var");
					for(int nlIndex=0; nlIndex < nl.getLength(); nlIndex++)
					{
					    Node node = (Node)nl.item(nlIndex);
					    element.removeChild(node);
					}
					
					Element newVar = document.createElement("var");
					
					Element varNameElement = createTextElement(document, "var-name", argumentName);
					Element varValueElement = createTextElement(document, "var-value", argumentValue);
					newVar.appendChild(varNameElement);
					newVar.appendChild(varValueElement);
					
					element.appendChild(newVar);
				}
				
				i++;
				argumentName = ServletActionContext.getRequest().getParameter(i + "_argumentName");
			}
			
			saveUpdatedDefinition(document);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		this.initialize(getContentTypeDefinitionId());

		return UPDATED;
	}

	public String doDeleteAttributeValidator() throws Exception
	{
		this.initialize(getContentTypeDefinitionId());

		try
		{
			Document document = createDocumentFromDefinition();

			int i = 0;
			String attributeValidatorName = ServletActionContext.getRequest().getParameter("attributeValidatorName");
			if(attributeValidatorName != null && !attributeValidatorName.equalsIgnoreCase(""))
			{
			    String validatorsXPath = "/xs:schema/xs:complexType[@name = 'Validation']/xs:annotation/xs:appinfo/form-validation/formset/form/field[@property = '" + attributeName + "'][@depends = '" + attributeValidatorName + "']";
				Node fieldNode = org.apache.xpath.XPathAPI.selectSingleNode(document.getDocumentElement(), validatorsXPath);
				if(fieldNode != null)
				{
					Element element = (Element)fieldNode;
					element.getParentNode().removeChild(element);
				}
			}
			
			saveUpdatedDefinition(document);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		this.initialize(getContentTypeDefinitionId());

		return UPDATED;
	}
/*
	
	public List getDefinedAssetKeys()
	{
		return ContentTypeDefinitionController.getController().getDefinedAssetKeys(contentTypeDefinition.getSchemaValue());
	}

	public List getDefinedCategoryKeys() throws Exception
	{
		List categoryKeys = ContentTypeDefinitionController.getController().getDefinedCategoryKeys(contentTypeDefinition.getSchemaValue());
		for (Iterator iter = categoryKeys.iterator(); iter.hasNext();)
		{
			CategoryAttribute info = (CategoryAttribute) iter.next();
			if(info.getCategoryId() != null)
				info.setCategoryName(getCategoryName(info.getCategoryId()));
			else
				info.setCategoryName("Undefined");
		}
		return categoryKeys;
	}

	public String getCategoryName(Integer id)
	{
		try
		{
			return categoryController.findById(id).getName();
		}
		catch(SystemException e)
		{
			return "Category not found";
		}
	}

	public String doInsertAssetKey() throws Exception
	{
		this.initialize(getContentTypeDefinitionId());

		try
		{
			Document document = createDocumentFromDefinition();
			createNewEnumerationKey(document, ContentTypeDefinitionController.ASSET_KEYS);
			saveUpdatedDefinition(document);
		}
		catch(Exception e)
		{
			getLogger().warn("Error adding asset key: ", e);
		}

		this.initialize(getContentTypeDefinitionId());
		return SUCCESS;
	}

	public String doInsertCategoryKey() throws Exception
	{
		this.initialize(getContentTypeDefinitionId());

		try
		{
			Document document = createDocumentFromDefinition();
			Element enumeration = createNewEnumerationKey(document, ContentTypeDefinitionController.CATEGORY_KEYS);

			Element annotation = document.createElement("xs:annotation");
			Element appinfo = document.createElement("xs:appinfo");
			Element params = document.createElement("params");

			enumeration.appendChild(annotation);
			annotation.appendChild(appinfo);
			appinfo.appendChild(params);
			params.appendChild(createTextElement(document, "title", getRandomName()));
			params.appendChild(createTextElement(document, "description", getRandomName()));
			params.appendChild(createTextElement(document, "categoryId", ""));

			saveUpdatedDefinition(document);
		}
		catch(Exception e)
		{
			getLogger().warn("Error adding categories key: ", e);
		}

		this.initialize(getContentTypeDefinitionId());
		return SUCCESS;
	}



	public String doUpdateAssetKey() throws Exception
	{
		initialize(getContentTypeDefinitionId());
		try
		{
			Document document = createDocumentFromDefinition();
			updateAssetEnumerationKey(document, ContentTypeDefinitionController.ASSET_KEYS, getAssetKey(), getNewAssetKey(), this.description, this.maximumSize, this.allowedContentTypes, this.imageWidth, this.imageHeight);
			saveUpdatedDefinition(document);
		}
		catch(Exception e)
		{
			getLogger().warn("Error updating asset key: ", e);
		}

		initialize(getContentTypeDefinitionId());
		return SUCCESS;
	}

	public String doUpdateCategoryKey() throws Exception
	{
		initialize(getContentTypeDefinitionId());

		try
		{
			Document document = createDocumentFromDefinition();
			Element enumeration = updateEnumerationKey(document, ContentTypeDefinitionController.CATEGORY_KEYS, getCategoryKey(), getNewCategoryKey());

			if(enumeration != null)
			{
				Element title = (Element)XPathAPI.selectSingleNode(enumeration, "xs:annotation/xs:appinfo/params/title");
				setTextElement(title, getSingleParameter("title"));

				Element description = (Element)XPathAPI.selectSingleNode(enumeration, "xs:annotation/xs:appinfo/params/description");
				setTextElement(description, getSingleParameter("description"));

				Element categoryId = (Element)XPathAPI.selectSingleNode(enumeration, "xs:annotation/xs:appinfo/params/categoryId");
				setTextElement(categoryId, getSingleParameter("categoryId"));
			}

			saveUpdatedDefinition(document);
		}
		catch(Exception e)
		{
			getLogger().warn("Error updating category key: ", e);
		}

		initialize(getContentTypeDefinitionId());
		return SUCCESS;
	}

	public String doDeleteAssetKey() throws Exception
	{
		return deleteKey(ContentTypeDefinitionController.ASSET_KEYS, getAssetKey());
	}

	public String doDeleteCategoryKey() throws Exception
	{
		return deleteKey(ContentTypeDefinitionController.CATEGORY_KEYS, getCategoryKey());
	}

	private String deleteKey(String keyType, String key) throws Exception
	{
		this.initialize(getContentTypeDefinitionId());

		try
		{
			Document document = createDocumentFromDefinition();

			String attributesXPath = "/xs:schema/xs:simpleType[@name = '" + keyType + "']/xs:restriction/xs:enumeration[@value='" + key + "']";
			NodeList anl = XPathAPI.selectNodeList(document.getDocumentElement(), attributesXPath);
			if(anl != null && anl.getLength() > 0)
			{
				Element element = (Element)anl.item(0);
				element.getParentNode().removeChild(element);
			}

			saveUpdatedDefinition(document);
		}
		catch(Exception e)
		{
			getLogger().warn("Error updating key: " + keyType, e);
		}

		this.initialize(getContentTypeDefinitionId());
		return SUCCESS;
	}

	public CategoryController getCategoryController()
	{
		return categoryController;
	}

	public List getAllCategories() throws SystemException
	{
		return getCategoryController().findAllActiveCategories();
	}
*/
	//-------------------------------------------------------------------------------------
	// XML Helper Methods
	//-------------------------------------------------------------------------------------
	/**
	 * Consolidate the Document creation
	 */
	private Document createDocumentFromDefinition() throws SAXException, IOException
	{
		String contentTypeDefinitionString = this.contentTypeDefinition.getSchemaValue();
		InputSource xmlSource = new InputSource(new StringReader(contentTypeDefinitionString));
		DOMParser parser = new DOMParser();
		parser.parse(xmlSource);
		return parser.getDocument();
	}

	/**
	 * Consolidate the update of a ContentTypeDefinition Document to the persistence mechanism
	 */
	private void saveUpdatedDefinition(Document document) throws ConstraintException, SystemException
	{
		StringBuffer sb = new StringBuffer();
		XMLHelper.serializeDom(document.getDocumentElement(), sb);
		this.contentTypeDefinition.setSchemaValue(sb.toString());
		//ContentTypeDefinitionController.getController().update(contentTypeDefinition);
	}

	/**
	 * Creates an <xs:enumeration> element with the specified key name
	 * @return The Element if child changes are needed, null if the element coudl not be created
	 */
	private Element createNewEnumerationKey(Document document, String keyType) throws TransformerException
	{
		Element enumeration = null;
		String assetKeysXPath = "/xs:schema/xs:simpleType[@name = '" + keyType + "']/xs:restriction";
		NodeList anl = XPathAPI.selectNodeList(document.getDocumentElement(), assetKeysXPath);

		Element keyRestriction = null;

		if(anl != null && anl.getLength() > 0)
		{
			keyRestriction = (Element)anl.item(0);
		}
		else
		{
			//The key type was not defined so we create it first.
			String schemaXPath = "/xs:schema";
			NodeList schemaNL = XPathAPI.selectNodeList(document.getDocumentElement(), schemaXPath);
			if(schemaNL != null && schemaNL.getLength() > 0)
			{
				Element schemaElement = (Element)schemaNL.item(0);

				Element keySimpleType = document.createElement("xs:simpleType");
				keySimpleType.setAttribute("name", keyType);

				keyRestriction = document.createElement("xs:restriction");
				keyRestriction.setAttribute("base", "xs:string");

				keySimpleType.appendChild(keyRestriction);
				schemaElement.appendChild(keySimpleType);
			}
		}

		enumeration = document.createElement("xs:enumeration");
		enumeration.setAttribute("value", getRandomName());
		keyRestriction.appendChild(enumeration);
		return enumeration;
	}

	/**
	 * Find an <xs:enumeration> element and update the key value.
	 * @return The Element if child changes are needed, null if the element is not found
	 */
	private Element updateEnumerationKey(Document document, String keyType, String oldKey, String newKey) throws TransformerException
	{
		String attributesXPath = "/xs:schema/xs:simpleType[@name = '" + keyType + "']/xs:restriction/xs:enumeration[@value='" + oldKey + "']";
		NodeList anl = XPathAPI.selectNodeList(document.getDocumentElement(), attributesXPath);
		if(anl != null && anl.getLength() > 0)
		{
			Element enumeration = (Element)anl.item(0);
			enumeration.setAttribute("value", newKey);
			return enumeration;
		}

		return null;
	}

	/**
	 * Find an <xs:enumeration> element and update the key value.
	 * @return The Element if child changes are needed, null if the element is not found
	 */
	private Element updateAssetEnumerationKey(Document document, String keyType, String oldKey, String newKey, String description, Integer maximumSize, String allowedContentTypes, String imageWidth, String imageHeight) throws TransformerException
	{
	    if(description == null)
	        description = "Undefined";
	    if(maximumSize == null)
	        maximumSize = new Integer(100);
	    if(allowedContentTypes == null)
	        allowedContentTypes = "*";
	    if(imageWidth == null)
	        imageWidth = "*";
	    if(imageHeight == null)
	        imageHeight = "*";
	    
	    
		String attributesXPath = "/xs:schema/xs:simpleType[@name = '" + keyType + "']/xs:restriction/xs:enumeration[@value='" + oldKey + "']";
		NodeList anl = XPathAPI.selectNodeList(document.getDocumentElement(), attributesXPath);
		if(anl != null && anl.getLength() > 0)
		{
			Element enumeration = (Element)anl.item(0);
			enumeration.setAttribute("value", newKey);
			
			Element descriptionElement = (Element)XPathAPI.selectSingleNode(enumeration, "xs:annotation/xs:appinfo/params/description");
			Element maximumSizeElement = (Element)XPathAPI.selectSingleNode(enumeration, "xs:annotation/xs:appinfo/params/maximumSize");
			Element allowedContentTypesElement = (Element)XPathAPI.selectSingleNode(enumeration, "xs:annotation/xs:appinfo/params/allowedContentTypes");
			Element imageWidthElement = (Element)XPathAPI.selectSingleNode(enumeration, "xs:annotation/xs:appinfo/params/imageWidth");
			Element imageHeightElement = (Element)XPathAPI.selectSingleNode(enumeration, "xs:annotation/xs:appinfo/params/imageHeight");

			if(descriptionElement == null)
			{
				Element annotation = document.createElement("xs:annotation");
				Element appinfo = document.createElement("xs:appinfo");
				Element params = document.createElement("params");
	
				enumeration.appendChild(annotation);
				annotation.appendChild(appinfo);
				appinfo.appendChild(params);
				
				descriptionElement = createTextElement(document, "description", getRandomName());
				maximumSizeElement = createTextElement(document, "maximumSize", maximumSize.toString());
				allowedContentTypesElement = createTextElement(document, "allowedContentTypes", allowedContentTypes);
			    imageWidthElement = createTextElement(document, "imageWidth", imageWidth);
			    imageHeightElement = createTextElement(document, "imageHeight", imageHeight);
				
				params.appendChild(descriptionElement);
				params.appendChild(maximumSizeElement);
				params.appendChild(allowedContentTypesElement);
				params.appendChild(imageWidthElement);
				params.appendChild(imageHeightElement);
			}
			else
			{
				setTextElement(descriptionElement, description);
				setTextElement(maximumSizeElement, maximumSize.toString());
				setTextElement(allowedContentTypesElement, allowedContentTypes);
				setTextElement(imageWidthElement, imageWidth);
				setTextElement(imageHeightElement, imageHeight);
			}
			
			return enumeration;
		}

		return null;
	}
	
	/**
	 * Creates a new text element
	 */
	private Element createTextElement(Document document, String tagName, String value)
	{
		Element e = document.createElement(tagName);
		e.appendChild(document.createTextNode(value));
		return e;
	}

	/**
	 * Updates the text child of an element, creating it if it needs to.
	 */
	private void setTextElement(Element e, String value)
	{
		if(e.getFirstChild() != null)
			e.getFirstChild().setNodeValue(value);
		else
			 e.appendChild(e.getOwnerDocument().createTextNode(value));
	}


	/**
	 * Generates a random name
	 */
	private String getRandomName()
	{
		return "undefined" + (int)(Math.random() * 100);
	}


	//-------------------------------------------------------------------------------------
	// Attribute Accessors
	//-------------------------------------------------------------------------------------

	public String getTitle()
	{
		return this.title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

    public String getName()
    {
        return this.contentTypeDefinition.getName();
    }

    public String getSchemaValue()
    {
        return this.contentTypeDefinition.getSchemaValue();
    }

	public Integer getType()
	{
		return this.contentTypeDefinition.getType();
	}

	/**
	 * This method returns the attributes in the content type definition for generation.
	 */

	public List getContentTypeAttributes()
	{
		return this.attributes;
	}

	public String getInputTypeId()
	{
		return inputTypeId;
	}

	public void setInputTypeId(String string)
	{
		inputTypeId = string;
	}

	public String getAttributeName()
	{
		return this.attributeName;
	}

	public void setAttributeName(String attributeName)
	{
		this.attributeName = attributeName;
	}

	public void setNewAttributeName(String newAttributeName)
	{
		this.newAttributeName = newAttributeName;
	}

	public String getAttributeParameterValueId()
	{
		return attributeParameterValueId;
	}

	public void setAttributeParameterValueId(String string)
	{
		attributeParameterValueId = string;
	}

	public String getAttributeParameterId()
	{
		return attributeParameterId;
	}

	public void setAttributeParameterId(String string)
	{
		attributeParameterId = string;
	}

	public String getAttributeParameterValueLabel()
	{
		return attributeParameterValueLabel;
	}

	public String getNewAttributeParameterValueId()
	{
		return newAttributeParameterValueId;
	}

	public void setAttributeParameterValueLabel(String string)
	{
		attributeParameterValueLabel = string;
	}

	public void setNewAttributeParameterValueId(String string)
	{
		newAttributeParameterValueId = string;
	}

	public String getAttributeParameterValueLocale()
	{
		return attributeParameterValueLocale;
	}

	public void setAttributeParameterValueLocale(String string)
	{
		attributeParameterValueLocale = string;
	}

	public String getAttributeToExpand()
	{
		return attributeToExpand;
	}

	public void setAttributeToExpand(String string)
	{
		attributeToExpand = string;
	}

	public String getCurrentContentTypeEditorViewLanguageCode()
	{
		return currentContentTypeEditorViewLanguageCode;
	}

	public void setCurrentContentTypeEditorViewLanguageCode(String string)
	{
		currentContentTypeEditorViewLanguageCode = string;
	}

	public List getAvailableLanguages()
	{
		return availableLanguages;
	}

	public String getAssetKey()			{ return assetKey; }
	public void setAssetKey(String s)	{ assetKey = s; }

	public String getNewAssetKey()			{ return newAssetKey; }
	public void setNewAssetKey(String s)	{ newAssetKey = s; }

	public String getCategoryKey()			{ return categoryKey; }
	public void setCategoryKey(String s)	{ categoryKey = s; }

	public String getNewCategoryKey()		{ return newCategoryKey; }
	public void setNewCategoryKey(String s)	{ newCategoryKey = s; }

	public String getErrorKey()
	{
		return "ContentTypeAttribute.updateAction";
	}

	public String getReturnAddress()
	{
		return "ViewListContentTypeDefinition.action";
	}

    public String getAllowedContentTypes()
    {
        return allowedContentTypes;
    }

    public void setAllowedContentTypes(String assetContentType)
    {
        this.allowedContentTypes = assetContentType;
    }
    
    public String getImageHeight()
    {
        return imageHeight;
    }
    
    public void setImageHeight(String imageHeight)
    {
        this.imageHeight = imageHeight;
    }
    
    public String getImageWidth()
    {
        return imageWidth;
    }
    
    public void setImageWidth(String imageWidth)
    {
        this.imageWidth = imageWidth;
    }
    
    public Integer getMaximumSize()
    {
        return maximumSize;
    }
    
    public void setMaximumSize(Integer maximumSize)
    {
        this.maximumSize = maximumSize;
    }
    
    public void setDescription(String description)
    {
        this.description = description;
    }
    public List getActivatedName()
    {
        return activatedName;
    }

	public Long getContentTypeDefinitionId()
	{
		return contentTypeDefinitionId;
	}

	public void setContentTypeDefinitionId(Long contentTypeDefinitionId)
	{
		this.contentTypeDefinitionId = contentTypeDefinitionId;
	}

	public ContentTypeDefinition getContentTypeDefinition()
	{
		return contentTypeDefinition;
	}
}
