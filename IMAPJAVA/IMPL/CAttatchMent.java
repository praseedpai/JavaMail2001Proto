import java.util.*;
import java.io.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.search.*;
import javax.activation.*;

/***********************************************
* CAttatchMent implements IAttatchMent interface
*    which Abstracts a Mail Attatchment 
*
*
********************************************/
public class CAttatchMent implements IAttatchMent
{

     private String m_FileName=null; 
     private String m_psuedoFileName=null;

     /*******************************************************
     * Names are Mangled to maintain there uniqueness
     * 
     *****************************************************/
     public String GetMangledName()
     {
         return      m_psuedoFileName; 
     }     
     /****************************************************
     *  Get The Attatchment FileName
     *
     *
     *
     *******************************************************/
     public String GetFileName()
      {
         return m_FileName;
      }
     /****************************************
     * Save the AttatchMent as a new File 
     * 
     *
     *****************************************************/
     public void     SaveAs( String NewFileName )
      {
        try {
          RandomAccessFile i_file = new RandomAccessFile(m_psuedoFileName,"r");
          RandomAccessFile o_file = new RandomAccessFile(NewFileName,"rw");
          byte[] ReadBuffer = new byte[16384];
          int n_read;
          long NumWrote = 0; 
          while ( ( n_read = i_file.read(ReadBuffer)) == 16384 )
          {
                  NumWrote += 16384;      
                   o_file.write(ReadBuffer);
          } 
          o_file.write(ReadBuffer,0, n_read);       
          i_file.close();
          o_file.close();  
          }
          catch(Exception e )
          {


          }
          return;
      }
     /************************************************************** 
     *  Get The Contents of The File as a Byte Array
     *
     *
     *
     ************************************************************/   
     public  byte[]  GetBuffer()
     {
         byte[] ReadBuffer = null;
         try { 
          RandomAccessFile i_file = new RandomAccessFile(m_psuedoFileName,"r");
          ReadBuffer = new byte[(int)i_file.length()]; 
          i_file.readFully(ReadBuffer);
          i_file.close();  
         }
         catch(Exception e )
          {


          } 
          return ReadBuffer;
     }
     /******************************************
     *  get the Size of the Attatchment
     *
     *
     ***********************************************/
     public long     GetSize()
     {
         long Size = -1;
         try { 
          RandomAccessFile i_file = new RandomAccessFile(m_psuedoFileName,"r");
          Size =  i_file.length(); 
          i_file.close();  
         }
         catch(Exception e )
          {


          } 
          return Size;

     }      
     /********************************************
     *  Get the Contents of the File as a Byte Array
     *  from specified offset and Size 
     *  To Implement Memory Efficient Read of the File
     *   if FileSize > 10 MB , we can use this function to
     *   read one MB at a Time 
     *****************************************************/
     public byte[]  GetBuffer(long OffSet, long  Size )
      {
          byte[] ReadBuffer = new byte[(int)Size]; 
          try {
          RandomAccessFile i_file = new RandomAccessFile(m_psuedoFileName,"r");
          i_file.readFully(ReadBuffer,(int)OffSet,(int)Size);
          i_file.close();  
          }
          catch( Exception e )
          {
                return null;

          } 
          
          return ReadBuffer;
      } 

     /********************************************
     *
     *
     *
     *
     *
     *
     ***********************************************/
     public void SetFileName(String p_FileName)
     {
        m_FileName = p_FileName;
     } 
     /********************************************
     *
     *
     *
     ************************************************/   
     public void SetPsuedoFileName(String p_FileName)
     {
          m_psuedoFileName = p_FileName;

     } 



}
