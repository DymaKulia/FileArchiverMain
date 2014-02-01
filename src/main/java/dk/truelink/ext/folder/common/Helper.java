package dk.truelink.ext.folder.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Helper {

	public static String findPathToJar(Object classFromJar) {
		String path = classFromJar.getClass().getProtectionDomain()
				.getCodeSource().getLocation().getFile();
		String pathDecode = "";
		try {
			pathDecode = URLDecoder.decode(path, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			new RuntimeException(e);
		}

		StringTokenizer st = new StringTokenizer(pathDecode, "/");
		int countTokens = st.countTokens();
		for (int i = 1; i < countTokens; i++) {
			st.nextToken();
		}
		String nameFile = st.nextToken();
		return pathDecode.substring(0, pathDecode.length()
				- (nameFile.length()));//!!!!!!!!!!!!
	}

	public static String[] readFromMailXml(File mailXml) {

		DocumentBuilder builder = DocumentBuilderCreator.getInstance();
		org.w3c.dom.Document doc = null;
		try {
			doc = builder.parse(new FileInputStream(mailXml));
		} catch (FileNotFoundException e1) {
			new RuntimeException(e1);
		} catch (SAXException e1) {
			new RuntimeException(e1);
		} catch (IOException e1) {
			new RuntimeException(e1);
		}

		NodeList nodeListHost = doc.getElementsByTagName("host");
		NodeList nodeListPort = doc.getElementsByTagName("port");
		NodeList nodeListUsername = doc.getElementsByTagName("username");
		NodeList nodeListPassword = doc.getElementsByTagName("password");
		NodeList nodeListSendTo = doc.getElementsByTagName("sendTo");

		String[] fromMailXml = new String[5];
		fromMailXml[0] = nodeListHost.item(0).getTextContent();
		fromMailXml[1] = nodeListPort.item(0).getTextContent();
		fromMailXml[2] = nodeListUsername.item(0).getTextContent();
		fromMailXml[3] = nodeListPassword.item(0).getTextContent();
		fromMailXml[4] = nodeListSendTo.item(0).getTextContent();

		return fromMailXml;
	}

	public static ArrayList<Entry> readFromConfigArchiverXml(
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

		ArrayList<Entry> configuration = new ArrayList<Entry>();

		NodeList nodeListSourseFolder = doc.getElementsByTagName("sourse_folder");
		NodeList nodeListDestFolder = doc.getElementsByTagName("dest_folder");
		NodeList nodeListTempFolder = doc.getElementsByTagName("temp_folder");
		NodeList nodeListAgeModify = doc.getElementsByTagName("age_modify");
		NodeList nodeListGzip = doc.getElementsByTagName("gzip");
		NodeList nodeListNoSubFolderScan = doc.getElementsByTagName("noSubFolderScan");

		for (int i = 0; i < nodeListSourseFolder.getLength(); i++) {

			
			Entry entry = new Entry();
			entry.setSourseFolder(nodeListSourseFolder.item(i).getTextContent());
			entry.setDestFolder(nodeListDestFolder.item(i).getTextContent());
			entry.setTempFolder(nodeListTempFolder.item(i).getTextContent());
			entry.setAgeModify(nodeListAgeModify.item(i).getTextContent());
			entry.setGzip(nodeListGzip.item(i).getTextContent());
			entry.setNoSubFolderScan(nodeListNoSubFolderScan.item(i).getTextContent());
			configuration.add(entry);
		}

		return configuration;
	}
}
