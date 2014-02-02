package dk.truelink.ext.folder.configarchiver;

import java.awt.Button;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import dk.truelink.ext.folder.common.DocumentBuilderCreator;
import dk.truelink.ext.folder.common.Entry;
import dk.truelink.ext.folder.common.Helper;
import dk.truelink.ext.folder.common.PathToConfigFiles;

public class ArchiverConfigWindow extends JFrame {

	private static final long serialVersionUID = 1L;

	private Button add;
	private Button delete;

	private Button save;
	private Button cansel;

	private JTable configArchiverTable;
	private ConfigArchiverTableModel configArchiverTableModel;

	private ArrayList<Entry> configuration;

	private String pathToJarFile;

	public ArchiverConfigWindow() {
		createAndConfigureWindowElements();
		fillArchiverConfigWindow();
		addToContainer();
	}

	private void createAndConfigureWindowElements() {
		add = new Button("Add");
		add.setPreferredSize(new Dimension(50, 30));
		add.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				AddConfigWindow archiverConfigWindow = new AddConfigWindow(
						configuration, configArchiverTableModel);
				archiverConfigWindow.setSize(300, 350);
				archiverConfigWindow.setLocationRelativeTo(null);
				archiverConfigWindow.setVisible(true);
				archiverConfigWindow.setResizable(false);
			}
		});

		delete = new Button("Delete");
		delete.setPreferredSize(new Dimension(50, 30));
		delete.addActionListener(new DeleteAction());

		save = new Button("Save");
		save.setPreferredSize(new Dimension(50, 30));
		save.addActionListener(new SaveAction());

		cansel = new Button("Cansel");
		cansel.setPreferredSize(new Dimension(50, 30));
		cansel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				ArchiverConfigWindow.this.dispose();
			}
		});
	}

	private void fillArchiverConfigWindow() {

		//pathToJarFile = Helper.findPathToJar(this);
		//File configArchiverXml = new File(pathToJarFile + "configArchiver.xml");

		File configArchiverXml = new File(PathToConfigFiles.PATH_TO_CONFIG_FILES + "configArchiver.xml");		

		if (configArchiverXml.exists()) {

			configuration = Helper.readFromConfigArchiverXml(configArchiverXml);
		} else {

			configuration = new ArrayList<Entry>();
		}

		configArchiverTableModel = new ConfigArchiverTableModel(configuration);
		configArchiverTable = new JTable(configArchiverTableModel);
	}

	private void addToContainer() {
		Container c = getContentPane();
		c.setLayout(new FlowLayout());
		JScrollPane scrollPane = new JScrollPane(configArchiverTable);
		scrollPane.setPreferredSize(new Dimension(800, 200));
		c.add(scrollPane);
		c.add(add);
		c.add(delete);
		c.add(save);
		c.add(cansel);
	}

	private class DeleteAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent addAction) {

			int[] selectedRows = configArchiverTable.getSelectedRows();
			for (int i = 0; i < selectedRows.length; i++) {
				configuration.remove(selectedRows[selectedRows.length - 1 - i]);
			}
			configArchiverTableModel.fireTableStructureChanged();
		}
	}

	private class SaveAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent addAction) {

			//File configArchiverXml = new File(pathToJarFile
			//		+ "configArchiver.xml");
			
			File configArchiverXml = new File(PathToConfigFiles.PATH_TO_CONFIG_FILES + "configArchiver.xml");			
			if (configArchiverXml.exists()) {
				configArchiverXml.delete();
			}
			
			File pathToConfigFiles = new File(PathToConfigFiles.PATH_TO_CONFIG_FILES);			
			if (!pathToConfigFiles.exists()) {
				pathToConfigFiles.mkdirs();
			}			

			DocumentBuilder builder = DocumentBuilderCreator.getInstance();
			Document doc = builder.newDocument();
			Element archiverSettings = doc.createElement("archiver_settings");

			for (int i = 0; i < configuration.size(); i++) {
				System.out.println(i);
				Element setting = doc.createElement("setting");
				Element sourseFolder = doc.createElement("sourse_folder");
				sourseFolder.appendChild(doc.createTextNode(configuration
						.get(i).getSourseFolder()));

				Element destFolder = doc.createElement("dest_folder");
				destFolder.appendChild(doc.createTextNode(configuration.get(i)
						.getDestFolder()));

				Element tempFolder = doc.createElement("temp_folder");
				tempFolder.appendChild(doc.createTextNode(configuration.get(i)
						.getTempFolder()));

				Element ageModify = doc.createElement("age_modify");
				ageModify.appendChild(doc.createTextNode(configuration.get(i)
						.getAgeModify()));
				
				Element gzip = doc.createElement("gzip");
				gzip.appendChild(doc.createTextNode(configuration.get(i)
						.getGzip()));
				
				Element noSubFolderScan = doc.createElement("noSubFolderScan");
				noSubFolderScan.appendChild(doc.createTextNode(configuration.get(i)
						.getNoSubFolderScan()));

				setting.appendChild(sourseFolder);
				setting.appendChild(destFolder);
				setting.appendChild(tempFolder);
				setting.appendChild(ageModify);
				setting.appendChild(gzip);
				setting.appendChild(noSubFolderScan);

				archiverSettings.appendChild(setting);
			}
			
			doc.appendChild(archiverSettings);

			// Write to file
			OutputStream file = null;
			try {
				file = new FileOutputStream(configArchiverXml);
			} catch (FileNotFoundException e) {
				new RuntimeException(e);
			}
			Transformer transformer = null;
			try {
				transformer = TransformerFactory.newInstance().newTransformer();
			} catch (TransformerConfigurationException e) {
				new RuntimeException(e);
			}
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			try {
				transformer.transform(new DOMSource(doc),
						new StreamResult(file));
			} catch (TransformerException e) {
				new RuntimeException(e);
			}

			ArchiverConfigWindow.this.dispose();
		}
	}

}
