<%@ taglib uri="webwork" prefix="ww" %>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="calendar" prefix="calendar" %>

<%@ page import="com.sun.syndication.feed.synd.*,com.sun.syndication.io.*, java.util.*,org.infoglue.calendar.entities.*" %>

<%!
String getRSSXML(org.infoglue.calendar.actions.ViewEventListAction action, javax.servlet.jsp.JspWriter out)
{
	String rssXML = null;
	
	try
	{
		String eventURL = action.getStringAttributeValue("detailUrl");
		if(eventURL == null)
			eventURL = "";
		
    	SyndFeed feed = new SyndFeedImpl();
        feed.setFeedType("atom_1.0");
		String feedTypeString = action.getStringAttributeValue("feedType");
		if(feedTypeString != null && !feedTypeString.equals(""))
			feed.setFeedType(feedTypeString);
					
        feed.setTitle(action.getStringAttributeValue("feedTitle"));
        feed.setLink(action.getStringAttributeValue("feedLink"));
        feed.setDescription(action.getStringAttributeValue("feedDescription"));
        
        List entries = getInternalFeedEntries(eventURL, action.getEvents(), action, out);
        
    	feed.setEntries(entries);
    	org.infoglue.common.util.rss.RssHelper rssHelper = new org.infoglue.common.util.rss.RssHelper();
    	rssXML = rssHelper.render(feed);
	}
	catch(Throwable t)
	{
		try
		{
			out.println("Error:" + t);
		}
		catch(Throwable t2){}
		t.printStackTrace();
	}
	
    return rssXML;
}

java.util.List getInternalFeedEntries(String eventURL, Set events, org.infoglue.calendar.actions.ViewEventListAction action, javax.servlet.jsp.JspWriter out)
{
	java.util.List entries = new java.util.ArrayList();
    SyndEntry entry;
    SyndContent description;
    
    if(events != null)
    {
    	Iterator eventsIterator = events.iterator();
    	while(eventsIterator.hasNext())
    	{
    		try
	    	{
	    		Event event = (Event)eventsIterator.next();
	    		EventVersion eventVersion = action.getEventVersion(event);
	    		
	    		entry = new SyndEntryImpl();
	    		entry.setTitle(eventVersion.getName());
				entry.setLink(eventURL.replaceAll("\\{eventId\\}", "" + event.getId()));
	    		entry.setPublishedDate(event.getStartDateTime().getTime());
	    		entry.setUri(eventURL.replaceAll("\\{eventId\\}", "" + event.getId()));
	    		
	    		List categories = new ArrayList();
	    		Iterator eventCategoriesIterator = event.getEventCategories().iterator();
	    		while(eventCategoriesIterator.hasNext())
	    		{
	    			EventCategory eventCategory = (EventCategory)eventCategoriesIterator.next();
	    			SyndCategory syndCategory = new SyndCategoryImpl();
	    			syndCategory.setTaxonomyUri(eventCategory.getEventTypeCategoryAttribute().getInternalName());
	    			syndCategory.setName(eventCategory.getCategory().getLocalizedName(action.getLanguageCode(), "sv"));
	    			categories.add(syndCategory);
	    		}
	
	    		//--------------------------------------------
	    		// Add an extra category to internal entries, 
	    		// so that we can identify them later.
	    		//--------------------------------------------
	    		
				SyndCategory syndCategory = new SyndCategoryImpl();
				syndCategory.setTaxonomyUri("isInfoGlueLink");
				syndCategory.setName("true");
				categories.add(syndCategory);
	
	    		entry.setCategories(categories);
	    		
	    		description = new SyndContentImpl();
	    		description.setType("text/html");
	    		description.setValue(eventVersion.getShortDescription());
	    		entry.setDescription(description);
				
	    		List contents = new ArrayList();
	    		
	    		String skipMetaData = action.getStringAttributeValue("skipMetaData");
	    		if(skipMetaData == null || !skipMetaData.equalsIgnoreCase("true"))
	    		{
		    		SyndContent metaData = new SyndContentImpl();
		
		    		StringBuffer xml = new StringBuffer("<![CDATA[<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		    		xml.append("<metadata>");
		    		xml.append("<startDateTime>" + action.formatDate(event.getStartDateTime().getTime(), "yyyy-MM-dd HH:mm") + "</startDateTime>");
		    		xml.append("<endDateTime>" + action.formatDate(event.getEndDateTime().getTime(), "yyyy-MM-dd HH:mm") + "</endDateTime>");
	
		    		xml.append("<longDescription>" + (eventVersion.getLongDescription() != null ? eventVersion.getLongDescription().replaceAll("&amp;", "&").replaceAll("&", "&amp;") : "") + "</longDescription>");
		    		try
		    		{
		    			xml.append("<puffBildURL>" + (action.getResourceUrl(event, "DetaljBild") != null ? action.getResourceUrl(event, "DetaljBild").replaceAll("&amp;", "&").replaceAll("&", "&amp;") : "") + "</puffBildURL>");
		    		}
		    		catch(Exception e)
		    		{
		    		}

		    		xml.append("<lecturer>" + (eventVersion.getLecturer() != null ? eventVersion.getLecturer().replaceAll("&amp;", "&").replaceAll("&", "&amp;") : "") + "</lecturer>");
		    		xml.append("<organizerName>" + (eventVersion.getOrganizerName() != null ? eventVersion.getOrganizerName().replaceAll("&amp;", "&").replaceAll("&", "&amp;") : "") + "</organizerName>");
		    		xml.append("<alternativeLocation>" + (eventVersion.getAlternativeLocation() != null ? eventVersion.getAlternativeLocation().replaceAll("&amp;", "&").replaceAll("&", "&amp;") : "") + "</alternativeLocation>");
		    		xml.append("<customLocation>" + (eventVersion.getCustomLocation() != null ? eventVersion.getCustomLocation().replaceAll("&amp;", "&").replaceAll("&", "&amp;") : "") + "</customLocation>");
		    		
		    		xml.append("<locations>");
		    		for(Location location : (Set<Location>)event.getLocations())
		    		{
		    			xml.append("<location>" + (location.getLocalizedName(action.getLanguageCode(), "sv") != null ? location.getLocalizedName(action.getLanguageCode(), "sv").replaceAll("&amp;", "&").replaceAll("&", "&amp;") : "") + "</location>");
		    		}
		    		xml.append("</locations>");

		    		xml.append("<additionalInfoURL>" + (eventVersion.getEventUrl() != null ? eventVersion.getEventUrl().replaceAll("&amp;", "&").replaceAll("&", "&amp;") : "") + "</additionalInfoURL>");
	
		    		xml.append("<resources>");
		    		for(Resource resource : (Set<Resource>)event.getResources())
		    		{
		    			try
			    		{
		    				xml.append("<resourceURL>" + (action.getResourceUrl(resource.getId()) != null ? action.getResourceUrl(resource.getId()).replaceAll("&amp;", "&").replaceAll("&", "&amp;") : "") + "</resourceURL>");
			    		}
			    		catch(Exception e)
			    		{
			    		}
			    	}
		    		xml.append("</resources>");
	
		    		xml.append("<lastRegistrationDateTime>" + (event.getLastRegistrationDateTime() != null ? action.formatDate(event.getLastRegistrationDateTime().getTime(), "yyyy-MM-dd HH:mm") : "") + "</lastRegistrationDateTime>");
		    		xml.append("<price>" + (event.getPrice() != null ? event.getPrice().replaceAll("&amp;", "&").replaceAll("&", "&amp;") : "") + "</price>");
	
		    		xml.append("<contactEmail>" + event.getContactEmail() + "</contactEmail>");
		    		xml.append("<contactName>" + (event.getContactName() != null ? event.getContactName().replaceAll("&amp;", "&").replaceAll("&", "&amp;") : "") + "</contactName>");
		    		xml.append("<contactPhone>" + (event.getContactPhone() != null ? event.getContactPhone().replaceAll("&amp;", "&").replaceAll("&", "&amp;") : "") + "</contactPhone>");
		    		xml.append("<maximumParticipants>" + event.getMaximumParticipants() + "</maximumParticipants>");
		    		
		    		xml.append("</metadata>]]>");
		
		    		metaData.setType("text/xml");
		    		metaData.setValue(xml.toString());
		    		
		    		contents.add(metaData);
	    		}
	    		
	    		entry.setContents(contents);
	    		
	    		entries.add(entry);
	    	}
    		catch(Exception e)
    		{
    			try
    			{
	    			out.println("Problem with:" + e.getMessage());
    			}
    			catch(Exception e2){}
    		}
    	}
    }
    
	return entries;
}
%>

<ww:set name="this" value="this" scope="page"/>
<% 
org.infoglue.calendar.actions.ViewEventListAction action = (org.infoglue.calendar.actions.ViewEventListAction)pageContext.getAttribute("this");
String rss = getRSSXML(action, out);
out.println(rss);
%>