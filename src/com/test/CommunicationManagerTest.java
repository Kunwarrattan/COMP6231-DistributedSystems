package com.test;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import com.assignment1.abstractclass.CommunicationFacilitator;
import com.assignment1.exception.CommunicationException;
import com.assignment1.utils.CommunicationManager;

class Server extends CommunicationFacilitator{
	
}
public class CommunicationManagerTest {
	public static void main(String[] args) throws CommunicationException, IOException, InterruptedException, ExecutionException, TimeoutException {
		Server server = new Server();
		CommunicationManager mgr = new CommunicationManager();
		mgr.send("kaushik", "localhost", 7000);
		mgr.send("kaushik", "localhost", 7000);
	}
}
