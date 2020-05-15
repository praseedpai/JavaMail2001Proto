import java.util.*;
import java.io.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.search.*;
import javax.activation.*;

/*****************************************
*
*  Class To Implement IMailbox Interface
*  Handles IMAP and POP3 protocols with
*  the Help of Java Mail API. Provider
*  is from Sun MicroSystems 
*
*
*
*
***************************************************/
public class CMailbox implements IMailbox
{
  private Store   m_store=null; 
  private Folder  m_folder=null;
  private Message[] msgs = null;   
  /*********************************
   *  ctor for the CMailbox object  
   *  Automatically opens the Mail folder by the Name INBOX 
   *  One has to explicitly call the close method 
   *  to close the folder
   ******************************************/ 
  CMailbox(Store p_store) throws Exception 
  {
         m_store = p_store;
         m_folder   = m_store.getDefaultFolder();
         if (m_folder == null) {	   
               throw new CMailboxException("Can't Open the Folder");
         }
         m_folder   = m_folder.getFolder("INBOX");
	   if (m_folder == null) {	   
               throw new CMailboxException("Can't Access the Folder");
         }
         try { 
         m_folder.open(Folder.READ_WRITE);
         }
         catch( MessagingException e )
         {
              throw new CMailboxException("Can't Open the Folder in Read/Write Mode"); 
         } 
  } 
  /**************************************************
  *  Call this Method explicitly to Close the Folder
  *
  *********************************************/
  public void close() 
  {
      try { 
       if ( m_folder != null ) {
           m_folder.close(true); 
           m_folder = null; 
       } 
      }
      catch(Exception e )
      {



      }   
  }
  /**********************************************
  * Set The Store Called From Connection Manager
  * Called internally by CMailConnectionManager
  *********************************************/
  public void SetStore( Store m_str )
  {
       m_store = m_str;
  }
  /************************************************************
  *   Check Whether There is any New Mail is in the Server Box
  *   Can be used to Peek the Server  
  **********************************************************/
  public boolean   IsNewMailAvailable()
  {
    try {
          boolean bRetVal;
          bRetVal=   m_folder.getUnreadMessageCount() != 0;
          return bRetVal;  
     }
    catch(Exception e )
    {
            return false;    
    }
  }
 
/***********************************************
*
*
********************************************************/
public int getNewMailCount()
{

       try {  
          int sr = m_folder.getUnreadMessageCount();
          return (int)sr;
       }
       catch(Exception e )
       {
            return -1;    
       }
     
} 

/******************************************************
 *
 *
 ************************************************/
 protected void finalize()
 {
       close();
 }
 /**************************************************
  *
  *
  ********************************************/
 public boolean FlagAsDeleted( int id )
 {
     try {
        msgs[id].setFlag(Flags.Flag.DELETED,true);
        return true; 
     }
     catch(Exception e ) {
        return false;
     } 
   
 } 
/******************************************************
 *
 *
 **********************************************************/

 public int getMailCount()
 {
   try {
      int num_mails = m_folder.getMessageCount() ;
      return (int)num_mails;
   }
   catch(Exception e )
   {
            return -1;    
   }

 }
/******************************************************
*  Returns the Array of New Messages
*  See the Note of IMail
*
*
*
********************************************************/
public IMail[] getAllMails()
{
   try 
   {
      msgs = m_folder.getMessages();
      FetchProfile fp = new FetchProfile();
      fp.add(FetchProfile.Item.ENVELOPE);
      fp.add(FetchProfile.Item.CONTENT_INFO); 
      fp.add(FetchProfile.Item.FLAGS); 
      m_folder.fetch(msgs, fp); 

      if (msgs.length == 0 ) {
          return null;
       }
      CMail rst[] = new CMail[msgs.length];
      Address a[] = null ;
      String Fool=null;

      for (int i = 0; i < msgs.length; i++) 
      {
        rst[i] = new CMail(i,this); 
      
        
 

        if (( a =  msgs[i].getFrom()) != null )
        {
            Fool = (String)a[0].toString();  
            rst[i].SetSender( Fool);     
        } 
        String [] Art = null;
        if (( a   = msgs[i].getRecipients(Message.RecipientType.TO)) != null )
         {
                 Art = new String[a.length];
                 for( int j=0;j<a.length; ++j ) {
                          Art[j] = a[j].toString();
                         
                 }
                 rst[i].SetRecievers(Art);
         }  
        String [] Art1 = null; 
        if (( a   = msgs[i].getRecipients(Message.RecipientType.CC)) != null )
          {
                 Art1 = new String[a.length];
                 for( int j=0;j<Art1.length; ++j ){
                       Art1[j] = a[j].toString();
                }

                rst[i].SetCCRecievers(Art1);
         }
      
         rst[i].SetSubject(msgs[i].getSubject());  
            	
         Object content = msgs[i].getContent();
         if (content instanceof Multipart) {
           
               Multipart m_part = (Multipart)content;
               HandleMultipart((Multipart)content,rst[i]);
         } 
         else {
           
            HandlePart(msgs[i],rst[i],0);
                        
         }

         
        
    }   
      
      return rst ;    

    }
    catch(Exception e )
      {
           System.out.println("retrieval Failed");  
           return null;

     }


}

/*****************************************************
*  Returns the Array of New Messages
*  See the Note of IMail
*
*
*********************************************************/

public IMail[] getNewMails()
{
    try 
   {
      SearchTerm Term = null;
      Term = new FlagTerm(new Flags(Flags.Flag.RECENT),true);     
      msgs = m_folder.search(Term);
      if (msgs == null )
          return null;
      FetchProfile fp = new FetchProfile();
      fp.add(FetchProfile.Item.ENVELOPE);
      fp.add(FetchProfile.Item.CONTENT_INFO); 
      fp.add(FetchProfile.Item.FLAGS); 
      m_folder.fetch(msgs, fp); 
 
      if (msgs.length == 0 ) {
          return null;
       }
      CMail rst[] = new CMail[msgs.length];
      Address a[] = null ;
      String Fool=null;
      for (int i = 0; i < msgs.length; i++) 
      {
        rst[i] = new CMail(i,this); 
       

        if (( a =  msgs[i].getFrom()) != null )
        {
            Fool = (String)a[0].toString();  
            rst[i].SetSender( Fool);     
        } 
        String [] Art = null;
        if (( a   = msgs[i].getRecipients(Message.RecipientType.TO)) != null )
         {
                 Art = new String[a.length];
                 for( int j=0;j<a.length; ++j ) {
                          Art[j] = a[j].toString();
                         
                 }
                 rst[i].SetRecievers(Art);
         }  
        String [] Art1 = null; 
        if (( a   = msgs[i].getRecipients(Message.RecipientType.CC)) != null )
          {
                 Art1 = new String[a.length];
                 for( int j=0;j<Art1.length; ++j ){
                       Art1[j] = a[j].toString();
                }

                rst[i].SetCCRecievers(Art1);
         }
      
         rst[i].SetSubject(msgs[i].getSubject());  
            	
         Object content = msgs[i].getContent();
         if (content instanceof Multipart) {
           
               Multipart m_part = (Multipart)content;
               HandleMultipart((Multipart)content,rst[i]);
         } 
         else {
           
            HandlePart(msgs[i],rst[i],0);
                        
         }

       
        
    }   
      
       return rst ;    

    }
    catch(Exception e )
      {
           System.out.println("retrieval Failed");  
           return null;

     }
}

/***********************************************************
 Called internally to Handle Attatchments
***********************************************************************/
public static void HandleMultipart(Multipart multipart,CMail Temp) 
      throws MessagingException, IOException 
{
    for (int i=0, n=multipart.getCount(); i<n; i++) {
      HandlePart(multipart.getBodyPart(i),Temp,i);
    }
}
/******************************************************
*
*
*  Called internally to Handle Attatchments
*
*
***********************************************************/
public static void HandlePart(Part part, CMail Temp,int id) 
      throws MessagingException, IOException 
{
    String disposition = part.getDisposition();
    String contentType = part.getContentType();
    if (disposition == null) 
     { 
         
         if ((contentType.length() >= 10) && 
            (contentType.toLowerCase().substring(
             0, 10).equals("text/plain"))) 
         {
             
            String StrCont =(String) part.getContent();
            System.out.println(  StrCont);
            Temp.SetBody( StrCont);
         } 
       return;
            
    } 

     

     
      
    if (disposition.equals(Part.ATTACHMENT)) 
    {
      String Str;
      Str = CreateUniqueFileName("SCRATCH\\"+part.getFileName());
      Temp.AddAttatchMent( Str , part.getFileName(),id);      

      
      saveFile(Str, part.getInputStream(),Temp,id);

    } 
    else if (disposition.equals(Part.INLINE)) {
      String Str;
      Str = CreateUniqueFileName("SCRATCH\\"+part.getFileName());
      Temp.AddAttatchMent( Str , part.getFileName(),id);       
      saveFile(Str, part.getInputStream(),Temp,id);
    } 
}

/*************************************************
*
*
*  Called internally to Handle Attatchments
*
*
*
*********************************************************/
public static void saveFile(String filename,
      InputStream input,CMail Temp,int id) throws IOException {
    
    File file = new File(filename);
    FileOutputStream fos = new FileOutputStream(file);
    BufferedOutputStream bos = new BufferedOutputStream(fos);
    BufferedInputStream bis = new BufferedInputStream(input);
    int aByte;
    while ((aByte = bis.read()) != -1) {
      bos.write(aByte);
    }
    bos.flush();
    bos.close();
    bis.close();
  }

/*************************************************************
*
* Called internally to Handle Attatchments
*
*****************************************************************/
public static String  CreateUniqueFileName( String FileName ) throws IOException
{
    if (FileName == null) 
    {
        FileName = File.createTempFile("xx", ".out").getName();
    }
   
    File file = new File(FileName);
    String LastName = FileName;  
    for (int i=0; file.exists(); i++) {
      LastName = FileName+i; 
      file = new File(FileName+i);
    }
    return LastName;
}
 

}
