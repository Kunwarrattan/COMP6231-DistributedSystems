package com.test;

import java.io.IOException;
import java.net.InetAddress;

import com.assignment1.abstractclass.CommunicationFacilitator;
import com.assignment1.config.Configuration;
import com.assignment1.exception.CommunicationException;
import com.assignment1.utils.CommunicationManager;

class Server extends CommunicationFacilitator{
	
}
public class CommunicationManagerTest {
	/*public static void main(String[] args) throws CommunicationException, IOException, InterruptedException, ExecutionException, TimeoutException {
		Server server = new Server();
		CommunicationManager mgr = new CommunicationManager();
		mgr.send("kaushik", InetAddress.getByName(InetAddress.getLocalHost().getCanonicalHostName()).getHostName(), 7000);
		mgr.send("kaushik", InetAddress.getByName(InetAddress.getLocalHost().getCanonicalHostName()).getHostName(), 7000);
	}*/
	public static void main(String args[]) throws IOException, CommunicationException{
		Server server = new Server();
		System.out.println(InetAddress.getLocalHost().getCanonicalHostName());
		CommunicationManager mgr = new CommunicationManager(Configuration.MULTICAST_PORT,Configuration.SENDER_ROLE,server);
		mgr.sendMulticast("kaushik is badass!!");
		
	}
}
