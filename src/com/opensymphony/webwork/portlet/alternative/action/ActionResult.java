package com.opensymphony.webwork.portlet.alternative.action;

import java.util.Iterator;
import java.util.StringTokenizer;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.ActionInvocation;
import com.opensymphony.xwork.Result;
import com.opensymphony.xwork.util.OgnlValueStack;
import com.opensymphony.xwork.util.TextParseUtil;

/**
 * Result type set from an {@link com.opensymphony.webwork.portlet.alternative.action.EventAction} indicating a
 * {@link com.opensymphony.webwork.portlet.alternative.action.RenderAction} to execute in the render phase.
 *
 * @author Nils-Helge Garli
 * @version: $LastChangedRevision: $ $LastChangedDate: $
 *
 */
public class ActionResult implements Result 
{
	
	/**
	 * Logger instance
	 */
	private static Log log = LogFactory.getLog(ActionResult.class);
	/**
	 * Name of view action to prepare
	 */
	private String viewAction = null;
	protected boolean parse = true;
	
	/**
	 * Execute the result. Simply sets the <code>action</code> render parameter with the 
	 * <code>viewAction</code> indicated.
	 * @see com.opensymphony.xwork.Result#execute(com.opensymphony.xwork.ActionInvocation)
	 */
	public void execute(ActionInvocation actionInvocation) throws Exception {
		log.debug("execute");
		log.debug("viewAction = " + viewAction);

		log.debug("***************************************");
		log.debug("viewAction = " + viewAction);
		ActionRequest req = (ActionRequest)actionInvocation.getInvocationContext().get("com.opensymphony.xwork.dispatcher.HttpServletRequest");
		log.debug("Request error:" + req.getAttribute("ErrorMessage"));

		if (parse) {
            OgnlValueStack stack = ActionContext.getContext().getValueStack();
            viewAction = TextParseUtil.translateVariables(viewAction, stack);
        }
		
		log.debug("viewAction = " + viewAction);

		if(StringUtils.isNotEmpty(viewAction)) {
			//ActionResponse response = (ActionResponse)actionInvocation.getInvocationContext().getContextMap().get(PortletActionConstants.RESPONSE);
			ActionRequest request = (ActionRequest)actionInvocation.getInvocationContext().getContextMap().get("com.opensymphony.xwork.dispatcher.HttpServletRequest");
			ActionResponse response = (ActionResponse)actionInvocation.getInvocationContext().getContextMap().get("com.opensymphony.xwork.dispatcher.HttpServletResponse");
			
			if (viewAction.indexOf('?') != -1) 
			{
	            convertQueryParamsToRenderParams(response, viewAction.substring(viewAction.indexOf('?') + 1));
	            viewAction = viewAction.substring(0, viewAction.indexOf('?'));
	        }
			
			if (viewAction.endsWith(".action")) 
			{
	            // View is rendered with a view action...luckily...
			    viewAction = viewAction.substring(0, viewAction.lastIndexOf("."));
			    response.setRenderParameter("action", viewAction);
	        } 
			/*
			else 
	        {
	            // View is rendered outside an action...uh oh...
	            response.setRenderParameter("action", "renderDirect");
	            response.setRenderParameter("location", viewAction);
	        }
	        */
		}

		log.debug("viewAction = " + viewAction);
		log.debug("***************************************");
	}
	
	/**
	 * Get the view action to prepare.
	 * @return Returns the viewAction.
	 */
	
	public String getViewAction() 
	{
		return viewAction;
	}
	
	/**
	 * Set the name of the view action to execute in the render phase
	 * @param viewAction The viewAction to set.
	 */
	
	public void setViewAction(String viewAction) 
	{
		this.viewAction = viewAction;
	}
	
    /**
     * Set parse to <tt>true</tt> to indicate that the location should be parsed as an OGNL expression. This
     * is set to <tt>true</tt> by default.
     *
     * @param parse <tt>true</tt> if the location parameter is an OGNL expression, <tt>false</tt> otherwise.
     */
	
    public void setParse(boolean parse) 
    {
        this.parse = parse;
    }
    
    /**
     * @param string
     */
    
    protected static void convertQueryParamsToRenderParams(ActionResponse response, String queryParams) 
	{
    	StringTokenizer tok = new StringTokenizer(queryParams, "&");
    	while (tok.hasMoreTokens()) 
    	{
            String token = tok.nextToken();
            String key = token.substring(0, token.indexOf('='));
            String value = token.substring(token.indexOf('=') + 1);
            response.setRenderParameter(key, value);
        }
	}

}
