package com.test;

import java.io.IOException;

import com.assignment1.abstractclass.CommunicationFacilitator;
import com.assignment1.config.Configuration;
import com.assignment1.utils.CommunicationManager;

class Client extends CommunicationFacilitator{
	
}
public class CommunicationMgrClient {
/*	public static void main(String[] args) throws SocketException, InterruptedException {
		Client client = new Client();
		CommunicationManager mgr = new CommunicationManager(7000, client);
		Thread.currentThread().sleep(1000);
		System.out.println(client.sortedQueue);
		System.out.println("client..");
		mgr.exit();
		
	}*/
	public static void main(String[] args) throws IOException, InterruptedException{
		Client client1 = new Client();
		Client client2 = new Client();
		CommunicationManager  mgr1 = new CommunicationManager(Configuration.MULTICAST_PORT,Configuration.RECIEVER_ROLE,client1);
		CommunicationManager  mgr2 = new CommunicationManager(Configuration.MULTICAST_PORT,Configuration.RECIEVER_ROLE,client2);
		Thread.currentThread().sleep(5000);
		System.out.println("Client1 : "+client1.sortedQueue);
		System.out.println("Client2 "+client2.sortedQueue);
		mgr1.exit();
		mgr2.exit();
	}
}
