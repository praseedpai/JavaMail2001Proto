import java.util.*;
import java.io.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.search.*;
import javax.activation.*;


class PaiTest00
{


 public static void main( String[] ar )
  {

     try {     
     IMailConnectionManager sar = new CMailConnectionManager(); 
     sar.SetProtocol("pop3"); 
     IMailbox tar = sar.connect( "192.168.101.1",110,"testpop","testpop");

     if ( tar == null )
     {
          System.out.println("Failed to Connect ");

     } 

     if (tar.IsNewMailAvailable() )
     {  
     System.out.println("New Mail in the Box " +      tar.getNewMailCount());
     }

     

     IMail [] ert = tar.getAllMails();

     if (ert == null )
      {

          System.out.println("Empty Mail Box");
          return;

      }  

     if ( ert.length == 0 )
      {

            System.out.println("No Mails fetched");
      }    

      

     for( int u=0; u< ert.length ; ++u )
      {   
 
           System.out.println("-----------------------------------");
           System.out.println("Sender " + ert[u].GetSender());
           String [] rst = ert[u].GetRecievers();  
           for( int jp =0; jp < rst.length; ++jp )
              System.out.println("Recievers "+ rst[jp] ); 
           String [] rst1 = ert[u].GetCCRecievers();
           if ( rst1 != null ) {  
           for( int kp =0; kp < rst1.length; ++kp )
              System.out.println("Recievers "+ rst1[kp] ); 
           }
           System.out.println("BODY \n" + ert[u].GetBody());
           System.out.println("Subject " + ert[u].getSubject());
       

           if (ert[u].GetAttatchMentCount() != 0 )
           {

              IAttatchMent[] df = ert[u].GetAttatchMents();
               
              for( int sj = 0; sj<df.length; ++sj )                    
              {
                 System.out.println("FileName for Message " + u + " "+df[sj].GetFileName());       
                  

              }


           }
      
      }
     


     sar.Close();
     
     }

     catch(Exception e )
     {
            System.out.println("Exeception While Retrieving Mail");
     }  

     
     System.gc();
     System.runFinalization();
   }



}