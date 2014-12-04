package IdlFiles;

/** 
 * Helper class for : LibraryException
 *  
 * @author OpenORB Compiler
 */ 
public class LibraryExceptionHelper
{
    private static final boolean HAS_OPENORB;
    static
    {
        boolean hasOpenORB = false;
        try
        {
            Thread.currentThread().getContextClassLoader().loadClass( "org.openorb.orb.core.Any" );
            hasOpenORB = true;
        }
        catch ( ClassNotFoundException ex )
        {
            // do nothing
        }
        HAS_OPENORB = hasOpenORB;
    }
    /**
     * Insert LibraryException into an any
     * @param a an any
     * @param t LibraryException value
     */
    public static void insert(org.omg.CORBA.Any a, IdlFiles.LibraryException t)
    {
        a.insert_Streamable(new IdlFiles.LibraryExceptionHolder(t));
    }

    /**
     * Extract LibraryException from an any
     *
     * @param a an any
     * @return the extracted LibraryException value
     */
    public static IdlFiles.LibraryException extract( org.omg.CORBA.Any a )
    {
        if ( !a.type().equivalent( type() ) )
        {
            throw new org.omg.CORBA.MARSHAL();
        }
        if (HAS_OPENORB){
/*        if (HAS_OPENORB && a instanceof org.openorb.orb.core.Any) {
            // streamable extraction. The jdk stubs incorrectly define the Any stub
            org.openorb.orb.core.Any any = (org.openorb.orb.core.Any)a;*/
            try {
                org.omg.CORBA.portable.Streamable s = a.extract_Streamable();
                if ( s instanceof IdlFiles.LibraryExceptionHolder )
                    return ( ( IdlFiles.LibraryExceptionHolder ) s ).value;
            }
            catch ( org.omg.CORBA.BAD_INV_ORDER ex )
            {
            }
            IdlFiles.LibraryExceptionHolder h = new IdlFiles.LibraryExceptionHolder( read( a.create_input_stream() ) );
            a.insert_Streamable( h );
            return h.value;
        }
        return read( a.create_input_stream() );
    }


    //
    // Internal TypeCode value
    //
    private static org.omg.CORBA.TypeCode _tc = null;
    private static boolean _working = false;

    /**
     * Return the LibraryException TypeCode
     * @return a TypeCode
     */
    public static org.omg.CORBA.TypeCode type()
    {
        if (_tc == null) {
            synchronized(org.omg.CORBA.TypeCode.class) {
                if (_tc != null)
                    return _tc;
                if (_working)
                    return org.omg.CORBA.ORB.init().create_recursive_tc(id());
                _working = true;
                org.omg.CORBA.ORB orb = org.omg.CORBA.ORB.init();
                org.omg.CORBA.StructMember _members[] = new org.omg.CORBA.StructMember[ 2 ];

                _members[ 0 ] = new org.omg.CORBA.StructMember();
                _members[ 0 ].name = "code";
                _members[ 0 ].type = orb.get_primitive_tc( org.omg.CORBA.TCKind.tk_long );
                _members[ 1 ] = new org.omg.CORBA.StructMember();
                _members[ 1 ].name = "message";
                _members[ 1 ].type = orb.get_primitive_tc( org.omg.CORBA.TCKind.tk_string );
                _tc = orb.create_exception_tc( id(), "LibraryException", _members );
                _working = false;
            }
        }
        return _tc;
    }

    /**
     * Return the LibraryException IDL ID
     * @return an ID
     */
    public static String id()
    {
        return _id;
    }

    private final static String _id = "IDL:IdlFiles/LibraryException:1.0";

    /**
     * Read LibraryException from a marshalled stream
     * @param istream the input stream
     * @return the readed LibraryException value
     */
    public static IdlFiles.LibraryException read(org.omg.CORBA.portable.InputStream istream)
    {
        IdlFiles.LibraryException new_one = new IdlFiles.LibraryException();

        if (!istream.read_string().equals(id()))
         throw new org.omg.CORBA.MARSHAL();
        new_one.code = istream.read_long();
        new_one.message = istream.read_string();

        return new_one;
    }

    /**
     * Write LibraryException into a marshalled stream
     * @param ostream the output stream
     * @param value LibraryException value
     */
    public static void write(org.omg.CORBA.portable.OutputStream ostream, IdlFiles.LibraryException value)
    {
        ostream.write_string(id());
        ostream.write_long( value.code );
        ostream.write_string( value.message );
    }

}
