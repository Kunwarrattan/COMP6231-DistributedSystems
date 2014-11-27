package IdlFiles;

import com.assignment1.config.Configuration;

/**
 * Exception definition: LibraryException.
 * 
 * @author OpenORB Compiler
 */
public final class LibraryException extends org.omg.CORBA.UserException
{
    /**
     * Exception member code
     */
    public int code = Configuration.DEFAULT_EXCEPTION_CODE;

    /**
     * Exception member message
     */
    public String message;

    /**
     * Default constructor
     */
    public LibraryException()
    {
        super(LibraryExceptionHelper.id());
    }

    /**
     * Constructor with fields initialization
     * @param code code exception member
     * @param message message exception member
     */
    public LibraryException(int code, String message)
    {
        super(LibraryExceptionHelper.id());
        this.code = code;
        this.message = message;
    }
    
    public LibraryException(String message)
    {
        super(LibraryExceptionHelper.id());
        this.message = message;
    }

    /**
     * Full constructor with fields initialization
     * @param code code exception member
     * @param message message exception member
     */
    public LibraryException(String orb_reason, int code, String message)
    {
        super(LibraryExceptionHelper.id() +" " +  orb_reason);
        this.code = code;
        this.message = message;
    }

}
