package dk.truelink.ext.folder.archiver;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.CRC32;
import java.util.zip.Deflater;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileArchiverMain {

	private static final String LOCK_FILENAME = "fileArchiver.lock";
	private static final String ZIP = ".zip";
	private static final String GZIP = ".gz";
	private static final String GZIP_PARAM = "-gzip";
	private static final String CLEAN_SOURCE_PARAM = "-cleanSource";
	private static final String NO_SUBFOLDER_SCAN_PARAM = "-noSubFolderScan";
	private static final String CHECK_MODE = "check";
	private static final FileFilter filesOnlyFilter = new FilesFilter();
	private static final FileFilter folderFilter = new FolderFileFilter();
	private static final String COPY = "copy";
	private static final String DELETE = "delete";
	private static final long marginMemory = 100000000;

	private static long copiedCount;
	private static long deletedCount;
	private static AgeFileFilter fileFilter;
	private static int ageModify;

	public static void main(String[] args) {

		if (args.length == 0) {
			System.out.println("Unknown path to configuration file");
			System.out.println("Use pattern: java -jar Archiver-0.0.1-all.jar <path to " + "configuration file> for execution mode");
			System.out.println("Or use pattern: java -jar Archiver-0.0.1-all.jar <path to " + "configuration file> <check> for configuration checking mode");
			System.exit(0);			
		} else if (args.length > 2) {
			System.out.println("Too much input parameters for archiver");
			System.exit(0);
		} else if (args.length == 2) {

			/** Do check of archiver configuration and print it */
			checkArchiverConfiguration(args);

		} else if (args.length == 1) {

			ArrayList<Task> configuration = readConfigurationFromFile(args[0]);
			doAllTasks(configuration);
		}
	}

	private static void doAllTasks(ArrayList<Task> configuration) {

		if (configuration != null) {

			for (int i = 0; i < configuration.size(); i++) {

				/** Create inArgs from configuration items and set ageModify */

				Task task = configuration.get(i);
				int countInArgs = 3;

				if (task.getGzip() != null && task.getGzip().equals("true")) {
					countInArgs++;
				}
				if (task.getNoSubFolderScan() != null && task.getNoSubFolderScan().equals("true")) {
					countInArgs++;
				}
				if (task.getNeedCleanSource() != null && task.getNeedCleanSource().equals("true")) {
					countInArgs++;
				}

				int index = 0;
				String[] inArgs = new String[countInArgs];
				inArgs[index] = task.getSourceFolder();
				index++;
				inArgs[index] = task.getDestFolder();
				index++;
				inArgs[index] = task.getTempFolder();
				if (task.getGzip().equals("true")) {
					index++;
					inArgs[index] = GZIP_PARAM;
				}
				if (task.getNoSubFolderScan().equals("true")) {
					index++;
					inArgs[index] = NO_SUBFOLDER_SCAN_PARAM;
				}
				if (task.getNeedCleanSource().equals("true")) {
					index++;
					inArgs[index] = CLEAN_SOURCE_PARAM;
				}

				ageModify = Integer.parseInt(task.getAgeModify()) * -1;
				try {
					fileFilter = new AgeFileFilter();
					doArchiveTask(inArgs);
				} catch (Exception ex) {
					System.out.println("Archive prosess with input parameters ");
					for (int k = 0; k < inArgs.length; k++) {
						System.out.print(inArgs[k] + " ");
					}
					System.out.println("is aborted");
					ex.printStackTrace();
				}
			}
		}
	}

	public static ArrayList<Task> readConfigurationFromFile(String path) {

		File archiverConfig = new File(path);
		if (!archiverConfig.exists()) {
			System.out.println("Configuration file for archiver does not exist");
			System.exit(0);	
		}

		ArrayList<Task> configuration = null;
		try {
			configuration = Helper.readArchivationConfigs(archiverConfig);
		} catch (RuntimeException ex) {
			System.out.println("Archive prosess is aborted");
			ex.printStackTrace();
			System.exit(0);	
		}
		return configuration;
	}

	private static void checkArchiverConfiguration(String[] args) {

		if (args[1].equals(CHECK_MODE)) {

			/** Do check of archiver configuration and print it */

			Checker.checkAllArchiverConfiguration(args[0]);
			String[] mail = Helper.readMailConfigs();
			System.out.println("-----mail cofiguration-------");
			if (mail == null) {
				System.out.println("NO MAIL CONFIGURATIONS");
			} else {
				String[] mailNames = { "host", "port", "username", "password", "sendTo" };
				for (int i = 0; i < mail.length; i++) {
					System.out.println(mailNames[i] + ": " + mail[i]);
				}
			}

		} else {
			throw new RuntimeException("Unknown parameter " + args[1]);
		}

	}

	public static void doArchiveTask(String[] args) {

		String source = args[0];
		String dest = args[1];
		String temp = args[2];

		if (source == null) {
			throw new RuntimeException("Source folder is NULL");
		} else if (dest == null) {
			throw new RuntimeException("Dest folder is NULL");
		} else if (temp == null) {
			throw new RuntimeException("Temp folder is NULL");
		}

		final Set extParams = loadAdditionalParametersToSet(args, 3);

		boolean forceGZip = extParams.contains(GZIP_PARAM);
		boolean cleanSource = extParams.contains(CLEAN_SOURCE_PARAM);
		boolean noSubFolderScan = extParams.contains(NO_SUBFOLDER_SCAN_PARAM);

		File sourceFolder = new File(source);
		File destFolder = new File(dest);
		File tempFolder = new File(temp);
		Checker.checkTaskConfiguration(sourceFolder, destFolder, tempFolder, false);

		final File lockFile = new File(destFolder, LOCK_FILENAME);
		lockFile.delete();

		try {
			final FileOutputStream out = new FileOutputStream(lockFile);
			try {
				java.nio.channels.FileLock lockFileLock = out.getChannel().tryLock();
				try {
					if (lockFileLock == null) {
						throw new RuntimeException("Program has been already started. Found lock file in destination folder '" + lockFile.getAbsolutePath() + "'");
					}
					System.out.println("-----------------------------------------------------------");
					System.out.println("Program started with args:");
					System.out.println("\tSource folder '" + source + "' (which folder to read)");
					System.out.println("\tDest   folder '" + dest + "' (where to put archived files)");
					System.out.println("\tTemp   folder '" + temp + "' (folder to use for temporary copied files)");
					System.out.println("\tForce  GZIP   '" + forceGZip + "' (at first ZIP into one file without deflation, than GZIP this file)");
					System.out.println("\tClean  source '" + cleanSource + "' (delete empty subfolders of source folder if all their files are moved into archive)");
					System.out.println("\tNo sub folder scan '" + noSubFolderScan + "' (work only with given folder, do not scan sub folders)");
					System.out.println("\tAge modify '" + ageModify + "'");

					long start = System.currentTimeMillis();
					long count = process(sourceFolder, destFolder, tempFolder, forceGZip, cleanSource, noSubFolderScan);
					long elapsed = System.currentTimeMillis() - start;

					System.out.println("Done in " + (elapsed / 1000) + " seconds, generated archive has " + count + " files");

				} finally {
					lockFileLock.release();
				}
			} catch (IOException e) {
				System.out.println("Try lock operation is fail on '" + lockFile.getAbsolutePath() + "'");
				e.printStackTrace();
			} finally {
				out.close();
			}
		} catch (FileNotFoundException e) {
			System.out.println("Could not find the file '" + lockFile.getAbsolutePath() + "' to create the FileInputStream");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Could not create/close the FileInputStream for '" + lockFile.getAbsolutePath() + "' file");
			e.printStackTrace();
		} finally {
			lockFile.delete();
		}
	}

	private static Set loadAdditionalParametersToSet(String[] args, int startWith) {
		Set extParams = new HashSet();
		if (args != null) {
			int i = startWith - 1;
			while (++i < args.length) {
				extParams.add(args[i]);
			}
		}
		return extParams;
	}

	/**
	 * Archive files, which we were modified before yesterday - so yesterday's
	 * and today's files are not archived.
	 * 
	 * @param sourceFolder
	 * @param destFolder
	 * @param tempFolder
	 * @param forceGZIP
	 * @param cleanSource
	 * @param noSubFolderScan
	 * @return number of archived files.
	 */
	private static long process(File sourceFolder, File destFolder, File tempFolder, boolean forceGZIP, boolean cleanSource, boolean noSubFolderScan) {

		copiedCount = 0;
		deletedCount = 0;

		long start = 0, elapsed = 0;

		File tempSubFolder = null;
		try {

			/**
			 * 1. Looking for files in source folder and its sub folders and
			 * copy them to temporary folder.
			 */
			final Calendar searchStartTime = Calendar.getInstance();
			final String tempSubFolderName = new SimpleDateFormat("yyyy.MM.dd_HH.mm.ss.SSS").format(searchStartTime.getTime());
			tempSubFolder = new File(tempFolder, tempSubFolderName);

			System.out.println("Check available disk space");

			checkAvailableDiskSpace(sourceFolder, tempFolder, noSubFolderScan, destFolder);

			System.out.print("\t1.Copy files to temp sub folder '" + tempSubFolderName + "' ... ");

			start = System.currentTimeMillis();
			copiedCount = copyFilesToTempOrDelete(sourceFolder, sourceFolder, tempSubFolder, cleanSource, noSubFolderScan, COPY);
			elapsed = System.currentTimeMillis() - start;
			System.out.println("Done in " + elapsed + "ms, moved " + copiedCount + " files.");

			if (copiedCount > 0) {

				/** 2. Make an archive */
				final String archiveName = new SimpleDateFormat("yyyy.MM.dd_HH.mm.ss.SSS").format(searchStartTime.getTime());

				System.out.print("\t2.Making an archive '" + archiveName + "' ... ");
				File archiveFile = null;
				start = System.currentTimeMillis();
				if (forceGZIP) {
					archiveFile = compactFolderWithGZIP(tempSubFolder, archiveName);
				} else {
					archiveFile = compactFolder(tempSubFolder, archiveName);
				}
				elapsed = System.currentTimeMillis() - start;
				System.out.println("Done in " + elapsed + "ms, size=" + archiveFile.length() + "B, path = '" + archiveFile.getAbsolutePath() + "'");

				/** 3. Delete temporary sub folder */
				System.out.print("\t3.Deleting temporary sub folder '" + tempSubFolder.getAbsolutePath() + "' ... ");
				start = System.currentTimeMillis();
				long deletedFileCount = deleteFolder(tempSubFolder);
				elapsed = System.currentTimeMillis() - start;
				System.out.println("Done in " + elapsed + "ms, deleted " + deletedFileCount + " files.");

				/**
				 * 4. Move archive to destination folder using current date as
				 * sub folders
				 */
				final String destSubFolderRelPath = buildDestSubFolderRelPath(searchStartTime.getTime());
				final File destSubFolder = new File(destFolder, destSubFolderRelPath);

				System.out.print("\t4.Moving archive to destination sub folder '" + destSubFolder.getAbsolutePath() + "' ... ");
				start = System.currentTimeMillis();
				moveFile(archiveFile, destSubFolder);
				elapsed = System.currentTimeMillis() - start;
				System.out.println("Done in " + elapsed + "ms");

				/** 5. Delete all files that already archived */

				/** for testing */
				Thread.sleep(500);

				System.out.print("\t5.Delete files in source folder '" + sourceFolder.getAbsolutePath() + "' ... ");
				start = System.currentTimeMillis();
				deletedCount = copyFilesToTempOrDelete(sourceFolder, sourceFolder, tempSubFolder, cleanSource, noSubFolderScan, DELETE);
				System.out.println("Delete " + deletedCount + " files");
				elapsed = System.currentTimeMillis() - start;
				System.out.println("Done in " + elapsed + "ms");
			}
		} catch (Exception e) {

			/** clear temp folder if archivation is aborted */
			if (tempSubFolder.exists()) {
				try {
					deleteFolder(tempSubFolder);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
			System.out.println("Cannot complete process: sourceFolder=" + sourceFolder + "; destFolder=" + destFolder + "; tempFolder=" + tempFolder);
			e.printStackTrace();
		}
		return copiedCount;
	}

	private static void checkAvailableDiskSpace(File sourceFolder, File tempFolder, boolean noSubFolderScan, File destFolder) {

		final File[] fileArray = sourceFolder.listFiles();
		long sizeFiles = calculateSizeFiles(fileArray, noSubFolderScan);

		if (sizeFiles + marginMemory >= tempFolder.getFreeSpace()) {
			String message = "Available disk space with temp folder is not enough to move." + " The size of files that need moving is " + sizeFiles
					+ " Bytes. Available disk space" + " with temp folder is " + tempFolder.getFreeSpace() + " Bytes";

			Helper.sendEmail(message);			

			throw new RuntimeException("do not have enough temp folder disc memory");
		}

		if (sizeFiles + marginMemory >= destFolder.getFreeSpace()) {
			String message = "Available disk space with dest folder is not enough to achivation." + " The size of files that need archivation is " + sizeFiles
					+ " Bytes. Available disk space" + " with dest folder is " + destFolder.getFreeSpace() + " Bytes";

			Helper.sendEmail(message);	
			throw new RuntimeException("Do not have enough dest folder disc memory");
		}
	}

	private static long calculateSizeFiles(File[] fileArray, boolean noSubFolderScan) {

		long sizeFiles = 0;
		for (int i = 0; i < fileArray.length; i++) {
			if (fileArray[i].isDirectory()) {
				if (!noSubFolderScan) {

					sizeFiles += calculateSizeFiles(fileArray[i].listFiles(), noSubFolderScan);
				}
			} else {
				sizeFiles += fileArray[i].length();
			}
		}
		return sizeFiles;
	}

	protected static String buildDestSubFolderRelPath(final Date date) {
		StringBuffer sb = new StringBuffer();
		sb.append(new SimpleDateFormat("yyyy").format(date));
		sb.append(File.separator);
		sb.append(new SimpleDateFormat("yyyy.MM").format(date));
		sb.append(File.separator);
		sb.append(new SimpleDateFormat("yyyy.MM.dd").format(date));
		return sb.toString();
	}

	private static long copyFilesToTempOrDelete(File sourceFolder, File sourceSubFolder, File tempFolder, boolean cleanSource, boolean noSubFolderScan, String command)
			throws Exception {
		long count = 0;

		final File[] fileArray = sourceSubFolder.listFiles(fileFilter);

		final String relSubFolderPath = sourceSubFolder.getAbsolutePath().substring(sourceFolder.getAbsolutePath().length());

		for (int i = 0; i < fileArray.length; i++) {
			File currentFile = fileArray[i];
			try {
				if (command.equals(COPY)) {
					final File newSubFolder = new File(tempFolder, relSubFolderPath);

					if (currentFile.lastModified() < fileFilter.getFilterCalendar().getTimeInMillis()) {
						copyFileBuffered(currentFile, newSubFolder);
						count++;
					}
				}
				if (command.equals(DELETE)) {
					if (!currentFile.delete()) {
						throw new Exception("Can't delete " + currentFile.getPath());
					}
					count++;
				}
			} catch (Exception e) {
				System.out.println("Cannot process file " + currentFile + ":");
				throw e;
			}
		}

		if (!noSubFolderScan) {
			final File[] folderArray = sourceSubFolder.listFiles(folderFilter);
			for (int i = 0; i < folderArray.length; i++) {
				File currentFolder = folderArray[i];
				try {
					count += copyFilesToTempOrDelete(sourceFolder, currentFolder, tempFolder, cleanSource, false, command);
					if (cleanSource) {
						File[] listFiles = currentFolder.listFiles(filesOnlyFilter);

						if (listFiles == null || listFiles.length == 0) {
							if (currentFolder != null && (!currentFolder.delete() || currentFolder.exists())) {

								/**
								 * IMPORTANT! We tried to delete a folder, but
								 * didn't manage. The main reason - it contains
								 * sub folders, which were not deleted at
								 * moveFilesToTemp
								 */
								System.out.println("Could not delete source folder '" + currentFolder.getAbsolutePath() + "'");
							}
						}
					}
				} catch (Exception e) {
					System.out.println("Cannot process folder " + currentFolder + ":");
					throw e;
				}
			}
		}
		return count;
	}

	private static File moveFile(File currentFile, File destSubFolder) throws Exception {
		final File newFile = new File(destSubFolder, currentFile.getName());

		destSubFolder.mkdirs();
		if (!(destSubFolder.exists() & destSubFolder.isDirectory())) {
			throw new Exception("Cannot create folder " + destSubFolder.getAbsolutePath() + " or it is not a directory");
		}

		currentFile.renameTo(newFile);
		return newFile;
	}

	public static void copyFileBuffered(File currentFile, File destSubFolder) {

		if (currentFile == null | destSubFolder == null) {
			throw new NullPointerException("current file or dest file is null");
		}

		final File fileDest = new File(destSubFolder, currentFile.getName());

		destSubFolder.mkdirs();
		if (!(destSubFolder.exists() & destSubFolder.isDirectory())) {
			throw new RuntimeException("Cannot create folder " + destSubFolder.getPath() + " or it is not a directory");
		}

		if (fileDest.isDirectory()) {
			throw new IllegalArgumentException("Destination file " + fileDest.getPath() + " is directory");
		}

		if (!currentFile.exists()) {
			throw new IllegalArgumentException("Current file " + currentFile.getPath() + " does not exist");
		}

		if (currentFile.isDirectory()) {
			throw new IllegalArgumentException("Current file " + currentFile.getPath() + " is directory");
		}
		
		if (!currentFile.canRead()) {
			throw new IllegalArgumentException("Can not read current file " + currentFile.getPath());
		}

		if (!fileDest.exists()) {

			try {
				fileDest.createNewFile();
			} catch (IOException e) {
				throw new IllegalArgumentException("Can not create file with name" + fileDest.getAbsolutePath(), e);
			}
		}

		if (!fileDest.canWrite()) {
			throw new IllegalArgumentException("Can not write to destinstion file " + fileDest.getPath());
		}

		BufferedInputStream in = null;
		BufferedOutputStream out = null;
		try {
			in = new BufferedInputStream(new FileInputStream(currentFile));
			out = new BufferedOutputStream(new FileOutputStream(fileDest));
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}

		byte[] buff = new byte[1024];
		int n;
		try {
			while ((n = in.read(buff)) != -1) {

				out.write(buff, 0, n);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		try {
			in.close();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static long deleteFolder(File folder) throws Exception {
		long count = 0;
		File[] listFiles = folder.listFiles();
		if (listFiles != null && listFiles.length > 0) {
			for (int i = 0; i < listFiles.length; i++) {
				File file = listFiles[i];
				if (file.isFile()) {
					if (!file.delete() || file.exists()) {
						throw new Exception("Cannot delete file " + file.getAbsolutePath());
					}
					count++;
				} else {
					count += deleteFolder(file);
				}
			}
		}
		if (!folder.delete() || folder.exists()) {
			throw new Exception("Cannot delete folder " + folder.getAbsolutePath());
		}
		return count;
	}

	private static File compactFolder(File folder, String archiveName) throws IOException {
		return compactFolder(folder, archiveName, false);
	}

	private static File compactFolder(File folder, String archiveName, boolean stored) throws IOException {
		File archiveFile = new File(folder.getParent(), archiveName + ZIP);
		ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(archiveFile));
		if (!stored) {
			zos.setLevel(Deflater.BEST_COMPRESSION);
			zos.setMethod(ZipOutputStream.DEFLATED);
		} else {
			zos.setMethod(ZipOutputStream.STORED);
		}
		zipDir(folder, zos, "", stored);
		zos.close();
		return archiveFile;
	}

	private static File compactFolderWithGZIP(File folder, String archiveName) throws IOException {
		/** Create STORED zip file */
		final File archiveFile = compactFolder(folder, archiveName, true);

		/** Create GZip */
		File archiveFileGZip = new File(archiveFile.getParent(), archiveName + ZIP + GZIP);
		GZIPOutputStream gzip = new GZIPOutputStream(new FileOutputStream(archiveFileGZip)) {
			{
				def.setLevel(Deflater.BEST_COMPRESSION);
			}
		};
		gzipDir(archiveFile, gzip);
		gzip.close();

		/** Delete STORED Zip file */
		if (!archiveFile.delete()) {
			System.out.print("Could not delete STORED zip archive.");
		}
		return archiveFileGZip;
	}

	/**
	 * Zip up a directory path
	 */
	public static void zipDir(File zipDir, ZipOutputStream zos, String path, boolean stored) throws IOException {
		/** get a listing of the directory content */
		String[] dirList = zipDir.list();
		byte[] readBuffer = new byte[2156];
		int bytesIn = 0;
		final CRC32 crc = stored ? new CRC32() : null;
		/** loop through dirList, and zip the files */
		for (int i = 0; i < dirList.length; i++) {
			File f = new File(zipDir, dirList[i]);
			if (f.isDirectory()) {
				zipDir(f, zos, path + f.getName() + "/", stored);
				continue;
			}
			FileInputStream fis = new FileInputStream(f);
			try {
				ZipEntry anEntry = new ZipEntry(path + f.getName());
				if (stored) {
					anEntry.setMethod(ZipEntry.STORED);
					long fileBytesSize = 0;
					bytesIn = fis.read(readBuffer);
					crc.reset();
					while (bytesIn != -1) {
						crc.update(readBuffer, 0, bytesIn);
						fileBytesSize += bytesIn;
						bytesIn = fis.read(readBuffer);
					}
					anEntry.setCompressedSize(fileBytesSize);
					anEntry.setSize(fileBytesSize);
					anEntry.setCrc(crc.getValue());

					fis.close();
					fis = new FileInputStream(f);
				}
				zos.putNextEntry(anEntry);
				bytesIn = fis.read(readBuffer);
				while (bytesIn != -1) {
					zos.write(readBuffer, 0, bytesIn);
					bytesIn = fis.read(readBuffer);
				}
			} finally {
				fis.close();
			}
		}
	}

	public static void gzipDir(File zipFile, GZIPOutputStream gzip) throws IOException {
		byte[] readBuffer = new byte[2156];
		int bytesIn = 0;
		FileInputStream fis = new FileInputStream(zipFile);
		try {
			bytesIn = fis.read(readBuffer);
			while (bytesIn != -1) {
				gzip.write(readBuffer, 0, bytesIn);
				bytesIn = fis.read(readBuffer);
			}
		} finally {
			fis.close();
		}
	}

	/**
	 * Filter gets files which has last modified date less than yesterday
	 */
	private static final class AgeFileFilter implements FileFilter {
		private final Calendar c;

		private AgeFileFilter() {
			final Calendar c = getCalendar(ageModify);
			this.c = c;
		}

		public boolean accept(File file) {
			return file.isFile() && file.lastModified() < this.c.getTimeInMillis();
		}

		public Calendar getFilterCalendar() {
			return c;
		}
	}

	private static Calendar getCalendar(int ageModify) {

		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, ageModify);
		calendar.set(Calendar.HOUR, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		return calendar;
	}

	/**
	 * Filter gets folders only
	 */
	private static final class FolderFileFilter implements FileFilter {

		public boolean accept(File file) {
			return file.isDirectory();
		}
	}

	/**
	 * Filter gets files only
	 */
	private static final class FilesFilter implements FileFilter {

		public boolean accept(File file) {
			return file.isFile();
		}
	}
	
	public static long getCopiedCount() {
		return copiedCount;
	}

	public static long getDeletedCount() {
		return deletedCount;
	}
}
