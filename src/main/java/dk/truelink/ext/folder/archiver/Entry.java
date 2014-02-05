package dk.truelink.ext.folder.archiver;

public class Entry {

	private String sourseFolder;
	private String tempFolder;
	private String destFolder;
	private String ageModify;
	private String gzip;
	private String noSubFolderScan;
	
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
	public String getGzip() {
		return gzip;
	}
	public void setGzip(String gzip) {
		this.gzip = gzip;
	}
	public String getNoSubFolderScan() {
		return noSubFolderScan;
	}
	public void setNoSubFolderScan(String noSubFolderScan) {
		this.noSubFolderScan = noSubFolderScan;
	}
}
