package dk.truelink.ext.folder.configarchiver;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import dk.truelink.ext.folder.common.Entry;

public class ConfigArchiverTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;
	private ArrayList<Entry> configuration;
	private String[] columnName= new String [] {"Sourse folder ","Dest folder","Temp folder", "Age modify", "-gzip","-noSubFolderScan"};
	
	public ConfigArchiverTableModel(ArrayList<Entry> configuration) {
		this.configuration = configuration;
	}

	@Override
	public int getColumnCount() {		
		return 6;
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
         case 4:
        	 return configuration.get(row).getGzip();
         case 5:
        	 return configuration.get(row).getNoSubFolderScan();
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
