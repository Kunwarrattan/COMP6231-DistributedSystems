package com.assignment1.remoteinterface;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import IdlFiles.LibraryManagementInterfaceOperations;

import com.assignment1.abstractclass.CommunicationFacilitator;
import com.assignment1.config.Configuration;
import com.assignment1.exception.CommunicationException;
import com.assignment1.implementations.LibraryServer;
import com.assignment1.utils.CommunicationManager;
import com.assignment1.utils.ReplicaManager;

public class ClientInterface extends CommunicationFacilitator implements
		Runnable {

	private String name;
	private CommunicationManager mgrone;
	private LibraryManagementInterfaceOperations concordia;
	private LibraryManagementInterfaceOperations mcgill;
	private LibraryManagementInterfaceOperations vanier;
	public volatile boolean stopServer = true;
	public volatile boolean instancesNotRunning = true;

	/**
	 * Constructor
	 * @throws Exception 
	 */
	public ClientInterface(String name) throws Exception {
		this.name = name;
		if (name.equals(Configuration.REPLICA1)) {
			mgrone = new CommunicationManager(
					Configuration.REPLICA_INTERFACE_PORT1, this);
			concordia = new LibraryServer(
					Configuration.LIBRARY1, 0,
					Configuration.UDP_PORT_1,
					Configuration.REPLICA1);
			mcgill = new LibraryServer(
					Configuration.LIBRARY2, 0,
					Configuration.UDP_PORT_2,
					Configuration.REPLICA1);
			vanier = new LibraryServer(
					Configuration.LIBRARY3, 0,
					Configuration.UDP_PORT_3,
					Configuration.REPLICA1);
			//TODO:	replicate it in all the other implementation
			instancesNotRunning = false;
			ReplicaManager mgr = new ReplicaManager();
			//FrontEnd f = new FrontEnd();
			//Sequencer seq = new Sequencer();
		}
		if (name.equals(Configuration.REPLICA2)) {
			mgrone = new CommunicationManager(
					Configuration.REPLICA_INTERFACE_PORT2, this);
			concordia = new com.Server.implementationK.LibraryServer(
					Configuration.LIBRARY1, 0,
					Configuration.K_UDP_PORT_1,
					Configuration.REPLICA2);
			mcgill = new com.Server.implementationK.LibraryServer(
					Configuration.LIBRARY2, 0,
					Configuration.K_UDP_PORT_2,
					Configuration.REPLICA2);
			vanier = new com.Server.implementationK.LibraryServer(
					Configuration.LIBRARY3, 0,
					Configuration.K_UDP_PORT_3,
					Configuration.REPLICA2);
			instancesNotRunning = false;
		}
		if (name.equals(Configuration.REPLICA3)) {
			mgrone = new CommunicationManager(
					Configuration.REPLICA_INTERFACE_PORT3, this);
			concordia = new com.Server.implementationV.LibraryServer(
					Configuration.LIBRARY1, 0,
					Configuration.V_UDP_PORT_1,
					Configuration.REPLICA3);
			mcgill = new com.Server.implementationV.LibraryServer(
					Configuration.LIBRARY2, 0,
					Configuration.V_UDP_PORT_2,
					Configuration.REPLICA3);
			vanier = new com.Server.implementationV.LibraryServer(
					Configuration.LIBRARY3, 0,
					Configuration.V_UDP_PORT_3,
					Configuration.REPLICA3);
			instancesNotRunning = false;
		}
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
	// Do for all 3 replicas
	public static void main(String[] args) throws Exception {
	
		new ClientInterface(Configuration.REPLICA1);
		new ClientInterface(Configuration.REPLICA2);
		//ReplicaManager mgr = new ReplicaManager();
		//new ClientInterface(Configuration.REPLICA3);

	}

	
	@SuppressWarnings("static-access")
	private void sendNotificaton() throws CommunicationException, IOException,
			InterruptedException, ExecutionException, TimeoutException {
		String expected = "";
		if (this.name.equals(Configuration.REPLICA1) && !instancesNotRunning) {
			expected = Configuration.REPLICA_HEARTBEAT
					+ Configuration.UDP_DELIMITER + Configuration.REPLICA1;
//			System.out.println("Sending notification for "  +  Configuration.REPLICA1);
			mgrone.send(expected, Configuration.DEAMON_RM_IP,
					Configuration.RM_RECV_PORT);

		} else if (this.name.equals(Configuration.REPLICA2) && !instancesNotRunning) {
			expected = Configuration.REPLICA_HEARTBEAT
					+ Configuration.UDP_DELIMITER + Configuration.REPLICA2;
//			System.out.println("Sending notification for "  +  Configuration.REPLICA2);
			mgrone.send(expected, Configuration.DEAMON_RM_IP,
					Configuration.RM_RECV_PORT);

		} else if (this.name.equals(Configuration.REPLICA3) && !instancesNotRunning) {
			expected = Configuration.REPLICA_HEARTBEAT
					+ Configuration.UDP_DELIMITER + Configuration.REPLICA3;
//			System.out.println("Sending notification for "  +  Configuration.REPLICA3);
			mgrone.send(expected, Configuration.DEAMON_RM_IP,
					Configuration.RM_RECV_PORT);
		}
//		System.out.println("ClientInterface : Notification : "+expected);
		Thread.currentThread().sleep(
				Configuration.MAX_DURATION_TO_WAIT_BEFORE_TIMEOUT * 500);
	}

	@Override
	public void run() {
		String name = Thread.currentThread().getName();
		if (name.equals(Configuration.HEART_BEAT_MONITOR)) {
			while (stopServer) {
				try {
					sendNotificaton();
					try {
						Thread.currentThread().sleep(10);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

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
							//TODO: check whether exit method is implemented in kunwar and venkatesh code..
							//TODO:	replicate it in all the other implementation
							if (this.name.equals(Configuration.REPLICA1)) {
								if (concordia != null) {
									concordia.exit();
								} else if (mcgill != null) {
									mcgill.exit();
									
								} else if (vanier != null) {
									vanier.exit();
									
								}
								mcgill = null;
								vanier = null;
								concordia = null;
								instancesNotRunning = true;
							} else if (this.name.equals(Configuration.REPLICA2)) {
								if (concordia != null) {
									concordia.exit();
								} else if (mcgill != null) {
									mcgill.exit();
								} else if (vanier != null) {
									vanier.exit();
								}
								mcgill = null;
								vanier = null;
								concordia = null;
								instancesNotRunning = true;
							} else if (this.name.equals(Configuration.REPLICA3)) {
								if (concordia != null) {
									concordia.exit();
								} else if (mcgill != null) {
									mcgill.exit();
								} else if (vanier != null) {
									vanier.exit();
								}
								mcgill = null;
								vanier = null;
								concordia = null;
								instancesNotRunning = true;
							}

						} else if (request
								.contains(Configuration.REPLICA_START_CMD)) {

							if (this.name.equals(Configuration.REPLICA1)) {
								if (concordia != null) {
									System.out.println("ClientInterface : server" +concordia+"restarting ");
									concordia.exit();
								} else if (mcgill != null) {
									System.out.println("ClientInterface : server" +concordia+"restarting");
									mcgill.exit();
								} else if (vanier != null) {
									System.out.println("ClientInterface : server" +concordia+"restarting");
									vanier.exit();
								}
								concordia = new LibraryServer(
										Configuration.LIBRARY1, 0,
										Configuration.UDP_PORT_1,
										Configuration.REPLICA1);
								mcgill = new LibraryServer(
										Configuration.LIBRARY2, 0,
										Configuration.UDP_PORT_2,
										Configuration.REPLICA1);
								vanier = new LibraryServer(
										Configuration.LIBRARY3, 0,
										Configuration.UDP_PORT_3,
										Configuration.REPLICA1);
								//TODO:	replicate it in all the other implementation
								instancesNotRunning = false;

							} else if (this.name.equals(Configuration.REPLICA2)) {
								if (concordia != null) {
									System.out.println("ClientInterface : server" +concordia+"restarting ");
									concordia.exit();
								} else if (mcgill != null) {
									System.out.println("ClientInterface : server" +concordia+"restarting ");
									mcgill.exit();
								} else if (vanier != null) {
									System.out.println("ClientInterface : server" +concordia+"restarting ");
									vanier.exit();
								}
								concordia = new com.Server.implementationK.LibraryServer(
										Configuration.LIBRARY1, 0,
										Configuration.K_UDP_PORT_1,
										Configuration.REPLICA2);
								mcgill = new com.Server.implementationK.LibraryServer(
										Configuration.LIBRARY2, 0,
										Configuration.K_UDP_PORT_2,
										Configuration.REPLICA2);
								vanier = new com.Server.implementationK.LibraryServer(
										Configuration.LIBRARY3, 0,
										Configuration.K_UDP_PORT_3,
										Configuration.REPLICA2);
								instancesNotRunning = false;
							} else if (this.name.equals(Configuration.REPLICA3)) {
								if (concordia != null) {
									System.out.println("ClientInterface : server" +concordia+"restarting ");
									concordia.exit();
								} else if (mcgill != null) {
									System.out.println("ClientInterface : server" +concordia+"restarting ");
									mcgill.exit();
								} else if (vanier != null) {
									System.out.println("ClientInterface : server" +concordia+"restarting ");
									vanier.exit();
								}
								concordia = new com.Server.implementationV.LibraryServer(
										Configuration.LIBRARY1, 0,
										Configuration.V_UDP_PORT_1,
										Configuration.REPLICA3);
								mcgill = new com.Server.implementationV.LibraryServer(
										Configuration.LIBRARY2, 0,
										Configuration.V_UDP_PORT_2,
										Configuration.REPLICA3);
								vanier = new com.Server.implementationV.LibraryServer(
										Configuration.LIBRARY3, 0,
										Configuration.V_UDP_PORT_3,
										Configuration.REPLICA3);
								instancesNotRunning = false;
							}

						}
					}
				}

				catch (Exception e) {
					e.printStackTrace();
				}
				try {
					Thread.currentThread().sleep(10);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

}
