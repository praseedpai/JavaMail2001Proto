///////////////////////////////////////
//
// component to Encode the Attatchment
//
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
class MimeEncodedFile implements Encodable
{

  static final int BASE64_MAXLINE = 76;
  static final String EOL = "\r\n";
  static byte m_base64tab[]={'A','B','C','D','E','F','G','H',
              'I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z',
              'a','b','c','d','e','f','g','h','i','j','k','l', 
              'm','n','o','p','q','r','s','t','u','v','w','x','y','z','0','1','2','3','4','5'
              ,'6','7','8','9','+','/'};

  public String m_FileName;
  public byte[] m_obuffer;
  long   m_nEncodedSize  ;


  public MimeEncodedFile()
  {
        m_obuffer    = null;
        m_nEncodedSize  = 0;

  }

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
 public int  
 EncodeBase64(byte[]  pszIn, long nInLen, byte[] pszOut, long  nOutSize, long  nOutLen)
 {
    int nInPos  = 0;
    int nOutPos = 0;
    int nLineLen = 0;
    //////////////////////////////////////////////////////////////////////// 
    // Get three characters at a time from the input buffer and encode them
    //
    //
     for (int i=0; i<nInLen/3; ++i) 
     {
        //////////////////////////////////////////  
        //Get the next 2 characters
        //
        //  
        int c1 = pszIn[nInPos++] & 0xFF;
        int c2 = pszIn[nInPos++] & 0xFF;
        int c3 = pszIn[nInPos++] & 0xFF;
        /////////////////////////////////// 
        //Encode into the 4 6 bit characters
        pszOut[nOutPos++] = m_base64tab[(c1 & 0xFC) >> 2];
        pszOut[nOutPos++] = m_base64tab[((c1 & 0x03) << 4) | ((c2 & 0xF0) >> 4)];
        pszOut[nOutPos++] = m_base64tab[((c2 & 0x0F) << 2) | ((c3 & 0xC0) >> 6)];
        pszOut[nOutPos++] = m_base64tab[c3 & 0x3F];
        nLineLen += 4;
        //////////////////////////////////////////////////////// 
        //Handle the case where we have gone over the max line boundary
        if (nLineLen >= BASE64_MAXLINE-3) 
        {
            pszOut[nOutPos++] = 13;
            pszOut[nOutPos++] = 10;
            nLineLen = 0;
       }
     }

  int sInlen = (int) nInLen;

  switch (sInlen % 3) 
  {
    case 0:
    {
       pszOut[nOutPos++] = 13;
       pszOut[nOutPos++] = 10;
       break;
    }
    case 1:
    {
      int c1 = pszIn[nInPos] & 0xFF;
      pszOut[nOutPos++] = m_base64tab[(c1 & 0xFC) >> 2];
      pszOut[nOutPos++] = m_base64tab[((c1 & 0x03) << 4)];
      pszOut[nOutPos++] = '=';
      pszOut[nOutPos++] = '=';
      pszOut[nOutPos++] = 13;
      pszOut[nOutPos++] = 10;
      break;
    }
    case 2:
    {
      int c1 = pszIn[nInPos++] & 0xFF;
      int c2 = pszIn[nInPos] & 0xFF;
      pszOut[nOutPos++] = m_base64tab[(c1 & 0xFC) >> 2];
      pszOut[nOutPos++] = m_base64tab[((c1 & 0x03) << 4) | ((c2 & 0xF0) >> 4)];
      pszOut[nOutPos++] = m_base64tab[((c2 & 0x0F) << 2)];
      pszOut[nOutPos++] = '=';
      pszOut[nOutPos++] = 13;
      pszOut[nOutPos++] = 10;
      break;
    }
    default: 
    {
     
      break;
    }
  }
 // pszOut[nOutPos] = 0;
  return nOutPos;
//  return 1;

}

/////////////////////
//
//
//
//
//
//
public long  Base64BufferSize(long  nInputSize)
{
  long nOutSize = (nInputSize+2)/3*4;                    // 3:4 conversion ratio
  nOutSize += 2*nOutSize/BASE64_MAXLINE + 3;  // Space for newlines and NUL
  return nOutSize;
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

  //////////////////////////////// 
  //determine the file size
  //
  //

  //RandomAccessFile m_file = new RandomAccessFile(sFileName , "r" );
  //long              m_FileLength = m_file.length();  
  //byte              m_buffer[]     = new byte[(int)m_FileLength];
  //m_file.read(m_buffer,0,(int)m_FileLength);
  //System.out.println("Finished Reading File");
  //////////////////////////////////////////// 
  //allocate the encoded buffer
  //int nOutSize = Base64BufferSize(fs.m_size);
  //m_pszEncoded = new char[nOutSize];
  //long   OutSize = Base64BufferSize(m_FileLength);
  //
  // m_obuffer = new byte[(int)OutSize];
  

  //////////////////////////////////
  //Do the encoding
  //
  //
  //EncodeBase64(pszIn, fs.m_size, m_pszEncoded, nOutSize, &m_nEncodedSize);
  //  m_nEncodedSize = EncodeBase64(m_buffer, m_FileLength, m_obuffer, OutSize , 0);
  //  System.out.println("Encoded the File ");
  //  m_buffer = null;
  //  m_file.close(); 
  m_FileName = sFileName;
  return 1;

 }
 catch( Exception e)
 {
   return 0;

 }
}

public void WriteToStream( OutputStream e )
{
    String ls =     "\r\n\r\n--#BOUNDARY#\r\n"+
                    "Content-Type: application/octet-stream; name="+
                    this.GetFileName()+"\r\n"+
                    "Content-Transfer-Encoding: base64\r\n"+
                    "Content-Disposition: attachment; filename="+this.GetFileName()+
                    "\r\n";
    System.out.println("HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHh=============");
    System.out.flush();
    try {
    e.write(ls.getBytes());
    e.write("\r\n".getBytes());
    e.flush();

    
    RandomAccessFile m_file = new RandomAccessFile(m_FileName , "r" );
    long              m_FileLength = m_file.length();  
    byte              m_buffer[]     = new byte[(int)m_FileLength];
    m_file.read(m_buffer,0,(int)m_FileLength);
    System.out.println("Finished Reading File");
    //////////////////////////////////////////// 
    //allocate the encoded buffer
    //int nOutSize = Base64BufferSize(fs.m_size);
    //m_pszEncoded = new char[nOutSize];
    long   OutSize = Base64BufferSize(m_FileLength);
    m_obuffer = new byte[(int)OutSize];
    //////////////////////////////////
    //Do the encoding
    //
    //
    //EncodeBase64(pszIn, fs.m_size, m_pszEncoded, nOutSize, &m_nEncodedSize);
    m_nEncodedSize = EncodeBase64(m_buffer, m_FileLength, m_obuffer, OutSize , 0);
    System.out.println("Encoded the File ");
    m_buffer = null;
    m_file.close(); 
  //  m_FileName = sFileName;
    e.write(m_obuffer); 
  }
  catch(Exception e1 )
  {

       System.out.println("Can't Encode ");


   }

}

}

