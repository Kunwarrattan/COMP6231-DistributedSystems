package com.assignment1.model;

import java.io.Serializable;
import java.util.Date;
/**
 * This class is holds all the information pertaining to a book that is available in a library
 * @author Kaushik
 *
 */
public class Book implements Serializable{
	private String name;
	private String author;
	private int numberOfCopies;
	private Date reservedDate = null;
	private Date currentDate = null;
	public Book(String name, String author, int numberOfCopies){
		this.name = name;
		this.author = author;
		this.numberOfCopies = numberOfCopies;
	}
	public Book(String name, String author, Date reservedDate){
		this.name = name;
		this.author = author;
		this.reservedDate = reservedDate;
		this.currentDate = reservedDate;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public int getNumberOfCopies() {
		return numberOfCopies;
	}
	public void setNumberOfCopies(int numberOfCopies) {
		this.numberOfCopies = numberOfCopies;
	}
	public Date getReservedDate() {
		return reservedDate;
	}
	public void setReservedDate(Date reservedDate) {
		this.reservedDate = reservedDate;
	}
	public Date getCurrentDate() {
		return currentDate;
	}
	public void setCurrentDate(Date currentDate) {
		this.currentDate = currentDate;
	}
	
}
