/***********************************************
*
*  Abstraction for the Mail Box 
*  Simple one developed in the Context of Lucent Knowledge Management
*  
*
*
*
*
*************************************************************/
public interface  IMailbox 
{
     
/*************************************** 
  Whether There is any New Mail is in the Server Box
  Can be used to Peek the Server  
********************************************/
public boolean   IsNewMailAvailable();
/**************************************************
*      
* 
*
*******************************************************/
public int getNewMailCount(); 

/**************************************************
  Returns the Array of New Messages
  See the Note of IMessageRec
***************************************************/
public IMail[] getNewMails();
/******************************************
*
*
*
***********************************************/
public int getMailCount();
/*************************************************
*
*
*
***************************************************/
public IMail[] getAllMails();  
/************************************************************
*
*
*
****************************************************************/
public void close();   
      
}
