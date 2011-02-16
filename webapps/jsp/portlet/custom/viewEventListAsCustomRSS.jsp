<%@ taglib uri="webwork" prefix="ww" %>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="calendar" prefix="calendar" %>

<%@ page import="com.sun.syndication.feed.synd.*,com.sun.syndication.io.*, java.util.*,org.infoglue.calendar.entities.*" %>

<%!
String getRSSXML(org.infoglue.calendar.actions.ViewEventListAction action)
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
        
        List entries = getInternalFeedEntries(eventURL, action.getEvents(), action);
        
    	feed.setEntries(entries);
    	org.infoglue.common.util.rss.RssHelper rssHelper = new org.infoglue.common.util.rss.RssHelper();
    	rssXML = rssHelper.render(feed);
	}
	catch(Throwable t)
	{
		t.printStackTrace();
	}
	
    return rssXML;
}

java.util.List getInternalFeedEntries(String eventURL, Set events, org.infoglue.calendar.actions.ViewEventListAction action)
{
	java.util.List entries = new java.util.ArrayList();
    SyndEntry entry;
    SyndContent description;
    
    if(events != null)
    {
    	Iterator eventsIterator = events.iterator();
    	while(eventsIterator.hasNext())
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
    		
    		entry.setContents(contents);
    		
    		entries.add(entry);
    	}
    }
    
	return entries;
}
%>

<ww:set name="this" value="this" scope="page"/>
<% 
org.infoglue.calendar.actions.ViewEventListAction action = (org.infoglue.calendar.actions.ViewEventListAction)pageContext.getAttribute("this");
String rss = getRSSXML(action);
out.println(rss);
%>