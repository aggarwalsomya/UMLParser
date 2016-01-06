package com.somya.UMLParser2;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class JavaFilesIterator {
	private String classPath;
	private List<Path> javaFilePaths = new ArrayList<Path>();

	// called by main()
	public JavaFilesIterator(String classPath) {
		this.classPath = classPath;
	}

	// returns all the paths of .java files in the classPath folder
	public List<String> iterateClassFolder() {
		List<String> javaFilePaths = new ArrayList<String>();
		String className = "";

		File dir = new File(classPath);
		if (!dir.isDirectory()) {
			System.out.println("Entered Classpath is not a directory");
			return javaFilePaths;
		}

		for (File file : dir.listFiles()) {
			if (file.getName().endsWith((".java"))) {
				className = file.getName();
				className = className.substring(0, className.lastIndexOf('.'));
				javaFilePaths.add(file.getAbsolutePath());
			}
		}

		if (javaFilePaths.isEmpty()) {
			System.out.println("Given Folder ( " + classPath
					+ " ) doesn't have any java files. Please provide the correct folder.");
		}
		return javaFilePaths;
	}

}
