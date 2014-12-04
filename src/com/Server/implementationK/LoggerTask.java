package com.Server.implementationK;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class LoggerTask {
	
	private static BufferedWriter Logger;
		public LoggerTask() throws FileNotFoundException{
			
		}
	
		public void WriteLog(String str) throws IOException{
			try {
				if(Logger == null){
					Logger = new BufferedWriter(
							 new OutputStreamWriter(
							 new FileOutputStream("serverLog.txt",true)));
				}
				Logger.append(str);
				Logger.newLine();
				Logger.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		public void closeQuietly(){
			try {
				Logger.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
}
