////////////////////////////////////////////////
// Driver Program To test SMTPMAIL JAVA CLASS
// Written for Lucent Knowledge Management System
//
//

import java.io.*;
import java.lang.*;

///////////////////////////////////
//
//
//
//
//
public class PaiSend 
{

 public PaiSend()
  {

  }

 public static void main( String[] st )
 {

     MailAddress Sender   =  MailAddress.parseAddress("pai@nestinfotech.com");
     MailAddress Reciever =  MailAddress.parseAddress("greenu@nestinfotech.com");
     try {
       SmtpMail rt = new   SmtpMail();
       MimeEncodedFile xt = new MimeEncodedFile();
       System.out.println(" Created The File ");
       xt.Attach("PaiSend.java");
       rt.AddAttatchment( xt ); 
       
      // MimeEncodedFile xt1 = new MimeEncodedFile();
      // System.out.println(" Created The File ");
      // xt1.Attach("Code.zip");
       //rt.AddAttatchment( xt1 );  

      
       UUEncodedFile xtr =new UUEncodedFile();
       xtr.Attach("code.zip"); 
       rt.AddAttatchment( xtr ); 
       

       
//       MimeEncodedFile xtn = new MimeEncodedFile();
  //     System.out.println(" Created The File ");
    //   xtn.Attach("SmtpMail.java");
      // rt.AddAttatchment( xtn );   
       
     
       System.out.println(" Added the Attatchment ");
       rt.SendMail("192.168.101.1",25,Sender,Reciever,
       "Hello","Hello World ");
       System.out.println(" Subham  ");
     }
     catch( Exception e )
     {
         System.out.println("Hello World");
     }
 }

}

// EOF File 
