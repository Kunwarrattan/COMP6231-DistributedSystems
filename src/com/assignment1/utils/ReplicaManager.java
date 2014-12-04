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
	private HashMap<String, Integer> failureTracker;
	public volatile boolean stopServer = true;

	public ReplicaManager() throws SocketException {
		this.failureTracker = new HashMap<String, Integer>();
		this.activeReplicas = new HashSet<String>();
		this.numberOfTimesReplicaHeartBeatMissed = new HashMap<String, Integer>();
		mgr = new CommunicationManager(Configuration.RM_RECV_PORT, this);
		Thread thread1 = new Thread(this, Configuration.HEART_BEAT_MONITOR);
		Thread thread2 = new Thread(this, Configuration.RCV_MONITOR);
		thread1.start();
		thread2.start();
	}

	public void listActiveReplicas(String timestamp, String hostName,
			int clientPort) {
		try {
			mgr.send(timestamp + Configuration.UDP_DELIMITER
					+ Configuration.REPLICA_COUNT_STR
					+ Configuration.UDP_DELIMITER + activeReplicas.size(),
					hostName, clientPort);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void handleFailure(String replica) {
		try {
			synchronized (failureTracker) {
				Integer i = failureTracker.get(replica);
				if (i != null) {
					i++;
					if (i == 2) {
						startReplicaCore(replica);
						i = 0;
					}
					failureTracker.put(replica, i);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
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
			resetReplicaFailureTrackerVal(Configuration.REPLICA1);
		} else if (Configuration.REPLICA2.equals(replicaName)) {
			mgr.send(data, Configuration.REPLICA_IP2,
					Configuration.REPLICA_INTERFACE_PORT2);
			addReplicaToSet(Configuration.REPLICA2);
			resetReplicaFailureTrackerVal(Configuration.REPLICA2);

		} else if (Configuration.REPLICA3.equals(replicaName)) {
			mgr.send(data, Configuration.REPLICA_IP3,
					Configuration.REPLICA_INTERFACE_PORT3);
			addReplicaToSet(Configuration.REPLICA3);
			resetReplicaFailureTrackerVal(Configuration.REPLICA3);
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
			removeReplicaFromFailureTracker(Configuration.REPLICA1);
		} else if (activeReplicas.contains(replicaName)
				&& Configuration.REPLICA2.equals(replicaName)) {
			mgr.send(data, Configuration.REPLICA_IP2,
					Configuration.REPLICA_INTERFACE_PORT2);
			removeReplicaFromSet(Configuration.REPLICA2);
			removeReplicaFromFailureTracker(Configuration.REPLICA2);

		} else if (activeReplicas.contains(replicaName)
				&& Configuration.REPLICA3.equals(replicaName)) {
			mgr.send(data, Configuration.REPLICA_IP3,
					Configuration.REPLICA_INTERFACE_PORT3);
			removeReplicaFromSet(Configuration.REPLICA3);
			removeReplicaFromFailureTracker(Configuration.REPLICA3);
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
			if (i == null) {
				addReplicaToSet(ary[1]);
				resetReplicaFailureTrackerVal(ary[1]);
				numberOfTimesReplicaHeartBeatMissed.put(ary[1], 0);
			} else {
				if (i > 0) {
					numberOfTimesReplicaHeartBeatMissed.put(ary[1], --i);
				}
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

	public void resetReplicaFailureTrackerVal(String replicaName) {
		synchronized (failureTracker) {
			failureTracker.put(replicaName, 0);
		}
	}

	public void removeReplicaFromFailureTracker(String replicaName) {
		synchronized (failureTracker) {
			failureTracker.remove(replicaName);
		}
	}

	@Override
	public void run() {
		String name = Thread.currentThread().getName();
		System.out.println("Replica Manager is running for" + name);
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
				try {
					if (data != null) {
						String[] arry;
						arry = data
								.split(Configuration.COMMUNICATION_SEPERATOR);
						String timestamp = arry[0];
						String request = arry[1];
						String hostName = arry[2];
						int port = Integer.parseInt(arry[3]);
						if (request
								.contains(Configuration.REPLICA_SHUT_DOWN_CMD)) {
							killReplica(request);
						} else if (request
								.contains(Configuration.REPLICA_START_CMD)) {
							startReplica(request);
						} else if (request
								.contains(Configuration.REPLICA_HEARTBEAT)) {
							this.recieveNotificationFromReplica(request);
						} else if (request
								.contains(Configuration.ERROR_IN_OUTPUT_STRING)) {
							this.handleFailure(request
									.split(Configuration.UDP_DELIMITER)[1]);
						} else if (request
								.contains(Configuration.LIST_ACTIVE_REPLICA)) {
							this.listActiveReplicas(timestamp, hostName, port);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	}

}
