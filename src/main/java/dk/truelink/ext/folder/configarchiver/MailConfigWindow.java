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

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
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
import dk.truelink.ext.folder.common.Helper;
import dk.truelink.ext.folder.common.PathToConfigFiles;

public class MailConfigWindow extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JLabel hostLabel;
	private JTextField host;
	private JLabel hostLabelError;

	private JLabel portLabel;
	private JTextField port;
	private JLabel portLabelError;

	private JLabel usernameLabel;
	private JTextField username;
	private JLabel usernameLabelError;

	private JLabel passwordLabel;
	private JPasswordField password;
	private JLabel passwordLabelError;

	private JLabel sendToLabel;
	private JTextField sendTo;
	private JLabel sendToLabelError;

	private Button save;
	private Button cansel;

	private String pathToJarFile;

	public MailConfigWindow() {
		createAndConfigureWindowElements();
		fillMailWindow();
		addToContainer();
	}

	private void createAndConfigureWindowElements() {
		hostLabel = new JLabel("host");
		hostLabel.setPreferredSize(new Dimension(80, 30));
		host = new JTextField();
		host.setPreferredSize(new Dimension(100, 30));
		hostLabelError = new JLabel();
		hostLabelError.setPreferredSize(new Dimension(50, 30));

		portLabel = new JLabel("port");
		portLabel.setPreferredSize(new Dimension(80, 30));
		port = new JTextField();
		port.setPreferredSize(new Dimension(100, 30));
		portLabelError = new JLabel();
		portLabelError.setPreferredSize(new Dimension(50, 30));

		usernameLabel = new JLabel("username");
		usernameLabel.setPreferredSize(new Dimension(80, 30));
		username = new JTextField();
		username.setPreferredSize(new Dimension(100, 30));
		usernameLabelError = new JLabel();
		usernameLabelError.setPreferredSize(new Dimension(50, 30));

		passwordLabel = new JLabel("password");
		passwordLabel.setPreferredSize(new Dimension(80, 30));
		password = new JPasswordField();
		password.setPreferredSize(new Dimension(100, 30));
		passwordLabelError = new JLabel();
		passwordLabelError.setPreferredSize(new Dimension(50, 30));

		sendToLabel = new JLabel("sendTo");
		sendToLabel.setPreferredSize(new Dimension(80, 30));
		sendTo = new JTextField();
		sendTo.setPreferredSize(new Dimension(100, 30));
		sendToLabelError = new JLabel();
		sendToLabelError.setPreferredSize(new Dimension(50, 30));

		save = new Button("Save");
		save.setPreferredSize(new Dimension(50, 30));
		save.addActionListener(new SaveAction());

		cansel = new Button("Cansel");
		cansel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				MailConfigWindow.this.dispose();
			}
		});
		cansel.setPreferredSize(new Dimension(50, 30));
	}

	private void fillMailWindow() {

		//pathToJarFile = Helper.findPathToJar(this);
		//File mailXml = new File(pathToJarFile + "mailXml.xml");
		File mailXml = new File(PathToConfigFiles.PATH_TO_CONFIG_FILES + "mailXml.xml");		

		if (mailXml.exists()) {

			String[] fromMailXml = Helper.readFromMailXml(mailXml);

			host.setText(fromMailXml[0]);
			port.setText(fromMailXml[1]);
			username.setText(fromMailXml[2]);
			password.setText(fromMailXml[3]);
			sendTo.setText(fromMailXml[4]);
		}
	}

	private void addToContainer() {
		Container c = getContentPane();
		c.setLayout(new FlowLayout());
		c.add(hostLabel);
		c.add(host);
		c.add(hostLabelError);

		c.add(portLabel);
		c.add(port);
		c.add(portLabelError);

		c.add(usernameLabel);
		c.add(username);
		c.add(usernameLabelError);

		c.add(passwordLabel);
		c.add(password);
		c.add(passwordLabelError);

		c.add(sendToLabel);
		c.add(sendTo);
		c.add(sendToLabelError);

		c.add(save);
		c.add(cansel);
	}

	private class SaveAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent addAction) {
			//File mailXml = new File(pathToJarFile + "mailXml.xml");
			File mailXml = new File(PathToConfigFiles.PATH_TO_CONFIG_FILES + "mailXml.xml");			
			if (mailXml.exists()) {
				mailXml.delete();
			}
			
			File pathToConfigFiles = new File(PathToConfigFiles.PATH_TO_CONFIG_FILES);			
			if (!pathToConfigFiles.exists()) {
				pathToConfigFiles.mkdirs();
			}
			
			String[] informationFromElements = new String[5];
			if (readInformationFromElements(informationFromElements)) {

				DocumentBuilder builder = DocumentBuilderCreator.getInstance();
				Document doc = builder.newDocument();
				Element mailSettings = doc.createElement("mail_settings");

				Element host = doc.createElement("host");
				host.appendChild(doc.createTextNode(informationFromElements[0]));
				mailSettings.appendChild(host);

				Element port = doc.createElement("port");
				port.appendChild(doc.createTextNode(informationFromElements[1]));
				mailSettings.appendChild(port);

				Element username = doc.createElement("username");
				username.appendChild(doc
						.createTextNode(informationFromElements[2]));
				mailSettings.appendChild(username);

				Element password = doc.createElement("password");
				password.appendChild(doc
						.createTextNode(informationFromElements[3]));
				mailSettings.appendChild(password);

				Element sendTo = doc.createElement("sendTo");
				sendTo.appendChild(doc
						.createTextNode(informationFromElements[4]));
				mailSettings.appendChild(sendTo);

				doc.appendChild(mailSettings);

				// Write to file
				OutputStream file = null;
				try {
					file = new FileOutputStream(mailXml);
				} catch (FileNotFoundException e) {
					new RuntimeException(e);
				}
				Transformer transformer = null;
				try {
					transformer = TransformerFactory.newInstance()
							.newTransformer();
				} catch (TransformerConfigurationException e) {
					new RuntimeException(e);
				}
				transformer.setOutputProperty(OutputKeys.INDENT, "yes");
				try {
					transformer.transform(new DOMSource(doc), new StreamResult(
							file));
				} catch (TransformerException e) {
					new RuntimeException(e);
				}

				MailConfigWindow.this.dispose();
			}
		}
	}

	private boolean readInformationFromElements(String[] informationFromElements) {

		int countErrors = 0;
		if (host.getText().length() == 0) {
			countErrors++;
			hostLabelError.setText("Empty");
		} else {
			informationFromElements[0] = host.getText();
			hostLabelError.setText("");
		}

		if (port.getText().length() == 0) {
			countErrors++;
			portLabelError.setText("Empty");
		} else {
			informationFromElements[1] = port.getText();
			portLabelError.setText("");
		}

		if (username.getText().length() == 0) {
			countErrors++;
			usernameLabelError.setText("Empty");
		} else {
			informationFromElements[2] = username.getText();
			usernameLabelError.setText("");
		}

		if (password.getText().length() == 0) {
			countErrors++;
			passwordLabelError.setText("Empty");
		} else {
			informationFromElements[3] = password.getText();
			passwordLabelError.setText("");
		}

		if (sendTo.getText().length() == 0) {
			countErrors++;
			sendToLabelError.setText("Empty");
		} else {
			informationFromElements[4] = sendTo.getText();
			sendToLabelError.setText("");
		}

		if (countErrors == 0) {
			return true;
		} else {
			return false;
		}
	}
}
