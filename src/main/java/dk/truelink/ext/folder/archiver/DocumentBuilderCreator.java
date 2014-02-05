package dk.truelink.ext.folder.archiver;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class DocumentBuilderCreator {

	private static DocumentBuilder instance;

	private DocumentBuilderCreator() {

	}

	public static DocumentBuilder getInstance() {

		if (instance == null) {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();			
	        try {
	        	instance = factory.newDocumentBuilder();
	        } catch (ParserConfigurationException e) {
	        	new RuntimeException(e);
	        }	        
			return instance;
		}
		return instance;
	}

}
