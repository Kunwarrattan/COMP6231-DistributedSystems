package com.assignment1.clients;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * This is the main client, which would call the {@link StudentClient} or the
 * {@link AdminClient} depending on the user
 * 
 * @author Kaushik
 * 
 */
public class MainClient {
	/**
	 * The execution starts here..
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		System.out
				.println("Welcome to Distributed Reservation Management System");
		boolean loopCondition = true;
		Scanner keyboard = new Scanner(System.in);
		MainClient client = new MainClient();
		ArrayList<Thread> threads = new ArrayList<Thread>();
		final StudentClient studClient = new StudentClient();
		final AdminClient adminClient = new AdminClient();
		while (loopCondition) {
			client.showMenu();
			try {
				int value = keyboard.nextInt();
				keyboard.nextLine();
				switch (value) {
				case 1:
					studClient.showDisplayForStudent();
					break;
				case 2:
					adminClient.showAdminMenu();
					break;
				default:
					loopCondition = false;
					break;
				}
			} catch (Exception e) {
				System.out.println("Illegal input. Pls try again.");
				keyboard.nextLine();
			}
		}
		for (Thread thread : threads) {
			thread.join();
		}
		System.out.println("Successfully exited..");
	}
	/**
	 * This function displays all the available functionalities available to the user.
	 */
	public void showMenu() {
		System.out.println("Operations available for you..");
		System.out.println("Press '1' to enter Student Section");
		System.out.println("Press '2' to enter Admin Section");
		System.out.println("Press '3' to Exit");
	}
}
