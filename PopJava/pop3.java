///////////////////////////////////////////
//
// A minimalist POP3 Client 
//
//
//
//
//
//
//

import java.io.*;
import java.util.*;
import java.net.*;
///////////////////////////////////////////
//
//
//
//
//
//
//
//

public class pop3   
{

    ////////////////////////////////////
    // 
    // Object state variables
    //
    protected final int AUTHORIZATION = 1;
    protected final int TRANSACTION = 2;
    protected final int UPDATE = 3;
    protected       int _TotalMsgs = 0;
    protected       int _TotalSize = 0;
    protected boolean _StatusOK = false;
    protected int State = 0;
    protected String LastCmd;
    protected String Host = null;
    protected int Port = 110;
    protected String User = null;
    protected String Password = null;
    protected Socket server;
    protected BufferedReader serverInputStream;
    protected DataOutputStream serverOutputStream;
    private   boolean debugOn=false;


    ///////////////////////////////////////////** 
    // 
    // 
    // 
    // 
    // 
    public pop3(String host, String user, String password) {
	Host = host;
	User = user;
	Password = password;
    }

    /////////////////////////////////////////////
    //
    //
    //
    //
    //

    public pop3() 
    {
    
    }

    ///////////////////////////////////////
    //
    //  Pop default port is 110
    //
    //
    // 
    public popStatus connect(String host) {
	Host = host;
	return this.connect();
    }

    /////////////////////////////////////
    // 
    //
    //
    //
    //
    //
    public popStatus connect(String host, int port) 
     {
	
	Host = host;
	Port = port;     
	return this.connect();
    }

    ///////////////////////////////////////////
    //
    //
    //
    //
    // 
    public synchronized popStatus connect() {
	popStatus status = new popStatus();
	debug("Connecting to " + Host + " at port " + Port);

	if (Host == null) {
	    status._Response = "-ERR Host not specified";
	    status._OK = false;
	    return status;
	}

	try {
	    server = new Socket(Host,Port);
	    if (server == null) {  
		debug("-ERR Error while connecting to POP3 server");
		status._OK = false;
		status._Response = "-ERR Error while connecting to POP3 server";
	    } 
            else 
            {
		debug("Connected");

		serverInputStream = new BufferedReader(
		new InputStreamReader(server.getInputStream()));

		if (serverInputStream == null) {
		    debug("Failed to setup an input stream.");
		    status._OK = false;
		    status._Response = "-ERR Error setting up input stream";
		    server = null;
		}

		serverOutputStream = new DataOutputStream(
                    server.getOutputStream() );

		if (serverOutputStream == null) {
		    debug("Failed to setup an output stream.");
		    status._OK = false;
		    status._Response = "-ERR Error setting up output stream";
		    server = null;
		}
	    }
	}
	catch (Exception e) {
	    String msg = "Exception! " + e.toString();
	    debug(msg);
	    status._OK = false;
	    status._Response = msg;
	    server = null;
	}
	
	if (server != null) {
	    status._OK = true;
	    _StatusOK = true;    
	    status._Response = recv();
	    Parse(status,2);
	    debug("Response=" + status._Response);
	}

	if (status._OK)
	  State = AUTHORIZATION;
	
	return status;
    }

    ///////////////////////////////////////
    //
    //
    //
    //
    //
    // 
    public popStatus login(String user, String password) 
    {
	User = user;
	Password = password;
	return login();
    }
    //////////////////////////////////
    //
    //
    //
    //
    //
    //
    //
    //
    public synchronized popStatus login() 
    {
	popStatus status = new popStatus();
	
	if (User == null || Password == null) {
	    status._Response = "-ERR Userid or Password not specified";
	    return status;
	}

	if ( server != null )  {
	    send("USER " + User);
	    status._Response = recv();
	    Parse(status,1);
	    if (status._OK) {
		send("PASS " + Password);
		status._Response = recv();
		Parse(status,1);
		if (status._OK) {
		    State = TRANSACTION;
		    // Now we will do an internal STAT function
		    popStatus statStatus = stat();
		}   
	    }
	}
	return status;
    }

    /////////////////////////////////
    //
    //
    //
    //
    //
    public synchronized void close() 
    {
	debug("Closing socket");
	try {
	    server.close();
	    State = 0;
	} catch (IOException e) {
	    debug("Failure in server.close()");
	}
    }
    /////////////////////////////////////////
    //
    //
    //
    //
    public synchronized popStatus stat() {
	popStatus status = new popStatus();
	if (State != TRANSACTION) {
	    status._Response = "-ERR Server not in transaction mode";
	    return status;
	}
	send("STAT");        // Issue the STAT command
	status._Response = recv();     // read the response
	String[] tokens = Parse(status, 4);
     
	if (status._OK) {
	    _TotalMsgs = Convert.toInt(tokens[1]);
	    _TotalSize = Convert.toInt(tokens[2]);
	}
	
	return status;
    }

    /////////////////////////////////////////
    //
    //
    //
    //
    //
    //
    public synchronized popStatus quit() 
    {
	popStatus status = new popStatus();
	send("QUIT");        
	State = UPDATE;
	status._Response = recv();     
	String[] tokens = Parse(status,2);
	close();
	return status;
    }
    /////////////////////////////////////
    //
    //
    //
    //
    // 
    public synchronized popStatus list(int msgnum) 
    {
	popStatus status = new popStatus();
	int i=0;
	send("LIST " + msgnum);        // Issue the LIST n command
	status._Response = recv();     // read the response
	String[] tokens = Parse(status,2);
	return status;
    }
    ////////////////////////////////
    //
    //
    //
    //
    //
    //
    //
    //
    public synchronized popStatus list() {
	
	popStatus status = new popStatus();
	send("LIST");        // Issue the LIST command
	recvN(status);     // read the response
	String[] tokens = Parse(status,2);
	return status;
    }

    ////////////////////////////////////////////////////
    //
    //
    //
    //
    //
    //
    //
    public synchronized popStatus uidl(int msgnum) 
    {
	popStatus status = new popStatus();
	send("UIDL " + msgnum);     
	status._Response = recv();  
	String[] tokens = Parse(status,2);
	return status;
    }
    ////////////////////////////////////
    //
    //
    //
    //
    //
    //
    //
    //
    public synchronized popStatus uidl() 
    {
        popStatus status = new popStatus();
	send("UIDL");      
	recvN(status);     
	String[] tokens = Parse(status,2);
	return status;
    }
    //////////////////////////////
    //
    //
    //
    //
    //
    //
    //
    public synchronized popStatus retr(int msgnum) 
    {
	popStatus status = new popStatus();
	send("RETR " + msgnum);        
	recvN(status);     
	String[] tokens = Parse(status,2);
	return status;
    }


    //----------------------------------------------------------

    /**
     *  Gets the top n lines of a mail message.
     *  The array of strings obtained are the lines of the
     *  mail headers and the top N lines of the indicated mail msg.
     *  The lines have <code>CR/LF</code> striped, any leading 
     *  <code>"."</code> fixed up and the ending <code>"."</code>
     *  removed. <br>
     *  The array can be retrieved with status.Responses() method.
     *  The <code>+OK</code> or <code>-ERR</code> status line is 
     *  returned.
     *  @param  msgnum     the message number
     *  @param  n          how many body lines should be retrieved.
     *     If <code>n=0</code>, you'll get just the headers, 
     *     unfortunately I've bumped into a buggy POP3 server that
     *     didn't like zeroes, so I suggest to use <code>n=1</code>
     *     if you want just headers.
     *  @return popStatus: result of this operation
     */
    public synchronized popStatus top(int msgnum, int n) {
	
	popStatus status = new popStatus();
	send("TOP " + msgnum + " " + n); // Issue the TOP msgnum n command
	
	// This may produce more than one response so we call the
	// recvN method and set multiline output into _Responses
	recvN(status);     // read the response
	
	String[] tokens = Parse(status,2);
	
	return status;
    }
    
    
    //----------------------------------------------------------

    /**
     *  Marks the mail message for deletion
     *  This mail message will be deleted when QUIT is issued.
     *  If you lose the connection the message is not deleted.
     *  @param msgnum      a message number
     *  @return popStatus: result of this operation
     *  @see  #rset()
     */
    public synchronized popStatus dele(int msgnum) {
	popStatus status = new popStatus();
	send("DELE " + msgnum);        // Issue the DELE n command
    
	status._Response = recv();     // read the response
	String[] tokens = Parse(status,2);
	
	return status;
    }

    //----------------------------------------------------------

    /**
     *  Resets the mail messages that have been marked for deletion.
     *  Nothing will be deleted if QUIT is issued next.
     *  @return popStatus: result of this operation
     *  @see  #dele(int)
     */
    public synchronized popStatus rset() {
	popStatus status = new popStatus();
	send("RSET");        // Issue the RSET command
	
	status._Response = recv();     // read the response
	String[] tokens = Parse(status,2);
	
	return status;
    }

    //----------------------------------------------------------

    /**
     *  Does not do anything but it will keep the server active.
     *  @return popStatus: result of this operation
     */
    public synchronized popStatus noop() {
	
	popStatus status = new popStatus();
	send("NOOP");        // Issue the NOOP command
	
	status._Response = recv();     // read the response
	String[] tokens = Parse(status,2);
	
	return status;
    }

    //----------------------------------------------------------

    /**
     *  Returns the number of messages on the server.
     *  This value is set by an internal <code>STAT</code> 
     *  issued at login.
     *  @return the number of messages on this server.
     *  @see    #get_TotalSize()
     */
    public int get_TotalMsgs() {
	return _TotalMsgs;
    }
    
    //----------------------------------------------------------

    /**
     *  Returns the size of messages on the server.
     *  This value is set by an internal <code>STAT</code>
     *  issued at login.
     *  @return the total size of messages on this server.
     *  @see    #get_TotalMsgs()
     */
    public int get_TotalSize() {
	return _TotalSize;
    }
    
    //----------------------------------------------------------

    /**
     *  Returns the contents of a mail message and append it to the
     *  specified mail file.
     *  It will internally call <code>RETR</code> and then write 
     *  the results to the specified file.
     *  @param  filename   the name of the file to be extended
     *  @param  msgnum     a message number
     *  @return popStatus: result of this operation
     */
    public synchronized  popStatus appendFile(String filename, int msgnum) {
	popStatus status = new popStatus();
	
	String[] contents;
	
	send("RETR " + msgnum);  	// RETR n will return the contents
	// of message n
	
	recvN(status);     // read the response
	String[] tokens = Parse(status,2);
	if (status._OK) {
	    RandomAccessFile openfile;
	    try {
		openfile = new RandomAccessFile(filename,"rw");
	    } catch (IOException e) {
		status._OK = false;
		status._Response = "-ERR File open failed";
		return status;
	    }
	    Date datestamp = new Date();
	    contents = status.Responses();
	    try {
		openfile.seek(openfile.length());
		openfile.writeBytes("From - " + datestamp.toString() + "\r\n");
		for(int i=0; i<contents.length;i++) {
		    
		    openfile.writeBytes(contents[i]+"\r\n");
		    //openfile.writeByte((int)'\r');  // add CR LF
		    //openfile.writeByte((int)'\n');
		}
		openfile.close();
		
	    } catch (IOException e) {
		status._OK = false;
		status._Response = "-ERR File write failed";
		return status;
	    }
	}
	status._OK = true;
	return status;
    }

    //-------------------------------------------------------------------

    /**
     * Parses the response to a previously sent command from the server.
     * It will set boolean status._OK true if it returned <code>+OK</code>
     * and return an array of strings each representing a white space 
     * delimited token. The remainder of the response after 
     * <code>maxToParse</code> is returned as a single String.
     */
    String[] Parse(popStatus status, int maxToParse) {
	String[] tokens = null;
	
	status._OK = false;
	String response = status._Response;
	if (response != null) {
	    int i=0;
	    int max;
	    if (response.trim().startsWith("+OK"))
		status._OK = true;
	    else
		debug(response);
	    // This will break the line into a set of tokens.
	    StringTokenizer st = new StringTokenizer(response);
	    //tokens = new String[st.countTokens()];
	    if (maxToParse == -1)
		max = st.countTokens();
	    else
		max = maxToParse;
	    tokens = new String[max+1];
	    while (st.hasMoreTokens() && i < max) {
		tokens[i] = new String(st.nextToken());
		//debug("Token " + i + "= '" + tokens[i] + "'");
		i++;
	    }
	    // Now get any remaining tokens as a single string
	    if (st.hasMoreTokens()) {
		StringBuffer rest = new StringBuffer(st.nextToken());
		while (st.hasMoreTokens() )
		    rest.append(" " + st.nextToken());
		tokens[max] = new String(rest);
		//debug("Token " + max + "= '" + tokens[max] + "'");
	    }
	}
	return tokens;
    }
    
    //------------------------------------------------------------

    /**
     *  Sends the passed command to the server.
     */
    void send (String cmdline) {
	debug(">> " + cmdline);
	LastCmd = cmdline;    // Save command for error msg
	
	try {
	    // Write string as a set of bytes
	    serverOutputStream.writeBytes(cmdline + "\r\n");
	    _StatusOK = true;
	} catch (IOException i){
	    System.err.println("Caught exception while sending command to server");
	    _StatusOK = false;
	    
	} catch (Exception e) {
	    System.err.println("Send: Unexpected exception: " + e.toString());
	    _StatusOK = false;
	}
    }

    //-----------------------------------------------------------------------

    /**
     *  Gets the next response to a previously sent command from the server.
     */
    String recv() {
	//debug("entered recv");
	
	String line = "";
	if ( ! _StatusOK  ) {
	    line = "-ERR Failed sending command to server";
	    return line;
	}
	// send() has written a command to the
	// server so now we will try to read the result
	try {
	    line = serverInputStream.readLine();
	    debug("<<" + line);
	} catch (IOException i){
	    System.err.println("Caught exception while reading");
	    line = "-ERR Caught IOException while reading from server";
	} catch (Exception e) {
	    System.err.println("Unexpected exception: " + e.toString());
	    line = "-ERR Unexpected exception while reading from server";
	}
	if (line == null) 	{	// prevent crash if reading a null line
	    debug("Read a null line from server");
	    line = "-ERR <NULL>";
	}
	if (line.trim().startsWith("-ERR")) {
	    debug("Result from server has error!");
	    debug("Sent:     '" + LastCmd + "'");
	    debug("Received: '" + line + "'");
	    return line;
	} else {
	    if (line.trim().startsWith("+OK")) {
		return line;
	    } else {
		debug("Received strange response");
		debug("'" + line + "'");
		line = "-ERR Invalid response";
		return line;
	    }
	}
    }
        
    //-----------------------------------------------------------------------

    /**
     * Gets the responses to a previously sent command from the server.
     * This is used when more than one line is expected.
     * The last line of output should be <code>".\r\n"</code>
     */
    void recvN(popStatus status) {
	debug("entered recvN");
	Vector v = new Vector(100,100);
	String line = "";
	String[] response = null;
	
	// send() has written a command to the
	// server so now we will try to read the result
	try {
	    boolean done = false;
	    int linenum=0;
	    while (!done) {
		line = serverInputStream.readLine();
		linenum++;
		debug("<<" + line.length() + " '" + line +"'");
		if (linenum == 1) { // process the initial line
		    if (line.trim().startsWith("-ERR ")) {
			debug("Result from server has error!");
			debug("Sent:     '" + LastCmd + "'");
			debug("Received: '" + line + "'");
			done = true;
			status._Response = line;
		    } else {
			if (line.trim().startsWith("+OK")) {
			    //Everything looks OK
			    status._Response = line;
			} else {
			    debug("Received strange response");
			    debug("'" + line + "'");
			    done = true;
			    status._Response = "-ERR Invalid response";
			}
		    }
		} else {
		    // process line 2 - n
		    if (line.startsWith(".")) {
			if (line.length() == 1)
			    done = true;
			else
			    v.addElement(line.substring(1));
		    } else
			v.addElement(line);
		}
		
	    }   // end of while(!done)
	} catch (IOException i){
	    System.err.println("Caught exception while reading");
	    status._Response = "-ERR Caught IOException while reading from server";
	} catch (Exception e) {
	    System.err.println("Unexpected exception: " + e.toString());
	    status._Response = "-ERR Unexpected exception while reading from server";
	}
	
	status._Responses = new String[v.size()];
	v.copyInto(status._Responses);
	return;
    }

    //------------------------------------------------------

    /**
     *  Sets debug on or off.
     *  Debug messages are written to standard error.
     *  @param OnOff  true to set on debugging, false to
     *      shut it up.
     */
    public void setDebugOn(boolean OnOff) {
	debugOn = OnOff;
    }
    
    //------------------------------------------------------

    /**
     *  If debugOn switch is set, display debug info.
     *  @param   debugstr  a debug message
     */
    public void debug(String debugstr) {
	if (debugOn) {
	    System.err.println(debugstr);
	}
    }

    //-------------------------------------------------------

} // end of Class pop3
