package com.assignment1.abstractclass;

import java.util.Map;
import java.util.TreeMap;

public abstract class CommunicationFacilitator {
	public TreeMap<String, String> sortedQueue = new TreeMap<String, String>();
	
	public void pushToQueue(String key, String val){
		synchronized(sortedQueue){
			sortedQueue.put(key, val);
		}
	}
	
	public String popFirstVal(){
		synchronized (sortedQueue) {
			Map.Entry<String,String> e = sortedQueue.pollFirstEntry();
			if(e != null){
				return e.getValue();
			}
			return null;
		}
	}
}
