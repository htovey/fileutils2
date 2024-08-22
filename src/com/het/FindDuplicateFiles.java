package com.het;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Deque;
import java.util.ArrayDeque;
import java.util.Map.Entry;

public class FindDuplicateFiles {

	private static int SAMPLE_SIZE = 10;
	
	public static void main(String[] args) {
		//input:  file path string
		//output: list of duplicate files
		String filePath = args[0];
		List<String> duplicateList = getDuplicateFileList(filePath);
		for (String str : duplicateList) {
			System.out.println("duplicate found: "+str);
		}
	}

	private static List<String> getDuplicateFileList(String filePath) {
		Map<String, Integer> dupeMap = new HashMap<String, Integer>();
		List<String> duplicateList = new ArrayList<String>();
		File file = new File(filePath);
		Deque<File> fileStack = new ArrayDeque<File>();
		fileStack.push(file);
		
		while(!fileStack.isEmpty()) {
			File currentFile = fileStack.pop();
			if (currentFile.isDirectory()) {
				for (File subFile : currentFile.listFiles()) {
					fileStack.push(subFile);
				}
			} else {
				try {
					String fileKey = getFileKey(currentFile);
					if (dupeMap.get(fileKey) != null) {
						int occurences = dupeMap.get(fileKey);
						dupeMap.put(fileKey, occurences+=1);
					} else {
						dupeMap.put(fileKey, 1);
					}
				} catch (NoSuchAlgorithmException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		for (Entry<String, Integer> entry : dupeMap.entrySet()) {
			if (entry.getValue() > 1) {
				duplicateList.add(entry.getKey());
			}
		}
		return duplicateList;
	}
	
	private static String getFileKey(File file) throws NoSuchAlgorithmException, IOException {
		FileInputStream fis = new FileInputStream(file);
		MessageDigest md = MessageDigest.getInstance("SHA-512");
		//interval between 3 samples = file length - (sample size * 3) / 2
		long sampleInterval = (file.length() - (SAMPLE_SIZE * 3)) / 2;
		DigestInputStream dis = new DigestInputStream(fis, md);
		byte [] targetBytes = new byte [SAMPLE_SIZE * 3];
		dis.read(targetBytes, 0, SAMPLE_SIZE);
		dis.skip(sampleInterval);
		dis.read(targetBytes, SAMPLE_SIZE, SAMPLE_SIZE);
		dis.skip(sampleInterval);
		dis.read(targetBytes, SAMPLE_SIZE*2, SAMPLE_SIZE);
		dis.close();
		return new BigInteger(1, md.digest()).toString(16);
	}
	
	private static class FileInfo {
		String fileName;
		int modifiedTime;
	}
}
