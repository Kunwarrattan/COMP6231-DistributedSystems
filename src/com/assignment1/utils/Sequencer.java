package com.assignment1.utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import com.assignment1.abstractclass.CommunicationFacilitator;
import com.assignment1.config.Configuration;
import com.assignment1.exception.CommunicationException;

public class Sequencer extends CommunicationFacilitator {
	private CommunicationManager mgr1 = null;
	private boolean stopServer = true;
	private HashMap<String, Set<String>> responseMap;
	private Object monitor = new Object();

	public Sequencer() throws IOException {
		responseMap = new HashMap<String, Set<String>>();
		mgr1 = new CommunicationManager(Configuration.MULTICAST_PORT,
				Configuration.SENDER_ROLE, Configuration.SEQUENCER_RECV_PORT,
				this);
	}

	public HashMap<String,Boolean> createAccount(String firstName, String lastName,
			String emailAddr, String phoneNumber, String userName,
			String password, String institutionName) throws IOException,
			CommunicationException {
		String data = Configuration.CREATE_ACCOUNT
				+ Configuration.UDP_DELIMITER + firstName
				+ Configuration.UDP_DELIMITER + lastName
				+ Configuration.UDP_DELIMITER + emailAddr
				+ Configuration.UDP_DELIMITER + phoneNumber
				+ Configuration.UDP_DELIMITER + userName
				+ Configuration.UDP_DELIMITER + password
				+ Configuration.UDP_DELIMITER + institutionName;
		String timeStamp = mgr1.sendMulticast(data);
		Set<String> responseSet = getResponseSet(timeStamp);
		HashMap<String, Boolean> responseMap = new HashMap<String, Boolean>();
		if(responseSet == null){
			throw new CommunicationException("Timed out..");
		}
		else {
			for(String reply : responseSet){
				String replyArry[] = reply.split(Configuration.UDP_DELIMITER);
				responseMap.put(replyArry[1], Boolean.valueOf(replyArry[2]));
			}
		}
		return responseMap;
	}
	
	public HashMap<String,Boolean> reserveBook(String userName, String password, String bookName,
			String authorName) throws IOException,
			CommunicationException {
		String data = Configuration.RESERVE_BOOK
				+ Configuration.UDP_DELIMITER + userName
				+ Configuration.UDP_DELIMITER + password
				+ Configuration.UDP_DELIMITER + bookName
				+ Configuration.UDP_DELIMITER + authorName;
		String timeStamp = mgr1.sendMulticast(data);
		Set<String> responseSet = getResponseSet(timeStamp);
		HashMap<String, Boolean> responseMap = new HashMap<String, Boolean>();
		if(responseSet == null){
			throw new CommunicationException("Timed out..");
		}
		else {
			for(String reply : responseSet){
				String replyArry[] = reply.split(Configuration.UDP_DELIMITER);
				responseMap.put(replyArry[1], Boolean.valueOf(replyArry[2]));
			}
		}
		return responseMap;
	}
	
	public HashMap<String,Boolean> reserveInterLibrary(String userName, String password,
			String bookName, String authorName) throws IOException,
			CommunicationException {
		String data = Configuration.RESERVE_INTER_LIBRARY
				+ Configuration.UDP_DELIMITER + userName
				+ Configuration.UDP_DELIMITER + password
				+ Configuration.UDP_DELIMITER + bookName
				+ Configuration.UDP_DELIMITER + authorName;
		String timeStamp = mgr1.sendMulticast(data);
		Set<String> responseSet = getResponseSet(timeStamp);
		HashMap<String, Boolean> responseMap = new HashMap<String, Boolean>();
		if(responseSet == null){
			throw new CommunicationException("Timed out..");
		}
		else {
			for(String reply : responseSet){
				String replyArry[] = reply.split(Configuration.UDP_DELIMITER);
				responseMap.put(replyArry[1], Boolean.valueOf(replyArry[2]));
			}
		}
		return responseMap;
	}
	
	public HashMap<String, String> getNonRetuners(String adminUserName, String adminPassword,
			String institutionName, int days) throws IOException, CommunicationException{
		String data = Configuration.RESERVE_INTER_LIBRARY
				+ Configuration.UDP_DELIMITER + adminUserName
				+ Configuration.UDP_DELIMITER + adminPassword
				+ Configuration.UDP_DELIMITER + institutionName
				+ Configuration.UDP_DELIMITER + days;
		String timeStamp = mgr1.sendMulticast(data);
		Set<String> responseSet = getResponseSet(timeStamp);
		HashMap<String, String> responseMap = new HashMap<String, String>();
		if(responseSet == null){
			throw new CommunicationException("Timed out..");
		}
		else {
			for(String reply : responseSet){
				String replyArry[] = reply.split(Configuration.UDP_DELIMITER);
				responseMap.put(replyArry[1], replyArry[3]);
			}
		}
		return responseMap;
	}

	public Set<String> getResponseSet(String timeStamp) {
		for (int i = 0; i < Configuration.MAX_NO_OF_TRIES; i++) {
			synchronized (monitor) {
				try {
					monitor.wait(Configuration.MAX_DURATION_TO_WAIT_BEFORE_TIMEOUT * 200);
					synchronized (responseMap) {
						Set<String> responseFromReplica = responseMap
								.get(timeStamp);
						if (responseFromReplica != null
								&& responseFromReplica.size() == ReplicaManager
										.listActiveReplicas().size()) {
							responseMap.remove(timeStamp);
							return responseFromReplica;
						}
						else if(responseFromReplica != null && i == 4){
							responseMap.remove(timeStamp);
						}
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	public void run() {
		while (stopServer) {
			String data = this.popFirstVal();
			if (data != null) {
				String[] arry;
				arry = data.split(Configuration.COMMUNICATION_SEPERATOR);
				String response = arry[1];
				String responseArry[] = response
						.split(Configuration.UDP_DELIMITER);
				synchronized (monitor) {
					Set<String> replicas = ReplicaManager.listActiveReplicas();
					boolean notifyAll = false;
					synchronized (responseMap) {
						Set<String> set = responseMap.get(responseArry[0]);
						if (set == null) {
							set = new HashSet<String>();
						}
						set.add(response);
						responseMap.put(responseArry[0], set);
						if (responseMap.size() == replicas.size()) {
							notifyAll = true;
						}
					}
					if (notifyAll)
						monitor.notifyAll();
				}
			}
		}
	}
}
