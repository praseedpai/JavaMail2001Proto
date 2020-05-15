import java.util.*;
import java.io.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.search.*;
import javax.activation.*;

/*************************************************
*
*
*
*
*
*
*
*****************************************************/
class SaveMailFilter extends MailFilter
{

 public void HandleMessage( IMail mail )   
 {

    try {          
 
     String [] rst = mail.GetRecievers();  
     for( int jp =0; jp < rst.length; ++jp )
          System.out.println("Recievers "+ rst[jp] );     
  
     String [] rst1 = mail.GetCCRecievers();
     if ( rst1 != null ) {  
        for( int kp =0; kp < rst1.length; ++kp )
            System.out.println("Recievers "+ rst1[kp] ); 
     }
     System.out.println(mail.GetBody());
     System.out.println(mail.getSubject());
     if (mail.GetAttatchMentCount() != 0 )
      {
         IAttatchMent[] df = mail.GetAttatchMents();
          for( int sj = 0; sj<df.length; ++sj )                    
          {
             String Lsp = df[sj].GetFileName();       
             String opt = CreateUniqueFileName(Lsp);
             System.out.println("--------------------\n");
             System.out.println(opt);
     
             df[sj].SaveAs(opt);
            

          }


     }
 
         
     mail.Delete(); 
}
  catch(Exception e)
  {




  }
 }


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

 public static void main(String[] ar )
 {

    SaveMailFilter st = new SaveMailFilter();
    st.start(); 
 } 

}






