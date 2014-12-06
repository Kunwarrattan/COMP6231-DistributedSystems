package com.assignment1.implementations;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import IdlFiles.LibraryException;
import IdlFiles.LibraryManagementInterfaceOperations;

import com.assignment1.abstractclass.CommunicationFacilitator;
import com.assignment1.config.Configuration;
import com.assignment1.exception.CommunicationException;
import com.assignment1.model.Book;
import com.assignment1.model.StudentAccount;
import com.assignment1.utils.CommunicationManager;
import com.assignment1.utils.FileOps;
import com.assignment1.utils.UDPManager;

/**
 * This class represents the library server and holds all the information
 * regarding the users and books available in this library
 * 
 * @author Kaushik
 * 
 */

public class LibraryServer implements
	LibraryManagementInterfaceOperations, Runnable {
	private CommunicationFacilitator facilitator;
	private HashMap<String, Book> bookMap = null;
	private HashMap<Character, HashMap<String, StudentAccount>> accounts = null;
	private String name;
	private String logFile;
	private String adminFile;
	private LinkedHashMap<String, StudentAccount> accountsCopy = new LinkedHashMap<String, StudentAccount>();
	private int portForGetNonReturners = 0;
	private int portForInterLibraryCommunication = 0;
	@SuppressWarnings("unused")
	private UDPManager udpMgr = null;
	private String replicaName = null;

	/**
	 * CODE start
	 */
	private CommunicationManager mgrone;
	public volatile boolean stopServer = true;

	/**
	 * Object initialization..
	 * 
	 * @param name
	 * @param portForGetNonReturners
	 * @param portForInterLibraryCommunication
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public LibraryServer(String name, int portForGetNonReturners,
			int portForInterLibraryCommunication,String replicaName) throws Exception {
		System.out.println("Library Server Started" + name + " Replica = " + replicaName + "Port" + portForGetNonReturners);
		this.name = name;
		this.facilitator = new CommunicationFacilitator(name);
		this.replicaName = replicaName;
		// this.portForGetNonReturners = portForGetNonReturners;
		this.portForInterLibraryCommunication = portForInterLibraryCommunication;
		FileOps.initServer(name);
		String bookInfoFile = Configuration.CURRENT_DIR + name + "//"
				+ Configuration.BOOK_MAP_SERIALIZED_FILE;
		String acctInfoFile = Configuration.CURRENT_DIR + name + "//"
				+ Configuration.USER_ACCT_MAP_SERIALIZED_FILE;
		logFile = Configuration.CURRENT_DIR + name + "//"
				+ Configuration.LOG_FILE_NAME;
		adminFile = Configuration.CURRENT_DIR + name + "//"
				+ Configuration.ADMIN_FILE_NAME;
		File file1 = new File(bookInfoFile);
		File file2 = new File(acctInfoFile);
		if (file1.exists()) {
			bookMap = (HashMap<String, Book>) FileOps
					.deserializeObjFromFile(bookInfoFile);
		} else {
			FileUtils.forceMkdir(new File(Configuration.CURRENT_DIR + name));
			bookMap = new HashMap<String, Book>();
			FileOps.serializeObjToFile(bookInfoFile, bookMap);
		}
		if (file2.exists()) {
			accounts = (HashMap<Character, HashMap<String, StudentAccount>>) FileOps
					.deserializeObjFromFile(acctInfoFile);
		} else {
			FileUtils.forceMkdir(new File(Configuration.CURRENT_DIR + name));
			accounts = new HashMap<Character, HashMap<String, StudentAccount>>();
			populateKeysForAccounts();
			FileOps.serializeObjToFile(acctInfoFile, accounts);
		}
		if (accounts.size() == 0) {
			String temp = Configuration.CURRENT_DIR + name + "//"
					+ Configuration.USER_DIR;
			FileUtils.deleteQuietly(new File(temp));
			FileUtils.forceMkdir(new File(temp));
		}
		if (bookMap.size() == 0) {
			loadBooks();
		}
		Iterator<HashMap<String, StudentAccount>> iter = accounts.values()
				.iterator();
		while (iter.hasNext()) {
			accountsCopy.putAll(iter.next());
		}
		udpMgr = new UDPManager(this, true);
		mgrone = new CommunicationManager(Configuration.MULTICAST_PORT, Configuration.RECIEVER_ROLE,
				this.facilitator);
		new Thread(this).start();
	}

	/**
	 * Add the default set of keys to accounts map..
	 */
	public void populateKeysForAccounts() {
		Character ch = new Character('A');
		for (int i = 0; i < 26; i++) {
			accounts.put(Character.valueOf((char) (ch + i)),
					new HashMap<String, StudentAccount>());
		}
	}

	/**
	 * This method is called from the client, to safely shut down the system.
	 * This method persists all the necessary data structures to the file
	 * system.
	 * 
	 */
	public void exit() {
		this.stopServer = false;
		this.mgrone.exit();
		System.out.println("LibraryServer : "+this.replicaName + " : exit() :");
	}

	/**
	 * Synchronized getter to obtain the StudentAccount.
	 * 
	 * @param key
	 * @param userName
	 * @return
	 */
	public StudentAccount synchronizedGetAccounts(Character key, String userName) {
		HashMap<String, StudentAccount> studentMap = null;
		studentMap = accounts.get(key);
		if (!studentMap.isEmpty()) {
			synchronized (studentMap) {
				return studentMap.get(userName);
			}
		}
		return null;
	}

	/**
	 * Synchronized getter to obtain the {@link Book} Information.
	 * 
	 * @param key
	 * @param userName
	 * @return
	 */
	public Book synchronizedGetBook(String bookName) {
		Book book = null;
		synchronized (bookMap) {
			book = bookMap.get(bookName);
		}
		if (book != null) {
			return new Book(book.getName(), book.getAuthor(),
					book.getNumberOfCopies());
		}
		return null;
	}

	/**
	 * Sysnchronized setter to put the modified book object to the
	 * {@link #bookMap}
	 * 
	 * @param book
	 * @throws LibraryException
	 */
	public void synchronizedPutBook(Book book) throws LibraryException {
		if (book != null) {
			synchronized (bookMap) {
				bookMap.put(
						book.getName() + "_" + book.getAuthor(),
						new Book(book.getName(), book.getAuthor(), book
								.getNumberOfCopies()));
			}
		} else {
			throw new LibraryException("Book cannot be empty..");
		}
	}

	/**
	 * Synchronized getter to obtain the {@link #accountsCopy} from
	 * {@link #getNonRetuners(String, String, String, int)}
	 * 
	 * @return
	 */
	public LinkedHashMap<String, StudentAccount> synchronizedGetAccountsCopy() {
		synchronized (accounts) {
			return accountsCopy;
		}
	}

	/**
	 * Synchronized setter to put the StudentAccount into the {@link #accounts}
	 * map
	 * 
	 * @param key
	 * @param acct
	 * @throws LibraryException
	 */
	public void synchronizedPutAccounts(Character key, StudentAccount acct)
			throws LibraryException {
		if (key != null && acct != null
				&& StringUtils.isNotBlank(acct.getUserName())) {
			HashMap<String, StudentAccount> studMap = accounts.get(key);
			synchronized (studMap) {
				studMap.put(acct.getUserName(), acct);
				accounts.put(key, studMap);
				accountsCopy.put(acct.getUserName(), acct);
			}
		} else {
			throw new LibraryException(
					"Either key or Student account list is empty..");
		}
	}

	/**
	 * This method is used by the client to create Account in the current
	 * instance of the LibraryServer
	 */
	public void createAccount(String firstName, String lastName,
			String emailAddr, String phoneNumber, String userName,
			String password, String institutionName) throws LibraryException {
		if (StringUtils.isBlank(userName) || StringUtils.isBlank(lastName)
				|| StringUtils.isBlank(emailAddr)
				|| StringUtils.isBlank(phoneNumber)
				|| StringUtils.isBlank(firstName)
				|| StringUtils.isBlank(password)
				|| StringUtils.isBlank(institutionName)) {
			FileOps.appendToFile(logFile, new StringBuilder(userName
					+ ": createAccount : All the parameters are mandatory..."));
			throw new LibraryException("All the parameters are mandatory...");

		}
		if (!institutionName.equals(name)) {
			FileOps.appendToFile(logFile, new StringBuilder(userName
					+ ":Institution name cannot be different.."));
			throw new LibraryException("Institution name cannot be different..");
		}
		if (!(userName.length() >= Configuration.MINIMUM_CREDENTIALS_LEN && userName
				.length() <= Configuration.MAXIMUM_CREDENTIALS_LEN)
				|| !(password.length() >= Configuration.MINIMUM_CREDENTIALS_LEN && password
						.length() <= Configuration.MAXIMUM_CREDENTIALS_LEN)) {
			FileOps.appendToFile(
					logFile,
					new StringBuilder(
							userName
									+ ": createAccount :Username and password should be within 6 and 15 characters.."));
			throw new LibraryException(
					"Username and password should be within 6 and 15 characters..");
		}
		if (!phoneNumber.matches(Configuration.PHONE_REGEX_PTTRN)) {
			FileOps.appendToFile(
					logFile,
					new StringBuilder(
							userName
									+ ": createAccount : Phone number doesn't match the pattern given.."));
			throw new LibraryException(
					"Phone number doesn't match the pattern given..");
		}
		if (!emailAddr.matches(Configuration.EMAIL_PATTERN)) {
			FileOps.appendToFile(
					logFile,
					new StringBuilder(
							userName
									+ ": createAccount : Given Email doesnt match the given pattern.."));
			throw new LibraryException(
					"Given Email doesnt match the given pattern..");
		}
		FileOps.appendToFile(logFile, new StringBuilder(userName
				+ ":All Validations passed.."));
		// StudentAccount studAcct = new StudentAccount(firstName, lastName,
		// emailAddr, phoneNumber, userName, password, institutionName);
		Character key = new Character(userName.toUpperCase().charAt(0));
		StudentAccount studAcct = synchronizedGetAccounts(key, userName);
		boolean alreadyExists = true;
		if (studAcct == null) {
			alreadyExists = false;
			studAcct = new StudentAccount(firstName, lastName, emailAddr,
					phoneNumber, userName, password, institutionName);
			FileOps.appendToFile(
					logFile,
					new StringBuilder(
							userName
									+ ":The account doesn't exist and hence creating new account.."));
		} else {
			if (studAcct.getPassword().equals(password)) {
				studAcct.setFirstName(firstName);
				studAcct.setLastName(lastName);
				studAcct.setEmailAddr(emailAddr);
				studAcct.setLastName(lastName);
				studAcct.setPhNo(phoneNumber);
				FileOps.appendToFile(
						logFile,
						new StringBuilder(
								userName
										+ ":The account exist and hence updating the account information.."));
			} else {
				FileOps.appendToFile(logFile, new StringBuilder(userName
						+ ": createAccount : User Credentials doesnt match.."));
				throw new LibraryException("User Credentials doesnt match..");
			}
		}
		synchronizedPutAccounts(key, studAcct);
		FileOps.appendToFile(logFile, new StringBuilder(userName
				+ ": createAccount : Account successfully created.."));
		if (!alreadyExists)
			FileOps.appendToFile(
					studAcct.getLogFile(),
					new StringBuilder(
							userName
									+ ": createAccount : Account successfully created.."));
		else
			FileOps.appendToFile(studAcct.getLogFile(), new StringBuilder(
					userName + ": createAccount : Account updated.."));
		// Thread.currentThread().sleep(50000);
	}

	/**
	 * The core reserve book functionality used in the system.
	 * 
	 * @param bookName
	 * @param authorName
	 * @param userName
	 * @return
	 * @throws LibraryException
	 */
	public Book reserveBookCore(String bookName, String authorName,
			String userName) throws LibraryException {
		Book book = synchronizedGetBook(bookName + "_" + authorName);
		if (book == null) {
			FileOps.appendToFile(logFile, new StringBuilder(userName
					+ ":The book is not present in this library.."));
			throw new LibraryException(Configuration.BOOK_NOT_FOUND,
					"The book is not present in this library..");
		} else {
			int noOfCopies = book.getNumberOfCopies();
			if (noOfCopies > 0) {
				book.setNumberOfCopies(--noOfCopies);
				synchronizedPutBook(book);
			} else {
				FileOps.appendToFile(logFile, new StringBuilder(userName
						+ ": reserveBook :Book : " + book.getName()
						+ " by Author : " + book.getAuthor()
						+ " is not available currently"));
				throw new LibraryException(Configuration.BOOK_NOT_FOUND,
						"The book is not available currently..");
			}
		}
		return book;

	}

	/**
	 * This method will be called by the client to reserve book in the current
	 * library instance.
	 * 
	 */
	public void reserveBook(String userName, String password, String bookName,
			String authorName, String inst) throws LibraryException {
		if (StringUtils.isBlank(userName) || StringUtils.isBlank(password)
				|| StringUtils.isBlank(bookName)
				|| StringUtils.isBlank(authorName)) {
			FileOps.appendToFile(logFile, new StringBuilder(userName
					+ ": reserveBook : All the parameters are mandatory..."));
			throw new LibraryException("All the parameters are mandatory...");
		}
		FileOps.appendToFile(logFile, new StringBuilder(userName
				+ ":All Validations passed.."));
		Character key = new Character(userName.toUpperCase().charAt(0));
		StudentAccount acct = synchronizedGetAccounts(key, userName);
		if (acct == null) {
			FileOps.appendToFile(logFile, new StringBuilder(userName
					+ ": reserveBook : Student Account doesnt exist.."));
			throw new LibraryException("Student Account doesnt exist..");
		}
		FileOps.appendToFile(logFile, new StringBuilder(userName
				+ ":Account exits.."));
		if (!acct.getPassword().equals(password)) {
			FileOps.appendToFile(logFile, new StringBuilder(userName
					+ ": reserveBook : User Credentials doesnt match.."));
			throw new LibraryException("User Credentials doesnt match..");
		}
		HashMap<String, Book> reservedBook = acct.getReservedBooks();
		Book book = reservedBook.get(bookName + "_" + authorName);
		if (book != null) {
			FileOps.appendToFile(
					logFile,
					new StringBuilder(
							userName
									+ ": reserveBook : A book already exists in the Student account.."));
			throw new LibraryException(
					"A book already exists in the Student account..");
		}
		try {
			book = reserveBookCore(bookName, authorName, userName);
			Book bookInStudAcct = new Book(book.getName(), book.getAuthor(),
					new Date());
			reservedBook.put(bookName + "_" + authorName, bookInStudAcct);
			synchronizedPutAccounts(key, acct);
			FileOps.appendToFile(
					logFile,
					new StringBuilder(userName + ": reserveBook : Book : "
							+ book.getName() + " by Author : "
							+ book.getAuthor() + " is reserved. Total num"
							+ "of books available " + book.getNumberOfCopies()));
			FileOps.appendToFile(
					acct.getLogFile(),
					new StringBuilder(
							userName
									+ ": reserveBook : Successfully reserved the book with name "
									+ bookInStudAcct.getName()
									+ " by Authour : "
									+ bookInStudAcct.getAuthor() + " for "
									+ Configuration.DEFAULT_NO_OF_DAYS
									+ " days.."));
		} catch (LibraryException e) {
			throw e;
		}
	}

	/**
	 * Used by the admin client to get all the users who had gone beyond the
	 * specified number of days.
	 */
	public String getNonRetuners(String adminUserName, String adminPassword,
			String institutionName, int days) throws LibraryException {
		if (StringUtils.isBlank(adminUserName)
				|| StringUtils.isBlank(adminPassword)
				|| StringUtils.isBlank(institutionName) || days <= 0) {
			FileOps.appendToFile(
					logFile,
					new StringBuilder(
							adminUserName
									+ ": getNonRetuners : The specified parameters are not proper.."));
			throw new LibraryException(
					"The specified parameters are not proper..");
		}
		if (!institutionName.equals(name)) {
			FileOps.appendToFile(logFile, new StringBuilder(adminUserName
					+ ": getNonRetuners : The institution name doesnt match.."));
			throw new LibraryException("The institution name doesnt match..");
		}
		if (!adminPassword.equals(Configuration.ADMIN_PASSWORD)
				|| !adminUserName.equals(Configuration.ADMIN_USER_NAME)) {
			FileOps.appendToFile(
					logFile,
					new StringBuilder(
							adminUserName
									+ ": getNonRetuners : The admin credentials doesnt match.."));
			throw new LibraryException("The admin credentials doesnt match..");
		}
		LinkedHashMap<String, StudentAccount> studAcct = synchronizedGetAccountsCopy();
		StringBuilder builder = new StringBuilder();
		if (studAcct != null && studAcct.size() > 0) {
			for (Map.Entry<String, StudentAccount> entry : studAcct.entrySet()) {
				StudentAccount acct = entry.getValue();
				if (acct.getInstitutionName().equals(institutionName)) {
					HashMap<String, Book> reservedBook = acct
							.getReservedBooks();
					if (reservedBook.size() > 0) {
						for (Map.Entry<String, Book> bookIter : reservedBook
								.entrySet()) {
							Book book = bookIter.getValue();
							if (checkWhetherDatePassedNumberOfDays(
									book.getReservedDate(),
									book.getCurrentDate(), days)) {
//								if (builder.length() == 0) {
//									builder.append("Educational Institution : ");
//									builder.append(name);
//									builder.append(System.lineSeparator());
//								}
								builder.append("\n"+acct.getUserName()+book.getName()+book.getAuthor());
//								builder.append(acct.getFirstName() + " ");
//								builder.append(acct.getLastName() + " ");
//								builder.append(acct.getPhNo());
//								builder.append("Educational Institution : "+name);
								
							//	builder.append(System.lineSeparator());
								break;
							}
						}
					}
				}
			}
		}
		StringBuilder logBuilder = new StringBuilder(adminUserName
				+ ": getNonRetuners : Finished Executing the function");
		logBuilder.append(builder);
		FileOps.appendToFile(adminFile, logBuilder);
		FileOps.appendToFile(logFile, logBuilder);
		return builder.toString();
	}

	/**
	 * This function is used by the client to reserve a book, if it is present
	 * in the current library, if not UDP requests will be made to other servers
	 * and will be reserved at that library if that book exists in that server's
	 * instance.
	 */
	public void reserveInterLibrary(String userName, String password,
			String bookName, String authorName, String inst) throws LibraryException {
		try {
			reserveBook(userName, password, bookName, authorName,inst);
		} catch (LibraryException exp) {
			if (exp.code == Configuration.BOOK_NOT_FOUND) {
				if (portForInterLibraryCommunication != Configuration.UDP_PORT_1
						&& wrapperForInterLibraryCommunication(bookName,
								authorName, Configuration.UDP_PORT_1)) {
					updateStudentReservedBooks(userName, bookName, authorName);

				} else if (portForInterLibraryCommunication != Configuration.UDP_PORT_2
						&& wrapperForInterLibraryCommunication(bookName,
								authorName, Configuration.UDP_PORT_2)) {
					updateStudentReservedBooks(userName, bookName, authorName);
				} else if (portForInterLibraryCommunication != Configuration.UDP_PORT_3
						&& wrapperForInterLibraryCommunication(bookName,
								authorName, Configuration.UDP_PORT_3)) {
					updateStudentReservedBooks(userName, bookName, authorName);
				} else {
					throw new LibraryException(
							"Book not found in any of the libraries.");
				}
			} else {
				throw exp;
			}
		}
	}

	/**
	 * Updates the student account with the reserved book details
	 * 
	 * @param userName
	 * @param bookName
	 * @param authorName
	 * @throws LibraryException
	 */
	private void updateStudentReservedBooks(String userName, String bookName,
			String authorName) throws LibraryException {
		Character key = new Character(userName.toUpperCase().charAt(0));
		StudentAccount acct = synchronizedGetAccounts(key, userName);
		if (acct != null) {
			acct.getReservedBooks().put(bookName + "_" + authorName,
					new Book(bookName, authorName, new Date()));
			synchronizedPutAccounts(key, acct);
			FileOps.appendToFile(
					acct.getLogFile(),
					new StringBuilder(
							userName
									+ ": reserveBook : Successfully reserved the book from interlibrary transfer with name "
									+ bookName + " by Authour : " + authorName
									+ " for "
									+ Configuration.DEFAULT_NO_OF_DAYS
									+ " days.."));
		}
	}

	/**
	 * Wrapper that initiates UDP connection to the specified server living on
	 * the specified serverPort
	 * 
	 * @param bookName
	 * @param authorName
	 * @param serverPort
	 * @return
	 */
	private boolean wrapperForInterLibraryCommunication(String bookName,
			String authorName, int serverPort) {
		String content = communicateWithOtherLibrariesUDP(bookName, authorName,
				serverPort);
		return processResponse(content);
	}

	/**
	 * Process response from the other servers.
	 * 
	 * @param content
	 * @return
	 */
	private boolean processResponse(String content) {
		boolean retVal = false;
		if (StringUtils.isNotBlank(content)) {
			int i = 0;
			try {
				i = Integer.parseInt(content);
			} catch (Exception e) {

			}
			if (i == 1)
				retVal = true;
		}
		return retVal;
	}

	/**
	 * This function establishes udp connection with other servers and sends the
	 * response generated by the other servers.
	 * 
	 * @param bookName
	 * @param authorName
	 * @param serverPort
	 * @return
	 */
	private String communicateWithOtherLibrariesUDP(String bookName,
			String authorName, int serverPort) {
		DatagramSocket aSocket = null;
		String content = bookName + ":" + authorName + ":" + name;
		byte[] dataBytes = content.getBytes();
		String response = "";
		try {
			aSocket = new DatagramSocket();
			InetAddress aHost = InetAddress.getByName("localhost");
			DatagramPacket request = new DatagramPacket(dataBytes,
					dataBytes.length, aHost, serverPort);
			aSocket.send(request);
			byte[] buffer = new byte[1000];
			DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
			aSocket.receive(reply);
			byte[] dataFromServer = new byte[reply.getLength()];
			System.arraycopy(reply.getData(), reply.getOffset(),
					dataFromServer, 0, reply.getLength());
			response += new String(dataFromServer);
		} catch (SocketException e) {
			System.out.println("Socket: " + e.getMessage());
		} catch (IOException e) {
			System.out.println("IO: " + e.getMessage());
		} finally {
			if (aSocket != null)
				aSocket.close();
		}
		return response;
	}

	/**
	 * This is an utility method to check if the reservedDate >= currentDate
	 * 
	 * @param reservedDate
	 * @param currentDate
	 * @param days
	 * @return
	 */
	private boolean checkWhetherDatePassedNumberOfDays(Date reservedDate,
			Date currentDate, int days) {
		// Date newDate = new Date(reservedDate.getTime());
		Calendar cal = Calendar.getInstance();
		cal.setTime(reservedDate);
		cal.add(Calendar.DATE, Configuration.DEFAULT_NO_OF_DAYS + days);
		if (currentDate.compareTo(cal.getTime()) >= 0) {
			return true;
		}
		return false;
	}

	/**
	 * This method is used to update the the currentData for a particular user
	 * and for a particular book
	 * 
	 * @param userName
	 * @param bookName
	 * @param numOfDays
	 * @throws Exception
	 */
	public void setDuration(String userName, String bookName, int numOfDays)
			throws Exception {
		StudentAccount acct = synchronizedGetAccounts(userName.toUpperCase()
				.charAt(0), userName);
		if (acct != null) {
			HashMap<String, Book> books = acct.getReservedBooks();
			for (Map.Entry<String, Book> entry : books.entrySet()) {
				Book book = entry.getValue();
				if (book.getName().equals(bookName)) {
					Date date = book.getCurrentDate();
					Calendar cal = Calendar.getInstance();
					cal.setTime(date);
					cal.add(Calendar.DATE, numOfDays);
					book.setCurrentDate(cal.getTime());
					synchronizedPutAccounts(userName.toUpperCase().charAt(0),
							acct);
					return;
				}

			}
		}
	}

//	public static void main(String[] args) throws Exception {
//		try {
//			LibraryServer server1 = new LibraryServer(Configuration.LIBRARY1,
//					0, Configuration.UDP_PORT_1,Configuration.REPLICA1);
//			LibraryServer server2 = new LibraryServer(Configuration.LIBRARY2,
//					0, Configuration.UDP_PORT_2,Configuration.REPLICA1);
//			LibraryServer server3 = new LibraryServer(Configuration.LIBRARY3,
//					0, Configuration.UDP_PORT_3,Configuration.REPLICA1);
//			server1.setDuration("kaushik", "3D Math",
//					Configuration.DEFAULT_NO_OF_DAYS + 4);
//			server1.setDuration("charlie", "3D Math",
//					Configuration.DEFAULT_NO_OF_DAYS + 6);
//			server1.loadBooks();
//			server2.loadBooks();
//			server3.loadBooks();
//		
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

	/**
	 * used to populate data in the server instances...
	 */
	public void loadBooks() {
		if (name == Configuration.LIBRARY1) {
			Book book1 = new Book("Cuda", "Nicholas", 2);
			Book book2 = new Book("Opencl", "Munshi", 3);
			Book book3 = new Book("3D Math", "Fletcher", 1);
			bookMap.put(book1.getName() + "_" + book1.getAuthor(), book1);
			bookMap.put(book2.getName() + "_" + book2.getAuthor(), book2);
			bookMap.put(book3.getName() + "_" + book3.getAuthor(), book3);
		} else if (name == Configuration.LIBRARY2) {
			Book book1 = new Book("Cuda", "Nicholas", 2);
			Book book2 = new Book("Opencl", "Munshi", 3);
			Book book3 = new Book("3D Math", "Fletcher", 1);
			bookMap.put(book1.getName() + "_" + book1.getAuthor(), book1);
			bookMap.put(book2.getName() + "_" + book2.getAuthor(), book2);
			bookMap.put(book3.getName() + "_" + book3.getAuthor(), book3);
		} else {
			Book book1 = new Book("Cuda", "Nicholas", 2);
			Book book2 = new Book("Opencl", "Munshi", 3);
			Book book3 = new Book("3D Math", "Fletcher", 1);
			bookMap.put(book1.getName() + "_" + book1.getAuthor(), book1);
			bookMap.put(book2.getName() + "_" + book2.getAuthor(), book2);
			bookMap.put(book3.getName() + "_" + book3.getAuthor(), book3);
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLogFile() {
		return logFile;
	}

	public void setLogFile(String logFile) {
		this.logFile = logFile;
	}

	public boolean isStopServer() {
		return stopServer;
	}

	public void setStopServer(boolean stopServer) {
		this.stopServer = stopServer;
	}

	public int getPortForInterLibraryCommunication() {
		return portForInterLibraryCommunication;
	}

	public void setPortForInterLibraryCommunication(
			int portForInterLibraryCommunication) {
		this.portForInterLibraryCommunication = portForInterLibraryCommunication;
	}

	public int getPortForGetNonReturners() {
		return portForGetNonReturners;
	}

	public void setPortForGetNonReturners(int portForGetNonReturners) {
		this.portForGetNonReturners = portForGetNonReturners;
	}

	// CODE
	@Override
	public void run() {
		while (stopServer) {
			String data = this.facilitator.popFirstVal();
			if (data != null) {
				String[] arry;
				arry = data.split(Configuration.COMMUNICATION_SEPERATOR);
				String timestamp = arry[0];
				String request = arry[1];
				String hostname = arry[2];
				//TODO: change port to SEQUENCER_RECV_PORT as shown below in all the implementation.. 
				int port = Configuration.SEQUENCER_RECV_PORT;
				String response = timestamp + Configuration.UDP_DELIMITER + this.replicaName+Configuration.UDP_DELIMITER
						+ Configuration.SUCCESS_STRING;
				String failureResponse = timestamp + Configuration.UDP_DELIMITER + this.replicaName+ Configuration.UDP_DELIMITER
						+ Configuration.FAILURE_STRING;
				//TODO: replicate the same in all the servers
				int i = 1;
				if (request.contains(Configuration.CREATE_ACCOUNT)) {
					String requestParam[] = request
							.split(Configuration.UDP_DELIMITER);
					try {
						createAccount(requestParam[i++], requestParam[i++],
								requestParam[i++], requestParam[i++],
								requestParam[i++], requestParam[i++],
								requestParam[i++]);
					} catch (LibraryException e) {
						response = failureResponse;
					}

				} else if (request.contains(Configuration.RESERVE_BOOK)) {
					String requestParam[] = request
							.split(Configuration.UDP_DELIMITER);
					try {
						reserveBook(requestParam[i++], requestParam[i++],
								requestParam[i++], requestParam[i++],requestParam[i++]);

					} catch (LibraryException e) {
						response =failureResponse;
					}
				} else if (request
						.contains(Configuration.RESERVE_INTER_LIBRARY)) {
					String requestParam[] = request
							.split(Configuration.UDP_DELIMITER);
					try {
						reserveInterLibrary(requestParam[i++],
								requestParam[i++], requestParam[i++],
								requestParam[i++],requestParam[i++]);
					} catch (LibraryException e) {
						response = failureResponse;
					}
				} else if (request.contains(Configuration.GET_NON_RETUNERS)) {
					String requestParam[] = request
							.split(Configuration.UDP_DELIMITER);
					try {
						response += Configuration.UDP_DELIMITER
								+ getNonRetuners(requestParam[i++],
										requestParam[i++], requestParam[i++],
										Integer.parseInt(requestParam[i++]));
					} catch (LibraryException e) {
						response = failureResponse;
					}
				}
				try {
					System.out.println("Response : "+response+" Hostname : "+hostname+"Port : " +port);
					mgrone.send(response, hostname, port);
				} catch (CommunicationException | IOException
						| InterruptedException | ExecutionException
						| TimeoutException e) {
					e.printStackTrace();
				}
			}
			try {
				Thread.currentThread().sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// mgrone.exit();
	}
	// CODE
}
