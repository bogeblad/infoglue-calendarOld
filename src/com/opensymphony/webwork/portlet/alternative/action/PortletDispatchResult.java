//$Id: PortletDispatchResult.java,v 1.8 2005/09/01 08:34:26 mattias Exp $
package com.opensymphony.webwork.portlet.alternative.action;

import java.util.Iterator;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.opensymphony.webwork.portlet.dispatcher.PortletDispatcher;
import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.ActionInvocation;
import com.opensymphony.xwork.Result;

/**
 * Result type that includes a JSP to render.
 *
 * @author Nils-Helge Garli
 * @version: $LastChangedRevision: $ $LastChangedDate: $
 *
 */
public class PortletDispatchResult implements Result {

	/**
	 * Logger instance.
	 */
	private static Log log = LogFactory.getLog(PortletDispatchResult.class);
	/**
	 * The JSP to dispatch to.
	 */
	private String dispatchTo = null;
	
	/**
	 * Execute the result. Obtains the {@link javax.portlet.PortletRequestDispatcher} from the
	 * {@link PortletContext} and includes the JSP.
	 * @see com.opensymphony.xwork.Result#execute(com.opensymphony.xwork.ActionInvocation)
	 */
	
	public void execute(ActionInvocation actionInvocation) throws Exception 
	{
		log.debug("execute");
		ActionContext ctx = ActionContext.getContext();
				
		PortletDispatcher dispatcher = (PortletDispatcher)ctx.get("com.opensymphony.webwork.portlet.dispatcher.PortletDispatcher");
		PortletContext context = dispatcher.getPortletContext();
		Object requestObject = ctx.get("com.opensymphony.xwork.dispatcher.HttpServletRequest");
		Object responseObject = ctx.get("com.opensymphony.xwork.dispatcher.HttpServletResponse");
		log.debug("requestObject:" + requestObject.getClass().getName());
		
		/*
        if(requestObject instanceof RenderRequest)
        {
        */
			RenderRequest req = (RenderRequest)requestObject;
	        RenderResponse res = (RenderResponse)responseObject;
	        log.debug("Remote user:" + req.getRemoteUser());
	        log.debug("Remote user:" + req.getUserPrincipal());
			log.debug("Including jsp " + dispatchTo);
		/*
			context.getRequestDispatcher(dispatchTo).include(req, res);
        }
		
		else
		{
		    ActionRequest req = (ActionRequest)requestObject;
		    ActionResponse res = (ActionResponse)responseObject;
	        context.getRequestDispatcher(dispatchTo).include(req, res);
		}
		*/
		try 
		{
			
		    context.getRequestDispatcher(dispatchTo).include(req, res);
			//cfg.getPortletContext().getRequestDispatcher(dispatchTo).include(req, res);
		}
		catch(Throwable t) {
			log.error("Error rendering JSP: " + t.getClass().getName());
			//TODO - to many causes... why
			/*
			Throwable cause = t;//.getCause();
			while(cause != null) {
				log.error("Nested: " + t.getClass().getName(), cause);
				//cause = cause.getCause();
				log.debug("Next exception: " + cause);
			}
			*/
		}
	}
	/**
	 * Get the name of the JSP to include
	 * @return Returns the dispatchTo.
	 */
	public String getDispatchTo() {
		return dispatchTo;
	}
	/**
	 * Set the name of the JSP to include
	 * @param dispatchTo The dispatchTo to set.
	 */
	public void setDispatchTo(String dispatchTo) {
		this.dispatchTo = dispatchTo;
	}
}
