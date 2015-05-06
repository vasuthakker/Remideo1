package com.remedeo.remedeoapp.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.os.Environment;

public class GetImageFiles {
	
	public static List<String> getFiles() {
		// making a file list
		String targetPath = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/Remidio";
		File[] files = new File(targetPath).listFiles(); // 2
		
		List<String> filePathList = null;
		if (files != null) {
			filePathList = new ArrayList<String>();
			for (int j = 0; j < files.length; j++) {
				if (files[j].isDirectory()) {
					File[] innerFiles = new File(files[j].getAbsolutePath())
							.listFiles();
					
					for (File innerFile : innerFiles) {
						String extension = innerFile.getName().substring(
								innerFile.getName().lastIndexOf(".") + 1);
						if (innerFile.isFile()
								&& ((extension.equalsIgnoreCase("jpeg"))
										|| (extension.equalsIgnoreCase("JPEG")) || (extension
											.equalsIgnoreCase("mp4")))) {
							filePathList.add(innerFile.getAbsolutePath());
						}
					}
				}
			}
		}
		return filePathList;
	}
	
	// Get directory only
	public static List<File> getDirectoryOnly() {
		// making a file list
		String targetPath = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/Remidio";
		File[] files = new File(targetPath).listFiles(); // 2
		return Arrays.asList(files);
	}
	
	public static boolean checkFiles() {
		// making a file list
		boolean isAvailabale = false;
		boolean tempVar = false;
		String targetPath = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/Remidio";
		File[] files = new File(targetPath).listFiles(); // 2
		
		List<String> filePathList = null;
		if (files != null) {
			filePathList = new ArrayList<String>();
			for (int j = 0; j < files.length; j++) {
				if (files[j].isDirectory()) {
					File[] innerFiles = new File(files[j].getAbsolutePath())
							.listFiles();
					
					for (File innerFile : innerFiles) {
						String extension = innerFile.getName().substring(
								innerFile.getName().lastIndexOf(".") + 1);
						if (innerFile.isFile()
								&& ((extension.equalsIgnoreCase("jpeg"))
										|| (extension.equalsIgnoreCase("JPEG")) || (extension
											.equalsIgnoreCase("mp4")))) {
							isAvailabale = true;
							tempVar = true;
							break;
						}
					}
					if (tempVar) {
						break;
					}
				}
			}
		}
		return isAvailabale;
	}
	
	public static List<String> getFilesForDirectory(String directoryName) {
		// making a file list
		String targetPath = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/Remidio/" + directoryName;
		File[] files = new File(targetPath).listFiles(); // 2
		
		List<String> filePathList = new ArrayList<String>();
		if (files != null) {
			for (File innerFile : files) {
				String extension = innerFile.getName().substring(
						innerFile.getName().lastIndexOf(".") + 1);
				if (innerFile.isFile()
						&& ((extension.equalsIgnoreCase("jpeg"))
								|| (extension.equalsIgnoreCase("JPEG")) || (extension
									.equalsIgnoreCase("mp4")))) {
					filePathList.add(innerFile.getAbsolutePath());
				}
			}
			
		}
		return filePathList;
	}
}
