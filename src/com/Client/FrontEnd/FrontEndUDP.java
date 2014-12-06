package com.Client.FrontEnd;

import java.io.IOException;
import java.net.SocketException;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import IdlFiles.LibraryException;
import IdlFiles.LibraryManagementInterfacePOA;

import com.assignment1.abstractclass.CommunicationFacilitator;
import com.assignment1.config.Configuration;
import com.assignment1.exception.CommunicationException;
import com.assignment1.utils.CommunicationManager;

class CommunicationHelper extends CommunicationFacilitator {

}

public class FrontEndUDP extends LibraryManagementInterfacePOA implements
		Runnable {

	CommunicationManager MGR = null;
	CommunicationHelper chelper = new CommunicationHelper();
	public volatile boolean stopServer = true;
	private HashMap<String, String> responseMap;
	private Object monitor = new Object();

	public FrontEndUDP() throws SocketException {
		super();
		responseMap = new HashMap<String, String>();
		MGR = new CommunicationManager(Configuration.UDP_FRONTEND_PORT_IMP,
				chelper);
		new Thread(this).start();
	}

	// create account
	@Override
	public void createAccount(String firstName, String lastName,
			String emailAddr, String phoneNumber, String userName,
			String password, String institutionName) throws LibraryException {
		try {
			String data = Configuration.CREATE_ACCOUNT
					+ Configuration.UDP_DELIMITER + firstName
					+ Configuration.UDP_DELIMITER + lastName
					+ Configuration.UDP_DELIMITER + emailAddr
					+ Configuration.UDP_DELIMITER + phoneNumber
					+ Configuration.UDP_DELIMITER + userName
					+ Configuration.UDP_DELIMITER + password
					+ Configuration.UDP_DELIMITER + institutionName;
			System.out.println("Request from "+ userName + " To Server " + institutionName + " received at " + new Date().getTime() + " for creating account for " + firstName + " " + lastName);		
			String timeStamp = MGR.send(data, Configuration.SEQUENCER_IP,
					Configuration.SEQUENCER_RECV_PORT);
			System.out.println("FrontEnd : createAccount ?: timestamp "
					+ timeStamp);
			String responseSet = getResponseSet(timeStamp);
			System.out.println("FrontEnd : set : "+responseSet);
			HashMap<String, Boolean> responseMap = new HashMap<String, Boolean>();
			if (responseSet == null) {
				throw new LibraryException("Timed out..");
			} else {
				for (String reply : responseSet
						.split(Configuration.UDP_DELIMITER)) {
					String replyArry[] = reply
							.split(Configuration.UDP_DELIMITER_SEQ);
					responseMap.put(replyArry[0],
							Boolean.parseBoolean(replyArry[1]));
				}
				System.out.println(responseMap);
			}
			if (responseMap.size() != 1) {
				if (responseMap.get(Configuration.REPLICA1) != null
						&& responseMap.get(Configuration.REPLICA1).equals(
								responseMap.get(Configuration.REPLICA2))) {
					if (responseMap.get(Configuration.REPLICA1).equals(
							responseMap.get(Configuration.REPLICA3))) {
						
						return;
					} else {
						String flag = Configuration.ERROR_IN_OUTPUT_STRING
								+ Configuration.UDP_DELIMITER
								+ Configuration.REPLICA3;
						System.out
								.println("FrontEnd : createAccount : Failure detected in "
										+ Configuration.REPLICA3);
						if(responseMap.get(Configuration.REPLICA3) != null &&  responseMap.get(Configuration.REPLICA3)){
							throw new LibraryException("The intented operation was not successfull");
						}
						else if(responseMap.get(Configuration.REPLICA3) == null && !responseMap.get(Configuration.REPLICA1)){
							throw new LibraryException("The intented operation was not successfull");
						}
						MGR.send(flag, Configuration.DEAMON_RM_IP,
								Configuration.RM_RECV_PORT);
					}
				} else if (responseMap.get(Configuration.REPLICA1) != null
						&& responseMap.get(Configuration.REPLICA1).equals(
								responseMap.get(Configuration.REPLICA3))) {
					String flag = Configuration.ERROR_IN_OUTPUT_STRING
							+ Configuration.UDP_DELIMITER
							+ Configuration.REPLICA2;
					System.out
							.println("FrontEnd : createAccount : Failure detected in "
									+ Configuration.REPLICA2);
					if(responseMap.get(Configuration.REPLICA2) != null && responseMap.get(Configuration.REPLICA2)){
						throw new LibraryException("The intented operation was not successfull");
					}
					else if(responseMap.get(Configuration.REPLICA2) == null && !responseMap.get(Configuration.REPLICA1)){
						throw new LibraryException("The intented operation was not successfull");
					}
					MGR.send(flag, Configuration.DEAMON_RM_IP,
							Configuration.RM_RECV_PORT);
				} else if (responseMap.get(Configuration.REPLICA2) != null
						&& responseMap.get(Configuration.REPLICA2).equals(
								responseMap.get(Configuration.REPLICA3))) {
					String flag = Configuration.ERROR_IN_OUTPUT_STRING
							+ Configuration.UDP_DELIMITER
							+ Configuration.REPLICA1;
					System.out
							.println("FrontEnd : createAccount : Failure detected in "
									+ Configuration.REPLICA1);
					if(responseMap.get(Configuration.REPLICA1)){
						throw new LibraryException("The intented operation was not successfull");
					}
					MGR.send(flag, Configuration.DEAMON_RM_IP,
							Configuration.RM_RECV_PORT);
				}
			}

		} catch (CommunicationException | IOException | InterruptedException
				| ExecutionException | TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new LibraryException(e.getMessage());
		}
		return;
	}

	@Override
	public void reserveBook(String userName, String password, String bookName,
			String authorName, String institutionName) throws LibraryException {
		try {
			String data = Configuration.RESERVE_BOOK
					+ Configuration.UDP_DELIMITER + userName
					+ Configuration.UDP_DELIMITER + password
					+ Configuration.UDP_DELIMITER + bookName
					+ Configuration.UDP_DELIMITER + authorName
					+ Configuration.UDP_DELIMITER + institutionName;
			System.out.println("Request from "+ userName + " To Server " + institutionName + " received at " + new Date().getTime() + " for reservebook for Bookname "+ bookName +  " Authore name " + authorName);
			String timeStamp = MGR.send(data, Configuration.SEQUENCER_IP,
					Configuration.SEQUENCER_RECV_PORT);
			System.out.println("FrontEnd : reserveBook ?: timestamp "
					+ timeStamp);
			String responseSet = getResponseSet(timeStamp);
			HashMap<String, Boolean> responseMap = new HashMap<String, Boolean>();
			if (responseSet == null) {
				throw new LibraryException("Timed out..");
			} else {
				for (String reply : responseSet
						.split(Configuration.UDP_DELIMITER)) {
					String replyArry[] = reply
							.split(Configuration.UDP_DELIMITER_SEQ);
					responseMap.put(replyArry[0],
							Boolean.parseBoolean(replyArry[1]));
				}
			}
			System.out.println(responseMap);
			if (responseMap.size() != 1) {
				if (responseMap.get(Configuration.REPLICA1) != null
						&& responseMap.get(Configuration.REPLICA1).equals(
								responseMap.get(Configuration.REPLICA2))) {
					if (responseMap.get(Configuration.REPLICA1).equals(
							responseMap.get(Configuration.REPLICA3))) {
						return;
					} else {
						String flag = Configuration.ERROR_IN_OUTPUT_STRING
								+ Configuration.UDP_DELIMITER
								+ Configuration.REPLICA3;
						System.out
								.println("FrontEnd : reserveBook : Failure detected in "
										+ Configuration.REPLICA3);
						if(responseMap.get(Configuration.REPLICA3) != null &&  responseMap.get(Configuration.REPLICA3)){
							throw new LibraryException("The intented operation was not successfull");
						}
						else if(responseMap.get(Configuration.REPLICA3) == null && !responseMap.get(Configuration.REPLICA1)){
							throw new LibraryException("The intented operation was not successfull");
						}
						MGR.send(flag, Configuration.DEAMON_RM_IP,
								Configuration.RM_RECV_PORT);
					}
				} else if (responseMap.get(Configuration.REPLICA1) != null
						&& responseMap.get(Configuration.REPLICA1).equals(
								responseMap.get(Configuration.REPLICA3))) {
					String flag = Configuration.ERROR_IN_OUTPUT_STRING
							+ Configuration.UDP_DELIMITER
							+ Configuration.REPLICA2;
					System.out
							.println("FrontEnd : reserveBook : Failure detected in "
									+ Configuration.REPLICA2);
					if(responseMap.get(Configuration.REPLICA2) != null && responseMap.get(Configuration.REPLICA2)){
						throw new LibraryException("The intented operation was not successfull");
					}
					else if(responseMap.get(Configuration.REPLICA2) == null && !responseMap.get(Configuration.REPLICA1)){
						throw new LibraryException("The intented operation was not successfull");
					}
					MGR.send(flag, Configuration.DEAMON_RM_IP,
							Configuration.RM_RECV_PORT);
				} else if (responseMap.get(Configuration.REPLICA2) != null
						&& responseMap.get(Configuration.REPLICA2).equals(
								responseMap.get(Configuration.REPLICA3))) {
					String flag = Configuration.ERROR_IN_OUTPUT_STRING
							+ Configuration.UDP_DELIMITER
							+ Configuration.REPLICA1;
					System.out
							.println("FrontEnd : reserveBook : Failure detected in "
									+ Configuration.REPLICA1);
					if(responseMap.get(Configuration.REPLICA1)){
						throw new LibraryException("The intented operation was not successfull");
					}
					MGR.send(flag, Configuration.DEAMON_RM_IP,
							Configuration.RM_RECV_PORT);
				}
			}

		} catch (CommunicationException | IOException | InterruptedException
				| ExecutionException | TimeoutException e) {
			e.printStackTrace();
			throw new LibraryException(e.getMessage());
		}

	}

	@Override
	public void exit() {

	}

	@Override
	public void reserveInterLibrary(String userName, String password,
			String bookName, String authorName, String institutionName)
			throws LibraryException {
		try {
			String data = Configuration.RESERVE_INTER_LIBRARY

			+ Configuration.UDP_DELIMITER + userName
					+ Configuration.UDP_DELIMITER + password
					+ Configuration.UDP_DELIMITER + bookName
					+ Configuration.UDP_DELIMITER + authorName
					+ Configuration.UDP_DELIMITER + institutionName;
			System.out.println("Request from "+ userName + " To Server " + institutionName + " received at " + new Date().getTime() + " for reservebook for Bookname "+ bookName +  " Authore name " + authorName);
			String timeStamp = MGR.send(data, Configuration.SEQUENCER_IP,
					Configuration.SEQUENCER_RECV_PORT);
			System.out.println("FrontEnd : reserveInterLibrary ?: timestamp "
					+ timeStamp);
			String responseSet = getResponseSet(timeStamp);
			HashMap<String, Boolean> responseMap = new HashMap<String, Boolean>();
			if (responseSet == null) {
				throw new LibraryException("Timed out..");
			} else {
				for (String reply : responseSet
						.split(Configuration.UDP_DELIMITER)) {
					String replyArry[] = reply
							.split(Configuration.UDP_DELIMITER_SEQ);
					responseMap.put(replyArry[0],
							Boolean.parseBoolean(replyArry[1]));
				}
			}
			System.out.println(responseMap);
			if (responseMap.size() != 1) {
				if (responseMap.get(Configuration.REPLICA1) != null
						&& responseMap.get(Configuration.REPLICA1).equals(
								responseMap.get(Configuration.REPLICA2))) {
					if (responseMap.get(Configuration.REPLICA1).equals(
							responseMap.get(Configuration.REPLICA3))) {
						return;
					} else {
						String flag = Configuration.ERROR_IN_OUTPUT_STRING
								+ Configuration.UDP_DELIMITER
								+ Configuration.REPLICA3;
						System.out
								.println("FrontEnd : reserveInterLibrary : Failure detected in "
										+ Configuration.REPLICA3);
						
						if(responseMap.get(Configuration.REPLICA3) != null &&  responseMap.get(Configuration.REPLICA3)){
							throw new LibraryException("The intented operation was not successfull");
						}
						else if(responseMap.get(Configuration.REPLICA3) == null && !responseMap.get(Configuration.REPLICA1)){
							throw new LibraryException("The intented operation was not successfull");
						}
						MGR.send(flag, Configuration.DEAMON_RM_IP,
								Configuration.RM_RECV_PORT);
					}
				} else if (responseMap.get(Configuration.REPLICA1) != null
						&& responseMap.get(Configuration.REPLICA1).equals(
								responseMap.get(Configuration.REPLICA3))) {
					String flag = Configuration.ERROR_IN_OUTPUT_STRING
							+ Configuration.UDP_DELIMITER
							+ Configuration.REPLICA2;
					System.out
							.println("FrontEnd : reserveInterLibrary : Failure detected in "
									+ Configuration.REPLICA2);
					
					if(responseMap.get(Configuration.REPLICA2) != null && responseMap.get(Configuration.REPLICA2)){
						throw new LibraryException("The intented operation was not successfull");
					}
					else if(responseMap.get(Configuration.REPLICA2) == null && !responseMap.get(Configuration.REPLICA1)){
						throw new LibraryException("The intented operation was not successfull");
					}
					MGR.send(flag, Configuration.DEAMON_RM_IP,
							Configuration.RM_RECV_PORT);
				} else if (responseMap.get(Configuration.REPLICA2) != null
						&& responseMap.get(Configuration.REPLICA2).equals(
								responseMap.get(Configuration.REPLICA3))) {
					String flag = Configuration.ERROR_IN_OUTPUT_STRING
							+ Configuration.UDP_DELIMITER
							+ Configuration.REPLICA1;
					System.out
							.println("FrontEnd : reserveInterLibrary : Failure detected in "
									+ Configuration.REPLICA1);
					
					if(responseMap.get(Configuration.REPLICA1) != null && responseMap.get(Configuration.REPLICA1)){
						throw new LibraryException("The intented operation was not successfull");
					}
					else if(responseMap.get(Configuration.REPLICA1) == null && !responseMap.get(Configuration.REPLICA2)){
						throw new LibraryException("The intented operation was not successfull");
					}
					MGR.send(flag, Configuration.DEAMON_RM_IP,
							Configuration.RM_RECV_PORT);
				}
			}

		} catch (CommunicationException | IOException | InterruptedException
				| ExecutionException | TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new LibraryException(e.getMessage());
		}
	}

	@Override
	public String getNonRetuners(String adminUserName, String adminPassword,
			String institutionName, int days) throws LibraryException {
		String retString = "";
		try {
			String data = Configuration.RESERVE_INTER_LIBRARY
					+ Configuration.UDP_DELIMITER + adminUserName
					+ Configuration.UDP_DELIMITER + adminPassword
					+ Configuration.UDP_DELIMITER + institutionName
					+ Configuration.UDP_DELIMITER + days;
			System.out.println("Request from "+ adminUserName + " To Server " + institutionName + " received at " + new Date().getTime() + " for check non returner lists");
			String timeStamp = MGR.send(data, Configuration.SEQUENCER_IP,
					Configuration.SEQUENCER_RECV_PORT);
			System.out.println("FrontEnd : getNonRetuners ?: timestamp "
					+ timeStamp);
			String responseSet = getResponseSet(timeStamp);
			HashMap<String, String> responseMap = new HashMap<String, String>();
			if (responseSet == null) {
				throw new LibraryException("Timed out..");
			} else {
				for (String reply : responseSet
						.split(Configuration.UDP_DELIMITER)) {
					String replyArry[] = reply
							.split(Configuration.UDP_DELIMITER_SEQ);
					responseMap.put(replyArry[0], replyArry[1]);
				}
			}
			System.out.println(responseMap);
			if (responseMap.size() != 1) {
				if (responseMap.get(Configuration.REPLICA1) != null
						&& responseMap.get(Configuration.REPLICA1).equals(
								responseMap.get(Configuration.REPLICA2))) {
					if (responseMap.get(Configuration.REPLICA1).equals(
							responseMap.get(Configuration.REPLICA3))) {

					} else {
						String flag = Configuration.ERROR_IN_OUTPUT_STRING
								+ Configuration.UDP_DELIMITER
								+ Configuration.REPLICA3;
						System.out
								.println("FrontEnd : getNonRetuners : Failure detected in "
										+ Configuration.REPLICA3);
						MGR.send(flag, Configuration.DEAMON_RM_IP,
								Configuration.RM_RECV_PORT);
					}
					retString = responseMap.get(Configuration.REPLICA1);
				} else if (responseMap.get(Configuration.REPLICA1) != null
						&& responseMap.get(Configuration.REPLICA1).equals(
								responseMap.get(Configuration.REPLICA3))) {
					String flag = Configuration.ERROR_IN_OUTPUT_STRING
							+ Configuration.UDP_DELIMITER
							+ Configuration.REPLICA2;
					System.out
							.println("FrontEnd : getNonRetuners : Failure detected in "
									+ Configuration.REPLICA2);
					MGR.send(flag, Configuration.DEAMON_RM_IP,
							Configuration.RM_RECV_PORT);
					retString = responseMap.get(Configuration.REPLICA1);
				} else if (responseMap.get(Configuration.REPLICA2) != null
						&& responseMap.get(Configuration.REPLICA2).equals(
								responseMap.get(Configuration.REPLICA3))) {
					String flag = Configuration.ERROR_IN_OUTPUT_STRING
							+ Configuration.UDP_DELIMITER
							+ Configuration.REPLICA1;
					System.out
							.println("FrontEnd : getNonRetuners : Failure detected in "
									+ Configuration.REPLICA1);
					MGR.send(flag, Configuration.DEAMON_RM_IP,
							Configuration.RM_RECV_PORT);
					retString = responseMap.get(Configuration.REPLICA2);
				}
			}
			else{
				if(responseMap.get(Configuration.REPLICA1) != null){
					retString = responseMap.get(Configuration.REPLICA1);
				}
				else if(responseMap.get(Configuration.REPLICA2) != null){
					retString = responseMap.get(Configuration.REPLICA2);
				}
				else{
					retString = responseMap.get(Configuration.REPLICA3);
				}
			}

		} catch (CommunicationException | IOException | InterruptedException
				| ExecutionException | TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new LibraryException(e.getMessage());
		}

		return retString;
	}

	public String getResponseSet(String timeStamp) {
		for (int i = 0; i < Configuration.MAX_NO_OF_TRIES; i++) {
			synchronized (monitor) {
				try {
					monitor.wait(Configuration.MAX_DURATION_TO_WAIT_BEFORE_TIMEOUT * 400);
					synchronized (responseMap) {
						String responseFromReplica = responseMap.get(timeStamp);
						if (responseFromReplica != null || i == 4) {
							responseMap.remove(timeStamp);
							return responseFromReplica;
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
			String data = chelper.popFirstVal();
			if (data != null) {
				System.out.println("FrontEnd: Response : "+data);
				String[] arry;
				arry = data.split(Configuration.COMMUNICATION_SEPERATOR);
				String response = arry[1];
				String responseArry[] = response
						.split(Configuration.UDP_DELIMITER);
				response = "";
				for (int i = 1; i < responseArry.length; i++) {
					response += responseArry[i];
					if (i != responseArry.length - 1) {
						response += Configuration.UDP_DELIMITER;
					}
				}
				synchronized (monitor) {
					synchronized (responseMap) {
						responseMap.put(responseArry[0], response);
					}
					monitor.notifyAll();
				}
			}
			try {
				Thread.currentThread().sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}