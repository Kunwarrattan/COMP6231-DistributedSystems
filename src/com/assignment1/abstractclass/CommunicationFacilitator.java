package com.assignment1.abstractclass;

import java.util.Map;
import java.util.TreeMap;

public abstract class CommunicationFacilitator {
	public TreeMap<Integer, String> sortedQueue;
	
	public void pushToQueue(Integer key, String val){
		synchronized(sortedQueue){
			sortedQueue.put(key, val);
		}
	}
	
	public String popFirstVal(){
		synchronized (sortedQueue) {
			Map.Entry<Integer,String> e = sortedQueue.pollFirstEntry();
			if(e != null){
				return e.getValue();
			}
			return null;
		}
	}
}
