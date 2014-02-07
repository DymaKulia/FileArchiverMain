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
		if(sourceFolder == null){
			return "";
		}
		return sourceFolder;
	}

	public void setSourseFolder(String sourseFolder) {
		this.sourceFolder = sourseFolder;
	}

	public String getTempFolder() {
		if(tempFolder == null){
			return "";
		}
		return tempFolder;
	}

	public void setTempFolder(String tempFolder) {
		this.tempFolder = tempFolder;
	}

	public String getDestFolder() {
		if(destFolder == null){
			return "";
		}
		return destFolder;
	}

	public void setDestFolder(String destFolder) {
		this.destFolder = destFolder;
	}

	public String getAgeModify() {
		if(ageModify == null){
			return "";
		}
		return ageModify;
	}

	public void setAgeModify(String ageModify) {
		this.ageModify = ageModify;
	}

	public String getGzip() {
		if(gzip == null){
			return "false";
		}
		return gzip;
	}

	public void setGzip(String gzip) {
		this.gzip = gzip;
	}

	public String getNoSubFolderScan() {
		if(noSubFolderScan == null){
			return "false";
		}
		return noSubFolderScan;
	}

	public void setNoSubFolderScan(String noSubFolderScan) {
		this.noSubFolderScan = noSubFolderScan;
	}

	public String getNeedCleanSource() {
		if(needCleanSource == null){
			return "false";
		}
		return needCleanSource;
	}

	public void setNeedCleanSource(String needCleanSource) {
		this.needCleanSource = needCleanSource;
	}

	public String getId() {
		if(id == null){
			return "";
		}
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
		
		return "id: " + id + " sourceFolder: " + sourceFolder + " tempFolder: "
				+ tempFolder + " destFolder: " + destFolder + " ageModify: "
				+ ageModify + " gzip: " + gzip + " noSubFolderScan: "
				+ noSubFolderScan + " needCleanSource: " + needCleanSource;
	}

}
