package com.assignment1.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;

import com.assignment1.config.Configuration;

public class FileOps {

	public static void initServer(String serverName) throws Exception {
		String temp = Configuration.CURRENT_DIR + serverName + "//";
		File folder = new File(temp);
		if (!folder.exists()) {
			try {
				FileUtils.forceMkdir(new File(temp));
				File userFolder = new File(temp + Configuration.USER_DIR);
				if (!userFolder.exists()) {
					FileUtils
							.forceMkdir(new File(temp + Configuration.USER_DIR));
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}
		}
	}

	public static Object deserializeObjFromFile(String fileName)
			throws Exception {
		FileInputStream fileIn = null;
		ObjectInputStream in = null;
		Object obj = null;
		try {
			fileIn = new FileInputStream(fileName);
			in = new ObjectInputStream(fileIn);
			obj = in.readObject();
		} catch (IOException i) {
			i.printStackTrace();
			throw i;
		} catch (ClassNotFoundException c) {
			c.printStackTrace();
			throw c;
		} finally {
			if (in != null) {
				in.close();
			}
			if (fileIn != null) {
				fileIn.close();
			}
		}
		return obj;
	}

	public static void serializeObjToFile(String fileName, Object obj)
			throws Exception {
		FileOutputStream fileOut = null;
		ObjectOutputStream out = null;
		try {
			fileOut = new FileOutputStream(fileName);
			out = new ObjectOutputStream(fileOut);
			out.writeObject(obj);
		} catch (IOException i) {
			i.printStackTrace();
			throw i;
		}
		finally{
			if(out != null){
				out.close();
			}
			if(fileOut != null){
				fileOut.close();
			}
		}
	}
	public static void appendToFile(String fileName, StringBuilder content){
		File file = new File(fileName);
		SimpleDateFormat fmt = new SimpleDateFormat(Configuration.DATE_FMT_PTTRN);
		if(content != null && content.length() > 0){
			content.insert(0, fmt.format(new Date()));
			content.append(System.lineSeparator());
			String temp = fileName + " : "+content.toString();
			System.out.println(temp);
			try{
				FileUtils.writeStringToFile(file,content.toString(), true);
			}
			catch(Exception e){
				e.printStackTrace();
			}
			
		}
	}
}
