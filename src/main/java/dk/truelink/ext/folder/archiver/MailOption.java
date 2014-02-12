package dk.truelink.ext.folder.archiver;

import java.util.HashMap;
import java.util.Map;

public enum MailOption {
	
    HOST("host"),
	PORT("port"),
	USERNAME("username"),
	PASSWORD("password"),
	SENDTO("sendTo");
    
    
    private static Map<String, MailOption> map;

	static {
		map = new HashMap<String, MailOption>();
		for (MailOption option : MailOption.values()) {
			map.put(option.getName(), option);
		}
	}

	private String optionName;

	MailOption(String optionName) {
		this.optionName = optionName;
	}

	public String getName() {
		return optionName;
	}

	public static MailOption getInstanse(String optionName) {

		return (MailOption) map.get(optionName);
	}		
}
