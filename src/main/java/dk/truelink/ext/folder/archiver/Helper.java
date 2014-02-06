package dk.truelink.ext.folder.archiver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Helper {
	
	private static String[] mailConfigs;
	
	public static String[] readMailConfigs(File configXml) {

		return mailConfigs;
	}

	public static ArrayList<Task> readArchivationConfigs(
			File configArchiverXml) {

		DocumentBuilder builder = DocumentBuilderCreator.getInstance();
		org.w3c.dom.Document doc = null;
		try {
			doc = builder.parse(new FileInputStream(configArchiverXml));
		} catch (FileNotFoundException e1) {
			new RuntimeException(e1);
		} catch (SAXException e1) {
			new RuntimeException(e1);
		} catch (IOException e1) {
			new RuntimeException(e1);
		}

		ArrayList<Task> configuration = new ArrayList<Task>();
		
		//Read global configurations
		Task globalTask = new Task();		
		NodeList globals = doc.getElementsByTagName("global");
		Node global = globals.item(0);
		NodeList globalOptions = global.getChildNodes();		
		fillTask(globalTask, globalOptions);			
		
		//Read each task configurations
		NodeList allTasks = doc.getElementsByTagName("task");
		
		for (int i = 0; i < allTasks.getLength(); i++) {
			
			Task task = new Task();			
			Node nodeTask = allTasks.item(i);
			
			if (nodeTask.getAttributes().getNamedItem("sourceFolder") == null) {
				if (nodeTask.getAttributes().getNamedItem("id") == null) {
					System.out.println(i + " in task order do not have mandatory sourceFolder attribute");
					throw new RuntimeException();
				} else {
					System.out.println("Task with id: \"" + nodeTask.getAttributes().getNamedItem("id")
									+ "\" do not have mandatory sourceFolder attribute");
					throw new RuntimeException();
				}
			} else {
				task.setSourseFolder(nodeTask.getAttributes().getNamedItem("sourceFolder").getNodeValue());
				
				if(nodeTask.getAttributes().getNamedItem("id") == null){
					task.setId(nodeTask.getAttributes().getNamedItem("sourceFolder").getNodeValue());
				} else {
					task.setId(nodeTask.getAttributes().getNamedItem("id").getNodeValue());
				}				
			}			
			
			NodeList taskOptions = nodeTask.getChildNodes();
			fillTask(task, taskOptions);
			
			//Checking task after fill and if task is no filling full
			//enter global configurations where it is need
			if (task.getDestFolder() == null) {                
				//
				task.setDestFolder(globalTask.getDestFolder());
			}
			if (task.getTempFolder() == null) {
				//
			}
			if (task.getAgeModify() == null) {
				//
			}
			if (task.getGzip() == null) {
				//
			}
			if (task.getNeedCleanSource() == null) {
				//
			}
			if (task.getNoSubFolderScan() == null) {
				//
			}

			if (configuration.contains(task)) {
				System.out.println(i + " in task order have not unique name");
				throw new RuntimeException();
			} else {
				configuration.add(task);
			}
		}
		
		return configuration;
	}
	
	private static void fillTask(Task task, NodeList options){
		
		for (int i = 0; i < options.getLength(); i++) {
			Node option = options.item(i);

			switch (option.getAttributes().getNamedItem("name").getNodeValue()) {

			case "destFolder":
				task.setAgeModify(option.getTextContent());
				break;
				
			case "tempFolder":
				task.setTempFolder(option.getTextContent());
				break;
				
			case "cleanSource":
				task.setNeedCleanSource(option.getTextContent());
				break;

			case "noSubfolderScan":
				task.setNoSubFolderScan(option.getTextContent());
				break;

			case "daysAgoOfLastModify":
				task.setAgeModify(option.getTextContent());
				break;

			case "useGzip":
				task.setGzip(option.getTextContent());
				break;
				
			case "mail":
				NodeList mailNodes = option.getChildNodes();
				mailConfigs = new String[5];
				mailConfigs[0] = mailNodes.item(0).getTextContent();
				mailConfigs[1] = mailNodes.item(1).getTextContent();
				mailConfigs[2] = mailNodes.item(2).getTextContent();
				mailConfigs[3] = mailNodes.item(3).getTextContent();
				mailConfigs[4] = mailNodes.item(4).getTextContent();				
				break;					
			}
		}
	}
}
