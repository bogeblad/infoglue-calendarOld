//$Id: PortletActionConstants.java,v 1.1 2004/11/26 18:22:00 mattias Exp $
package com.opensymphony.webwork.portlet.alternative.action;

/**
 * Interface defining some constants used in the WebWork2 portlet implementation
 *
 * @author Nils-Helge Garli
 * @version: $LastChangedRevision: $ $LastChangedDate: $
 *
 */
public interface PortletActionConstants {
	/**
	 * Key used for looking up and storing the portlet phase
	 */
	String PHASE = "no.concrea.portlet.phase";
	/**
	 * Constant used for the render phase
	 * ({@link javax.portlet.Portlet#render(javax.portlet.RenderRequest, javax.portlet.RenderResponse)})
	 */
	Integer RENDER_PHASE = new Integer(1);
	/**
	 * Constant used for the event phase 
	 * ({@link javax.portlet.Portlet#processAction(javax.portlet.ActionRequest, javax.portlet.ActionResponse)})
	 */
	Integer EVENT_PHASE = new Integer(2);
	/**
	 * Key used for looking up and storing the {@link javax.portlet.PortletRequest}
	 */
	String REQUEST = "no.concrea.portlet.request";
	/**
	 * Key used for looking up and storing the {@link javax.portlet.PortletResponse}
	 */
	String RESPONSE = "no.concrea.portlet.response";
	/**
	 * Key used for looking up and storing the {@link javax.portlet.PortletConfig}
	 */
	String PORTLET_CONFIG = "no.concrea.portlet.config";
	/**
	 * Name of the action used as error handler
	 */
	String ERROR_ACTION = "errorHandler";
	/**
	 * Key used to store the exception used in the error handler
	 */
	String EXCEPTION_KEY = "no.concrea.portlet.ww2.exception";
}
