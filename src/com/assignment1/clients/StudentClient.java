package com.assignment1.clients;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.omg.CORBA.ORB;

import IdlFiles.LibraryException;
import IdlFiles.LibraryManagementInterface;
import IdlFiles.LibraryManagementInterfaceHelper;

import com.assignment1.config.Configuration;
import com.assignment1.utils.FileOps;

/**
 * This class represents the client portion of the student
 * 
 * @author Kaushik
 * 
 */
public class StudentClient {
	private List<Thread> threads = Collections.synchronizedList(new LinkedList<Thread>());
	private HashMap<String, String> studentMap = null;
	private String logFile = Configuration.CURRENT_DIR
			+ Configuration.CENTRAL_REPO_NAME + "//";

	@SuppressWarnings("unchecked")
	public StudentClient() throws Exception {
		File folder = new File(logFile);
		FileUtils.forceMkdir(folder);
		logFile += Configuration.CENTRAL_REPO_SER_FILE;
		File file1 = new File(logFile);
		if (file1.exists()) {
			studentMap = (HashMap<String, String>) FileOps
					.deserializeObjFromFile(logFile);
		} else {
			studentMap = new HashMap<String, String>();
			FileOps.serializeObjToFile(logFile, studentMap);
		}
	}

	/**
	 * Provides the display to the student wherein he can enter his choice on
	 * what he needs to do in the library.
	 * 
	 * @throws Exception
	 */
	public void showDisplayForStudent() throws Exception {
		System.out
				.println("Distributed Reservation Management System : Student Section");
		boolean loopCondition = true;
		Scanner keyboard = new Scanner(System.in);
		while (loopCondition) {
			showMenu();
			try {
				int value = keyboard.nextInt();
				switch (value) {
				case 1:
					createAccount(keyboard);
					break;
				case 2:
					reserveBook(keyboard, false);
					break;
				case 3:
					createAccountUsingMultipleThreads();
					break;
				case 4:
					reserveBook(keyboard, true);
					break;
				case 5:
					for (Thread thread : threads) {
						thread.join();
					}
					exit();
					System.out.println("Successfully exited..");
					return;
				default:
					break;
				}
			} catch (Exception e) {
				System.out.println("Illegal input. Pls try again.");
				keyboard.nextLine();
			}
		}

	}
	/**
	 * This function displays all the available functionalities available to the user.
	 */
	public void showMenu() {
		System.out.println("Operations available for you..");
		System.out.println("Press '1' for Account Creation");
		System.out.println("Press '2' for Book Reservation");
		System.out
				.println("Press '3' to simulate creation of accounts by many threads");
		System.out.println("Press '4' for Interlibrary Book Reservation");
		System.out.println("Press '5' to exit");
	}
	/**
	 * This method is used to intimate the server to safely shutdown.
	 */
	public void exit() {

		try {
			LibraryManagementInterface server = getServerInstance(Configuration.LIBRARY1);
			LibraryManagementInterface server1 = getServerInstance(Configuration.LIBRARY2);
			LibraryManagementInterface server2 = getServerInstance(Configuration.LIBRARY3);
			server.exit();
			server1.exit();
			server2.exit();
			FileOps.serializeObjToFile(logFile, studentMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * This method is used to mimic creation of accounts using multiple threads..
	 */
	public void createAccountUsingMultipleThreads() {
		int i = 0;
		while (i < 5) {
			System.out.println("Create Account.");
			final String firstName = "test";
			final String lastName = "test";
			final String emailAddress = "test@gmail.com";
			final String phoneNumber = "514-111-1111";
			final String userName = "abcdefg1";
			final String password = "123456";
			final String institutionName = Configuration.LIBRARY1;
			System.out.println("Creating threads.. ");
			Thread thread = new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						LibraryManagementInterface server = getServerInstance(institutionName);
						server.createAccount(firstName, lastName, emailAddress,
								phoneNumber, userName, password,
								institutionName);
						if (!studentMap.containsKey(userName)) {
							// synchronized (studentMap) {
							studentMap.put(userName, institutionName);
							// }
							System.out
									.println(userName + ": Account created..");
						} else {
							System.out
									.println(userName + ": Account updated..");
						}
					} catch (LibraryException e) {
						System.out.println(e.message);
					}
				}
			});
			thread.start();
			threads.add(thread);
			i++;
			// threads.add(thread);
		}
	}
	/**
	 * This method is used to get the server instance from the corba object.
	 * @param serverName
	 * @return
	 */
	public LibraryManagementInterface getServerInstance(String serverName) {
		String args[] = null;
		ORB orb = ORB.init(args, null);
		org.omg.CORBA.Object obj = null;
		try {
			obj = orb.string_to_object(FileUtils.readFileToString(new File(
					".//" + serverName + "IOR.txt")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (obj != null) {
			return LibraryManagementInterfaceHelper.narrow(obj);
		}
		return null;
	}
	/**
	 * This method calls the library server, to create an account.
	 * @param keyboard
	 */
	public void createAccount(Scanner keyboard) {

		System.out.println("Create Account.");
		final String firstName = getFirstName(keyboard);
		final String lastName = getLastName(keyboard);
		final String emailAddress = getEmailAddr(keyboard);
		final String phoneNumber = getPhoneNumber(keyboard);
		final String userName = getUserCredentials(keyboard, true);
		final String password = getUserCredentials(keyboard, false);
		final String institutionName = getInstitutionName(keyboard);
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					LibraryManagementInterface server = getServerInstance(institutionName);
					server.createAccount(firstName, lastName, emailAddress,
							phoneNumber, userName, password, institutionName);
					if (!studentMap.containsKey(userName)) {
						synchronized (studentMap) {
							studentMap.put(userName, institutionName);
						}
						System.out.println(userName + ": Account created..");
					} else {
						System.out.println(userName + ": Account updated..");
					}

				} catch (LibraryException e) {
					System.out.println(e.message);
				}
			}
		});
		thread.start();
		threads.add(thread);

	}
	/**
	 * This method is used to reserve a book at a given LibraryServer
	 * @param keyboard
	 * @param isInterLibraryRequest
	 */
	public void reserveBook(Scanner keyboard,
			final boolean isInterLibraryRequest) {
		System.out.println("Reserve Book.");
		final String userName = getUserCredentials(keyboard, true);
		final String password = getUserCredentials(keyboard, false);
		final String bookName = getBookInfo(keyboard, true);
		final String authorName = getBookInfo(keyboard, false);
		final String instName = synchronizedGetInstNameQuietly(userName);
		if (StringUtils.isNotBlank(instName)) {
			System.out.format(
					"The username %s belongs to the institution name %s ",
					userName, instName);
			Thread thread = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						LibraryManagementInterface server = getServerInstance(instName);
						if (!isInterLibraryRequest)
							server.reserveBook(userName, password, bookName,
									authorName,instName);
						else
							server.reserveInterLibrary(userName, password,
									bookName, authorName,instName);
						System.out
								.format("The book with name %s by %s is successfully reserved for the username %s",
										bookName, authorName, userName);
					} catch (LibraryException e) {
						System.out.println(e.message);
					}
				}
			});
			thread.start();
			threads.add(thread);
		} else {
			System.out
					.format("The %s username is not mapped to any of the Educational Institution..",
							userName);
		}
	}
	
	private String synchronizedGetInstNameQuietly(String userName) {
		synchronized (studentMap) {
			return studentMap.get(userName);
		}
	}
	/**
	 * Get first name of the student.
	 * @param keyboard
	 * @return
	 */
	private String getFirstName(Scanner keyboard) {
		String firstName;
		while (true) {
			System.out.println("Pls Enter your first name :");
			firstName = keyboard.next();
			if (StringUtils.isNotBlank(firstName)) {
				break;
			}
			System.out.println("Pls enter a valid input..");
		}
		keyboard.nextLine();
		return firstName;
	}
	/**
	 * Get last name of the student.
	 * @param keyboard
	 * @return
	 */
	private String getLastName(Scanner keyboard) {
		String lastName;
		while (true) {
			System.out.println("Pls Enter your last name :");
			lastName = keyboard.next();
			if (StringUtils.isNotBlank(lastName)) {
				break;
			}
			System.out.println("Pls enter a valid input..");
		}
		keyboard.nextLine();
		return lastName;
	}
	/**
	 * Get email addr of the student.
	 * @param keyboard
	 * @return
	 */
	private String getEmailAddr(Scanner keyboard) {
		String emailAddress;
		while (true) {
			System.out.println("Pls Enter your email address :");
			emailAddress = keyboard.next();
			if (StringUtils.isNotBlank(emailAddress)) {
				if (emailAddress.matches(Configuration.EMAIL_PATTERN)) {
					break;
				} else {
					System.out
							.println("Pls enter email address in the format xxx@xxx.xxx");
				}
			} else {
				System.out.println("Pls enter a valid input..");
			}
		}
		keyboard.nextLine();
		return emailAddress;
	}
	/**
	 * Get phone number of the student..
	 * @param keyboard
	 * @return
	 */
	private String getPhoneNumber(Scanner keyboard) {
		String phoneNumber;
		while (true) {
			System.out.println("Pls Enter your phone number :");
			phoneNumber = keyboard.next();
			if (StringUtils.isNotBlank(phoneNumber)) {
				if (phoneNumber.matches(Configuration.PHONE_REGEX_PTTRN)) {
					break;
				} else {
					System.out
							.println("Pls enter phone number in the format ddd-ddd-dddd");
				}
			} else {
				System.out.println("Pls enter a valid input..");
			}
		}
		keyboard.nextLine();
		return phoneNumber;
	}
	/**
	 * Get user name or password of the user..
	 * @param keyboard
	 * @param isUserName
	 * @return
	 */
	private String getUserCredentials(Scanner keyboard, boolean isUserName) {
		String content;
		while (true) {
			if (isUserName)
				System.out.println("Pls Enter your unique user name :");
			else
				System.out.println("Pls Enter your password :");
			content = keyboard.next();
			if (StringUtils.isNotBlank(content)) {
				if (content.length() >= Configuration.MINIMUM_CREDENTIALS_LEN
						&& content.length() <= Configuration.MAXIMUM_CREDENTIALS_LEN) {
					break;
				} else {
					System.out.println("The input should be minimum of "
							+ Configuration.MINIMUM_CREDENTIALS_LEN
							+ " and maximum of "
							+ Configuration.MAXIMUM_CREDENTIALS_LEN);
				}
			} else {
				System.out.println("Pls enter a valid input..");
			}
		}
		keyboard.nextLine();
		return content;
	}
	/**
	 * Get institution name of the student.
	 * @param keyboard
	 * @return
	 */
	private String getInstitutionName(Scanner keyboard) {
		String institutionName;
		while (true) {
			System.out.println("Pls Enter your Institution name :");
			institutionName = keyboard.next();
			if (StringUtils.isNotBlank(institutionName)) {
				if (institutionName.equals(Configuration.LIBRARY1)
						|| institutionName.equals(Configuration.LIBRARY2)
						|| institutionName.equals(Configuration.LIBRARY3)) {
					break;
				} else {
					System.out
							.println("Possible values for institution name : "
									+ Configuration.LIBRARY1 + " or "
									+ Configuration.LIBRARY2 + " or "
									+ Configuration.LIBRARY3);
				}
			} else {
				System.out.println("Pls enter a valid input..");
			}
		}
		keyboard.nextLine();
		return institutionName;
	}
	/**
	 * Get book details that the student wants to register.
	 * @param keyboard
	 * @param isBookName
	 * @return
	 */
	private String getBookInfo(Scanner keyboard, boolean isBookName) {
		String content;
		while (true) {
			if (isBookName)
				System.out
						.println("Pls Enter the book name you would want to reserve :");
			else
				System.out
						.println("Pls Enter the author name of the book you would want to reserve :");
			content = keyboard.nextLine();
			if (StringUtils.isNotBlank(content)) {
				break;
			}
			System.out.println("Pls enter a valid input..");
		}
		return content;
	}
}
