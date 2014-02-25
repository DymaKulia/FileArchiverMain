package dk.truelink.ext.folder.archiver;

import java.io.File;
import java.util.ArrayList;

public class Checker {

	private static int countWarnings;

	public static void checkAllArchiverConfiguration(String pathToConfigurationFile) {

		System.out.println("");		
		System.out.println("---------- START CHECKING -------------");		
		System.out.println("");		

		File archiverConfigXml = new File(pathToConfigurationFile);
		if (!archiverConfigXml.exists()) {
			System.out.println("Cheking is aborted");
			System.out.println("Configuration file: " + pathToConfigurationFile + " for archiver does not exist");
			String message = "Configuration file for archiver does not exist";
			throw new RuntimeException(message);
		} else {

			ArrayList<Task> configuration = Helper.readArchivationConfigs(archiverConfigXml);

			System.out.println("Configuration file has " + configuration.size() + " tasks");

			for (int i = 0; i < configuration.size(); i++) {
				System.out.println("----------------Task " + i + "-----------------");
				System.out.println("Task id \"" + configuration.get(i).getId() + "\" :");
				System.out.println("Source folder: " + configuration.get(i).getSourceFolder());
				System.out.println("Dest folder: " + configuration.get(i).getDestFolder());
				System.out.println("Temp folder: " + configuration.get(i).getTempFolder());
				System.out.println("cleanSource: " + configuration.get(i).getNeedCleanSource());
				System.out.println("subfolderScan: " + configuration.get(i).getNoSubFolderScan());
				System.out.println("daysAgoOfLastModify: " + configuration.get(i).getAgeModify());
				System.out.println("useGzip: " + configuration.get(i).getGzip());

				countWarnings = 0;
				File sourceFolder = null;
				if (configuration.get(i).getSourceFolder() != null) {
					sourceFolder = new File(configuration.get(i).getSourceFolder());
				} else {
					System.out.println("WARNING: In Task \'" + configuration.get(i).getId() + "\' source folder is null");
					countWarnings++;
				}

				File destFolder = null;
				if (configuration.get(i).getDestFolder() != null) {
					destFolder = new File(configuration.get(i).getDestFolder());
				} else {
					System.out.println("WARNING: In Task \'" + configuration.get(i).getId() + "\' dest folder is null");
					countWarnings++;
				}

				File tempFolder = null;
				if (configuration.get(i).getTempFolder() != null) {
					tempFolder = new File(configuration.get(i).getTempFolder());
				} else {
					System.out.println("WARNING: In Task \'" + configuration.get(i).getId() + "\' temp folder is null");
					countWarnings++;
				}

				if (countWarnings == 0) {
					countWarnings = checkTaskConfiguration(sourceFolder, destFolder, tempFolder, true);
				}
				System.out.println("");
				if (countWarnings == 0) {
					System.out.println("NO WARNINGS! TASK IS AVAILABLE!");
					System.out.println("");
				} else {
					System.out.println("COUNT WARNINGS IS " + countWarnings);
					System.out.println("PLEASE CHECK CONFIGURATION OF TASK WITH ID " + configuration.get(i).getId());
					System.out.println("");
				}
			}
		}
	}

	public static int checkTaskConfiguration(File sourceFolder, File destFolder, File tempFolder, boolean checkerMode) {

		if (!(sourceFolder.exists() & sourceFolder.isDirectory())) {
			System.out.println("");						
			System.out.println("WARNING: source folder " + sourceFolder.getPath() + " does not exist or is not a directory");
			
			countWarnings++;
			if (!checkerMode) {
				throw new RuntimeException("source folder " + sourceFolder.getPath() + " does not exist or is not a directory");
			}
		}
		if (!(destFolder.exists() & destFolder.isDirectory())) {
			System.out.println("");
			System.out.println("WARNING: destination folder " + destFolder.getPath() + " does not exist or is not a directory");

			countWarnings++;
			if (!checkerMode) {
				throw new RuntimeException("destination folder " + destFolder.getPath() + " does not exist or is not a directory");
			}
		}
		if (!(tempFolder.exists() & tempFolder.isDirectory())) {
			System.out.println("");
			System.out.println("WARNING: temporary folder " + tempFolder.getPath() + " does not exist or is not a directory");

			countWarnings++;
			if (!checkerMode) {
				throw new RuntimeException("temporary folder " + tempFolder.getPath() + " does not exist or is not a directory");
			}
		}

		if (sourceFolder.getAbsolutePath().equals(destFolder.getAbsolutePath())) {

			System.out.println("");
			System.out.println("WARNING: source " + sourceFolder.getPath() + " and destination " + destFolder.getPath() + "  folders must not be the same");

			countWarnings++;
			if (!checkerMode) {
				throw new RuntimeException("source " + sourceFolder.getPath() + " and destination " + destFolder.getPath() + "  folders must not be the same");
			}
		}

		if (tempFolder.getAbsolutePath().equals(destFolder.getAbsolutePath())) {
			System.out.println("");
			System.out.println("WARNING: temporary " + tempFolder.getPath() + " and destination " + destFolder.getPath() + "  folders must not be the same");

			countWarnings++;
			if (!checkerMode) {
				throw new RuntimeException("temporary " + tempFolder.getPath() + " and destination " + destFolder.getPath() + "  folders must not be the same");
			}
		}

		if (sourceFolder.getAbsolutePath().equals(tempFolder.getAbsolutePath())) {
			System.out.println("");
			System.out.println("WARNING: source " + sourceFolder.getPath() + " and temporary " + tempFolder.getPath() + "  folders must not be the same");

			countWarnings++;
			if (!checkerMode) {
				throw new RuntimeException("source " + sourceFolder.getPath() + " and temporary " + tempFolder.getPath() + "  folders must not be the same");
			}
		}
		return countWarnings;
	}

	public static int getCountWarnings() {
		return countWarnings;
	}
}
