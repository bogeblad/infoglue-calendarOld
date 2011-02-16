package org.infoglue.calendar.databeans;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.table.AbstractTableModel;

import org.infoglue.calendar.controllers.EntryController;
import org.infoglue.calendar.entities.Entry;

public class EntriesDynamicTableModel extends AbstractTableModel
{
	private List entries = new ArrayList();
	private List attributeNames = new ArrayList();
	
	/**
	 * Creates a new TableModel, populated with data about various software projects.
	 */
	public EntriesDynamicTableModel(Set entries, List attributeNames)
	{
		this.entries.addAll(entries);
		this.attributeNames.addAll(attributeNames);
	}

	/**
	 * Returns the row count.
	 *
	 * @return the row count.
	 */
	public int getRowCount()
	{
		return this.entries.size();
	}

	/**
	 * Returns the column count.
	 *
	 * @return the column count.
	 */
	public int getColumnCount()
	{
		return 12 + attributeNames.size();
	}

	/**
	 * Returns the column name.
	 *
	 * @param column the column (zero-based index).
	 * @return the column name.
	 */
	public String getColumnName(final int column)
	{
		switch (column)
		{
			case 0:
				return "id";
			case 1:
				return "firstName";
			case 2:
				return "lastName";
			case 3:
				return "email";
			case 4:
				return "organisation";
			case 5:
				return "address";
			case 6:
				return "zipcode";
			case 7:
				return "city";
			case 8:
				return "phone";
			case 9:
				return "fax";
			case 10:
				return "message";
			case 11:
				return "fullName";
			default:
			{
				int attributeIndex = column - 12;
				if(this.attributeNames.size() > attributeIndex)
					return (String)this.attributeNames.get(attributeIndex);
				else
					throw new IllegalArgumentException("EntriesDynamicTableModel: invalid column index.");
			}
		}
	}


	/**
	 * Returns the value at a particular row and column.
	 *
	 * @param row    the row (zero-based index).
	 * @param column the column (zero-based index).
	 * @return the value.
	 */
	public Object getValueAt(final int row, final int column)
	{
		Entry entry = (Entry) entries.get(row);

		if (column == 0)
		{
			return entry.getId();
		} 
		else if (column == 1)
		{
			return entry.getFirstName();
		} 
		else if (column == 2)
		{
			return entry.getLastName();
		} 
		else if (column == 3)
		{
			return entry.getEmail();
		} 
		else if (column == 4)
		{
			return entry.getOrganisation();
		} 
		else if (column == 5)
		{
			return entry.getAddress();
		} 
		else if (column == 6)
		{
			return entry.getZipcode();
		} 
		else if (column == 7)
		{
			return entry.getCity();
		} 
		else if (column == 8)
		{
			return entry.getPhone();
		} 
		else if (column == 9)
		{
			return entry.getFax();
		} 
		else if (column == 10)
		{
			return entry.getMessage();
		} 
		else if (column == 11)
		{
			return entry.getFirstName() + " " + entry.getLastName();
		} 
		/*
		else if (column == 12)
		{
			return entry.getAttributes();
		} 
		*/
		else
		{
			int attributeIndex = column - 12;
			if(this.attributeNames.size() > attributeIndex)
			{
				String name = (String)this.attributeNames.get(attributeIndex);
				String value = EntryController.getController().getAttributeValue(entry.getAttributes(), name, false);
				return value;
			}
			else
				return null;
		}
		/*
		else
		{
			return null;
		}
		*/
	}
}