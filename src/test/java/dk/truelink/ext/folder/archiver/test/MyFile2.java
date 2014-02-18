package dk.truelink.ext.folder.archiver.test;

import java.io.File;

public class MyFile2 extends MyFile {

	public MyFile2(String pathname) {
		super(pathname);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public boolean isDirectory() {
		return true;
	}

}