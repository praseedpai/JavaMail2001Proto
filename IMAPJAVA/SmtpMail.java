import java.io.*;
import java.net.*;
///////////////////////////////
//
//
//
//
//
//
//
import java.net.*;
import java.util.*;
///////////////////////////////////////
//
//
//
//
//
//
//
//
public class SmtpMail
{

  ///////////////////////////////
  // Ctor
  //
  public SmtpMail()
  {
    super();
    rcpts = new Vector();
    attatchments = new Vector();
  }
  ////////////////////////////////////////////
  //
  //  Add an Attatchment
  //
  //
  //
  public void AddAttatchment( Encodable f)
  {
      attatchments.addElement(f);
  }
  ///////////////////////////////////////////
  //
  //  Add a Reciever  
  //
  //
  public void AddReciever(MailAddress addr)
  {
      rcpts.addElement(addr);  
  }
  ////////////////////////////////
  //
  //
  //
  //
  public void debugMode(boolean debug)
  {
    this.debug = debug;
  }
  //////////////////////////////////////
  //
  //
  //
  protected void trace(String t)
  {
    if (debug) {
      System.err.println("D:" + t);
    }
  }
  /////////////////////////////////////////////////////
  //
  //
  //
  //
  //
  public void SendMail(String serverName, int port, MailAddress sender,
			 MailAddress receiver, String subject, String msg)
			 throws SmtpException, UnknownHostException, IOException
  {
    serverAddress = InetAddress.getByName(serverName);
    serverPort = port;
    this.sender = sender;
    rcpts.addElement(receiver);
    this.subject = subject;
    message = msg;
    System.out.println("About to Send");
    Send();
  }
  /////////////////////////////////////////////////////
  //
  //
  //
  //
  protected void Send()
    throws SmtpException, IOException
  {
    Socket sock = null;

    try {
      trace("trying to connect to server");
      sock = new Socket(serverAddress, serverPort);
      BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
      PrintWriter out = new PrintWriter(sock.getOutputStream());
      trace("connected");
      int rc = getResponse(in);
      if (rc/100 != 2) {
	throw new SmtpException("CONNECT", rc, lastResponseText);
      }
      SendCommand(out, "HELO " + InetAddress.getLocalHost().getHostName(),
		  in, 2);
      //////////////////////
      //
      //
      SendCommand(out, "MAIL from: " + sender.getAddress(),
		  in, 2);
        
      System.out.println("About to Send "+sender.getAddress() );

      for ( int iterat = 0; iterat< rcpts.size(); iterat++ )
      {
         String rt = ((MailAddress)(rcpts.get(iterat))).getAddress();
         SendCommand(out, "RCPT to:<" + rt+">", in, 2);
      }
      SendCommand(out, "DATA", in, 3);
      SendDataHeader(out);
      String Xt = "\r\n--#BOUNDARY#\r\nContent-Type: text/plain; charset=us-ascii\r\n" +							                                                               
      "Content-Transfer-Encoding: quoted-printable\r\n";
      if ( attatchments.size() !=0 )
      {
         System.out.println(Xt);
         SendCommand(out, Xt, in);  
      }
      SendData(out);

      System.out.println("Finished Sending Body"); 
      OutputStream rst = sock.getOutputStream();
      for ( int nIter=0;nIter<attatchments.size();nIter++)
      {
        System.out.println("In the Mime Loop");
        Encodable CurrFile = (Encodable)attatchments.get(nIter);
        System.out.println("going to Write Byte Array");
        CurrFile.WriteToStream(rst);             
        System.out.println("Wrote the Byte Array"); 
        rst.flush();        
      }
      if ( attatchments.size() != 0 ) {
             SendCommandsansCR(out, "\r\n--#BOUNDARY#--");  
        }

        System.out.println("About to Send Finish");

        SendCommand(out, "\r\n.",
		  in, 2);  
        System.out.println("About to Send Quit");
        SendCommand(out, "QUIT", in, 2);
    }
    catch (IOException e1) {
      if (sock != null) sock.close();
      /*re*/throw e1;
    }
    catch (SmtpException e2) {
      if (sock != null) sock.close();
      /*re*/throw e2;
    }
  }
    
  //////////////////////////////////////////////////
  //
  //
  //
  //
  //
  protected void SendCommand(PrintWriter out, String cmd)
  {
    trace("sending command: " + cmd);
    out.write(cmd);
    out.write("\r\n");
    out.flush();
  }
  /////////////////////////////////////////////
  //
  //
  //
  protected void SendCommandsansCR(PrintWriter out, String cmd)
  {
    trace("sending command: " + cmd);
    out.write(cmd);
    out.flush();
  }
  /////////////////////////////////////////
  //
  //
  //
  //
  //
  protected void SendCommand(PrintWriter out, String cmd, BufferedReader in,
			     int OkClass)
    throws SmtpException, IOException
  {
    SendCommand(out, cmd);
    int rc = getResponse(in);
    if (rc/100 != OkClass) {
      throw new SmtpException(cmd, rc, lastResponseText);
    }
  }
  /////////////////////////////////////////////////////////////////
  //
  //
  //
  //
  protected void SendCommand(PrintWriter out, String cmd, BufferedReader in)
    throws SmtpException, IOException
  {
    SendCommand(out, cmd);
  }
  ///////////////////////////////////////////////////////
  //
  //
  //
  //
  protected void SendData(PrintWriter out)
  {
    String data = msg2data(message);
    trace(data);
    out.write(data);
    out.flush();
  }
  ///////////////////////////////////////////////
  //
  //
  //
  //
  //
  protected void SendDataHeader(PrintWriter out)
  {
    //////////////////////////////////////// 
    // send header
    //
    out.write("From: " + sender + "\r\n");
    out.write("To: " + rcpts.firstElement() + "\r\n");
    out.write("Subject: " + subject + "\r\n");
    if ( attatchments.size() != 0 )
    {
      String Lps =        "MIME-Version: 1.0\r\nContent-type:multipart/mixed; boundary=\"#BOUNDARY#\"\r\n";
      out.write(Lps); 
    }
    out.write("\r\n"); // end header
    out.flush();
  } 

  ////////////////////////////////////////////////////////////   
  //
  //
  //
  //
  //
  //
  //
  //
  //
  //
  //
  // 
  protected String msg2data(String msg)
  {
    StringBuffer buff = new StringBuffer();
    String line;
    int start=0;
    int end=0;

    if (msg != null) {
      buff.ensureCapacity(msg.length()+100);
      do {
	end = msg.indexOf('\n', start);

	if (end == -1) {
	  line = msg.substring(start);
	}
	else {
	  line = msg.substring(start, end);
	  end++; // skip newline character
	}

	if (line.length() > 0 && line.charAt(0) == '.') {
	  buff.append('.');
	}

	buff.append(line);

	if (end != -1) {
	  buff.append("\r\n");
	}

	start = end;
      } while (end != -1);
    }

    return buff.toString();
  }
  ////////////////////////////////////////
  //
  // Gets a response from the SMTP server.
  // 
  // <p>Each line begins with a returncode, followed by either a space or a
  // dash (<code>'-'</code>).  A dash indicates that there are following lines,
  // a space that this is the last line.  The rest of a line is addition text.
  // Lines are separated by <code>&lt;CR/LF&gt;</code>.</p>
  //
  // @param in Inputstream to SMTP server
  // @return Returncode from SMTP server
  // @exception IOException Error while reading from server
  //
  protected int getResponse(BufferedReader in)
    throws IOException
  {
    int responseCode;
    boolean moreLines;
    String line;
    StringBuffer text = new StringBuffer();
    do {
      line = in.readLine();
      trace("response: " + line);
      moreLines = (line.charAt(3) == '-');
      text.append(line.substring(4, line.length()));
      
    } while (moreLines);
    responseCode = Integer.parseInt(line.substring(0, 3));
    lastResponseText = text.toString();
    return responseCode;
  }

  
  private InetAddress serverAddress = null;
  private int serverPort = -1;
  private MailAddress sender = null;
  private Vector rcpts = null;
  private Vector ccs   = null;
  private Vector bcc   = null;
  private