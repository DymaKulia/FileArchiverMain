package dk.truelink.ext.folder.archiver;

import java.util.HashMap;
import java.util.Map;

public enum Option {

	DEST_FOLDER("destFolder"), TEMP_FOLDER("tempFolder"), CLEAN_SOURSE("cleanSource"), NO_SUBFOLDER_SCAN("noSubfolderScan"), DAYS_AGO_OF_LAST_MODIFY("daysAgoOfLastModify"), USE_GZIP(
			"useGzip"), MAIL("mail");

	private static Map<String, Option> map;

	static {
		map = new HashMap<String, Option>();
		for (Option option : Option.values()) {
			map.put(option.getName(), option);
		}
	}

	private String optionName;

	Option(String optionName) {
		this.optionName = optionName;
	}

	public String getName() {
		return optionName;
	}

	public static Option getInstanse(String optionName) {

		Option option = (Option) map.get(optionName);
		
		return option;
	}
}
