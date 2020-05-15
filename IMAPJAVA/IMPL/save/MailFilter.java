import java.util.*;
import java.io.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.search.*;
import javax.activation.*;


public abstract class MailFilter  extends Thread
{

 
 public void HandleMessage( IMail mail )   // Override this in the Sub Class
 {
 
      


 }
 public void run() 
 {   
    
      IMailbox tar=null ;
      IMailConnectionManager sar = null;
      IMail [] ert=null;   
      while (true) { 

        try {     
    
       sar  = new CMailConnectionManager(); 
       sar.SetProtocol("pop3"); 
       try {  
        tar = sar.connect( "192.168.101.1",110,"testpop","testpop");
       }
       catch(Exception e )
       {
           System.out.println("Failed To Connect to Server");  
           System.out.println(e);
       }

       ert  = tar.getAllMails();

       if (ert == null )
       {
             System.out.println("No Mails in the Box");  
             tar.close();  
             sar.Close(); 
             Thread.sleep(6000);  // sleep for 1 minutes            
             sar = null;
             tar = null;
             System.gc();
             System.runFinalization(); 
             continue;
               
       }  
       if ( ert.length == 0 )
       {
            tar.close();  
            sar.Close(); 
            sar = null;
            tar = null;
            ert = null;
            System.gc();
            System.runFinalization(); 
            continue;
            
       }    
       System.out.println(" Mails in the Box");  
       for( int u=0; u< ert.length ; ++u )
       {   
              HandleMessage(ert[u]);
       }
       tar.close(); 
       sar.Close(); 
       ert = null;
       sar = null;
       tar = null; 
       System.gc();
       System.runFinalization(); 
       

     
     }
    catch (Exception e )
    {

        
    }  

  }

}





}


