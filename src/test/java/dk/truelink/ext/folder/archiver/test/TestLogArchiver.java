package dk.truelink.ext.folder.archiver.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import dk.truelink.ext.folder.archiver.Checker;
import dk.truelink.ext.folder.archiver.FileArchiverMain;
import dk.truelink.ext.folder.archiver.Task;

public class TestLogArchiver {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

	}

	@Before
	public void set() throws Exception {

	}

	@After
	public void tearDown() throws Exception {

	}

	/**
	 * @throws SQLException
	 * @throws java.lang.Exception
	 */

	@Test
	public void goArchiverWithWrongCountOfInnerParameters() {

		String[] args1 = {};
		try {
			FileArchiverMain.main(args1);
			fail("if inner args absent must be called RuntimeException");
		} catch (RuntimeException ex) {
			assertEquals("Message must be \"Unknown path to configuration file\"", ex.getMessage(), "Unknown path to configuration file");
		}

		String[] args2 = { "1", "2", "3" };
		try {
			FileArchiverMain.main(args2);
			fail("if count inner args more then 3 must be called RuntimeException");
		} catch (RuntimeException ex) {
			assertEquals("Message must be \"Too much input parameters for archiver\"", ex.getMessage(), "Too much input parameters for archiver");
		}
	}

	@Test
	public void goArchiverWithUncorrectInnerParameters() {

		// check mode
		String[] args1 = { "D:\\3\\config.xml", "check" };
		try {
			FileArchiverMain.main(args1);
			fail("if first inner arg is uncorrect must be called RuntimeException");
		} catch (RuntimeException ex) {
			assertEquals("Message must be \"Configuration file for archiver does not exist\"", ex.getMessage(), "Configuration file for archiver does not exist");
		}

		String[] args2 = { "D:\\3\\config.xml", "checke" };
		try {
			FileArchiverMain.main(args2);
			fail("if second inner arg is uncorrect must be called RuntimeException");
		} catch (RuntimeException ex) {
			assertEquals("Message must be \"Unknown parameter " + args2[1] + "\"", ex.getMessage(), "Unknown parameter " + args2[1]);
		}

		// archive mode
		String[] args3 = { "D:\\3\\config.xml" };
		try {
			FileArchiverMain.main(args3);
			fail("if first inner arg is uncorrect must be called RuntimeException");
		} catch (RuntimeException ex) {
			assertEquals("Message must be \"Configuration file for archiver does not exist\"", ex.getMessage(), "Configuration file for archiver does not exist");
		}
	}

	@Test
	// @Ignore
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

	}

	@Test
	// @Ignore
	public void testHelperClass() {

		// Test program if task don't has mandatory attribute and no id
		URL url1 = getClass().getResource("configs/confNoTaskSourseFolderNoId.xml");
		try {
			FileArchiverMain.readConfigurationFromFile(getPath(url1));
		} catch (RuntimeException ex) {
			assertEquals("Message must be \"Configuration file for archiver does not exist\"", ex.getMessage(), "Configuration file for archiver does not exist");
		}
		// ...

		// Test program if task don't has only mandatory attribute
		URL url2 = getClass().getResource("configs/confNoTaskSourseFolder.xml");
		try {
			FileArchiverMain.readConfigurationFromFile(getPath(url2));
		} catch (RuntimeException ex) {
			assertEquals("Message must be \"Configuration file for archiver does not exist\"", ex.getMessage(), "Configuration file for archiver does not exist");
		}
		// ...

		// Test program if task has only mandatory attribute
		URL url3 = getClass().getResource("configs/confHasTaskSourseFolder.xml");
		ArrayList<Task> configs3 = FileArchiverMain.readConfigurationFromFile(getPath(url3));

		// ...

		// Test program if task has mandatory attribute and id
		URL url4 = getClass().getResource("configs/confHasTaskSourseFolderAndId.xml");
		ArrayList<Task> configs4 = FileArchiverMain.readConfigurationFromFile(getPath(url4));

		// ...

		// Check fill task from global options
		URL url5 = getClass().getResource("configs/confGlobalAndNoTaskOptions.xml");
		ArrayList<Task> configs5 = FileArchiverMain.readConfigurationFromFile(getPath(url5));

		// ...

		// Check fill task from local options
		URL url6 = getClass().getResource("configs/confTaskOptionsAndNoGlobalOptions.xml");
		ArrayList<Task> configs6 = FileArchiverMain.readConfigurationFromFile(getPath(url6));

		// ...

		// Check fill task part from local options and part from global
		URL url7 = getClass().getResource("configs/confPartTaskOptionsAndPartGlobalOptions.xml");
		ArrayList<Task> configs7 = FileArchiverMain.readConfigurationFromFile(getPath(url7));

		// ...

		// Tasks with same id
		URL url8 = getClass().getResource("configs/confSameTaskId.xml");
		try {
			FileArchiverMain.readConfigurationFromFile(getPath(url8));
			fail();
		} catch (RuntimeException ex) {
			assertEquals("Message must be \"Configuration file for archiver does not exist\"", ex.getMessage(), "Configuration file for archiver does not exist");
		}

		// Tasks with uncorrect option name
		URL url9 = getClass().getResource("configs/confUncorrectOptionName.xml");
		try {
			FileArchiverMain.readConfigurationFromFile(getPath(url9));
			fail();
		} catch (RuntimeException ex) {
			assertEquals("Message must be \"Configuration file for archiver does not exist\"", ex.getMessage(), "Configuration file for archiver does not exist");
		}

		// Tasks with two same destFolder option name
		URL url10 = getClass().getResource("configs/confTwoSameDestFolderOptionName.xml");
		try {
			FileArchiverMain.readConfigurationFromFile(getPath(url10));
			fail();
		} catch (RuntimeException ex) {
			assertEquals("Message must be \"Configuration file for archiver does not exist\"", ex.getMessage(), "Configuration file for archiver does not exist");
		}

		// Tasks with two same tempFolder option name
		URL url11 = getClass().getResource("configs/confTwoSameTempFolderOptionName.xml");
		try {
			FileArchiverMain.readConfigurationFromFile(getPath(url11));
			fail();
		} catch (RuntimeException ex) {
			assertEquals("Message must be \"Configuration file for archiver does not exist\"", ex.getMessage(), "Configuration file for archiver does not exist");
		}

		// Tasks with two same cleanSource option name
		URL url12 = getClass().getResource("configs/confTwoSameCleanSourseOptionName.xml");
		try {
			FileArchiverMain.readConfigurationFromFile(getPath(url12));
			fail();
		} catch (RuntimeException ex) {
			assertEquals("Message must be \"Configuration file for archiver does not exist\"", ex.getMessage(), "Configuration file for archiver does not exist");
		}

		// Tasks with two same noSubfolderScan option name
		URL url13 = getClass().getResource("configs/confTwoSameNoSubfolderScanOptionName.xml");
		try {
			FileArchiverMain.readConfigurationFromFile(getPath(url13));
			fail();
		} catch (RuntimeException ex) {
			assertEquals("Message must be \"Configuration file for archiver does not exist\"", ex.getMessage(), "Configuration file for archiver does not exist");
		}

		// Tasks with two same daysAgoOfLastModify option name
		URL url14 = getClass().getResource("configs/confTwoSameDaysAgoOfLastModifyOptionName.xml");
		try {
			FileArchiverMain.readConfigurationFromFile(getPath(url14));
			fail();
		} catch (RuntimeException ex) {
			assertEquals("Message must be \"Configuration file for archiver does not exist\"", ex.getMessage(), "Configuration file for archiver does not exist");
		}

		// Tasks with two same useGzip option name
		URL url15 = getClass().getResource("configs/confTwoSameUseGzipOptionName.xml");
		try {
			FileArchiverMain.readConfigurationFromFile(getPath(url15));
			fail();
		} catch (RuntimeException ex) {
			assertEquals("Message must be \"Configuration file for archiver does not exist\"", ex.getMessage(), "Configuration file for archiver does not exist");
		}

		// Tasks with uncorrect noSubfolderScan option value
		URL url16 = getClass().getResource("configs/confUncorrectNoSabfolderScanOptionName.xml");
		try {
			FileArchiverMain.readConfigurationFromFile(getPath(url16));
			fail();
		} catch (RuntimeException ex) {
			assertEquals("Message must be \"Configuration file for archiver does not exist\"", ex.getMessage(), "Configuration file for archiver does not exist");
		}

		// Tasks with uncorrect daysAgoOfLastModify option value
		URL url17 = getClass().getResource("configs/confUncorrectDaysAgoOfLastModifyOptionName.xml");
		try {
			FileArchiverMain.readConfigurationFromFile(getPath(url17));
			fail();
		} catch (RuntimeException ex) {
			assertEquals("Message must be \"Configuration file for archiver does not exist\"", ex.getMessage(), "Configuration file for archiver does not exist");
		}

	}

	@Test
	// @Ignore
	public void testFileArchiverMainClass() {

		// Test program if task dest folder is null
		URL url1 = getClass().getResource("configs/confOnlyDestFolderIsNull.xml");
		String[] args1 = { getPath(url1) };
		try {
			FileArchiverMain.main(args1);
			fail();
		} catch (RuntimeException ex) {
			assertEquals("Message must be \"Configuration file for archiver does not exist\"", ex.getMessage(), "Configuration file for archiver does not exist");
		}

		// Test program if task temp folder is null
		URL url2 = getClass().getResource("configs/confOnlyTempFolderIsNull.xml");
		String[] args2 = { getPath(url2) };
		try {
			FileArchiverMain.main(args2);
			fail();
		} catch (RuntimeException ex) {
			assertEquals("Message must be \"Configuration file for archiver does not exist\"", ex.getMessage(), "Configuration file for archiver does not exist");
		}

		// Test archivation process
		URL url3 = getClass().getResource("configs/mainTestConfiguration.xml");
		ArrayList<Task> mainConfigs = FileArchiverMain.readConfigurationFromFile(getPath(url3));

		File testSourceFolder = new File(mainConfigs.get(0).getSourceFolder());
		File testDestFolder = new File(mainConfigs.get(0).getDestFolder());
		File testTempFolder = new File(mainConfigs.get(0).getTempFolder());

		try {
			testSourceFolder.createNewFile();
			testDestFolder.createNewFile();
			testTempFolder.createNewFile();
		} catch (IOException e) {			
			e.printStackTrace();
		}

		
		// test if no changes are in source folder during archivation
		// in this case all archived files must be deleted in source folder

		// ...

		// test if some changes are in source folder during archivation
		// in this case not all archived files must be deleted in source folder

		
		// возможно нужно создать много файлов в папке источнике и
		// потом в отдельном потоке изменять дату модификации
		// а в основном потоке производить архивацию и сравнивать
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

}
