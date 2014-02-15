package dk.truelink.ext.folder.archiver.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.net.URL;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import dk.truelink.ext.folder.archiver.Checker;
import dk.truelink.ext.folder.archiver.FileArchiverMain;

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
		
		String[] args2 = {"1","2","3"};
		try {
			FileArchiverMain.main(args2);
			fail("if count inner args more then 3 must be called RuntimeException");
		} catch (RuntimeException ex) {
			assertEquals("Message must be \"Too much input parameters for archiver\"", ex.getMessage(), "Too much input parameters for archiver");
		}
	}
	
	@Test
	public void goArchiverWithUncorrectInnerParameters() {
		
		//check mode
		String[] args1 = {"D:\\3\\config.xml","check"}; 
		try {
			FileArchiverMain.main(args1);
			fail("if first inner arg is uncorrect must be called RuntimeException");
		} catch (RuntimeException ex) {
			assertEquals("Message must be \"Configuration file for archiver does not exist\"", ex.getMessage(), "Configuration file for archiver does not exist");
		}
		
		String[] args2 = {"D:\\3\\config.xml","checke"};
		try {
			FileArchiverMain.main(args2);
			fail("if second inner arg is uncorrect must be called RuntimeException");
		} catch (RuntimeException ex) {
			assertEquals("Message must be \"Unknown parameter "+args2[1]+"\"", ex.getMessage(), "Unknown parameter " + args2[1]);
		}
		
		
		//archive mode
		String[] args3 = {"D:\\3\\config.xml"};
		try {
			FileArchiverMain.main(args3);
			fail("if first inner arg is uncorrect must be called RuntimeException");
		} catch (RuntimeException ex) {
			assertEquals("Message must be \"Configuration file for archiver does not exist\"", ex.getMessage(), "Configuration file for archiver does not exist");
		}
	}
	
	@Test
	public void testCheckMode(){
		
		URL url = getClass().getResource("configs/confNullTaskFolders.xml");
		String pathToConfig = url.getFile();
		String[] args1 = {pathToConfig,"check"};
		FileArchiverMain.main(args1);		
		assertEquals("Count errors must be 2", 2,Checker.getCountWarnings());		
		
		
		
	}
	

	@Test
	public void goArchiverWithAnyConfigurations() {

		URL url = getClass().getResource("configs/twoSameDestFolderOptions.xml");
		String pathToConfig = url.getFile();

	}

}
