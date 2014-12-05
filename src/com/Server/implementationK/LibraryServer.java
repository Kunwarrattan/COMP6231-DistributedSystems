/**
 * 
 */
package com.Server.implementationK;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.SocketException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import IdlFiles.LibraryException;
import IdlFiles.LibraryManagementInterfaceOperations;

import com.assignment1.config.Configuration;
import com.assignment1.exception.CommunicationException;
import com.assignment1.utils.CommunicationManager;
import com.assignment1.abstractclass.CommunicationFacilitator;
/**
 * @author Kunwar
 *
 */
public class LibraryServer extends CommunicationFacilitator implements LibraryManagementInterfaceOperations, Runnable  {
	private String replicaName = null;
	private int port = 0;
	private String server = null;
	private CommunicationManager mgrone;
	public volatile boolean stopServer = true;
	ServerFunction serverfunction = null;
	public LibraryServer(String library1, int i, int udpPort1, String replica1) throws IOException {
		super(library1);
		this.replicaName = replica1;
		System.out.println("Library Server Started" + library1 + " replica1 = " + replicaName + "Port" + udpPort1);
		this.server = library1;
		this.port = udpPort1;
		serverfunction = new ServerFunction(server,0,port,replicaName);
		//UDPClass = new UDPClass(this, true);
		mgrone = new CommunicationManager(Configuration.MULTICAST_PORT, Configuration.RECIEVER_ROLE,
				this);
		new Thread(this).start();
	}

	@Override
	public void createAccount(String firstName, String lastName,
			String emailAddr, String phoneNumber, String userName,
			String password, String institutionName) throws LibraryException{
		String response = serverfunction.createAccount(firstName,lastName,emailAddr,phoneNumber,userName,password,institutionName);
		if(response.equalsIgnoreCase(" User Account for Successfully Created ")){
			return;
		}else{
			throw new LibraryException(response);
		}
	}

	@Override
	public void reserveBook(String userName, String password, String bookName,
			String authorName, String inst) throws LibraryException {
		String response = serverfunction.reserveBook(userName,password,bookName, authorName);
		if(response.equalsIgnoreCase("issued Book")){
			return; 
		}else{
			throw new LibraryException(response);
		}
	}

	@Override
	public void reserveInterLibrary(String userName, String password,
			String bookName, String authorName, String inst)
			throws LibraryException {
		String response = serverfunction.reserveInterLibrary(userName,password,bookName, authorName);
		if(response.equalsIgnoreCase("issued Book")){
			return; 
		}else{
			throw new LibraryException(response);
		}
	}

	@Override
	public String getNonRetuners(String adminUserName, String adminPassword,
			String institutionName, int days) throws LibraryException {
		String str = serverfunction.getNonreturners(adminUserName, adminPassword, institutionName, days);
		return str;
	}

	@Override
	public void exit(){
		this.stopServer = false;
		this.mgrone.exit();
		System.out.println("LibraryServer : "+this.replicaName + " : exit() :");
		
	}
	
	// CODE
	@Override
	public void run() {
		while (stopServer) {
			String data = this.popFirstVal();
			if (data != null) {
				String[] arry;
				arry = data.split(Configuration.COMMUNICATION_SEPERATOR);
				String timestamp = arry[0];
				String request = arry[1];
				String hostname = arry[2];
				//TODO: change port to SEQUENCER_RECV_PORT as shown below in all the implementation.. 
				int port = Configuration.SEQUENCER_RECV_PORT;
				String response = timestamp + Configuration.UDP_DELIMITER + this.replicaName+Configuration.UDP_DELIMITER
						+ Configuration.SUCCESS_STRING;
				String failureResponse = timestamp + Configuration.UDP_DELIMITER + this.replicaName+ Configuration.UDP_DELIMITER
						+ Configuration.FAILURE_STRING;
				//TODO: replicate the same in all the servers
				int i = 1;
				if (request.contains(Configuration.CREATE_ACCOUNT)) {
					String requestParam[] = request
							.split(Configuration.UDP_DELIMITER);
					try {
						createAccount(requestParam[i++], requestParam[i++],
								requestParam[i++], requestParam[i++],
								requestParam[i++], requestParam[i++],
								requestParam[i++]);
					} catch (LibraryException e) {
						response = failureResponse;
					}

				} else if (request.contains(Configuration.RESERVE_BOOK)) {
					String requestParam[] = request
							.split(Configuration.UDP_DELIMITER);
					try {
						reserveBook(requestParam[i++], requestParam[i++],
								requestParam[i++], requestParam[i++],requestParam[i++]);

					} catch (LibraryException e) {
						response =failureResponse;
					}
				} else if (request
						.contains(Configuration.RESERVE_INTER_LIBRARY)) {
					String requestParam[] = request
							.split(Configuration.UDP_DELIMITER);
					try {
						reserveInterLibrary(requestParam[i++],
								requestParam[i++], requestParam[i++],
								requestParam[i++],requestParam[i++]);
					} catch (LibraryException e) {
						response = failureResponse;
					}
				} else if (request.contains(Configuration.GET_NON_RETUNERS)) {
					String requestParam[] = request
							.split(Configuration.UDP_DELIMITER);
					try {
						response += Configuration.UDP_DELIMITER
								+ getNonRetuners(requestParam[i++],
										requestParam[i++], requestParam[i++],
										Integer.parseInt(requestParam[i++]));
					} catch (LibraryException e) {
						response = failureResponse;
					}
				}
				try {
					mgrone.send(response, hostname, port);
				} catch (CommunicationException | IOException
						| InterruptedException | ExecutionException
						| TimeoutException e) {
					e.printStackTrace();
				}
			}
			try {
				Thread.currentThread().sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// mgrone.exit();
	}

	
	
}

