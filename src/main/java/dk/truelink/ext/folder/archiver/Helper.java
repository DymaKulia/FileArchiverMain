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

	public static ArrayList<Task> readArchivationConfigs(File configArchiverXml) {

		DocumentBuilder builder = DocumentBuilderCreator.getInstance();
		org.w3c.dom.Document doc = null;
		try {
			doc = builder.parse(new FileInputStream(configArchiverXml));
		} catch (FileNotFoundException e1) {
			throw new RuntimeException(e1);
		} catch (SAXException e1) {
			throw new RuntimeException(e1);
		} catch (IOException e1) {
			throw new RuntimeException(e1);
		}

		ArrayList<Task> configuration = new ArrayList<Task>();

		// Read global configurations
		Task globalTask = new Task();
		globalTask.setId("GLOBAL");
		NodeList globals = doc.getElementsByTagName("global");
		Node global = globals.item(0);

		if (global != null) {
			NodeList globalOptions = global.getChildNodes();
			fillTask(globalTask, globalOptions);
		}

		// Read each task configurations
		NodeList allTasks = doc.getElementsByTagName("task");

		for (int i = 0; i < allTasks.getLength(); i++) {

			Task task = new Task();
			Node nodeTask = allTasks.item(i);

			if (nodeTask.getAttributes().getNamedItem("sourceFolder") == null) {
				if (nodeTask.getAttributes().getNamedItem("id") == null) {
					String message = i + " in task order do not have mandatory sourceFolder attribute";
					throw new RuntimeException(message);
				} else {
					String message = "Task with " + nodeTask.getAttributes().getNamedItem("id") + " do not have mandatory sourceFolder attribute";
					throw new RuntimeException(message);
				}
			} else {
				task.setSourseFolder(nodeTask.getAttributes().getNamedItem("sourceFolder").getNodeValue());

				if (nodeTask.getAttributes().getNamedItem("id") == null) {
					task.setId(nodeTask.getAttributes().getNamedItem("sourceFolder").getNodeValue());
				} else {
					task.setId(nodeTask.getAttributes().getNamedItem("id").getNodeValue());
				}
			}

			NodeList taskOptions = nodeTask.getChildNodes();
			fillTask(task, taskOptions);

			// Checking task after fill and if task is no filling full
			// enter global configurations where it is need
			if (task.getDestFolder() == null) {
				task.setDestFolder(globalTask.getDestFolder());
			}
			if (task.getTempFolder() == null) {
				task.setTempFolder(globalTask.getTempFolder());
			}
			if (task.getAgeModify() == null) {
				task.setAgeModify(globalTask.getAgeModify());
			}
			if (task.getGzip() == null) {
				task.setGzip(globalTask.getGzip());
			}
			if (task.getNeedCleanSource() == null) {
				task.setNeedCleanSource(globalTask.getNeedCleanSource());
			}
			if (task.getNoSubFolderScan() == null) {
				task.setNoSubFolderScan(globalTask.getNoSubFolderScan());
			}

			if (configuration.contains(task)) {
				String message = i + " in task order has not unique id. \n" + "Task with id \"" + task.getId() + "\" already exists";
				throw new RuntimeException(message);
			} else {
				configuration.add(task);
			}
		}
		return configuration;
	}

	private static void fillTask(Task task, NodeList options) {

		for (int i = 0; i < options.getLength(); i++) {
			Node optionNode = options.item(i);

			if (optionNode.getNodeName().equals("option")) {

				String optionName = optionNode.getAttributes().getNamedItem("name").getNodeValue();
				Option option = Option.getInstanse(optionName);

				if (option == null) {
					String message = "In task \'" + task.getId() + "\' option with name \'" + optionName + "\' is uncorrect";
					throw new RuntimeException(message);
				}

				switch (option) {

				case DEST_FOLDER:
					if (task.getDestFolder() != null) {
						String message = "Task \'" + task.getId() + "\' has two or more same options with name " + optionName;
						throw new RuntimeException(message);
					}
					task.setDestFolder(optionNode.getTextContent());
					break;

				case TEMP_FOLDER:
					if (task.getTempFolder() != null) {
						String message = "Task \'" + task.getId() + "\' has two or more same options with name " + optionName;
						throw new RuntimeException(message);
					}
					task.setTempFolder(optionNode.getTextContent());
					break;

				case CLEAN_SOURSE:
					if (task.getNeedCleanSource() != null) {
						String message = "Task \'" + task.getId() + "\' has two or more same options with name " + optionName;
						throw new RuntimeException(message);
					}
					checkValue(optionNode.getTextContent(), optionName, task);
					task.setNeedCleanSource(optionNode.getTextContent());
					break;

				case NO_SUBFOLDER_SCAN:
					if (task.getNoSubFolderScan() != null) {
						String message = "Task \'" + task.getId() + "\' has two or more same options with name " + optionName;
						throw new RuntimeException(message);
					}
					checkValue(optionNode.getTextContent(), optionName, task);
					task.setNoSubFolderScan(optionNode.getTextContent());
					break;

				case DAYS_AGO_OF_LAST_MODIFY:
					if (task.getAgeModify() != null) {
						String message = "Task \'" + task.getId() + "\' has two or more same options with name " + optionName;
						throw new RuntimeException(message);
					}
					String age = null;
					try {
						age = optionNode.getTextContent();
						int integer = Integer.parseInt(age);
					} catch (Exception e) {
						String message = "Task \'" + task.getId() + "\' has uncorrect value \'" + age + "\' of option with name " + optionName;
						throw new RuntimeException(message);
					}

					task.setAgeModify(optionNode.getTextContent());
					break;

				case USE_GZIP:
					if (task.getGzip() != null) {
						String message = "Task \'" + task.getId() + "\' has two or more same options with name " + optionName;
						throw new RuntimeException(message);
					}
					checkValue(optionNode.getTextContent(), optionName, task);
					task.setGzip(optionNode.getTextContent());
					break;

				case MAIL:
					NodeList mailNodes = optionNode.getChildNodes();
					mailConfigs = new String[5];

					for (int k = 0; k < mailNodes.getLength(); k++) {

						Node mailConf = mailNodes.item(k);

						if (!mailConf.getNodeName().equals("#text")) {

							MailOption mailOption = MailOption.getInstanse(mailConf.getNodeName());

							if (mailOption == null) {
								String message = "Option name "+mailConf.getNodeName()+" is uncorrect";
								throw new RuntimeException(message);
							}
							
							switch (mailOption) {

							case HOST:
								mailConfigs[0] = mailConf.getTextContent();
								break;
							case PORT:
								mailConfigs[1] = mailConf.getTextContent();
								break;
							case USERNAME:
								mailConfigs[2] = mailConf.getTextContent();
								break;
							case PASSWORD:
								mailConfigs[3] = mailConf.getTextContent();
								break;
							case SENDTO:
								mailConfigs[4] = mailConf.getTextContent();
								break;
							}
						}
					}
					break;
				}
			}
		}
	}
	
	public static void sendEmail(String message) {
		/** Mail notification module */
		String host, username, password, sendTo;
		int port;

		if (mailConfigs != null) {

			host = mailConfigs[0];
			port = Integer.parseInt(mailConfigs[1]);
			username = mailConfigs[2];
			password = mailConfigs[3];
			sendTo = mailConfigs[4];

			EMailNotifier mailNotifier = new EMailNotifier(host, port, username, password);
			try {
				mailNotifier.sendMail(sendTo, "Logs archiver", message);
			} catch (Exception e) {
				System.out.println("Cannot send Email with next configuration:");
				String[] mailNames = { "host", "port", "username", "password", "sendTo" };
				for (int i = 0; i < mailConfigs.length; i++) {
					System.out.println(mailNames[i] + ": " + mailConfigs[i]);
				}
				System.out.println(e);
			}
		}
	}

	private static void checkValue(String value, String optionName, Task task) {
		if (!(value.equals("true") | value.equals("false"))) {

			String message = "Task \'" + task.getId() + "\' has value which differs from \'true\' or \'false\' in option with name " + optionName;
			throw new RuntimeException(message);
		}
	}
}
