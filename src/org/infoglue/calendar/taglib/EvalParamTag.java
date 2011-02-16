/*
 * Copyright 2003,2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/* 

 */

package org.infoglue.calendar.taglib;

import javax.portlet.PortletURL;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.pluto.tags.BasicURLTag;
import org.apache.pluto.tags.ParamTag;


/**
 ** Supporting class for the <CODE>param</CODE> tag.
 ** defines a parameter that can be added to a <CODE>actionURL</CODE> or
 ** a <CODE>renderURL</CODE>
 ** <BR>The following attributes are mandatory
 ** <UL>
 ** <LI><CODE>name</CODE>
 ** <LI><CODE>value</CODE>
 ** </UL>
 **/
public class EvalParamTag extends AbstractCalendarTag
{
    private String name;
    private String value;
    private String type = "String";
    
    /**
     * Processes the <CODE>param</CODE> tag.
     * @return <CODE>SKIP_BODY</CODE>
     */
    public int doStartTag() throws JspException
    {
        BasicURLTag urlTag = (BasicURLTag)findAncestorWithClass(this, BasicURLTag.class);
        if (urlTag == null)
        {
            throw new JspException("the 'param' Tag must have actionURL or renderURL as a parent");
        }
        PortletURL url = urlTag.getUrl();

        if (getName() != null)
        {
            url.setParameter(getName(),getValue());
        }

        return SKIP_BODY;
    }

    /**
     * Returns the name.
     * @return String
     */
    public String getName()
    {
        return name;
    }

    /**
     * Returns the value.
     * @return String
     */
    public String getValue()
    {
        if (value == null)
        {
            value = "";
        }
        return value;
    }

    /**
     * Sets the name.
     * @param name The name to set
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Sets the value.
     * @param value The value to set
     */
    public void setValue(String value) throws JspException
    {
        if(type.equalsIgnoreCase("Integer"))
            this.value = evaluateInteger("EvalParamTag", "value", value).toString();
        else if(type.equalsIgnoreCase("Integer"))
            this.value = evaluateLong("EvalParamTag", "value", value).toString();
        else 
            this.value = evaluateString("EvalParamTag", "value", value);
    }

    public void setType(String type)
    {
        this.type = type;
    }
}
