/**
 * 
 */
package com.Server.implementationK;

/**
 * @author Kunwar
 *
 */
import java.io.Serializable;
import java.util.HashMap;

@SuppressWarnings("serial")
public class Student implements Serializable{
	public String firstname;
	public String lastname;
	public String email;
	public String phoneNumber;
	public String username;
	public String Password;
	public String EducationalInstitute;
	public HashMap<String, Book> bookhistory;	
}
