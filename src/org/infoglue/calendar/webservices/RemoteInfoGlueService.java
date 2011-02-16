package org.infoglue.calendar.webservices;

import javax.servlet.http.HttpServletRequest;

import org.apache.axis.MessageContext;
import org.apache.axis.transport.http.HTTPConstants;
import org.apache.log4j.Logger;


/**
 * This class is responsible for letting an external application call InfoGlue
 * API:s remotely. It handles api:s to manage contents and associated entities.
 * 
 * @author Mattias Bogeblad
 */

public class RemoteInfoGlueService 
{
    private final static Logger logger = Logger.getLogger(RemoteInfoGlueService.class.getName());

    protected HttpServletRequest getRequest() 
    {
    	HttpServletRequest request = (HttpServletRequest)MessageContext.getCurrentContext().getProperty(HTTPConstants.MC_HTTP_SERVLETREQUEST);
    	return request;
	}


}
