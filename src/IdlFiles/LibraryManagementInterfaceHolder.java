package IdlFiles;

/**
 * Holder class for : LibraryManagementInterface
 * 
 * @author OpenORB Compiler
 */
final public class LibraryManagementInterfaceHolder
        implements org.omg.CORBA.portable.Streamable
{
    /**
     * Internal LibraryManagementInterface value
     */
    public IdlFiles.LibraryManagementInterface value;

    /**
     * Default constructor
     */
    public LibraryManagementInterfaceHolder()
    { }

    /**
     * Constructor with value initialisation
     * @param initial the initial value
     */
    public LibraryManagementInterfaceHolder(IdlFiles.LibraryManagementInterface initial)
    {
        value = initial;
    }

    /**
     * Read LibraryManagementInterface from a marshalled stream
     * @param istream the input stream
     */
    public void _read(org.omg.CORBA.portable.InputStream istream)
    {
        value = LibraryManagementInterfaceHelper.read(istream);
    }

    /**
     * Write LibraryManagementInterface into a marshalled stream
     * @param ostream the output stream
     */
    public void _write(org.omg.CORBA.portable.OutputStream ostream)
    {
        LibraryManagementInterfaceHelper.write(ostream,value);
    }

    /**
     * Return the LibraryManagementInterface TypeCode
     * @return a TypeCode
     */
    public org.omg.CORBA.TypeCode _type()
    {
        return LibraryManagementInterfaceHelper.type();
    }

}
