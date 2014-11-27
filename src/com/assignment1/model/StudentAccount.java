package com.assignment1.model;

import java.io.Serializable;
import java.util.HashMap;

import com.assignment1.config.Configuration;
/**
 * This class represents StudentAccount of a library.
 * @author Kaushik
 *
 */
public class StudentAccount implements Serializable{
	private String firstName;
	private String lastName;
	private String emailAddr;
	private String phNo;
	private String userName;
	private String password;
	private HashMap<String, Book> reservedBooks = null;
	private float fine;
	private String institutionName;
	private String logFile;
	public StudentAccount(String firstName, String lastName, String emailAddr,
			String phNo, String userName, String password, String instName) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.emailAddr = emailAddr;
		this.phNo = phNo;
		this.userName = userName;
		this.password = password;
		this.reservedBooks = new HashMap<String, Book>();
		this.institutionName = instName;
		this.logFile = Configuration.CURRENT_DIR+instName+"//"+Configuration.USER_DIR+"//"+userName+".txt";
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getEmailAddr() {
		return emailAddr;
	}
	public void setEmailAddr(String emailAddr) {
		this.emailAddr = emailAddr;
	}
	public String getPhNo() {
		return phNo;
	}
	public void setPhNo(String phNo) {
		this.phNo = phNo;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public HashMap<String, Book> getReservedBooks() {
		return reservedBooks;
	}
	public void setReservedBooks(HashMap<String, Book> reservedBooks) {
		this.reservedBooks = reservedBooks;
	}
	public float getFine() {
		return fine;
	}
	public void setFine(float fine) {
		this.fine = fine;
	}
	public String getInstitutionName() {
		return institutionName;
	}
	public void setInstitutionName(String institutionName) {
		this.institutionName = institutionName;
	}
	public String getLogFile() {
		return logFile;
	}
	public void setLogFile(String logFile) {
		this.logFile = logFile;
	}
	
}
