package org.infoglue.calendar.actions;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.portlet.PortletFileUpload;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoglue.calendar.controllers.ContentTypeDefinitionController;
import org.infoglue.calendar.controllers.EntryController;
import org.infoglue.calendar.controllers.EventController;
import org.infoglue.calendar.controllers.EventTypeController;
import org.infoglue.calendar.entities.Entry;
import org.infoglue.calendar.entities.Event;
import org.infoglue.calendar.entities.EventType;
import org.infoglue.calendar.entities.EventVersion;
import org.infoglue.calendar.util.EntrySearchResultfilesConstructor;
import org.infoglue.common.contenttypeeditor.entities.ContentTypeAttribute;
import org.infoglue.common.util.PropertyHelper;

import com.opensymphony.webwork.ServletActionContext;
import com.opensymphony.xwork.Action;

public class ComposeEmailAction extends CalendarAbstractAction 
{

	private static Log log = LogFactory.getLog(ComposeEmailAction.class);

	private Long eventId;
	private String attachParticipants = "false";
	
    private String searchHashCode = "";

	private String emailAddresses = "";
	
	private Long[] searchEventId;
	private String searchFirstName;
	private String searchLastName;
	private String searchEmail;
    private String[] categoryId;
    private String[] locationId;
    private boolean onlyFutureEvents = true;
    private Map categoryAttributesMap = new HashMap();
    private String andSearch = "false";

	private String subject;

	private String message;

	private List attachments = new LinkedList();

	private String send;

	private String cancel;

	private String remove;

	public String execute() throws Exception 
	{
		try
		{
			log.debug("execute()");
			if (PortletFileUpload.isMultipartContent(ServletActionContext.getRequest()))
			{
				log.debug("This is a multipart request.");
				
				DiskFileItemFactory dfif = new DiskFileItemFactory();
				PortletFileUpload pfu = new PortletFileUpload(dfif);
				List params = pfu.parseRequest(ServletActionContext.getRequest());
				for (Iterator it = params.iterator(); it.hasNext();)
				{
					DiskFileItem dfi = (DiskFileItem) it.next();
					if (dfi.isFormField())
					{
						String paramName = dfi.getFieldName();
						String paramValue = dfi.getString();
						if (paramName.equals("emailAddresses"))
						{
							emailAddresses = paramValue;
						} 
						else if (paramName.equals("searchHashCode"))
						{
							searchHashCode = paramValue;
						} 
						else if (paramName.equals("subject"))
						{
							subject = paramValue;
						} 
						else if (paramName.equals("message"))
						{
							message = paramValue;
						} 
						else if (paramName.equals("attachParticipants"))
						{
							attachParticipants = paramValue;
						} 
						else if (paramName.equals("eventId") && paramValue.length() > 0)
						{
							eventId = new Long(paramValue);
						} 
						else if (paramName.equals("attachments"))
						{
							log.debug("Got param attachments: " + paramValue);
							StringTokenizer st = new StringTokenizer(paramValue, ",[]", false);
							while (st.hasMoreTokens())
							{
								String fileName = st.nextToken();
								log.debug("Attachment: " + fileName);
								attachments.add(new File(fileName));
							}
						}
					} 
					else
					{
						String fileName = dfi.getName();
						File f = new File(getTempFilePath() + File.separator + fileName);
						log.debug("Attaching file: " + f.getPath());
						dfi.write(f);
						attachments.add(f);
					}
				}
			} 
			else
			{
				if (cancel != null)
				{
					// cancel sending message
					return "finished";
				} 
				else if (send != null)
				{
			        // should we attach participants?
			        if(eventId != null && (attachParticipants != null && attachParticipants.equalsIgnoreCase("true"))) 
			        {
			        	Event event = EventController.getController().getEvent(eventId, getSession());
			        	EventVersion eventVersion = getEventVersion(event);
			        	
			        	Set entries = event.getEntries();
			        	if(entries != null && entries.size() > 0)
			        	{
			        		List resultValues = new ArrayList();
			        		String entryResultValues = "PDF";
			                
				        	HttpServletRequest request = ServletActionContext.getRequest();
				        	Map parameters = new HashMap();
				        	parameters.put("headLine", "Entries for event " + eventVersion.getName() + " (" + this.formatDate(event.getStartDateTime().getTime(), "yyyy-MM-dd") + ")");

				        	EventType eventType = EventTypeController.getController().getEventType(event.getEntryFormId(), getSession());
				    		List attributes = ContentTypeDefinitionController.getController().getContentTypeAttributes(eventType.getSchemaValue());

				    		List attributeNames = new ArrayList();
				    		Iterator attributesIterator = attributes.iterator();
				    		while(attributesIterator.hasNext())
				    		{
				    			ContentTypeAttribute attribute = (ContentTypeAttribute)attributesIterator.next();
				    			attributeNames.add(attribute.getName());
				    		}
				        	parameters.put("attributes", attributes);
				        	parameters.put("attributeNames", attributeNames);
				        	
				        	EntrySearchResultfilesConstructor results = new EntrySearchResultfilesConstructor(parameters, entries, getTempFilePath(), request.getScheme(), request.getServerName(), request.getServerPort(), resultValues, this, event.getEntryFormId().toString());
				        	Map searchResultFiles = results.getFileResults();
				        	
				        	String fileName = (String)searchResultFiles.get("PDF");
				        	if(fileName != null)
				        	{
				        		File f = new File(fileName.trim());
				        		log.debug("f:" + f.exists());
				        		attachments.add(f);
				        	}
			        	}
			        }
	
					// time to send
					EntryController.getController().mailEntries(emailAddresses, subject, message, attachments, this.getLocale(), this.getSession());
					return "finished";
				}
				
				emailAddresses = (String)ServletActionContext.getRequest().getSession().getAttribute("request_" + searchHashCode + "_emailAddresses");
				if(emailAddresses == null)
					emailAddresses = "";
				
	            Long[] searchEventId = (Long[])ServletActionContext.getRequest().getSession().getAttribute("request_" + searchHashCode + "_searchEventId");
	            if(searchEventId != null && searchEventId.length == 1)
	            {
	            	this.eventId = searchEventId[0];
	            	//this.attachParticipants = true;
	            }
			}
			return Action.SUCCESS;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return Action.ERROR;
		}
	}

	/**
	 * @return Returns the emailAddresses.
	 */
	public String getEmailAddresses() 
	{
		return emailAddresses;
	}

	/**
	 * @param emailAddresses
	 *            The emailAddresses to set.
	 */
	
	public void setEmailAddresses(String emailAddresses) 
	{
		// aaargh, ugly fix, but I didn't have time to dig deeper
		// the reason is I couldn't pass this parameter with ; since
		// that broke something on the way so I changed all ; to , and
		// now I have to change them back.
		if( emailAddresses.indexOf(",") > -1 ) {
			emailAddresses = emailAddresses.replaceAll( ",", ";" );
		}
		this.emailAddresses = emailAddresses;
	}
	

	/**
	 * @return Returns the message.
	 */
	public String getMessage() 
	{
		return message;
	}

	/**
	 * @param message
	 *            The message to set.
	 */
	public void setMessage(String message) 
	{
		this.message = message;
	}

	/**
	 * @return Returns the subject.
	 */
	public String getSubject() 
	{
		return subject;
	}

	/**
	 * @param subject
	 *            The subject to set.
	 */
	public void setSubject(String subject) 
	{
		this.subject = subject;
	}

	/**
	 * @return Returns the attachments.
	 */
	public List getAttachments() 
	{
		return attachments;
	}

	public void setAttachments(List attachments) 
	{
		log.debug("Got list param attachments: " + attachments);
		StringTokenizer st = new StringTokenizer(attachments.toString(), ",[]",
				false);
		while (st.hasMoreTokens()) {
			String fileName = st.nextToken();
			this.attachments.add(new File(fileName.trim()));
		}
	}

	/**
	 * @return Returns the cancel.
	 */
	public String getCancel() 
	{
		return cancel;
	}

	/**
	 * @param cancel
	 * The cancel to set.
	 */
	public void setCancel(String cancel) 
	{
		this.cancel = cancel;
	}

	/**
	 * @return Returns the send.
	 */
	public String getSend() 
	{
		return send;
	}

	/**
	 * @param send
	 * The send to set.
	 */
	public void setSend(String send) 
	{
		log.debug("Got parameter send: " + send);
		this.send = send;
	}

	/**
	 * @return Returns the remove.
	 */
	public String getRemove() 
	{
		return remove;
	}

	/**
	 * @param remove
	 *            The remove to set.
	 */
	public void setRemove(String remove) 
	{
		this.remove = remove;
	}

	public Long getEventId()
	{
		return eventId;
	}

	public void setEventId(Long eventId)
	{
		this.eventId = eventId;
	}

	public void setAttachParticipants(String attachParticipants)
	{
		this.attachParticipants = attachParticipants;
	}

	public String getAttachParticipants()
	{
		return attachParticipants;
	}

	public Long[] getSearchEventId()
	{
		return searchEventId;
	}

	public String getSearchFirstName()
	{
		return searchFirstName;
	}

	public String getSearchLastName()
	{
		return searchLastName;
	}

	public void setSearchEventId(Long[] searchEventId)
	{
		this.searchEventId = searchEventId;
		if(searchEventId != null && searchEventId.length == 1)
			this.eventId = searchEventId[0];
	}

	public void setSearchFirstName(String searchFirstName)
	{
		this.searchFirstName = searchFirstName;
	}

	public void setSearchLastName(String searchLastName)
	{
		this.searchLastName = searchLastName;
	}

	public String[] getCategoryId()
	{
		return categoryId;
	}

	public void setCategoryId(String[] categoryId)
	{
		this.categoryId = categoryId;
	}

	public String[] getLocationId()
	{
		return locationId;
	}

	public void setLocationId(String[] locationId)
	{
		this.locationId = locationId;
	}

	public String getSearchEmail()
	{
		return searchEmail;
	}

	public void setSearchEmail(String searchEmail)
	{
		this.searchEmail = searchEmail;
	}

	public Map getCategoryAttributesMap()
	{
		return categoryAttributesMap;
	}

	public void setCategoryAttributesMap(Map categoryAttributesMap)
	{
		this.categoryAttributesMap = categoryAttributesMap;
	}

	public boolean isOnlyFutureEvents()
	{
		return onlyFutureEvents;
	}

	public void setOnlyFutureEvents(boolean onlyFutureEvents)
	{
		this.onlyFutureEvents = onlyFutureEvents;
	}

	public void setAndSearch(String andSearch)
	{
		this.andSearch = andSearch;
	}

	public String getSearchHashCode()
	{
		return searchHashCode;
	}

	public void setSearchHashCode(String searchHashCode)
	{
		this.searchHashCode = searchHashCode;
	}
}
