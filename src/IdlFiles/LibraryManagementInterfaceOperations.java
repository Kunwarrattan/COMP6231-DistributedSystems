package IdlFiles;

/**
 * Interface definition: LibraryManagementInterface.
 * 
 * @author OpenORB Compiler
 */
public interface LibraryManagementInterfaceOperations
{
    /**
     * Operation createAccount
     */
    public void createAccount(String firstName, String lastName, String emailAddr, String phoneNumber, String userName, String password, String institutionName)
        throws IdlFiles.LibraryException;

    /**
     * Operation reserveBook
     */
    public void reserveBook(String userName, String password, String bookName, String authorName)
        throws IdlFiles.LibraryException;

    /**
     * Operation reserveInterLibrary
     */
    public void reserveInterLibrary(String userName, String password, String bookName, String authorName)
        throws IdlFiles.LibraryException;

    /**
     * Operation getNonRetuners
     */
    public String getNonRetuners(String adminUserName, String adminPassword, String institutionName, int days)
        throws IdlFiles.LibraryException;

    /**
     * Operation exit
     */
    public void exit();

}
