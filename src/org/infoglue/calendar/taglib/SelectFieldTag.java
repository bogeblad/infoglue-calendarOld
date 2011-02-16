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
package org.infoglue.calendar.taglib;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.servlet.jsp.JspException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoglue.calendar.entities.BaseEntity;
import org.infoglue.calendar.entities.Role;
import org.infoglue.calendar.entities.Group;
import org.infoglue.calendar.util.AttributeType;

import org.infoglue.common.contenttypeeditor.entities.ContentTypeAttributeParameter;
import org.infoglue.common.contenttypeeditor.entities.ContentTypeAttributeParameterValue;
import org.infoglue.common.security.beans.InfoGlueGroupBean;
import org.infoglue.common.security.beans.InfoGluePrincipalBean;
import org.infoglue.common.security.beans.InfoGlueRoleBean;

import com.opensymphony.webwork.ServletActionContext;


/**
 * 
 */
public class SelectFieldTag extends AbstractCalendarTag 
{
    private static Log log = LogFactory.getLog(SelectFieldTag.class);

	private static final long serialVersionUID = 3617579309963752240L;
	
	private String name;
	private String labelCssClass = "";
	private String cssClass = "";
	private String size = "";
	private String multiple = "false";
	private String[] selectedValues;
	private List selectedValueList;
	private Set selectedValueSet;
	private Collection values;
	private String label;
	private String headerItem;
	private boolean skipContainer = false;
	private boolean skipLineBreak = false;
	
	private List fieldErrors;
	private Object errorAction = null;
	
    private boolean mandatory;

	private Boolean skipRowDiv = false;
	private String rowDivHTMLStart = "<div class=\"fieldrow\">";
	private String rowDivHTMLEnd = "</div>";
	private String requiredLabelClass = "redstar";
	private String requiredText = "*";

	public void setRequiredText(String requiredText)
	{
		this.requiredText = requiredText;
	}

	public void setRequiredLabelClass(String requiredLabelClass)
	{
		this.requiredLabelClass = requiredLabelClass;
	}

	public void setSkipRowDiv(String skipRowDiv) throws JspException
    {
        String evaluatedString = evaluateString("AbstractInputCalendarTag", "skipRowDiv", skipRowDiv);
        if(evaluatedString != null && !evaluatedString.equals(skipRowDiv))
        	skipRowDiv = evaluatedString;
        
    	if(skipRowDiv.equalsIgnoreCase("true"))
            this.skipRowDiv = true;
        else
            this.skipRowDiv = false;   
    }

    public void setRowDivHTMLStart(String rowDivHTMLStart)
    {
        this.rowDivHTMLStart = rowDivHTMLStart;
    }

    public void setRowDivHTMLEnd(String rowDivHTMLEnd)
    {
        this.rowDivHTMLEnd = rowDivHTMLEnd;
    }

	/**
	 * 
	 */
	public SelectFieldTag() 
	{
		super();
	}
	
	public int doEndTag() throws JspException
    {
		String errorName = name;
		if(errorName.indexOf("attribute_") > -1)
			errorName = errorName.substring(errorName.indexOf("attribute_") + 10);

	    fieldErrors = (List)findOnValueStack("#fieldErrors." + errorName);
	    
	    errorAction = findOnValueStack("#errorAction");
	    if(errorAction != null)
	    {
	        Object obj = findOnValueStack("#errorAction." + errorName);
	        if(obj instanceof String)
	            selectedValues = new String[]{(String)obj};
	        else if(obj instanceof String[])
	            selectedValues = (String[])obj;
	    }

	    String errorMessage = "";
	    if(fieldErrors != null && fieldErrors.size() > 0)
	    {   
	        Iterator i = fieldErrors.iterator();
	        while(i.hasNext())
		    {
	            String fieldError = (String)i.next();
	            String translatedError = this.getLabel(fieldError);
	            if(translatedError != null && translatedError.length() > 0)
	                fieldError = translatedError;

	          	errorMessage = "<span class=\"errorMessage\">" + fieldError + "</span>";
	        }
	    }	

	    StringBuffer sb = new StringBuffer();

	    if(!skipContainer)
	    	sb.append(rowDivHTMLStart);

	    if(this.label != null)
	        sb.append("<label for=\"" + this.name + "\">" + this.label + "</label>" + (mandatory ? "<span class=\"" + requiredLabelClass + "\">" + requiredText + "</span>" : "") + " " + errorMessage + (skipLineBreak ? "" : "<br/>"));
		else
		    sb.append("<label for=\"" + this.name + "\">" + this.name + "</label>" + (mandatory ? "<span class=\"" + requiredLabelClass + "\">" + requiredText + "</span>" : "") + " " + errorMessage + (skipLineBreak ? "" : "<br/>"));
			    
        sb.append("<select id=\"" + name + "\" name=\"" + name + "\" " + (multiple.equals("false") ? "" : "multiple=\"true\"") + " " + (size.equals("") ? "" : "size=\"" + size + "\"") + " class=\"" + cssClass + "\">");

        if(this.headerItem != null)
        {
            String selectedTop = "";
        	
            if((selectedValues == null || selectedValues.length == 0 || selectedValues[0].length() == 0) && (selectedValueList == null || selectedValueList.size() == 0) && (selectedValueSet == null || selectedValueSet.size() == 0))
                selectedTop = "selected=\"true\"";
                    
            sb.append("<option value=\"\" " + selectedTop + ">" + headerItem + "</option>");
            sb.append("<option value=\"\">------------------</option>");
        }

        if(values != null)
        {
    	    Map<String,String> sortedOptions = new TreeMap<String, String>();

    	    Iterator valuesIterator = values.iterator();
	        while(valuesIterator.hasNext())
		    {
	            String id;
	            String optionText;
	            Object obj = valuesIterator.next();
	            //log.debug("Obj: " + obj.getClass().getName());
	            if(obj instanceof InfoGluePrincipalBean)
	            {
	                InfoGluePrincipalBean value = (InfoGluePrincipalBean)obj;
	                id = value.getName().toString();
	                optionText = value.getFirstName() + " " + value.getLastName();
	            } 
	            else if(obj instanceof InfoGlueRoleBean)
	            {
	                InfoGlueRoleBean value = (InfoGlueRoleBean)obj;
	                id = value.getName().toString();
	                optionText = value.getDisplayName();
	            } 
	            else if(obj instanceof InfoGlueGroupBean)
	            {
	                InfoGlueGroupBean value = (InfoGlueGroupBean)obj;
	                id = value.getName().toString();
	                optionText = value.getDisplayName();
	            } 
	            else if(obj instanceof BaseEntity)
	            {
	                BaseEntity value = (BaseEntity)obj;
	                id = value.getId().toString();
	                optionText = value.getLocalizedName(getLanguageCode(), "sv");
	            }
	            else if(obj instanceof AttributeType)
	            {
	            	AttributeType value = (AttributeType)obj;
	                id = value.getId();
	                optionText = value.getName();
	            }
	            else if(obj instanceof Locale)
	            {
	            	Locale value = (Locale)obj;
	                id = value.getLanguage();
	                optionText = value.getDisplayLanguage();
	            }
	            else if(obj instanceof ContentTypeAttributeParameterValue)
	            {
	            	ContentTypeAttributeParameterValue value = (ContentTypeAttributeParameterValue)obj;
	            	id = value.getId();
	                optionText = value.getValue("label");
	            }
	            else if(obj instanceof String[])
	            {
	            	String[] value = (String[])obj;
	            	if(value.length > 1)
	            	{
	            		id = value[0];
	            		optionText = value[1];
	            	}
	            	else
	            	{
	            		id = "faulty";
	            		optionText = "faulty";
	            	}
	            }
	            else
	            {
	                String value = obj.toString();
	                id = value;
	                optionText = value;
	            }
            	log.info("ID:" + id + ": optionText:" + optionText);
            		
	            String selected = "";
	            if(selectedValues != null)
	            {
	            	log.info("selectedValues:" + selectedValues + ":" + selectedValues.length);
	            	for(int i=0; i<selectedValues.length; i++)
		            {
	                	log.info("1:" + id + "=" + selectedValues[i]);
		                if(id.equalsIgnoreCase(selectedValues[i].toString()))
		                {
		                    selected = " selected=\"1\"";
		                	break;
		                }
		                else
		                    selected = "";
		            }
	            }
	            else if(selectedValueList != null)
	            {
	                Iterator selectedValueListIterator = selectedValueList.iterator();
	                while(selectedValueListIterator.hasNext())
		            {
	                    String selId;
	                	Object selObj = selectedValueListIterator.next();
	    	            if(selObj instanceof InfoGluePrincipalBean)
	    	            {
	    	                InfoGluePrincipalBean selValue = (InfoGluePrincipalBean)selObj;
	    	                selId = selValue.getName().toString();
	    	            } 
	    	            else if(selObj instanceof InfoGlueRoleBean)
	    	            {
	    	                InfoGlueRoleBean value = (InfoGlueRoleBean)selObj;
	    	                selId = value.getName().toString();
	    	            } 
	    	            else if(selObj instanceof InfoGlueGroupBean)
	    	            {
	    	                InfoGlueGroupBean value = (InfoGlueGroupBean)selObj;
	    	                selId = value.getName().toString();
	    	            } 
	    	            else if(selObj instanceof Role)
	    	            {
	    	                Role value = (Role)selObj;
	    	                selId = value.getName().toString();
	    	            } 
	    	            else if(selObj instanceof Group)
	    	            {
	    	                Group value = (Group)selObj;
	    	                selId = value.getName().toString();
	    	            }
	    	            else
	    	            {
	    	                BaseEntity selValue = (BaseEntity)selObj;
	    	                selId = selValue.getId().toString();
	    	            }
	    	            
	    	            log.info("2:" + id + "=" + selId);
		                if(id.equalsIgnoreCase(selId))
		                    selected = " selected=\"1\"";
		            }
	            }
	            else if(selectedValueSet != null)
	            {
	                Iterator selectedValueSetIterator = selectedValueSet.iterator();
	                while(selectedValueSetIterator.hasNext())
		            {
	                    String selId;
	                	Object selObj = selectedValueSetIterator.next();
	                	if(selObj instanceof InfoGluePrincipalBean)
	    	            {
	    	                InfoGluePrincipalBean selValue = (InfoGluePrincipalBean)selObj;
	    	                selId = selValue.getName().toString();
	    	            } 
	    	            else if(selObj instanceof InfoGlueRoleBean)
	    	            {
	    	                InfoGlueRoleBean value = (InfoGlueRoleBean)selObj;
	    	                selId = value.getName().toString();
	    	            } 
	    	            else if(selObj instanceof InfoGlueGroupBean)
	    	            {
	    	                InfoGlueGroupBean value = (InfoGlueGroupBean)selObj;
	    	                selId = value.getName().toString();
	    	            } 
	    	            else if(selObj instanceof Role)
	    	            {
	    	                Role value = (Role)selObj;
		                	selId = value.getName().toString();
	    	            } 
	    	            else if(selObj instanceof Group)
	    	            {
	    	                Group value = (Group)selObj;
	    	                selId = value.getName().toString();
	    	            }
	    	            else
	    	            {
	    	                BaseEntity selValue = (BaseEntity)selObj;
	    	                selId = selValue.getId().toString();
	    	            }
	    	            
	    	            log.info("3:" + id + "=" + selId);
	                    if(id.equalsIgnoreCase(selId))
		                    selected = " selected=\"1\"";
		            }
	            }
	            
	            sortedOptions.put(optionText.toLowerCase(), "<option value=\"" + id + "\"" + selected + ">" + optionText + "</option>");
	            //sb.append("<option value=\"" + id + "\"" + selected + ">" + optionText + "</option>");
	        }
	        
	        for(String sortedOptionKey : sortedOptions.keySet())
	        {
	        	String value = sortedOptions.get(sortedOptionKey);
	            //sb.append("<option value=\"" + id + "\"" + selected + ">" + optionText + "</option>");
	        	sb.append(value);
	        }

        }

        sb.append("</select><br/>");
        
	    if(!skipContainer)
	    	sb.append(rowDivHTMLEnd);
        
        write(sb.toString());
	    
        log.info("sb:" + sb.toString());
        this.selectedValues = null;
        this.selectedValueList = null;
        this.selectedValueSet = null;
        
        this.rowDivHTMLStart = "<div class=\"fieldrow\">";
        this.rowDivHTMLEnd = "</div>";
        this.skipRowDiv = false;
        this.requiredLabelClass = "redstar";
        this.requiredText = "*";

        return EVAL_PAGE;
    }

    public void setCssClass(String cssClass)
    {
        this.cssClass = cssClass;
    }
    
    public void setName(String name) throws JspException
    {
        Object o = findOnValueStack(name);
        String evaluatedString = evaluateString("SelectFieldTag", "name", name);
        if(o != null && !(o instanceof String[]))
            this.name = o.toString();
        else if(evaluatedString != null && !evaluatedString.equals(name))
            this.name = evaluatedString;
        else
        {
            this.name = name;
        }
    }

    public void setLabel(String rawLabel) throws JspException
    {
        Object o = findOnValueStack(rawLabel);
        String evaluatedString = evaluateString("SelectFieldTag", "label", rawLabel);
        log.info("o:" + o);
        log.info("evaluatedString:" + evaluatedString);
        if(o != null)
            this.label = (String)o;
        else if(evaluatedString != null && !evaluatedString.equals(rawLabel))
            this.label = evaluatedString;
        else
        {
            String translatedLabel = this.getLabel(rawLabel);
            if(translatedLabel != null && translatedLabel.length() > 0)
                this.label = translatedLabel;
        }
    }

    public void setHeaderItem(String headerItem)
    {
        this.headerItem = headerItem;
    }

    public void setMultiple(String multiple)
    {
        this.multiple = multiple;
    }

    public void setSelectedValues(String selectedValues) throws JspException
    {
        Object o = findOnValueStack(selectedValues);
        if(o != null) 
            this.selectedValues = (String[])o;
        else
        {
            try
            {
                this.selectedValues = evaluateStringArray("SelectTag", "selectedValues", selectedValues);
            }
            catch(Exception e)
            {
                this.selectedValues = null;
            }
        }
        
        //log.debug("this.selectedValues:" + this.selectedValues);
    }

    public void setSelectedValue(String selectedValue) throws JspException
    {
        Object o = findOnValueStack(selectedValue);
        //log.debug("o:" + o);
        if(o != null) 
            this.selectedValues = new String[] {o.toString()};
        else
            this.selectedValues = null;
        
        //this.selectedValues = new String[] {evaluateString("SelectTag", "selectedValue", selectedValue)};
    }

    public void setValue(String value) throws JspException
    {
        Object o = findOnValueStack(value);
        if(o != null)
        {
            this.values = (Collection)o;
        }
        else
            this.selectedValues = null;
        
        //this.values = evaluateList("SelectTag", "values", values);
    }

    public void setSelectedValueList(String value) throws JspException
    {
        Object o = findOnValueStack(value);
        if(o != null) 
        {
            this.selectedValueList = (List)o;
        }
        else
        {
            this.selectedValueList = null;
        }

        //this.values = evaluateList("SelectTag", "values", values);
    }

    public void setSelectedValueSet(String value) throws JspException
    {
        log.info("setSelectedValueSet VALUE:" + value);
        Object o = findOnValueStack(value);
        if(o != null) 
        {
            this.selectedValueSet = (Set)o;
        }
        else
        {
            this.selectedValueSet = null;
        }
        //this.values = evaluateList("SelectTag", "values", values);
    }

    
    public void setSize(String size)
    {
        this.size = size;
    }
    public void setLabelCssClass(String labelCssClass)
    {
        this.labelCssClass = labelCssClass;
    }
    
	public void setSkipContainer(boolean skipContainer)
	{
		this.skipContainer = skipContainer;
	}

	public void setSkipLineBreak(boolean skipLineBreak)
	{
		this.skipLineBreak = skipLineBreak;
	}

    public void setRequired(String required) throws JspException
    {
        String evaluatedString = evaluateString("AbstractInputCalendarTag", "required", required);
        if(evaluatedString != null && !evaluatedString.equals(required))
        	required = evaluatedString;
        
    	if(required.equalsIgnoreCase("true"))
            this.mandatory = true;
        else
            this.mandatory = false;   
    }

    
    public void setMandatory(String mandatory)
    {
    	log.debug("APA1:" + mandatory);
    }

    public boolean getMandatory()
    {
    	return this.mandatory;
    }

}
