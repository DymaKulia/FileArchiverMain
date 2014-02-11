package dk.truelink.ext.folder.archiver;

public class Task {

	private String id;
	private String sourceFolder;
	private String tempFolder;
	private String destFolder;
	private String ageModify;
	private String gzip;
	private String noSubFolderScan;
	private String needCleanSource;

	public String getSourceFolder() {		
		return sourceFolder;
	}

	public void setSourseFolder(String sourseFolder) {
		this.sourceFolder = sourseFolder;
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

	public String getNeedCleanSource() {		
		return needCleanSource;
	}

	public void setNeedCleanSource(String needCleanSource) {
		this.needCleanSource = needCleanSource;
	}

	public String getId() {		
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public boolean equals(Object obj) {

		Task task = (Task) obj;

		if (id.equals(task.getId())) {
			return true;
		} else {

			return false;
		}
	}

	@Override
	public String toString() {

		return "id: " + id + " sourceFolder: " + sourceFolder + " tempFolder: " + tempFolder + " destFolder: " + destFolder + " ageModify: " + ageModify + " gzip: " + gzip
				+ " noSubFolderScan: " + noSubFolderScan + " needCleanSource: " + needCleanSource;
	}

}
