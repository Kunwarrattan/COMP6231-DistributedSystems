package IdlFiles;

/**
 * Holder class for : LibraryException
 * 
 * @author OpenORB Compiler
 */
final public class LibraryExceptionHolder
        implements org.omg.CORBA.portable.Streamable
{
    /**
     * Internal LibraryException value
     */
    public IdlFiles.LibraryException value;

    /**
     * Default constructor
     */
    public LibraryExceptionHolder()
    { }

    /**
     * Constructor with value initialisation
     * @param initial the initial value
     */
    public LibraryExceptionHolder(IdlFiles.LibraryException initial)
    {
        value = initial;
    }

    /**
     * Read LibraryException from a marshalled stream
     * @param istream the input stream
     */
    public void _read(org.omg.CORBA.portable.InputStream istream)
    {
        value = LibraryExceptionHelper.read(istream);
    }

    /**
     * Write LibraryException into a marshalled stream
     * @param ostream the output stream
     */
    public void _write(org.omg.CORBA.portable.OutputStream ostream)
    {
        LibraryExceptionHelper.write(ostream,value);
    }

    /**
     * Return the LibraryException TypeCode
     * @return a TypeCode
     */
    public org.omg.CORBA.TypeCode _type()
    {
        return LibraryExceptionHelper.type();
    }

}
