package com.assignment1.remoteinterface;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

import com.assignment1.exception.LibraryException;

@WebService()
@SOAPBinding(style=Style.RPC)
public interface LibraryManagementInterface {
	@WebMethod public void createAccount(String firstName, String lastName, String emailAddr, String phoneNumber, 
			String userName, String password, String institutionName) throws LibraryException;
	@WebMethod public void reserveBook(String userName, String password, String bookName, String authorName) throws LibraryException;
	@WebMethod public void exit() throws LibraryException;
	@WebMethod public void reserveInterLibrary(String userName, String password, String bookName, String authorName) throws LibraryException;
	@WebMethod public String getNonRetuners(String adminUserName, String adminPassword,
			String institutionName, int days) throws LibraryException;
}
