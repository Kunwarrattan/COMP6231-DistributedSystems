package com.assignment1.utils;

import java.util.Date;

import com.assignment1.config.Configuration;

public abstract class SequenceGenerator {
	private Date timeStamp = new Date();
	private int index = 1;
	
	public synchronized String getUniqueTimeStamp(){
		timeStamp = new Date();
		return timeStamp.getTime() + Configuration.TIMESTAMP_SEPERATOR + ++index;
	}
}
