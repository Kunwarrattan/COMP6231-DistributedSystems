package com.assignment1.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
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
	public volatile boolean stopServer = true;
	public Boolean result = false;
	private boolean isMultiCast = false;

	public CommunicationManager() throws SocketException{
		sendingSocket = new DatagramSocket(); 
	}
	public CommunicationManager(int receivingPort,
			CommunicationFacilitator facilitator) throws SocketException {
		this.receivingPort = receivingPort;
		this.facilitator = facilitator;
		init();
		this.start();
	}

	public CommunicationManager(int receivingPort, boolean isMultiCast,
			CommunicationFacilitator facilitator) throws SocketException {
		this.receivingPort = receivingPort;
		this.isMultiCast = isMultiCast;
		this.facilitator = facilitator;
		init();
		this.start();
	}

	public void init() throws SocketException {
		recievingSocket = new DatagramSocket(receivingPort);
	}

	public void send(String data, String hostName, int clientPort)
			throws CommunicationException, IOException, InterruptedException,
			ExecutionException, TimeoutException {
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
		int i = 0;
		for (i = 0; i < Configuration.MAX_NO_OF_TRIES && result == false; i++) {
			System.out.println(InetAddress.getByName(hostName));
			System.out.println(clientPort);
			DatagramPacket sendPacket = new DatagramPacket(finalBuffer,
					finalBuffer.length, InetAddress.getByName(hostName),
					clientPort);

			sendingSocket.send(sendPacket);
			sendingSocket.setSoTimeout(900);
			System.out.println(clientPort);
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
						if (StringUtils.isNotBlank(actualData)) {
							String[] arry = actualData
									.split(Configuration.COMMUNICATION_SEPERATOR);
							if (arry.length == 4) {
								String checkSum = arry[2];
								String dataRecieved = arry[0]
										+ Configuration.COMMUNICATION_SEPERATOR
										+ arry[1];
								String checkSum1 = getCheckSum(dataRecieved);
								if (checkSum.equals(checkSum1)) {
									if (arry[3]
											.equals(Configuration.ACK_STRING)) {
										synchronized (result) {
											result = true;
											System.out.println("Recieved..");
										}
									}
								}
							}
						}

					}
					catch(IOException e){
						System.out.println("Timed out..");
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
		if (i >= Configuration.MAX_NO_OF_TRIES) {
			throw new CommunicationException("Deliver of packet failed..");
		}
		sendingSocket.close();
		
	}

	public String getCheckSum(String data) throws IOException {
		byte dataInByte[] = data.getBytes();
		ByteArrayInputStream bais = new ByteArrayInputStream(dataInByte);
		CheckedInputStream cis = new CheckedInputStream(bais, new Adler32());
		byte readBuffer[] = new byte[dataInByte.length];
		cis.read(readBuffer);
		String retVal = Long.toString(cis.getChecksum().getValue());
		cis.close();
		return retVal;
	}

	public void recv() throws CommunicationException, InterruptedException,
			ExecutionException, TimeoutException {
		int i = 0;
		String result = null;
		for (i = 0; i < Configuration.MAX_NO_OF_TRIES && result == null; i++) {
			byte[] buffer = new byte[Configuration.MAX_PACKET_SIZE];
			DatagramPacket recvPacket = new DatagramPacket(buffer,
					Configuration.MAX_PACKET_SIZE);
			try {
				recievingSocket.receive(recvPacket);
				byte[] tempData = recvPacket.getData();
				byte[] recievedData = new byte[recvPacket.getLength()];
				System.arraycopy(tempData, recvPacket.getOffset(),
						recievedData, 0, recvPacket.getLength());
				String actualData = new String(recievedData);
				actualData = actualData.trim();
				if (StringUtils.isNotBlank(actualData)) {
					String[] arry = actualData
							.split(Configuration.COMMUNICATION_SEPERATOR);
					if (arry.length == 3) {
						String checkSum = arry[2];
						String dataRecieved = arry[0]
								+ Configuration.COMMUNICATION_SEPERATOR
								+ arry[1];
						String checkSum1 = getCheckSum(dataRecieved);
						if (checkSum.equals(checkSum1)) {
							String response = actualData
									+ Configuration.COMMUNICATION_SEPERATOR
									+ Configuration.ACK_STRING;
							byte[] responseByte = response.getBytes();
							DatagramPacket reply = new DatagramPacket(
									responseByte, responseByte.length,
									recvPacket.getAddress(),
									recvPacket.getPort());
							recievingSocket.send(reply);
							dataRecieved += Configuration.COMMUNICATION_SEPERATOR
									+ recvPacket.getAddress().getHostName()
									+ Configuration.COMMUNICATION_SEPERATOR
									+ recvPacket.getPort();
						}
					}
				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			/*
			 * ExecutorService service = Executors
			 * .newSingleThreadScheduledExecutor(); Future<String> future =
			 * service.submit(new Callable<String>() {
			 * 
			 * @Override public String call() { byte[] buffer = new
			 * byte[Configuration.MAX_PACKET_SIZE]; DatagramPacket recvPacket =
			 * new DatagramPacket(buffer, Configuration.MAX_PACKET_SIZE); try {
			 * recievingSocket.receive(recvPacket); byte[] tempData =
			 * recvPacket.getData(); byte[] recievedData = new
			 * byte[recvPacket.getLength()]; System.arraycopy(tempData,
			 * recvPacket.getOffset(), recievedData, 0, recvPacket.getLength());
			 * String actualData = new String(recievedData); actualData =
			 * actualData.trim(); if (StringUtils.isNotBlank(actualData)) {
			 * String[] arry = actualData
			 * .split(Configuration.COMMUNICATION_SEPERATOR); if (arry.length ==
			 * 3) { String checkSum = arry[2]; String dataRecieved = arry[0] +
			 * Configuration.COMMUNICATION_SEPERATOR + arry[1]; String checkSum1
			 * = getCheckSum(dataRecieved); if (checkSum.equals(checkSum1)) {
			 * String response = actualData +
			 * Configuration.COMMUNICATION_SEPERATOR + Configuration.ACK_STRING;
			 * byte[] responseByte = response.getBytes(); DatagramPacket reply =
			 * new DatagramPacket( responseByte, responseByte.length,
			 * recvPacket.getAddress(), recvPacket .getPort());
			 * recievingSocket.send(reply); dataRecieved +=
			 * Configuration.COMMUNICATION_SEPERATOR + recvPacket.getAddress()
			 * .getHostName() + Configuration.COMMUNICATION_SEPERATOR +
			 * recvPacket.getPort(); return dataRecieved; } } }
			 * 
			 * } catch (IOException e) { // TODO Auto-generated catch block
			 * e.printStackTrace(); } return null; } }); result = future.get(
			 * Configuration.MAX_DURATION_TO_WAIT_BEFORE_TIMEOUT,
			 * TimeUnit.SECONDS); }
			 */
			if (result != null) {
				String resp[] = result
						.split(Configuration.COMMUNICATION_SEPERATOR);
				int reqId = Integer.parseInt(resp[0]
						.split(Configuration.TIMESTAMP_SEPERATOR)[1]);
				facilitator.pushToQueue(reqId, result);
			}
		}
	}

	@Override
	public void run() {
		while (stopServer) {
			try {
				recv();
			} catch (CommunicationException | InterruptedException
					| ExecutionException | TimeoutException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
