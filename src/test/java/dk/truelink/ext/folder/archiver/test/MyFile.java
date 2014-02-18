package dk.truelink.ext.folder.archiver.test;

import java.io.File;

public class MyFile extends File {

	public MyFile(String pathname) {
		super(pathname);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean mkdirs() {
		return false;
	}

	@Override
	public boolean isDirectory() {
		return false;
	}
	
	@Override
	public boolean exists() {
		return true;
	}
	
	@Override
	public boolean canRead() {
		return false;
	}

}
