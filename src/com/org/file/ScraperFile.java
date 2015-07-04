package com.org.file;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

public class ScraperFile {
	public static String read(String filepath){
		
		return "";
	}
	
	public static void write(String filepath, String content){
		Writer writer = null;

		try {
		    writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filepath), "utf-8"));
		    writer.write(content);
		    writer.close();
		} catch (IOException ex) {}
	}
}
