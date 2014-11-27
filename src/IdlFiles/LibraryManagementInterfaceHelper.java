package IdlFiles;

/** 
 * Helper class for : LibraryManagementInterface
 *  
 * @author OpenORB Compiler
 */ 
public class LibraryManagementInterfaceHelper
{
    /**
     * Insert LibraryManagementInterface into an any
     * @param a an any
     * @param t LibraryManagementInterface value
     */
    public static void insert(org.omg.CORBA.Any a, IdlFiles.LibraryManagementInterface t)
    {
        a.insert_Object(t , type());
    }

    /**
     * Extract LibraryManagementInterface from an any
     *
     * @param a an any
     * @return the extracted LibraryManagementInterface value
     */
    public static IdlFiles.LibraryManagementInterface extract( org.omg.CORBA.Any a )
    {
        if ( !a.type().equivalent( type() ) )
        {
            throw new org.omg.CORBA.MARSHAL();
        }
        try
        {
            return IdlFiles.LibraryManagementInterfaceHelper.narrow( a.extract_Object() );
        }
        catch ( final org.omg.CORBA.BAD_PARAM e )
        {
            throw new org.omg.CORBA.MARSHAL(e.getMessage());
        }
    }

    //
    // Internal TypeCode value
    //
    private static org.omg.CORBA.TypeCode _tc = null;

    /**
     * Return the LibraryManagementInterface TypeCode
     * @return a TypeCode
     */
    public static org.omg.CORBA.TypeCode type()
    {
        if (_tc == null) {
            org.omg.CORBA.ORB orb = org.omg.CORBA.ORB.init();
            _tc = orb.create_interface_tc( id(), "LibraryManagementInterface" );
        }
        return _tc;
    }

    /**
     * Return the LibraryManagementInterface IDL ID
     * @return an ID
     */
    public static String id()
    {
        return _id;
    }

    private final static String _id = "IDL:IdlFiles/LibraryManagementInterface:1.0";

    /**
     * Read LibraryManagementInterface from a marshalled stream
     * @param istream the input stream
     * @return the readed LibraryManagementInterface value
     */
    public static IdlFiles.LibraryManagementInterface read(org.omg.CORBA.portable.InputStream istream)
    {
        return(IdlFiles.LibraryManagementInterface)istream.read_Object(IdlFiles._LibraryManagementInterfaceStub.class);
    }

    /**
     * Write LibraryManagementInterface into a marshalled stream
     * @param ostream the output stream
     * @param value LibraryManagementInterface value
     */
    public static void write(org.omg.CORBA.portable.OutputStream ostream, IdlFiles.LibraryManagementInterface value)
    {
        ostream.write_Object((org.omg.CORBA.portable.ObjectImpl)value);
    }

    /**
     * Narrow CORBA::Object to LibraryManagementInterface
     * @param obj the CORBA Object
     * @return LibraryManagementInterface Object
     */
    public static LibraryManagementInterface narrow(org.omg.CORBA.Object obj)
    {
        if (obj == null)
            return null;
        if (obj instanceof LibraryManagementInterface)
            return (LibraryManagementInterface)obj;

        if (obj._is_a(id()))
        {
            _LibraryManagementInterfaceStub stub = new _LibraryManagementInterfaceStub();
            stub._set_delegate(((org.omg.CORBA.portable.ObjectImpl)obj)._get_delegate());
            return stub;
        }

        throw new org.omg.CORBA.BAD_PARAM();
    }

    /**
     * Unchecked Narrow CORBA::Object to LibraryManagementInterface
     * @param obj the CORBA Object
     * @return LibraryManagementInterface Object
     */
    public static LibraryManagementInterface unchecked_narrow(org.omg.CORBA.Object obj)
    {
        if (obj == null)
            return null;
        if (obj instanceof LibraryManagementInterface)
            return (LibraryManagementInterface)obj;

        _LibraryManagementInterfaceStub stub = new _LibraryManagementInterfaceStub();
        stub._set_delegate(((org.omg.CORBA.portable.ObjectImpl)obj)._get_delegate());
        return stub;

    }

}
