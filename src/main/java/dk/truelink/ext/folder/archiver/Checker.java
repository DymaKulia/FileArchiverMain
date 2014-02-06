package dk.truelink.ext.folder.archiver;

import java.io.File;
import java.util.ArrayList;

public class Checker {

	public static void checkAllArchiverConfiguration(
			String pathToConfigurationFile) {
		
		System.out.println("Start checking ...");

		File archiverConfigXml = new File(pathToConfigurationFile);
		if (!archiverConfigXml.exists()) {
			System.out.println("Configuration file: " + pathToConfigurationFile
					+ " for archiver does not exist");
		} else {

			ArrayList<Task> configuration = Helper
					.readArchivationConfigs(archiverConfigXml);

			System.out.println("Configuration file has " + configuration.size()
					+ " tasks");

			for (int i = 0; i < configuration.size(); i++) {
				System.out.println("----------------Task "+i+"-----------------");
				System.out.println("Task id \"" + configuration.get(i).getId()	+ "\" :");
				System.out.println("Source folder: " + configuration.get(i).getSourceFolder());				
				System.out.println("Dest folder: " + configuration.get(i).getDestFolder());
				System.out.println("Temp folder: " + configuration.get(i).getTempFolder());
				System.out.println("cleanSource: " + configuration.get(i).getNeedCleanSource());
				System.out.println("subfolderScan: " + configuration.get(i).getNoSubFolderScan());
				System.out.println("daysAgoOfLastModify: " + configuration.get(i).getAgeModify());
				System.out.println("useGzip: " + configuration.get(i).getGzip());
				
				File sourceFolder = new File(configuration.get(i).getSourceFolder());
				File destFolder = new File(configuration.get(i).getDestFolder());
				File tempFolder = new File(configuration.get(i).getTempFolder());
				
				int countWarnings = checkTaskConfiguration(sourceFolder, destFolder, tempFolder, true);

				if (countWarnings == 0) {
					System.out.println("NO WARNINGS! TASK IS AVAILABLE!");
				} else {
					System.out.println("COUNT WARNINGS IS " + countWarnings);
					System.out.println("PLEASE CHECK CONFIGURATION OF TASK WITH ID "
									+ configuration.get(i).getId());
				}
			}
			
			

		}

	}

	public static int checkTaskConfiguration(File sourceFolder,
			File destFolder, File tempFolder, boolean checkerMode) {

		int countWarnings = 0;
		
		if (!sourceFolder.exists() || !sourceFolder.isDirectory()) {
			System.out.println("WARNING: source folder "
					+ sourceFolder.getAbsolutePath()
					+ " does not exist or is not a directory");
			
			countWarnings++;
			if (!checkerMode) {
				throw new RuntimeException();
			}
		}
		if (!destFolder.exists() || !destFolder.isDirectory()) {
			System.out.println("WARNING: destination folder "
					+ destFolder.getAbsolutePath()
					+ " does not exist or is not a directory");
			
			countWarnings++;
			if (!checkerMode) {
				throw new RuntimeException();
			}
		}
		if (!tempFolder.exists() || !tempFolder.isDirectory()) {
			System.out.println("WARNING: temporary folder "
					+ tempFolder.getAbsolutePath()
					+ " does not exist or is not a directory");
			
			countWarnings++;
			if (!checkerMode) {
				throw new RuntimeException();
			}
		}

		if (sourceFolder.getAbsolutePath().equals(destFolder.getAbsolutePath())) {
			System.out.println("WARNING: source " + sourceFolder.getAbsolutePath()
					+ " and destination " + destFolder.getAbsolutePath()
					+ "  folders must not be the same");
			
			countWarnings++;
			if (!checkerMode) {
				throw new RuntimeException();
			}
		}
		if (tempFolder.getAbsolutePath().equals(destFolder.getAbsolutePath())) {
			System.out.println("WARNING: temporary " + tempFolder.getAbsolutePath()
					+ " and destination " + destFolder.getAbsolutePath()
					+ "  folders must not be the same");
			
			countWarnings++;
			if (!checkerMode) {
				throw new RuntimeException();
			}
		}
		if (sourceFolder.getAbsolutePath().equals(tempFolder.getAbsolutePath())) {
			System.out.println("WARNING: source " + sourceFolder.getAbsolutePath()
					+ " and temporary " + tempFolder.getAbsolutePath()
					+ "  folders must not be the same");
			
			countWarnings++;
			if (!checkerMode) {
				throw new RuntimeException();
			}
		}
		
		return countWarnings;
	}
}
