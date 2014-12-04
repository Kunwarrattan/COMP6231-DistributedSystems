package com.assignment1.utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import com.assignment1.abstractclass.CommunicationFacilitator;
import com.assignment1.config.Configuration;
import com.assignment1.exception.CommunicationException;

public class Sequencer extends CommunicationFacilitator {
	private CommunicationManager mgr1 = null;
	private boolean stopServer = true;
	private HashMap<String, Set<String>> responseMap;
	private HashMap<String, String> responseMapForRM;
	private Object monitor = new Object();
	private Object monitor1 = new Object();

	public Sequencer() throws IOException {
		responseMap = new HashMap<String, Set<String>>();
		responseMapForRM = new HashMap<String, String>();
		mgr1 = new CommunicationManager(Configuration.MULTICAST_PORT,
				Configuration.SENDER_ROLE, Configuration.SEQUENCER_RECV_PORT,
				this);
	}

	public void createAccount(String firstName, String lastName,
			String emailAddr, String phoneNumber, String userName,
			String password, String institutionName, String timestamp) {
		try {
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
			if (responseSet == null) {
				throw new CommunicationException("Timed out..");
			} else {
				String response = "";
				int i = 0;
				for (String reply : responseSet) {
					String replyArry[] = reply
							.split(Configuration.UDP_DELIMITER);
					response += replyArry[1] + Configuration.UDP_DELIMITER_SEQ
							+ replyArry[2];
					if (i != responseSet.size() - 1) {
						response += Configuration.UDP_DELIMITER;
					}
					i++;
				}
				if (response.length() != 0) {
					response = timestamp + Configuration.UDP_DELIMITER +response;
					mgr1.send(response, Configuration.FRONTEND_IP,
							Configuration.UDP_FRONTEND_PORT_IMP);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void reserveBook(String userName, String password, String bookName,
			String authorName, String instName, String timestamp) {
		try {
			String data = Configuration.RESERVE_BOOK
					+ Configuration.UDP_DELIMITER + userName
					+ Configuration.UDP_DELIMITER + password
					+ Configuration.UDP_DELIMITER + bookName
					+ Configuration.UDP_DELIMITER + authorName
					+ Configuration.UDP_DELIMITER + instName;
			String timeStamp = mgr1.sendMulticast(data);
			Set<String> responseSet = getResponseSet(timeStamp);
			if (responseSet == null) {
				throw new CommunicationException("Timed out..");
			} else {
				String response = "";
				int i = 0;
				for (String reply : responseSet) {
					String replyArry[] = reply
							.split(Configuration.UDP_DELIMITER);
					response += replyArry[1] + Configuration.UDP_DELIMITER_SEQ
							+ replyArry[2];
					if (i != responseSet.size() - 1) {
						response += Configuration.UDP_DELIMITER;
					}
					i++;
				}
				if (response.length() != 0) {
					response = timestamp + Configuration.UDP_DELIMITER +response;
					mgr1.send(response, Configuration.FRONTEND_IP,
							Configuration.UDP_FRONTEND_PORT_IMP);
				}
			}
			// return responseMap;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void reserveInterLibrary(String userName, String password,
			String bookName, String authorName, String instName, String timestamp) {
		try {
			String data = Configuration.RESERVE_INTER_LIBRARY
					+ Configuration.UDP_DELIMITER + userName
					+ Configuration.UDP_DELIMITER + password
					+ Configuration.UDP_DELIMITER + bookName
					+ Configuration.UDP_DELIMITER + authorName
					+ Configuration.UDP_DELIMITER + instName;
			String timeStamp = mgr1.sendMulticast(data);
			Set<String> responseSet = getResponseSet(timeStamp);
			if (responseSet == null) {
				throw new CommunicationException("Timed out..");
			} else {
				String response = "";
				int i = 0;
				for (String reply : responseSet) {
					String replyArry[] = reply
							.split(Configuration.UDP_DELIMITER);
					response += replyArry[1] + Configuration.UDP_DELIMITER_SEQ
							+ replyArry[2];
					if (i != responseSet.size() - 1) {
						response += Configuration.UDP_DELIMITER;
					}
					i++;
				}
				if (response.length() != 0) {
					response = timestamp + Configuration.UDP_DELIMITER +response;
					mgr1.send(response, Configuration.FRONTEND_IP,
							Configuration.UDP_FRONTEND_PORT_IMP);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// return responseMap;
	}

	public void getNonRetuners(String adminUserName, String adminPassword,
			String institutionName, String days, String timestamp) {
		try {
			String data = Configuration.RESERVE_INTER_LIBRARY
					+ Configuration.UDP_DELIMITER + adminUserName
					+ Configuration.UDP_DELIMITER + adminPassword
					+ Configuration.UDP_DELIMITER + institutionName
					+ Configuration.UDP_DELIMITER + days;
			String timeStamp = mgr1.sendMulticast(data);
			Set<String> responseSet = getResponseSet(timeStamp);
			if (responseSet == null) {
				throw new CommunicationException("Timed out..");
			} else {
				String response = "";
				int i = 0;
				for (String reply : responseSet) {
					String replyArry[] = reply
							.split(Configuration.UDP_DELIMITER);
					response += replyArry[1] + Configuration.UDP_DELIMITER_SEQ
							+ replyArry[3];
					if (i != responseSet.size() - 1) {
						response += Configuration.UDP_DELIMITER;
					}
					i++;
				}
				if (response.length() != 0) {
					response = timestamp + Configuration.UDP_DELIMITER +response;
					mgr1.send(response, Configuration.FRONTEND_IP,
							Configuration.UDP_FRONTEND_PORT_IMP);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// return responseMap;
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
								&& responseFromReplica.size() == listTotActiveReplicas()) {
							responseMap.remove(timeStamp);
							return responseFromReplica;
						} else if (responseFromReplica != null && i == 4) {
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

	public String getResponseFromRM(String timeStamp) {
		for (int i = 0; i < Configuration.MAX_NO_OF_TRIES; i++) {
			synchronized (monitor1) {
				try {
					monitor1.wait(Configuration.MAX_DURATION_TO_WAIT_BEFORE_TIMEOUT * 100);
					synchronized (responseMapForRM) {
						String responseFromRM = responseMapForRM.get(timeStamp);
						if (responseFromRM != null || i == 4) {
							responseMapForRM.remove(timeStamp);
							return responseFromRM;
						}
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	public int listTotActiveReplicas() {
		String timeStamp;
		try {
			timeStamp = mgr1.send(Configuration.LIST_ACTIVE_REPLICA,
					Configuration.DEAMON_RM_IP, Configuration.RM_RECV_PORT);
			String response = getResponseFromRM(timeStamp);
			if(response != null){
				return Integer.parseInt(response.split(Configuration.UDP_DELIMITER)[2]);
			}
		} catch (CommunicationException | IOException | InterruptedException
				| ExecutionException | TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}

	public void run() {
		while (stopServer) {
			String data = this.popFirstVal();
			if (data != null) {
				String[] arry;
				arry = data.split(Configuration.COMMUNICATION_SEPERATOR);
				String timestamp = arry[0];
				String response = arry[1];
				String responseArry[] = response
						.split(Configuration.UDP_DELIMITER);
				int i = 0;
				if (response.contains(Configuration.CREATE_ACCOUNT)) {
					createAccount(responseArry[i++], responseArry[i++],
							responseArry[i++], responseArry[i++],
							responseArry[i++], responseArry[i++],
							responseArry[i++],timestamp);
				} else if (response.contains(Configuration.RESERVE_BOOK)) {
					reserveBook(responseArry[i++], responseArry[i++],
							responseArry[i++], responseArry[i++],
							responseArry[i++],timestamp);
				} else if (response
						.contains(Configuration.RESERVE_INTER_LIBRARY)) {
					reserveInterLibrary(responseArry[i++], responseArry[i++],
							responseArry[i++], responseArry[i++],
							responseArry[i++],timestamp);
				} else if (response.contains(Configuration.GET_NON_RETUNERS)) {
					getNonRetuners(responseArry[i++], responseArry[i++],
							responseArry[i++], responseArry[i++],timestamp);
				}
				else if(response.contains(Configuration.REPLICA_COUNT_STR)){
					synchronized (monitor1) {
						synchronized (responseMapForRM) {
							responseMapForRM.put(responseArry[0], response);
						}
						monitor1.notifyAll();
					}
				}
				else {
					synchronized (monitor) {
						boolean notifyAll = false;
						synchronized (responseMap) {
							Set<String> set = responseMap.get(responseArry[0]);
							if (set == null) {
								set = new HashSet<String>();
							}
							set.add(response);
							responseMap.put(responseArry[0], set);
							int totReplica = this.listTotActiveReplicas();
							if (set.size() == totReplica && totReplica != 0) {
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
}
