package org.infoglue.calendar.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.infoglue.calendar.controllers.CalendarController;
import org.infoglue.calendar.controllers.CategoryController;
import org.infoglue.calendar.entities.Calendar;
import org.infoglue.calendar.entities.Category;
import org.infoglue.common.util.HibernateUtil;
import org.infoglue.common.util.PropertyHelper;

/**
 * Servlet implementation class CategoryServlet
 */

public class CategoryServlet extends HttpServlet 
{
	private static final long serialVersionUID = 1L;
       
    private final static Logger logger = Logger.getLogger(CategoryServlet.class.getName());

	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		StringBuffer sb = new StringBuffer();

        try
        {
        	Session session = HibernateUtil.currentSession();
        	Transaction tx = null;
        	try 
        	{
        		tx = session.beginTransaction();
        		
        		String languageCode = "en";
        		String languageCodeParameter = request.getParameter("languageCode");
        		if(languageCodeParameter != null && !languageCodeParameter.equals(""))
        			languageCode = languageCodeParameter;
        		
        		StringBuffer allCategoriesProperty = new StringBuffer("");
        		
        		Category filterCategory = CategoryController.getController().getCategoryByPath(session, PropertyHelper.getProperty("filterCategoryPath"));
        		
        		Set<Category> categorySet = filterCategory.getChildren();
        		
        		// Add all categories to a map (sorted)
        		Map<String,Category> categoryMap = new TreeMap<String, Category>();
        		
        		Iterator categoryIterator = categorySet.iterator();
        		while(categoryIterator.hasNext())
        		{
        			Category category = (Category)categoryIterator.next();
        			categoryMap.put(category.getLocalizedName(languageCode, "en").toLowerCase(), category);
        		}
        		
        		// Iterate the sorted map
        		Iterator categorySetIterator = categoryMap.keySet().iterator();
        		while(categorySetIterator.hasNext())
        		{
        			Category category = (Category)categoryMap.get(categorySetIterator.next());
        			
        			sb.append("    <property name=\"" + category.getLocalizedName(languageCode, "en") + "\" value=\"" + category.getInternalName() + "\"/>");
        			if(allCategoriesProperty.length() > 0)
        				allCategoriesProperty.append(",");
        			allCategoriesProperty.append(category.getInternalName());
        		}
        		
        		// Original version
        		/* 
        	    Iterator categorySetIterator = categorySet.iterator();
        		while(categorySetIterator.hasNext())
        		{
        			Category category = (Category)categorySetIterator.next();
        			
        			sb.append("    <property name=\"" + category.getLocalizedName(languageCode, "en") + "\" value=\"" + category.getInternalName() + "\"/>");
        			if(allCategoriesProperty.length() > 0)
        				allCategoriesProperty.append(",");
        			allCategoriesProperty.append(category.getInternalName());
        		}
        		*/
        		
        		sb.insert(0, "<?xml version=\"1.0\" encoding=\"UTF-8\"?><properties><property name=\"All\" value=\"" + allCategoriesProperty + "\"/>");
                sb.append("</properties>");
    			
        		tx.commit();
        	}
        	catch (Exception e) 
        	{
        		if (tx!=null) tx.rollback();
        	    throw e;
        	}
        	finally 
        	{
        		HibernateUtil.closeSession();
        	}
        }
        catch(Exception e)
        {
        	logger.error("En error occurred when we tried to create a new contentVersion:" + e.getMessage(), e);
        }

		response.setContentType("text/xml");
		
		PrintWriter pw = response.getWriter();
		
		pw.println(sb.toString());
		pw.flush();
		pw.close();
	}


}
