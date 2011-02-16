/* ===============================================================================
*
* Part of the InfoGlue Content Management Platform (www.infoglue.org)
*
* ===============================================================================
*
*  Copyright (C)
* 
* This program is free software; you can redistribute it and/or modify it under
* the terms of the GNU General Public License version 2, as published by the
* Free Software Foundation. See the file LICENSE.html for more information.
* 
* This program is distributed in the hope that it will be useful, but WITHOUT
* ANY WARRANTY, including the implied warranty of MERCHANTABILITY or FITNESS
* FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
* 
* You should have received a copy of the GNU General Public License along with
* this program; if not, write to the Free Software Foundation, Inc. / 59 Temple
* Place, Suite 330 / Boston, MA 02111-1307 / USA.
*
* ===============================================================================
*/
package org.infoglue.common.taglib;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;

import org.apache.log4j.Logger;
import org.infoglue.calendar.taglib.AbstractTag;
import org.infoglue.common.util.rss.RssHelper;

import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndEntryImpl;

/**
 * This tag will generate a SyndEntry item to be used in a syndFeed.  
 */

public class RSSFeedEntryTag extends AbstractTag 
{
    private final static Logger logger = Logger.getLogger(RSSFeedEntryTag.class.getName());

    /**
	 * The universal version identifier.
	 */
	private static final long serialVersionUID = 8603406098980150888L;
	
	private String title 		= null;
	private String link 		= null;
	private Date publishedDate	= null;
	private String description	= null;
	private String descriptionContentType = "text/html";
	
    //Lets use the iso date format
    private static final String DATE_FORMAT = "yyyy-MM-dd";

	/**
	 * Default constructor.
	 */
	public RSSFeedEntryTag() 
	{
		super();
	}
	

	/**
	 * Process the end tag. Sets a cookie.  
	 * 
	 * @return indication of whether to continue evaluating the JSP page.
	 * @throws JspException if an error occurred while processing this tag.
	 */
	public int doEndTag() throws JspException
    {
	    try
	    {
		    RssHelper rssHelper = new RssHelper();
		    
	        DateFormat dateParser = new SimpleDateFormat(DATE_FORMAT);

	        SyndEntry entry = new SyndEntryImpl();
	        entry.setTitle(title);
	        entry.setLink(link);
	        entry.setPublishedDate(publishedDate);

	        SyndContent syndContent = new SyndContentImpl();
	        syndContent.setType(descriptionContentType);
	        syndContent.setValue(description);
	     
	        entry.setDescription(syndContent);
		    
		    addEntry(entry);
	    }
	    catch(Exception e)
	    {
	        logger.error("An error occurred when generating RSS-feed:" + e.getMessage(), e);
	    }
	    
        return EVAL_PAGE;
    }

	/**
	 * Adds the parameter to the ancestor tag.
	 * 
	 * @throws JspException if the ancestor tag isn't a url tag.
	 */
	protected void addEntry(SyndEntry entry) throws JspException
	{
		final RSSFeedTag parent = (RSSFeedTag) findAncestorWithClass(this, RSSFeedTag.class);
		if(parent == null)
		{
			throw new JspTagException("RSSFeedEntryTag must have a RSSFeedTag ancestor.");
		}

		((RSSFeedTag) parent).addFeedEntry(entry);
	}

	
    public void setDescription(String description) throws JspException
    {
        this.description = evaluateString("RssFeedEntry", "description", description);
    }
    
    public void setLink(String link) throws JspException
    {
        this.link = evaluateString("RssFeedEntry", "link", link);
    }
    
    public void setTitle(String title) throws JspException
    {
        this.title = evaluateString("RssFeedEntry", "title", title);
    }
    
    public void setDescriptionContentType(String descriptionContentType) throws JspException
    {
        this.descriptionContentType = evaluateString("RssFeedEntry", "descriptionContentType", descriptionContentType);
    }
    
    public void setPublishedDate(String publishedDate) throws JspException
    {
        this.publishedDate = (Date)evaluate("RssFeedEntry", "publishedDate", publishedDate, Date.class);
    }
}
