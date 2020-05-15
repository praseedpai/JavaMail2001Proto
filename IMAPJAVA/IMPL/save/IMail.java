/********************************************************
*
*  Interface for the Incoming Mail
*  Array of Objects which implements the interface 
*  will be retrieved by the Pop / IMAP Client
*
********************************************************/
public interface IMail
{
/*********************************************
* Retrieve the Name of the Sender
*******************************************/
public String      GetSender();
/*********************************************
*  Retrieve the Array of Recievers
*  
*
************************************************/
public String[]   GetRecievers();
/***************************************************    
* Get the CCed Recievers
*
*****************************************************/
public String[]   GetCCRecievers();
/*****************************************************
*  retrieve the UIDL of the Message
*  Unique ID 
******************************************************/
    public String      GetID();
/*********************************************    
*  Delete the current Message
*
**********************************************/
public boolean   Delete();
/***********************************************
* Retrieve the Body of the Message
*  
*
**************************************************/
public String      GetBody();
/**************************************************
*
*
*
*****************************************************/
public String getSubject(); 
/***************************************************   
* Array of AttatchMents
*
***************************************************/
public IAttatchMent[]  GetAttatchMents();
/**************************************************
*  No of Attatchments
*
*
****************************************************/
public long          GetAttatchMentCount();
}
