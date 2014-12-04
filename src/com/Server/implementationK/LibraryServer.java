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
	public LibraryServer(String library1, int i, int udpPort1, String replica1) throws FileNotFoundException, SocketException {
		
		this.replicaName = replica1;
		this.server = library1;
		this.port = udpPort1;
		
		@SuppressWarnings("unused")
		LoggerTask log = new LoggerTask();
		ServerFunction UDP = new ServerFunction(library1,0,udpPort1,replica1);
		//UDPClass = new UDPClass(this, true);
		if (library1.equals(Configuration.LIBRARY1))
			mgrone = new CommunicationManager(Configuration.RECIEVING_PORT1,
					this);
		else if (library1.equals(Configuration.LIBRARY2))
			mgrone = new CommunicationManager(Configuration.RECIEVING_PORT2,
					this);
		else if (library1.equals(Configuration.LIBRARY3))
			mgrone = new CommunicationManager(Configuration.RECIEVING_PORT3,
					this);
	}

//	public static void main(String[] args) {
//		try{
//		
//		
//		
////		System.out.print(" \n Concordia Server is started at " + " " +(new Date().toString()+ " and is up and running \n\n"));
////		log.WriteLog(" \n Concordia Server is started at " + " " +(new Date().toString()+ " and is up and running \n\n"));;
////		
////		System.out.print(" \n McGill Server is started at " + " " +(new Date().toString()+ " and is up and running \n\n"));
////		log.WriteLog(" \n McGill Server is started at " + " " +(new Date().toString()+ " and is up and running \n\n"));
////		
////		System.out.print(" \n Vanier Server is started at " + " " +(new Date().toString()+ " and is up and running \n\n"));
////		log.WriteLog(" \n Vanier Server is started at " + " " +(new Date().toString()+ " and is up and running \n\n"));
////		
////		System.out.print(" \n Concordia Server is started at PORT 1000" + " on " +(new Date().toString()+ " and is up and running \n\n"));
////		log.WriteLog(" \n Concordia Server is started at PORT 1000" + " on " +(new Date().toString()+ " and is up and running \n\n"));;
////		
////		System.out.print(" \n McGill Server is started at PORT 1001 " + " on " +(new Date().toString()+ " and is up and running \n\n"));
////		log.WriteLog(" \n McGill Server is started at PORT 1001" + " on " +(new Date().toString()+ " and is up and running \n\n"));
////		
////		System.out.print(" \n Vanier Server is started at  PORT 1002" + " on " +(new Date().toString()+ " and is up and running \n\n"));
////		log.WriteLog(" \n Vanier Server is started at PORT 1002" + " on " +(new Date().toString()+ " and is up and running \n\n"));
////		
//		}catch(Exception err){
//			err.printStackTrace();
//		}finally{
//			
//		}
//	}

	@Override
	public void createAccount(String firstName, String lastName,
			String emailAddr, String phoneNumber, String userName,
			String password, String institutionName) throws LibraryException {
		ServerFunction serverfunction = new ServerFunction(this.server, this.port, this.replicaName);
		String response = serverfunction.createAccount(firstName,lastName,emailAddr,phoneNumber,userName,password,institutionName);
		
	}

	@Override
	public void reserveBook(String userName, String password, String bookName,
			String authorName) throws LibraryException {
		ServerFunction serverfunction = new ServerFunction(this.server, this.port, this.replicaName);
		reserveBook(userName,password,bookName, authorName);
		
	}

	@Override
	public void reserveInterLibrary(String userName, String password,
			String bookName, String authorName) throws LibraryException {
		ServerFunction serverfunction = new ServerFunction(this.server, this.port, this.replicaName);
		reserveInterLibrary(userName,password,bookName, authorName);
	}

	@Override
	public String getNonRetuners(String adminUserName, String adminPassword,
			String institutionName, int days) throws LibraryException {
		ServerFunction serverfunction = new ServerFunction(this.server, this.port, this.replicaName);
		String str = getNonRetuners(adminUserName, adminPassword, institutionName, days);
		return str;
	}

	@Override
	public void exit() throws LibraryException {
		// TODO Auto-generated method stub
		
	}
	
	// CODE
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (stopServer) {
			String data = this.popFirstVal();
			if (data != null) {
				String[] arry;
				arry = data.split(Configuration.COMMUNICATION_SEPERATOR);
				String timestamp = arry[0];
				String request = arry[1];
				String hostname = arry[2];
				
				int port = Integer.parseInt(arry[3]);
				
				String response = timestamp + Configuration.UDP_DELIMITER + this.replicaName+Configuration.UDP_DELIMITER
						+ Configuration.SUCCESS_STRING;
				String failureResponse = timestamp + Configuration.UDP_DELIMITER + this.replicaName+ Configuration.UDP_DELIMITER
						+ Configuration.FAILURE_STRING;
				if (request.contains(Configuration.CREATE_ACCOUNT)) {
					String requestParam[] = request
							.split(Configuration.UDP_DELIMITER);
					int i = 0;
					try {
						createAccount(requestParam[++i], requestParam[++i],
								requestParam[++i], requestParam[++i],
								requestParam[++i], requestParam[++i],
								requestParam[++i]);
					} catch (LibraryException e) {
						response = failureResponse;
					}

				} else if (request.contains(Configuration.RESERVE_BOOK)) {
					String requestParam[] = request
							.split(Configuration.UDP_DELIMITER);
					int i = 0;
					try {
						reserveBook(requestParam[++i], requestParam[++i],
								requestParam[++i], requestParam[++i]);

					} catch (LibraryException e) {
						response =failureResponse;
					}
				} else if (request
						.contains(Configuration.RESERVE_INTER_LIBRARY)) {
					String requestParam[] = request
							.split(Configuration.UDP_DELIMITER);
					int i = 0;
					try {
						reserveInterLibrary(requestParam[++i],
								requestParam[++i], requestParam[++i],
								requestParam[++i]);
					} catch (LibraryException e) {
						response = failureResponse;
					}
				} else if (request.contains(Configuration.GET_NON_RETUNERS)) {
					String requestParam[] = request
							.split(Configuration.UDP_DELIMITER);
					int i = 0;
					try {
						response += Configuration.UDP_DELIMITER
								+ getNonRetuners(requestParam[++i],
										requestParam[++i], requestParam[++i],
										Integer.parseInt(requestParam[++i]));
					} catch (LibraryException e) {
						response = failureResponse;
					}
				}
				try {
					mgrone.send(response, hostname, port);
				} catch (CommunicationException | IOException
						| InterruptedException | ExecutionException
						| TimeoutException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		// mgrone.exit();
	}
	
}

