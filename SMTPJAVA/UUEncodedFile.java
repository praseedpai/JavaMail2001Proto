///////////////////////////////////////////////////////////
//
// component to Encode the Attatchment using UUENCODE/UUDECODE
// Written for LUCENT KNOWLEDGE MANAGEMENT SYSTEM
//
//
//
//
import java.io.*;
import java.lang.*;
import java.net.*;
///////////////////////////////////
//
//  
//
//
//
//
class UUEncodedFile implements Encodable
{
  protected String       m_Name;
  protected int          m_mode;      
  private   boolean      m_wroteprefix;
  public    String       m_FileName;
  public    byte[]       m_obuffer;
  private   byte[]       m_ebuffer;
  long                   m_nEncodedSize;
  int                    buffsize;
  public    OutputStream ostr;

  public byte[] GetEncodedbuffer()
  {
     return  m_obuffer;
  }

  public String GetFileName()
  {
     return m_FileName;      
  }

  public long GetFileSize()
  {
     return m_nEncodedSize ;   
  }

  public UUEncodedFile()
  {
        m_obuffer       = null;
        m_nEncodedSize  = 0;
        m_ebuffer       = new  byte[45];
        buffsize        = 0;
  }
  //////////////////////////////////
  //
  //
  //
  //
  //
  //
  public void WriteToBuffer( int i )
  {
    m_ebuffer[buffsize++] = (byte)i;
    if ( buffsize == 45 )
    {
         WritePrefix();
         EncodeLine();
         buffsize = 0; 
    }    

  }
  /////////////////////////////////
  //
  //
  //
  //
  //
  public void Flush()
  {
    if (buffsize > 0 )
    {
      WritePrefix();
      EncodeLine(); 
    }

    WriteSuffix();  
   try {
    ostr.flush();
   }
   catch( Exception e )
   {
     

   }
  }
  /////////////////////////////////
  //
  //
  //
  //
  //
  //
  //
  //
  public void Write( byte b[] , int i , int j )
  {
       for(int k=0;k<j;k++ ) {
            WriteToBuffer( b[i+k]);
       }
  }
  ///////////////////////////////////
  //
  //
  //
  //
  //
  public int  
  Encode(byte[]  pszIn, long nInLen)
  {
    Write( pszIn , (int)0 , (int)nInLen );
    return 1;
  }
  ///////////////////////////
  //
  //
  //
  //
  public void WritePrefix()
  {
   if ( !m_wroteprefix ) 
   {
       PrintStream psr = new PrintStream(ostr); 
       psr.println("begin "+ m_mode + " "+m_Name); 
       psr.flush();
       m_wroteprefix = true;
   }
  }
  ///////////////////////////////
  //
  //
  //
  //
  //
  public void WriteSuffix()
  {
       PrintStream psr = new PrintStream(ostr); 
       psr.println(" \nend"); 
       psr.flush();
  }
  ////////////////////////////
  //
  //
  //
  //
  //
  //
  private void EncodeLine()
  {
  int i1=0;

  try { 
  ostr.write((buffsize&0x3f)+32); // Will be Always m
  
  while ( i1<buffsize )
  {
     byte byte0 = m_ebuffer[i1++];
     byte byte1,byte2;
     
     if ( i1 < buffsize )
     {
       byte1 = m_ebuffer[i1++];
       if (i1 < buffsize )
       {
           byte2 = m_ebuffer[i1++];
       }
       else {
         byte2 =1;
       } 


     }    
     else {
         byte1=1;
         byte2=1;        
     }

     int i = byte0 >>> 2 & 0x3F;
     int j = byte0 << 4 & 0x30 | byte1 >>>4 & 0xf;
     int k = byte1 << 2 &0x3C | byte2 >>>6 &0x3;
     int l = byte2 & 0x3f;

     ostr.write(i+32);
     ostr.write(j+32);
     ostr.write(k+32);
     ostr.write(l+32);
   

  } 
  ostr.write(10);
 }
 catch(Exception e1)
  {



   }
 return;
}
////////////////////////////
//
//
//  SetNameMode
//
//
//
//
public void SetNameMode(String s , int i )
{

  m_Name = s;
  m_mode = i; 

}
////////////////////////////////
//
//
//
//
public int Attach(String sFileName)
{
 
 try {

  ///////////////////////
  // If Buffer Previously Allocated free it 
  // 
  if ( m_obuffer != null )
       m_obuffer = null;
  m_FileName = sFileName;
  return 1;

 }
 catch( Exception e)
 {
   return 0;

 }
}

///////////////////////
//
//
//
//


public void WriteToStream( OutputStream e )
{

    String ls =     "\r\n\r\n--#BOUNDARY#\r\n"+
                    "Content-Type: application/octet-stream; name="+
                    this.GetFileName()+"\r\n"+
                    "Content-Transfer-Encoding: uuencode\r\n"+
                    "Content-Disposition: attachment; filename="+this.GetFileName()+
                    "\r\n";
    System.out.println("HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHh=============");
    System.out.flush();
    try {
    ostr = e;
    e.write(ls.getBytes());
    e.write("\r\n".getBytes());
    e.flush();
    RandomAccessFile m_file         = new RandomAccessFile(m_FileName , "r" );
    long              m_FileLength  = m_file.length();  
    byte              m_buffer[]    = new byte[(int)m_FileLength];
    m_file.read(m_buffer,0,(int)m_FileLength);
    Encode(m_buffer, m_FileLength);
    Flush();
    System.out.println("Encoded the File ");
    m_buffer = null;
    m_file.close(); 

    }
   catch(Exception e1 )
    {


      

    }
}

}

