package com.comodo.qa.browsers.tools;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;

public class FileHelper {
	@SuppressWarnings("rawtypes")
	public static File getFileFromRootFolder(String rootPath, String fileName) {
		File root = new File(rootPath);
        try {
            boolean recursive = true;

            Collection files = FileUtils.listFiles(root, null, recursive);

            for (Iterator iterator = files.iterator(); iterator.hasNext();) {
                File file = (File) iterator.next();
                if (file.getName().equals(fileName))
                    return file;
            }
        } catch (Exception e) {
            return null;
        }
        
        return null;
	}
	
}
