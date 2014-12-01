package com.assignment1.utils;

import java.io.IOException;
import java.net.SocketException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import com.assignment1.abstractclass.CommunicationFacilitator;
import com.assignment1.config.Configuration;
import com.assignment1.exception.CommunicationException;

public class ReplicaManager extends CommunicationFacilitator implements
		Runnable {
	private Set<String> activeReplicas;
	private CommunicationManager mgr;
	private HashMap<String, Integer> numberOfTimesReplicaHeartBeatMissed;
	public volatile boolean stopServer = true;

	public ReplicaManager() throws SocketException {
		this.activeReplicas = new HashSet<String>();
		this.numberOfTimesReplicaHeartBeatMissed = new HashMap<String, Integer>();
		mgr = new CommunicationManager(Configuration.RM_RECV_PORT, this);
		Thread thread1 = new Thread(this, Configuration.HEART_BEAT_MONITOR);
		Thread thread2 = new Thread(this, Configuration.RCV_MONITOR);
		thread1.start();
		thread2.start();
	}

	public static Set<String> listActiveReplicas() {
		return null;
	}

	private void startReplica(String dataRecieved)
			throws CommunicationException, IOException, InterruptedException,
			ExecutionException, TimeoutException {
		// expected string Configuration.REPLICA_START_CMD +
		// Configuration.UDP_DELIMITER + Configuration.REPLICA1 ;
		String ary[] = dataRecieved.split(Configuration.UDP_DELIMITER);
		startReplicaCore(ary[1]);
	}

	private void startReplicaCore(String replicaName)
			throws CommunicationException, IOException, InterruptedException,
			ExecutionException, TimeoutException {
		String data = Configuration.REPLICA_START_CMD;
		if (Configuration.REPLICA1.equals(replicaName)) {
			mgr.send(data, Configuration.REPLICA_IP1,
					Configuration.REPLICA_INTERFACE_PORT1);
			addReplicaToSet(Configuration.REPLICA1);
		} else if (Configuration.REPLICA2.equals(replicaName)) {
			mgr.send(data, Configuration.REPLICA_IP2,
					Configuration.REPLICA_INTERFACE_PORT2);
			addReplicaToSet(Configuration.REPLICA2);

		} else if (Configuration.REPLICA3.equals(replicaName)) {
			mgr.send(data, Configuration.REPLICA_IP3,
					Configuration.REPLICA_INTERFACE_PORT3);
			addReplicaToSet(Configuration.REPLICA3);
		}
	}

	private void killReplicaCore(String replicaName)
			throws CommunicationException, IOException, InterruptedException,
			ExecutionException, TimeoutException {
		String data = Configuration.REPLICA_SHUT_DOWN_CMD;
		if (activeReplicas.contains(replicaName)
				&& Configuration.REPLICA1.equals(replicaName)) {
			mgr.send(data, Configuration.REPLICA_IP1,
					Configuration.REPLICA_INTERFACE_PORT1);
			removeReplicaFromSet(Configuration.REPLICA1);
		} else if (activeReplicas.contains(replicaName)
				&& Configuration.REPLICA2.equals(replicaName)) {
			mgr.send(data, Configuration.REPLICA_IP2,
					Configuration.REPLICA_INTERFACE_PORT2);
			removeReplicaFromSet(Configuration.REPLICA2);

		} else if (activeReplicas.contains(replicaName)
				&& Configuration.REPLICA3.equals(replicaName)) {
			mgr.send(data, Configuration.REPLICA_IP3,
					Configuration.REPLICA_INTERFACE_PORT3);
			removeReplicaFromSet(Configuration.REPLICA3);
		}
	}

	private void killReplica(String dataRecieved)
			throws CommunicationException, IOException, InterruptedException,
			ExecutionException, TimeoutException {
		// expected string Configuration.REPLICA_SHUT_DOWN_CMD +
		// Configuration.UDP_DELIMITER + Configuration.REPLICA1 ;
		String ary[] = dataRecieved.split(Configuration.UDP_DELIMITER);
		killReplicaCore(ary[1]);
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
		}
	}

	private void incrementNotifcationNotRecievedCount()
			throws CommunicationException, IOException, InterruptedException,
			ExecutionException, TimeoutException {
		synchronized (numberOfTimesReplicaHeartBeatMissed) {
			for (Map.Entry<String, Integer> entry : numberOfTimesReplicaHeartBeatMissed
					.entrySet()) {
				Integer i = entry.getValue();
				i++;
				if (i > Configuration.MAX_NO_OF_TRIES) {
					startReplicaCore(entry.getKey());
					i = 0;
				}
				numberOfTimesReplicaHeartBeatMissed.put(entry.getKey(), i);
			}
		}
		Thread.currentThread().sleep(
				Configuration.MAX_DURATION_TO_WAIT_BEFORE_TIMEOUT * 1000);
	}

	public void removeReplicaFromSet(String replicaName) {
		synchronized (activeReplicas) {
			activeReplicas.remove(replicaName);
		}
	}

	public void addReplicaToSet(String replicaName) {
		synchronized (activeReplicas) {
			activeReplicas.add(replicaName);
		}
	}

	@Override
	public void run() {
		String name = Thread.currentThread().getName();
		if (name.equals(Configuration.HEART_BEAT_MONITOR)) {
			while (stopServer) {
				try {
					incrementNotifcationNotRecievedCount();
				} catch (CommunicationException | IOException
						| InterruptedException | ExecutionException
						| TimeoutException e) {
					e.printStackTrace();
				}
			}
		} else if (name.equals(Configuration.RCV_MONITOR)) {
			while (stopServer) {
				String data = this.popFirstVal();
				try{
				if (data != null) {
					String[] arry;
					arry = data.split(Configuration.COMMUNICATION_SEPERATOR);
					String request = arry[1];
					if(request.contains(Configuration.REPLICA_SHUT_DOWN_CMD)){
						killReplica(request);
					}
					else if(request.contains(Configuration.REPLICA_START_CMD)){
						startReplica(request);
					}
					else if(request.contains(Configuration.REPLICA_HEARTBEAT)){
						this.recieveNotificationFromReplica(request);
					}
				}
				}
				catch(Exception e){
					e.printStackTrace();
				}
			}
		}

	}

}
