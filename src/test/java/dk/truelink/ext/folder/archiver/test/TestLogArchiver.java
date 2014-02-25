package dk.truelink.ext.folder.archiver.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;

import org.junit.AfterClass;
import org.junit.Ignore;
import org.junit.Test;

import dk.truelink.ext.folder.archiver.Checker;
import dk.truelink.ext.folder.archiver.FileArchiverMain;
import dk.truelink.ext.folder.archiver.Helper;
import dk.truelink.ext.folder.archiver.Task;

public class TestLogArchiver {

	private ArrayList<Task> mainConfigs;

	/**
	 * @throws SQLException
	 * @throws java.lang.Exception
	 */

	@AfterClass
	public static void clear() {
		// clear disc after testing

		File testFolder = new File("C:/LogArchiverTestFolder02192014");

		try {
			deleteFolder(testFolder);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void goArchiverWithWrongCountOfInnerParameters() {

		String[] args1 = {};
		FileArchiverMain.main(args1);
		String[] args2 = { "1", "2", "3" };
		FileArchiverMain.main(args2);
	}

	@Test
	public void goArchiverWithUncorrectInnerParameters() {

		// check mode
		String[] args1 = { "somePath", "check" };
		try {
			FileArchiverMain.main(args1);
			fail("if first inner arg is uncorrect must be called RuntimeException");
		} catch (RuntimeException ex) {
			assertEquals("Message must be \"Configuration file for archiver does not exist\"", ex.getMessage(), "Configuration file for archiver does not exist");
		}

		String[] args2 = { "somePath", "checke" };
		try {
			FileArchiverMain.main(args2);
			fail("if second inner arg is uncorrect must be called RuntimeException");
		} catch (RuntimeException ex) {
			assertEquals("Message must be \"Unknown parameter " + args2[1] + "\"", ex.getMessage(), "Unknown parameter " + args2[1]);
		}

		// archive mode
		String[] args3 = { "somePath" };

		FileArchiverMain.main(args3);

	}

	@Test
	public void testCheckerClass() {

		// Test program if task folders are null
		URL url1 = getClass().getResource("configs/confNullTaskFolders.xml");
		String[] args1 = { getPath(url1), "check" };
		FileArchiverMain.main(args1);
		assertEquals("Count errors must be 2", 2, Checker.getCountWarnings());

		// Test program if task folders are same and not exist
		URL url2 = getClass().getResource("configs/sameNotExistsFolders.xml");
		String[] args2 = { getPath(url2), "check" };
		FileArchiverMain.main(args2);
		assertEquals("Count errors must be 6", 6, Checker.getCountWarnings());

		// No warnings
		URL url = getClass().getResource("configs/main/mainTestConfiguration.xml");
		mainConfigs = FileArchiverMain.readConfigurationFromFile(getPath(url));

		File testSourceFolder = new File(mainConfigs.get(0).getSourceFolder());
		File testDestFolder = new File(mainConfigs.get(0).getDestFolder());
		File testTempFolder = new File(mainConfigs.get(0).getTempFolder());

		testSourceFolder.mkdirs();
		testDestFolder.mkdir();
		testTempFolder.mkdir();
		String[] args3 = { getPath(url), "check" };
		FileArchiverMain.main(args3);
		assertEquals("Count errors must be 0", 0, Checker.getCountWarnings());

		try {
			Checker.checkTaskConfiguration(new File("someFile"), testDestFolder, testTempFolder, false);
			fail("must be called RuntimeException");
		} catch (RuntimeException ex) {
			assertEquals("Message must be \"source folder\"", ex.getMessage().substring(0, 13), "source folder");
		}

		try {
			Checker.checkTaskConfiguration(testSourceFolder, new File("someFile"), testTempFolder, false);
			fail("must be called RuntimeException");
		} catch (RuntimeException ex) {
			assertEquals("Message must be \"destination folder\"", ex.getMessage().substring(0, 18), "destination folder");
		}

		try {
			Checker.checkTaskConfiguration(testSourceFolder, testDestFolder, new File("someFile"), false);
			fail("must be called RuntimeException");
		} catch (RuntimeException ex) {
			assertEquals("Message must be \"temporary folder\"", ex.getMessage().substring(0, 16), "temporary folder");
		}

		try {
			Checker.checkTaskConfiguration(testDestFolder, testDestFolder, testTempFolder, false);
			fail("must be called RuntimeException");
		} catch (RuntimeException ex) {
			assertEquals("Message must be \"source\"", ex.getMessage().substring(0, 6), "source");
		}

		try {
			Checker.checkTaskConfiguration(testTempFolder, testDestFolder, testTempFolder, false);
			fail("must be called RuntimeException");
		} catch (RuntimeException ex) {
			assertEquals("Message must be \"source\"", ex.getMessage().substring(0, 6), "source");
		}

		try {
			Checker.checkTaskConfiguration(testSourceFolder, testDestFolder, testDestFolder, false);
			fail("must be called RuntimeException");
		} catch (RuntimeException ex) {
			assertEquals("Message must be \"temporary\"", ex.getMessage().substring(0, 9), "temporary");
		}

	}

	@Test
	public void testHelperClass() {

		// Test program if task don't has mandatory attribute and no id
		URL url1 = getClass().getResource("configs/confNoTaskSourseFolderNoId.xml");
		try {
			FileArchiverMain.readConfigurationFromFile(getPath(url1));
			fail("If source folder is absent the exception must be called");
		} catch (RuntimeException ex) {
			assertEquals("Message must be \"0 in task order do not have mandatory sourceFolder attribute\"", ex.getMessage(),
					"0 in task order do not have mandatory sourceFolder attribute");
		}

		// Test program if task don't has only mandatory attribute
		URL url2 = getClass().getResource("configs/confNoTaskSourseFolder.xml");
		try {
			FileArchiverMain.readConfigurationFromFile(getPath(url2));
			fail("If source folder is absent the exception must be called");
		} catch (RuntimeException ex) {
			assertEquals("Message must be \"Task with id=\"task0\" do not have mandatory sourceFolder attribute\"", ex.getMessage(),
					"Task with id=\"task0\" do not have mandatory sourceFolder attribute");
		}

		// Test program if task has only mandatory attribute
		URL url3 = getClass().getResource("configs/confHasTaskSourseFolder.xml");
		ArrayList<Task> configs3 = FileArchiverMain.readConfigurationFromFile(getPath(url3));
		assertEquals("Task id must be like source folder name", configs3.get(0).getId(), "someFolder");

		// Test program if task has mandatory attribute and id
		URL url4 = getClass().getResource("configs/confHasTaskSourseFolderAndId.xml");
		ArrayList<Task> configs4 = FileArchiverMain.readConfigurationFromFile(getPath(url4));
		assertEquals("Task id must be \"task0\"", configs4.get(0).getId(), "task0");
		assertEquals("Task source folder name must be \"someFolder\"", configs4.get(0).getSourceFolder(), "someFolder");

		// Check fill task from global options
		URL url5 = getClass().getResource("configs/confGlobalAndNoTaskOptions.xml");
		ArrayList<Task> configs5 = FileArchiverMain.readConfigurationFromFile(getPath(url5));
		assertOptions(configs5.get(0));

		// Check fill task from local options
		URL url6 = getClass().getResource("configs/confTaskOptionsAndNoGlobalOptions.xml");
		ArrayList<Task> configs6 = FileArchiverMain.readConfigurationFromFile(getPath(url6));
		assertOptions(configs6.get(0));

		// Check fill task part from local options and part from global
		URL url7 = getClass().getResource("configs/confPartTaskOptionsAndPartGlobalOptions.xml");
		ArrayList<Task> configs7 = FileArchiverMain.readConfigurationFromFile(getPath(url7));
		assertOptions(configs7.get(0));

		// Tasks with same id
		URL url8 = getClass().getResource("configs/confSameTaskId.xml");
		try {
			FileArchiverMain.readConfigurationFromFile(getPath(url8));
			fail("if tasks id are same must be called runtime exeption");
		} catch (RuntimeException ex) {
			assertEquals("Message must be \"1 in task order has not unique id\"", ex.getMessage().substring(0, 33), "1 in task order has not unique id");
		}

		// Tasks with uncorrect option name
		URL url9 = getClass().getResource("configs/confUncorrectOptionName.xml");
		try {
			FileArchiverMain.readConfigurationFromFile(getPath(url9));
			fail("if option name is uncorrect must be called runtime exeption");
		} catch (RuntimeException ex) {
			assertEquals("Message must be \"In task \'GLOBAL\' option with name \'destFolde\' is uncorrect\"", ex.getMessage(),
					"In task \'GLOBAL\' option with name \'destFolde\' is uncorrect");
		}

		// Tasks with uncorrect mail option name
		URL urlMail = getClass().getResource("configs/confUncorrectMailOptionName.xml");
		try {
			FileArchiverMain.readConfigurationFromFile(getPath(urlMail));
			fail("if mail option name is uncorrect must be called runtime exeption");
		} catch (RuntimeException ex) {
			assertEquals("Message must be \"Option name porte is uncorrect\"", ex.getMessage(), "Option name porte is uncorrect");
		}

		// Tasks with two same destFolder option name
		URL url10 = getClass().getResource("configs/confTwoSameDestFolderOptionName.xml");
		try {
			FileArchiverMain.readConfigurationFromFile(getPath(url10));
			fail("if configs have same option names must be called runtime exeption");
		} catch (RuntimeException ex) {
			assertEquals("Message must be \"Task \'someId1\' has two or more same options with name destFolder\"", ex.getMessage(),
					"Task \'someId1\' has two or more same options with name destFolder");
		}

		// Tasks with two same tempFolder option name
		URL url11 = getClass().getResource("configs/confTwoSameTempFolderOptionName.xml");
		try {
			FileArchiverMain.readConfigurationFromFile(getPath(url11));
			fail("if configs have same option names must be called runtime exeption");
		} catch (RuntimeException ex) {
			assertEquals("Message must be \"Task \'someId1\' has two or more same options with name tempFolder\"", ex.getMessage(),
					"Task \'someId1\' has two or more same options with name tempFolder");
		}

		// Tasks with two same cleanSource option name
		URL url12 = getClass().getResource("configs/confTwoSameCleanSourseOptionName.xml");
		try {
			FileArchiverMain.readConfigurationFromFile(getPath(url12));
			fail("if configs have same option names must be called runtime exeption");
		} catch (RuntimeException ex) {
			assertEquals("Message must be \"Task \'someId1\' has two or more same options with name cleanSource\"", ex.getMessage(),
					"Task \'someId1\' has two or more same options with name cleanSource");
		}

		// Tasks with two same noSubfolderScan option name
		URL url13 = getClass().getResource("configs/confTwoSameNoSubfolderScanOptionName.xml");
		try {
			FileArchiverMain.readConfigurationFromFile(getPath(url13));
			fail("if configs have same option names must be called runtime exeption");
		} catch (RuntimeException ex) {
			assertEquals("Message must be \"Task \'someId1\' has two or more same options with name noSubfolderScan\"", ex.getMessage(),
					"Task \'someId1\' has two or more same options with name noSubfolderScan");
		}

		// Tasks with two same daysAgoOfLastModify option name
		URL url14 = getClass().getResource("configs/confTwoSameDaysAgoOfLastModifyOptionName.xml");
		try {
			FileArchiverMain.readConfigurationFromFile(getPath(url14));
			fail("if configs have same option names must be called runtime exeption");
		} catch (RuntimeException ex) {
			assertEquals("Message must be \"Task \'someId1\' has two or more same options with name daysAgoOfLastModify\"", ex.getMessage(),
					"Task \'someId1\' has two or more same options with name daysAgoOfLastModify");
		}

		// Tasks with two same useGzip option name
		URL url15 = getClass().getResource("configs/confTwoSameUseGzipOptionName.xml");
		try {
			FileArchiverMain.readConfigurationFromFile(getPath(url15));
			fail("if configs have same option names must be called runtime exeption");
		} catch (RuntimeException ex) {
			assertEquals("Message must be \"Task \'someId1\' has two or more same options with name useGzip\"", ex.getMessage(),
					"Task \'someId1\' has two or more same options with name useGzip");
		}

		// Tasks with uncorrect noSubfolderScan option value
		URL url16 = getClass().getResource("configs/confUncorrectNoSabfolderScanOptionValue.xml");
		try {
			FileArchiverMain.readConfigurationFromFile(getPath(url16));
			fail("If option value is uncorrect the exception must be called");
		} catch (RuntimeException ex) {
			assertEquals("Message must be \"Task 'someId1' has value which differs from 'true' or 'false' in option with name noSubfolderScan\"", ex.getMessage(),
					"Task 'someId1' has value which differs from 'true' or 'false' in option with name noSubfolderScan");
		}

		// Tasks with uncorrect daysAgoOfLastModify option value
		URL url17 = getClass().getResource("configs/confUncorrectDaysAgoOfLastModifyOptionValue.xml");
		try {
			FileArchiverMain.readConfigurationFromFile(getPath(url17));
			fail("If option value is uncorrect the exception must be called");
		} catch (RuntimeException ex) {
			assertEquals("Message must be \"Task 'someId1' has uncorrect value '1df' of option with name daysAgoOfLastModify\"", ex.getMessage(),
					"Task 'someId1' has uncorrect value '1df' of option with name daysAgoOfLastModify");
		}

		// test mail
		URL url18 = getClass().getResource("configs/confMailOnly.xml");
		try {
			Helper.readArchivationConfigs(new File(url18.toURI()));
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Helper.sendEmail("test massage");
	}

	@Test
	public void testFileArchiverMainClass() {

		String[] args1 = { null, null, null };
		try {
			FileArchiverMain.doArchiveTask(args1);
			;
			fail("If some of three first inner parameters is null the exception must be called");
		} catch (RuntimeException ex) {
			assertEquals("Message must be \"Source folder is NULL\"", ex.getMessage(), "Source folder is NULL");
		}

		String[] args2 = { "", null, null };
		try {
			FileArchiverMain.doArchiveTask(args2);
			;
			fail("If some of three first inner parameters is null the exception must be called");
		} catch (RuntimeException ex) {
			assertEquals("Message must be \"Dest folder is NULL\"", ex.getMessage(), "Dest folder is NULL");
		}

		String[] args3 = { "", "", null };
		try {
			FileArchiverMain.doArchiveTask(args3);
			;
			fail("If some of three first inner parameters is null the exception must be called");
		} catch (RuntimeException ex) {
			assertEquals("Message must be \"Temp folder is NULL\"", ex.getMessage(), "Temp folder is NULL");
		}

		// Test archivation process
		URL url = getClass().getResource("configs/main/mainTestConfiguration.xml");
		mainConfigs = FileArchiverMain.readConfigurationFromFile(getPath(url));

		File testSourceFolder = new File(mainConfigs.get(0).getSourceFolder());
		File testDestFolder = new File(mainConfigs.get(0).getDestFolder());
		File testTempFolder = new File(mainConfigs.get(0).getTempFolder());

		testSourceFolder.mkdirs();
		testDestFolder.mkdir();
		testTempFolder.mkdir();

		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -3);
		calendar.set(Calendar.HOUR, 1);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		// test if no changes are in source folder during archivation
		// in this case all archived files must be deleted in source folder
		for (int i = 0; i < 20; i++) {
			File file = new File(mainConfigs.get(0).getSourceFolder(), i + ".txt");
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			file.setLastModified(calendar.getTimeInMillis());
		}

		File subFolder = new File(mainConfigs.get(0).getSourceFolder(), "subFolder");
		subFolder.mkdir();
		for (int i = 0; i < 5; i++) {
			File subFile = new File(subFolder.getPath(), i + ".txt");
			try {
				subFile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			subFile.setLastModified(calendar.getTimeInMillis());
		}

		String[] args = { getPath(url) };
		FileArchiverMain.main(args);
		assertEquals("Count of copied and deleted files must be same", FileArchiverMain.getCopiedCount(), FileArchiverMain.getDeletedCount());

		// test if some changes are in source folder during archivation
		// in this case not all archived files must be deleted in source folder
		for (int i = 0; i < 20; i++) {
			File file = new File(mainConfigs.get(0).getSourceFolder(), i + ".txt");
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			file.setLastModified(calendar.getTimeInMillis());
		}

		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {

				Calendar calendar = Calendar.getInstance();

				try {
					Thread.sleep(400);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				for (int i = 0; i < 5; i++) {
					File file = new File(mainConfigs.get(0).getSourceFolder(), i + ".txt");
					file.setLastModified(calendar.getTimeInMillis());
				}
			}
		});
		thread.start();

		FileArchiverMain.main(args);
		assertEquals("Count of copied and deleted files mustn't be same", (FileArchiverMain.getCopiedCount() - 5), FileArchiverMain.getDeletedCount());

	}

	@Test
	public void testCopyFileOnExceptions() {
		try {
			FileArchiverMain.copyFileBuffered(null, null);
			fail("If some inner parameters is null an exception must be");
		} catch (NullPointerException e) {
			assertEquals("Message must be \"current file or dest file is null\"", e.getMessage(), "current file or dest file is null");
		}

		try {
			FileArchiverMain.copyFileBuffered(new File("some"), new MyFile("some"));
			fail("If dest folder doesn't exists an exception must be");
		} catch (RuntimeException e) {
			assertEquals("Message must be \"Cannot create folder some or it is not a directory\"", e.getMessage(), "Cannot create folder some or it is not a directory");
		}

		try {
			FileArchiverMain.copyFileBuffered(new File("someFile"), new MyFile2("someFile"));
			fail("If current file doesn't exist an exception must be");
		} catch (RuntimeException e) {
			assertEquals("Message must be \"Current file someFile does not exist\"", e.getMessage(), "Current file someFile does not exist");
		}

		try {
			FileArchiverMain.copyFileBuffered(new MyFile2("somePath"), new MyFile2("someFile"));
			fail("If current file is directory an exception must be");
		} catch (RuntimeException e) {
			assertEquals("Message must be \"Current file somePath is directory\"", e.getMessage(), "Current file somePath is directory");
		}

		try {
			FileArchiverMain.copyFileBuffered(new MyFile("somePath"), new MyFile2("someFile"));
			fail("If current file can't raed an exception must be");
		} catch (RuntimeException e) {
			assertEquals("Message must be \"Can not read current file somePath\"", e.getMessage(), "Can not read current file somePath");
		}
	}

	private String getPath(URL url) {

		File file = null;
		try {
			file = new File(url.toURI());
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return file.getPath();
	}

	private void assertOptions(Task task) {
		assertEquals("Task dest folder must be \"someDestPath\"", task.getDestFolder(), "someDestPath");
		assertEquals("Task temp folder name must be \"someTempPath\"", task.getTempFolder(), "someTempPath");
		assertEquals("Task cleanSource must be \"true\"", task.getNeedCleanSource(), "true");
		assertEquals("Task noSubfolderScan name must be \"true\"", task.getNoSubFolderScan(), "true");
		assertEquals("Task daysAgoOfLastModify must be \"1\"", task.getAgeModify(), "1");
		assertEquals("Task useGzip name must be \"true\"", task.getGzip(), "true");
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

}
