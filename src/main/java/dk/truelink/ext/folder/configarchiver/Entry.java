package dk.truelink.ext.folder.configarchiver;

public class Entry {

	private String sourseFolder;
	private String tempFolder;
	private String destFolder;
	private String ageModify;
	
	public String getSourseFolder() {
		return sourseFolder;
	}
	public void setSourseFolder(String sourseFolder) {
		this.sourseFolder = sourseFolder;
	}
	public String getTempFolder() {
		return tempFolder;
	}
	public void setTempFolder(String tempFolder) {
		this.tempFolder = tempFolder;
	}
	public String getDestFolder() {
		return destFolder;
	}
	public void setDestFolder(String destFolder) {
		this.destFolder = destFolder;
	}
	public String getAgeModify() {
		return ageModify;
	}
	public void setAgeModify(String ageModify) {
		this.ageModify = ageModify;
	}
}
