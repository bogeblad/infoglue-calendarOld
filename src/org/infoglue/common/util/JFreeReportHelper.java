package org.infoglue.common.util;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.table.TableModel;

import org.infoglue.calendar.databeans.EntriesDynamicTableModel;
import org.infoglue.calendar.databeans.EntriesTableModel;
import org.infoglue.common.util.io.FileHelper;
import org.jfree.report.JFreeReport;
import org.jfree.report.JFreeReportBoot;
import org.jfree.report.modules.output.pageable.base.PageableReportProcessor;
import org.jfree.report.modules.output.pageable.pdf.PDFOutputTarget;
import org.jfree.report.modules.output.pageable.plaintext.PlainTextOutputTarget;
import org.jfree.report.modules.output.pageable.plaintext.TextFilePrinterDriver;
import org.jfree.report.modules.output.table.csv.CSVTableProcessor;
import org.jfree.report.modules.output.table.html.DirectoryHtmlFilesystem;
import org.jfree.report.modules.output.table.html.HtmlProcessor;
import org.jfree.report.modules.output.table.html.StreamHtmlFilesystem;
import org.jfree.report.modules.output.table.html.ZIPHtmlFilesystem;
import org.jfree.report.modules.output.table.rtf.RTFProcessor;
import org.jfree.report.modules.output.table.xls.ExcelProcessor;
import org.jfree.report.modules.parser.base.ReportGenerator;
import org.jfree.util.Log;
import org.jfree.util.ObjectUtilities;
import org.jfree.xml.ParseException;

/**
 * A helper class which let's you generate a report into all formats.
 *
 * @author Mattias Bogeblad
 */

public class JFreeReportHelper
{
	private static boolean initialized = false;

	private static JFreeReport originalReport = null;
	private static JFreeReport dynamicReport = null;

	public void getEntriesReport(Map map, Set entries, final String filename, final String format, final String entryTypeId)
	{
		try
		{
			if(!initialized)
			{
				JFreeReportBoot.getInstance().start();
				initialized = true;

				File template = new File(PropertyHelper.getProperty("contextRootPath") + "templates/entriesReport.xml");
				File dynamicTemplate = new File(PropertyHelper.getProperty("contextRootPath") + "templates/dynamicEntriesReport.xml");
				File dynamicTemplateResult = new File(PropertyHelper.getProperty("contextRootPath") + "templates/dynamicEntriesReportResult" + entryTypeId + ".xml");
				String dynamicTemplateString = FileHelper.getFileAsString(dynamicTemplate);

				PrintWriter pw = new PrintWriter(dynamicTemplateResult);
				VelocityTemplateProcessor ctp = new VelocityTemplateProcessor();
				ctp.renderTemplate(map, pw, dynamicTemplateString);
				pw.close();

				originalReport = parseReport(template);
				dynamicReport = parseReport(dynamicTemplateResult);
			}

			JFreeReport report = originalReport;
			TableModel data = new EntriesTableModel(entries);
			if(format.contains("xls"))
			{
				report = dynamicReport;
				//System.out.println("Using dynamic report:" + report);
				data = new EntriesDynamicTableModel(entries, (List)map.get("attributeNames"));
			}
			
			report.setData(data);

			Iterator keyIterator = map.keySet().iterator();
			while (keyIterator.hasNext())
			{
				String key = (String) keyIterator.next();
				Object value = map.get(key);

				report.setProperty(key, value);
				report.setPropertyMarked(key, true);
			}

			try
			{
				if (format.contains("pdf"))
					createPDF(report, filename);
				if (format.contains("csv"))
					createCSV(report, filename);
				if (format.contains("html"))
					createDirectoryHTML(report, filename);
				if (format.contains("txt"))
					createPlainText(report, filename);
				if (format.contains("rtf"))
					createRTF(report, filename);
				if (format.contains("xls"))
					createXLS(report, filename);
			} catch (Exception e)
			{
				Log.error("Failed to write report", e);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public void getEventReportAsXLS(Map map, List events, final String filename)
	{
		try
		{
			map.put("events", events);
			map.put("formatter", new VisualFormatter());
			
			File dynamicTemplate = new File(PropertyHelper.getProperty("contextRootPath") + "templates/eventReportXLS.vm");
			String dynamicTemplateString = FileHelper.getFileAsString(dynamicTemplate);

			PrintWriter pw = new PrintWriter(filename);
			VelocityTemplateProcessor ctp = new VelocityTemplateProcessor();
			ctp.renderTemplate(map, pw, dynamicTemplateString);
			pw.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Reads the report from the specified template file.
	 * 
	 * @param templateURL
	 *            the template location.
	 * @return a report.
	 * 
	 * @throws ParseException
	 *             if the report could not be parsed.
	 */
	private JFreeReport parseReport (final File template) throws ParseException
	{
		final ReportGenerator generator = ReportGenerator.getInstance();
		try
		{
			return generator.parseReport(template);
		}
		catch (Exception e)
		{
			throw new ParseException("Failed to parse the report", e);
		}
	}

	/**
	 * Saves a report to PDF format.
	 *
	 * @param report   the report.
	 * @param fileName target file name.
	 * @return true or false.
	 */
	public static boolean createPDF (final JFreeReport report, final String fileName)
	{
		OutputStream out = null;
		try
		{
			out = new BufferedOutputStream(new FileOutputStream(new File(fileName)));
			final PDFOutputTarget target = new PDFOutputTarget(out);
			target.configure(report.getReportConfiguration());
			target.open();

			final PageableReportProcessor proc = new PageableReportProcessor(report);
			proc.setOutputTarget(target);
			proc.processReport();

			target.close();
			return true;
		}
		catch (Exception e)
		{
			Log.error("Writing PDF failed.", e);
			return false;
		}
		finally
		{
			try
			{
				if (out != null)
				{
					out.close();
				}
			}
			catch (Exception e)
			{
				Log.error("Saving PDF failed.", e);
			}
		}
	}

	/**
	 * Saves a report to plain text format.
	 *
	 * @param report   the report.
	 * @param filename target file name.
	 * @throws Exception if an error occurs.
	 */
	public static void createPlainText (final JFreeReport report, final String filename)
	throws Exception
	{
		final PageableReportProcessor pr = new PageableReportProcessor(report);
		final OutputStream fout = new BufferedOutputStream(new FileOutputStream(filename));
		// cpi = 10, lpi = 6
		final TextFilePrinterDriver pc = new TextFilePrinterDriver(fout, 15, 10);
		final PlainTextOutputTarget target = new PlainTextOutputTarget(pc);
		pr.setOutputTarget(target);
		target.open();
		pr.processReport();
		target.close();
		fout.close();
	}

	/**
	 * Saves a report to rich-text format (RTF).
	 *
	 * @param report   the report.
	 * @param filename target file name.
	 * @throws Exception if an error occurs.
	 */
	public static void createRTF (final JFreeReport report, final String filename)
	throws Exception
	{
		final RTFProcessor pr = new RTFProcessor(report);
		pr.setStrictLayout(false);
		final OutputStream fout = new BufferedOutputStream(new FileOutputStream(filename));
		pr.setOutputStream(fout);
		pr.processReport();
		fout.close();
	}

	/**
	 * Saves a report to CSV format.
	 *
	 * @param report   the report.
	 * @param filename target file name.
	 * @throws Exception if an error occurs.
	 */
	public static void createCSV (final JFreeReport report, final String filename)
	throws Exception
	{
		final CSVTableProcessor pr = new CSVTableProcessor(report);
		pr.setStrictLayout(false);
		final Writer fout = new BufferedWriter(new FileWriter(filename));
		pr.setWriter(fout);
		pr.processReport();
		fout.close();
	}

	/**
	 * Saves a report to Excel format.
	 *
	 * @param report   the report.
	 * @param filename target file name.
	 * @throws Exception if an error occurs.
	 */
	public static void createXLS (final JFreeReport report, final String filename)
	throws Exception
	{
		final ExcelProcessor pr = new ExcelProcessor(report);
		pr.setStrictLayout(false);
		final OutputStream fout = new BufferedOutputStream(new FileOutputStream(filename));
		pr.setOutputStream(fout);
		pr.processReport();
		fout.close();
	}


	/**
	 * Saves a report into a single HTML format.
	 *
	 * @param report   the report.
	 * @param filename target file name.
	 * @throws Exception if an error occurs.
	 */
	public static void createStreamHTML (final JFreeReport report, final String filename)
	throws Exception
	{
		final HtmlProcessor pr = new HtmlProcessor(report);
		pr.setStrictLayout(false);
		pr.setGenerateXHTML(true);
		final OutputStream fout = new BufferedOutputStream(new FileOutputStream(filename));
		pr.setFilesystem(new StreamHtmlFilesystem(fout));
		pr.processReport();
		fout.close();
	}

	/**
	 * Saves a report to HTML. The HTML file is stored in a directory.
	 *
	 * @param report   the report.
	 * @param filename target file name.
	 * @throws Exception if an error occurs.
	 */
	public static void createDirectoryHTML (final JFreeReport report,
			final String filename)
	throws Exception
	{
		final HtmlProcessor pr = new HtmlProcessor(report);
		pr.setFilesystem(new DirectoryHtmlFilesystem(new File(filename)));
		pr.processReport();
	}

	/**
	 * Saves a report in a ZIP file. The zip file contains a HTML document.
	 *
	 * @param report   the report.
	 * @param filename target file name.
	 * @throws Exception if an error occurs.
	 */
	public static void createZIPHTML (final JFreeReport report, final String filename)
	throws Exception
	{
		final HtmlProcessor pr = new HtmlProcessor(report);
		final OutputStream fout = new BufferedOutputStream(new FileOutputStream(filename));
		pr.setFilesystem(new ZIPHtmlFilesystem(fout, "data"));
		pr.processReport();
		fout.close();
	}


}
