package com.assignment1.remoteinterface;

import java.io.IOException;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import IdlFiles.LibraryManagementInterfaceOperations;

import com.assignment1.abstractclass.CommunicationFacilitator;
import com.assignment1.config.Configuration;
import com.assignment1.exception.CommunicationException;
import com.assignment1.implementations.LibraryServer;
import com.assignment1.utils.CommunicationManager;
import com.assignment1.utils.ReplicaManager;
import com.assignment1.utils.Sequencer;

public class ClientInterface extends CommunicationFacilitator implements
		Runnable {

	private Set<String> activeReplicas;
	private String name;
	private CommunicationManager mgrone;
	private com.assignment1.implementations.LibraryServer libserverKaushik;
	private com.Server.implementationK.LibraryServer libserverkunwar;
	private com.Server.implementationV.LibraryServer libserverVenkatesh;

	private LibraryManagementInterfaceOperations concordia;
	private LibraryManagementInterfaceOperations mcgill;
	private LibraryManagementInterfaceOperations vanier;

	public volatile boolean stopServer = true;

	private HashMap<String, Integer> numberOfTimesReplicaHeartBeatMissed;

	public ClientInterface(){
		
	}
	/**
	 * Constructor
	 */
	public ClientInterface(String name) {
		this.name = name;
		Thread thread1 = new Thread(this, Configuration.HEART_BEAT_MONITOR);
		Thread thread2 = new Thread(this, Configuration.RCV_MONITOR);
		thread1.start();
		thread2.start();
	}

	/**
	 * Main Function
	 * 
	 * @throws Exception
	 */
	//Do for all 3 replicas
	public static void main(String[] args) throws Exception {
		ClientInterface instance = new ClientInterface();
		
		if (instance.name.equals(Configuration.REPLICA1)) 
		{
			ReplicaManager mgr = new ReplicaManager();
			Sequencer seq = new Sequencer();
	/*	*/
		} 
		
		else if (instance.name.equals(Configuration.REPLICA2)) 
		{
/*			instance.concordia = new com.Server.implementationK.LibraryServer(Configuration.LIBRARY1, 0,
					Configuration.UDP_PORT_1, Configuration.REPLICA1);
			instance.mcgill = new com.Server.implementationK.LibraryServer(Configuration.LIBRARY2, 0,
					Configuration.UDP_PORT_2, Configuration.REPLICA2);
			instance.vanier = new com.Server.implementationK.LibraryServer(Configuration.LIBRARY3, 0,
					Configuration.UDP_PORT_3, Configuration.REPLICA3);*/
		}
		
		else if (instance.name.equals(Configuration.REPLICA3)) {
/*			instance.concordia = new com.Server.implementationV.LibraryServer(Configuration.LIBRARY1, 0,
					Configuration.UDP_PORT_1, Configuration.REPLICA1);
			instance.mcgill = new com.Server.implementationV.LibraryServer(Configuration.LIBRARY2, 0,
					Configuration.UDP_PORT_2, Configuration.REPLICA2);
			instance.vanier = new com.Server.implementationV.LibraryServer(Configuration.LIBRARY3, 0,
					Configuration.UDP_PORT_3, Configuration.REPLICA3);*/
		}

	}

//	private void startReplica(String dataRecieved)
//			throws CommunicationException, IOException, InterruptedException,
//			ExecutionException, TimeoutException {
//		// expected string Configuration.REPLICA_START_CMD +
//		// Configuration.UDP_DELIMITER + Configuration.REPLICA1 ;
//		String ary[] = dataRecieved.split(Configuration.UDP_DELIMITER);
//		startReplicaCore(ary[1]);
//	}
//
//	private void startReplicaCore(String replicaName)
//			throws CommunicationException, IOException, InterruptedException,
//			ExecutionException, TimeoutException {
//		String data = Configuration.REPLICA_START_CMD;
//
//		if (Configuration.REPLICA1.equals(replicaName)) {
//			
//			mgrone.send(data, Configuration.REPLICA_IP1,
//					Configuration.REPLICA_INTERFACE_PORT1);
//			addReplicaToSet(Configuration.REPLICA1);
//
//		}
//
//		else if (Configuration.REPLICA2.equals(replicaName)) {
//
//			mgrone.send(data, Configuration.REPLICA_IP2,
//					Configuration.REPLICA_INTERFACE_PORT2);
//			addReplicaToSet(Configuration.REPLICA2);
//
//		}
//
//		else if (Configuration.REPLICA3.equals(replicaName)) {
//
//			mgrone.send(data, Configuration.REPLICA_IP3,
//					Configuration.REPLICA_INTERFACE_PORT3);
//			addReplicaToSet(Configuration.REPLICA3);
//
//		}
//	}
//
//	public void addReplicaToSet(String replicaName) {
//
//		synchronized (activeReplicas) {
//			activeReplicas.add(replicaName);
//		}
//
//	}
//	
	private void sendNotificaton() throws CommunicationException, IOException,
	InterruptedException, ExecutionException, TimeoutException {

		String expected;
		  
		if(name.equals(Configuration.REPLICA1)){
			   expected = Configuration.REPLICA_HEARTBEAT +
						Configuration.UDP_DELIMITER + Configuration.REPLICA1;
			   mgrone.send(expected, Configuration.REPLICA_IP1,
						Configuration.REPLICA_INTERFACE_PORT1);
			   
		  }else if(name.equals(Configuration.REPLICA2)){
		   	  expected = Configuration.REPLICA_HEARTBEAT +
						Configuration.UDP_DELIMITER + Configuration.REPLICA1;
	    	  mgrone.send(expected, Configuration.REPLICA_IP2,
	    			    Configuration.REPLICA_INTERFACE_PORT2);
		    	  
		  }else if(name.equals(Configuration.REPLICA3)){
		   	  expected = Configuration.REPLICA_HEARTBEAT +
	   					Configuration.UDP_DELIMITER + Configuration.REPLICA1;
	    	  mgrone.send(expected, Configuration.REPLICA_IP3,
						Configuration.REPLICA_INTERFACE_PORT3);
	    }
		  
		 
		Thread.currentThread().sleep(
		Configuration.MAX_DURATION_TO_WAIT_BEFORE_TIMEOUT * 500);
	}

	private void recieveNotificationFromReplica(String notification) {
		// expected string Configuration.REPLICA_HEARTBEAT +
		// Configuration.UDP_DELIMITER + Configuration.REPLICA1 ;
		String ary[] = notification.split(Configuration.UDP_DELIMITER);

		synchronized (numberOfTimesReplicaHeartBeatMissed) {
			Integer i = numberOfTimesReplicaHeartBeatMissed.get(ary[1]);
			if (i > 0) {
				numberOfTimesReplicaHeartBeatMissed.put(ary[1], --i);

			}

		}}
	

	@Override
	public void run() {
		String name = Thread.currentThread().getName();
		if (name.equals(Configuration.HEART_BEAT_MONITOR)) {
			while (stopServer) {
				try {
					   
					    	sendNotificaton();
					    
					
				} catch (CommunicationException | IOException
						| InterruptedException | ExecutionException
						| TimeoutException e) {

					e.printStackTrace();
				}
			}

		} else if (name.equals(Configuration.RCV_MONITOR)) {

			while (stopServer) {

				String data = this.popFirstVal();
				try {

					if (data != null) {
						String[] arry;
						arry = data
								.split(Configuration.COMMUNICATION_SEPERATOR);
						String request = arry[1];

						if (request
								.contains(Configuration.REPLICA_SHUT_DOWN_CMD)) {
							
							if(name.equals(Configuration.REPLICA1)){
								libserverKaushik.exit();
								
							}else if(name.equals(Configuration.REPLICA2)){
								libserverkunwar.exit();
								
							}else if(name.equals(Configuration.REPLICA3)){
								libserverVenkatesh.exit();
								
							}
							

						} else if (request
								     .contains(Configuration.REPLICA_START_CMD)) {
							
							if (name.equals(Configuration.REPLICA1)){
								if(concordia != null){
									concordia.exit();
								}
								else if(mcgill != null){
									mcgill.exit();
								}else if(vanier != null){
									vanier.exit();
								}
								concordia = new LibraryServer(Configuration.LIBRARY1, 0,
										Configuration.UDP_PORT_1, Configuration.REPLICA1);
								mcgill = new LibraryServer(Configuration.LIBRARY2, 0,
										Configuration.UDP_PORT_2, Configuration.REPLICA2);
								vanier = new LibraryServer(Configuration.LIBRARY3, 0,
										Configuration.UDP_PORT_3, Configuration.REPLICA3);
								
								
							}
							else if(name.equals(Configuration.REPLICA2)){
								concordia = new com.Server.implementationK.LibraryServer(Configuration.LIBRARY1, 0,
										Configuration.K_UDP_PORT_1, Configuration.REPLICA1);
								mcgill = new com.Server.implementationK.LibraryServer(Configuration.LIBRARY2, 0,
										Configuration.K_UDP_PORT_2, Configuration.REPLICA2);
								vanier = new com.Server.implementationK.LibraryServer(Configuration.LIBRARY3, 0,
										Configuration.K_UDP_PORT_3, Configuration.REPLICA3);
								
							}else if(name.equals(Configuration.REPLICA3))
							{
								concordia = new com.Server.implementationV.LibraryServer(Configuration.LIBRARY1, 0,
										Configuration.V_UDP_PORT_1, Configuration.REPLICA1);
								mcgill = new com.Server.implementationV.LibraryServer(Configuration.LIBRARY2, 0,
										Configuration.V_UDP_PORT_2, Configuration.REPLICA2);
								vanier = new com.Server.implementationV.LibraryServer(Configuration.LIBRARY3, 0,
										Configuration.V_UDP_PORT_3, Configuration.REPLICA3);
							}
							
					} else if (request
								.contains(Configuration.REPLICA_HEARTBEAT)) {
							this.recieveNotificationFromReplica(request);
						}
					}
				}

				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	}

}
