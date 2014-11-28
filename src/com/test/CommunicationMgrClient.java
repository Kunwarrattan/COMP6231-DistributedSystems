package com.test;

import java.net.SocketException;

import com.assignment1.abstractclass.CommunicationFacilitator;
import com.assignment1.utils.CommunicationManager;

class Client extends CommunicationFacilitator{
	
}
public class CommunicationMgrClient {
	public static void main(String[] args) throws SocketException, InterruptedException {
		Client client = new Client();
		CommunicationManager mgr = new CommunicationManager(7000, client);
		System.out.println("client..");
		
	}
}
