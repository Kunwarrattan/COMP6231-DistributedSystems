package com.assignment1.utils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import com.assignment1.config.Configuration;
import com.assignment1.implementations.LibraryServer;

public class UDPManager extends Thread {
	private LibraryServer serverInstance = null;
	private boolean listenForInterLibraryActions = false;

	public UDPManager(LibraryServer serverInstance,
			boolean listenForInterLibraryActions) {
		this.serverInstance = serverInstance;
		this.listenForInterLibraryActions = listenForInterLibraryActions;
		this.start();
	}

	public void run() {
		System.out.println("Starting the UDP Server..");
		DatagramSocket aSocket = null;
		try {
			int port = 0;
			String name = serverInstance.getName();
			if (listenForInterLibraryActions) {
				port = serverInstance.getPortForInterLibraryCommunication();
				aSocket = new DatagramSocket(port);
				System.out.println("Name : " + name
						+ " UDP server for inter library actions listening at "
						+ port);
			} else {
				port = serverInstance.getPortForGetNonReturners();
				aSocket = new DatagramSocket(port);
				System.out.println("Name : " + name
						+ " UDP server for getNonReturners listening at "
						+ port);
			}
			aSocket.setSoTimeout(Configuration.RECV_TIMEOUT);
			while (serverInstance.isStopServer()) {
				try {
					byte[] buffer = new byte[1000];
					DatagramPacket request = new DatagramPacket(buffer,
							buffer.length);
					aSocket.receive(request);
					byte[] dataCopy = new byte[request.getLength()];
					System.arraycopy(request.getData(), request.getOffset(),
							dataCopy, 0, request.getLength());
					String actualData = new String(dataCopy);
					actualData = actualData.trim();
					String datArry[] = actualData
							.split(Configuration.UDP_DELIMITER);
					String response = "Invalid parameters";
					if (!listenForInterLibraryActions && datArry.length == 3) {
						try {
							response = serverInstance.getNonRetuners(
									datArry[0], datArry[1], name,
									Integer.parseInt(datArry[2]));
						} catch (Exception e) {
							response = "Exception Occurred : " + e.getMessage();
							e.printStackTrace();
						}
					} else if (listenForInterLibraryActions
							&& datArry.length == 3) {
						try {
							serverInstance.reserveBookCore(datArry[0],
									datArry[1], datArry[2]);
							String logFile = Configuration.CURRENT_DIR + name
									+ "//" + datArry[2] + ".txt";
							FileOps.appendToFile(
									logFile,
									new StringBuilder(
											datArry[2]
													+ ": reserveBook :Book : "
													+ datArry[0]
													+ " by Author : "
													+ datArry[1]
													+ " has been reserved for Inter library Transfer By "
													+ datArry[2] + " library.."));
							response = "1";
						} catch (Exception e) {
							response = "0";
						}
					}
					byte[] responseByte = response.getBytes();
					DatagramPacket reply = new DatagramPacket(responseByte,
							responseByte.length, request.getAddress(),
							request.getPort());
					aSocket.send(reply);
					System.out.println("Name : " + name
							+ " UDP server stopped listening at " + port);
				} catch (SocketTimeoutException e) {
				}
			}
		} catch (SocketException e) {
			System.out.println("Socket: " + e.getMessage());
		} catch (IOException e) {
			System.out.println("IO: " + e.getMessage());
		} finally {
			if (aSocket != null)
				aSocket.close();
		}
		System.out.println("Stopping the UDP Server..");

	}
}
