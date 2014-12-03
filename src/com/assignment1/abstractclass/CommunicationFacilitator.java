package com.assignment1.abstractclass;

import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;

import com.assignment1.utils.SequenceGenerator;

public class CommunicationFacilitator extends SequenceGenerator{
	public TreeMap<String, String> sortedQueue = new TreeMap<String, String>();
	private String instName;
	public CommunicationFacilitator(String instName){
		this.instName = instName;
	}
	public CommunicationFacilitator(){
		
	}
	public void pushToQueue(String key, String val){
		synchronized(sortedQueue){
			sortedQueue.put(key, val);
		}
	}
	
	public String popFirstVal(){
		synchronized (sortedQueue) {
			Map.Entry<String,String> e = sortedQueue.pollFirstEntry();
			if(e != null){
				String retVal = e.getValue();
				if(StringUtils.isNotBlank(instName)){
					if(retVal.contains(instName)){
						return retVal;
					}
				}
				else{
					return e.getValue();
				}
			}
			return null;
		}
	}
}
