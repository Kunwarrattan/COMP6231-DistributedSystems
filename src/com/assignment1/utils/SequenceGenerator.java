package com.assignment1.utils;

import java.util.Date;

import com.assignment1.config.Configuration;

public class SequenceGenerator {
	public static Date timeStamp = new Date();
	public static int index = 1;
	public static synchronized String getTimeStamp(){
		timeStamp = new Date();
		return timeStamp.getTime() + Configuration.TIMESTAMP_SEPERATOR + index;
	}
}
