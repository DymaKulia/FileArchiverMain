package dk.truelink.ext.folder.configarchiver;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

public class ConfigArchiverTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;
	private ArrayList<Entry> configuration;
	private String[] columnName= new String [] {"Sourse folder ","Dest folder","Temp folder", "Age modify"};
	
	public ConfigArchiverTableModel(ArrayList<Entry> configuration) {
		this.configuration = configuration;
	}

	@Override
	public int getColumnCount() {		
		return 4;
	}

	@Override
	public int getRowCount() {		
		return configuration.size();
	}

	@Override
	public Object getValueAt(int row, int column) {
		
		switch (column) 
        {
         case 0:
        	 return configuration.get(row).getSourseFolder();        	 
         case 1: 
        	 return configuration.get(row).getDestFolder();        	
         case 2:
        	 return configuration.get(row).getTempFolder();        	 
         case 3:
        	 return configuration.get(row).getAgeModify();        	 
         default:
        	 return null;
        }		
	}
	
	@Override
    public String getColumnName(int column)
    {
        return columnName[column];
    }

}
