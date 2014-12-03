package com.assignment1.config;
/**
 * All the configuration for the entire project resides here.
 * @author Kaushik
 *
 */
public class Configuration {
	public static final String LIBRARY1 = "CONCORDIA";
	public static final String LIBRARY2 = "MCGILL";
	public static final String LIBRARY3 = "WATERLOO";
	public static final String CURRENT_DIR = ".//";
	public static final String USER_DIR = "USER";
	public static final String USER_ACCT_MAP_SERIALIZED_FILE= "accountMapInfo.ser";
	public static final String BOOK_MAP_SERIALIZED_FILE= "bookMapInfo.ser";
	public static final String PHONE_REGEX_PTTRN = "\\d\\d\\d-\\d\\d\\d-\\d\\d\\d\\d";
	public static final Integer MINIMUM_CREDENTIALS_LEN = 6;
	public static final Integer MAXIMUM_CREDENTIALS_LEN = 15;
	public static final String EMAIL_PATTERN = "(.)*@(.)*\\.(.)*";
	public static final String DATE_FMT_PTTRN = "dd.MM.yyyy HH:mm:ss:SS";
	public static final String LOG_FILE_NAME = "log.txt";
	public static final int DEFAULT_NO_OF_DAYS = 14;
	public static final String ADMIN_USER_NAME ="Admin";
	public static final String ADMIN_PASSWORD = "Admin";
	public static final String ADMIN_FILE_NAME ="Admin.txt";
	public static final int PORT = 2020;
	public static final String CENTRAL_REPO_NAME = "central";
	public static final String CENTRAL_REPO_SER_FILE = "studentMapping.ser";
	public static final int UDP_PORT_1 = 6789;
	public static final int UDP_PORT_2 = 6790;
	public static final int UDP_PORT_3 = 6791;
	public static final String UDP_DELIMITER = ":";
	public static final int[] UDP_PORTS = {UDP_PORT_1,UDP_PORT_2,UDP_PORT_3};
	public static final int BOOK_NOT_FOUND = 0;
	public static final int DEFAULT_EXCEPTION_CODE = -1;
	public static final String HOSTNAME = "http://localhost:8080/";
	public static final String COMMUNICATION_SEPERATOR = "~";
	public static final int MAX_PACKET_SIZE = 65508;
	public static final int MAX_NO_OF_TRIES = 5;
	public static final int MAX_MILLISECONDS_FOR_PERIODIC_JOB = 5;
	public static final int MAX_DURATION_TO_WAIT_BEFORE_TIMEOUT = 1;
	public static final String ACK_STRING = "ACK";
	public static final String TIMESTAMP_SEPERATOR = "@";
	public static final int QUEUE_SIZE = 100;
	public static final String PROCEED_STRING = "SUCCESS";
	public static final int RECV_TIMEOUT = 900;
	public static final String SENDER_ROLE = "SENDER";
	public static final String RECIEVER_ROLE = "RECIEVER";
	public static final String MULTICAST_ADDR = "224.0.1.20";
	public static final int MULTICAST_PORT = 64;
	public static final int SEQUENCER_RECV_PORT = 6000;
	public static final int RECIEVING_PORT1 = 7000;
	public static final int RECIEVING_PORT2 = 7001;
	public static final int RECIEVING_PORT3 = 7002;
	public static final String CREATE_ACCOUNT = "CREATE_ACCT";
	public static final String RESERVE_BOOK = "RESERVE_BOOK";
	public static final String RESERVE_INTER_LIBRARY = "RESERVE_INTER_LIB";
	public static final String GET_NON_RETUNERS = "GET_NON_RETUNERS";
	public static final String SUCCESS_STRING = "true";
	public static final String FAILURE_STRING = "false";
	public static final String REPLICA1 = "REPLICA1";
	public static final String REPLICA2 = "REPLICA2";
	public static final String REPLICA3 = "REPLICA3";
	public static final int REPLICA_INTERFACE_PORT1 = 5000;
	public static final int REPLICA_INTERFACE_PORT2 = 5001;
	public static final int REPLICA_INTERFACE_PORT3 = 5002;
	public static final String REPLICA_START_CMD = "START";
	public static final String REPLICA_SHUT_DOWN_CMD = "STOP";
	public static final int RM_RECV_PORT = 5003;
	public static String REPLICA_IP1 = "";
	public static String REPLICA_IP2 = "";
	public static String REPLICA_IP3 = "";
	public static final String REPLICA_HEARTBEAT = "MY_HEARTBEAT";
	public static final String HEART_BEAT_MONITOR = "HEART_BEAT_MONITOR";
	public static final String RCV_MONITOR = "RCV_MONITOR";
	public static final int UDP_FRONTEND_PORT_IMP = 4000;
	public static final String SEQUENCER_IP = "";
	public static final String UDP_DELIMITER_SEQ = ",";
}
