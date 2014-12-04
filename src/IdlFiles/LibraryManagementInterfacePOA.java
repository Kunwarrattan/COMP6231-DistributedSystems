package IdlFiles;

/**
 * Interface definition: LibraryManagementInterface.
 * 
 * @author OpenORB Compiler
 */
public abstract class LibraryManagementInterfacePOA extends org.omg.PortableServer.Servant
        implements LibraryManagementInterfaceOperations, org.omg.CORBA.portable.InvokeHandler
{
    public LibraryManagementInterface _this()
    {
        return LibraryManagementInterfaceHelper.narrow(_this_object());
    }

    public LibraryManagementInterface _this(org.omg.CORBA.ORB orb)
    {
        return LibraryManagementInterfaceHelper.narrow(_this_object(orb));
    }

    private static String [] _ids_list =
    {
        "IDL:IdlFiles/LibraryManagementInterface:1.0"
    };

    public String[] _all_interfaces(org.omg.PortableServer.POA poa, byte [] objectId)
    {
        return _ids_list;
    }

    public final org.omg.CORBA.portable.OutputStream _invoke(final String opName,
            final org.omg.CORBA.portable.InputStream _is,
            final org.omg.CORBA.portable.ResponseHandler handler)
    {

        if (opName.equals("createAccount")) {
                return _invoke_createAccount(_is, handler);
        } else if (opName.equals("exit")) {
                return _invoke_exit(_is, handler);
        } else if (opName.equals("getNonRetuners")) {
                return _invoke_getNonRetuners(_is, handler);
        } else if (opName.equals("reserveBook")) {
                return _invoke_reserveBook(_is, handler);
        } else if (opName.equals("reserveInterLibrary")) {
                return _invoke_reserveInterLibrary(_is, handler);
        } else {
            throw new org.omg.CORBA.BAD_OPERATION(opName);
        }
    }

    // helper methods
    private org.omg.CORBA.portable.OutputStream _invoke_createAccount(
            final org.omg.CORBA.portable.InputStream _is,
            final org.omg.CORBA.portable.ResponseHandler handler) {
        org.omg.CORBA.portable.OutputStream _output;
        String arg0_in = _is.read_string();
        String arg1_in = _is.read_string();
        String arg2_in = _is.read_string();
        String arg3_in = _is.read_string();
        String arg4_in = _is.read_string();
        String arg5_in = _is.read_string();
        String arg6_in = _is.read_string();

        try
        {
            createAccount(arg0_in, arg1_in, arg2_in, arg3_in, arg4_in, arg5_in, arg6_in);

            _output = handler.createReply();

        }
        catch (IdlFiles.LibraryException _exception)
        {
            _output = handler.createExceptionReply();
            IdlFiles.LibraryExceptionHelper.write(_output,_exception);
        }
        return _output;
    }

    private org.omg.CORBA.portable.OutputStream _invoke_reserveBook(
            final org.omg.CORBA.portable.InputStream _is,
            final org.omg.CORBA.portable.ResponseHandler handler) {
        org.omg.CORBA.portable.OutputStream _output;
        String arg0_in = _is.read_string();
        String arg1_in = _is.read_string();
        String arg2_in = _is.read_string();
        String arg3_in = _is.read_string();
        String arg4_in = _is.read_string();

        try
        {
            reserveBook(arg0_in, arg1_in, arg2_in, arg3_in, arg4_in);

            _output = handler.createReply();

        }
        catch (IdlFiles.LibraryException _exception)
        {
            _output = handler.createExceptionReply();
            IdlFiles.LibraryExceptionHelper.write(_output,_exception);
        }
        return _output;
    }

    private org.omg.CORBA.portable.OutputStream _invoke_reserveInterLibrary(
            final org.omg.CORBA.portable.InputStream _is,
            final org.omg.CORBA.portable.ResponseHandler handler) {
        org.omg.CORBA.portable.OutputStream _output;
        String arg0_in = _is.read_string();
        String arg1_in = _is.read_string();
        String arg2_in = _is.read_string();
        String arg3_in = _is.read_string();
        String arg4_in = _is.read_string();

        try
        {
            reserveInterLibrary(arg0_in, arg1_in, arg2_in, arg3_in, arg4_in);

            _output = handler.createReply();

        }
        catch (IdlFiles.LibraryException _exception)
        {
            _output = handler.createExceptionReply();
            IdlFiles.LibraryExceptionHelper.write(_output,_exception);
        }
        return _output;
    }

    private org.omg.CORBA.portable.OutputStream _invoke_getNonRetuners(
            final org.omg.CORBA.portable.InputStream _is,
            final org.omg.CORBA.portable.ResponseHandler handler) {
        org.omg.CORBA.portable.OutputStream _output;
        String arg0_in = _is.read_string();
        String arg1_in = _is.read_string();
        String arg2_in = _is.read_string();
        int arg3_in = _is.read_long();

        try
        {
            String _arg_result = getNonRetuners(arg0_in, arg1_in, arg2_in, arg3_in);

            _output = handler.createReply();
            _output.write_string(_arg_result);

        }
        catch (IdlFiles.LibraryException _exception)
        {
            _output = handler.createExceptionReply();
            IdlFiles.LibraryExceptionHelper.write(_output,_exception);
        }
        return _output;
    }

    private org.omg.CORBA.portable.OutputStream _invoke_exit(
            final org.omg.CORBA.portable.InputStream _is,
            final org.omg.CORBA.portable.ResponseHandler handler) {
        org.omg.CORBA.portable.OutputStream _output;

        exit();

        _output = handler.createReply();

        return _output;
    }

}
