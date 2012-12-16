/**
 * Copyright 2004 Custine Technology Group, Inc.
 *
 * User: ccustine
 * Date: Aug 7, 2004
 * Time: 6:09:54 PM
 */
package com.opensymphony.webwork.portlet.dispatcher;

import com.opensymphony.webwork.ServletActionContext;
import com.opensymphony.webwork.WebWorkStatics;
import com.opensymphony.webwork.util.AttributeMap;
import com.opensymphony.webwork.views.velocity.VelocityManager;
import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.ActionProxy;
import com.opensymphony.xwork.ActionProxyFactory;
import com.opensymphony.xwork.config.ConfigurationException;
import com.opensymphony.xwork.config.ConfigurationManager;
import com.opensymphony.xwork.config.providers.XmlConfigurationProvider;
import com.opensymphony.xwork.interceptor.component.ComponentConfiguration;
import com.opensymphony.xwork.interceptor.component.ComponentInterceptor;
import com.opensymphony.xwork.interceptor.component.ComponentManager;
import com.opensymphony.xwork.interceptor.component.DefaultComponentManager;
import com.opensymphony.xwork.util.LocalizedTextUtil;

import net.sf.cglib.proxy.Enhancer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.event.LoadEvent;
import org.hibernate.event.LoadEventListener;
import org.hibernate.event.def.DefaultLoadEventListener;
import org.hibernate.proxy.HibernateProxy;

import javax.portlet.*;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class PortletDispatcher extends GenericPortlet implements WebWorkStatics
{
    protected static final Log log = LogFactory.getLog(PortletDispatcher.class);

    private static final String PORTLET_DISPATCHER = "com.opensymphony.webwork.portlet.dispatcher.PortletDispatcher";

    public static final String ACTION_CONTEXT = "com.opensymphony.webwork.portlet.ActionContext";

   
    public static HashMap createContextMap(Map requestMap, Map parameterMap,
            Map sessionMap, Map applicationMap, PortletRequest request,
            PortletResponse response, PortletConfig portletConfig)
    {        
        HashMap extraContext = new HashMap();
        extraContext.put(ActionContext.PARAMETERS, parameterMap);
        extraContext.put(ActionContext.SESSION, sessionMap);
        extraContext.put(ActionContext.APPLICATION, applicationMap);
        extraContext.put(ActionContext.LOCALE, request.getLocale());

        extraContext.put(HTTP_REQUEST, request);
        extraContext.put(HTTP_RESPONSE, response);
        extraContext.put(SERVLET_CONFIG, portletConfig);
        extraContext.put(ComponentInterceptor.COMPONENT_MANAGER, request
                .getAttribute(ComponentManager.COMPONENT_MANAGER_KEY));

        // helpers to get access to request/session/application scope
        extraContext.put("request", requestMap);
        extraContext.put("session", sessionMap);
        extraContext.put("application", applicationMap);
        extraContext.put("parameters", parameterMap);

        AttributeMap attrMap = new AttributeMap(extraContext);
        extraContext.put("attr", attrMap);

        return extraContext;
    }

    public void init(PortletConfig config) throws PortletException
    {
        log.debug("Initializing portlet"
                + config.getPortletContext().getPortletContextName() + "::"
                + config.getPortletName());
        super.init(config);

        String xworkConfig = (String) config.getInitParameter("xwork-config");
        if (xworkConfig != null)
        {
            log.debug("Loading xwork config file " + xworkConfig
                    + " for portlet " + config.getPortletName());
            ConfigurationManager
                    .addConfigurationProvider(new XmlConfigurationProvider(
                            xworkConfig));
        }
        // This init mothod requires a modification to VelocityManager to accept
        // the PortletContext
        // I think this is dependent on WebWork CVS or a recent release
        //TODO
        
        //VelocityManager.getInstance().init(config.getPortletContext());
        
        LocalizedTextUtil
                .addDefaultResourceBundle("com/opensymphony/webwork/webwork-messages");
        // store a reference to ourself into the SessionContext so that we can
        // generate a PageContext
        //    config.getPortletContext().setAttribute("webwork.servlet", this);
    }
    
    public void processAction(ActionRequest actionRequest,
            ActionResponse actionResponse) throws PortletException, IOException
    {
        log.debug("Got to processAction!!");
        try
        {
            serviceAction(actionRequest, actionResponse, getPortletName(),
                    getActionName(actionRequest),
                    getRequestMap(actionRequest),
                    getParameterMap(actionRequest),
                    getSessionMap(actionRequest), getPortletApplicationMap());
        } 
        catch (Exception e)
        {
            if (e instanceof PortletException)
            {
                throw new PortletException(
                        "Error dispatching to WebWork Portlet",
                        (PortletException) e);
            }
            throw new PortletException("Error dispatching to WebWork Portlet");
        }

    }
    
    public void render(RenderRequest request, RenderResponse response) throws PortletException, java.io.IOException
    {
        response.setTitle(getTitle(request));
        
        log.debug("Rendering View Mode");
        
        try
        {
            //response.setContentType("text/html");
            //response.getWriter().println("doView APAPAPAPA <a href");
            
            service(request, response);
        } 
        catch (Exception e)
        {
            e.printStackTrace();
            throw new PortletException("Error dispatching to WebWork Portlet");
        }
    }

    protected void doDispatch(RenderRequest renderRequest, RenderResponse renderResponse) throws PortletException, IOException
    {

    }

    public void service(PortletRequest request, PortletResponse response) throws PortletException
    {
    	log.debug("Getting to serviceAction");
        
        serviceAction(request, response, getPortletName(),
                getActionName(request),
                getRequestMap(request), getParameterMap(request),
                getSessionMap(request), getPortletApplicationMap());
    }

    public void serviceAction(PortletRequest request, PortletResponse response,
            String namespace, String actionName, Map requestMap,
            Map parameterMap, Map sessionMap, Map applicationMap)
    {
        
        addSession(request);
        
        log.debug("Getting to serviceAction with namespace:" + namespace + " actionName:" + actionName);

        HashMap extraContext = createContextMap(requestMap, parameterMap, sessionMap, applicationMap, request, response, getPortletConfig());
        
        if (namespace.length() > 0)
        {
            namespace = new StringBuffer("/").append(namespace).toString();

        }
        
        if(log.isDebugEnabled())
        	log.debug(sessionMap);
        if(log.isDebugEnabled())
        	log.debug(parameterMap);
        extraContext.put(PORTLET_DISPATCHER, this);
        if(log.isDebugEnabled())
        	log.debug("Getting to beginning of serviceAction");
        if(log.isDebugEnabled())
        	log.debug(new StringBuffer().append("Namespace: ").append(namespace).append("\nAction: ").append(actionName).append("\nPortlet Name: ").append(getPortletName()).toString());
        
        try
        {
            if (request instanceof RenderRequest)
            {
                log.debug("Yes - it was a render request....");
                if (request.getPortletSession(true).getAttribute(ACTION_CONTEXT) != null)
                {
                    log.debug("Getting key for old action");
                    // String oldActionKey = request.getParameter("com.opensymphony.webwork.portlet.ActionContextKey");
                    ActionContext oldAction = (ActionContext) request.getPortletSession().getAttribute(ACTION_CONTEXT);
                    extraContext.put(ActionContext.VALUE_STACK, oldAction.getValueStack());
                    extraContext.put(ActionContext.PARAMETERS, oldAction.getParameters());
                    request.getPortletSession().removeAttribute(ACTION_CONTEXT);
                    log.debug("Should have removed the key");
                }
            }
            String actionOverride = (String) request.getParameter("xwork.action");
            if (actionOverride != null)
            {
                log.debug("Override action is " + actionOverride);
                actionName = actionOverride;
            }
            
            
            ActionProxy proxy = ActionProxyFactory.getFactory().createActionProxy(namespace, actionName, extraContext);
            request.setAttribute("webwork.valueStack", proxy.getInvocation().getStack());
            
            proxy.execute();
            
            //TODO: This code is a B.S. cheat by me. Need to figure out if this is even needed.
            if (request instanceof ActionRequest)
            {
                request.getPortletSession().setAttribute(ACTION_CONTEXT, proxy.getInvocation().getInvocationContext());
            }
            request.removeAttribute("webwork.valueStack");
        } 
        catch (ConfigurationException e)
        {
            log.error("Could not find action:" + e.getMessage());
            log.warn("Could not find action:" + e.getMessage(), e);
            //sendError(request, response, HttpServletResponse.SC_NOT_FOUND, e);
            disposeSession(request);
        } 
        catch (Exception e)
        {
            log.error("Could not execute action:" + e.getMessage());
            log.warn("Could not execute action:" + e.getMessage(), e);
            //sendError(request, response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e);
            disposeSession(request);
        }
        catch (Throwable e)
        {
            log.error("Could not execute action:" + e.getMessage());
            log.warn("Could not execute action:" + e.getMessage(), e);
            //sendError(request, response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e);
            disposeSession(request);
        }
    }

	/**
     * Build the name of the action from the request. Checks the request parameter
     * <code>action</action> for the action name
     *
     * @param request the HttpServletRequest object.
     * @return the name or alias of the action to execute.
     */
    protected String getActionName(ActionRequest request) {
        String actionName = request.getParameter("action");
        if(actionName == null) {
        	actionName = "default";
        }
        log.debug("actionName = " + actionName);
        return actionName;
    } 
    
	/**
     * Build the name of the action from the request. Checks the request parameter
     * <code>action</action> for the action name
     *
     * @param request the HttpServletRequest object.
     * @return the name or alias of the action to execute.
     */
    protected String getActionName(PortletRequest request) {
        String actionName = request.getParameter("action");
        log.debug("request actionName = " + actionName);
        if(actionName == null) {
            actionName = (String) getPortletConfig().getInitParameter(request.getPortletMode().toString());
        }
        log.debug("actionName = " + actionName);
        return actionName;
    } 
    
    protected String getModeActionName(PortletMode mode) throws PortletException
    {
        log.debug("PortletMode is: " + mode.toString());
        //  TODO: Move this exception throwing somewhere else.  Nested too deep.
        String actionName = (String) getPortletConfig().getInitParameter(mode.toString());

        log.debug("actionName in getModeActionName:" + actionName);
        
        if (actionName == null)
        {
            throw new PortletException("Can't find portlet init param for " + mode.toString() + " action");
        }
        return actionName;
    }

    protected Map getPortletApplicationMap()
    {
        return new PortletApplicationMap(getPortletContext());
    }

    protected Map getParameterMap(PortletRequest request)
    {
        return request.getParameterMap();
    }

    protected Map getRequestMap(PortletRequest request)
    {
        return new PortletRequestMap(request);
    }

    protected Map getSessionMap(PortletRequest request)
    {
        return new PortletSessionMap(request);
    }

    protected void sendError(PortletRequest request, PortletResponse response, int code, Exception e)
    {
        request.setAttribute("javax.servlet.error.exception", e);

        // for compatibility
        request.setAttribute("javax.servlet.jsp.jspException", e);
    }

    private Session session;
	private Transaction transaction;
	private static SessionFactory factory = null;

	static 
	{
		try
		{
			Configuration cfg = new Configuration();
			cfg.configure();

			cfg.setListeners("load", new LoadEventListener [] {new DefaultLoadEventListener(), new LoadEventListener() {
				
				public void onLoad(LoadEvent event, LoadType loadType) throws HibernateException {
	                Object obj = event.getResult();
	                if (obj instanceof HibernateProxy) {
	                	Enhancer.registerCallbacks(obj.getClass(),null);
	                }
	            }

	        }});

			factory = cfg.buildSessionFactory();
			
		}
		catch (Exception e) 
		{
			System.out.println("Could not connect to database:" + e.getMessage());
		}
	}
	
    private void addSession(PortletRequest request)
    {
        log.debug("Initializing session:" + factory);
	    
        if(factory != null)
        {
		    Session session = factory.openSession();
		    log.debug("Initializing session:" + session);
		    transaction = session.beginTransaction();
			
	        request.setAttribute("HIBERNATE_SESSION", session);
	        request.setAttribute("HIBERNATE_TRANSACTION", transaction);
        }
        else
        {
	        request.setAttribute("DATABASE_CONFIGURATION_ERROR", "No database connection could be made");
        }
    }
    
	public Session getSession(PortletRequest request) throws HibernateException 
	{
	    return (Session)request.getAttribute("HIBERNATE_SESSION");
	}

	public Transaction getTransaction(PortletRequest request) throws HibernateException 
	{
	    return (Transaction)request.getAttribute("HIBERNATE_TRANSACTION");
	}

	public void emptySession(PortletRequest request) throws HibernateException 
	{
	    request.removeAttribute("HIBERNATE_SESSION");
	}

	public void emptyTransaction(PortletRequest request) throws HibernateException 
	{
	    request.removeAttribute("HIBERNATE_TRANSACTION");
	}

	public void emptyValueStack(PortletRequest request) throws HibernateException 
	{
		request.removeAttribute("webwork.valueStack");
	}
	
	
    boolean rollBackOnly = false;
	
	public void disposeSession(PortletRequest request) throws HibernateException {
		
		log.debug("disposing");

		if (getSession(request)==null) return;

		if (rollBackOnly) 
		{
			try 
			{
				log.debug("rolling back");
				if (getTransaction(request)!=null) getTransaction(request).rollback();
			}
			catch (HibernateException e) 
			{
			    log.error("error during rollback", e);
				throw e;
			}
			finally 
			{
				try
				{
				    getSession(request).close();
				}
				finally
				{
					emptySession(request);
					emptyTransaction(request);
					emptyValueStack(request);
				}
			}
		}
		else 
		{
			try 
			{
				log.debug("committing");
				if (getTransaction(request)!=null) 
				{
				    getTransaction(request).commit();
				}
			}
			catch (HibernateException e) 
			{
			    log.error("error during commit", e);
				getTransaction(request).rollback();
				throw e;
			}
			finally 
			{
				try
				{
				    getSession(request).close();
				}
				finally 
				{
					emptySession(request);
					emptyTransaction(request);
					emptyValueStack(request);
				}
			}
		}
	}
	
    protected PortletContext getPortletContext(PortletSession session) 
    {
        return session.getPortletContext();
    }
    
    protected DefaultComponentManager createComponentManager() 
    {
        return new DefaultComponentManager();
    }

}