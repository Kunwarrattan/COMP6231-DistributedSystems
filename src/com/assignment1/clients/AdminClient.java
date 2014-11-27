package com.assignment1.clients;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Scanner;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.omg.CORBA.ORB;

import IdlFiles.LibraryException;
import IdlFiles.LibraryManagementInterface;
import IdlFiles.LibraryManagementInterfaceHelper;

import com.assignment1.config.Configuration;

/**
 * Class that represents an Admin.
 * 
 * @author Kaushik
 * 
 */
public class AdminClient {
	/**
	 * Provides the display to the admin wherein he can enter his choice on what
	 * he needs to do in the library.
	 * 
	 * @throws Exception
	 */
	public void showAdminMenu() {
		System.out
				.println("Distributed Reservation Management System : Admin Section");
		Scanner keyboard = new Scanner(System.in);
		boolean loopCondition = true;
		while (loopCondition) {
			showMenu();
			try {
				int value = keyboard.nextInt();
				keyboard.nextLine();
				switch (value) {
				case 1:
					getNonReturners(keyboard);
					break;
				case 2:
					exit();
					return;
				default:
					return;
				}
			} catch (Exception e) {
				System.out.println("Illegal input. Pls try again.");
				keyboard.nextLine();
			}
		}
		System.out.println("Successfully exited..");

	}

	/**
	 * This method is used to get the server instance from the corba object.
	 * 
	 * @param serverName
	 * @return
	 */
	private LibraryManagementInterface getServerInstance(String serverName) {
		String args[] = null;
		ORB orb = ORB.init(args, null);
		org.omg.CORBA.Object obj = null;
		try {
			obj = orb.string_to_object(FileUtils.readFileToString(new File(
					".//" + serverName + "IOR.txt")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (obj != null) {
			return LibraryManagementInterfaceHelper.narrow(obj);
		}
		return null;
	}
	
	/**
	 * This method is used to intimate the server to safely shutdown.
	 */
	public void exit() {

		try {
			LibraryManagementInterface server = getServerInstance(Configuration.LIBRARY1);
			LibraryManagementInterface server1 = getServerInstance(Configuration.LIBRARY2);
			LibraryManagementInterface server2 = getServerInstance(Configuration.LIBRARY3);
			server.exit();
			server1.exit();
			server2.exit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * This method contacts all the servers to get a list of students who had
	 * exceeded their due dates.
	 * 
	 * @param keyboard
	 */
	private void getNonReturners(Scanner keyboard) {
		String adminUserName = getUserCredentials(keyboard, true);
		String adminPass = getUserCredentials(keyboard, false);
		int days = getNumberOfDays(keyboard);
		// String adminData = adminUserName.trim() + Configuration.UDP_DELIMITER
		// + adminPass.trim() + Configuration.UDP_DELIMITER + days;
		LibraryManagementInterface server1 = getServerInstance(Configuration.LIBRARY1);
		LibraryManagementInterface server2 = getServerInstance(Configuration.LIBRARY2);
		LibraryManagementInterface server3 = getServerInstance(Configuration.LIBRARY3);
		try {
			System.out.println(server1.getNonRetuners(adminUserName, adminPass,
					Configuration.LIBRARY1, days));
			System.out.println(server2.getNonRetuners(adminUserName, adminPass,
					Configuration.LIBRARY2, days));
			System.out.println(server3.getNonRetuners(adminUserName, adminPass,
					Configuration.LIBRARY3, days));
		} catch (LibraryException e) {
			System.out.println(e.message);
		}
		// String resp = intiateUDPConnectionWithServer(adminData);
		// System.out.println(resp);
	}

	@Deprecated
	private String intiateUDPConnectionWithServer(String data) {
		DatagramSocket aSocket = null;
		byte[] dataBytes = data.getBytes();
		String response = "";
		int i = 0;
		try {
			aSocket = new DatagramSocket();
			InetAddress aHost = InetAddress.getByName("localhost");
			while (i < Configuration.UDP_PORTS.length) {
				int serverPort = Configuration.UDP_PORTS[i];
				DatagramPacket request = new DatagramPacket(dataBytes,
						dataBytes.length, aHost, serverPort);
				aSocket.send(request);
				byte[] buffer = new byte[1000];
				DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
				aSocket.receive(reply);
				byte[] dataFromServer = new byte[reply.getLength()];
				System.arraycopy(reply.getData(), reply.getOffset(),
						dataFromServer, 0, reply.getLength());
				response += new String(dataFromServer);
				i++;
			}
		} catch (SocketException e) {
			System.out.println("Socket: " + e.getMessage());
		} catch (IOException e) {
			System.out.println("IO: " + e.getMessage());
		} finally {
			if (aSocket != null)
				aSocket.close();
		}
		return response;
	}
	/**
	 * This function displays all the available functionalities available to the user.
	 */
	private void showMenu() {
		System.out.println("Operations available for you..");
		System.out.println("Press '1' for To get a list of non-returners");
		System.out.println("Press '2' to Exit");
	}
	/**
	 * Get user name or password of the user..
	 * @param keyboard
	 * @param isUserName
	 * @return
	 */
	private String getUserCredentials(Scanner keyboard, boolean isUserName) {
		String content;
		while (true) {
			if (isUserName)
				System.out.println("Pls Enter your unique user name :");
			else
				System.out.println("Pls Enter your password :");
			content = keyboard.next();
			if (StringUtils.isNotBlank(content)) {
				break;
			} else {
				System.out.println("Pls enter a valid input..");
			}
		}
		keyboard.nextLine();
		return content;
	}
	/**
	 * Get the number of days
	 * @param keyboard
	 * @return
	 */
	private int getNumberOfDays(Scanner keyboard) {
		int content;
		while (true) {
			try {
				System.out.println("Pls enter the number of Days");
				content = keyboard.nextInt();
				break;
			} catch (Exception e) {
				System.out.println("Pls enter a valid input..");
			}
		}
		keyboard.nextLine();
		return content;
	}
}
