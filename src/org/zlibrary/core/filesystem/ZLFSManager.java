package org.zlibrary.core.filesystem;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import org.zlibrary.core.util.*;

abstract class ZLFSManager {
	private final HashMap myForcedFiles = new HashMap();
	protected static ZLFSManager ourInstance;
	
	public static void deleteInstance() {
		if (ourInstance != null) {
			ourInstance = null;
		}
	}
	
	public HashMap getForcedFiles() {
		return myForcedFiles;
	}
	
	public void putForcedFile(String key, int value) {
		myForcedFiles.put(key, value);
	}
	
	public static ZLFSManager instance() {
		return ourInstance;
	}

		
	//protected ZLFSManager();
		
	public void normalize(String path) {}

	abstract protected InputStream createPlainInputStream(String path);
	abstract protected OutputStream createOutputStream(String path);
	//abstract protected ZLFSDir createPlainDirectory(String path);
	//abstract protected ZLFSDir createNewDirectory(String path);
	abstract protected ZLFileInfo fileInfo(String path);
	abstract protected boolean removeFile(String path);
	abstract protected String convertFilenameToUtf8(String name);

	abstract protected int findArchiveFileNameDelimiter(String path);
	abstract protected int findLastFileNameDelimiter(String path);
	abstract protected ZLDir rootDirectory();
	abstract protected String rootDirectoryPath();
	abstract protected String parentPath(String path);
}