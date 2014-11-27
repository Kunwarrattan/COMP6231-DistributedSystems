package com.test;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Date;

public class Test {
	public static void main(String[] args) throws IOException, InterruptedException {
		Sender s = new Sender(64);
		ThreadManager r = new ThreadManager();
		ThreadManager r1 = new ThreadManager();
		r.start();
		r1.start();
		//Thread.currentThread().setPriority(1);
		while(r.val1 != true || r1.val1 != true){
			//Thread.currentThread().sleep(1);
			//System.out.println(r.val1 || r1.val1);
		}
		System.out.println("going to send..");
		s.send("kaushik");
		while((r.val != true || r1.val != true) || (r.isAlive() && r1.isAlive())){
			//System.out.println(r.val || r.val1);
			//Thread.sleep(1000);
		}
		System.out.println("closing..");
		
	}
}
class ThreadManager extends Thread{
	public boolean val = false;
	public boolean val1  = false;
	@Override
	public void run() {
		// TODO Auto-generated method stub
		Reciever r;
		try {
			r = new Reciever(64);
			
			byte[] buf = new byte[65508];
			System.out.println("Going. to be started...");
			DatagramPacket packet = new DatagramPacket(buf, 65508);
				val1 = true;
			r.recieve(packet);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			val = true;
			System.out.println(val+" Id :"+Thread.currentThread().getId());
	}
	
}
class Sender{
	private MulticastSocket s;
	public Sender(int port) throws IOException{
		s = new MulticastSocket(port);
		//s.joinGroup(InetAddress.getByName("224.0.1.20"));
	}
	public void send(String data) throws IOException{
		System.out.println("Server"+s.getLocalPort());
		DatagramPacket packet = new DatagramPacket(data.getBytes(), data.getBytes().length,InetAddress.getByName("224.0.1.20"),s.getLocalPort());
		s.send(packet);
		System.out.println("server"+s.getRemoteSocketAddress());
		s.close();
	}
}
class Reciever{
	private MulticastSocket s;
	public Reciever(int port) throws IOException{
		s = new MulticastSocket(port);
		s.joinGroup(InetAddress.getByName("224.0.1.20"));
	}
	public void recieve(DatagramPacket packet) throws IOException{
		s.receive(packet);
		byte recv[] = new byte[65508];
		System.arraycopy(packet.getData(), 0, recv, 0, packet.getData().length);
		//System.out.println(new String(recv));
		System.out.println(new String(recv).trim() + "Date : "+ new Date() + "length :" +new String(recv).trim().length());
		s.leaveGroup(InetAddress.getByName("224.0.1.20"));
		s.close();
	}
}
