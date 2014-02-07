package dk.truelink.ext.folder.archiver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Helper {
	
	private static String[] mailConfigs;
	
	public static String[] readMailConfigs() {

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
					System.out.println("Task with " + nodeTask.getAttributes().getNamedItem("id")
									+ " do not have mandatory sourceFolder attribute");
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
			if (task.getDestFolder().equals("")) { 
				task.setDestFolder(globalTask.getDestFolder());
			}
			if (task.getTempFolder().equals("")) {
				task.setTempFolder(globalTask.getTempFolder());
			}
			if (task.getAgeModify().equals("")) {
				task.setAgeModify(globalTask.getAgeModify());
			}
			if (task.getGzip().equals("")) {
				task.setGzip(globalTask.getGzip());
			}
			if (task.getNeedCleanSource().equals("")) {
				task.setNeedCleanSource(globalTask.getNeedCleanSource());
			}
			if (task.getNoSubFolderScan().equals("")) {				
				task.setNoSubFolderScan(globalTask.getNoSubFolderScan());
			}

			if (configuration.contains(task)) {
				System.out.println(i + " in task order has not unique id");
				System.out.println("Task with id \""+task.getId()+"\" already exists");
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
			
			if (option.getNodeName().equals("option")) {

				switch (option.getAttributes().getNamedItem("name")
						.getNodeValue()) {

				case "destFolder":
					task.setDestFolder(option.getTextContent());
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
					
					for (int k = 0; k < mailNodes.getLength(); k++) {

						Node mailConf = mailNodes.item(k);
						
						switch (mailConf.getNodeName()) {

						case "host":
							mailConfigs[0] = mailConf.getTextContent();
							break;
						case "port":
							mailConfigs[1] = mailConf.getTextContent();
							break;
						case "username":
							mailConfigs[2] = mailConf.getTextContent();
							break;
						case "password":
							mailConfigs[3] = mailConf.getTextContent();
							break;
						case "sendTo":
							mailConfigs[4] = mailConf.getTextContent();
							break;
						}
					}
					break;
				}
			}
		}
	}
}
