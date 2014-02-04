package dk.truelink.ext.folder.archiver;

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

import dk.truelink.ext.folder.common.Entry;
import dk.truelink.ext.folder.common.Helper;
import dk.truelink.ext.folder.common.PathToConfigFiles;

public class FileArchiverMain {

	private static final String LOCK_FILENAME = "fileArchiver.lock";
	private static final String ZIP = ".zip";
	private static final String GZIP = ".gz";
	private static final String GZIP_PARAM = "-gzip";
	private static final String CLEAN_SOURCE_PARAM = "-cleanSource";
	private static final String NO_SUBFOLDER_SCAN_PARAM = "-noSubFolderScan";
	private static final FileFilter fileFilter = new BeforeYesterdayFileFilter();
	private static final FileFilter filesOnlyFilter = new FilesFilter();
	private static final FileFilter folderFilter = new FolderFileFilter();
	private static int ageModify;
	private static long marginMemory = 100000000;

	public static void main(String[] args) {

		//Helper helper = new Helper();
		//String pathToJarFile = Helper.findPathToJar(helper);
		//File archiverConfigXml = new File(pathToJarFile + "configArchiver.xml");

		File archiverConfigXml = new File(PathToConfigFiles.PATH_TO_CONFIG_FILES + "configArchiver.xml");
		if(!archiverConfigXml.exists()){
			System.out.println("Configuratin file for file archiver does not exist");
			return;
		}
		ArrayList<Entry> configuration = Helper
				.readFromConfigArchiverXml(archiverConfigXml);

		for (int i = 0; i < configuration.size(); i++) {

			/*
			 * Create inArgs from configuration items and set ageModify
			 */
			Entry entry = configuration.get(i);
			int countInArgs = 3;

			if (entry.getGzip().equals("true")) {
				countInArgs++;
			}
			if (entry.getNoSubFolderScan().equals("true")) {
				countInArgs++;
			}

			int index = 0;
			String[] inArgs = new String[countInArgs];
			inArgs[index] = entry.getSourseFolder();
			index++;
			inArgs[index] = entry.getDestFolder();
			index++;
			inArgs[index] = entry.getTempFolder();
			if (entry.getGzip().equals("true")) {
				index++;
				inArgs[index] = GZIP_PARAM;
			}
			if (entry.getNoSubFolderScan().equals("true")) {
				index++;
				inArgs[index] = NO_SUBFOLDER_SCAN_PARAM;
			}

			ageModify = Integer.parseInt(entry.getAgeModify()) * -1;
			try {
				System.out.println(inArgs.length+ "inArgs.length");
				mainArchiverMethod(inArgs);
			} catch (Exception ex) {
				System.out.println("Archive prosess with inner parameters ");
				for (int k = 0; k < inArgs.length; k++) {
					System.out.print(inArgs[k] + " ");
				}
				System.out.println("is aborted");
				System.out.println(ex);
			}

		}
	}

	public static void mainArchiverMethod(String[] args) {

		if (args.length < 3) {
			System.out
					.println("Utility to archive contents of some folder, grouped by last change date. If folder contains subfolders, files in these subfolders will also be archived keeping relative path.");
			throw new RuntimeException("args.length < 3");
		}
		String source = args[0];
		String dest = args[1];
		String temp = args[2];

		final Set extParams = loadAdditionalParametersToSet(args, 3);

		boolean forceGZip = extParams.contains(GZIP_PARAM);
		boolean cleanSource = extParams.contains(CLEAN_SOURCE_PARAM);
		boolean noSubFolderScan = extParams.contains(NO_SUBFOLDER_SCAN_PARAM);

		File sourceFolder = new File(source);
		File destFolder = new File(dest);
		File tempFolder = new File(temp);

		checkArgs(sourceFolder, destFolder, tempFolder);

		final File lockFile = new File(destFolder, LOCK_FILENAME);
		lockFile.delete();

		try {
			final FileOutputStream out = new FileOutputStream(lockFile);
			try {
				java.nio.channels.FileLock lockFileLock = out.getChannel()
						.tryLock();
				try {
					if (lockFileLock == null) {
						System.out
								.println("Program has been already started. Found lock file in destination folder '"
										+ lockFile.getAbsolutePath() + "'");						
						throw new RuntimeException("lockFileLock == null");
					}

					System.out.println("Program started with args:");
					System.out.println("\tSource folder '" + source
							+ "' (which folder to read)");
					System.out.println("\tDest   folder '" + dest
							+ "' (where to put archived files)");
					System.out.println("\tTemp   folder '" + temp
							+ "' (folder to use for temporary copied files)");
					System.out
							.println("\tForce  GZIP   '"
									+ forceGZip
									+ "' (at first ZIP into one file without deflation, than GZIP this file)");
					System.out
							.println("\tClean  source '"
									+ cleanSource
									+ "' (delete empty subfolders of source folder if all their files are moved into archive)");
					System.out
							.println("\tNo sub folder scan '"
									+ noSubFolderScan
									+ "' (work only with given folder, do not scan sub folders)");

					long start = System.currentTimeMillis();
					long count = process(sourceFolder, destFolder, tempFolder,
							forceGZip, cleanSource, noSubFolderScan);
					long elapsed = System.currentTimeMillis() - start;

					System.out.println("Done in " + (elapsed / 1000)
							+ " seconds, generated archive has " + count
							+ " files");

				} finally {
					lockFileLock.release();
				}
			} catch (IOException e) {
				System.out.println("Try lock operation is fail on '"
						+ lockFile.getAbsolutePath() + "'");
				e.printStackTrace();
			} finally {
				out.close();
			}
		} catch (FileNotFoundException e) {
			System.out.println("Could not find the file '"
					+ lockFile.getAbsolutePath()
					+ "' to create the FileInputStream");
			e.printStackTrace();
		} catch (IOException e) {
			System.out
					.println("Could not create/close the FileInputStream for '"
							+ lockFile.getAbsolutePath() + "' file");
			e.printStackTrace();
		} finally {
			lockFile.delete();
		}
	}

	private static Set loadAdditionalParametersToSet(String[] args,
			int startWith) {
		Set extParams = new HashSet();
		if (args != null) {
			int i = startWith - 1;
			while (++i < args.length) {
				extParams.add(args[i]);
			}
		}
		return extParams;
	}

	private static void checkArgs(File sourceFolder, File destFolder,
			File tempFolder) {
		if (!sourceFolder.exists() || !sourceFolder.isDirectory()) {
			System.out.println("Source folder "
					+ sourceFolder.getAbsolutePath()
					+ " does not exist or is not a directory");			
			throw new RuntimeException();
		}
		if (!destFolder.exists() || !destFolder.isDirectory()) {
			System.out.println("Destination folder "
					+ destFolder.getAbsolutePath()
					+ " does not exist or is not a directory");			
			throw new RuntimeException();
		}
		if (!tempFolder.exists() || !tempFolder.isDirectory()) {
			System.out.println("Temporary folder "
					+ tempFolder.getAbsolutePath()
					+ " does not exist or is not a directory");			
			throw new RuntimeException();
		}

		if (sourceFolder.getAbsolutePath().equals(destFolder.getAbsolutePath())) {
			System.out.println("Source " + sourceFolder.getAbsolutePath()
					+ " and destination " + destFolder.getAbsolutePath()
					+ "  folders must not be the same");			
			throw new RuntimeException();
		}
		if (tempFolder.getAbsolutePath().equals(destFolder.getAbsolutePath())) {
			System.out.println("Temporary " + tempFolder.getAbsolutePath()
					+ " and destination " + destFolder.getAbsolutePath()
					+ "  folders must not be the same");			
			throw new RuntimeException();
		}
		if (sourceFolder.getAbsolutePath().equals(tempFolder.getAbsolutePath())) {
			System.out.println("Source " + sourceFolder.getAbsolutePath()
					+ " and temporary " + tempFolder.getAbsolutePath()
					+ "  folders must not be the same");			
			throw new RuntimeException();
		}
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
	private static long process(File sourceFolder, File destFolder,
			File tempFolder, boolean forceGZIP, boolean cleanSource,
			boolean noSubFolderScan) {

		long count = 0;
		long start = 0, elapsed = 0;
		try {
			// 1. Looking for files in source folder and its sub folders and
			// move them to temporary folder.
			final Calendar searchStartTime = Calendar.getInstance();
			final String tempSubFolderName = new SimpleDateFormat(
					"yyyy.MM.dd_HH.mm.ss.SSS")
					.format(searchStartTime.getTime());
			File tempSubFolder = new File(tempFolder, tempSubFolderName);

			System.out.println("Check available disk space");

			checkAvailableDiskSpace(sourceFolder, tempFolder,
					noSubFolderScan, destFolder);

			System.out.print("\t1.Moving files to temp sub folder '"
					+ tempSubFolderName + "' ... ");
			start = System.currentTimeMillis();
			count = moveFilesToTemp(sourceFolder, sourceFolder, tempSubFolder,
					cleanSource, noSubFolderScan);
			elapsed = System.currentTimeMillis() - start;
			System.out.println("Done in " + elapsed + "ms, moved " + count
					+ " files.");

			if (count > 0) {
				// 2. Make an archive
				final Calendar archiveStartTime = Calendar.getInstance();
				final String archiveName = new SimpleDateFormat(
						"yyyy.MM.dd_HH.mm.ss.SSS").format(archiveStartTime
						.getTime());

				System.out.print("\t2.Making an archive '" + archiveName
						+ "' ... ");
				File archiveFile = null;
				start = System.currentTimeMillis();
				if (forceGZIP) {
					archiveFile = compactFolderWithGZIP(tempSubFolder,
							archiveName);
				} else {
					archiveFile = compactFolder(tempSubFolder, archiveName);
				}
				elapsed = System.currentTimeMillis() - start;
				System.out.println("Done in " + elapsed + "ms, size="
						+ archiveFile.length() + "B, path = '"
						+ archiveFile.getAbsolutePath() + "'");

				// 3. Delete temporary sub folder
				System.out.print("\t3.Deleting temporary sub folder '"
						+ tempSubFolder.getAbsolutePath() + "' ... ");
				start = System.currentTimeMillis();
				long deletedFileCount = deleteFolder(tempSubFolder);
				elapsed = System.currentTimeMillis() - start;
				System.out.println("Done in " + elapsed + "ms, deleted "
						+ deletedFileCount + " files.");

				// 4. Move archive to destination folder using current date as
				// sub folders
				final String destSubFolderRelPath = buildDestSubFolderRelPath(archiveStartTime
						.getTime());
				final File destSubFolder = new File(destFolder,
						destSubFolderRelPath);

				System.out
						.print("\t4.Moving archive to destination sub folder '"
								+ destSubFolder.getAbsolutePath() + "' ... ");
				start = System.currentTimeMillis();
				moveFile(archiveFile, destSubFolder);
				elapsed = System.currentTimeMillis() - start;
				System.out.println("Done in " + elapsed + "ms");
			}
		} catch (Exception e) {
			System.out.println("Cannot complete process: sourceFolder="
					+ sourceFolder + "; destFolder=" + destFolder
					+ "; tempFolder=" + tempFolder);
			e.printStackTrace();
		}
		return count;
	}

	private static void checkAvailableDiskSpace(File sourceFolder,
			File tempFolder, boolean noSubFolderScan,
			File destFolder) {

		final File[] fileArray = sourceFolder.listFiles();
		long sizeFiles = calculateSizeFiles(fileArray, noSubFolderScan);

		if (sizeFiles + marginMemory >= tempFolder.getFreeSpace()) {
			String message = "Available disk space with temp folder is not enough to move."
					+ " The size of files that need moving is "
					+ sizeFiles
					+ " Bytes. Available disk space"
					+ " with temp folder is "
					+ tempFolder.getFreeSpace() + " Bytes";
			try {
				sendEmail(message);
			} catch (Exception e) {
				System.out.println("Cannot send Email");
				System.out.println(e);
			}
			throw new RuntimeException("do not have enough disc memory");
		}

		if (sizeFiles + marginMemory >= destFolder.getFreeSpace()) {
			String message = "Available disk space with dest folder is not enough to achivation."
					+ " The size of files that need archivation is "
					+ sizeFiles
					+ " Bytes. Available disk space"
					+ " with dest folder is "
					+ destFolder.getFreeSpace()
					+ " Bytes";
			sendEmail(message);
			throw new RuntimeException("do not have enough disc memory");
		}

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

	protected static String buildDestSubFolderRelPath(final Date date) {
		StringBuffer sb = new StringBuffer();
		sb.append(new SimpleDateFormat("yyyy").format(date));
		sb.append(File.separator);
		sb.append(new SimpleDateFormat("yyyy.MM").format(date));
		sb.append(File.separator);
		sb.append(new SimpleDateFormat("yyyy.MM.dd").format(date));
		return sb.toString();
	}

	private static long moveFilesToTemp(File sourceFolder,
			File sourceSubFolder, File tempFolder, boolean cleanSource,
			boolean noSubFolderScan) throws Exception {
		long count = 0;

		final File[] fileArray = sourceSubFolder.listFiles(fileFilter);

		final String relSubFolderPath = sourceSubFolder.getAbsolutePath()
				.substring(sourceFolder.getAbsolutePath().length());

		for (int i = 0; i < fileArray.length; i++) {
			File currentFile = fileArray[i];
			try {
				final File newSubFolder = new File(tempFolder, relSubFolderPath);
				moveFile(currentFile, newSubFolder);
				count++;
			} catch (Exception e) {
				System.out.println("Cannot process file " + currentFile + ":");
				e.printStackTrace();
				throw e;
			}
		}

		if (!noSubFolderScan) {
			final File[] folderArray = sourceSubFolder.listFiles(folderFilter);
			for (int i = 0; i < folderArray.length; i++) {
				File currentFolder = folderArray[i];
				try {
					count += moveFilesToTemp(sourceFolder, currentFolder,
							tempFolder, cleanSource, false);
					if (cleanSource) {
						File[] listFiles = currentFolder
								.listFiles(filesOnlyFilter);
						if (listFiles == null || listFiles.length == 0) {
							if (currentFolder != null
									&& (!currentFolder.delete() || currentFolder
											.exists())) {
								/*
								 * IMPORTANT!
								 * 
								 * We tried to delete a folder, but didn't
								 * manage. The main reason - it contains sub
								 * folders, which were not deleted at
								 * moveFilesToTemp
								 */
								// System.out.println("Could not delete source folder '"
								// + currentFolder.getAbsolutePath() + "'");
							}
						}
					}
				} catch (Exception e) {
					System.out.println("Cannot process folder " + currentFolder
							+ ":");
					e.printStackTrace();
					throw e;
				}
			}
		}
		return count;
	}

	private static File moveFile(File currentFile, File destSubFolder)
			throws Exception {
		final File newFile = new File(destSubFolder, currentFile.getName());

		destSubFolder.mkdirs();
		if (!destSubFolder.exists() || !destSubFolder.isDirectory()) {
			throw new Exception("Cannot create folder "
					+ destSubFolder.getAbsolutePath()
					+ " or it is not a directory");
		}
		currentFile.renameTo(newFile);
		return newFile;
	}

	private static long deleteFolder(File folder) throws Exception {
		long count = 0;
		File[] listFiles = folder.listFiles();
		if (listFiles != null && listFiles.length > 0) {
			for (int i = 0; i < listFiles.length; i++) {
				File file = listFiles[i];
				if (file.isFile()) {
					if (!file.delete() || file.exists()) {
						throw new Exception("Cannot delete file "
								+ file.getAbsolutePath());
					}
					count++;
				} else {
					count += deleteFolder(file);
				}
			}
		}
		if (!folder.delete() || folder.exists()) {
			throw new Exception("Cannot delete folder "
					+ folder.getAbsolutePath());
		}
		return count;
	}

	private static File compactFolder(File folder, String archiveName)
			throws IOException {
		return compactFolder(folder, archiveName, false);
	}

	private static File compactFolder(File folder, String archiveName,
			boolean stored) throws IOException {
		File archiveFile = new File(folder.getParent(), archiveName + ZIP);
		ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(
				archiveFile));
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

	private static File compactFolderWithGZIP(File folder, String archiveName)
			throws IOException {
		// Create STORED zip file
		final File archiveFile = compactFolder(folder, archiveName, true);

		// Create GZip
		File archiveFileGZip = new File(archiveFile.getParent(), archiveName
				+ ZIP + GZIP);
		GZIPOutputStream gzip = new GZIPOutputStream(new FileOutputStream(
				archiveFileGZip)) {
			{
				def.setLevel(Deflater.BEST_COMPRESSION);
			}
		};
		gzipDir(archiveFile, gzip);
		gzip.close();

		// Delete STORED Zip file
		if (!archiveFile.delete()) {
			System.out.print("Could not delete STORED zip archive.");
		}
		return archiveFileGZip;
	}

	/**
	 * Zip up a directory path
	 */
	public static void zipDir(File zipDir, ZipOutputStream zos, String path,
			boolean stored) throws IOException {
		// get a listing of the directory content
		String[] dirList = zipDir.list();
		byte[] readBuffer = new byte[2156];
		int bytesIn = 0;
		final CRC32 crc = stored ? new CRC32() : null;
		// loop through dirList, and zip the files
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

	public static void gzipDir(File zipFile, GZIPOutputStream gzip)
			throws IOException {
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
	private static final class BeforeYesterdayFileFilter implements FileFilter {
		private final Calendar c;

		private BeforeYesterdayFileFilter() {
			final Calendar c = Calendar.getInstance();
			c.add(Calendar.DATE, ageModify);
			c.set(Calendar.HOUR, 0);
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.SECOND, 0);
			c.set(Calendar.MILLISECOND, 0);
			this.c = c;
		}

		public boolean accept(File file) {
			return file.isFile()
					&& file.lastModified() < this.c.getTimeInMillis();
		}
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

	private static void sendEmail(String message) {

		// Mail notification module
		String host, username, password, sendTo;
		int port;
		//Helper helper = new Helper();
		//String pathToJarFile = Helper.findPathToJar(helper);
		//File mailXml = new File(pathToJarFile + "mailXml.xml");
		
		File mailXml = new File(PathToConfigFiles.PATH_TO_CONFIG_FILES + "mailXml.xml");
		if (mailXml.exists()) {

			String[] fromMailXml = Helper.readFromMailXml(mailXml);
			host = fromMailXml[0];
			port = Integer.parseInt(fromMailXml[1]);
			username = fromMailXml[2];
			password = fromMailXml[3];
			sendTo = fromMailXml[4];

			EMailNotifier mailNotifier = new EMailNotifier(host, port,
					username, password);
			mailNotifier.sendMail(sendTo, "Logs archiver", message);
		}
		//
	}

}
