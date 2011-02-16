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

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.jsp.JspException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoglue.calendar.entities.BaseEntity;

import org.infoglue.common.security.beans.InfoGluePrincipalBean;



/**
 * 
 */
public class CheckBoxFieldTag extends AbstractCalendarTag 
{
    private static Log log = LogFactory.getLog(CheckBoxFieldTag.class);

	private static final long serialVersionUID = 3617579309963752240L;
	
	private String name;
	private String labelCssClass = "";
	private String cssClass = "";
	private String size = "";
	private String multiple = "false";
	private String[] selectedValues;
	private List selectedValueList;
	private Set selectedValueSet;
	private Map values;
	private String label;
	private List fieldErrors;
	private Object errorAction = null;

	private boolean mandatory = false;

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
	public CheckBoxFieldTag() 
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
	    
	    if(!skipRowDiv)
	    	sb.append(rowDivHTMLStart);
		            
	    if(this.label != null)
	    {
			sb.append("<label>" + this.label + "</label>" + (getMandatory() ? "<span class=\"" + requiredLabelClass + "\">" + requiredText + "</span>" : "") + " " + errorMessage + "<br/>");
	    }
	    else
	        sb.append("<label>" + this.name + "</label>" + (getMandatory() ? "<span class=\"" + requiredLabelClass + "\">" + requiredText + "</span>" : "") + " " + errorMessage + "<br/>");

        if(values != null)
        {
	        Iterator valuesIterator = values.keySet().iterator();
	        while(valuesIterator.hasNext())
		    {
	            String id 			= (String)valuesIterator.next();
	            String optionText 	= (String)values.get(id);

	            log.info("Id:" + id);
	            log.info("optionText:" + optionText);

                log.info("selectedValue:" + selectedValues);

	            String checked = "";

	            if(selectedValues != null)
	            {
	            	log.info("selectedValues.length:" + selectedValues.length);
		            for(int i=0; i<selectedValues.length; i++)
		            {
		            	log.info("selectedValues[i]:" + selectedValues[i]);
		                if(id.equalsIgnoreCase(selectedValues[i]))
		                    checked = " checked=\"1\"";
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
	    	            else
	    	            {
	    	                BaseEntity selValue = (BaseEntity)selObj;
	    	                selId = selValue.getId().toString();
	    	            }
	    	            
		                if(id.equalsIgnoreCase(selId))
		                    checked = " checked=\"1\"";
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
	    	            else
	    	            {
	    	                BaseEntity selValue = (BaseEntity)selObj;
	    	                selId = selValue.getId().toString();
	    	            }
	    	            
		                if(id.equalsIgnoreCase(selId))
		                    checked = " checked=\"1\"";
		            }
	            }
	            
	    		sb.append("<input name=\"" + name + "\" value=\"" + id + "\" class=\"\" type=\"checkbox\" id=\"" + name + "\"" + checked + "><label for=\"" + name + "\"/> " + this.getLabel(optionText) + "</label><br/>");
	        }
        }
	    if(!skipRowDiv)
	    	sb.append(rowDivHTMLEnd);

        write(sb.toString());
	    
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
    
    public void setName(String name)
    {
        Object o = findOnValueStack(name);
        if(o != null) 
            this.name = o.toString();
        else
            this.name = name;
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

    public void setMultiple(String multiple)
    {
        this.multiple = multiple;
    }

    public void setSelectedValues(String selectedValues) throws JspException
    {
        Object o = findOnValueStack(selectedValues);
        if(o != null)
        {
            if(o instanceof String[])
                this.selectedValues = (String[])o;
            else
                this.selectedValues = new String[] {o.toString()};
        }
        else
            this.selectedValues = null;
        //this.selectedValues = evaluateStringArray("SelectTag", "selectedValues", selectedValues);
    }

    public void setSelectedValue(String selectedValue) throws JspException
    {
        Object o = findOnValueStack(selectedValue);
        if(o != null) 
            this.selectedValues = new String[] {o.toString()};
        else
            this.selectedValues = null;
        
        //this.selectedValues = new String[] {evaluateString("SelectTag", "selectedValue", selectedValue)};
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

    public void setValueMap(String valueMap) throws JspException
    {
        Object o = findOnValueStack(valueMap);
        if(o != null) 
        {
            this.values = (Map)o;
        }
        else
        {
            this.values = null;
        }
    }

    
    public void setSize(String size)
    {
        this.size = size;
    }
    
    public void setLabelCssClass(String labelCssClass)
    {
        this.labelCssClass = labelCssClass;
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
