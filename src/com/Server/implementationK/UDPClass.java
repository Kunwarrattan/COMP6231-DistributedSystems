package com.Server.implementationK;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class UDPClass implements Runnable{
	private int portNumber;
	private String servername;
	private String replica;
	/* -------------------------------------- UDP Client -------------------------------------------------------------------
	 * 
	 * UDP client initilaizing and calling the UDP Server with port and data
	 * 
	 * ---------------------------------------------------------------------------------------------------------
	 * */
	public UDPClass(int PORT, String server, String replica){
		this.portNumber = PORT;
		this.servername = server;
		this.replica = replica;
		new Thread(this).start();
	}
	public boolean initiateConnectionWithOtherServers(int port, String data) {
		DatagramSocket aSocket = null;
		byte[] dataBytes = data.getBytes();
		boolean retVal = false;
		String response = "";
		try {
			aSocket = new DatagramSocket();
			InetAddress aHost = InetAddress.getByName("localhost");
			DatagramPacket request = new DatagramPacket(dataBytes,dataBytes.length, aHost, port);
			aSocket.send(request);
			byte[] buffer = new byte[1000];
			DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
			aSocket.receive(reply);
			byte[] dataFromServer = new byte[reply.getLength()];
			System.arraycopy(reply.getData(), reply.getOffset(),
					dataFromServer, 0, reply.getLength());
			response = new String(dataFromServer);
			response = response.trim();
			if(response.equals("1")){
				retVal = true;
			}
		} catch (SocketException e) {
			System.out.println("Socket: " + e.getMessage());
		} catch (IOException e) {
			System.out.println("IO: " + e.getMessage());
		} finally {
			if (aSocket != null)
				aSocket.close();
		}
		return retVal;
	}


	
	/* ------------------------------------------------- UDP Server --------------------------------------------------------
	 * run method for the UDP server on three different threads
	 * has three copies running for 3 different servers
	 * waiting for UDP calls from the server
	 * 
	 * ---------------------------------------------------------------------------------------------------------
	 * */
	
	public void run() {
		DatagramSocket host = null;
		try {
			ServerFunction lb1 = new ServerFunction(this.servername, this.portNumber,this.replica);
			host = new DatagramSocket(this.portNumber);
			System.out.println("udp connection running..");
			while (true) {
				//System.out.println("UDP " + this.servername
						//+ " is running at Port " + this.portNumber);
				byte[] receiveData = new byte[1024];
				DatagramPacket request = new DatagramPacket(receiveData,receiveData.length);
				host.receive(request);
				byte[] dataCopy = new byte[request.getLength()];
				System.arraycopy(request.getData(), request.getOffset(),
						dataCopy, 0, request.getLength());
				String actualData = new String(dataCopy);
				actualData = actualData.trim();
				String datArry[] = actualData.split(":");
				String response = "0";
				if (datArry.length == 3) {
					try {
						
						if(lb1.reserveBookForInterLibrary(datArry[0], datArry[1],datArry[2])){
							response = "1";
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				byte[] responseByte = response.getBytes();
				DatagramPacket reply = new DatagramPacket(responseByte,responseByte.length, request.getAddress(),request.getPort());
				host.send(reply);
			}
		} catch (SocketException e) {
			System.out.println("Socekt " + e.getMessage());
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			if (host != null) {
				host.close();
			}
		}
	}
	
}
