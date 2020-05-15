////////////////////////////////////////////////////
//  Abstract The Attatchment 
//
public interface IAttatchMent
{
       //////////////////////////////////////////////////
       //  Get The Attatchment FileName
       //
       public String GetFileName();
       /////////////////////////////////////////////////////////////
       // Save the Current FileName as Different File
       // to avoid Name Collision 
       public void     SaveAs( String NewFileName );
       //////////////////////////////////////////////////////////////////////////////
       //  Get The Contents of The File as a Byte Array
       //
       public byte[]  GetBuffer();
       ///////////////////////////////////////////////////////////
       //  get the Size of the Attatchment
       //
       public long     GetSize();      
       /////////////////////////////////////////////////////////
       //  Get the Contents of the File as a Byte Array
       //  from specified offset and Size 
       //  To Implement Memory Efficient Read of the File
       //   if FileSize > 10 MB , we can use this function to
       //   read one MB at a Time 
       public byte[]  GetBuffer(long OffSet, long  Size );
}
