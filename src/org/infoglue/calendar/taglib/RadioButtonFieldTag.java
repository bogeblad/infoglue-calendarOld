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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.jsp.JspException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * 
 */
public class RadioButtonFieldTag extends AbstractCalendarTag 
{
    private static Log log = LogFactory.getLog(RadioButtonFieldTag.class);

	private static final long serialVersionUID = 3617579309963752240L;
	
	private boolean mandatory = false;

	private String name;
	private String labelCssClass = "";
	private String cssClass = "";
	private String selectedValue;
	private Map values;
	private String label;
	private List fieldErrors;
	private Object errorAction = null;
	
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
	public RadioButtonFieldTag() 
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
	            selectedValue = (String)obj;
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
	            log.info("Id:" + id);
	            String optionText 	= (String)values.get(id);
	            log.info("optionText:" + optionText);

                log.info("selectedValue:" + selectedValue);
	            String checked = "";
	            if(selectedValue != null)
	            {
	                if(id.equalsIgnoreCase(selectedValue))
	                    checked = " checked=\"checked\"";
	            }
	            
	    		sb.append("<input name=\"" + name + "\" value=\"" + id + "\" class=\"\" type=\"radio\" id=\"" + name + "\"" + checked + "><label for=\"" + name + "\"> " + this.getLabel(optionText) + "</label><br/>");
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

    public void setSelectedValue(String selectedValue) throws JspException
    {
    	log.info("Setting selectedValue:" + selectedValue);
        Object o = findOnValueStack(selectedValue);
        if(o != null) 
            this.selectedValue = o.toString();
        else
            this.selectedValue = null;        
        log.info("Setting selectedValue:" + this.selectedValue);
    	log.debug("Setting selectedValue:" + this.selectedValue);        
    }
    
    public void setLabelCssClass(String labelCssClass)
    {
        this.labelCssClass = labelCssClass;
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
