import java.util.*;
import java.io.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.search.*;
import javax.activation.*;

/*******************************************
*  CMail implements IMAIL interface 
*  
*****************************************************/
public class CMail implements IMail
{
    public    String   m_Sender;
    protected String[] recs;
    protected String[] ccs;
    protected String   m_Subject;
    private   String   m_body; 
    private   long     a_count; 
    private   Vector   m_attatchvec= new Vector();
    public    CAttatchMent[] Attatch;
    private   CMailbox  Parent=null; 
    private   int        msg_uid;
    
    CMail(int id,CMailbox m_box)
    {
      m_Sender  = null;
      recs = null;
      ccs = null;
      m_Subject = null;
      m_body=null; 
      a_count=0; 
      Attatch = null;
      msg_uid = id;
      Parent  = m_box; 
    } 
    ////////////////////////////////
    //
    //
    //
    public void SetBody(String m_str )
    {
          m_body = m_str;
    }
    ////////////////////////////////////////////////
    //  Retrieve the Name of the Sender
    //
    //
    public void      SetSender(String p_Sender)
    {
       
       m_Sender = p_Sender;
       
  
    }  
    /////////////////////////////////////
    //
    //
    public void SetSubject(String s )
      {

             m_Subject = s;

       }  
   
    ////////////////////////////
    //
    //
    //

    public void AddAttatchMent(String uFileName,String aFileName,int id)
    {
     
         CAttatchMent m_att = new CAttatchMent();
         m_att.SetFileName(aFileName);
         m_att.SetPsuedoFileName(uFileName);        
         m_attatchvec.addElement(m_att);
    }   

    /////////////////////////////////////////////////////////
    //  Retrieve the Array of Recievers
    //  
    //
    public void SetRecievers(String[] rs)
    {
      recs = rs;  
    }
    /////////////////////////////////////////////////////////
    // Get the CCed Recievers
    //
    //
    public void   SetCCRecievers(String[] rs)
    {
        System.out.println("Setting CC's ");  
        ccs = rs;
      
    }

    
    ////////////////////////////////////////////////
    //  Retrieve the Name of the Sender
    //
    //
    public String      GetSender()
    {
        return m_Sender;
    }
 
    /////////////////////////////////////////////////////////
    //  Retrieve the Array of Recievers
    //  
    //
    public String[]   GetRecievers()
    {
        return recs;

    }
    /////////////////////////////////////////////////////////
    // Get the CCed Recievers
    //
    //
    public String[]   GetCCRecievers()
    {
           
          return ccs;

    }
    //////////////////////////////////////////////
    //
    //
    //
    public String getSubject()
    {
        return m_Subject;
    } 
    //////////////////////////////////////////////////////////////
    //  retrieve the UIDL of the Message
    //  Unique ID 
    public String      GetID()
    {
       return null;
    }
    ///////////////////////////////////////////////////////////////
    //  Delete the current Message
    //
    public boolean   Delete()
    {
          Parent.FlagAsDeleted(msg_uid);   
          return true;
    }
    //////////////////////////////////////////////////////
    // Retrieve the Body of the Message
    //  
    //
    public String      GetBody()
    {
          return m_body;
    }  
    /////////////////////////////////////////////////////////////
    // Array of AttatchMents
    //
    public IAttatchMent[]  GetAttatchMents()
    {
         int nCount;
 
         if ( Attatch == null )
         {
            if ((nCount =  m_attatchvec.size()) == 0 ) 
                   return null;

            Attatch = new CAttatchMent[ nCount ];

            for( int j = 0; j < nCount ; ++j )
               Attatch[j] = (CAttatchMent)m_attatchvec.get(j);             

         }
          
        return Attatch; 
    }
    //////////////////////////////////////////////////////////////////////
    //  No of Attatchments
    public long          GetAttatchMentCount()
    {
         return m_attatchvec.size();
    }


    //////////////////////////////////////
    //
    //
    //
    public void finalize()
    {
         int nCount;
         if ((nCount =  m_attatchvec.size()) == 0 ) 
                   return ;

        

         for( int j = 0; j < nCount ; ++j ) {
               CAttatchMent ret  = (CAttatchMent)m_attatchvec.get(j);  
               File ns = new File(ret.GetMangledName());
               ns.delete();

          }

          return ;
       
        
    }

     


}
