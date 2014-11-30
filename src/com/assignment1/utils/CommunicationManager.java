package com.assignment1.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.zip.Adler32;
import java.util.zip.CheckedInputStream;

import org.apache.commons.lang3.StringUtils;

import com.assignment1.abstractclass.CommunicationFacilitator;
import com.assignment1.config.Configuration;
import com.assignment1.exception.CommunicationException;

public class CommunicationManager extends Thread {
	// Queue..
	private CommunicationFacilitator facilitator;
	private int receivingPort;
	private DatagramSocket sendingSocket;
	private DatagramSocket recievingSocket;
	private volatile boolean stopServer = true;
	private Boolean result = false;
	private MulticastSocket multicastSender;
	private MulticastSocket multicastReciever;

	public CommunicationManager() throws SocketException {
		sendingSocket = new DatagramSocket();
		sendingSocket.setSoTimeout(Configuration.RECV_TIMEOUT);
	}

	public CommunicationManager(int receivingPort,
			CommunicationFacilitator facilitator) throws SocketException {
		this.receivingPort = receivingPort;
		this.facilitator = facilitator;
		init();
		this.start();
	}

	public CommunicationManager(int multicastPort, String role,
			int receivingPort,CommunicationFacilitator facilitator) throws IOException {
		if (role.equals(Configuration.SENDER_ROLE)) {
			multicastSender = new MulticastSocket(multicastPort);
		} else {
			multicastReciever = new MulticastSocket(multicastPort);
			multicastReciever.joinGroup(InetAddress
					.getByName(Configuration.MULTICAST_ADDR));
		}
		this.receivingPort = receivingPort;
		this.facilitator = facilitator;
		init();
	}

	public CommunicationManager(int multicastPort, String role,CommunicationFacilitator facilitator)
			throws IOException {
		if (role.equals(Configuration.SENDER_ROLE)) {
			multicastSender = new MulticastSocket(multicastPort);
		} else {
			multicastReciever = new MulticastSocket(multicastPort);
			multicastReciever.joinGroup(InetAddress
					.getByName(Configuration.MULTICAST_ADDR));
		}
		this.facilitator = facilitator;
		this.start();
	}

	private void init() throws SocketException {
		recievingSocket = new DatagramSocket(receivingPort);
	}

	public void exit() {
		stopServer = false;
		if (recievingSocket != null) {
			recievingSocket.close();
		}
		if (multicastReciever != null) {
			multicastReciever.close();
		}
	}

	public void send(String data, String hostName, int clientPort)
			throws CommunicationException, IOException, InterruptedException,
			ExecutionException, TimeoutException {
		result = false;
		int i = 0;
		final String timeStamp = SequenceGenerator.getTimeStamp();
		for (i = 0; i < Configuration.MAX_NO_OF_TRIES && result == false; i++) {
			String tempData = timeStamp + Configuration.COMMUNICATION_SEPERATOR
					+ data;
			String checkSum = getCheckSum(tempData);
			String finalData = tempData + Configuration.COMMUNICATION_SEPERATOR
					+ checkSum;
			System.out.println("First packet.." + finalData);
			if (finalData.length() > Configuration.MAX_PACKET_SIZE) {
				throw new CommunicationException(
						"String size cannot be greater than "
								+ Configuration.MAX_PACKET_SIZE);
			}
			byte[] finalBuffer = finalData.getBytes();
			DatagramPacket sendPacket = new DatagramPacket(finalBuffer,
					finalBuffer.length, InetAddress.getByName(hostName),
					clientPort);

			sendingSocket.send(sendPacket);
			ExecutorService service = Executors
					.newSingleThreadScheduledExecutor();
			Future future = service.submit(new Runnable() {
				@Override
				public void run() {
					byte[] buffer = new byte[Configuration.MAX_PACKET_SIZE];
					DatagramPacket recvPacket = new DatagramPacket(buffer,
							Configuration.MAX_PACKET_SIZE);
					try {
						sendingSocket.receive(recvPacket);
						byte[] tempData = recvPacket.getData();
						byte[] recievedData = new byte[recvPacket.getLength()];
						System.arraycopy(tempData, recvPacket.getOffset(),
								recievedData, 0, recvPacket.getLength());
						String actualData = new String(recievedData);
						actualData = actualData.trim();
						System.out.println("response from the reciever.."
								+ actualData);
						if (StringUtils.isNotBlank(actualData)) {
							String[] arry = actualData
									.split(Configuration.COMMUNICATION_SEPERATOR);
							if (arry.length == 4) {
								String checkSum = arry[2];
								String dataRecieved = arry[0]
										+ Configuration.COMMUNICATION_SEPERATOR
										+ arry[1];
								String checkSum1 = getCheckSum(dataRecieved);
								if (checkSum.equals(checkSum1)
										&& timeStamp.equals(arry[0])) {
									if (arry[3]
											.equals(Configuration.ACK_STRING)) {
										synchronized (result) {
											result = true;
											String data = arry[0]
													+ Configuration.COMMUNICATION_SEPERATOR
													+ Configuration.PROCEED_STRING;
											checkSum = getCheckSum(data);
											String finalData = data
													+ Configuration.COMMUNICATION_SEPERATOR
													+ checkSum;
											byte[] finalBuffer = finalData
													.getBytes();
											DatagramPacket sendPacket = new DatagramPacket(
													finalBuffer,
													finalBuffer.length,
													recvPacket.getAddress(),
													recvPacket.getPort());
											sendingSocket.send(sendPacket);
											System.out
													.println("Last packet to the reciever "
															+ finalData);
										}
									}
								}
							}
						}

					} catch (SocketTimeoutException e) {
						System.out.println("Sender timed out..");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
			try {
				future.get(Configuration.MAX_DURATION_TO_WAIT_BEFORE_TIMEOUT,
						TimeUnit.SECONDS);

			} catch (TimeoutException e) {
				System.out.println("Restarting..");
			}
			future.cancel(true);
			service.shutdownNow();
			if (result)
				break;
		}
		if (result) {
			System.out.println("Successfully sent,.");
		}
		if (i >= Configuration.MAX_NO_OF_TRIES) {
			throw new CommunicationException("Deliver of packet failed..");
		}
	}

	private String getCheckSum(String data) throws IOException {
		byte dataInByte[] = data.getBytes();
		ByteArrayInputStream bais = new ByteArrayInputStream(dataInByte);
		CheckedInputStream cis = new CheckedInputStream(bais, new Adler32());
		byte readBuffer[] = new byte[dataInByte.length];
		cis.read(readBuffer);
		String retVal = Long.toString(cis.getChecksum().getValue());
		cis.close();
		return retVal;
	}

	private void recv() throws CommunicationException, InterruptedException,
			ExecutionException, TimeoutException {
		String result = null;
		while (stopServer) {
			byte[] buffer = new byte[Configuration.MAX_PACKET_SIZE];
			DatagramPacket recvPacket = new DatagramPacket(buffer,
					Configuration.MAX_PACKET_SIZE);
			try {
				recievingSocket.receive(recvPacket);
				String actualData = returnProcessedData(recvPacket);
				System.out.println("First packet recieved.." + actualData);
				if (verifyExpectedData(actualData, 3, null)) {
					String response = actualData
							+ Configuration.COMMUNICATION_SEPERATOR
							+ Configuration.ACK_STRING;
					byte[] responseByte = response.getBytes();
					DatagramPacket reply = new DatagramPacket(responseByte,
							responseByte.length, recvPacket.getAddress(),
							recvPacket.getPort());
					System.out.println("Response packet to the sender.."
							+ response);
					recievingSocket.send(reply);
					String data[] = actualData
							.split(Configuration.COMMUNICATION_SEPERATOR);

					String dataRecieved = data[0]
							+ Configuration.COMMUNICATION_SEPERATOR + data[1]
							+ Configuration.COMMUNICATION_SEPERATOR
							+ recvPacket.getAddress().getHostName()
							+ Configuration.COMMUNICATION_SEPERATOR
							+ recvPacket.getPort();
					buffer = new byte[Configuration.MAX_PACKET_SIZE];
					recvPacket = new DatagramPacket(buffer,
							Configuration.MAX_PACKET_SIZE);
					recievingSocket.setSoTimeout(Configuration.RECV_TIMEOUT);
					recievingSocket.receive(recvPacket);
					actualData = returnProcessedData(recvPacket);
					System.out.println("Final packet from sender.."
							+ actualData);
					if (verifyExpectedData(actualData, 3, data[0])) {
						System.out.println("Verified after the final packet..");
						result = dataRecieved;
					}
				}
			} catch (SocketTimeoutException e) {
				System.out.println("Reciever Timeout..");
				try {
					recievingSocket.setSoTimeout(0);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			catch (SocketException e) {
				// TODO: handle exception
				System.out.println("Closed..");
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			if (StringUtils.isNotBlank(result)) {
				String resp[] = result
						.split(Configuration.COMMUNICATION_SEPERATOR);
				facilitator.pushToQueue(resp[0], result);
				result = null;
				System.out.println("Successfully recieved,.");
			}

		}
		recievingSocket.close();
	}

	private String returnProcessedData(DatagramPacket packet) {
		byte[] tempData = packet.getData();
		byte[] recievedData = new byte[packet.getLength()];
		System.arraycopy(tempData, packet.getOffset(), recievedData, 0,
				packet.getLength());
		String actualData = new String(recievedData);
		actualData = actualData.trim();
		return actualData;

	}

	private boolean verifyExpectedData(String data, int length,
			String expectedTimeStamp) throws IOException {
		if (StringUtils.isNotBlank(data)) {
			String[] arry = data.split(Configuration.COMMUNICATION_SEPERATOR);
			if (arry.length == length) {
				String checkSum = arry[arry.length - 1];
				String dataRecieved = arry[0]
						+ Configuration.COMMUNICATION_SEPERATOR + arry[1];
				String checkSum1 = getCheckSum(dataRecieved);
				boolean checkSumValidation = checkSum.equals(checkSum1);
				if (checkSumValidation) {
					if (StringUtils.isBlank(expectedTimeStamp)) {
						return true;
					} else {
						if (expectedTimeStamp.equals(arry[0])) {
							return true;
						} else {
							return false;
						}
					}
				}
			}
		}
		return false;
	}

	public void sendMulticast(String data) throws IOException,
			CommunicationException {
		String timeStamp = SequenceGenerator.getTimeStamp();
		String tempData = timeStamp + Configuration.COMMUNICATION_SEPERATOR
				+ data;
		String checkSum = getCheckSum(tempData);
		String finalData = tempData + Configuration.COMMUNICATION_SEPERATOR
				+ checkSum;
		if (finalData.length() > Configuration.MAX_PACKET_SIZE) {
			throw new CommunicationException(
					"String size cannot be greater than "
							+ Configuration.MAX_PACKET_SIZE);
		}
		byte[] finalBuffer = finalData.getBytes();
		DatagramPacket sendPacket = new DatagramPacket(finalBuffer,
				finalBuffer.length,
				InetAddress.getByName(Configuration.MULTICAST_ADDR),
				multicastSender.getLocalPort());
		multicastSender.send(sendPacket);
	}

	private void multicastRecv() throws IOException {
		try{
		while (stopServer) {
			byte[] buf = new byte[Configuration.MAX_PACKET_SIZE];
			DatagramPacket packet = new DatagramPacket(buf, buf.length);
			multicastReciever.receive(packet);
			String actualData = returnProcessedData(packet);
			if (verifyExpectedData(actualData, 3, null)) {
				String resp[] = actualData
						.split(Configuration.COMMUNICATION_SEPERATOR);
				String dataToQueue = resp[0]
						+ Configuration.COMMUNICATION_SEPERATOR + resp[1]
						+ Configuration.COMMUNICATION_SEPERATOR
						+ packet.getAddress().getHostName()
						+ Configuration.COMMUNICATION_SEPERATOR
						+ Configuration.SEQUENCER_RECV_PORT;
				facilitator.pushToQueue(resp[0], dataToQueue);
			}
		}
		}
		catch(SocketException e){
			System.out.println("Closed..");
		}
		
	}

	@Override
	public void run() {
		Thread thread1 = null;
		Thread thread2 = null;
		if (recievingSocket != null) {
			thread1 = new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					try {
						recv();
					} catch (CommunicationException | InterruptedException
							| ExecutionException | TimeoutException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
			thread1.start();
		}
		if (multicastReciever != null) {
			thread2 = new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					try {
						multicastRecv();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
			thread2.start();
		}
		try {
			if (thread1 != null) {
				thread1.join();
			}
			if (thread2 != null) {
				thread2.join();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
