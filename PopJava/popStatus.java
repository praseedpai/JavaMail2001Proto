/////////////////////////////////////
//
//
//
//
//
//
//
//
//
import java.lang.*;
import java.io.*;
///////////////////////////////////
//
//
//
//
//
//
//
//
public class popStatus   
{


   	boolean	_OK=false;  
   	String  _Response;      
	String[] _Responses= new String[0]; 

        ////////////////////////////////
        //
        //
        //
        //
        public String[] Responses() 
        {
	  return _Responses;
        }
        //////////////////////
        //
        //
        //
        //
        // 
        public String Response() 
        {
        	return _Response;
        }

        ///////////////////////////////////
        //
        //
        //
        //
        //
        public boolean OK() {
	    return _OK;
        }
} 
