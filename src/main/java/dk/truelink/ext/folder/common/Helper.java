package dk.truelink.ext.folder.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Helper {

	public static String findPathToJar(Object classFromJar) {
		String path = classFromJar.getClass().getProtectionDomain()
				.getCodeSource().getLocation().getFile();			
		String pathDecode = "";
		try {
			pathDecode = URLDecoder.decode(path,"UTF-8");
		} catch (UnsupportedEncodingException e) {		
			new RuntimeException(e);
		}
		
		StringTokenizer st = new StringTokenizer(pathDecode, "/");
		int countTokens = st.countTokens();
		for (int i = 1; i < countTokens; i++) {			
			st.nextToken();			
		}		
		String nameFile = st.nextToken();		
		return pathDecode.substring(0, pathDecode.length()-(nameFile.length()));
	}
	
	public static String[] readFromMailXml(File mailXml) {
		
		DocumentBuilderFactory factory = DocumentBuilderFactory
				.newInstance();
		factory.setValidating(false);
		DocumentBuilder builder = null;
		org.w3c.dom.Document doc = null;
		try {
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e1) {
			new RuntimeException(e1);
		}
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
		fromMailXml[0]=nodeListHost.item(0).getTextContent();
		fromMailXml[1]=nodeListPort.item(0).getTextContent();
		fromMailXml[2]=nodeListUsername.item(0).getTextContent();
		fromMailXml[3]=nodeListPassword.item(0).getTextContent();
		fromMailXml[4]=nodeListSendTo.item(0).getTextContent();
				
		return fromMailXml;
	}
}
