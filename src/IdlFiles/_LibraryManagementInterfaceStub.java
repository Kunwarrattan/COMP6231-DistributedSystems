package IdlFiles;

/**
 * Interface definition: LibraryManagementInterface.
 * 
 * @author OpenORB Compiler
 */
public class _LibraryManagementInterfaceStub extends org.omg.CORBA.portable.ObjectImpl
        implements LibraryManagementInterface
{
    static final String[] _ids_list =
    {
        "IDL:IdlFiles/LibraryManagementInterface:1.0"
    };

    public String[] _ids()
    {
     return _ids_list;
    }

    private final static Class _opsClass = IdlFiles.LibraryManagementInterfaceOperations.class;

    /**
     * Operation createAccount
     */
    public void createAccount(String firstName, String lastName, String emailAddr, String phoneNumber, String userName, String password, String institutionName)
        throws IdlFiles.LibraryException
    {
        while(true)
        {
            if (!this._is_local())
            {
                org.omg.CORBA.portable.InputStream _input = null;
                try
                {
                    org.omg.CORBA.portable.OutputStream _output = this._request("createAccount",true);
                    _output.write_string(firstName);
                    _output.write_string(lastName);
                    _output.write_string(emailAddr);
                    _output.write_string(phoneNumber);
                    _output.write_string(userName);
                    _output.write_string(password);
                    _output.write_string(institutionName);
                    _input = this._invoke(_output);
                    return;
                }
                catch(org.omg.CORBA.portable.RemarshalException _exception)
                {
                    continue;
                }
                catch(org.omg.CORBA.portable.ApplicationException _exception)
                {
                    String _exception_id = _exception.getId();
                    if (_exception_id.equals(IdlFiles.LibraryExceptionHelper.id()))
                    {
                        throw IdlFiles.LibraryExceptionHelper.read(_exception.getInputStream());
                    }

                    throw new org.omg.CORBA.UNKNOWN("Unexpected User Exception: "+ _exception_id);
                }
                finally
                {
                    this._releaseReply(_input);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _so = _servant_preinvoke("createAccount",_opsClass);
                if (_so == null)
                   continue;
                IdlFiles.LibraryManagementInterfaceOperations _self = (IdlFiles.LibraryManagementInterfaceOperations) _so.servant;
                try
                {
                    _self.createAccount( firstName,  lastName,  emailAddr,  phoneNumber,  userName,  password,  institutionName);
                    return;
                }
                finally
                {
                    _servant_postinvoke(_so);
                }
            }
        }
    }

    /**
     * Operation reserveBook
     */
    public void reserveBook(String userName, String password, String bookName, String authorName)
        throws IdlFiles.LibraryException
    {
        while(true)
        {
            if (!this._is_local())
            {
                org.omg.CORBA.portable.InputStream _input = null;
                try
                {
                    org.omg.CORBA.portable.OutputStream _output = this._request("reserveBook",true);
                    _output.write_string(userName);
                    _output.write_string(password);
                    _output.write_string(bookName);
                    _output.write_string(authorName);
                    _input = this._invoke(_output);
                    return;
                }
                catch(org.omg.CORBA.portable.RemarshalException _exception)
                {
                    continue;
                }
                catch(org.omg.CORBA.portable.ApplicationException _exception)
                {
                    String _exception_id = _exception.getId();
                    if (_exception_id.equals(IdlFiles.LibraryExceptionHelper.id()))
                    {
                        throw IdlFiles.LibraryExceptionHelper.read(_exception.getInputStream());
                    }

                    throw new org.omg.CORBA.UNKNOWN("Unexpected User Exception: "+ _exception_id);
                }
                finally
                {
                    this._releaseReply(_input);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _so = _servant_preinvoke("reserveBook",_opsClass);
                if (_so == null)
                   continue;
                IdlFiles.LibraryManagementInterfaceOperations _self = (IdlFiles.LibraryManagementInterfaceOperations) _so.servant;
                try
                {
                    _self.reserveBook( userName,  password,  bookName,  authorName);
                    return;
                }
                finally
                {
                    _servant_postinvoke(_so);
                }
            }
        }
    }

    /**
     * Operation reserveInterLibrary
     */
    public void reserveInterLibrary(String userName, String password, String bookName, String authorName)
        throws IdlFiles.LibraryException
    {
        while(true)
        {
            if (!this._is_local())
            {
                org.omg.CORBA.portable.InputStream _input = null;
                try
                {
                    org.omg.CORBA.portable.OutputStream _output = this._request("reserveInterLibrary",true);
                    _output.write_string(userName);
                    _output.write_string(password);
                    _output.write_string(bookName);
                    _output.write_string(authorName);
                    _input = this._invoke(_output);
                    return;
                }
                catch(org.omg.CORBA.portable.RemarshalException _exception)
                {
                    continue;
                }
                catch(org.omg.CORBA.portable.ApplicationException _exception)
                {
                    String _exception_id = _exception.getId();
                    if (_exception_id.equals(IdlFiles.LibraryExceptionHelper.id()))
                    {
                        throw IdlFiles.LibraryExceptionHelper.read(_exception.getInputStream());
                    }

                    throw new org.omg.CORBA.UNKNOWN("Unexpected User Exception: "+ _exception_id);
                }
                finally
                {
                    this._releaseReply(_input);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _so = _servant_preinvoke("reserveInterLibrary",_opsClass);
                if (_so == null)
                   continue;
                IdlFiles.LibraryManagementInterfaceOperations _self = (IdlFiles.LibraryManagementInterfaceOperations) _so.servant;
                try
                {
                    _self.reserveInterLibrary( userName,  password,  bookName,  authorName);
                    return;
                }
                finally
                {
                    _servant_postinvoke(_so);
                }
            }
        }
    }

    /**
     * Operation getNonRetuners
     */
    public String getNonRetuners(String adminUserName, String adminPassword, String institutionName, int days)
        throws IdlFiles.LibraryException
    {
        while(true)
        {
            if (!this._is_local())
            {
                org.omg.CORBA.portable.InputStream _input = null;
                try
                {
                    org.omg.CORBA.portable.OutputStream _output = this._request("getNonRetuners",true);
                    _output.write_string(adminUserName);
                    _output.write_string(adminPassword);
                    _output.write_string(institutionName);
                    _output.write_long(days);
                    _input = this._invoke(_output);
                    String _arg_ret = _input.read_string();
                    return _arg_ret;
                }
                catch(org.omg.CORBA.portable.RemarshalException _exception)
                {
                    continue;
                }
                catch(org.omg.CORBA.portable.ApplicationException _exception)
                {
                    String _exception_id = _exception.getId();
                    if (_exception_id.equals(IdlFiles.LibraryExceptionHelper.id()))
                    {
                        throw IdlFiles.LibraryExceptionHelper.read(_exception.getInputStream());
                    }

                    throw new org.omg.CORBA.UNKNOWN("Unexpected User Exception: "+ _exception_id);
                }
                finally
                {
                    this._releaseReply(_input);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _so = _servant_preinvoke("getNonRetuners",_opsClass);
                if (_so == null)
                   continue;
                IdlFiles.LibraryManagementInterfaceOperations _self = (IdlFiles.LibraryManagementInterfaceOperations) _so.servant;
                try
                {
                    return _self.getNonRetuners( adminUserName,  adminPassword,  institutionName,  days);
                }
                finally
                {
                    _servant_postinvoke(_so);
                }
            }
        }
    }

    /**
     * Operation exit
     */
    public void exit()
    {
        while(true)
        {
            if (!this._is_local())
            {
                org.omg.CORBA.portable.InputStream _input = null;
                try
                {
                    org.omg.CORBA.portable.OutputStream _output = this._request("exit",true);
                    _input = this._invoke(_output);
                    return;
                }
                catch(org.omg.CORBA.portable.RemarshalException _exception)
                {
                    continue;
                }
                catch(org.omg.CORBA.portable.ApplicationException _exception)
                {
                    String _exception_id = _exception.getId();
                    throw new org.omg.CORBA.UNKNOWN("Unexpected User Exception: "+ _exception_id);
                }
                finally
                {
                    this._releaseReply(_input);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _so = _servant_preinvoke("exit",_opsClass);
                if (_so == null)
                   continue;
                IdlFiles.LibraryManagementInterfaceOperations _self = (IdlFiles.LibraryManagementInterfaceOperations) _so.servant;
                try
                {
                    _self.exit();
                    return;
                }
                finally
                {
                    _servant_postinvoke(_so);
                }
            }
        }
    }

}
