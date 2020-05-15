/////////////////////
//
//
//
//
//
//
import java.util.*;
import java.io.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.search.*;
import javax.activation.*;


public class TestClient
{
    static String protocol = "pop3";      

    TestClient() {


    }

    void DoAction()
    {

     try { 
     ///////////////////////////////////////////////// 
     // Get a Properties object
     //  
     System.out.println("About Start");  
     Properties props = System.getProperties();
     System.out.println("Hello World");   
     ////////////////////////////////////////////// 
     // Get a Session object	    
     //
     Session session = Session.getDefaultInstance(props, null);
     ////////////////////////////////////////////
     // Get a Store object
     //
     Store store = null;
     System.out.println("About to Set Protocol"); 
     if (protocol != null)		
	    store = session.getStore(protocol);
     else	
           store = session.getStore();
	
     System.out.println("About to Connect");   
     store.connect("192.168.101.1", "pai", "iap");
      	
      // Open the Folder
      Folder folder = store.getDefaultFolder();
           System.out.println("Go the Folder");    
     if (folder == null) {
	        System.out.println("Cant find default namespace");
	        System.exit(1);
	    }
	    folder = folder.getFolder("INBOX");
	    if (folder == null) {	                                                        System.out.println("Invalid folder");
	        System.exit(1);	    
            }
	    folder.open(Folder.READ_ONLY);
            //////////////////     
            //
            //
            System.out.println(folder.getMessageCount());
            System.out.println("folder is closed + OK ");
            Message [] msgs = folder.getMessages();
            System.out.println(msgs.length); 
            FetchProfile fp = new FetchProfile();
	    fp.add(FetchProfile.Item.ENVELOPE);
            fp.add(FetchProfile.Item.CONTENT_INFO);     
	    folder.fetch(msgs, fp); 
            for (int i = 0; i < msgs.length; i++) 
            {	
        	
                 System.out.println(msgs[i].getFrom()[0].toString());   


                Object content = msgs[i].getContent();
                
                 
                if (content instanceof Multipart) {
                    HandleMultipart((Multipart)content);
                 } 
                 else if (content intstanceof String  {

                 }
                 
                   HandlePart(msgs[i]);
                 }

	    }
            folder.close(false);
       }

       catch(Exception e)
       {

              e.printStackTrace(); 
              System.out.println("Some Pasnam");

        }

}

 public static void HandleMultipart(Multipart multipart) 
      throws MessagingException, IOException {
    for (int i=0, n=multipart.getCount(); i<n; i++) {
      HandlePart(multipart.getBodyPart(i));
    }
  }
  public static void HandlePart(Part part) 
      throws MessagingException, IOException {
    String disposition = part.getDisposition();
    String contentType = part.getContentType();
    if (disposition == null) { // When just body
      System.out.println("Null: "  + contentType);
      // Check if plain
      if ((contentType.length() >= 10) && 
          (contentType.toLowerCase().substring(
           0, 10).equals("text/plain"))) {
        part.writeTo(System.out);
      } else { // Don't think this will happen
        System.out.println("Other body: " + contentType);
        part.writeTo(System.out);
      }
    } else if (disposition.equals(Part.ATTACHMENT)) {
      System.out.println("Attachment: " + part.getFileName() + 
        " : " + contentType);
      saveFile(part.getFileName(), part.getInputStream());
    } else if (disposition.equals(Part.INLINE)) {
      System.out.println("Inline: " + 
        part.getFileName() + 
        " : " + contentType);
      saveFile(part.getFileName(), part.getInputStream());
    } else {  // Should never happen
      System.out.println("Other: " + disposition);
    }
  }
  public static void saveFile(String filename,
      InputStream input) throws IOException {
    if (filename == null) {
      filename = File.createTempFile("xx", ".out").getName();
    }
    // Do no overwrite existing file
    File file = new File(filename);
    for (int i=0; file.exists(); i++) {
      file = new File(filename+i);
    }
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


        public static void main(String s[] )

        {
             System.out.println("asdddddddddddd"); 
             TestClient xts = new TestClient();
             System.out.println("Created Th  BBBBBBBBasdddddddddddd"); 
             xts.DoAction();               
             System.out.println("CCCCCCCCCasdddddddddddd"); 


         }






}