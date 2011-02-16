package org.infoglue.calendar.taglib;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.taglibs.standard.tag.el.core.ExpressionUtil;

public abstract class AbstractTag extends TagSupport 
{
	public AbstractTag()
	{
		super();
	}

	protected void setResultAttribute(Object value)
	{
		if(value == null)
			pageContext.removeAttribute(id);
		else
			pageContext.setAttribute(id, value);
		
	}
	
	protected Object evaluate(String tagName, String attributeName, String expression, Class expectedType) throws JspException
	{
		return ExpressionUtil.evalNotNull(tagName, attributeName, expression, expectedType, this, pageContext);
	}

	protected Integer evaluateInteger(String tagName, String attributeName, String expression) throws JspException
	{
		return (Integer) evaluate(tagName, attributeName, expression, Integer.class);
	}

	protected Long evaluateLong(String tagName, String attributeName, String expression) throws JspException
	{
		return (Long) evaluate(tagName, attributeName, expression, Long.class);
	}

	protected String evaluateString(String tagName, String attributeName, String expression) throws JspException
	{
		return (String) evaluate(tagName, attributeName, expression, String.class);
	}

	protected String[] evaluateStringArray(String tagName, String attributeName, String expression) throws JspException
	{
	    Object o = evaluate(tagName, attributeName, expression, String[].class);
	    return (String[]) evaluate(tagName, attributeName, expression, String[].class);
	}

	protected Collection evaluateCollection(String tagName, String attributeName, String expression) throws JspException
	{
		return (Collection) evaluate(tagName, attributeName, expression, Collection.class);
	}

	protected List evaluateList(String tagName, String attributeName, String expression) throws JspException
	{
		return (List) evaluate(tagName, attributeName, expression, List.class);
	}

	protected Set evaluateSet(String tagName, String attributeName, String expression) throws JspException
	{
		return (Set) evaluate(tagName, attributeName, expression, Set.class);
	}

}
