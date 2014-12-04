package com.ReplicaManger.DeamonClient;

import java.io.IOException;
import java.net.SocketException;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import com.assignment1.abstractclass.CommunicationFacilitator;
import com.assignment1.config.Configuration;
import com.assignment1.exception.CommunicationException;
import com.assignment1.utils.CommunicationManager;

public class DeamonClient extends CommunicationFacilitator {

	private CommunicationManager mgrone;

	Scanner in = new Scanner(System.in);

	public DeamonClient() throws CommunicationException, IOException, InterruptedException, ExecutionException, TimeoutException {
		mgrone = new CommunicationManager();
		showMenu();
	}

	public void showMenu() throws CommunicationException, IOException,
	InterruptedException, ExecutionException, TimeoutException {
		do {
			System.out.println("Operations available for you..");
			System.out.println("Press '1' to start particular replica");
			System.out.println("Press '2' to start all replicas");
			System.out.println("Press '3' to kill particular replica");
			System.out.println("Press '4' to kill all replica");

			System.out.print("Please Enter you Option :");

			int value = in.nextInt();

			switch (value) {
			case 1:
				startupReplica();
				break;

			case 2:
				startupAllReplicas();
				break;

			case 3:
				killReplica();
				break;

			case 4:
				killAllReplicas();
				break;

			default:
				System.out.println("Invalid Output");
			}
		} while (true);

	}

	private void startupReplica() throws CommunicationException, IOException,
	InterruptedException, ExecutionException, TimeoutException {
		System.out.println("Please Select the replica to startup");
		System.out
		.println("PRESS 1 FOR REPLICA 1 \n PRESS 2 FOR REPLICA 2 \n PRESS 3 FOR REPLICA 3 \n ");
		int key = in.nextInt();
		String data;
		do {
			switch (key) {
			case 1:
				data = Configuration.REPLICA_START_CMD
				+ Configuration.UDP_DELIMITER + Configuration.REPLICA1;
				mgrone.send(data, Configuration.DEAMON_RM_IP,
						Configuration.RM_RECV_PORT);

				break;
			case 2:
				data = Configuration.REPLICA_START_CMD
				+ Configuration.UDP_DELIMITER + Configuration.REPLICA2;
				mgrone.send(data, Configuration.DEAMON_RM_IP,
						Configuration.RM_RECV_PORT);
				break;
			case 3:
				data = Configuration.REPLICA_START_CMD
				+ Configuration.UDP_DELIMITER + Configuration.REPLICA3;
				mgrone.send(data, Configuration.DEAMON_RM_IP,
						Configuration.RM_RECV_PORT);
				break;
			default:
				System.out.println("Invalid ! entry");
			}
		} while (true);
		// String responseSet = getResponseSet(timeStamp);
	}

	private void startupAllReplicas() throws CommunicationException,
	IOException, InterruptedException, ExecutionException,
	TimeoutException {
		String data = Configuration.REPLICA_START_CMD
				+ Configuration.UDP_DELIMITER + Configuration.REPLICA1;
		mgrone.send(data, Configuration.DEAMON_RM_IP,
				Configuration.RM_RECV_PORT);

		data = Configuration.REPLICA_START_CMD + Configuration.UDP_DELIMITER
				+ Configuration.REPLICA2;
		mgrone.send(data, Configuration.DEAMON_RM_IP,
				Configuration.RM_RECV_PORT);

		data = Configuration.REPLICA_START_CMD + Configuration.UDP_DELIMITER
				+ Configuration.REPLICA3;
		mgrone.send(data, Configuration.DEAMON_RM_IP,
				Configuration.RM_RECV_PORT);

	}

	private void killReplica() throws CommunicationException, IOException,
	InterruptedException, ExecutionException, TimeoutException {
		System.out.println("Please Select the replica to kill");
		System.out
		.println("PRESS 1 FOR REPLICA 1 \n PRESS 2 FOR REPLICA 2 \n PRESS 3 FOR REPLICA 3 \n ");
		int key = in.nextInt();
		String data;
		do {
			switch (key) {
			case 1:
				data = Configuration.REPLICA_SHUT_DOWN_CMD
				+ Configuration.UDP_DELIMITER + Configuration.REPLICA1;
				mgrone.send(data, Configuration.DEAMON_RM_IP,
						Configuration.RM_RECV_PORT);

				break;
			case 2:
				data = Configuration.REPLICA_SHUT_DOWN_CMD
				+ Configuration.UDP_DELIMITER + Configuration.REPLICA2;
				mgrone.send(data, Configuration.DEAMON_RM_IP,
						Configuration.RM_RECV_PORT);
				break;
			case 3:
				data = Configuration.REPLICA_SHUT_DOWN_CMD
				+ Configuration.UDP_DELIMITER + Configuration.REPLICA3;
				mgrone.send(data, Configuration.DEAMON_RM_IP,
						Configuration.RM_RECV_PORT);
				break;
			default:
				System.out.println("Invalid ! entry");
			}
		} while (true);

	}

	private void killAllReplicas() throws CommunicationException, IOException,
	InterruptedException, ExecutionException, TimeoutException {
		String data = Configuration.REPLICA_SHUT_DOWN_CMD
				+ Configuration.UDP_DELIMITER + Configuration.REPLICA1;
		mgrone.send(data, Configuration.DEAMON_RM_IP,
				Configuration.RM_RECV_PORT);

		data = Configuration.REPLICA_SHUT_DOWN_CMD
				+ Configuration.UDP_DELIMITER + Configuration.REPLICA2;
		mgrone.send(data, Configuration.DEAMON_RM_IP,
				Configuration.RM_RECV_PORT);

		data = Configuration.REPLICA_SHUT_DOWN_CMD
				+ Configuration.UDP_DELIMITER + Configuration.REPLICA3;
		mgrone.send(data, Configuration.DEAMON_RM_IP,
				Configuration.RM_RECV_PORT);
	}
	
	public static void main(String[] args) throws CommunicationException, IOException, InterruptedException, ExecutionException, TimeoutException{
		DeamonClient dm = new DeamonClient();
	}
	

}
