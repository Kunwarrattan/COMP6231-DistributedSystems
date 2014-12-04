package com.test;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.assignment1.config.Configuration;
import com.sun.org.apache.xml.internal.serializer.ToSAXHandler;

public class Test1 extends Thread{
	private Object monitor = new Object();
	private List<String> x = new LinkedList<String>();
	public String toString(){
		return x.toString();
	}
	public Test1(){
/*		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Thread.currentThread().setName("GET Val 2");
				getVal();
			}
		}).start();*/
		//this.start();
	}
	public void getVal(){
		boolean loopCondition = true;
		while(loopCondition)
		synchronized (monitor) {
			try {
				System.out.println("Gonna Wait.."+Thread.currentThread().getName());
				monitor.wait();
				synchronized (x) {
					if(x.size() == 20)
						loopCondition = false;
					System.out.println("Woke up from my deep slumber.."+Thread.currentThread().getName());
					System.out.println(Thread.currentThread().getName() + x);
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public void run(){
		int i = 0;
		while(i < 20){
			synchronized (monitor) {
				synchronized (x) {
					x.add("uselss"+ ++i);
				}
				System.out.println("X updated : "+x);
				monitor.notifyAll();
			}
			try {
				System.out.println("Gonna sleepp..");
				Thread.currentThread().sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public static void main(String[] args) {
		int ar[] = {1,2};
		int z = 0;
		System.out.println(ar[z++] + " : "+ar[z++]);
		
		HashMap<String, Integer> i = new HashMap<>();
		i.put("x", new Integer(1));
		i.put("y", new Integer(11));
		for(Map.Entry<String, Integer> entry : i.entrySet()){
			Integer u = entry.getValue();
			u++;
			i.put(entry.getKey(), u);
		}
		System.out.println(i);
	/*	System.out.println(new Boolean("true") == false);
		Test1 t = new Test1();
		Thread.currentThread().setName("Main");
		t.getVal();*/
	}
}
