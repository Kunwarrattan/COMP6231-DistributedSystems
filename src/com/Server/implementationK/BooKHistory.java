package com.Server.implementationK;


import java.io.Serializable;
import java.util.Date;

@SuppressWarnings("serial")
public class BooKHistory implements Serializable {
	String Username;
	String institute;
	String BookName;
	String AuthorName;
	Date issueDate;
	Date dueDate;
}

