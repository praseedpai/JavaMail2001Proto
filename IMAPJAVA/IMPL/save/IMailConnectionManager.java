/*********************************************************
 * Abstraction for connection management
 *
 *
 *
 *
 *******************************************************/
public interface IMailConnectionManager
{
/**************************************************
*   Connect to The Mail Server
*   Params
*          HostName  := IP Address of the Mail Server
*          ( Port is always assumed to be 25 )
*          UserName  := User Name
*          PassWord   :=  PassWord
*    Returns
*	    An implementation of IMailbox
*************************************************************/
 public 	IMailbox   Connect( String HostName , String UserName ,   
  String  PassWord) throws Exception;
	
/*********************************************************
*   Connect to The Mail Server. Here port can be specified.
*   Params
*          HostName   := IP Address of the Mail Server
*          Port       := Port no ( IMAP port )
*          UserName   := User Name
*          PassWord   :=  PassWord
* Returns
*		an implementation of IMailbox
**************************************************************/
public 	IMailbox   connect( String HostName , int Port, String UserName ,    	                                                                                                                                                                                                                                                       						
String  PassWord)	throws Exception;
/***********************************************
*  Set The Protocol Name. 
*  At Present POP3 and IMAP4 are  supported
*  Param  Str := "POP3" | "IMAP4"
*  Used to Modify the Behavior of Connect
***********************************************************/
public void   SetProtocol( String Str );
/**********************************************************
*  Get The Mail Client Protocol
*   return Value  :=    "POP3" | "IMAP4";
*
**************************************************************/
public String      GetProtocol();
/*****************************************************
*
* Close the Connection to Mail Server
*
*
**********************************************************/
public void Close(); 
}
