package dk.truelink.ext.folder.configarchiver;

import java.io.File;

public class BeginMainConfigWindow {
	
	public static void main(String[] args) {
		
		MainConfigWindow mainConfigWindow = new MainConfigWindow();
        mainConfigWindow.setSize(250, 150);
        mainConfigWindow.setLocationRelativeTo(null);              
        mainConfigWindow.setVisible(true);
        mainConfigWindow.setResizable(false);      
        
        long sizeFiles=calculateSizeFiles(new File("D:/3").listFiles(),
    			false);
        System.out.println(sizeFiles + " SIZE OF FILES");
	}
	
	private static long calculateSizeFiles(File[] fileArray,
			boolean noSubFolderScan) {

		long sizeFiles = 0;
		for (int i = 0; i < fileArray.length; i++) {
			if(fileArray[i].isDirectory()){
				if(!noSubFolderScan){
					sizeFiles += calculateSizeFiles(fileArray[i].listFiles(),
							noSubFolderScan);
				}				
			} else {
				sizeFiles += fileArray[i].length();
			}
		}
		return sizeFiles;
	}
	
}
