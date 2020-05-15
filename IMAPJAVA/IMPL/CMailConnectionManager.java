import java.util.*;
import java.io.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.search.*;
import javax.activation.*;
/*********************************************
* Implementation of IMailConnectionManager
*
*
*
*
***************************************************/
public class CMailConnectionManager implements IMailConnectionManager
{

  protected String m_ProtocolName="pop3";   // Can be set to IMAP later
  protected String m_HostName=null;         // Mail Server Name or IP 
  protected String m_UserName=null;         // UserName                                                                                                                                                                                                                                                         
  protected String m_PassWord=null;         // Password ??
  protected int    m_port;                  // TCP/IP port
  protected Store  m_store=null;            // JAVAMAIL Store abstraction

/*******************************************************************
* ctor for the Class CMailConnectionManager which implements
* IMailConnectionManager
*
*
*******************************************************************/
CMailConnectionManager()
{



}
/***************************************************************
*
*   Connect to The Mail Server
*   Params
*          HostName  := IP Address of the Mail Server
*          ( Port is always assumed to be 25 )
*          UserName  := User Name
*          PassWord   :=  PassWord
*    Return
*	    An implementation of IMailbox
********************************************************************/
public 	IMailbox   Connect( String HostName , String UserName , 			
		 String  PassWord)  throws Exception
 {
       m_HostName = HostName;             
       m_UserName = UserName;
       m_PassWord = PassWord; 
       m_port     = 25; 
       m_store  = null;
       m_PassWord = PassWord;          
       Properties props = System.getProperties();
       ////////////////////////////////////////////// 
       // Get a Session object	    
       //
       Session session = Session.getDefaultInstance(props, null);
       ////////////////////////////////////////////
       // Get a Store object
       //
       if (m_ProtocolName != null)		
	   m_store = session.getStore(m_ProtocolName);
       else	
           m_store = session.getStore();

       m_store.connect(m_HostName, m_UserName, m_PassWord);
       CMailbox MailBox =  new CMailbox(m_store);
       return MailBox;
 }
////////////////////////////////////////////////////////////////////      
//   Connect to The Mail Server. Here port can be specified.
//   Params
//          HostName   := IP Address of the Mail Server
//          Port       := Port no
//          UserName   := User Name
//          PassWord   :=  PassWord
// Return
//		an implementation of IMailbox

public 	IMailbox   connect( String HostName , int Port, String UserName ,String  PassWord)	throws Exception
{
       m_HostName = HostName;             
       m_UserName = UserName;
       m_PassWord = PassWord; 
       m_port     = Port;
       Properties props = System.getProperties();
       ////////////////////////////////////////////// 
       // Get a Session object	    
       //
       Session session = Session.getDefaultInstance(props, null);
       ////////////////////////////////////////////
       // Get a Store object
       //
       if (m_ProtocolName != null)		
	   m_store = session.getStore(m_ProtocolName);
       else	
           m_store = session.getStore();
	
       m_store.connect(m_HostName, m_port ,m_UserName, m_PassWord);
       CMailbox MailBox =  new CMailbox(m_store);
       return MailBox;
}

/********************************************************
*  Set The Protocol Name. 
*  At Present POP3 and IMAP4 are  supported
*  Param  Str := "POP3" | "IMAP4"
*  Used to Modify the Behavior of Connect
************************************************************/

public void   SetProtocol( String Str )
{
   
        m_ProtocolName = Str;                
 
}

/*********************************************
*
*  Get The Mail Client Protocol
*   return Value  :=    "POP3" | "IMAP4";
*
**********************************************/
public String      GetProtocol()
{
    return m_ProtocolName;

}
/*********************************************
*   Close the Connection
*
*
*
**********************************************/
public void Close()
{
 try 
 {
    if ( m_store != null )
     {   
            m_store.close();
            m_store = null;
     } 
 }
 catch(Exception e)
 {


 }


}

/**************************************************
*
*  Clean up Method Automatically called by
*  Garbage Collector
*
**************************************************/
protected void finalize()
{
     Close();    

}
 

}
