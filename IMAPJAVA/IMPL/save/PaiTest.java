import java.util.*;
import java.io.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.search.*;
import javax.activation.*;


class PaiTest
{


 public static void main( String[] ar )
  {

     try {     
     IMailConnectionManager sar = new CMailConnectionManager(); 
     sar.SetProtocol("pop3"); 
     IMailbox tar = sar.connect( "192.168.101.1",110,"pai","iap");

     if ( tar == null )
     {
          System.out.println("Failed to Connect ");

     } 

     sar.Close();
     }

     catch(Exception e )
     {


     }  

     


  }



}